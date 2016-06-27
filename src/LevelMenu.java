import java.awt.*;
import java.awt.event.KeyEvent;

public class LevelMenu extends Levels{
	//Background
	private Background bg;
	//Choices
	private int currentChoice = 0;
	private String[] options = {
			"Start", "Quit"
	};
	//Colors/Fonts for title text
	private Color titleColor;
	private Font titleFont;
	//Font for other text
	private Font font;
	//BGM
	private AudioPlayer bgm;
	
	public LevelMenu(){
		try{
			bg = new Background("/Resources/Backgrounds/menu.png",1,true,true); //Get background pic
			bg.setVector(-0.1,0); //Set speed to move left
			//Set fonts and colors
			titleColor = new Color(0,204,204);
			titleFont = new Font("Arial",Font.PLAIN,28);

			font = new Font("Arial",Font.PLAIN,12);

			bgm = new AudioPlayer("intro.mp3");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		bgm.loop();
	}
	
	public void init(){} //Nothing to init
	
	public void update(){
		bg.update();
	}
	
	public void draw(Graphics2D g){
		bg.draw(g);

		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Path from Exile",(GamePanel.WIDTH-g.getFontMetrics().stringWidth("Path from Exile"))/2,70);

		g.setFont(font);
		for(int i=0;i<options.length;i++){ //Draw all choices
			if(i == currentChoice) g.setColor(Color.blue); //Current choice color = black
			else g.setColor(Color.gray);

			g.drawString(options[i],145,140+i*15);
		}
	}

	private void select(){
		if(currentChoice == 0){
			bgm.stop();
			GamePanel.setLevel(1);
		}
		if(currentChoice == 1){
			System.exit(0);
		}
	}

	public void keyPressed(int k){
		if(k == KeyEvent.VK_ENTER){
			select();
		}
		if(k == KeyEvent.VK_UP){
			currentChoice--;
			if(currentChoice == -1) currentChoice = options.length-1;
		}
		if(k == KeyEvent.VK_DOWN){
			currentChoice++;
			if(currentChoice == options.length) currentChoice = 0;
		}
	}
	
	public void keyReleased(int k){}
}
