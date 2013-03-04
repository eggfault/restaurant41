package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.layoutGUI.*;
import java.awt.Color;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent {
	// Constants
    final private int MIN_ITEM_QUANTITY = 3;
    final private int MAX_ITEM_QUANTITY = 6;
    final private int LOW_STOCK = 4;				// When the stock is <= LOW_STOCK, an order for more will be placed
    final private int STOCK_ORDER_QUANTITY = 3;		// How many more of an item to place in an order when it runs low
    final private int CHECK_REVOLVING_STAND_DELAY = 5000;	// How long to wait in between revolving stand checks
	
    // List of all the orders
    private List<FoodOrder> orders;
    private List<Delivery> deliveries;
    private Inventory inventory;
    public enum OrderStatus {pending, cooking, done}; 	// Order status
    private String name;								// Name of the cook
    private Menu menu;    
    CashierAgent cashier;
    Timer timer = new Timer();							// Timer for simulation
    Restaurant restaurant;								// UI layout
	private RevolvingStandMonitor revolvingStand;
	private boolean checkRevolvingStand;

    /** Constructor for CookAgent class
     * @param name name of the cook
     * @param cashier 
     */
    public CookAgent(String name, Restaurant restaurant, CashierAgent cashier) {
		super();
		
		this.name = name;
		this.restaurant = restaurant;
		this.cashier = cashier;
		
		orders  = Collections.synchronizedList(new ArrayList<FoodOrder>());
		deliveries = Collections.synchronizedList(new ArrayList<Delivery>());
		// Create the restaurant's inventory.
		menu = new Menu();
		inventory = new Inventory(menu, MIN_ITEM_QUANTITY, MAX_ITEM_QUANTITY);
		// Initial check of stock for low items
		checkInventoryForLowStock();
		// Check the revolving stand periodically
		checkRevolvingStand = true;
    }
    
    /** Represents a delivery from the market for food ordered by the cashier */
    private class Delivery {
    	public String productName;
    	public String marketName;
    	public int quantity;
    	
    	public Delivery(String marketName, String productName, int deliverQuantity) {
    		this.marketName = marketName;
			this.productName = productName;
			this.quantity = deliverQuantity;
		}
    }

    // *** MESSAGES ***

    /** Message from a waiter giving the cook a new order.
     * @param waiter waiter that the order belongs to
     * @param tableNum identification number for the table
     * @param choice type of food to be cooked
     */
    public void msgHereIsAnOrder(WaiterAgent waiter, int tableNum, MenuItem choice) {
		orders.add(new FoodOrder(waiter, tableNum, choice));
		stateChanged();
    }
    
    /** Message from the market to deliver the food ordered by the cashier */
	public void msgDeliverOrder(String marketName, String productName, int deliverQuantity) {
		deliveries.add(new Delivery(marketName, productName, deliverQuantity));
		stateChanged();
	}

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
    	
		// If there exists an order o whose status is done, place o.
		for(FoodOrder o:orders) {
		    if(o.status == OrderStatus.done) {
		    	synchronized(orders) {
		    		placeOrder(o);
		    	}
				return true;
		    }
		}
		
		// If there exists an order o whose status is pending, cook o.
		for(FoodOrder o:orders) {
		    if(o.status == OrderStatus.pending) {
		    	synchronized(orders) {
		    		cookOrder(o);
		    	}
				return true;
		    }
		}
		
		// Process deliveries
		for(Delivery d:deliveries) {
			// There is only one status; all deliveries are removed from the list after being processed
			synchronized(deliveries) {
				processDelivery(d);
			}
			return true;
		}
		
		// Check the revolving stand if the checking delay has expired
		if(checkRevolvingStand) {
			checkRevolvingStand();
		}
		
		// We have tried all our rules (in this case only one) and found
		// nothing to do. So return false to main loop of abstract agent
		// and wait.
		return false;
    }
    

    // *** ACTIONS ***
    
    private void processDelivery(Delivery delivery) {
    	print("Received " + delivery.quantity + " of " + delivery.productName + " from " + delivery.marketName + "!");
    	inventory.addToQuantity(delivery.productName, delivery.quantity);
    	inventory.setOrdered(delivery.productName, false);		// open this item up for re-ordering if it runs low again
		deliveries.remove(delivery);
	}

	/** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(FoodOrder order) {
    	// Animation routine
		DoCooking(order);
		// Now check the inventory to see if anything is running low
		checkInventoryForLowStock();
		order.status = OrderStatus.cooking;
    }

    private void placeOrder(FoodOrder order) {
		DoPlacement(order);
		order.waiter.msgOrderIsReady(order.tableNum, order.food);
		orders.remove(order);
    }
    
    private void checkInventoryForLowStock() {
    	print("Checking inventory for low stock...");
    	for(int i = 0; i < menu.getLength(); i ++) {
    		String menuItemName = menu.itemAtIndex(i).getName();
    		if(inventory.getQuantity(menuItemName) <= LOW_STOCK && !inventory.alreadyOrdered(menuItemName)) {
    			// Order more of this item!
    			print(cashier.getName() + ", please order some more " + menuItemName);
    			cashier.msgOrderMoreOf(i, STOCK_ORDER_QUANTITY);
    			inventory.setOrdered(menuItemName, true);
    		}
    	}
    }
    
    private void checkRevolvingStand() {
    	print("Checking revolving stand for pending orders...");
    	FoodOrder newOrder = revolvingStand.remove();
    	if(newOrder != null)
    	{
	    	print("Removed " + newOrder.toString() + " from stand");
	    	orders.add(newOrder);
	    	checkRevolvingStand = false;
	    	timer.schedule(new TimerTask() {
	    		public void run() {
	    			checkRevolvingStand = true;
	    			stateChanged();
	    		}
	    	}, CHECK_REVOLVING_STAND_DELAY);
    	}
    }
    
    // *** EXTRA -- all the simulation routines***

    /** Returns the name of the cook */
    public String getName() {
        return name;
    }

    private void DoCooking(final FoodOrder order) {
		print("Cooking: " + order + " for table:" + (order.tableNum+1));
		
		// Check if cook has any of the order in his inventory
		if(inventory.getQuantity("Steak") > 0) {
			// Subtract 1 of this item from the cook's inventory
			inventory.subtractFromQuantity(order.choice.getName(), 1);
			
			// Put it on the grill. Do GUI stuff
			order.food = new Food(order.choice.getName().substring(0,2), new Color(0,255,255), restaurant);
			order.food.cookFood();
		
			timer.schedule(new TimerTask() {
			    public void run() {				//this routine is like a message reception    
					order.status = OrderStatus.done;
					stateChanged();
			    }
			}, (int)(inventory.getProduct(order.choice.getName()).getCookTime()*1000));		// uses mapping to find name of the order in inventory and retrieves its cook time
		}
		else {
			// Out of this item!
			print("Looks like I am out of " + order.toString() + "!");
			// Tell waiter the food is out of stock (note: this is for if the customer orders 1 item only, as required in v4.1)
			order.waiter.msgOutOfStock(order.tableNum);
		}
    }
    
    public void DoPlacement(FoodOrder order) {
		print("Order finished: " + order + " for table:" + (order.tableNum + 1));
		order.food.placeOnCounter();
    }
    
    // Unused: replaced by a parameter in CookAgent's constructor as a bugfix for the initial inventory stock check
    public void setCashier(CashierAgent cashier) {
    	this.cashier = cashier;
    }

	public void setRevolvingStand(RevolvingStandMonitor revolvingStand) {
		this.revolvingStand = revolvingStand;
	}
}


    
