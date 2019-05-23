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
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
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
	private int[] triggers;
	private int bitmapWidth, bitmapHeight;
	private int offsetX, offsetY;
	private int mouseOnTileX, mouseOnTileY;
	
	public DrawingBoard(final LevelEditor editor, int width, int height) {
		super();
		this.editor = editor;
		offsetX = offsetY = 0;
		
		int w = width * Tile.WIDTH;
		int h = height * Tile.HEIGHT;
		Dimension size = new Dimension(w, h);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		this.validate();
		editor.validate();
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
			try {
				render();
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		triggers = new int[w * h];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = 0;
			tilesEditorID[i] = 0;
			triggers[i] = 0;
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
		bitmapWidth = w;
		bitmapHeight = h;
		offsetX = -((this.getWidth() - (w * Tile.WIDTH)) / 2);
		offsetY = -((this.getHeight() - (h * Tile.HEIGHT)) / 2);
		editor.input.offsetX = offsetX;
		editor.input.offsetY = offsetY;
		
	}
	
	public void newImage() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JTextField widthField;
				JTextField heightField;
				int result;
				do {
					widthField = new JTextField("10");
					heightField = new JTextField("10");
					JPanel panel = new JPanel(new GridLayout(1, 2));
					panel.add(new JLabel("  Width:"));
					panel.add(widthField);
					panel.add(new JLabel("  Height:"));
					panel.add(heightField);
					result = JOptionPane.showConfirmDialog(null, panel, "Create New Area", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				}
				while (Integer.valueOf(widthField.getText()) <= 0 || Integer.valueOf(heightField.getText()) <= 0);
				if (result == JOptionPane.OK_OPTION) {
					setImageSize(Integer.valueOf(widthField.getText()), Integer.valueOf(heightField.getText()));
				}
			}
		});
	}
	
	public void newImage(final int x, final int y) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setImageSize(x, y);
			}
		});
	}
	
	public void render() throws Exception {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}
		hoverOver();
		switch (EditorConstants.metadata) {
			case Pixel_Data: {
				if (tilesEditorID != null) {
					for (int j = 0; j < tilesEditorID.length; j++) {
						if (bitmapWidth <= 0)
							break;
						if (bitmapHeight <= 0)
							break;
						int w = j % bitmapWidth;
						int h = j / bitmapWidth;
						
						Map.Entry<Integer, Data> entry = EditorConstants.getInstance().getDatas().get(tilesEditorID[j]);
						Data data = entry.getValue();
						if (data == null) {
							break;
						}
						if (data.image == null) {
							tiles[j] = 0;
							tilesEditorID[j] = 0;
							continue;
						}
						if (image == null)
							return;
						Graphics g = this.image.getGraphics();
						BufferedImage bimg = new BufferedImage(data.image.getIconWidth(), data.image.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics gB = bimg.getGraphics();
						// TODO: Area Type ID must be included.
						gB.drawImage(data.image.getImage(), 0, 0, null);
						gB.dispose();
						
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
				break;
			}
			case Triggers: {
				if (triggers != null) {
					for (int k = 0; k < triggers.length; k++) {
						if (bitmapWidth <= 0)
							break;
						if (bitmapHeight <= 0)
							break;
						
						Trigger trigger = null;
						try {
							trigger = EditorConstants.getInstance().getTriggers().get(triggers[k]);
						}
						catch (Exception e) {
							// Eraser.
							trigger = EditorConstants.getInstance().getTriggers().get(0);
							
						}
						Data data = null;
						try {
							Map.Entry<Integer, Data> entry = EditorConstants.getInstance().getDatas().get(tilesEditorID[k]);
							data = entry.getValue();
						}
						catch (Exception e) {
							data = EditorConstants.getInstance().getDatas().get(0).getValue();
						}
						
						if (trigger == null)
							break;
						int w = k % bitmapWidth;
						int h = k / bitmapWidth;
						
						Graphics g = this.image.getGraphics();
						
						if ((triggers[k] & 0xFFFF) != 0) {
							g.setColor(Color.cyan);
							g.fillRect(w * Tile.WIDTH, h * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT);
						}
						else {
							// g.drawImage(data.image.getImage(), w * Tile.WIDTH, h * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, null);
							Graphics gD = this.image.getGraphics();
							BufferedImage bimg = new BufferedImage(data.image.getIconWidth(), data.image.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
							Graphics gB = bimg.getGraphics();
							// TODO: Area Type ID must be included.
							gB.drawImage(data.image.getImage(), 0, 0, null);
							gB.dispose();
							
							if (data.areaTypeIncluded) {
								switch (data.areaTypeIDType) {
									case ALPHA:
									default:
										this.setBiomeTile((tiles[k] >> 24) & 0xFF, g);
										break;
									case RED:
										this.setBiomeTile((tiles[k] >> 16) & 0xFF, g);
										break;
									case GREEN:
										this.setBiomeTile((tiles[k] >> 8) & 0xFF, g);
										break;
									case BLUE:
										this.setBiomeTile(tiles[k] & 0xFF, g);
										break;
								}
							}
							else
								gD.setColor(Color.white);
							g.fillRect(w * Tile.WIDTH, h * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT);
							g.drawImage(bimg, w * Tile.WIDTH, h * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, null);
							g.dispose();
						}
						g.dispose();
					}
				}
				break;
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
		switch (EditorConstants.metadata) {
			case Pixel_Data: {
				if (editor.input.isDragging()) {
					offsetX = editor.input.offsetX;
					offsetY = editor.input.offsetY;
				}
				else if (editor.input.isDrawing()) {
					this.mouseOnTileX = offsetX + editor.input.drawingX;
					if (this.mouseOnTileX < 0)
						return;
					if (this.mouseOnTileX >= bitmapWidth * Tile.WIDTH)
						return;
					this.mouseOnTileY = offsetY + editor.input.drawingY;
					if (this.mouseOnTileY < 0)
						return;
					if (this.mouseOnTileY >= bitmapHeight * Tile.HEIGHT)
						return;
					Data d = editor.controlPanel.getSelectedData();
					if (d != null) {
						setDataProperties(d);
					}
					editor.validate();
				}
				break;
			}
			case Triggers: {
				if (editor.input.isDragging()) {
					offsetX = editor.input.offsetX;
					offsetY = editor.input.offsetY;
				}
				else if (editor.input.isDrawing()) {
					this.mouseOnTileX = editor.input.offsetX + editor.input.drawingX;
					this.mouseOnTileY = editor.input.offsetY + editor.input.drawingY;
					if (this.mouseOnTileX < 0 || this.mouseOnTileX >= bitmapWidth * Tile.WIDTH)
						return;
					if (this.mouseOnTileY < 0 || this.mouseOnTileY >= bitmapHeight * Tile.HEIGHT)
						return;
					Trigger t = editor.controlPanel.getSelectedTrigger();
					if (t != null) {
						int x = this.getMouseTileX();
						int y = this.getMouseTileY();
						t.setTriggerPositionX((byte) x);
						t.setTriggerPositionY((byte) y);
						
						int i = y * bitmapWidth + x;
						this.triggers[i] = t.getDataValue();
					}
					editor.validate();
				}
				break;
			}
		}
	}
	
	private void setDataProperties(Data d) {
		switch (d.alpha) {
			case 0x02: {
				TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
				int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
				Data temp = null;
				for (Map.Entry<Integer, Data> entry : EditorConstants.getInstance().getDatas()) {
					if (panel.dataValue == entry.getKey()) {
						temp = entry.getValue();
						break;
					}
				}
				if (temp != null) {
					tiles[i] = panel.dataValue;
					tilesEditorID[i] = temp.editorID;
				}
				else {
					tiles[i] = (d.alpha << 24) | (d.red << 16) | panel.dataValue & 0xFFFF;
					tilesEditorID[i] = d.editorID;
				}
				break;
			}
			case 0x03: {
				switch (d.red) {
					case 0x05: { // Sign
						TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
						int green = (panel.dataValue >> 8) & 0xFF;
						int blue = (panel.dataValue & 0xFF);
						int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
						tiles[i] = (d.alpha << 24) | (d.red << 16) | (green << 8) | blue;
						tilesEditorID[i] = d.editorID;
						break;
					}
					default: {
						defaultTileProperties(d);
						break;
					}
				}
				break;
			}
			case 0x04: // Warp point
			case 0x05: // Sector point
			case 0x09: // Door
			case 0x0B: // Carpet
			case 0x0C: { // Carpet
				manualInputTileProperties(d);
				break;
			}
			case 0x08: { // House
				switch (d.red) {
					case 0x0B: // Left roof
					case 0x0C: // Middle roof
					case 0x0D: {// Right roof
						TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
						int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
						tiles[i] = (d.alpha << 24) | (d.red << 16) | panel.dataValue & 0xFFFF;
						tilesEditorID[i] = d.editorID;
						break;
					}
					default:
						defaultTileProperties(d);
						break;
				}
				break;
			}
			case 0x0A: { // Items
				TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
				int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
				tiles[i] = (d.alpha << 24) | panel.dataValue & 0xFFFFFF;
				tilesEditorID[i] = d.editorID;
				break;
			}
			case 0x0D: { // Default starting point
				int green = d.green;
				int blue = d.blue;
				if (d.greenByEditor)
					green = this.getMouseTileX();
				if (d.blueByEditor)
					blue = this.getMouseTileY();
				int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
				tiles[i] = (d.alpha << 24) | (d.red << 16) | (green << 8) | blue;
				tilesEditorID[i] = d.editorID;
				TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
				panel.greenInputField.setText(Integer.toString(green));
				panel.blueInputField.setText(Integer.toString(blue));
				panel.greenField.setText(Integer.toString(green));
				panel.blueField.setText(Integer.toString(blue));
				panel.validate();
				break;
			}
			default: {
				defaultTileProperties(d);
				break;
			}
		}
	}
	
	private void defaultTileProperties(Data d) {
		TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
		int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
		Data temp = null;
		for (Map.Entry<Integer, Data> entry : EditorConstants.getInstance().getDatas()) {
			if (panel.dataValue == entry.getKey()) {
				temp = entry.getValue();
				break;
			}
		}
		if (temp != null) {
			tiles[i] = panel.dataValue;
			tilesEditorID[i] = temp.editorID;
		}
		else {
			tiles[i] = d.getColorValue();
			tilesEditorID[i] = d.editorID;
		}
	}
	
	private void manualInputTileProperties(Data d) {
		TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
		int i = this.getMouseTileY() * bitmapWidth + this.getMouseTileX();
		tiles[i] = panel.dataValue;
		tilesEditorID[i] = d.editorID;
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
	
	public int getBitmapWidth() {
		return this.bitmapWidth;
	}
	
	public int getBitmapHeight() {
		return this.bitmapHeight;
	}
	
	public BufferedImage getMapImage() {
		if (bitmapWidth * bitmapHeight == 0)
			return null;
		
		int size = 0;
		int h = 0;
		
		if (this.triggers == null) {
			this.triggers = new int[1];
			this.triggers[0] = 0;
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i : this.triggers) {
			if (i != 0)
				list.add(Integer.valueOf(i));
		}
		size = list.size();
		h = (size / bitmapHeight) + 1;
		
		BufferedImage buffer = new BufferedImage(bitmapWidth, bitmapHeight + h, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
		
		int index = 0;
		int width = 0;
		pixels[0] = list.size();
		for (int i = 0; i < list.size(); i++) {
			width = i + 1;
			pixels[width + (index * bitmapHeight)] = list.get(i).intValue();
			if (width >= bitmapHeight) {
				index++;
				width -= bitmapHeight;
			}
		}
		
		for (int iterator = 0; iterator < tiles.length; iterator++) {
			pixels[bitmapWidth * h + iterator] = tiles[iterator];
		}
		return buffer;
	}
	
	public boolean hasBitmap() {
		return (this.bitmapHeight * this.bitmapWidth != 0);
	}
	
	public void openMapImage(BufferedImage image) {
		try {
			int[] srcTiles = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			int triggerCount = srcTiles[0];
			int triggerRows = (triggerCount / image.getWidth()) + 1;
			this.setImageSize(image.getWidth(), (image.getHeight() - triggerRows));
			if (this.triggers != null)
				this.triggers = null;
			this.triggers = new int[image.getWidth() * (image.getHeight() - triggerRows)];
			for (int i = 0; i < triggerCount; i++) {
				int x = (srcTiles[i + 1] >> 24) & 0xFF;
				int y = (srcTiles[i + 1] >> 16) & 0xFF;
				this.triggers[y * image.getWidth() + x] = srcTiles[i + 1];
			}
			for (int i = 0; i < srcTiles.length - (triggerRows * image.getWidth()); i++)
				this.tiles[i] = srcTiles[i + (triggerRows * image.getWidth())];
			ArrayList<Map.Entry<Integer, Data>> list = EditorConstants.getInstance().getDatas();
			for (int i = 0; i < tiles.length; i++) {
				int alpha = ((tiles[i] >> 24) & 0xFF);
				for (int j = 0; j < list.size(); j++) {
					Map.Entry<Integer, Data> entry = list.get(j);
					Data d = entry.getValue();
					switch (alpha) {
						case 0x01: // Path
						case 0x02: // Ledges
						case 0x03: // Obstacles
						case 0x06: // Stairs
						case 0x07: // Water
						case 0x08: // House
							// Extended Tile IDs are used to differentiate tiles.
							if (alpha == Integer.valueOf(d.alpha)) {
								int red = ((tiles[i] >> 16) & 0xFF);
								if (red == Integer.valueOf(d.red)) {
									tilesEditorID[i] = d.editorID;
								}
							}
							continue;
						case 0x05: {// Area Zone
							// Extended Tile IDs are used to differentiate tiles.
							if (alpha == Integer.valueOf(d.alpha)) {
								int blue = tiles[i] & 0xFF;
								if (blue == d.blue) {
									tilesEditorID[i] = d.editorID;
									break;
								}
							}
							continue;
						}
						default: {
							// Alpha value is only used.
							if (alpha == Integer.valueOf(d.alpha)) {
								tilesEditorID[i] = d.editorID;
								break;
							}
							continue;
						}
					}
				}
			}
		}
		catch (NegativeArraySizeException e) {
			JOptionPane.showMessageDialog(null, "Incorrect file format. The file does not contain necessary metadata.");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error loading the file. Unable to pinpoint the cause.");
		}
	}
	
	private void hoverOver() {
		try {
			int w = 0;
			int h = 0;
			
			// try...catch for X position
			try {
				w = (editor.input.offsetX + editor.input.mouseX) / Tile.WIDTH;
			}
			catch (Exception e) {
				w = (editor.input.offsetX + editor.input.mouseX) / (LevelEditor.WIDTH * LevelEditor.SIZE);
			}
			
			// try catch for Y position
			try {
				h = (editor.input.offsetY + editor.input.mouseY) / Tile.HEIGHT;
			}
			catch (Exception e) {
				h = (editor.input.offsetY + editor.input.mouseY) / (LevelEditor.WIDTH * LevelEditor.SIZE);
			}
			
			// checks for mouse hoving above triggers
			int[] list = null;
			switch (EditorConstants.metadata) {
				case Pixel_Data:
				default:
					list = tiles;
					break;
				case Triggers:
					list = triggers;
					break;
			}
			int value = list[h * bitmapWidth + w];
			// Show trigger IDs and stuffs.
			TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
			panel.alphaField.setText(Integer.toString((value >> 24) & 0xFF));
			panel.redField.setText(Integer.toString((value >> 16) & 0xFF));
			panel.greenField.setText(Integer.toString((value >> 8) & 0xFF));
			panel.blueField.setText(Integer.toString(value & 0xFF));
			panel.validate();
		}
		catch (Exception e) {
			TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
			panel.alphaField.setText("");
			panel.redField.setText("");
			panel.greenField.setText("");
			panel.blueField.setText("");
			panel.validate();
		}
	}
}