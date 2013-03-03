package restaurant.interfaces;

import restaurant.CustomerAgent.AgentEvent;

public interface Customer {

	/** Waiter sends this message to give the customer his or her bill */
    public abstract void msgHereIsYourBill();

	/** Returns the name of the customer */
	public abstract String getName();

}