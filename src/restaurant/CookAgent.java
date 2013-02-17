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

    // List of all the orders
    private List<Order> orders = new ArrayList<Order>();
    private Map<String,FoodData> inventory = new HashMap<String,FoodData>();
    public enum Status {pending, cooking, done}; // order status

    // Name of the cook
    private String name;

    // Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout
    
    // Constants
    final private int MIN_ITEM_QUANTITY = 2;
    final private int MAX_ITEM_QUANTITY = 5;

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant) {
		super();
	
		this.name = name;
		this.restaurant = restaurant;
		// Create the restaurant's inventory.
		Menu menu = new Menu();
		for(int i = 0; i < menu.getLength(); i ++)
		{
			String itemName = menu.itemAtIndex(i).getName();
			int itemQuantity = (int)(Math.random()*(MAX_ITEM_QUANTITY-MIN_ITEM_QUANTITY))+MIN_ITEM_QUANTITY;
			inventory.put(itemName, new FoodData(itemName, itemQuantity));
		}
		/*
		// old way of filling up inventory
		inventory.put("Steak", new FoodData("Steak", 5));
		inventory.put("Chicken", new FoodData("Chicken", 4));
		inventory.put("Pizza", new FoodData("Pizza", 3));
		inventory.put("Salad", new FoodData("Salad", 2));
		*/
    }
    /** Private class to store information about food.
     *  Contains the food type, its cooking time, and ...
     */
    private class FoodData {
		String type; //kind of food
		double cookTime;
		// other things ...
		
		public FoodData(String type, double cookTime){
		    this.type = type;
		    this.cookTime = cookTime;
		}
    }
    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */
    private class Order {
		public WaiterAgent waiter;
		public int tableNum;
		public MenuItem choice;
		public Status status;
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
		    this.status = Status.pending;
		}
	
		/** Represents the object as a string */
		public String toString() {
		    return choice.getName() + " for " + waiter ;
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


    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
		//If there exists an order o whose status is done, place o.
		for(Order o:orders) {
		    if(o.status == Status.done) {
				placeOrder(o);
				return true;
		    }
		}
		//If there exists an order o whose status is pending, cook o.
		for(Order o:orders) {
		    if(o.status == Status.pending) {
				cookOrder(o);
				return true;
		    }
		}
	
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
    }
    

    // *** ACTIONS ***
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(Order order) {
		DoCooking(order);
		order.status = Status.cooking;
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
		//put it on the grill. gui stuff
		order.food = new Food(order.choice.getName().substring(0,2),new Color(0,255,255), restaurant);
		order.food.cookFood();
	
		timer.schedule(new TimerTask() {
		    public void run() {//this routine is like a message reception    
				order.status = Status.done;
				stateChanged();
		    }
		}, (int)(inventory.get(order.choice.getName()).cookTime*1000));
    }
    
    public void DoPlacement(Order order) {
		print("Order finished: " + order + " for table:" + (order.tableNum+1));
		order.food.placeOnCounter();
    }
}


    
