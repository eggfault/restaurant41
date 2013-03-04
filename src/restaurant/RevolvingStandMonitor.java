package restaurant;

import java.util.Vector;

public class RevolvingStandMonitor extends Object {
    private final int MAX_SIZE = 3;
    private int count = 0;
    private Vector<FoodOrder> contents;
    
    public RevolvingStandMonitor() {
    	contents = new Vector<FoodOrder>();
    }
    
    synchronized public void insert(FoodOrder data) {
        while(count == MAX_SIZE) {
            try { 
                print("Full, waiting...");
                wait(5000);                         // Full, wait to add
            } catch (InterruptedException ex) {};
        }
            
        insertItem(data);
        count++;
        if(count == 1) {
        	print("Not empty, notify!");
            notify();								// Not full, notify a
            										// waiting consumer
        }
    }
    
    synchronized public FoodOrder remove() {
    	FoodOrder data;
//        while(count == 0)
//            try { 
//                print("Empty, waiting...");
//                wait(5000);                         // Empty, wait to consume
//            } catch (InterruptedException ex) {};
    	if(count == 0)
    		return null;
        data = removeItem();
        count--;
        if(count == MAX_SIZE-1){ 
            print("Not full, notify!");
            notify();                               // Not full, notify a 
                                                    // waiting producer
        }
        return data;
    }
    
    private void insertItem(FoodOrder data) {
    	contents.addElement(data);
    }
    
    private FoodOrder removeItem() {
    	FoodOrder data = (FoodOrder)contents.firstElement();
    	contents.removeElementAt(0);
        return data;
    }
    
    private void print(String s) {
    	System.out.println("RevolvingStand: " + s);
    }
}