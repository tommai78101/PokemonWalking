package editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = -8739477187675627751L;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 144;
	public static final int SIZE = 3;
	
	//	private static boolean running;
	//	private BufferedImage image;
	//	private int[] pixels;
	//	private Block block;
	
	private ControlPanel controlPanel;
	private FileControl fileControlPanel;
	
	public LevelEditor(String name) {
		super(name);
		
		Dimension size = new Dimension(WIDTH * SIZE, HEIGHT * SIZE);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		
		this.setLayout(new BorderLayout());
		
		fileControlPanel = new FileControl();
		controlPanel = new ControlPanel();
		
		this.add(fileControlPanel, BorderLayout.NORTH);
		this.add(controlPanel, BorderLayout.WEST);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		
		//		this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		//		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		//		block = new Block();
	}
	
	//	public void start() {
	//		//		running = true;
	//		Thread thread = new Thread(this);
	//		thread.setName("Level Editor Loop Thread");
	//		thread.start();
	//	}
	
	//	@Override
	//	public void run() {
	//		//		long lastTime = System.nanoTime();
	//		//		double unprocessed = 0.0;
	//		//		final double nsPerTick = 1000000000.0 / 30.0;
	//		//		long now;
	//		//		while (running) {
	//		//			
	//		//			now = System.nanoTime();
	//		//			unprocessed += (now - lastTime) / nsPerTick;
	//		//			lastTime = now;
	//		//			
	//		//			if (unprocessed >= 10.0)
	//		//				unprocessed = 1.0;
	//		//			if (unprocessed <= 0 || Double.isNaN(unprocessed))
	//		//				unprocessed = 1.0;
	//		//			
	//		//			while (unprocessed >= 1.0) {
	//		//				tick();
	//		//				render();
	//		//				unprocessed -= 1.0;
	//		//			}
	//		//			
	//		//			try {
	//		//				Thread.sleep(1);
	//		//			}
	//		//			catch (InterruptedException e) {
	//		//			}
	//		//		}
	//	}
	
	//	public void tick() {
	//		
	//	}
	
	//	public void render() {
	//		BufferStrategy bs = this.getBufferStrategy();
	//		if (bs == null) {
	//			this.createBufferStrategy(3);
	//			bs = this.getBufferStrategy();
	//		}
	//		
	////		for (int i = 0; i < pixels.length; i++) {
	////			pixels[i] = 0x44FF13AA;
	////		}
	//		
	////		this.controlPanel.render(pixels);
	////		block.render(pixels);
	//		
	//		Graphics g = bs.getDrawGraphics();
	////		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
	//		g.dispose();
	//		bs.show();
	//	}
	
	//	public static void stop() {
	//		running = false;
	//	}
	
	public static void main(String[] args) {
		LevelEditor le = new LevelEditor("Level Editor (Hobby)");
		//		le.start();
	}
}