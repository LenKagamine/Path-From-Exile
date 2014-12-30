import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Level3 extends Levels{
    public Level3(){
	init();
    }
    public void init(){
	tileMap = new TileMap(30);
	tileMap.loadTiles("/Resources/Tilesets/tilemap.png");
	tileMap.loadMap("/Resources/Maps/level3.map");
	tileMap.setPosition(0,0);
	tileMap.setSmooth(0.1);
	
	bg = new Background("/Resources/Backgrounds/bgnight.gif",0.1,true,false);
	
	player = new Player(tileMap,"/Resources/Sprites/Player/Archer2.png");
	loadSavePlayer();
	player.setPosition(120,90);
	
	spawnEnemies();
	
	explosions = new ArrayList();
	
	hud = new HUD(player);
	
	door = new Door(tileMap,"/Resources/Sprites/doorSmall.png");
	door.setPosition(195,1237);
	
	phase = new Rectangle(0,0,0,0);
	
	bgm = new AudioPlayer("level3.mp3");
	bgm.loop();
    }
    
    private void spawnEnemies(){
	enemies = new ArrayList();
	Slime s;
	Point[] points = new Point[]{
	    new Point(120,675),
	    new Point(140,680),
	    new Point(160,688),
	    new Point(180,685),
	    new Point(200,680),
	    new Point(220,673),
	    new Point(70,820),
	    new Point(90,820),
	    new Point(110,830),
	    new Point(130,830),
	    new Point(150,830),
	    new Point(240,830),
	    new Point(300,950),
	    new Point(150,950),
	    new Point(175,1100)
	};
	for(int i=0;i<points.length;i++){
	    s = new Slime(tileMap);
	    s.setPosition(points[i].x, points[i].y);
	    enemies.add(s);
	}
    }
    
    public void update(){
	if(door.contains(player)){
	    playerWin = stopInput = true;
	}
	if(player.getHealth() == 0 || player.getY() > tileMap.getHeight()){
	    playerDead = stopInput = true;
	}
	
	if(playerWin) playerWin();
	if(playerDead) playerDead();
	player.update();
	tileMap.setPosition(GamePanel.WIDTH / 2 - player.getX(),GamePanel.HEIGHT / 2 - player.getY());
	//Set Background
	bg.setPosition(tileMap.getX(), tileMap.getY());
	//Attack enemies
	player.checkAttack(enemies);
	//Update Enemies
	for(int i=0;i<enemies.size();i++){
	    Enemy e = (Enemy)enemies.get(i);
	    e.update();
	    if(e.isDead()){
		enemies.remove(i);
		i--;
		explosions.add(new Explosion(e.getX(),e.getY()));
	    }
	}
	//Update explosions
	for(int i=0;i<explosions.size();i++){
	    ((Explosion)explosions.get(i)).update();
	    if(((Explosion)explosions.get(i)).shouldRemove()){
		explosions.remove(i);
		i--;
	    }
	}
	//Update door
	door.update();
    }
    public void draw(Graphics2D g){
	//Draw background
	bg.draw(g);
	//Draw map
	tileMap.draw(g);
	//Draw door
	door.draw(g);
	//Draw player
	player.draw(g);
	//Draw enemies
	for(int i=0;i<enemies.size();i++){
	    ((Enemy)enemies.get(i)).draw(g);
	}
	//Draw explosions
	for(int i=0;i<explosions.size();i++){
	    ((Explosion)explosions.get(i)).setMapPosition((int)tileMap.getX(),(int)tileMap.getY());
	    ((Explosion)explosions.get(i)).draw(g);
	}
	//Draw HUD
	hud.draw(g);
	//Draw phases
	g.setColor(Color.black);
	g.fill(phase);
    }
    public void keyPressed(int k){
	if(stopInput) return;
	if(k == KeyEvent.VK_LEFT) player.setLeft(true);
	if(k == KeyEvent.VK_RIGHT) player.setRight(true);
	if(k == KeyEvent.VK_S) player.setUp(true);
	if(k == KeyEvent.VK_DOWN) player.setDown(true);
	if(k == KeyEvent.VK_UP) player.setJumping(true);
	if(k == KeyEvent.VK_Z) player.setFiring();
	if(k == KeyEvent.VK_X) player.setStabbing();
	if(k == KeyEvent.VK_C) player.setSwinging();
	if(k == KeyEvent.VK_V) player.setGliding(true);
    }
    public void keyReleased(int k){
	if(stopInput) return;
	if(k == KeyEvent.VK_LEFT) player.setLeft(false);
	if(k == KeyEvent.VK_RIGHT) player.setRight(false);
	if(k == KeyEvent.VK_S) player.setUp(false);
	if(k == KeyEvent.VK_DOWN) player.setDown(false);
	if(k == KeyEvent.VK_UP) player.setJumping(false);
	if(k == KeyEvent.VK_V) player.setGliding(false);
    }
    
    public void playerDead(){
	playerCounter++;
	if(playerCounter == 1){
	    player.setDead();
	}
	if(playerCounter == 60){
	    phase = new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0);
	}
	else if(playerCounter > 60){
	    phase.x -= 6;
	    phase.y -= 4;
	    phase.width += 12;
	    phase.height += 8;
	}
	if(playerCounter >= 120){
	    playerDead = stopInput = false;
	    playerCounter = 0;
	    reset();
	}
    }
    public void reset(){
	player.reset();
	player.setPosition(100,100);
	spawnEnemies();
	stopInput = false;
	phase = new Rectangle(0,0,0,0);
    }
    public void playerWin(){
	playerCounter++;
	bgm.stop();
	if(playerCounter == 1){
	    player.stop();
	}
	if(playerCounter == 60){
	    phase = new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0);
	}
	else if(playerCounter > 60){
	    phase.x -= 6;
	    phase.y -= 4;
	    phase.width += 12;
	    phase.height += 8;
	}
	if(playerCounter >= 120){
	    savePlayer();
	    GamePanel.setLevel(4);
	    playerCounter = 0;
	    stopInput = false;
	}
    }
}
