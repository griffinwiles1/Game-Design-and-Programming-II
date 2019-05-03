package k1Sokoban;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class Baggage extends Actor {
	int teleportable = 0;
	
    public Baggage(int x, int y) {
        super(x, y);
        
        //Set the image of the Bag
        URL loc = this.getClass().getResource("/res/baggage.png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }

    public void move(int x, int y) {
        int nx = this.x() + x;
        int ny = this.y() + y;
        this.setX(nx);
        this.setY(ny);
        
        //Make sure the bag has moved 3 times before it teleports again
        if (teleportable > 0) {
        	teleportable--;
        }
    }
    
    public void teleport(int x, int y) {
	    //Make sure the bag isn't stuck in a teleport loop
    	//The bag has to move 3 times before it can teleport again
    	if (teleportable == 0) {
    		//Move the bag to the other teleport location
    		this.setX(x);
	    	this.setY(y);
	    	
	    	teleportable = 3;
	    }
    }
}