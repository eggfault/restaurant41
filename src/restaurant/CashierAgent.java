package restaurant;

import agent.Agent;
import java.util.*;
import restaurant.layoutGUI.*;

/** Cashier agent for restaurant.
 *  Collects money from customers.
 *  Handles ordering food from market.
 */
public class CashierAgent extends Agent {
	public enum Status {pending, done}; // transaction status
	
    // Name of the cashier
    private String name;
    
    // List of all the transactions
    private List<Transaction> transactions = new ArrayList<Transaction>();
    
    // Amount of money the cashier has
    private int money;

    //Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout

    /** Constructor for CashierAgent class
     * @param name name of the cashier
     */
    public CashierAgent(String name, Restaurant restaurant) {
		super();
	
		this.name = name;
		this.restaurant = restaurant;
		
		money = 450;			// cashier will start with $450
    }
    
    private class Transaction {
		public CustomerAgent customer;
		public double bill;
		public double payment;
		public Status status;
	
		/** Constructor for Transaction class 
		 * @param customer customer that this transaction is for
		 * @param bill the customer's bill
		 * @param payment the amount of money the customer paid 
		 */
		public Transaction(CustomerAgent customer, double bill, double payment) {
		    this.customer = customer;
		    this.bill = bill;
		    this.payment = payment;
		    this.status = Status.pending;
		}
    }
    
    // *** MESSAGES ***
    /** Customer sends this when he is ready to pay.
     * @param customer customer who is paying the cashier. */
    public void msgPayForFood(CustomerAgent customer, double bill, double payment) {
    	transactions.add(new Transaction(customer, bill, payment));
    	stateChanged();
    }
    
    // *** SCHEDULER ***
    protected boolean pickAndExecuteAnAction() {
    	
    	for(Transaction t:transactions) {
		    if(t.status == Status.pending) {
				handleTransaction(t);
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

    // *** EXTRA ***

	/** Returns the name of the cashier */
    public String getName() {
        return name;
    }
}


    
