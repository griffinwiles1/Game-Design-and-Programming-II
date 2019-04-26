package k1Sokoban;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

import sf.Sound;
import sf.SoundFactory;

public class Player extends Actor {
	public Integer moves = 0;
	public String name = "AAA";
	public Integer highScore = 99;

    public final static String SOUND_MOVE = "res/move.wav";
    public Player(int x, int y) {
        super(x, y);

        URL loc = this.getClass().getResource("/res/sokoban.png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }

    public void move(int x, int y) {
        int nx = this.x() + x;
        int ny = this.y() + y;
        this.setX(nx);
        this.setY(ny);
        this.moves++;
        
        //Make a sound when you move
        Sound sound = SoundFactory.getInstance(SOUND_MOVE);
        SoundFactory.play(sound);
    }
}