package k1Sokoban;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class Teleport extends Actor {
	public Teleport(int x, int y) {
		super(x, y);
		
		//TODO Set the image of the Teleporter
		URL loc = this.getClass().getResource("/res/area.png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
	}
}
