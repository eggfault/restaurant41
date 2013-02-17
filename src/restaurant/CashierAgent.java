package restaurant;

import agent.Agent;
import java.util.*;
import restaurant.layoutGUI.*;
import java.awt.Color;


/** Cashier agent for restaurant.
 *  Collects money from customers.
 *  Handles ordering food from market.
 */
public class CashierAgent extends Agent {

    // Name of the cashier
    private String name;
    
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

    // *** MESSAGES ***

    /** Customer sends this when he is ready to pay.
     * @param customer customer who is paying the cashier. */
    public void msgPayForFood(CustomerAgent customer) {
    	/*
		for(MyCustomer c:customers) {
		    if(c.cmr.equals(customer)) {
				c.state = CustomerState.IS_DONE;
				stateChanged();
				return;
		    }
		}*/
    	System.out.println("Customer paid cashier");
    }
    
    // *** SCHEDULER ***
    protected boolean pickAndExecuteAnAction() {

    	return false;
    }
    
    // *** ACTIONS ***
    
    

    // *** EXTRA ***

    /** Returns the name of the cashier */
    public String getName() {
        return name;
    }
}


    
