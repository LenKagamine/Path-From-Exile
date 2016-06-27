import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class Slime extends Enemy{
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {7, 1, 1, 4};

	private static final int WALKING = 0;
	private static final int FALLING = 1;
	private static final int HURT = 2;
	private static final int DYING = 3;
	
	public Slime(TileMap tm){
		super(tm);

		moveSpeed = 0.6;
		maxSpeed = 0.6;
		fallSpeed = 0.2;
		maxFallSpeed = 10.0;

		width = 36;
		height = 47;
		cwidth = 27;
		cheight = 47;

		health = maxHealth = 2;
		damage = 1;
		try{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Resources/Sprites/Enemies/Slime.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for(int i=0;i<numFrames.length;i++){
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j=0;j<numFrames[i];j++){
					if(i == FALLING) bi[j] = spritesheet.getSubimage(j * 31,47,31,33);
					else if(i == HURT) bi[j] = spritesheet.getSubimage(j * 31,81,31,33);
					else if(i == DYING) bi[j] = spritesheet.getSubimage(j * 32,114,32,30);
					else bi[j] = spritesheet.getSubimage(j * width,i * height,width,height);
				}
				sprites.add(bi);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = WALKING;
		animation.setFrames((BufferedImage[])sprites.get(WALKING));
		animation.setDelay(300);
		right = true;
	}

	private void getNextPosition(){
		if(left && !dying){
			dx -= moveSpeed;
			if(dx < -maxSpeed) dx = -maxSpeed;
		}
		else if(right && !dying){
			dx += moveSpeed;
			if(dx > maxSpeed) dx = maxSpeed;
		}
		if(falling){
			dy += fallSpeed;
		}
	}

	public void update(){
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp,ytemp);

		if(currentAction == DYING && animation.hasPlayedOnce()){
			dying = false; //No more dying animation
			dead = true;   //Completely dead and removed
		}

		if(dying){
			if(currentAction != DYING){
				currentAction = DYING;
				animation.setFrames((BufferedImage[])sprites.get(DYING));
				animation.setDelay(100);
				dx = 0;
			}
		}
		else if(flinching){
			if(currentAction != HURT){
				currentAction = HURT;
				animation.setFrames((BufferedImage[])sprites.get(HURT));
				animation.setDelay(-1);
				width = 31;
				height = 30;
			}
			long elapsed = System.currentTimeMillis() - flinchTimer;
			if(elapsed > 400){
				flinching = false;
			}
		}
		else if(dy > 0){
			if(currentAction != FALLING){
				currentAction = FALLING;
				animation.setFrames((BufferedImage[])sprites.get(FALLING));
				animation.setDelay(-1);
				width = 31;
				height = 43;
			}
		}
		else if(left || right){
			if(currentAction != WALKING){
				currentAction = WALKING;
				animation.setFrames((BufferedImage[])sprites.get(WALKING));
				animation.setDelay(100);
				width = 36;
				height = 47;
			}
		}

		if(right && dx == 0 && !dying){
			right = false;
			left = true;
			facingRight = true;
		}
		else if(left && dx == 0 && !dying){
			left = false;
			right = true;
			facingRight = false;
		}

		animation.update();
	}

	public void draw(Graphics2D g){
		super.draw(g);
	}
}
