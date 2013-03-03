/**
 * 
 */
package restaurant.test.Mock;

import restaurant.Menu;
import restaurant.MenuItem;
import restaurant.WaiterAgent;
import restaurant.interfaces.Customer;

public class MockCustomer extends MockAgent implements Customer {
	private boolean hasEnoughMoney;
	public EventLog log = new EventLog();
	
	public MockCustomer(String name) {
		super(name);
		hasEnoughMoney = true;
	}
	
	/** Waiter sends this message to give the customer his or her bill */
    public void msgHereIsYourBill() {
    	String logData = "Received message msgHereIsYourBill and I ";
    	if(hasEnoughMoney)
    		logData += "DO have enough money to pay for it.";
    	else
    		logData += "do NOT have enough money to pay for it.";
		log.add(new LoggedEvent(logData));
    }
    
    /** Used to unit test non-normative case in which cashier forces the offending customer to wash dishes */
    public void makePoor() {
    	hasEnoughMoney = false;
    }

	@Override
	public void msgFollowMeToTable(WaiterAgent waiter, Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDecided() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWhatWouldYouLike() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleaseReorder() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourFood(MenuItem choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEating() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourChange(double change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoWashDishes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWouldYouLikeToWait() {
		// TODO Auto-generated method stub
		
	}

}
