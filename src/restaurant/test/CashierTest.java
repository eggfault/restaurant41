package restaurant.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import org.junit.Test;
import restaurant.CashierAgent;
import restaurant.test.Mock.MockCustomer;

public class CashierTest extends TestCase {
	
	/**
	 * This is the CashierAgent to be tested.
	 */
	public CashierAgent cashier;
	
	/**
	 * Sent by the customer when paying the cashier for food.
	 * The customer DOES have enough money.
	 */
	@Test
	public void testMsgPayForFood() {
		// Create a new cashier agent
		CashierAgent cashier = new CashierAgent("Cashier1",null);
		
		// Create a mock customer
		MockCustomer customer = new MockCustomer("Customer1");
		
		// The customer pays the cashier
		cashier.msgPayForFood(customer, 4.50, 4.50);
	}

}
