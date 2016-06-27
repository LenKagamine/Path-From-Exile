public class Enemy extends MapObject{
	protected int damage;
	protected boolean dying;
	public Enemy(TileMap tm){
		super(tm);
	}

	public boolean isDead(){
		return dead;
	}
	public boolean isDying(){
		return dying;
	}

	public int getDamage(){
		return damage;
	}

	public void hit(int damage){
		//super.hit(damage);
		if(flinching) return; //Invincible during flinching to prevent juggling
		health -= damage;
		if(health < 0) health = 0; //No negative health
		if(health == 0) dying = true;
		flinching = true;
		flinchTimer = System.currentTimeMillis(); //Start timer
	}

	public void update(){}
}
