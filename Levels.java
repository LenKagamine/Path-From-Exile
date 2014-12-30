import java.util.ArrayList;
import java.awt.Rectangle;
public abstract class Levels{
    protected TileMap tileMap;
    protected Background bg;
    
    protected Player player;
    protected boolean playerDead, playerWin;
    protected boolean stopInput = false;
    protected int playerCounter = 0;
    
    protected ArrayList enemies;
    protected ArrayList explosions;
    
    protected HUD hud;
    protected Door door;
    
    protected AudioPlayer bgm;
    
    protected Rectangle phase;
    
    public abstract void init();
    public abstract void update();
    public abstract void draw(java.awt.Graphics2D g);
    public abstract void keyPressed(int k);
    public abstract void keyReleased(int k);
    
    public void savePlayer(){
	PlayerSave.setHealth(player.getHealth());
	PlayerSave.setMana(player.getMana());
    }
    public void loadSavePlayer(){
	player.setHealth(PlayerSave.getHealth());
	player.setMana(PlayerSave.getMana());
    }
}
