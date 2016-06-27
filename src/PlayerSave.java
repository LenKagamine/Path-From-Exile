public class PlayerSave{
	private static int healthSave = 5;
	private static double manaSave = 25.0;

	public static int getHealth(){
		return healthSave;
	}
	
	public static void setHealth(int health){
		healthSave = health;
	}
	
	public static double getMana(){
		return manaSave;
	}
	
	public static void setMana(double Mana){
		manaSave = Mana;
	}
}
