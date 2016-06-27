import java.awt.Rectangle;
import java.awt.Graphics2D;

public abstract class MapObject{
	//TileMap
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap,ymap; //xmap,ymap are location of screen on TileMap
	//Position and Speed
	protected double x,y,dx,dy; //x,y are location of the player on the whole TileMap
	//Dimensions
	protected int width,height;
	//Collision Box
	protected int cwidth,cheight;
	//More Collision
	protected int currRow,currCol;
	protected double xdest,ydest;
	protected double xtemp,ytemp;
	protected boolean topLeft,topRight,bottomLeft,bottomRight; //Normal collision
	protected boolean topLeftD,topRightD,bottomLeftD,bottomRightD; //Damage collision
	//Animation
	protected Animation animation;
	protected int currentAction, previousAction;
	protected boolean facingRight;
	//Movement
	protected boolean left,right,up,down;
	protected boolean jumping,falling;
	//Movement attributes
	protected double moveSpeed,maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed,maxFallSpeed;
	protected double jumpStart, stopJumpSpeed;
	//Health and Damage
	protected int health,maxHealth;
	protected boolean dead;
	protected boolean flinching;
	protected long flinchTimer;

	public MapObject(TileMap tm){
		tileMap = tm;
		tileSize = tm.getTileSize();
	}

	public boolean intersects(MapObject o){
		Rectangle r1 = getRect();
		Rectangle r2 = o.getRect();
		return r1.intersects(r2);
	}
	
	public boolean contains(MapObject o){
		Rectangle r1 = getRect();
		Rectangle r2 = o.getRect();
		return r1.contains(r2);
	}

	public Rectangle getRect(){
		return new Rectangle((int)(x-cwidth/2),(int)(y-cheight/2),cwidth,cheight);
	}

	public void hit(int damage){
		if(flinching) return; //Invinsible during flinching to prevent juggling
		health -= damage;
		if(health < 0) health = 0; //No negative health
		if(health == 0) dead = true;
		flinching = true;
		flinchTimer = System.currentTimeMillis(); //Start timer
	}
	
	public void calculateCorners(double x,double y){
		int leftTile = (int)(x - cwidth / 2) / tileSize;
		int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cheight / 2) / tileSize;
		int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;

		topLeft = tileMap.getType(topTile,leftTile) == Tile.BLOCKED;
		topRight = tileMap.getType(topTile,rightTile) == Tile.BLOCKED;
		bottomLeft = tileMap.getType(bottomTile,leftTile) == Tile.BLOCKED;
		bottomRight = tileMap.getType(bottomTile,rightTile) == Tile.BLOCKED;

		topLeftD = tileMap.getType(topTile,leftTile) == Tile.DANGER;
		topRightD = tileMap.getType(topTile,rightTile) == Tile.DANGER;
		bottomLeftD = tileMap.getType(bottomTile,leftTile) == Tile.DANGER;
		bottomRightD = tileMap.getType(bottomTile,rightTile) == Tile.DANGER;
	}

	public void checkTileMapCollision(){
		currCol = (int)x / tileSize;
		currRow = (int)y / tileSize;

		xdest = x + dx;
		ydest = y + dy;

		xtemp = x;
		ytemp = y;

		calculateCorners(x,ydest);
		if(dy < 0){ //Up direction
			if(topLeft || topRight){
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			}
			else{
				ytemp += dy;
			}
		}
		if(dy > 0){ //Down
			if(bottomLeft || bottomRight){
				dy = 0;
				falling = false;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
			}
			else{
				ytemp += dy;
			}
		}
		//Damaging tiles collision (non-blocking)
		if(dy < 0){ //Up direction
			if(topLeftD || topRightD){
				hit(Tile.dangerDMG);
			}
		}
		if(dy > 0){ //Down
			if(bottomLeftD || bottomRightD){
				hit(Tile.dangerDMG);
			}
		}

		calculateCorners(xdest,y);
		if(dx < 0){ //Left
			if(topLeft || bottomLeft){
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
			}
			else{
				xtemp += dx;
			}
		}
		if(dx > 0){ //Right
			if(topRight || bottomRight){
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
			}
			else{
				xtemp += dx;
			}
		}
		//Damaging tiles
		if(dx < 0){ //Left
			if(topLeftD || bottomLeftD){
				hit(Tile.dangerDMG);
			}
		}
		if(dx > 0){ //Right
			if(topRightD|| bottomRightD){
				hit(Tile.dangerDMG);
			}
		}

		if(!falling){
			calculateCorners(x,ydest+1);
			if(!bottomLeft && !bottomRight){
				falling = true;
			}
		}
	}

	public int getX(){
		return (int) x;
	}
	public int getY(){
		return (int) y;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public int getCWidth(){
		return cwidth;
	}
	public int getCHeight(){
		return cheight;
	}

	public void setPosition(double x,double y){
		this.x = x;
		this.y = y;
	}
	public void setVector(double dx,double dy){
		this.dx = dx;
		this.dy = dy;
	}

	public void setMapPosition(){
		xmap = tileMap.getX();
		ymap = tileMap.getY();
	}

	public void setLeft(boolean b){
		left = b;
	}
	public void setRight(boolean b){
		right = b;
	}
	public void setUp(boolean b){
		up = b;
	}
	public void setDown(boolean b){
		down = b;
	}
	public void setJumping(boolean b){
		jumping = b;
	}

	public void draw(Graphics2D g){
		setMapPosition();
		if(facingRight){
			g.drawImage(animation.getImage(),(int) (x + xmap - width / 2),(int) (y + ymap - height / 2),null);
		}
		else{ //Facing left = flip image
				g.drawImage(animation.getImage(),(int) (x + xmap - width / 2 + width),(int) (y + ymap - height / 2),-width,height,null);
		}
	}
}
