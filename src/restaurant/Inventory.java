package restaurant;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
	private Map<String,Stock> contents;
	
	/** A stock contains a MenuItem and its quantity */
	private class Stock {
		public MenuItem product;
		public int quantity;
		
		public Stock(MenuItem product, int quantity) {
			this.product = product;
			this.quantity = quantity;
		}
	}
	
	public Inventory(Menu menu, int MIN_ITEM_QUANTITY, int MAX_ITEM_QUANTITY)
	{
		contents = new HashMap<String,Stock>();
		for(int i = 0; i < menu.getLength(); i ++)
		{
			String itemName = menu.itemAtIndex(i).getName();
			double itemPrice = menu.itemAtIndex(i).getPrice();
			int itemQuantity = (int)(Math.random()*(MAX_ITEM_QUANTITY-MIN_ITEM_QUANTITY))+MIN_ITEM_QUANTITY;
			int itemCookTime = menu.itemAtIndex(i).getCookTime();
			contents.put(itemName, new Stock(new MenuItem(itemName, itemPrice, itemCookTime),itemQuantity));
		}
	}

	public MenuItem getProduct(String name) {
		return contents.get(name).product;
	}
	
	public int getQuantity(String name) {
		return contents.get(name).quantity;
	}
	
	/** Removes specified amount from quantity of item */
	public void subtractFromQuantity(String name, int amount) {
		contents.get(name).quantity -= amount;
	}
	
	/** Adds specified amount to quantity of item */
	public void addToQuantity(String name, int amount) {
		contents.get(name).quantity += amount;
	}
	
	public int size() {
		return contents.size();
	}
}
