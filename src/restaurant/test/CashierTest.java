package restaurant.test;

import static org.junit.Assert.*;
import java.util.*;
import junit.framework.TestCase;
import org.junit.Test;
import restaurant.CashierAgent;
import restaurant.interfaces.Market;
import restaurant.test.Mock.MockCustomer;
import restaurant.test.Mock.MockMarket;

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
		assertTrue("MockCustomer should have received msgHereIsYourChange: "
			+ customer.log.toString(), customer.log.containsString("Received msgHereIsYourChange"));
		assertEquals("Only 1 message should have been sent to MockCustomer. Event log: "
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
		assertTrue("MockCustomer should have received msgGoWashDishes: "
			+ customer.log.toString(), customer.log.containsString("Received msgGoWashDishes"));
		assertEquals("Only 1 message should have been sent to MockCustomer. Event log: "
			+ customer.log.toString(), 1, customer.log.size());
	}
	
	@Test
	public void testMsgOrderMoreOf() {
		// Create a new cashier agent
		CashierAgent cashier = new CashierAgent("Cashier1",null);
		
		// Create a mock market
		MockMarket market1 = new MockMarket("Market1");
		
		// Create a list of the mock markets
		List<Market> markets = new ArrayList<Market>();
		markets.add(market1);
		
		// Set the cashier's market list
		cashier.setMarkets(markets);
		
		// Tell cashier to order 3 more steaks
		cashier.msgOrderMoreOf(0, 3);
		
		// Check for firing of actions from message handler
		assertEquals("MockMarket1 should have an empty event log before the Cashier's scheduler is called. " +
			"Instead, the MockMarket1's event log reads: " + market1.log.toString(), 0, market1.log.size());
		
		cashier.pickAndExecuteAnAction();
		
		// Make sure MockMarket received the steak order request
		assertTrue("MockMarket should have received msgRequestOrder: "
			+ market1.log.toString(), market1.log.containsString("Received msgRequestOrder"));
		
		// Give cashier an invoice costing $9.00 for the steak order
		cashier.msgHereIsYourOrderInvoice(market1, 0, 9.00);
		
		cashier.pickAndExecuteAnAction();
		
		// Make sure MockMarket received the payment for the steak order
		assertTrue("MockMarket should have received msgRequestOrder: "
			+ market1.log.toString(), market1.log.containsString("Received msgPayForOrder"));
	}
}
