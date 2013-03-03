/**
 * 
 */
package restaurant.test.Mock;

import restaurant.CashierAgent;
import restaurant.interfaces.Market;

public class MockMarket extends MockAgent implements Market {
	public EventLog log = new EventLog();
	
	public MockMarket(String name) {
		super(name);
	}

	public void msgRequestOrder(CashierAgent cashier, int productIndex, int quantity) {
		log.add(new LoggedEvent("Received msgRequestOrder from " + cashier.getName()));
	}
	
	public void msgPayForOrder(int productIndex, double payment) {
		log.add(new LoggedEvent("Received msgPayForOrder"));
	}

}
