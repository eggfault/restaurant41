package restaurant;

import restaurant.CookAgent.OrderStatus;
import restaurant.layoutGUI.Food;

/** Class used for storing food order data.
 *  This used to be a private class exclusive to CookAgents,
 *  but because of the data sharing required in v4.2, it is
 *  more logical to allow both waiters and the cook
 *  to use this class by placing instances of into the
 *  revolving stand.
 *  Contains the waiter, table number, food item,
 *  cooktime and status.
 */
public class FoodOrder {
	public WaiterAgent waiter;
	public int tableNum;
	public MenuItem choice;
	public OrderStatus status;
	public Food food; // A GUI variable

	/** Constructor for Order class
	 * @param waiter waiter that this order belongs to
	 * @param tableNum identification number for the table
	 * @param choice type of food to be cooked 
	 */
	public FoodOrder(WaiterAgent waiter, int tableNum, MenuItem choice) {
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