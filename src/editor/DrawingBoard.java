/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

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
		// this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		this.validate();
		this.editor.validate();
	}

	public void setImageSize(int w, int h) {
		if (w <= 0 || h <= 0)
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
		System.out.println("I have pressed \"NEW\" button. Pop-up should appear soon.");
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JTextField widthField;
				JTextField heightField;
				int result;
				do {
					System.out.println("Setting the default values for width and height to 10");
					widthField = new JTextField("10");
					heightField = new JTextField("10");
					System.out.println("Adding the width and height fields to the panel pane.");
					JPanel panel = new JPanel(new GridLayout(1, 2));
					panel.add(new JLabel("  Width:"));
					panel.add(widthField);
					panel.add(new JLabel("  Height:"));
					panel.add(heightField);
					System.out.println("Now showing the confirmation dialog to me. A pop-up should come up anytime soon.");
					result = JOptionPane.showConfirmDialog(null, panel, "Create New Area", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				}
				while (Integer.valueOf(widthField.getText()) <= 0 || Integer.valueOf(heightField.getText()) <= 0);
				if (result == JOptionPane.OK_OPTION) {
					System.out.println("Pop up has closed. Width and Height are now defined, now creating the image.");
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
				// TODO: Area Type ID must be included.
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
			case 0x00: // grass
				g.setColor(EditorConstants.GRASS_GREEN);
				break;
			case 0x01: // Road
				g.setColor(EditorConstants.ROAD_WHITE);
				break;
			case 0x02: // Dirt
			case 0x04: // dirt
				g.setColor(EditorConstants.DIRT_SIENNA);
				break;
			case 0x03: // Water
				g.setColor(EditorConstants.WATER_BLUE);
				break;
			case 0x05: // Door/Carpet
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
				tiles[i] = (panel.getAlpha() << 24) | (panel.getRed() << 16) | (panel.getGreen() << 8) | panel.getBlue();
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
		for (int i = 0; i < srcTiles.length; i++)
			tiles[i] = srcTiles[i];
		List<Map.Entry<Integer, Data>> list = EditorConstants.getInstance().getSortedTileMap();
		for (int i = 0; i < tiles.length; i++) {
			int alpha = ((srcTiles[i] >> 24) & 0xFF);
			for (int j = 0; j < list.size(); j++) {
				Map.Entry<Integer, Data> entry = list.get(j);
				Data d = entry.getValue();
				switch (alpha) {
					case 0x01: // Path
					case 0x02: // Ledges
					case 0x06: // Stairs
					case 0x07: // Water
					case 0x09: // House
						// Extended Tile IDs are used to differenate tiles.
						if (alpha == Integer.valueOf(d.alpha)) {
							int red = ((srcTiles[i] >> 16) & 0xFF);
							if (red == Integer.valueOf(d.red)) {
								tilesEditorID[i] = d.editorID;
								break;
							}
						}
						continue;
					case 0x05: // Area Zone
						// Extended Tile IDs are used to differenate tiles.
						if (alpha == Integer.valueOf(d.alpha)) {
							int blue = srcTiles[i] & 0xFF;
							if (blue == d.blue) {
								tilesEditorID[i] = d.editorID;
								break;
							}
						}
						continue;
					default:
						// Alpha value is only used.
						if (alpha == Integer.valueOf(d.alpha))
							tilesEditorID[i] = d.editorID;
						break;
				}
			}

			// for (int i = 0; i< srcTiles.length; i++){
			// tiles[i] = srcTiles[i];
			// int alpha = (srcTiles[i] >> 24)&0xFF;
			// int red = (srcTiles[i]>>16)&0xFF;
			// int blue = srcTiles[i]&0xFF;
			// for (int j=0; j <list.size(); j++){
			// Map.Entry<Integer, Data> entry = list.get(j);
			// Data data = entry.getValue();
			// if (alpha == Integer.valueOf(data.alpha)) {
			// if (red == Integer.valueOf(data.red) || blue == Integer.valueOf(data.blue)){
			// //Target Area
			// tilesEditorID[i] = data.editorID;
			// break;
			// }
			// }
			// }
			// }
		}
	}
}