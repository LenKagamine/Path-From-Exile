import java.awt.*;

public class HUD{
    private Player player;
    
    private Font font;
    
    public HUD(Player p){
	player = p;
	font = new Font("Arial", Font.PLAIN, 14);
    }
    
    public void draw(Graphics2D g){
	g.setFont(font);
	g.setColor(Color.black);
	if(5*player.getMaxMana() > 20*player.getHealth()) g.fillRoundRect(13,8,(int)(5*player.getMaxMana())+4,44,5,5);
	else g.fillRoundRect(13,8,20*player.getHealth()+4,44,5,5);
	g.setColor(Color.red);
	g.fillRoundRect(15,10,20*player.getHealth(),20,5,5);
	g.setColor(Color.blue);
	g.fillRoundRect(15,30,(int)(5*player.getMana()),20,5,5);
	g.setColor(Color.WHITE);
	g.drawString(player.getHealth() + "/"+ player.getMaxHealth(),20,25);
	g.drawString((int)player.getMana() + "/" + player.getMaxMana(),20,45);
    }
}
