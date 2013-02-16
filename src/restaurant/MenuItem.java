package restaurant;

/** Represents an item on the menu (i.e. salad) */
public class MenuItem {
	private String name;
	private double price;
	//private int eatTime;
	
	public MenuItem(String name, double price)
	{
		this.name = name;
		this.price = price;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getPrice()
	{
		return price;
	}
}
