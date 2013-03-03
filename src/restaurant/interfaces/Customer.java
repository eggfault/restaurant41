package restaurant.interfaces;

public interface Customer {
	
	/** Returns the name of the customer */
	public abstract String getName();
	
    /** Cashier sends this to give change back to the customer after he has paid for food */
    public abstract void msgHereIsYourChange(double change);
    
    /** Cashier sends this if customer cannot not pay him */
	public abstract void msgGoWashDishes();
	
}