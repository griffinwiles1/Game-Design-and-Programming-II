package k1Sokoban;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sf.Sound;
import sf.SoundFactory;

//TODO Test on other computers/Figure out why it is acting weird on them

@SuppressWarnings({"serial","rawtypes", "unchecked"})
public class Board extends JPanel implements ActionListener { 

    private final int OFFSET = 0;
    private final int SPACE = 32;
    private final int LEFT_COLLISION = 1;
    private final int RIGHT_COLLISION = 2;
    private final int TOP_COLLISION = 3;
    private final int BOTTOM_COLLISION = 4;

    private ArrayList<Wall> walls = new ArrayList();
    private ArrayList<Baggage> baggs = new ArrayList();
    private ArrayList<Area> areas = new ArrayList();
    private Teleport[] teleports = new Teleport[2];
    
    private Player soko;
    private int w = 0;
    private int h = 0;
    private boolean completed = false;
    private boolean inGame = false;
    
    private String userName = "AAA";
    private boolean hasUserName = false;
    
    Timer timer;
    int time = 0;
    
    //Sounds
    public final static String DIR = "res/";
    
    public final static String SOUND_START = DIR + "start.wav";
    public final static String SOUND_GOAL = DIR + "goal.wav";
    public final static String SOUND_COMPLETE = DIR + "complete.wav";
    
    //Create the various levels
    //  # = Wall
    //  $ = Baggage
    //  . = Goal Area (Requires same number as Baggage)
    //  @ = Player
    //  & = Teleport1
    //  * = Teleport2
    // \n = New Row
    private String level1 =
    		  "                      \n"
    		+ "     #############    \n"
    		+ "    ##      #@#  #    \n"
    		+ "    # $       $  #    \n"
    		+ "    #     $      #    \n"
    		+ "    #     #### ###    \n"
    		+ "    #### ##      #    \n"
    		+ "    #    #       #    \n"
    		+ "    #     $      #    \n"
    		+ "    # ## #######.#    \n"
    		+ "    #.#.  #   .###    \n"
    		+ "    ###   #  ### #    \n"
    		+ "      #          #    \n"
    		+ "      ####    ####    \n"
    		+ "         ######       \n"
    		+ "                      \n";
    
    private String level2 = 
  	          "                      \n"
			+ "                      \n"
			+ "                      \n"
    	    + "      #####           \n"
    	    + "      #   #           \n"
            + "      #$  #           \n"
            + "    ###  $##          \n"
            + "    #  $ $ #          \n"
            + "  ### # ## #   ###### \n"
            + "  #   # ## #####  ..# \n"
            + "  # $  $          ..# \n"
            + "  ##### ### #@##  ..# \n"
            + "      #     ######### \n"
            + "      #######         \n"
            + "                      \n"
            + "                      \n";
    
    private String level3 = 
    		  " ##### ############   \n"
    		+ " #   # #          ##  \n"
    	    + " #  $# # #   $ #   ## \n"
    		+ " #   ### #     #    # \n"
    	    + " #       #          # \n"
    		+ " #  ########   #.#$## \n"
    	    + " ##    ###     ### #  \n"
    		+ "  #      ##        #  \n"
    	    + "  #        #     #### \n"
    		+ " ## ## ##    #      # \n"
    	    + " #   # ## #         # \n"
    		+ " #   # ## #  ## ### # \n"
    	    + " ###.#.#  ## #    $ # \n"
    		+ "   #####          # # \n"
    	    + "   #.     ##### ###@# \n"
    		+ "   ########   ### ### \n";

    //Can modify the order of the levels here
    String[] levels = {level1, level2, level3};
    int currentLevel = 0;
    String level = levels[currentLevel];

    
    public Board() {
        addKeyListener(new TAdapter());
        
        //Every second update the timer
        timer = new Timer(1000, (ActionListener) this);
        timer.start();
        
        setFocusable(true);
        initWorld();
    }

    
    public int getBoardWidth() {
        return this.w;
    }

    
    public int getBoardHeight() {
        return this.h;
    }
    
    
    public void GetUserInfo() {
        userName = JOptionPane.showInputDialog("Enter your name:", "AAA");
        repaint();
    }
    
    
    //Shown at the start of each new level
    public void ShowLevelScreen(Graphics2D g2d) {        
    	repaint();
    	if (!hasUserName) {
    		hasUserName = true;
    		GetUserInfo();
    	}
    	
    	//Set the background to Gray
    	g2d.setBackground(Color.GRAY);
    	
    	//Declare font sizes + metrics
    	Font large = new Font("Helvetica", Font.BOLD, 36);
    	Font medium = new Font("Helvetica", Font.BOLD, 32);
    	Font small = new Font("Helvetica", Font.BOLD, 24);
    	
    	FontMetrics metr1 = this.getFontMetrics(large);
    	FontMetrics metr2 = this.getFontMetrics(medium);
        FontMetrics metr3 = this.getFontMetrics(small);
        
        String start = userName + ", press [space] to start and [Q] to quit";
    	
    	if (currentLevel == 0) {
	    	String sokoban = "SOKOBAN";
	    	
	        //Set the text position
	        g2d.setFont(large);
	        g2d.drawString(sokoban, (getBoardWidth() - metr1.stringWidth(sokoban)) / 2, 160);
	        g2d.setFont(small);
	        g2d.drawString(start, (getBoardWidth() - metr3.stringWidth(start)) / 2, getBoardHeight() / 2 + 60 );
    	} else {
    		String score = "Your Score: " + soko.highScore.toString();
    		
    		//Set the text position
    		g2d.setFont(medium);
    		g2d.drawString(score, (getBoardWidth() - metr2.stringWidth(score)) / 2, 160);
    		g2d.setFont(small);
	        g2d.drawString(start, (getBoardWidth() - metr3.stringWidth(start)) / 2, getBoardHeight() / 2 + 60 );
    	}
    }

    
    public final void initWorld() {
        int x = OFFSET;
        int y = OFFSET;
        
        Wall wall;
        Baggage b;
        Area a;
        Teleport t;
        
        //Reset the time to 0
        time = 0;

        //Loop through the level to see where everything is positioned
        for (int i = 0; i < level.length(); i++) {
        	
            char item = level.charAt(i);
            
            if (item == '\n') {
                y += SPACE;
                if (this.w < x) {
                    this.w = x;
                }
                x = OFFSET;
            } else if (item == '#') {
                wall = new Wall(x, y);
                walls.add(wall);
                x += SPACE;
            } else if (item == '$') {
                b = new Baggage(x, y);
                baggs.add(b);
                x += SPACE;
            } else if (item == '.') {
                a = new Area(x, y);
                areas.add(a);
                x += SPACE;
            } else if (item == '@') {
                soko = new Player(x, y);
                x += SPACE;
            } else if (item == '&') {
            	t = new Teleport(x, y);
            	teleports[0] = t;
            	x += SPACE;
        	} else if (item == '*') {
        		t = new Teleport(x, y);
        		teleports[1] = t;
        		x += SPACE;
            } else if (item == ' ') {
                x += SPACE;
            }
            
            h = y;
        }
    }
    
    
    public void buildWorld(Graphics g) {   
        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        ArrayList<Actor> world = new ArrayList();
        world.addAll(walls);
        world.addAll(areas);
        world.addAll(baggs);
        world.add(soko);
        
        soko.name = userName;
        
        //Draw all of the Actors into the world
        for (int i = 0; i < world.size(); i++) {
            
        	Actor item = world.get(i);
        	
//            if ((item instanceof Player) || (item instanceof Baggage)) {
//                g.drawImage(item.getImage(), item.x(), item.y(), this);
//            } else {
//                g.drawImage(item.getImage(), item.x(), item.y(), this);
//            }
        	
        	g.drawImage(item.getImage(), item.x(), item.y(), this);
            
        	//Set the font
            Font small = new Font("Helvetica", Font.BOLD, 18);
            g.setFont(small);
            g.setColor(new Color(0, 0, 0));;
            
            //Draw the user's name
            g.drawString(soko.name, 20, 24);
            
            //Draw the user's moves
            g.drawString("Moves: " + soko.moves.toString(), 20, 48);
            
            //Draw the time elapsed on the level
            g.drawString("Time: " + time, 620, 24);
            
            //TODO Get HighScore from the class
            //Have to do for name, time, and moves
            //Also have to set the high scores
            g.drawString("High Score: " + soko.highScore, 300, 24);
        }
    }

    
    @Override
    public void paint(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	super.paint(g2d);
        
    	//Used to show the screen at the start of each level
        if (inGame) {
        	buildWorld(g2d);
        } else {
        	ShowLevelScreen(g2d);
        	restartLevel();
        	
        }
               
    }

    
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
        	//If the level is complete, the player can't do anything
            if (completed) {
                return;
            }

            int key = e.getKeyCode();
 
            if (inGame) {
            	if (key == KeyEvent.VK_LEFT) {
                    if (checkWallCollision(soko,
                            LEFT_COLLISION)) {
                        return;
                    }

                    if (checkBagCollision(LEFT_COLLISION)) {
                        return;
                    }

                    soko.move(-SPACE, 0);

                } else if (key == KeyEvent.VK_RIGHT) {
                	
                    if (checkWallCollision(soko,
                            RIGHT_COLLISION)) {
                        return;
                    }

                    if (checkBagCollision(RIGHT_COLLISION)) {
                        return;
                    }

                    soko.move(SPACE, 0);

                } else if (key == KeyEvent.VK_UP) {

                    if (checkWallCollision(soko,
                            TOP_COLLISION)) {
                        return;
                    }

                    if (checkBagCollision(TOP_COLLISION)) {
                        return;
                    }

                    soko.move(0, -SPACE);

                } else if (key == KeyEvent.VK_DOWN) {

                    if (checkWallCollision(soko,
                            BOTTOM_COLLISION)) {
                        return;
                    }

                    if (checkBagCollision(BOTTOM_COLLISION)) {
                        return;
                    }

                    soko.move(0, SPACE);

                } else if (key == KeyEvent.VK_R) {
                    restartLevel();
                }
            }
            
            if (key == KeyEvent.VK_SPACE) {
            	Sound sound = SoundFactory.getInstance(SOUND_START);
            	SoundFactory.play(sound);
            	inGame = true;
            } else if (key == KeyEvent.VK_Q) {
            	System.exit(0);
            }
            
            repaint();
        }
    }

    
    private boolean checkWallCollision(Actor actor, int type) {

        if (type == LEFT_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = walls.get(i);
                if (actor.isLeftCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == RIGHT_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = walls.get(i);
                if (actor.isRightCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == TOP_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = walls.get(i);
                if (actor.isTopCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == BOTTOM_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = walls.get(i);
                if (actor.isBottomCollision(wall)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    
    private boolean checkBagCollision(int type) {

        if (type == LEFT_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = baggs.get(i);
                if (soko.isLeftCollision(bag)) {

                    for (int j=0; j < baggs.size(); j++) {
                        Baggage item = baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isLeftCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                LEFT_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(-SPACE, 0);
                    isCompleted();
                }
            }
            return false;

        } else if (type == RIGHT_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = baggs.get(i);
                if (soko.isRightCollision(bag)) {
                    for (int j=0; j < baggs.size(); j++) {

                        Baggage item = baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isRightCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                RIGHT_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(SPACE, 0);
                    isCompleted();                   
                }
            }
            return false;

        } else if (type == TOP_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = baggs.get(i);
                if (soko.isTopCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isTopCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                TOP_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(0, -SPACE);
                    isCompleted();
                }
            }

            return false;

        } else if (type == BOTTOM_COLLISION) {
        
            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = baggs.get(i);
                if (soko.isBottomCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isBottomCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                BOTTOM_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(0, SPACE);
                    isCompleted();
                }
            }
        }

        return false;
    }

    
    //Check if the level is completed
    public void isCompleted() {
    	
        int num = baggs.size();
        int bagsCompleted = 0;
        
        //Check to see if all of the bags are in place
        for (int i = 0; i < num; i++) {
            Baggage bag = baggs.get(i);
            for (int j = 0; j < num; j++) {
                Area area = areas.get(j);
                if (bag.x() == area.x()
                        && bag.y() == area.y()) {
                    bagsCompleted += 1;
                }
            }
        }

        if (bagsCompleted == num) {
            completed = true;            
            repaint();
            
            Sound sound = SoundFactory.getInstance(SOUND_GOAL);
            sound.play();
            
            restartLevel();
            
            //Used to avoid an ArrayOutOfBounds error
            //Modify depending on number of levels
            if (currentLevel < 3) {
            	currentLevel++;
                level = levels[currentLevel];	
            }
        }
    }

	//Restart the level
    public void restartLevel() {
    	
    	//If the level was competed move to the next level
    	//TODO get different screens for what is happening
    	//Start, Restart, Complete, etc.
    	
    	repaint();
    	//Clear all of the Actors from the level
        areas.clear();
        baggs.clear();
        walls.clear();
        
        initWorld();
        
        //Move to a new screen
        inGame = false;
        
        //Only if they complete the level
        if (completed) {
            completed = false;
        }
    }

    
    //Used as a timer in game
	@Override
	public void actionPerformed(ActionEvent event) {
		if(inGame) {
			time++;
		}
		//Required to have the timer update on screen
		repaint();
	}
}