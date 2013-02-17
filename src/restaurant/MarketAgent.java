package restaurant;

import agent.Agent;
import java.util.*;
import restaurant.layoutGUI.*;

/** Market agent for restaurant.
 *  Is provider of food for the cook.
 *  Only interacts directly with the cashier.
 */
public class MarketAgent extends Agent {
	public enum Status {requesting, pending, done}; // transaction status
	
    // Name of the market
    private String name;
    
    // Cashier who places the orders
    private CashierAgent cashier;
    
    // Menu
    private Menu menu;
    
    // List of all the orders
    private List<Order> orders = new ArrayList<Order>();
    
    // Amount of money the market has (this is not actually used for anything, but the money is still tracked)
    private int money;

    // Timer for simulation
    Timer timer = new Timer();

    /** Constructor for CashierAgent class
     * @param name name of the cashier
     */
    public MarketAgent(String name, CashierAgent cashier) {
		super();
	
		this.name = name;
		this.cashier = cashier;
		this.menu = new Menu();
		
		money = 0;			// market will start with $0 (it won't spend any money, just collect it)
    }
    
    private class Order {
		public CashierAgent cashier;
		public int productIndex;
		public int quantity;
		public Status status;
	
		/** Constructor for Order class 
		 * @param customer customer that this transaction is for
		 * @param bill the customer's bill
		 * @param payment the amount of money the customer paid 
		 */
		public Order(CashierAgent cashier, int productIndex, int quantity) {
		    this.cashier = cashier;
		    this.quantity = quantity;
		    this.productIndex = productIndex;
		    this.status = Status.pending;
		    
		    // Product availablity is currently hardcoded
		    // code
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
    	
    	for(Order o:orders) {
		    if(o.status == Status.pending) {
				processOrder(o);
				return true;
		    }
		}
    	
    	return false;
    }
    
    // *** ACTIONS ***
    
    private void processOrder(Order order) {
    	System.out.println("Order processed: " + order.quantity + " orders of " + menu.itemAtIndex(order.productIndex).getName());
    	// Add a delay here eventually...
    	orders.remove(order);
    	
	}

    // *** EXTRA ***

	/** Returns the name of the cashier */
    public String getName() {
        return name;
    }
}


    
