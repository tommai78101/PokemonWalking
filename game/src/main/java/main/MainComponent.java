/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package main;

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
import javax.swing.WindowConstants;

import screen.Scene;
import utility.Debug;

public class MainComponent extends Canvas implements Runnable {

	// DEBUG: Useful for reducing the number of times to swap between the game application window and Eclipse.
	private static final boolean DebugMode = false;

	private static final long serialVersionUID = 1L;

	public static final String GAME_TITLE = "Pokémon Walking Algorithm (Hobby) by tom_mai78101";
	public static final int GAME_WIDTH = 160;
	public static final int GAME_HEIGHT = 144;
	public static final int GAME_SCALE = 3;

	public static int COMPONENT_WIDTH;
	public static int COMPONENT_HEIGHT;

	// -----------------------
	private final Scene scene;
	private static Game game;
	private JFrame frame;
	// -----------------------

	private InputHandler inputHandler;

	// -----------------------
	private boolean running;

	// private int fps;

	// -----------------------

	/**
	 * Constructor of MainComponent.
	 * 
	 * This is what the main() method executes in order to start the game.
	 * 
	 */
	public MainComponent(JFrame frame) {
		this.running = false;
		this.scene = new Scene(MainComponent.GAME_WIDTH, MainComponent.GAME_HEIGHT);
		this.scene.loadResources();
		this.frame = frame;
	}

	/**
	 * Intializes the game and loads all required assets.
	 * 
	 * This method can only be run once throughout the entire application life cycle. Otherwise, there will be errors and unexpected glitches.
	 * 
	 * @return Nothing.
	 */
	public void init() {
		// init(): Place where I get assets from.

		this.requestFocus();
		MainComponent.COMPONENT_HEIGHT = this.getHeight();
		MainComponent.COMPONENT_WIDTH = this.getWidth();

		// Game loading
		// We pass the BaseScreen variable as a parameter, acting as an output.
		MainComponent.game = new Game(this);

		// Input Handling
		this.inputHandler = new InputHandler(Game.keys);
		this.addKeyListener(this.inputHandler);

		// world = new TestWorld(Art.testArea);

		// this.world.addEntity(player);
		// this.world.setEntityStartingPosition(this.player);
	}

	/**
	 * Starts the game execution.
	 * 
	 * After MainComponent object has been loaded, this method is used to initiate game initialization and execution of the entire game.
	 * 
	 * @return Nothing.
	 */
	public void start() {
		this.running = true;
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
	 */
	public void stop() {
		this.running = false;
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long lastTimer = System.currentTimeMillis();
		double unprocessed = 0.0;
		int frames = 0;
		int tick = 0;
		// boolean shouldRender = false;
		final double nsPerTick = 1000000000.0 / 30.0;
		try {
			this.init();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

		while (this.running) {
			// For debugging, this is disabled.
			// shouldRender = false;
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

			// Limits the number of ticks per unprocessed frame.
			int toTick = tick;
			if (tick > 0 && tick < 3)
				toTick = 1;
			if (tick > 7)
				toTick = 7;

			for (int i = 0; i < toTick; i++) {
				tick--;
				this.tick();
				this.render();
				frames++;
				// For debugging, this is disabled.
				// shouldRender = true;
			}

			// For debugging, this is disabled.

			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
				Debug.error("Something is wrong... No response.");
				this.requestFocus();
			}

			if (System.currentTimeMillis() - lastTimer > 1000) {
				lastTimer += 1000;
				MainComponent.this.frame.setTitle(MainComponent.GAME_TITLE + " - FPS: " + Integer.toString(frames));
				frames = 0;
			}
		}
		// Game loop has exited, everything stops from here on out.
	}

	/**
	 * Updates the game.
	 * 
	 * This method is synchronized for no reason other than to avoid the main thread from fighting from the Game Thread. It is possible that this is unnecessary. Please provide any feedback about this.
	 * 
	 * @return Nothing.
	 */
	public synchronized void tick() {
		// keys.tick();
		// world.tick();
		MainComponent.game.tick();
	}

	/**
	 * Renders the game to the screen.
	 * 
	 * The game uses software renderer, since it is modifying the BufferedImage acting as a back buffer. This method is synchronized for no reason other than to avoid the main thread from fighting from the Game Thread. It is possible that this is unnecessary. Please provide any feedback about this.
	 * 
	 * @return Nothing.
	 */
	public synchronized void render() {
		BufferStrategy bufferStrategy = this.getBufferStrategy();
		if (bufferStrategy == null) {
			// Create new buffers before returning, if no buffers exist yet.
			this.createBufferStrategy(2);
			return;
		}

		// Get graphics variable.
		Graphics g = bufferStrategy.getDrawGraphics();

		// Background border
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		// screen.clear(0xA4E767);

		// World rendering.
		// if (world != null) {
		// int xScroll = player.getX() - screen.getWidth() / 2;
		// int yScroll = player.getY() - screen.getHeight() / 2;
		// world.render(screen, xScroll, yScroll);
		// }

		// game.setScrollOffset(GAME_WIDTH / 2, (GAME_HEIGHT + Tile.HEIGHT) / 2);
		MainComponent.game.setCameraRelativeToArea(MainComponent.GAME_WIDTH / 2, MainComponent.GAME_HEIGHT / 2);
		MainComponent.game.render(g);

		// Key input debugging only.
		// debugKeys(g, 0, 30);

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
	 */
	public WindowListener getWindowListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				MainComponent.this.stop();
			}
		};
	}

	// -------------------------------
	// Private methods:

	/**
	 * Creates a BufferedImage that is compatible with the graphics card the player is currently using.
	 * 
	 * The developer is unsure of its benefits. Please provide feedback if you have any comments about this.
	 * 
	 * @param BufferedImage
	 *            Takes a BufferedImage that is to make it compatible with the graphics card built in the computer the game is running on.
	 * @return BufferedImage The BufferedImage used for the Graphics object to blit to the screen.
	 */
	public static final BufferedImage createCompatibleBufferedImage(BufferedImage image) {
		GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			.getDefaultConfiguration();
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;
		BufferedImage newImage = gfx_config.createCompatibleImage(
			image.getWidth(), image.getHeight(),
			image.getTransparency()
		);
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
		at.scale(MainComponent.GAME_SCALE, MainComponent.GAME_SCALE);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		after = scaleOp.filter(before, after);
		return after;
	}

	public Scene getScene() {
		return this.scene;
	}

	// ---------------------------------
	// Other methods

	public static final Game getGame() {
		return MainComponent.game;
	}

	// ---------------------------------
	// Main execution method

	public static void main(String[] args) {
		Debug.log("Game is now loading, it will take a while.");

		JFrame frame = new JFrame(MainComponent.GAME_TITLE);

		MainComponent component = new MainComponent(frame);
		frame.getContentPane().add(component);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(component.getWindowListener());
		frame.setResizable(false);
		frame.setAlwaysOnTop(MainComponent.DebugMode);
		frame.pack();

		//Setting the frame size must come after pack() is called.
		Insets inset = frame.getInsets();
		frame.setSize(
			new Dimension(
				inset.left + inset.right + MainComponent.GAME_WIDTH * MainComponent.GAME_SCALE,
				inset.top + inset.bottom + MainComponent.GAME_HEIGHT * MainComponent.GAME_SCALE
			)
		);

		//Set visibility must come after pack() and setSize() are called.
		frame.setVisible(true);

		Debug.log("Game is now starting.");
		component.start();
	}
}