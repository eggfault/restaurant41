package restaurant;

import agent.Agent;
import java.util.*;
import restaurant.layoutGUI.*;

/** Cashier agent for restaurant.
 *  Collects money from customers.
 *  Handles ordering food from market.
 */
public class CashierAgent extends Agent {
	public enum TransactionStatus {pending, failedToPay}; 			// transaction status
	public enum OrderStatus {pending, requested, needToPay};	// order status
	
    private String name;						// Name of the cashier
    private List<Transaction> transactions;		// List of all the transactions
    private double money;							// Amount of money the cashier has
    private List<Order> orders;					// Orders for more food from markets
    Timer timer = new Timer();					// Timer for simulations
    Restaurant restaurant; 						// Gui layout
    //private List<MarketAgent> markets;			// List of markets to order food from (unused in v4.1)
    private MarketAgent market;						// Market to order food from

    /** Constructor for CashierAgent class
     * @param name name of the cashier
     */
    public CashierAgent(String name, Restaurant restaurant) {
		super();
	
		this.name = name;
		this.restaurant = restaurant;
		
		orders = new ArrayList<Order>();
		transactions = new ArrayList<Transaction>();
		
		money = 2500.00;			// cashier will start with $2500
    }
    
    private class Transaction {
		public CustomerAgent customer;
		public double bill;
		public double payment;
		public TransactionStatus status;
	
		/** Constructor for Transaction class 
		 * @param customer customer that this transaction is for
		 * @param bill the customer's bill
		 * @param payment the amount of money the customer paid 
		 */
		public Transaction(CustomerAgent customer, double bill, double payment, TransactionStatus status) {
		    this.customer = customer;
		    this.bill = bill;
		    this.payment = payment;
		    this.status = status;
		}
    }
    
    private class Order {
    	public int productIndex;
    	public int quantity;
    	public OrderStatus status;
    	public double cost;
    	
    	public Order(int productIndex, int quantity) {
    		this.productIndex = productIndex;
    		this.quantity = quantity;
    		this.status = OrderStatus.pending;
    		cost = 0;
    	}
    }
    
    // *** MESSAGES ***
    /** Customer sends this when he is ready to pay.
     * @param customer customer who is paying the cashier. */
    public void msgPayForFood(CustomerAgent customer, double bill, double payment) {
    	transactions.add(new Transaction(customer, bill, payment, TransactionStatus.pending));
    	stateChanged();
    }
    
    /** Sent by customer when he cannot pay for the food he already ordered and ate */
	public void msgIDoNotHaveEnoughMoney(CustomerAgent customer, double bill, double payment) {
		transactions.add(new Transaction(customer, bill, payment, TransactionStatus.failedToPay));
		stateChanged();
	}
    
    /** Sent by cook when requesting more of the specified MenuItem */
    public void msgOrderMoreOf(int productIndex, int requestedQuantity) {
		orders.add(new Order(productIndex, requestedQuantity));
		stateChanged();
	}
    
    /** Sent by a market after cashier requests an order */
    public void msgHereIsYourOrderInvoice(int productIndex, double orderPrice) {
		print("Received invoice from " + market.getName() + " for a price of $" + orderPrice);
		// Find the matching order
		for(Order o:orders) {
			if(o.productIndex == productIndex) {
				o.cost = orderPrice;
				stateChanged();
				o.status = OrderStatus.needToPay;
				stateChanged();
				return;
			}
		}
	}
    
    // *** SCHEDULER ***
    protected boolean pickAndExecuteAnAction() {
    	
    	for(Transaction t:transactions) {
		    if(t.status == TransactionStatus.pending) {
		    	synchronized(transactions) {
		    		handleTransaction(t);
		    	}
				return true;
		    }
		}
    	
    	for(Transaction t:transactions) {
		    if(t.status == TransactionStatus.failedToPay) {
		    	synchronized(transactions) {
		    		punishCustomer(t);
		    	}
				return true;
		    }
		}
    	
    	for(Order o:orders) {
		    if(o.status == OrderStatus.pending) {
		    	synchronized(orders) {
		    		placeOrder(o);
		    	}
				return true;
		    }
		}
    	
    	for(Order o:orders) {
    		if(o.status == OrderStatus.needToPay) {
    			synchronized(orders) {
    				payForOrder(o);
    			}
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    // *** ACTIONS ***
    
    private void handleTransaction(Transaction transaction) {
    	double change = (double)Math.round((transaction.payment - transaction.bill) * 100) / 100;		// rounds the change to decimal places
    	print(transaction.customer.getName() + " paid cashier. Change due is $" + change);
    	money += transaction.payment;
    	transaction.customer.msgHereIsYourChange(change);
    	transactions.remove(transaction);
	}
    
    private void placeOrder(Order order) {
    	market.msgRequestOrder(this, order.productIndex, order.quantity);
    	order.status = OrderStatus.requested;
    	stateChanged();
    }
    
    private void payForOrder(Order order) {
    	double payment = order.cost;
    	money -= payment;
    	print("Paying market $" + payment + " for order!");
    	market.msgPayForOrder(order.productIndex, payment);
    	orders.remove(order);
    }
    
    private void punishCustomer(Transaction transaction) {
    	print(transaction.customer.getName() + " you filthy scum! Go wash the dishes as punishment or I will call the police!");
    	transaction.customer.msgGoWashDishes();
    	transactions.remove(transaction);
    }

    // *** EXTRA ***

	/** Returns the name of the cashier */
    public String getName() {
        return name;
    }

    public void setMarket(MarketAgent market) {
    	this.market = market;
    }
    
    /** Sets the list of markets (should be sent from RestaurantPanel) */
    // This is currently unused now, I am just going to make 1 market for v4.1
//	public void setMarkets(List<MarketAgent> markets) {
//		this.markets = markets; 
//	}
}


    
