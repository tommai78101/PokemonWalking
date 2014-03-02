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
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import level.TestWorld;
import screen.BaseScreen;
import entity.TestEntity;

public class GameComponent extends Canvas implements Runnable {
	
	private static final long serialVersionUID = 1L;
	public static String GAME_TITLE = "Hobby";
	public static int GAME_WIDTH = 160;
	public static int GAME_HEIGHT = 144;
	
	//-----------------------
	private final BaseScreen screen;
	
	public int scale;
	private boolean running;
	private int fps;
	
	private final Keys keys = new Keys();
	private InputHandler inputHandler;
	
	//-----------------------
	public TestWorld world;
	public TestEntity player;
	
	//-----------------------
	
	public GameComponent() {
		scale = 3;
		running = false;
		
		screen = new BaseScreen(GAME_WIDTH, GAME_HEIGHT);
		screen.loadResources();
		this.createWorld();
	}
	
	public void init() {
		this.requestFocus();
		
		//-------------------
		//Input Handling
		inputHandler = new InputHandler(keys);
		this.addKeyListener(inputHandler);
		
		//-------------------
		//World loading
		initializeWorld();
	}
	
	public void createWorld() {
		world = new TestWorld(30, 20);
		//TODO: Create a method of loading the world.
	}
	
	public void initializeWorld() {
		this.player = new TestEntity(keys);
		this.world.addEntity(player);
	}
	
	public void start() {
		running = true;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		running = false;
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long lastTimer = System.currentTimeMillis();
		double unprocessed = 0.0;
		int frames = 0;
		int tick = 0;
		try {
			init();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		while (running) {
			double nsPerTick = 1000000000.0 / 60.0;
			boolean shouldRender = false;
			while (unprocessed >= 1) {
				tick++;
				unprocessed -= 1;
			}
			
			//Limits the number of ticks per unprocessed frame.
			int toTick = tick;
			if (tick > 0 && tick < 3)
				toTick = 1;
			if (tick > 20)
				toTick = 20;
			
			for (int i = 0; i < toTick; i++) {
				tick--;
				tick();
				shouldRender = true;
			}
			
			BufferStrategy bufferStrategy = this.getBufferStrategy();
			if (bufferStrategy == null) {
				this.createBufferStrategy(4);
				continue;
			}
			
			if (shouldRender) {
				frames++;
				Graphics g = bufferStrategy.getDrawGraphics();
				render(g);
				g.dispose();
			}
			
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (shouldRender) {
				if (bufferStrategy != null) {
					bufferStrategy.show();
				}
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
		world.tick();
		
	}
	
	public synchronized void render(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.translate((((this.getWidth() - (GAME_WIDTH) * scale)) / 2) - this.player.xPosition * scale, ((this.getHeight() - (GAME_HEIGHT) * scale) / 2) - this.player.yPosition * scale);
		g.clipRect(0, 0, GAME_WIDTH * scale, GAME_HEIGHT * scale);
		screen.clear(0xffffff);
		
		if (world != null) {
			int xScroll = (player.xPosition);
			int yScroll = (player.yPosition);
			world.render(screen, xScroll, yScroll);
		}
		
		BufferedImage image = createCompatibleBufferedImage(screen.getBufferedImage());
		g.drawImage(image, 0, 0, GAME_WIDTH * scale, GAME_HEIGHT * scale, null);
	}
	
	public WindowListener getWindowListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				GameComponent.this.stop();
			}
		};
	}
	
	public static void main(String[] args) {
		GameComponent game = new GameComponent();
		JFrame frame = new JFrame(GAME_TITLE);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(game);
		frame.setContentPane(panel);
		frame.pack();
		frame.setResizable(false);
		Insets inset = frame.getInsets();
		frame.setSize(new Dimension(inset.left + inset.right + GAME_WIDTH * game.scale, inset.top + inset.bottom + GAME_HEIGHT * game.scale));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(game.getWindowListener());
		frame.setVisible(true);
		
		game.start();
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
	//---------------------------------
	//Other methods
}
