package restaurant;

import agent.Agent;
import java.util.*;
import restaurant.CookAgent.Status;
import restaurant.layoutGUI.*;

import java.awt.Color;


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
    
    private void handleTransaction(Transaction t) {
    	double change = (double)Math.round((t.payment - t.bill) * 100) / 100;		// rounds the change to decimal places
    	print(t.customer.getName() + " paid cashier. Change due is $" + change);
    	money += t.payment;
    	t.customer.msgHeresYourChange(change);
    	transactions.remove(t);
	}

    // *** EXTRA ***

	/** Returns the name of the cashier */
    public String getName() {
        return name;
    }
}


    
