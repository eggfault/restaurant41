package restaurant;

import agent.Agent;
import java.util.Timer;
import java.util.TimerTask;
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
    final private int MAX_ITEM_QUANTITY = 5;
    final private int LOW_STOCK = 4;				// when the stock is <= LOW_STOCK, an order for more will be placed
    final private int STOCK_ORDER_QUANTITY = 3;		// how many more of an item to place in an order when it runs low
	
    // List of all the orders
    private List<Order> orders;
    private List<Delivery> deliveries;
    private Inventory inventory;
    public enum OrderStatus {pending, cooking, done}; // order status
    
    // Name of the cook
    private String name;
    
    private Menu menu;
    
    CashierAgent cashier;

    // Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant) {
		super();
		
		this.name = name;
		this.restaurant = restaurant;
		
		orders  = new ArrayList<Order>();
		deliveries = new ArrayList<Delivery>();
		// Create the restaurant's inventory.
		menu = new Menu();
		inventory = new Inventory(menu, MIN_ITEM_QUANTITY, MAX_ITEM_QUANTITY);
    }
    
    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */
    private class Order {
		public WaiterAgent waiter;
		public int tableNum;
		public MenuItem choice;
		public OrderStatus status;
		public Food food; //a gui variable
	
		/** Constructor for Order class 
		 * @param waiter waiter that this order belongs to
		 * @param tableNum identification number for the table
		 * @param choice type of food to be cooked 
		 */
		public Order(WaiterAgent waiter, int tableNum, MenuItem choice) {
		    this.waiter = waiter;
		    this.choice = choice;
		    this.tableNum = tableNum;
		    this.status = OrderStatus.pending;
		}
	
		/** Represents the object as a string */
		public String toString() {
		    return choice.getName() + " for " + waiter ;
		}
    }
    
    /** Represents a delivery from the market for food ordered by the cashier */
    private class Delivery {
    	public String productName;
    	public int quantity;
    	
    	public Delivery(String productName, int deliverQuantity) {
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
		orders.add(new Order(waiter, tableNum, choice));
		stateChanged();
    }
    
    /** Message from the market to deliver the food ordered by the cashier */
	public void msgDeliverOrder(String productName, int deliverQuantity) {
		deliveries.add(new Delivery(productName, deliverQuantity));
		stateChanged();
	}

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
    	
		// If there exists an order o whose status is done, place o.
		for(Order o:orders) {
		    if(o.status == OrderStatus.done) {
				placeOrder(o);
				return true;
		    }
		}
		
		// If there exists an order o whose status is pending, cook o.
		for(Order o:orders) {
		    if(o.status == OrderStatus.pending) {
				cookOrder(o);
				return true;
		    }
		}
		
		// Process deliveries
		for(Delivery d:deliveries) {
			// There is only one status; all deliveries are removed from the list after being processed
			processDelivery(d);
			return true;
		}
		
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
    }
    

    // *** ACTIONS ***
    
    private void processDelivery(Delivery delivery) {
    	print("Received " + delivery.quantity + " of " + delivery.productName + " from the market!");
    	inventory.addToQuantity(delivery.productName, delivery.quantity);
		deliveries.remove(delivery);
	}

	/** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(Order order) {
		DoCooking(order);
		order.status = OrderStatus.cooking;
    }

    private void placeOrder(Order order) {
		DoPlacement(order);
		order.waiter.msgOrderIsReady(order.tableNum, order.food);
		orders.remove(order);
    }

    // *** EXTRA -- all the simulation routines***

    /** Returns the name of the cook */
    public String getName() {
        return name;
    }

    private void DoCooking(final Order order) {
		print("Cooking: " + order + " for table:" + (order.tableNum+1));
		
		// Subtract 1 of this item from the cook's inventory
		inventory.subtractFromQuantity(order.choice.getName(), 1);
		
		// Put it on the grill. Do GUI stuff
		order.food = new Food(order.choice.getName().substring(0,2), new Color(0,255,255), restaurant);
		order.food.cookFood();
	
		timer.schedule(new TimerTask() {
		    public void run() {//this routine is like a message reception    
				order.status = OrderStatus.done;
				stateChanged();
		    }
		}, (int)(inventory.getProduct(order.choice.getName()).getCookTime()*1000));		// uses mapping to find name of the order in inventory and retrieves its cook time
		
		// Now check the inventory to see if anything is running low
		checkInventoryForLowStock();
    }
    
    public void DoPlacement(Order order) {
		print("Order finished: " + order + " for table:" + (order.tableNum+1));
		order.food.placeOnCounter();
    }
    
    private void checkInventoryForLowStock() {
    	print("Checking inventory for low stock...");
    	for(int i = 0; i < menu.getLength(); i ++) {
    		String menuItemName = menu.itemAtIndex(i).getName();
    		if(inventory.getQuantity(menuItemName) <= LOW_STOCK) {
    			cashier.msgOrderMoreOf(i, STOCK_ORDER_QUANTITY);
    			print(cashier.getName() + ", please order some more " + menuItemName);
    		}
    	}
    }
    
    public void setCashier(CashierAgent cashier) {
    	this.cashier = cashier;
    }
}


    
