import java.awt.image.BufferedImage;

public class Animation{
	//Frames
	private BufferedImage[] frames;
	private int currentFrame;

	private long startTime, delay;

	private boolean playedOnce;

	public Animation(){
		playedOnce = false;
	}

	public void setFrames(BufferedImage[] frames){
		this.frames = frames;
		currentFrame = 0;
		startTime = System.currentTimeMillis();
		playedOnce = false;
	}

	public void setDelay(long d){
		delay = d;
	}
	public void setFrame(int i){
		currentFrame = i;
	}

	public void update(){
		if(delay == -1) return; //No delay

		long elapsed = System.currentTimeMillis() - startTime;
		if(elapsed > delay){ //Wait for delay
			currentFrame++; //Time passed, next frame
			startTime = System.currentTimeMillis();
		}
		if(currentFrame == frames.length){ //Animation gone through once
			currentFrame = 0; //Reset frames
			playedOnce = true;
		}
	}

	public int getFrame(){
		return currentFrame;
	}
	public BufferedImage getImage(){
		return frames[currentFrame];
	}
	public boolean hasPlayedOnce(){
		return playedOnce;
	}
}
