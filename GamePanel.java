import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    //Levels
    private static Levels[] levels;
    private static int currentLevel;
    //Width 320, Height 240, scaled by 2
    public static final int WIDTH = 320,HEIGHT = 240,SCALE = 2,numLevels = 5;
    
    //Thread
    private Thread thread;
    private boolean running;
    private int FPS = 60;
    private long targetTime = 1000/FPS;
    //Image/Drawing
    private BufferedImage image;
    private Graphics2D g;
    
    public GamePanel(){
	super();
	setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE)); //Set size
	setFocusable(true); //Allow focus
	requestFocus(); //Set focus
    }
    
    public void addNotify(){
	super.addNotify();
	if(thread == null){
	    thread = new Thread(this); //Create the thread if not created before
	    addKeyListener(this); //Start listening
	    thread.start(); //Start thread
	}
    }
    
    public void init(){ //Initialize buffered image and states
	image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	g = (Graphics2D) image.getGraphics();
	running = true;
	
	//Levels
	levels = new Levels[numLevels];
	currentLevel = 0;
	loadLevel(currentLevel);
    }
    private static void loadLevel(int level){
	if(level == 0) levels[level] = new LevelMenu();
	else if(level == 1) levels[level] = new Level1();
	else if(level == 2) levels[level] = new Level2();
	else if(level == 3) levels[level] = new Level3();
	else if(level == 4) levels[level] = new Level4();
    }
    private static void unloadLevel(int level){
	levels[level] = null;
    }
    
    public void run(){ //Main game loop
	init();
	
	long start,elapsed,wait; //Time keeping for FPS
	while(running){
	    start = System.currentTimeMillis();
	    
	    update();
	    draw();
	    drawToScreen();
	    
	    elapsed = System.currentTimeMillis()-start;
	    
	    wait = targetTime - elapsed;
	    
	    if(wait < 0) wait = 0; //Negative wait time
	    try{
		Thread.sleep(wait); //Wait for FPS
	    }
	    catch(Exception e){
		e.printStackTrace();
	    }
	}
    }
    
    public void update(){//Update game
	if(levels[currentLevel] != null) ((Levels)levels[currentLevel]).update();
    }
    public void draw(){//Draw game
	if(levels[currentLevel] != null) ((Levels)levels[currentLevel]).draw(g);
    }
    public void drawToScreen(){ //Draw buffered image to screen
	Graphics g2 = getGraphics();
	g2.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
	g2.dispose();
    }
    
    public void keyTyped(KeyEvent key){} //Not needed; here for KeyListener
    public void keyPressed(KeyEvent key){ //Key pressed
	try{
	    int k = key.getKeyCode();
	    ((Levels)levels[currentLevel]).keyPressed(k);
	}catch(Exception e){}
    }
    public void keyReleased(KeyEvent key){ //Key released
	try{
	    int k = key.getKeyCode();
	    ((Levels)levels[currentLevel]).keyReleased(k);
	}catch(Exception e){}
    }
    
    public static void setLevel(int level){ //Change level
	unloadLevel(currentLevel);
	currentLevel = level;
	loadLevel(currentLevel);
    }
}
