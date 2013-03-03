package restaurant.interfaces;

import restaurant.CashierAgent;

public interface Market {
	
	public abstract void msgPayForOrder(int productIndex, double payment);
	
	public abstract void msgRequestOrder(CashierAgent cashier, int productIndex, int quantity);
}
