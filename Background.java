import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Background{
    private BufferedImage image;
    private int width,height;
    private double x,y,dx,dy;
    private double moveScale;
    private boolean scrollH,scrollV; //Horizontal/Vertical scrolling enabled
    
    public Background(String s,double moveScale,boolean scrollH,boolean scrollV){
	this.scrollH = scrollH;
	this.scrollV = scrollV;
	try{
	    image = ImageIO.read(getClass().getResourceAsStream(s)); //Get picture
	    width = image.getWidth(); //Get specs
	    height = image.getHeight();
	    this.moveScale = moveScale;
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }
    
    public void setPosition(double x,double y){
	if(scrollH) this.x = x*moveScale;
	else this.x = 0; //If scrolling not enabled, just follow screen
	if(scrollV) this.y = y*moveScale;
	else this.y = 0;
	fixPosition();
    }
    
    public void setVector(double dx,double dy){
	this.dx = dx;
	this.dy = dy;
    }
    
    public void update(){
	x += dx;
	y += dy;
	fixPosition();
    }
    
    public void fixPosition(){ //Exceptions for scrolling
	while(x <= -width) x += width;
	while(x >= width) x -= width;
	while(y <= -height) y += height;
	while(y >= height) y -= height;
    }
    
    public void draw(Graphics2D g){ //Draw image
	g.drawImage(image,(int) x,(int) y,null);
	if(x<0) g.drawImage(image,(int) x+GamePanel.WIDTH,(int) y,null); //Draw again for scrolling
	else g.drawImage(image,(int) x-GamePanel.WIDTH,(int) y,null);
    }
}
