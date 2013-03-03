/**
 * 
 */
package restaurant.test.Mock;

import restaurant.interfaces.Customer;

public class MockCustomer extends MockAgent implements Customer {
	public EventLog log = new EventLog();
	
	public MockCustomer(String name) {
		super(name);
	}
    
    public void msgHereIsYourChange(double amount) {
    	log.add(new LoggedEvent("Received msgHereIsYourChange"));
    }

	public void msgGoWashDishes() {
		log.add(new LoggedEvent("Received msgGoWashDishes"));
	}

}
