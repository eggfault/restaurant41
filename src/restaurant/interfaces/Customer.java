package restaurant.interfaces;

import restaurant.Menu;
import restaurant.MenuItem;
import restaurant.WaiterAgent;
import restaurant.CustomerAgent.AgentEvent;

public interface Customer {

	/** Waiter sends this message to give the customer his or her bill */
    public abstract void msgHereIsYourBill();
    
    /** Waiter sends this message so the customer knows to sit down  */
    public abstract void msgFollowMeToTable(WaiterAgent waiter, Menu menu);
    
    /** Waiter sends this message to take the customer's order */
    public abstract void msgDecided();
    
    /** Waiter sends this message to take the customer's order */
    public abstract void msgWhatWouldYouLike();
    
    /** Waiter sends this message to retake the customer's order */
    public abstract void msgPleaseReorder();
    
    /** Waiter sends this when the food is ready */ 
    public abstract void msgHereIsYourFood(MenuItem choice);

	/** Returns the name of the customer */
	public abstract String getName();
	
	/** Timer sends this when the customer has finished eating */
    public abstract void msgDoneEating();
    
    /** Cashier sends this to give change back to the customer after he has paid for food */
    public abstract void msgHereIsYourChange(double change);
    
    /** Cashier sends this if customer cannot not pay him */
	public abstract void msgGoWashDishes();
	
	/** Host sends this to ask the customer if he would like to wait to be seated */
    public abstract void msgWouldYouLikeToWait();
    
    

}