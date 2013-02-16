package restaurant;


public class Menu {
    private MenuItem choices[] = new MenuItem[]
	{
		new MenuItem("Steak", 15.99),
		new MenuItem("Chicken", 10.99), 
		new MenuItem("Salad", 5.99),
		new MenuItem("Pizza", 8.99),
    };
    
    public MenuItem getRandomItem()
    {
    	return choices[(int)(Math.random()*choices.length)];
    }
    
    /** Returns how many items are on the menu */
    public int getLength()
    {
    	return choices.length;
    }
    
    public MenuItem itemAtIndex(int index)
    {
    	return choices[index];
    }
}
    
