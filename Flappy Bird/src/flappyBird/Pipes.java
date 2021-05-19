package flappyBird;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Pipes {
	public ArrayList<Rectangle> pipes;
	private int time;
	private int currentTime = 0;
	
	public static int spd = 4;
	
	private final int PIPE_WIDTH = 60;
	public static int PIPE_GAP = 110;
	
	public Pipes(int time){
		pipes = new ArrayList<>();
		this.time = time;
	}
	
	public void update(){
		currentTime++;
		if(currentTime == time){
			currentTime = 0;
			
			// Randomly selecting height of pipes
			Random r = new Random();
			int low = 40;
			int high = FlappyBird.HEIGHT - PIPE_GAP - 40;
			int height1 = r.nextInt(high-low) + low;		
			
			int y2 = height1 + PIPE_GAP;
			int height2 = FlappyBird.HEIGHT - y2;
			
			pipes.add(new Rectangle(FlappyBird.WIDTH, 0, PIPE_WIDTH, height1));
			pipes.add(new Rectangle(FlappyBird.WIDTH, y2, PIPE_WIDTH, height2));
		}
		
		for(int i = 0; i< pipes.size(); i++){
			Rectangle rect = pipes.get(i);
			rect.x -= spd;
			
			// Removing pipes when they go out of screen
			if(rect.x + rect.width <= 0){
				pipes.remove(i--);
				// Increasing the score
				FlappyBird.score += 0.5;
				if(FlappyBird.score > FlappyBird.highscore){
					FlappyBird.highscore = FlappyBird.score;
				}
				continue;
			}
		}
	}
	
	public void render(Graphics g){
		int width = 2;
		for(int i = 0; i< pipes.size(); i++){
			Rectangle rect = pipes.get(i);
			g.setColor(Color.black);
		    g.fillRect(rect.x, rect.y, rect.width, rect.height);
		    g.setColor(new Color(141, 235, 141));
		    g.fillRect(rect.x+width/2, rect.y+width/2, rect.width-width, rect.height-width);
		    g.setColor(new Color(51, 204, 51));
		    g.fillRect(rect.x+width/2, rect.y+width/2, (rect.width-width)-10, rect.height-width);
		}
	}
}
