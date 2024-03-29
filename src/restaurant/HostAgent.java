package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.interfaces.Waiter;


/** Host agent for restaurant.
 *  Keeps a list of all the waiters and tables.
 *  Assigns new customers to waiters for seating and 
 *  keeps a list of waiting customers.
 *  Interacts with customers and waiters.
 */
public class HostAgent extends Agent {

    /** Private class storing all the information for each table,
     * including table number and state. */
    private class Table {
		public int tableNum;
		public boolean occupied;
	
		/** Constructor for table class.
		 * @param num identification number
		 */
		public Table(int num) {
		    tableNum = num;
		    occupied = false;
		}	
    }

    /** Private class to hold waiter information and state */
    private class MyWaiter {
		public WaiterAgent wtr;
		public boolean working;
		public boolean wantsABreak;
	
		/** Constructor for MyWaiter class
		 * @param waiter
		 */
		public MyWaiter(WaiterAgent waiter) {
		    wtr = waiter;
		    working = true;
		    wantsABreak = false;
		}
    }
    
    private class MyCustomer {
    	public CustomerAgent cmr;
    	public boolean alreadyAskedToWait = false;
    	
    	/** Constructor for MyCustomer class
		 * @param customer
		 */
		public MyCustomer(CustomerAgent customer) {
		    cmr = customer;
		}
    }
    
    // List of all the customers that need a table
    private List<MyCustomer> waitList = Collections.synchronizedList(new ArrayList<MyCustomer>());

    // List of all waiter that exist.
    private List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
    private int nextWaiter = 0; // The next waiter that needs a customer
    
    // List of all the tables
    int nTables;
    private Table tables[];

    // Name of the host
    private String name;

    /** Constructor for HostAgent class 
     * @param name name of the host */
    public HostAgent(String name, int ntables) {
		super();
		this.nTables = ntables;
		tables = new Table[nTables];
	
		for(int i = 0; i < nTables; i++) {
		    tables[i] = new Table(i);
		}
		this.name = name;
    }

    // *** MESSAGES ***

    /** Customer sends this message to be added to the wait list 
     * @param customer customer that wants to be added */
    public void msgIWantToEat(CustomerAgent customer) {
		waitList.add(new MyCustomer(customer));
		stateChanged();
    }

    /** Waiter sends this message after the customer has left the table 
     * @param tableNum table identification number */
    public void msgTableIsFree(int tableNum) {
		tables[tableNum].occupied = false;
		stateChanged();
    }
    
    /** Customer decides to wait to be seated */
	public void msgIWillWait(CustomerAgent customer) {
		// Do not do anything
		// Customer will simply remain in the waitList
		print("Ok, thank you for deciding to wait. We will serve you momentarily.");
	}
	
	/** Customer will not wait to be seated; customer will leave */
	public void msgIWillNotWait(CustomerAgent customer) {
		// Find customer and remove him from the waitlist
		MyCustomer tc = new MyCustomer(customer);
		for(MyCustomer c:waitList) {
			if(c.cmr.equals(customer))
				synchronized(waitList) {
					tc = c;
				}
		}
		waitList.remove(tc);
		// Some parting words </3
		print("Well, screw you too. Goodbye.");
	}
	
	/** Sent by the waiter when he wants to take a break */
	public void msgIWantToTakeABreak(WaiterAgent waiterAgent) {
		// Find the matching waiter
		for(MyWaiter w:waiters) {
			if(w.wtr == waiterAgent) {
				synchronized(waiters) {
					w.wantsABreak = true;
					return;
				}
			}
		}
	}
	
	/** Sent by the waiter when he is done with his break */
	public void msgIAmDoneWithMyBreak(WaiterAgent waiter) {
		// Find the matching waiter
		for(MyWaiter w:waiters) {
			if(w.wtr == waiter) {
				synchronized(waiters) {
					print("Ok, " + w.wtr.getName() + " go back to work now.");
					w.working = true;
					return;
				}
			}
		}		
	}
	
	/** Sent by the waiter if he is forced to take a break via the GUI */
	public void msgIAmForcedToTakeABreak(WaiterAgent waiterAgent) {
		// Find the matching waiter
		for(MyWaiter w:waiters) {
			if(w.wtr == waiterAgent) {
				synchronized(waiters) {
					print("Well " + w.wtr.getName() + ", if it is the will of The User, then you shall take a break.");
					w.working = false;
					return;
				}
			}
		}		
	}
    
    // *** SCHEDULER ***
    /** Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
    	for(MyWaiter w:waiters) {
			if(w.wantsABreak) {
				// Waiter wants a break, check if the restaurant is busy
				synchronized(waiters) {
					if(waiters.size() > waitList.size()) {			// use a more sophisticated check later
						tellWaiterToTakeABreak(w);
					}
					else {
						denyWaiterABreak(w);
					}
				}
			}
		}
    	
		if(!waitList.isEmpty() && !waiters.isEmpty()) {
			synchronized(waiters) {
				// Finds the next waiter that is working
				while(!waiters.get(nextWaiter).working) {
				    nextWaiter = (nextWaiter + 1) % waiters.size();
				}
		    }
		    print("Picking waiter number:" + nextWaiter);
		    // Then runs through the tables and finds the first unoccupied 
		    // table and tells the waiter to sit the first customer at that table
		    for(int i=0; i < nTables; i++) {
				if(!tables[i].occupied) {
				    synchronized(waitList) {
						tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),
						    waitList.get(0), i);
				    }
				    return true;
				}
		    }
		    // All tables are occupied, ask the customers if they want to wait
		    for(int i=0; i < waitList.size(); i++)
		    {
			    synchronized(waitList) {
			    	if(!waitList.get(i).alreadyAskedToWait)
			    		askCustomerToWait(waitList.get(i));
			    }
		    }
		}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Assigns a customer to a specified waiter and 
     * tells that waiter which table to sit them at.
     * @param waiter
     * @param customer
     * @param tableNum */
    private void tellWaiterToSitCustomerAtTable(MyWaiter waiter, MyCustomer customer, int tableNum) {
		print("Telling " + waiter.wtr + " to sit " + customer.cmr + " at table " + (tableNum+1));
		waiter.wtr.msgSitCustomerAtTable(customer.cmr, tableNum);
		tables[tableNum].occupied = true;
		waitList.remove(customer);
		nextWaiter = (nextWaiter+1) % waiters.size();
		stateChanged();
    }
    
    private void askCustomerToWait(MyCustomer customer)
    {
    	print("Hi " + customer.cmr.getName() + " we are currently very busy! Would you like to wait?");
    	customer.cmr.msgWouldYouLikeToWait();
    	customer.alreadyAskedToWait = true;
    }

    // *** EXTRA ***

    /** Returns the name of the host 
     * @return name of host */
    public String getName() {
        return name;
    }    

    /** Hack to enable the host to know of all possible waiters 
     * @param waiter new waiter to be added to list
     */
    public void setWaiter(WaiterAgent waiter) {
		waiters.add(new MyWaiter(waiter));
		stateChanged();
    }
    
    public void tellWaiterToTakeABreak(MyWaiter waiter) {
    	print("Ok " + waiter.wtr.getName() + ", you can take a break.");
    	waiter.wtr.msgYouCanTakeABreak();
    	waiter.working = false;
    	waiter.wantsABreak = false;
    }
    
    public void denyWaiterABreak(MyWaiter waiter) {
    	print("No, " + waiter.wtr.getName() + ", we need you right now. Keep working.");
    	waiter.wantsABreak = false;
    }
    
    // Gautam Nayak - Gui calls this when table is created in animation
    public void addTable() {
		nTables++;
		Table[] tempTables = new Table[nTables];
		for(int i=0; i < nTables - 1; i++) {
		    tempTables[i] = tables[i];
		}  		  			
		tempTables[nTables - 1] = new Table(nTables - 1);
		tables = tempTables;
    }
}
