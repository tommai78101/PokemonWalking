package editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import abstracts.Tile;

public class DrawingBoard extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private LevelEditor editor;
	
	private BufferedImage image;
	private int[] tiles;
	private int[] pixels;
	private int bitmapWidth, bitmapHeight;
	private int offsetX, offsetY;
	private int mouseOnTileX, mouseOnTileY;
	
	public DrawingBoard(final LevelEditor editor) {
		super();
		this.editor = editor;
		offsetX = offsetY = 0;
	}
	
	@Override
	public void run() {
		while (editor.running) {
			tick();
			render();
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
			}
		}
	}
	
	@Override
	public void setSize(int width, int height) {
		int w = width * Tile.WIDTH;
		int h = height * Tile.HEIGHT;
		Dimension size = new Dimension(w, h);
		//this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		this.validate();
		this.editor.validate();
	}
	
	public void setImageSize(int w, int h) {
		if (w == 0 || h == 0)
			return;
		if (image != null) {
			image.flush();
			image = null;
		}
		tiles = new int[w * h];
		image = new BufferedImage(w * Tile.WIDTH, h * Tile.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		for (int j = 0; j < image.getHeight(); j++) {
			for (int i = 0; i < image.getWidth(); i++) {
				if (i % Tile.WIDTH == 0 || j % Tile.HEIGHT == 0)
					pixels[j * image.getWidth() + i] = 0;
				else
					pixels[j * image.getWidth() + i] = -1;
			}
		}
		offsetX = offsetY = 0;
		editor.input.dx = editor.input.dy = 0;
		bitmapWidth = w;
		bitmapHeight = h;
	}
	
	public void newImage() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JTextField widthField = new JTextField("10");
				JTextField heightField = new JTextField("10");
				JPanel panel = new JPanel(new GridLayout(1, 2));
				panel.add(new JLabel("  Width:"));
				panel.add(widthField);
				panel.add(new JLabel("  Height:"));
				panel.add(heightField);
				int result = JOptionPane.showConfirmDialog(null, panel, "Create New Area", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					setImageSize(Integer.valueOf(widthField.getText()), Integer.valueOf(heightField.getText()));
				}
			}
		});
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			bs = this.getBufferStrategy();
		}
		
		//		else if (editor.input.isDrawing()) {
		//			int px = editor.input.mouseX % (dx - image.getWidth());
		//			int py = editor.input.mouseY / (dx - image.getHeight());
		//			Iterator<Data> it = editor.getResourceFilePaths().iterator();
		//			for (; it.hasNext();) {
		//				Data d = it.next();
		//				if (d.editorID == editor.controlPanel.getSelectedData().editorID) {
		//					tiles[py * bitmapWidth + px] = 0xFF00000;//d.editorID;
		//				}
		//			}
		//		}
		
		if (tiles != null) {
			for (int j = 0; j < tiles.length; j++) {
				if (bitmapWidth == 0)
					break;
				int w = j % bitmapWidth;
				int h = j / bitmapWidth;
				
				//				for (Iterator<Data> it = editor.getResourceFilePaths().iterator(); it.hasNext();) {
				//					Data temp = it.next();
				//					if (temp.editorID == tiles[j]) {
				//						d = temp;
				//						break;
				//					}
				//				}
				Data d = editor.controlPanel.buttonCache.get(tiles[j]);
				if (d == null)
					break;
				if (d.image == null)
					break;
				Image img = d.image;
				BufferedImage bimg;
				if (img instanceof BufferedImage)
					bimg = (BufferedImage) img;
				else {
					bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics g = bimg.getGraphics();
					g.drawImage(img, 0, 0, null);
					g.dispose();
				}
				//				int[] px = bimg.getRGB(0, 0, img.getWidth(null), img.getHeight(null), null, 0, img.getWidth(null));
				//				for (int yy = h * Tile.HEIGHT; yy < (h + 1) * Tile.HEIGHT; yy++) {
				//					for (int xx = w * Tile.WIDTH; xx < (w + 1) * Tile.WIDTH; xx++) {
				//						try {
				//							this.pixels[yy * (bitmapWidth * Tile.WIDTH) + xx] = px[yy * Tile.WIDTH + xx];
				//						}
				//						catch (Exception e)
				//						{
				//						}
				//					}
				//				}
				
				Graphics g = this.image.getGraphics();
				g.setColor(Color.white);
				g.fillRect(w * Tile.WIDTH, h * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT);
				g.drawImage(bimg, w * Tile.WIDTH, h * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, null);
				g.dispose();
			}
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.translate(-offsetX, -offsetY);
		
		//		g.setColor(Color.LIGHT_GRAY);
		//		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (image != null) {
			//g.drawImage(image, (this.getWidth() - image.getWidth()) / 2, (this.getHeight() - image.getHeight()) / 2, null);
			g.drawImage(image, 0, 0, null);
		}
		
		g.dispose();
		bs.show();
	}
	
	public void tick() {
		if (editor.input.isDragging()) {
			offsetX = editor.input.dx;
			offsetY = editor.input.dy;
		}
		
		if (editor.input.isDrawing()) {
			this.mouseOnTileX = offsetX + editor.input.mouseX;
			if (this.mouseOnTileX < 0)
				return;
			if (this.mouseOnTileX >= bitmapWidth * Tile.WIDTH)
				return;
			this.mouseOnTileY = offsetY + editor.input.mouseY;
			if (this.mouseOnTileY < 0)
				return;
			if (this.mouseOnTileY >= bitmapHeight * Tile.HEIGHT)
				return;
			Data d = editor.controlPanel.getSelectedData();
			if (d != null)
				tiles[this.getMouseTileY() * bitmapWidth + this.getMouseTileX()] = d.editorID;
			editor.input.forceCancelDrawing();
		}
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	public int getMouseTileX() {
		return this.mouseOnTileX / Tile.WIDTH;
	}
	
	public int getMouseTileY() {
		return this.mouseOnTileY / Tile.HEIGHT;
	}
	
	public BufferedImage getMapImage() {
		BufferedImage buffer = new BufferedImage(bitmapWidth, bitmapHeight, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < tiles.length; i++) {
			pixels[i] = tiles[i];
		}
		return buffer;
	}
}
