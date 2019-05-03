package k1Sokoban;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

import sf.Sound;
import sf.SoundFactory;

public class Player extends Actor {
	//Keep track of how many times the Player has moved in this level
	public Integer moves = 0;
	
	//TODO Make this entered by the user
	public String name = "AAA";
	//TODO Make this saved in a text file to store for later use
	public Integer highScore = 99;

	//Sound for the Player's movement
    public final static String SOUND_MOVE = "res/move.wav";
    
    public Player(int x, int y) {
        super(x, y);

        //Set the image of the Player
        URL loc = this.getClass().getResource("/res/sokoban.png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }

    //Move the Player
    public void move(int x, int y) {
        int nx = this.x() + x;
        int ny = this.y() + y;
        this.setX(nx);
        this.setY(ny);
        
        //Keep track of the number of moves
        this.moves++;
        
        //Make a sound when you move
        Sound sound = SoundFactory.getInstance(SOUND_MOVE);
        SoundFactory.play(sound);
    }
}