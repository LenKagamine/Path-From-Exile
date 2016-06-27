import java.awt.image.BufferedImage;

public class Tile{
	private BufferedImage image;
	private int type;
	//Tile Types
	public static final int NORMAL = 0,BLOCKED = 1,DANGER = 2; //Damaging tiles are non-blocking
	public static final int dangerDMG = 2; //Damaging tiles deal 2 damage

	public Tile(BufferedImage image, int type){
		this.image = image;
		this.type = type;
	}

	public BufferedImage getImage(){
		return image;
	}
	
	public int getType(){
		return type;
	}
}
