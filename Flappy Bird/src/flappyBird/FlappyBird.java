package flappyBird;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class FlappyBird extends Canvas implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 640, HEIGHT = 480;
	private boolean running = false;
	
	public static double score = 0;
	public static double highscore = 0;
	
	public static boolean isPaused = true;
	public static boolean isGameover = false;
	
	public int lvlNo = 0; 
	public String lvl = "<< EASY >>";
	
	private int time = 0;
	private int targetFrames = 40;
	private boolean showText = true;
	
	private Thread thread;
	private BufferedImage bg;
	
	public static Pipes pipes;
	public Bird bird;
	
	// Constructor
	public FlappyBird(){
		Dimension d = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(d);
		addKeyListener(this);
		setFocusable(true);
		
		// Initializing Pipes and Bird objects
		pipes = new Pipes(90);
		bird = new Bird(60, HEIGHT/2, pipes.pipes);
		
		// Getting the bg image
		try {
			bg = ImageIO.read(getClass().getResource("/background.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// If not running start
	public synchronized void start(){
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	// If running stop
	public synchronized void stop(){
		if(!running) return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		// Setting the frame and its properties
		JFrame frame = new JFrame("Flappy Bird"); 
		FlappyBird fb = new FlappyBird();
		frame.add(fb);
		frame.setResizable(false);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		fb.start();
	}

	@Override
	public void run() {
		// Capping framerate at 60fps
	    long startTime;
	    long waitTime;
	    long updateTime;
	    
	    final int FPS = 60;
	    final long TARGET_TIME = 1000000000 / FPS;
	    
	    // Game loop
	    while (running) {
	        startTime = System.nanoTime();
	        
	        update();
	        render();

	        updateTime = System.nanoTime() - startTime;
	        waitTime = (TARGET_TIME - updateTime) / 1000000;
	        
	        if(waitTime < 0){
	        	waitTime = 0;
	        }
	        
	        try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	    stop();
	}
	

	private void render() {
		// Creating graphics object
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		// Drawing the bg
		g.drawImage(bg, 0, 0, WIDTH, HEIGHT, null);
		// Rendering the main game
		if(!isPaused){
			pipes.render(g);
			bird.render(g);
			g.setColor(Color.white);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
			g.drawString("Highscore: " + (int)highscore, 20, 30);
			g.drawString("Score: " + (int)score, 20, 60);
		}else{
			// Rendering the start & game over screen
			String text;
			String instr;
			int width;
			int xAxis;
			if(isGameover){
				text = "Game Over";
				instr = "Press Enter to Play Again";
			}else{
				 text = "Flappy Bird";
				 instr = "Press Enter to Begin";
			}
			g.setColor(new Color(23, 91, 105));
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 52));
			width = g.getFontMetrics().stringWidth(text);
			xAxis = (WIDTH/2)- width/2;
			g.drawString(text, xAxis, HEIGHT/2 - 50);
			
			g.setColor(new Color(23, 118, 133));
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
			width = g.getFontMetrics().stringWidth(instr);
			xAxis = (WIDTH/2)- width/2;
			if(showText) g.drawString(instr, xAxis, HEIGHT/2);
			
			String lvlMsg = "Select Level";
			g.setColor(new Color(23, 91, 105));
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
			width = g.getFontMetrics().stringWidth(lvlMsg);
			xAxis = (WIDTH/2)- width/2;
			g.drawString(lvlMsg, xAxis, HEIGHT/2 + 50);
			
			g.setColor(new Color(23, 118, 133));
			width = g.getFontMetrics().stringWidth(lvl);
			xAxis = (WIDTH/2)- width/2;
			g.drawString(lvl, xAxis, HEIGHT/2 + 80);
		}
		g.dispose();
		bs.show();
	}

	private void update() {
		// If game is not paused update the pipes and bird
		if(!isPaused){
			pipes.update();
			bird.update();
		}else{
			time++;
			if(time == targetFrames){
				time = 0;
				if(showText) showText = false;
				else showText = true;
			}
		}
	}

	public void updateLvl(int lvlNo){
		// Values for different lvls
		switch(lvlNo){
		case 0:
			lvl = "<< EASY >>";
			Pipes.PIPE_GAP = 120;
			Pipes.spd = 4;
			break;
		case 1:
			lvl = "<< MEDIUM >>";
			Pipes.PIPE_GAP = 105;
			Pipes.spd = 5;
			break;
		case 2:
			lvl = "<< HARD >>";
			Pipes.PIPE_GAP = 90;
			Pipes.spd = 6;
			break;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(!isPaused){
			if(e.getKeyCode()== KeyEvent.VK_SPACE ) bird.isPressed = true;                   
		} else {
			// Starting the game on ENTER
			if(e.getKeyCode()== KeyEvent.VK_ENTER ) isPaused = false;
			// Changing lvls on LEFT & RIGHT arrows
			if(e.getKeyCode()== KeyEvent.VK_RIGHT ){
				if(lvlNo < 2){
					lvlNo++;
					updateLvl(lvlNo);	
				}else lvlNo = 2;
			};
			if(e.getKeyCode()== KeyEvent.VK_LEFT ){
				if(lvlNo > 0){
					lvlNo--;
					updateLvl(lvlNo);	
				}else lvlNo = 0;
			};
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()== KeyEvent.VK_SPACE ) bird.isPressed = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
