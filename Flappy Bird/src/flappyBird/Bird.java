package flappyBird;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Bird extends Rectangle {
	
	private static final long serialVersionUID = 1L;

	private int spd = 4;
	public boolean isPressed = false;
	
	private ArrayList<Rectangle> pipes;
	private BufferedImage flappy;
	
	public Bird(int x, int y, ArrayList<Rectangle> pipes){
		setBounds(x, y, 36, 26);
		this.pipes = pipes;
		
		// Getting the bird img
		try {
			flappy = ImageIO.read(getClass().getResource("/flappy.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void gameOver(){
		// Resetting the bird, pipes and score
		FlappyBird.pipes = new Pipes(90);
		pipes = FlappyBird.pipes.pipes;
		y = FlappyBird.HEIGHT/2;
		FlappyBird.score = 0;
		FlappyBird.isPaused = true;
		FlappyBird.isGameover = true;
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void update(){
		// Incr or Decr y-axis of bird
		if(isPressed){
			y -= spd;
		}else{
			y += spd;
		}
		
		// Game over if bird intersects the pipes
		for(int i = 0; i < pipes.size(); i++){
			if(this.intersects(pipes.get(i))){
				gameOver();
				break;
			}
		}
		
		// Game over if bird touches edges of screen
		if(y > FlappyBird.HEIGHT - 32 || y < 0){
			gameOver();
		}
	}
	
	public void render(Graphics g){
		// Drawing the bird
		g.drawImage(flappy, x, y, 42, 32, null);
	}
}

