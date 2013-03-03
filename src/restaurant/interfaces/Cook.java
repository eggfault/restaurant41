package restaurant.interfaces;

public interface Cook {

	/**
	 * Message from a waiter giving the cook a new order.
	 * 
	 * @param waiter
	 *            waiter that the order belongs to
	 * @param tableNum
	 *            identification number for the table
	 * @param choice
	 *            type of food to be cooked
	 */
	public abstract void msgHereIsAnOrder(Waiter waiter, int tableNum,
			String choice);

	/** Returns the name of the cook */
	public abstract String getName();

}