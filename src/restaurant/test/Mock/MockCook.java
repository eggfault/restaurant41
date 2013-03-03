/**
 * 
 */
package restaurant.test.Mock;

import restaurant.interfaces.Cook;
import restaurant.interfaces.Waiter;

/**
 * @author Sean Turner
 * 
 */
public class MockCook extends MockAgent implements Cook {

	public MockCook(String name) {
		super(name);
	}

	public EventLog log = new EventLog();

}
