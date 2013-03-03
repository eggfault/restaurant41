package restaurant.interfaces;

import restaurant.CashierAgent;

public interface Market {
	
	/** Returns the name of the market */
	public abstract String getName();
	
	/** Cashier sends this when he places an order */
	public abstract void msgPayForOrder(int productIndex, double payment);
	
	/** Cashier sends this when he pays for an order after receiving the invoice */
	public abstract void msgRequestOrder(CashierAgent cashier, int productIndex, int quantity);
	
}
