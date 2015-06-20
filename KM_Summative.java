import javax.swing.*;
public class KM_Summative{
    public static void main(String[] args){
	/*
	Controls: left/right = walk
		  up = jump (up again to double jump)
		  down = fall faster
		  z = shoot
		  x = stab/dash
		  c = swing
		  v = glide (only when falling)
		  enter = select (in menu)
	*/
	JFrame window = new JFrame("KM_Summative");
	window.setContentPane(new GamePanel());
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
	window.setUndecorated(true);
	window.setResizable(false);
	window.pack();
	window.setLocationRelativeTo(null);
	window.setVisible(true);
    }
}
