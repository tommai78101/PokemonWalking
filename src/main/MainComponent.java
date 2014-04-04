package main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import resources.Art;
import screen.BaseScreen;

public class MainComponent extends Canvas implements Runnable {
	
	private static final long serialVersionUID = 1L;
	public static int GAME_WIDTH = 160;
	public static String GAME_TITLE = "Pokémon Walking Algorithm (Hobby) by tom_mai78101";
	public static int GAME_HEIGHT = 144;
	public static int GAME_SCALE = 3;
	
	//-----------------------
	private final BaseScreen screen;
	private Game game;
	//-----------------------
	
	private final Keys keys = new Keys();
	//Wrong input handler. Requires a rewrite.
	//private InputHandler inputHandler;
	private NewInputHandler inputHandler;
	
	//-----------------------	
	private boolean running;
	
	//private int fps;
	
	//-----------------------
	
	/**
	 * Constructor of MainComponent.
	 * 
	 * This is what the main() method executes in order to start
	 * the game.
	 * 
	 * */
	public MainComponent() {
		running = false;
		screen = new BaseScreen(GAME_WIDTH, GAME_HEIGHT);
		screen.loadResources();
	}
	
	/**
	 * Intializes the game and loads all required assets.
	 * 
	 * This method can only be run once throughout the entire application life cycle. Otherwise,
	 * there will be errors and unexpected glitches.
	 * 
	 * @return Nothing.
	 * */
	public void init() {
		//init(): Place where I get assets from.
		
		this.requestFocus();
		
		//Input Handling
		inputHandler = new NewInputHandler(keys);
		this.addKeyListener(inputHandler);
		
		//Game loading
		//We pass the BaseScreen variable as a parameter, acting as an output.
		game = new Game(screen, keys);
		game.load();
		//world = new TestWorld(Art.testArea);
		
		//this.world.addEntity(player);
		//this.world.setEntityStartingPosition(this.player);
	}
	
	/**
	 * Starts the game execution.
	 * 
	 * After MainComponent object has been loaded, this method is used to initiate game initialization
	 * and execution of the entire game.
	 * 
	 * @return Nothing.
	 * */
	public void start() {
		running = true;
		Thread thread = new Thread(this);
		thread.setName("Game Loop Thread");
		thread.start();
	}
	
	/**
	 * Stops the Game Thread.
	 * 
	 * This is used when the player presses the Close button.
	 * 
	 * @return Nothing.
	 * */
	public void stop() {
		running = false;
		game.save();
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		//long lastTimer = System.currentTimeMillis();
		double unprocessed = 0.0;
		//int frames = 0;
		int tick = 0;
		//boolean shouldRender = false;
		final double nsPerTick = 1000000000.0 / 30.0;
		try {
			init();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		while (running) {
			//For debugging, this is disabled.
			//shouldRender = false;
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			if (unprocessed >= 40.0)
				unprocessed = 1.0;
			if (unprocessed < 0.0 || Double.isNaN(unprocessed))
				unprocessed = 1.0;
			
			while (unprocessed >= 1.0) {
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
				render();
				//For debugging, this is disabled.
				//shouldRender = true;
			}
			
			//For debugging, this is disabled.
			//			if (shouldRender) {
			//				//frames++;
			//				//render();
			//			}
			
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
				System.out.println("Something is wrong... No response.");
				this.requestFocus();
			}
			
			//			if (System.currentTimeMillis() - lastTimer > 1000) {
			//				lastTimer += 1000;
			//				//fps = frames;
			//				//System.out.println("FPS: " + Integer.toString(fps));
			//				//frames = 0;
			//			}
		}
		//Game loop has exited, everything stops from here on out.
	}
	
	/**
	 * Updates the game.
	 * 
	 * This method is synchronized for no reason other than to avoid the main thread from fighting from the Game Thread.
	 * It is possible that this is unnecessary. Please provide any feedback about this.
	 * 
	 * @return Nothing.
	 * */
	public synchronized void tick() {
		//keys.tick();
		//world.tick();
		game.tick();
	}
	
	/**
	 * Renders the game to the screen.
	 * 
	 * The game uses software renderer, since it is modifying the BufferedImage acting as a back buffer. This method
	 * is synchronized for no reason other than to avoid the main thread from fighting from the Game Thread.
	 * It is possible that this is unnecessary. Please provide any feedback about this.
	 * 
	 * @return Nothing.
	 * */
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
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		//screen.clear(0xA4E767);
		
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
		
		//Key input debugging only.
		debugKeys(g, 0, 30);
		
		g.dispose();
		bufferStrategy.show();
	}
	
	/**
	 * Stops the Game Thread when the Close button has been pressed.
	 * 
	 * This is added to ensure the Game Thread exits safely.
	 * 
	 * @see WindowListener
	 * @see WindowAdapter
	 * */
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
	
	/**
	 * Debugs all inputs received from the player, and shows the result at the top left corner of the screen.
	 * 
	 * This will be removed some time in the future.
	 * 
	 * @param Graphics
	 *            The Graphics object that the BufferStrategy uses.
	 * @return Nothing.
	 * @see BufferStrategy
	 * @see Graphics
	 * */
	private void debugKeys(Graphics g, int x, int y) {
		g.setColor(Color.black);
		//The game uses 8f FONT when shown on the screen. It is scaled by GAME_SCALE.
		g.setFont(Art.font.deriveFont(Font.PLAIN, 24f));
		g.drawString("POKéMON", 200, y + 100);
		if (keys.up.isTappedDown)
			g.drawString("up tapped", x, y);
		else if (keys.up.isPressedDown)
			g.drawString("up pressed", x, y);
		
		if (keys.left.isTappedDown)
			g.drawString("left tapped", x, y);
		else if (keys.left.isPressedDown)
			g.drawString("left pressed", x, y);
		
		if (keys.down.isTappedDown)
			g.drawString("down tapped", x, y);
		else if (keys.down.isPressedDown)
			g.drawString("down pressed", x, y);
		
		if (keys.right.isTappedDown)
			g.drawString("right tapped", x, y);
		else if (keys.right.isPressedDown)
			g.drawString("right pressed", x, y);
	}
	
	/**
	 * Creates a BufferedImage that is compatible with the graphics card the player is currently using.
	 * 
	 * The developer is unsure of its benefits. Please provide feedback if you have any comments about this.
	 * 
	 * @param BufferedImage
	 *            Takes a BufferedImage that is to make it compatible with the graphics card built in the computer the
	 *            game is running on.
	 * @return BufferedImage The BufferedImage used for the Graphics object to blit to the screen.
	 * */
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