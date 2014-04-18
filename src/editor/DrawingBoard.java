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
import java.util.List;
import java.util.Map;
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
	private int[] tilesEditorID;
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
		long now, lastTime = System.nanoTime();
		double unprocessed = 0.0;
		final double nsPerTick = 1000000000.0 / 30.0;
		while (editor.running) {
			now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			if (unprocessed >= 40.0)
				unprocessed = 40.0;
			if (unprocessed <= 0.0)
				unprocessed = 1.0;
			
			while (unprocessed >= 1.0) {
				tick();
				unprocessed -= 1.0;
			}
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
		tilesEditorID = new int[w * h];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = 0;
			tilesEditorID[i] = 0;
		}
		image = new BufferedImage(w * Tile.WIDTH, h * Tile.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
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
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}
		if (tilesEditorID != null) {
			for (int j = 0; j < tilesEditorID.length; j++) {
				if (bitmapWidth <= 0)
					break;
				if (bitmapHeight <= 0)
					break;
				int w = j % bitmapWidth;
				int h = j / bitmapWidth;
				
				Data data = EditorConstants.getInstance().getTileMap().get(tilesEditorID[j]);
				if (data == null) {
					break;
				}
				if (data.image == null) {
					tiles[j] = 0;
					tilesEditorID[j] = 0;
					continue;
				}
				
				Image img = data.image;
				BufferedImage bimg;
				if (img instanceof BufferedImage)
					bimg = (BufferedImage) img;
				else {
					bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics g = bimg.getGraphics();
					g.drawImage(img, 0, 0, null);
					g.dispose();
				}
				
				Graphics g = this.image.getGraphics();
				//TODO: Area Type ID must be included.
				if (data.areaTypeIncluded) {
					switch (data.areaTypeIDType) {
						case ALPHA:
						default:
							this.setBiomeTile((tiles[j] >> 24) & 0xFF, g);
							break;
						case RED:
							this.setBiomeTile((tiles[j] >> 16) & 0xFF, g);
							break;
						case GREEN:
							this.setBiomeTile((tiles[j] >> 8) & 0xFF, g);
							break;
						case BLUE:
							this.setBiomeTile(tiles[j] & 0xFF, g);
							break;
					}
				}
				else
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
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
		g.dispose();
		bs.show();
	}
	
	private void setBiomeTile(final int toCompare, Graphics g) {
		switch (toCompare) {
			case 0x00: //grass
				g.setColor(EditorConstants.GRASS_GREEN);
				break;
			case 0x01: //Road
				g.setColor(EditorConstants.ROAD_WHITE);
				break;
			case 0x02: //Dirt
			case 0x04: //dirt
				g.setColor(EditorConstants.DIRT_SIENNA);
				break;
			case 0x03: //Water
				g.setColor(EditorConstants.WATER_BLUE);
				break;
			case 0x05: //Door/Carpet
			default:
				g.setColor(Color.white);
				break;
		}
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
			if (d != null) {
				TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
				int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
				tiles[i] = (
						panel.getAlpha() << 24) |
						(panel.getRed() << 16) |
						(panel.getGreen() << 8) |
						panel.getBlue();
				tilesEditorID[i] = d.editorID;
			}
			editor.input.forceCancelDrawing();
			editor.validate();
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
	
	public void openMapImage(BufferedImage image) {
		this.setImageSize(image.getWidth(), image.getHeight());
		int[] srcTiles = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		List<Map.Entry<Integer, Data>> list = EditorConstants.getInstance().getSortedTileMap();
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = srcTiles[i];
			DATA_COLOR_LOOP: for (Map.Entry<Integer, Data> entry : list) {
				Data d = entry.getValue();
				int color = (d.alpha << 24) | (d.red << 16) | (d.green << 8) | d.blue;
				if (tiles[i] == color) {
					tilesEditorID[i] = d.editorID;
					break DATA_COLOR_LOOP;
				}
			}
		}
	}
}
