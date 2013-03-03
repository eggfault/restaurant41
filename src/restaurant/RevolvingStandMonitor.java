package restaurant;

import java.util.Vector;

public class RevolvingStandMonitor extends Object {
    private final int MAX_SIZE = 5;
    private int count = 0;
    private Vector<MenuItem> contents;
    
    public RevolvingStandMonitor() {
    	contents = new Vector<MenuItem>();
    }
    
    synchronized public void insert(MenuItem data) {
        while (count == MAX_SIZE) {
            try { 
                print("Full, waiting");
                wait(5000);                         // Full, wait to add
            } catch (InterruptedException ex) {};
        }
            
        insertItem(data);
        count++;
        if(count == 1) {
        	print("Not Empty, notify");
            notify();								// Not full, notify a
            										// waiting consumer
        }
    }
    
    synchronized public MenuItem remove() {
    	MenuItem data;
        while(count == 0)
            try { 
                print("Empty, waiting");
                wait(5000);                         // Empty, wait to consume
            } catch (InterruptedException ex) {};

        data = removeItem();
        count--;
        if(count == MAX_SIZE-1){ 
            print("Not full, notify");
            notify();                               // Not full, notify a 
                                                    // waiting producer
        }
        return data;
    }
    
    private void insertItem(MenuItem data) {
    	contents.addElement(data);
    }
    
    private MenuItem removeItem() {
    	MenuItem data = (MenuItem)contents.firstElement();
    	contents.removeElementAt(0);
        return data;
    }
    
    private void print(String s) {
    	System.out.println("RevolvingStand: " + s);
    }
}