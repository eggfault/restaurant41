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
		
		// The customer pays the cashier $5.00 for a bill worth $4.50
		cashier.msgPayForFood(customer, 4.50, 5.00);
		
		// Check for firing of actions from message handler
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. " +
				"Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		
		cashier.pickAndExecuteAnAction();
		
		// Now check with assert statements to see if the scheduler did what it was supposed to
		assertTrue(
				"MockCustomer should have received a message containing change due: "
						+ customer.log.toString(), customer.log.containsString("Received msgHereIsYourChange"));
		assertEquals(
				"Only 1 message should have been sent to MockCustomer. Event log: "
						+ customer.log.toString(), 1, customer.log.size());
	}
	
	@Test
	public void testMsgIDoNotHaveEnoughMoney() {
		// Create a new cashier agent
		CashierAgent cashier = new CashierAgent("Cashier1",null);
		
		// Create a mock customer
		MockCustomer customer = new MockCustomer("Customer1");
		
		// The customer does not have enough money and pays the cashier $0.00 for a bill worth $5.00
		cashier.msgIDoNotHaveEnoughMoney(customer, 5.00, 0.00);
		
		// Check for firing of actions from message handler
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. " +
				"Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
				
		cashier.pickAndExecuteAnAction();
		
		// Now check with assert statements to see if the scheduler did what it was supposed to
		assertTrue(
				"MockCustomer should have received a message to go wash dishes: "
						+ customer.log.toString(), customer.log.containsString("Received msgGoWashDishes"));
		assertEquals(
				"Only 1 message should have been sent to MockCustomer. Event log: "
						+ customer.log.toString(), 1, customer.log.size());
	}
	
	@Test
	public void testMsgOrderMoreOf() {
		
	}
}
