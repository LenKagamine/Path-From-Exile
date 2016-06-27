import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject{
	public boolean attacking;
	//Shoot
	private double mana;
	private int maxMana;
	private boolean shooting;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<Bullet> fireBalls;
	//Stab
	private boolean stabbing;
	private int stabCost;
	private int stabDamage;
	private int stabRange;
	private double stabMoveSpeed;
	private double stabCounter,stabCounterIncrement;
	//Swing
	private boolean swinging;
	private int swingCost;
	private int swingRange;
	//Gliding & Double Jump
	private boolean gliding;
	private boolean doubleJump, doubleJumped; //Only player can double jump
	private double doubleJumpStart;
	//Animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {4, 4, 1, 2, 5, 3, 3}; //Number of frames in each action
	//Animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 2;
	private static final int GLIDING = 3;
	private static final int SHOOTING = 4;
	private static final int STABBING = 5;
	private static final int SWINGING = 6;
	//Audio
	private AudioPlayer shoot;
	
	public Player(TileMap tm, String s){
		super(tm);
		//Dimensions
		width = 40;
		height = 40;
		cwidth = 20;
		cheight = 28;
		//Speeds
		moveSpeed = 0.6;
		maxSpeed = 2.0;
		stopSpeed = 0.8;
		fallSpeed = 0.3;
		maxFallSpeed = 4.0;
		jumpStart = -7.0;
		stopJumpSpeed = 0.8;
		doubleJumpStart = -5.5;
		stabMoveSpeed = 2.0;

		stabCounter = 2.0;
		stabCounterIncrement = 0.12;

		facingRight = true;

		health = maxHealth = 5;
		mana = maxMana = 24;
		//Abilities
		fireCost = 2;
		fireBallDamage = 5;
		fireBalls = new ArrayList<Bullet>();
		
		stabCost = 5;
		stabDamage = 8;
		stabRange = 40;
		
		swingCost = 5;
		swingRange = 45;

		//Load sprites
		try{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(s));

			sprites = new ArrayList<BufferedImage[]>();
			for(int i=0;i<numFrames.length;i++){
				BufferedImage[] bi = new BufferedImage[numFrames[i]]; //Input for all sprites, grouped by animation
				for(int j=0;j<numFrames[i];j++){
					if(i == SHOOTING){
						bi[j] = spritesheet.getSubimage(j * 61, 160, 61, 108);
					}
					else if(i == STABBING){
						bi[j] = spritesheet.getSubimage(j * 101, 268, 102, 51);
					}
					else if(i == SWINGING){
						bi[j] = spritesheet.getSubimage(j * 103, 316, 103, 94);
					}
					else{
						bi[j] = spritesheet.getSubimage(j * width,i * height,width,height);
					}
				}
				sprites.add(bi);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//Init animation
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames((BufferedImage[])sprites.get(IDLE));
		animation.setDelay(500);

		shoot = new AudioPlayer("shoot.mp3");
	}
	
	public int getHealth(){
		return health;
	}
	public void setHealth(int health){
		this.health = health;
	}
	public int getMaxHealth(){
		return maxHealth;
	}
	public double getMana(){
		return mana;
	}
	public void setMana(double mana){
		this.mana = mana;
	}
	public int getMaxMana(){
		return maxMana;
	}

	public void setFiring(){
		if(mana > fireCost && !attacking) shooting = true; //Fire only if you have the mana to do it; don't fire while stabbing
	}
	public void setStabbing(){
		if(mana > stabCost && !attacking) stabbing = true; //Don't stab while shooting; laser bug
	}
	public void setSwinging(){
		if(mana > swingCost && !attacking) swinging = true;
	}
	public void isAttacking(){ //Is player attacking?
		if(shooting || stabbing || swinging) attacking = true;
		else attacking = false;
	}

	public void setGliding(boolean b){
		gliding = b;
	}
	public void setJumping(boolean b){
		if(b && !jumping && falling && !doubleJumped) doubleJump = true;
		jumping = b;
	}

	public void setDead(){
		health = 0;
		stop();
	}
	
	public void stop(){ //Stop every single action
		left = right = up = down = flinching = jumping = falling
				= attacking = gliding = stabbing = swinging = shooting = false;
	}
	
	public void reset(){
		health = maxHealth;
		mana = maxMana;
		facingRight = true;
		currentAction = IDLE;
		stop();
	}

	public void checkAttack(ArrayList<Enemy> enemies){
		for(int i=0;i<enemies.size();i++){ //Loop through enemies
			Enemy e = (Enemy)enemies.get(i);
			//Stab attack
			if(stabbing){
				if(facingRight){
					if(e.getX() > x && e.getX() < x + stabRange &&
							e.getY() > y - height / 2 && e.getY() < y + height / 2){
						e.hit(stabDamage);
					}
				}
				else{
					if(e.getX() < x && e.getX() > x - stabRange &&
							e.getY() > y - height / 2 && e.getY() < y + height / 2){
						e.hit(stabDamage);
					}
				}
			}
			if(swinging){
				if(facingRight){
					if(e.getX() > x - swingRange && e.getX() < x + swingRange &&
							e.getY() > y - height && e.getY() < y + height){
						e.hit(stabDamage);
					}
				}
				else{
					if(e.getX() < x + swingRange && e.getX() > x - swingRange &&
							e.getY() > y - height / 2 && e.getY() < y + height / 2){
						e.hit(stabDamage);
					}
				}
			}
			//Fireball
			for(int j=0;j<fireBalls.size();j++){
				if(((Bullet)fireBalls.get(j)).intersects(e)){
					e.hit(fireBallDamage);
					((Bullet)fireBalls.get(j)).setHit();
					break;
				}
			}
			//Check enemy collision; invincible while attacking
			if(intersects(e) && !attacking && !e.isDying()){
				hit(e.getDamage());
			}
		}
	}

	private void getNextPosition(){
		//Movement
		if(left){
			dx -= moveSpeed;
			if (dx < -maxSpeed){
				dx = -maxSpeed;
			}
		}
		else if(right){
			dx += moveSpeed;
			if(dx > maxSpeed){
				dx = maxSpeed;
			}
		}
		else{
			if(dx > 0){
				dx -= stopSpeed;
				if(dx < 0){
					dx = 0;
				}
			}
			else if(dx < 0){
				dx += stopSpeed;
				if(dx > 0){
					dx = 0;
				}
			}
		}
		//Cannot move while attacking, except in air
		if(attacking && !(jumping || falling)){
			dx = 0;
		}

		//Jumping
		if(jumping && !falling){
			dy = jumpStart;
			falling = true;
		}
		//Double Jumping
		if(doubleJump){
			dy = doubleJumpStart;
			doubleJumped = true;
			doubleJump = false;
		}
		if(!falling) doubleJumped = false;
		//Falling
		if(falling){
			if(dy > 0 && gliding) dy += fallSpeed * 0.1;
			else dy += fallSpeed;

			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;

			if(dy > maxFallSpeed) dy = maxFallSpeed; //Cap

		}
		//Stab also makes player dash, but decelerate
		if(currentAction != STABBING) stabCounter = 2.0;
		if(stabbing && !jumping && !falling){
			stabCounter -= stabCounterIncrement; //Slow down
			if(facingRight) dx = stabMoveSpeed + stabCounter;
			else dx = -stabMoveSpeed - stabCounter;
		}
	}

	public void update(){
		//Update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp,ytemp);
		//Check attack has stopped; attacks' animation only play once
		if(currentAction == STABBING) if(animation.hasPlayedOnce()) stabbing = false;
		if(currentAction == SHOOTING) if(animation.hasPlayedOnce()) shooting = false;
		if(currentAction == SWINGING) if(animation.hasPlayedOnce()) swinging = false;
		isAttacking(); //Check attacking
		//Fireball attack
		mana += 0.01;
		if(mana > maxMana) mana = maxMana;
		if(shooting && currentAction != SHOOTING){
			if(mana > fireCost){
				mana -= fireCost;
				Bullet fb = new Bullet(tileMap, facingRight);
				fb.setPosition(x, y); //Fire from player's location
				fireBalls.add(fb);
			}
		}
		if(stabbing && currentAction != STABBING){
			if(mana > stabCost){
				mana -= stabCost;
			}
		}
		if(swinging && currentAction != SWINGING){
			if(mana > swingCost){
				mana -= swingCost;
			}
		}
		
		//Update fireballs
		for(int i=0;i<fireBalls.size();i++){
			((Bullet)fireBalls.get(i)).update();
			if(((Bullet)fireBalls.get(i)).shouldRemove()){
				fireBalls.remove(i);
				i--;
			}
		}
		//Check done flinching
		if(flinching){
			long elapsed = System.currentTimeMillis() - flinchTimer;
			if(elapsed > 1000){
				flinching = false;
			}
		}

		//Set animation
		if(stabbing){
			if(currentAction != STABBING){
				currentAction = STABBING;
				animation.setFrames((BufferedImage[])sprites.get(STABBING));
				animation.setDelay(100);
				width = 102;
			}
		}
		else if(shooting){
			if(currentAction != SHOOTING){
				shoot.play();
				currentAction = SHOOTING;
				animation.setFrames((BufferedImage[])sprites.get(SHOOTING));
				animation.setDelay(100);
				width = 61;
			}
		}
		else if(swinging){
			if(currentAction != SWINGING){
				currentAction = SWINGING;
				animation.setFrames((BufferedImage[])sprites.get(SWINGING));
				animation.setDelay(100);
				width = 103;
			}
		}
		else if(dy > 0){ //Falling
			if(gliding){ //Gliding
				if(currentAction != GLIDING){
					currentAction = GLIDING;
					animation.setFrames((BufferedImage[])sprites.get(GLIDING));
					animation.setDelay(200);
					width = 40;
				}
			}
			else if (currentAction != FALLING){ //Falling
				currentAction = FALLING;
				animation.setFrames((BufferedImage[])sprites.get(FALLING));
				animation.setDelay(-1); //No animation for fall
				width = 40;
			}
			if(down){ //Go down faster
				dy += 0.5;
			}
		}
		else if(dy < 0){ //Jumping
			if(currentAction != JUMPING){
				currentAction = JUMPING;
				animation.setFrames((BufferedImage[])sprites.get(JUMPING));
				animation.setDelay(-1); //No animation for jump
				width = 40;
			}
		}
		else if(left || right){
			if(currentAction != WALKING){
				currentAction = WALKING;
				animation.setFrames((BufferedImage[])sprites.get(WALKING));
				animation.setDelay(200);
				width = 40;
			}
		}
		else{ //Idle
			if(currentAction != IDLE){
				currentAction = IDLE;
				animation.setFrames((BufferedImage[])sprites.get(IDLE));
				animation.setDelay(500);
				width = 40;
			}
		}

		animation.update();

		//Set direction
		if(currentAction != STABBING && currentAction != SHOOTING){
			if(right) facingRight = true;
			if(left) facingRight = false;
		}
	}

	public void fixPlayerPosition(boolean start){
		if(start){ //Reposition due to sprite size differences
			if(currentAction == SHOOTING){
				width = 61;
				height = 108;
				y -= 2;
				x += 5;
				if(!facingRight) x -= 10;
			}
			if(currentAction == STABBING){
				width = 102;
				height = 51;
				y += 5;
			}
			if(currentAction == SWINGING){
				width = 103;
				height = 94;
				y -= 10;
			}
		}
		else{
			width = 40;
			height = 40;
			if(currentAction == SHOOTING){
				y += 2;
				x -= 5;
				if(!facingRight) x += 10;
			}
			if(currentAction == STABBING) y -= 5;
			if(currentAction == SWINGING) y += 10;
		}
	}
	
	public void draw(Graphics2D g){
		//Draw fireballs
		for(int i=0;i<fireBalls.size();i++){
			((Bullet)fireBalls.get(i)).draw(g);
		}
		//Draw player
		if(flinching){
			long elapsed = System.currentTimeMillis() - flinchTimer;
			if(elapsed / 100 % 2 == 0){
				return;
			}
		}

		fixPlayerPosition(true);
		super.draw(g);
		fixPlayerPosition(false);
	}
}
