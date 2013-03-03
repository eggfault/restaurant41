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
	 * This tests the regular routine of a customer with
	 * enough money paying for his or her own meal. The
	 * cashier should give the customer any change for the
	 * payment.
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
	
	/**
	 * This tests the scenario in which a paying customer cannot
	 * pay for his or her food. The cashier should tell the customer
	 * to go wash dishes.
	 */
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
	
	/**
	 * This test checks to see if ordering from a market is successful.
	 * The cashier will be given an order request (presumably from the cook)
	 * and place the order with a market. The market will reply with an
	 * invoice for the cost of the order, and the cashier will pay the market.
	 */
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
	
	/**
	 * This test checks to see if the market rotation system works when the initial market
	 * is out of stock for a requested order. If successful, the cashier should switch to
	 * ordering from market2 after market1 reports that it is out of steak.
	 */
	@Test
	public void testMsgOutOfStock() {
		// Create a new cashier agent
		CashierAgent cashier = new CashierAgent("Cashier1",null);
		
		// Create a mock market
		MockMarket market1 = new MockMarket("Market1");
		MockMarket market2 = new MockMarket("Market2");
		
		// Create a list of the mock markets
		List<Market> markets = new ArrayList<Market>();
		markets.add(market1);
		markets.add(market2);
		
		// Set the cashier's market list
		cashier.setMarkets(markets);
		
		// Tell cashier to order 8 more steaks
		cashier.msgOrderMoreOf(0, 8);
		
		// Check for firing of actions from message handler
		assertEquals("MockMarket1 should have an empty event log before the Cashier's scheduler is called. " +
			"Instead, the MockMarket1's event log reads: " + market1.log.toString(), 0, market1.log.size());
		
		cashier.pickAndExecuteAnAction();
		
		// Make sure market1 received the steak order request
		assertTrue("market1 should have received msgRequestOrder: "
			+ market1.log.toString(), market1.log.containsString("Received msgRequestOrder"));
		
		// Simulate market 1 not having enough steak in stock
		cashier.msgOutOfStock(market1, 0);
		
		cashier.pickAndExecuteAnAction();
		
		// Make sure the cashier re-ordered from market2 (account for the 1.5 second delay in ordering)
		int timer = 0;
		int timeout = 3000;
		while (timer < timeout && !market2.log.containsString("Received msgRequestOrder")) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// do nothing
			}
			timer += 50;
		}
		// Either timed out or the string was found in the log, check to see which occurred
		assertTrue("market2 should have received msgRequestOrder: ",
			market2.log.containsString("Received msgRequestOrder"));
	}
}
