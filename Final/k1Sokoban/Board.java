package k1Sokoban;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import sf.Sound;
import sf.SoundFactory;

@SuppressWarnings({"serial","rawtypes", "unchecked"})
public class Board extends JPanel { 

    private final int OFFSET = 0;
    private final int SPACE = 32;
    private final int LEFT_COLLISION = 1;
    private final int RIGHT_COLLISION = 2;
    private final int TOP_COLLISION = 3;
    private final int BOTTOM_COLLISION = 4;

    private ArrayList walls = new ArrayList();
    private ArrayList baggs = new ArrayList();
    private ArrayList areas = new ArrayList();
    private Player soko;
    private int w = 0;
    private int h = 0;
    private boolean completed = false;
    private boolean inGame = false;
    
    private int current = 0;
    
    //Sounds
    public final static String DIR = "res/";
    
    public final static String SOUND_START = DIR + "start.wav";
    public final static String SOUND_GOAL = DIR + "goal.wav";
    public final static String SOUND_COMPLETE = DIR + "complete.wav";
    
    private String level2 =
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
    
    private String level1 = 
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

    String[] levels = {level1, level2, level3};
    int currentLevel = 0;
    String level = levels[currentLevel];

    public Board() {

        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();
    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }
    
    public void ShowIntroScreen(Graphics2D g2d) {
    	g2d.setBackground(Color.GRAY);
    	
    	String sokoban = "SOKOBAN";
    	Font large = new Font("Helvetica", Font.BOLD, 36);
    	String start = "Press [space] to start and [Q] to quit";
    	Font small = new Font("Helvetica", Font.BOLD, 24);
    	FontMetrics metr1 = this.getFontMetrics(large);
        FontMetrics metr2 = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(large);
        g2d.drawString(sokoban, (getBoardWidth() - metr1.stringWidth(sokoban)) / 2, 160);
        g2d.setFont(small);
        g2d.drawString(start, (getBoardWidth() - metr2.stringWidth(start)) / 2, getBoardHeight() / 2 + 60 );
        
        //Utilize a JOptionPane
    }

    public final void initWorld() {
        
        int x = OFFSET;
        int y = OFFSET;
        
        Wall wall;
        Baggage b;
        Area a;


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
            } else if (item == ' ') {
                x += SPACE;
            }

            h = y;
        }
    }

    public void buildWorld(Graphics g) {

        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Graphics2D g2d = (Graphics2D) g;

        ArrayList world = new ArrayList();
        world.addAll(walls);
        world.addAll(areas);
        world.addAll(baggs);
        world.add(soko);

        for (int i = 0; i < world.size(); i++) {

            Actor item = (Actor) world.get(i);

            if ((item instanceof Player) || (item instanceof Baggage)) {
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            } else {
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            }

            if (completed) {
                g.setColor(new Color(0, 0, 0));
                g.drawString("Completed", 25, 20);
            }

            Font small = new Font("Helvetica", Font.BOLD, 18);
            g.setFont(small);
            g.setColor(new Color(0, 0, 0));;
            g.drawString(soko.name, 20, 30);
            g.drawString(soko.highScore.toString(), 64, 30);
            g.drawString("Moves: " + soko.moves.toString(), 20, 50);
            g2d.setBackground(Color.GRAY);
        }
    }

    @Override
    public void paint(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	super.paint(g2d);
        
        if (inGame) {
        	buildWorld(g2d);
        } else {
        	ShowIntroScreen(g2d);
        	restartLevel();
        	
        }
               
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

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
            
            if (key == KeyEvent.VK_SPACE && inGame == false) {
            	Sound sound = SoundFactory.getInstance(SOUND_START);
            	SoundFactory.play(sound);
            	inGame = true;
            } else if (key == KeyEvent.VK_Q) {
            	restartLevel();
            }
            
            repaint();
        }
    }

    private boolean checkWallCollision(Actor actor, int type) {

        if (type == LEFT_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
                if (actor.isLeftCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == RIGHT_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
                if (actor.isRightCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == TOP_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
                if (actor.isTopCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == BOTTOM_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
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

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isLeftCollision(bag)) {

                    for (int j=0; j < baggs.size(); j++) {
                        Baggage item = (Baggage) baggs.get(j);
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

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isRightCollision(bag)) {
                    for (int j=0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
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

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isTopCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
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

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isBottomCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
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

    public void isCompleted() {

        int num = baggs.size();
        int compl = 0;
        

        for (int i = 0; i < num; i++) {
            Baggage bag = (Baggage) baggs.get(i);
            for (int j = 0; j < num; j++) {
                Area area = (Area) areas.get(j);
                if (bag.x() == area.x()
                        && bag.y() == area.y()) {
                    compl += 1;
                    if(current < compl) {
                    	Sound sound = SoundFactory.getInstance(SOUND_GOAL);
                        SoundFactory.play(sound);
                    	current++;
                    }
                }
            }
        }

        if (compl == num) {
            completed = true;            
            repaint();            
            restartLevel();
            
            Sound sound = SoundFactory.getInstance(SOUND_COMPLETE);
            SoundFactory.play(sound);
            if (currentLevel < 3) {
            	currentLevel++;
                level = levels[currentLevel];	
            }
        }
    }

    public void restartLevel() {

        areas.clear();
        baggs.clear();
        walls.clear();
        initWorld();
        inGame = false;
        current = 0;
        if (completed) {
            completed = false;
        }
    }
}