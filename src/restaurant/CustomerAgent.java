package restaurant;

import restaurant.gui.RestaurantGui;
import restaurant.layoutGUI.*;
import agent.Agent;
import java.util.*;
import java.awt.Color;

/** Restaurant customer agent. 
 * Comes to the restaurant when he/she becomes hungry.
 * Randomly chooses a menu item and simulates eating 
 * when the food arrives. 
 * Interacts with a waiter only */
public class CustomerAgent extends Agent {
    private String name;
    private int UID;				// Unique ID; this is currently unused (16 Feb 2013, 7:06 PM).
    private int hungerLevel = 5;  	// Determines length of meal
    private Menu menu;
    private MenuItem choice;		// what food the customer decides to order (this is also used to calculate the bill)
    private double bill;			// maybe make a Bill class eventually to track multiple orders and calculate tip, etc.
    private double money;			// how much money the customer has (this is randomized upon initialization)
    private final double MIN_MONEY = 7.50;
    private final double MAX_MONEY = 26.00;
    private final int WASH_DISHES_TIME = 5000;
    
    private RestaurantGui gui;
    
    // ** Agent connections **
    private HostAgent host;
    private WaiterAgent waiter;
    private CashierAgent cashier;
    Restaurant restaurant;
    
    Timer timer = new Timer();
    GuiCustomer guiCustomer; //for gui
   // ** Agent state **
    private boolean isHungry = false; //hack for gui
    public enum AgentState
	    {DoingNothing, WaitingInRestaurant, SeatedWithMenu, WaiterImReadyToOrder, WaitingForFood, Eating,
    	WaiterImReadyToPay, PayingForFood};
	//{NO_ACTION,NEED_SEATED,NEED_DECIDE,NEED_ORDER,NEED_EAT,NEED_LEAVE};
    private AgentState state = AgentState.DoingNothing;//The start state
    public enum AgentEvent 
	    {gotHungry, beingSeated, decidedChoice, waiterToTakeOrder, foodDelivered, doneEating,
    	waiterToGiveBill, donePaying, decidingAboutWaiting, washDishes};
    List<AgentEvent> events = new ArrayList<AgentEvent>();
    
    /** Constructor for CustomerAgent class 
     * @param name name of the customer
     * @param gui reference to the gui so the customer can send it messages
     */
    public CustomerAgent(String name, RestaurantGui gui, Restaurant restaurant) {
		super();
		this.gui = gui;
		this.name = name;
		this.restaurant = restaurant;
		this.money = 0;						// money is set in goingToRestaurant()
		guiCustomer = new GuiCustomer(name.substring(0,2), new Color(0,255,0), restaurant);
    }
    
    public CustomerAgent(String name, Restaurant restaurant) {
		super();
		this.gui = null;
		this.name = name;
		this.restaurant = restaurant;
		this.money = 0;						// money is set in goingToRestaurant()
		guiCustomer = new GuiCustomer(name.substring(0,1), new Color(0,255,0), restaurant);
    }
    
    // *** MESSAGES ***
    /** Sent from GUI to set the customer as hungry */
    public void setHungry() {
		events.add(AgentEvent.gotHungry);
		isHungry = true;
		print("I'm hungry");
		stateChanged();
    }
    /** Waiter sends this message so the customer knows to sit down 
     * @param waiter the waiter that sent the message
     * @param menu a reference to a menu */
    public void msgFollowMeToTable(WaiterAgent waiter, Menu menu) {
		this.menu = menu;
		this.waiter = waiter;
		print("Received msgFollowMeToTable from " + waiter);
		events.add(AgentEvent.beingSeated);
		stateChanged();
    }
    /** Waiter sends this message to take the customer's order */
    public void msgDecided(){
		events.add(AgentEvent.decidedChoice);
		stateChanged(); 
    }
    /** Waiter sends this message to take the customer's order */
    public void msgWhatWouldYouLike() {
		events.add(AgentEvent.waiterToTakeOrder);
		stateChanged(); 
    }
    /** Waiter sends this message to retake the customer's order */
    public void msgPleaseReorder() {
		events.add(AgentEvent.waiterToTakeOrder);
		stateChanged(); 
    }
    /** Waiter sends this message to give the customer his or her bill */
    public void msgHereIsYourBill() {
		events.add(AgentEvent.waiterToGiveBill);
		stateChanged(); 
    }
    /** Waiter sends this when the food is ready 
     * @param choice the food that is done cooking for the customer to eat */
    public void msgHereIsYourFood(MenuItem choice) {
		events.add(AgentEvent.foodDelivered);
		stateChanged();
    }
    /** Timer sends this when the customer has finished eating */
    public void msgDoneEating() {
		events.add(AgentEvent.doneEating);
		stateChanged(); 
    }
    /** Cashier sends this to give change back to the customer after he has paid for food */
    public void msgHereIsYourChange(double change) {
    	money += change;
    	print("Received $" + change + " in change. Now I have $" + cash(money));
    	events.add(AgentEvent.donePaying);
    	stateChanged();
    }
    /** Cashier sends this if customer cannot not pay him */
	public void msgGoWashDishes() {
		events.add(AgentEvent.washDishes);
		stateChanged();
	}
    /** Host sends this to ask the customer if he would like to wait to be seated */
    public void msgWouldYouLikeToWait() {
    	events.add(AgentEvent.decidingAboutWaiting);
    	stateChanged();
    }

    // *** SCHEDULER ***
    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
		if (events.isEmpty()) return false;
		AgentEvent event = events.remove(0); //pop first element
		
		//Simple finite state machine
		if (state == AgentState.DoingNothing) {
		    if (event == AgentEvent.gotHungry)	{
				goingToRestaurant();
				state = AgentState.WaitingInRestaurant;
				return true;
		    }
		    // elseif (event == xxx) {}
		}
		if (state == AgentState.WaitingInRestaurant) {
		    if (event == AgentEvent.beingSeated) {
				makeMenuChoice();
				state = AgentState.SeatedWithMenu;
				return true;
		    }
		    else if (event == AgentEvent.decidingAboutWaiting) {
		    	decideToWaitOrLeave();
		    	state = AgentState.WaitingInRestaurant;
		    	return true;
		    }
		    else if (event == AgentEvent.gotHungry)	{		// only happens if customer leaves and is manually set to Hungry again
				goingToRestaurant();
				state = AgentState.WaitingInRestaurant;
				return true;
		    }
		}
		if (state == AgentState.SeatedWithMenu) {
		    if (event == AgentEvent.decidedChoice)	{
				readyToOrder();
				state = AgentState.WaiterImReadyToOrder;
				return true;
		    }
		}
		if (state == AgentState.WaiterImReadyToOrder) {
		    if (event == AgentEvent.waiterToTakeOrder)	{
				orderFood();
				state = AgentState.WaitingForFood;
				return true;
		    }
		}
		if (state == AgentState.WaitingForFood) {
		    if (event == AgentEvent.foodDelivered) {
				eatFood();
				state = AgentState.Eating;
				return true;
		    }
		    else if(event == AgentEvent.waiterToTakeOrder) {
		    	reorderFood();
		    	state = AgentState.WaitingForFood;
		    	return true;
		    }
		}
		if (state == AgentState.Eating) {
		    if (event == AgentEvent.doneEating)	{
			    readyToPay();
			    state = AgentState.WaiterImReadyToPay;
				return true;
		    }
		}
		if (state == AgentState.WaiterImReadyToPay) {
		    if (event == AgentEvent.waiterToGiveBill) {
			    payCashierForFood();
			    state = AgentState.PayingForFood;
				return true;
		    }
		}
		if (state == AgentState.PayingForFood) {
		    if (event == AgentEvent.donePaying) {
			    leaveRestaurantAfterEating();
			    state = AgentState.DoingNothing;
				return true;
		    }
		}
		if (state == AgentState.PayingForFood) {
		    if (event == AgentEvent.washDishes) {
			    washDishesAndLeave();
			    state = AgentState.DoingNothing;
				return true;
		    }
		}
	
		print("No scheduler rule fired, should not happen in FSM, event="+event+" state="+state);
		return false;
    }
    
    // *** ACTIONS ***
    
    private void washDishesAndLeave() {
    	print("Washing dishes as punishment (" + WASH_DISHES_TIME + " ms)");
    	timer.schedule(new TimerTask() {
		    public void run() {
		    	print("Finished washing dishes.");
		    	leaveRestaurantNonNormative();
		    }
		}, WASH_DISHES_TIME);
	}

	private void decideToWaitOrLeave() {
    	if((int)(Math.random() * 10) > 2) {	// chance that customer will wait; this code is currently hardcoded but will be improved later
    		// Customer will wait
    		print("I will wait.");
    		host.msgIWillWait(this);
    	}
    	else {
    		// Customer will not wait; customer will leave the restaurant
    		print("I will not wait. I am leaving.");
    		host.msgIWillNotWait(this);
    		leaveRestaurantNonNormative();
    	}
	}

    /** Goes to the restaurant when the customer becomes hungry */
    private void goingToRestaurant() {
    	print("Going to restaurant.");
    	money = Math.random()*(MAX_MONEY-MIN_MONEY) + MIN_MONEY;	// gives the customer a random amount of money 
    	guiCustomer.appearInWaitingQueue();
    	// Small chance that customer will leave because the food is too expensive
    	if ((int)(Math.random() * 10) == 0) {
    		print("The price of the food here is too damn high!");
    		leaveRestaurantNonNormative();
    	}
    	else {
    		host.msgIWantToEat(this);	//send him our instance, so he can respond to us
    	}
    	stateChanged();
    }
    
    /** Starts a timer to simulate the customer thinking about the menu */
    private void makeMenuChoice() {
		print("Deciding menu choice...(3000 milliseconds)");
		timer.schedule(new TimerTask() {
		    public void run() {  
		    	msgDecided();	    
		    }
		}, 3000);//how long to wait before running task
		stateChanged();
    }
    
    /** Tells waiter the customer is ready to order food. */
    private void readyToOrder() {
		print("I decided!");
		waiter.msgImReadyToOrder(this);
		stateChanged();
    }
    
    /** Tells waiter the customer is ready to pay for the food (finished eating) */
    private void readyToPay() {
		print("I am ready to pay.");
		waiter.msgImReadyToPay(this);
		stateChanged();
    }
    
    /** Picks a random choice from the menu and sends it to the waiter */
    private void orderFood() {
		choice = menu.getRandomItem();
		print("I will have " + choice.getName());
		waiter.msgHereIsMyChoice(this, choice);
		stateChanged();
    }
    
    /** Same as orderFood() but avoids previous choice */
    private void reorderFood() {
    	MenuItem newChoice = menu.getRandomItem();
		while(newChoice.equals(choice))					// Not the best way to do this, but it works and is temporary (eventually should use indicies and mod to select new item)
			newChoice = menu.getRandomItem();
		choice = newChoice;
		print("(Reorder) I will have " + choice.getName());
		waiter.msgHereIsMyChoice(this, choice);
		stateChanged();
    }

    /** Starts a timer to simulate eating */
    private void eatFood() {
		print("Eating for " + hungerLevel*1000 + " milliseconds.");
		timer.schedule(new TimerTask() {
		    public void run() {
		    	msgDoneEating();
		    }
		},
	    getHungerLevel() * 0); //how long to wait before running task
		// getHungerLevel() * 1000); change back to this in final version
		stateChanged();
    }
    
    /** When the customer is done eating, he calculates the bill and attempts to pay the cashier */
    private void payCashierForFood() {
    	// Semi-hack to calculate cost of food
    	print("I have $" + cash(money));
    	bill = choice.getPrice();				// this is for ONE order only, code must be changed if accomodating multiple orders
    	if(money >= bill) {
	    	double payment = ((int)bill / 5 + 1) * 5;		// truncates bill to nearest int, uses integer division to divide by 5, adds 1, multiplies by 5
	    													// this effectively makes the customer pay for the food in bills of 5	    	
	    	if(money >= payment) {
		    	print("Paying the cashier $" + cash(payment) + " for a bill costing $" + bill);
		    	money -= payment;
	    	}
	    	else {
	    		// Pay with ALL the money. This still needs to be fixed up more later on for realism.
	    		// Customer shouldn't pay with coins if it's unnecessary to do so
	    		// But that takes some extra calculations which are low priority for v4.1...
	    		payment = money;
	    		money -= payment;
	    	}
	    	print("Now I have $" + cash(money) + " left.");
	    	cashier.msgPayForFood(this, bill, payment);
    	}
		else {
			print("Not enough money!");
			cashier.msgIDoNotHaveEnoughMoney(this, bill, 0.00);
	    }
		stateChanged();
    }

    /** When the customer is done paying for food, he leaves the restaurant */
    private void leaveRestaurantAfterEating() {
		print("Leaving the restaurant.");
		
		guiCustomer.leave(); //for the animation
		waiter.msgDoneEatingAndLeaving(this);
		isHungry = false;
		stateChanged();
		gui.setCustomerEnabled(this); //Message to gui to enable hunger button
	
		//hack to keep customer getting hungry. Only for non-gui customers
		if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
    }
    
    /** 1. The customer does not want to wait to be seated and leaves, or
     * 2. The customer thinks the food is too expensive, or
     * 3. The customer could not pay for food and just finished his punishment */
    private void leaveRestaurantNonNormative() {
		print("Leaving the restaurant.");
		guiCustomer.leave(); //for the animation
		isHungry = false;
		stateChanged();
		gui.setCustomerEnabled(this); //Message to gui to enable hunger button
	
		//hack to keep customer getting hungry. Only for non-gui customers
		if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
    }
    
    /** This starts a timer so the customer will become hungry again.
     * This is a hack that is used when the GUI is not being used */
    private void becomeHungryInAWhile() {
		timer.schedule(new TimerTask() {
		    public void run() {  
			setHungry();		    
		    }},
		    15000);//how long to wait before running task
	    }

    // *** EXTRA ***
	
    /** establish connection to host agent. 
     * @param host reference to the host */
    public void setHost(HostAgent host) {
		this.host = host;
    }
    
	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
    
    /** Returns the customer's name
     *@return name of customer */
    public String getName() {
    	return name;
    }

    /** @return true if the customer is hungry, false otherwise.
     ** Customer is hungry from time he is created (or button is
     ** pushed, until he eats and leaves.*/
    public boolean isHungry() {
    	return isHungry;
    }

    /** @return the hungerlevel of the customer */
    public int getHungerLevel() {
    	return hungerLevel;
    }
    
    /** Sets the customer's hungerlevel to a new value
     * @param hungerLevel the new hungerlevel for the customer */
    public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel; 
	}
	
    public GuiCustomer getGuiCustomer() {
	    	return guiCustomer;
    }
    
    /** @return the string representation of the class */
    public String toString() {
    	return "customer " + getName();
    }
    
    /** Sets ID of the customer. This is currently unused (16 Feb 2013, 7:06 PM). */
    public void setUID(int UID)
    {
    	this.UID = UID;
    }
    
    /** Gets ID of the customer. This is currently unused (16 Feb 2013, 7:06 PM). */
    public int getUID()
    {
    	return this.UID;
    }

    /** Used to set the customer's money, such as making him poor (have $0) to show non-normatives */
	public void setMoney(int money) {
		this.money = money;
		print("I have been forced via GUI to now have $" + money);
	}
}

