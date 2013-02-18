package restaurant;

/** Represents an item on the menu (i.e. salad) */
public class MenuItem {
	private String name;
	private double price;
	private int cookTime;
	//private String type;
	//private int eatTime;
	
	public MenuItem(String name, double price, int cookTime)
	{
		this.name = name;
		this.price = price;
		this.cookTime = cookTime;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public int getCookTime()
	{
		return cookTime;
	}
}
