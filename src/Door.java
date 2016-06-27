import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Door extends MapObject{
	private BufferedImage[] sprites;
	
	public Door(TileMap tm,String s){
		super(tm);
		facingRight = true;
		width = 30;
		height = 47;
		cwidth = 30;
		cheight = 50;
		try{
			sprites = new BufferedImage[1];
			sprites[0] = ImageIO.read(getClass().getResourceAsStream(s));
		}
		catch(Exception e){
			e.printStackTrace();
		}

		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(-1);
	}

	public void update(){
		animation.update();
	}

	public void draw(Graphics2D g){
		super.draw(g);
	}
}
