package main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import screen.BaseScreen;

public class MainComponent extends Canvas implements Runnable {
	
	private static final long serialVersionUID = 1L;
	public static String GAME_TITLE = "Hobby";
	public static int GAME_WIDTH = 160;
	public static int GAME_HEIGHT = 144;
	public static int GAME_SCALE = 3;
	
	//-----------------------
	private final BaseScreen screen;
	private Game game;
	//-----------------------
	
	private final Keys keys = new Keys();
	private InputHandler inputHandler;
	
	//-----------------------	
	private boolean running;
	private int fps;
	
	//-----------------------
	
	public MainComponent() {
		running = false;
		screen = new BaseScreen(GAME_WIDTH, GAME_HEIGHT);
		screen.loadResources();
	}
	
	//Place where I get assets from.
	public void init() {
		this.requestFocus();
		
		//-------------------
		//Input Handling
		inputHandler = new InputHandler(keys);
		this.addKeyListener(inputHandler);
		//this.player = new Player(keys);
		
		//-------------------
		//Game loading
		//We pass the BaseScreen variable as a parameter, acting as an output.
		game = new Game(screen, keys);
		game.load();
		//world = new TestWorld(Art.testArea);
		
		//this.world.addEntity(player);
		//this.world.setEntityStartingPosition(this.player);
	}
	
	public void start() {
		running = true;
		Thread thread = new Thread(this);
		thread.setName("Game Loop Thread");
		thread.start();
	}
	
	public void stop() {
		running = false;
		game.save();
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long lastTimer = System.currentTimeMillis();
		double unprocessed = 0.0;
		int frames = 0;
		int tick = 0;
		boolean shouldRender = false;
		final double nsPerTick = 1000000000.0 / 60.0;
		try {
			init();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		while (running) {
			shouldRender = false;
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			while (unprocessed >= 1) {
				tick++;
				unprocessed -= 1;
			}
			
			//Limits the number of ticks per unprocessed frame.
			int toTick = tick;
			if (tick > 0 && tick < 3)
				toTick = 1;
			if (tick > 7)
				toTick = 7;
			
			for (int i = 0; i < toTick; i++) {
				tick--;
				tick();
				shouldRender = true;
			}
			
			if (shouldRender) {
				frames++;
				render();
			}
			
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
			}
			
			if (System.currentTimeMillis() - lastTimer > 1000) {
				lastTimer += 1000;
				fps = frames;
				System.out.println("FPS: " + Integer.toString(fps));
				frames = 0;
			}
		}
		//Game loop has exited, everything stops from here on out.
	}
	
	public synchronized void tick() {
		keys.tick();
		//world.tick();
		game.tick();
	}
	
	public synchronized void render() {
		BufferStrategy bufferStrategy = this.getBufferStrategy();
		if (bufferStrategy == null) {
			//Create new buffers before returning, if no buffers exist yet.
			this.createBufferStrategy(2);
			return;
		}
		
		//Get graphics variable.
		Graphics g = bufferStrategy.getDrawGraphics();
		
		//Background border
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		screen.clear(0xA4E767);
		
		//World rendering.
		//if (world != null) {
		//	int xScroll = player.getX() - screen.getWidth() / 2;
		//	int yScroll = player.getY() - screen.getHeight() / 2;
		//world.render(screen, xScroll, yScroll);
		//}
		
		//game.setScrollOffset(GAME_WIDTH / 2, (GAME_HEIGHT + Tile.HEIGHT) / 2);
		game.setCameraRelativeToArea(GAME_WIDTH / 2, GAME_HEIGHT / 2);
		game.render();
		
		BufferedImage image = this.createCompatibleBufferedImage(screen.getBufferedImage());
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		g.dispose();
		bufferStrategy.show();
	}
	
	public WindowListener getWindowListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				MainComponent.this.stop();
			}
		};
	}
	
	//-------------------------------
	//Private methods:
	
	private BufferedImage createCompatibleBufferedImage(BufferedImage image) {
		GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;
		BufferedImage newImage = gfx_config.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
		Graphics2D graphics = (Graphics2D) newImage.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return newImage;
	}
	
	@SuppressWarnings("unused")
	private BufferedImage scaleBufferedImage(BufferedImage before) {
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(GAME_SCALE, GAME_SCALE);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		after = scaleOp.filter(before, after);
		return after;
	}
	
	//---------------------------------
	//Other methods
	
	//---------------------------------
	//Main execution method
	
	public static void main(String[] args) {
		
		MainComponent game = new MainComponent();
		JFrame frame = new JFrame(GAME_TITLE);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(game);
		frame.setContentPane(panel);
		frame.pack();
		frame.setResizable(false);
		Insets inset = frame.getInsets();
		frame.setSize(new Dimension(inset.left + inset.right + GAME_WIDTH * GAME_SCALE, inset.top + inset.bottom + GAME_HEIGHT * GAME_SCALE));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(game.getWindowListener());
		frame.setVisible(true);
		
		game.start();
	}
}