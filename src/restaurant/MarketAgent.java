package restaurant;

import agent.Agent;
import java.util.*;
import restaurant.layoutGUI.*;

/** Market agent for restaurant.
 *  Is provider of food for the cook.
 *  Only interacts directly with the cashier.
 */
public class MarketAgent extends Agent {
	// Constants
	final private int MIN_ITEM_QUANTITY = 0;
    final private int MAX_ITEM_QUANTITY = 10;
	
	public enum OrderStatus {requesting, pending, done, canceled};
    private String name;									// name of the market
    private CashierAgent cashier;							// cashier who places the orders
    private Menu menu;										// market's copy of the menu
    private List<Order> orders;								// list of all the orders
    private Inventory inventory;							// inventory of all MenuItems
    private int money;										// Amount of money the market has
    														// (this is not actually used for anything, but the money is still tracked)
    Timer timer = new Timer();								// timer for simulations

    /** Constructor for CashierAgent class
     * @param name name of the cashier
     */
    public MarketAgent(String name, CashierAgent cashier) {
		super();
	
		this.name = name;
		this.cashier = cashier;
		
		menu = new Menu();
		orders = new ArrayList<Order>();
		inventory = new Inventory(menu, MIN_ITEM_QUANTITY, MAX_ITEM_QUANTITY);
		
		money = 0;			// market will start with $0 (it won't spend any money, just collect it)
    }
    
    private class Order {
    	public String name;
		public CashierAgent cashier;
		public int productIndex;
		public int quantity;
		public OrderStatus status;
		public double myMarketPrice;
	
		/** Constructor for Order class 
		 * @param customer customer that this transaction is for
		 * @param bill the customer's bill
		 * @param payment the amount of money the customer paid 
		 */
		public Order(CashierAgent cashier, int productIndex, int quantity) {
		    this.cashier = cashier;
		    this.quantity = quantity;
		    this.productIndex = productIndex;
		    
		    status = OrderStatus.requesting;
		    name = menu.itemAtIndex(productIndex).getName();
		}
    }
    
    // *** MESSAGES ***
    /** Cashier sends this when he places an order
     * @param cashier cashier who is paying the cashier. */
    public void msgRequestOrder(CashierAgent cashier, int productIndex, int quantity) {
    	// Find product with the specified name
    	orders.add(new Order(cashier, productIndex, quantity));
    	stateChanged();
    }
    
    // *** SCHEDULER ***
    protected boolean pickAndExecuteAnAction() {
    	
    	// Delete any canceled orders (supposed be concurrent-modification-safe, but probably isn't)
    	Iterator<Order> iter = orders.iterator();
    	while(iter.hasNext()) {
    		if(iter.next().status == OrderStatus.canceled) iter.remove();
    	}
    	
    	for(Order o:orders) {
		    if(o.status == OrderStatus.requesting) {
		    	synchronized(orders) {
		    		calculateOrder(o);
		    	}
				return true;
		    }
		}
    	
    	return false;
    }
    
    // *** ACTIONS ***
    
    private void calculateOrder(Order order) {
    	print("Order requested: " + order.quantity + " orders of " + order.name);
    	int myQuantity = inventory.getQuantity(order.name);
    	// Check if the market has any of the request product in stock
    	if(myQuantity >= order.quantity) {			// enough in stock to completely fulfill the request
    		print("We can provide " + order.quantity + " " + order.name + "s because we have " + myQuantity + " of them in stock!");
    		// Calculate invoice
    		// Market price will be 1/3 to 1/2 of the menu price
    		order.myMarketPrice = 1/(Math.random() + 2)*menu.itemAtIndex(order.productIndex).getPrice();
    		order.cashier.msgHereIsYourOrderInvoice(order.productIndex, order.myMarketPrice*order.quantity);
    		order.status = OrderStatus.pending;
    	}
    	else if(myQuantity > 0) {					// not enough to fulfill request but more than 0
    		print("We do not have " + order.quantity + " " + order.name + "s but we do have " + inventory.getQuantity(order.name) + " of them!");
    		order.status = OrderStatus.pending;
    	}
    	else {																// none in stock
    		print("Sorry, we do not have any " + order.name + "s in stock!");
    		order.status = OrderStatus.canceled;			// cancel the order
    	}
    	stateChanged();
	}

    // *** EXTRA ***

	/** Returns the name of the market */
    public String getName() {
        return name;
    }
}


    
