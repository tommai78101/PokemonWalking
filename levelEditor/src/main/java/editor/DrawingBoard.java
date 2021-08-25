/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.Debug;
import common.Tileable;
import dataset.EditorData;
import dataset.ItemSet;
import dataset.NpcSet;
import dataset.ObstacleSet;

public class DrawingBoard extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private LevelEditor editor;

	private BufferedImage image;
	private int[] tiles;
	private int[] tilesEditorID;
	private TriggerSet triggers;
	private NpcSet npcs;
	private ObstacleSet obstacles;
	private ItemSet items;
	private int bitmapWidth, bitmapHeight;
	private int offsetX, offsetY;
	private int mouseOnTileX, mouseOnTileY;
	private boolean mouseInsideDrawingBoardCheck = false;

	public DrawingBoard(final LevelEditor editor, int width, int height) {
		this.editor = editor;
		this.offsetX = this.offsetY = 0;

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				DrawingBoard.this.mouseInsideDrawingBoardCheck = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				DrawingBoard.this.mouseInsideDrawingBoardCheck = false;
			}
		});

		int w = width * Tileable.WIDTH;
		int h = height * Tileable.HEIGHT;
		Dimension size = new Dimension(w, h);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		editor.validate();
	}

	@Override
	public void run() {
		long now, lastTime = System.nanoTime();
		double unprocessed = 0.0;
		double targetedFrameRate = 30.0;
		final double nsPerTick = 1000000000.0 / targetedFrameRate;
		while (this.editor.running) {
			now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;

			if (unprocessed >= targetedFrameRate)
				unprocessed = targetedFrameRate;
			if (unprocessed <= 0.0)
				unprocessed = 1.0;

			try {
				this.tick();
				Thread.sleep((long) unprocessed);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				this.render();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setImageSize(int w, int h, boolean runTriggerSetCheck) {
		if (w <= 0 || h <= 0) {
			Debug.error("Improper image size [" + w + " x " + h + "] was given.");
			return;
		}

		// Initializing a new Trigger Set.
		if (runTriggerSetCheck) {
			String checksum = this.editor.getChecksum();
			boolean isSetEmpty = (this.triggers == null || this.triggers.isEmpty());
			if (isSetEmpty || !this.triggers.matchesChecksum(checksum)) {
				// Only if the triggers set is null or is empty, do we create a new trigger set.
				this.triggers = new TriggerSet(w, h, this.editor.getChecksum());
			}

			isSetEmpty = (this.npcs == null || this.npcs.isEmpty());
			if (isSetEmpty || !this.npcs.matchesChecksum(checksum)) {
				this.npcs = new NpcSet(this.editor.getChecksum());
			}

			isSetEmpty = (this.obstacles == null || this.obstacles.isEmpty());
			if (isSetEmpty || !this.obstacles.matchesChecksum(checksum)) {
				this.obstacles = new ObstacleSet(checksum);
			}

			isSetEmpty = (this.items == null || this.items.isEmpty());
			if (isSetEmpty || !this.items.matchesChecksum(checksum)) {
				this.items = new ItemSet(checksum);
			}
		}

		// Initializing the data editor ID arrays.
		this.tiles = new int[w * h];
		this.tilesEditorID = new int[w * h];
		Arrays.fill(this.tiles, 0);
		Arrays.fill(this.tilesEditorID, 0);

		// Clearing the rendered area map image.
		if (this.image != null) {
			this.image.flush();
			this.image = null;
		}
		this.image = new BufferedImage(w * Tileable.WIDTH, h * Tileable.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
		for (int j = 0; j < this.image.getHeight(); j++) {
			for (int i = 0; i < this.image.getWidth(); i++) {
				if (i % Tileable.WIDTH == 0 || j % Tileable.HEIGHT == 0)
					pixels[j * this.image.getWidth() + i] = 0;
				else
					pixels[j * this.image.getWidth() + i] = -1;
			}
		}

		// Initializing other attributes of the current area map session in the level editor.
		this.bitmapWidth = w;
		this.bitmapHeight = h;
		this.offsetX = -((this.getWidth() - (w * Tileable.WIDTH)) / 2);
		this.offsetY = -((this.getHeight() - (h * Tileable.HEIGHT)) / 2);
		this.editor.input.offsetX = this.offsetX;
		this.editor.input.offsetY = this.offsetY;
	}

	public void newImage() {
		EventQueue.invokeLater(() -> {
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
				result = JOptionPane.showConfirmDialog(
					null, panel, "Create New Area", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE
				);
			}
			while (Integer.valueOf(widthField.getText()) <= 0 || Integer.valueOf(heightField.getText()) <= 0);
			if (result == JOptionPane.OK_OPTION) {
				int width = Integer.parseInt(widthField.getText());
				int height = Integer.parseInt(heightField.getText());
				DrawingBoard.this.editor.generateChecksum();
				DrawingBoard.this.setImageSize(width, height, true);

				// Script editor
				if (DrawingBoard.this.editor.scriptEditor != null) {
					// Start a clean slate for the Script Editor.
					DrawingBoard.this.editor.scriptEditor.resetComponents();
				}
			}
		});
	}

	public void newImage(final int x, final int y) {
		EventQueue.invokeLater(() -> DrawingBoard.this.setImageSize(x, y, true));
	}

	public void render() throws Exception {
		if (this.bitmapWidth <= 0 || this.bitmapHeight <= 0)
			return;
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}
		this.hoverOver();
		final List<Map.Entry<Integer, SpriteData>> list = EditorConstants.getInstance().getDatas();
		switch (EditorConstants.metadata) {
			case Tilesets: {
				if (this.tilesEditorID != null) {
					for (int j = 0; j < this.tilesEditorID.length; j++) {
						int w = j % this.bitmapWidth;
						int h = j / this.bitmapWidth;

						Map.Entry<Integer, SpriteData> entry = list.get(this.tilesEditorID[j]);
						SpriteData data = entry.getValue();
						if (data == null) {
							break;
						}
						if (data.image == null) {
							this.tiles[j] = 0;
							this.tilesEditorID[j] = 0;
							continue;
						}
						if (this.image == null)
							return;
						Graphics g = this.image.getGraphics();
						BufferedImage bimg = new BufferedImage(
							data.image.getIconWidth(), data.image.getIconHeight(),
							BufferedImage.TYPE_INT_ARGB
						);
						Graphics gB = bimg.getGraphics();
						// TODO: Area Type ID must be included.
						gB.drawImage(data.image.getImage(), 0, 0, null);
						gB.dispose();

						if (data.areaTypeIncluded) {
							// If the area type is included with the Data object, we can add some biome colors.
							// By default, we set the biome colors based on ALPHA value.
							// When area type of NONE is used, the BLUE value determines the biome colors.
							switch (data.areaTypeIDType) {
								case ALPHA:
								default:
									this.setBiomeTile((this.tiles[j] >> 24) & 0xFF, g);
									break;
								case RED:
									this.setBiomeTile((this.tiles[j] >> 16) & 0xFF, g);
									break;
								case GREEN:
									this.setBiomeTile((this.tiles[j] >> 8) & 0xFF, g);
									break;
								case BLUE:
								case NONE:
									this.setBiomeTile(this.tiles[j] & 0xFF, g);
									break;
							}
						}
						else
							g.setColor(Color.white);

						g.fillRect(w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT);
						g.drawImage(bimg, w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT, null);
						g.dispose();
					}
				}
				break;
			}
			case Triggers: {
				if (this.triggers == null)
					break;

				Trigger selectedTrigger = this.editor.properties.getSelectedTrigger();

				// Iterate through a bitmap array of triggers.
				for (int currentTriggerIndex = 0; currentTriggerIndex < this.tiles.length; currentTriggerIndex++) {
					SpriteData data = null;
					try {
						Map.Entry<Integer, SpriteData> entry = list.get(this.tilesEditorID[currentTriggerIndex]);
						data = entry.getValue();
					}
					catch (Exception e) {
						data = list.get(0).getValue();
					}

					int w = currentTriggerIndex % this.bitmapWidth;
					int h = currentTriggerIndex / this.bitmapWidth;

					Graphics g = this.image.getGraphics();

					// This check makes sure that the iterating current tile contains a trigger.
					if (this.triggers.hasTriggers(currentTriggerIndex)) {
						if (this.triggers.contains(currentTriggerIndex, selectedTrigger)) {
							g.setColor(Color.cyan);
						}
						else {
							g.setColor(Color.yellow);
						}
						g.fillRect(w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT);
					}
					else {
						// g.drawImage(data.image.getImage(), w * Tile.WIDTH, h * Tile.HEIGHT,
						// Tile.WIDTH, Tile.HEIGHT, null);
						Graphics gD = this.image.getGraphics();
						BufferedImage bimg = new BufferedImage(
							data.image.getIconWidth(), data.image.getIconHeight(),
							BufferedImage.TYPE_INT_ARGB
						);
						Graphics gB = bimg.getGraphics();
						// TODO: Area Type ID must be included.
						gB.drawImage(data.image.getImage(), 0, 0, null);
						gB.dispose();

						if (data.areaTypeIncluded) {
							switch (data.areaTypeIDType) {
								case ALPHA:
								default:
									this.setBiomeTile((this.tiles[currentTriggerIndex] >> 24) & 0xFF, g);
									break;
								case RED:
									this.setBiomeTile((this.tiles[currentTriggerIndex] >> 16) & 0xFF, g);
									break;
								case GREEN:
									this.setBiomeTile((this.tiles[currentTriggerIndex] >> 8) & 0xFF, g);
									break;
								case BLUE:
									this.setBiomeTile(this.tiles[currentTriggerIndex] & 0xFF, g);
									break;
							}
						}
						else
							gD.setColor(Color.white);
						g.fillRect(w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT);
						g.drawImage(bimg, w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT, null);
					}
					g.dispose();
				}
				break;
			}
			case NonPlayableCharacters: {
				if (this.tilesEditorID != null) {
					for (int j = 0; j < this.tilesEditorID.length; j++) {
						int w = j % this.bitmapWidth;
						int h = j / this.bitmapWidth;

						// Drawing Tileset first
						Map.Entry<Integer, SpriteData> entry = list.get(this.tilesEditorID[j]);
						SpriteData data = entry.getValue();
						if (data == null) {
							break;
						}
						if (data.image == null) {
							this.tiles[j] = 0;
							this.tilesEditorID[j] = 0;
							continue;
						}
						if (this.image == null)
							return;
						Graphics g = this.image.getGraphics();
						BufferedImage bimg = new BufferedImage(
							data.image.getIconWidth(), data.image.getIconHeight(),
							BufferedImage.TYPE_INT_ARGB
						);
						Graphics gB = bimg.getGraphics();
						// TODO: Area Type ID must be included.
						gB.drawImage(data.image.getImage(), 0, 0, null);

						// Drawing NPCs on top of Tileset
						EditorData npc = this.npcs.get(w, h);
						if (npc != null) {
							entry = list.get(npc.getEditorData());
							SpriteData npcData = entry.getValue();
							if (npcData != null) {
								gB.drawImage(npcData.image.getImage(), 0, 0, null);
							}
						}

						gB.dispose();

						if (data.areaTypeIncluded) {
							// If the area type is included with the Data object, we can add some biome colors.
							// By default, we set the biome colors based on ALPHA value.
							// When area type of NONE is used, the BLUE value determines the biome colors.
							switch (data.areaTypeIDType) {
								case ALPHA:
								default:
									this.setBiomeTile((this.tiles[j] >> 24) & 0xFF, g);
									break;
								case RED:
									this.setBiomeTile((this.tiles[j] >> 16) & 0xFF, g);
									break;
								case GREEN:
									this.setBiomeTile((this.tiles[j] >> 8) & 0xFF, g);
									break;
								case BLUE:
								case NONE:
									this.setBiomeTile(this.tiles[j] & 0xFF, g);
									break;
							}
						}
						else
							g.setColor(Color.white);

						g.fillRect(w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT);
						g.drawImage(bimg, w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT, null);
						g.dispose();
					}
				}
				break;
			}
		}
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.translate(-this.offsetX, -this.offsetY);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (this.image != null) {
			g.drawImage(this.image, 0, 0, null);
		}
		g.dispose();
		bs.show();

		// At each end of the rendering, we validate the editor, so the data is refreshed and kept
		// up-to-date.
		this.editor.validate();
	}

	public void tick() {
		if (this.editor.input.isDragging()) {
			this.offsetX = this.editor.input.offsetX;
			this.offsetY = this.editor.input.offsetY;
		}
		else if (this.isMouseInDrawingBoard()) {
			switch (EditorConstants.metadata) {
				case Tilesets: {
					if (!this.editor.input.isDrawing())
						return;

					// Tilesets Editing is subjected to both dragging and clicking.
					this.mouseOnTileX = this.offsetX + this.editor.input.drawingX;
					this.mouseOnTileY = this.offsetY + this.editor.input.drawingY;
					if (this.mouseOnTileX < 0 || this.mouseOnTileX >= this.bitmapWidth * Tileable.WIDTH || this.mouseOnTileY < 0 || this.mouseOnTileY >= this.bitmapHeight * Tileable.HEIGHT)
						return;

					SpriteData selectedData = this.editor.controlPanel.getSelectedData();
					if (selectedData != null) {
						if (selectedData.name.equals("Select")) {
							this.editor.controlPanel.setSelectedData(selectedData);

							// NOTE(Thompson): Do not set the control panel's selected data to the picked data.
							SpriteData tilePickerData = this.getSelectedDataProperties();

							// Overwrite the Mouse Select data with the new data from the DrawingBoard.
							this.editor.controlPanel.getPropertiesPanel().setDataProperties(tilePickerData);
						}
						else {
							this.setDataProperties(selectedData);
						}
					}
					break;
				}
				case Triggers: {
					// Triggers Editing is subjected to clicking only.
					this.mouseOnTileX = this.editor.input.offsetX + this.editor.input.drawingX;
					this.mouseOnTileY = this.editor.input.offsetY + this.editor.input.drawingY;
					if (this.mouseOnTileX < 0 || this.mouseOnTileX >= this.bitmapWidth * Tileable.WIDTH || this.mouseOnTileY < 0 || this.mouseOnTileY >= this.bitmapHeight * Tileable.HEIGHT)
						return;

					Trigger selectedTrigger = this.editor.controlPanel.getSelectedTrigger();
					if (selectedTrigger != null) {
						if (this.editor.input.isClicking() && !selectedTrigger.isEraser()) {
							int x = this.getMouseTileX();
							int y = this.getMouseTileY();
							int i = y * this.bitmapWidth + x;
							if (this.triggers.contains(i, selectedTrigger)) {
								this.triggers.removeTrigger(i, selectedTrigger);
							}
							else {
								this.triggers.addTrigger(i, (byte) x, (byte) y, selectedTrigger);
							}
							this.editor.input.forceCancelDrawing();
						}
						else if (this.editor.input.isDrawing() && selectedTrigger.isEraser()) {
							int x = this.getMouseTileX();
							int y = this.getMouseTileY();
							int i = y * this.bitmapWidth + x;
							this.triggers.clearAllTriggers(i);
						}
					}
					break;
				}
				case NonPlayableCharacters: {
					// NPCs Editing is subjected to clicking only.
					this.mouseOnTileX = this.editor.input.offsetX + this.editor.input.drawingX;
					this.mouseOnTileY = this.editor.input.offsetY + this.editor.input.drawingY;
					if (this.mouseOnTileX < 0 || this.mouseOnTileX >= this.bitmapWidth * Tileable.WIDTH || this.mouseOnTileY < 0 || this.mouseOnTileY >= this.bitmapHeight * Tileable.HEIGHT)
						return;

					SpriteData selectedData = this.editor.controlPanel.getSelectedData();
					if (selectedData != null && this.editor.input.isDrawing()) {
						this.setDataProperties(selectedData);
					}
					break;
				}
			}
		}
	}

	/**
	 * There are two places where selected data may come in.<br/>
	 * - From the argument parameter.<br/>
	 * - From the ControlPanel object's "dataValue" property.<br/>
	 * <p>
	 * The "selectedData" will always get priority. It will be used to fetch the correct Data object
	 * from a lookup list.
	 * <p>
	 * The ControlPanel's dataValue gathers the user's modified data values, then inserts it into the
	 * data value of the "data object from the lookup list".
	 *
	 * @param selectedData
	 */
	public void setDataProperties(SpriteData selectedData) {
		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		panel.areaIDInputField.setText(Integer.toString(this.editor.getUniqueAreaID()));

		int tileIndex = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();

		// Search for the data from the actual collection of game data we have loaded in.
		SpriteData data = null;
		int tempEditorIDValue = selectedData.getEditorID();
		List<Map.Entry<Integer, SpriteData>> dataList = EditorConstants.getInstance().getDatas();
		for (Map.Entry<Integer, SpriteData> entry : dataList) {
			if (tempEditorIDValue == entry.getKey()) {
				data = entry.getValue();
				break;
			}
		}

		// NOTE(Thompson): The case statements are not in order. Code logic > ordering sequence.
		switch (data.alpha) {
			case 0x01: // Sand
			case 0x02: { // Grass
				// Data Properties panel can edit reserved miscellaneous data for that tile.
				this.tiles[tileIndex] = (data.alpha << 24) | (data.red << 16) | panel.dataValue & 0xFFFF;
				this.tilesEditorID[tileIndex] = data.editorID;
				break;
			}
			case 0x03: {
				switch (data.red) {
					case 0x05: { // Sign
						int green = (panel.dataValue >> 8) & 0xFF;
						int blue = (panel.dataValue & 0xFF);
						int check = (green << 8) | blue;
						if (check > 0) {
							this.tiles[tileIndex] = (data.alpha << 24) | (data.red << 16) | check;
							this.tilesEditorID[tileIndex] = data.editorID;
							this.obstacles.add(this.getMouseTileX(), this.getMouseTileY(), data.editorID, this.tiles[tileIndex]);
						}
						else {
							JOptionPane.showMessageDialog(null, "Sign: Tile specific ID (dialogue ID) should not be 0x0000.");
							this.editor.input.forceCancelDrawing();
						}
						break;
					}
					default: {
						this.defaultTileProperties(data);
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
				data = this.manualInputTileProperties(data);
				panel.redInputField.setText(Integer.toString(data.red));
				panel.greenInputField.setText(Integer.toString(data.green));
				panel.redField.setText(Integer.toString(data.red));
				panel.greenField.setText(Integer.toString(data.green));
				panel.blueInputField.setText(Integer.toString(data.blue));
				panel.blueField.setText(Integer.toString(data.blue));
				break;
			}
			case 0x08: { // House
				switch (data.red) {
					case 0x0B: // Left roof
					case 0x0C: // Middle roof
					case 0x0D: {// Right roof
						this.tiles[tileIndex] = (data.alpha << 24) | (data.red << 16) | panel.dataValue & 0xFFFF;
						this.tilesEditorID[tileIndex] = data.editorID;
						break;
					}
					default:
						this.defaultTileProperties(data);
						break;
				}
				break;
			}
			case 0x0A: { // Items
				this.tiles[tileIndex] = (data.alpha << 24) | panel.dataValue & 0xFFFFFF;
				this.tilesEditorID[tileIndex] = data.editorID;
				this.items.add(this.getMouseTileX(), this.getMouseTileY(), data.editorID, data.getColorValue());
				break;
			}
			case 0x0D: { // Triggers
				switch (data.red) {
					case 0x00: { // Default starting location.
						int green = data.green;
						int blue = data.blue;
						if (data.greenByEditor)
							green = this.getMouseTileX();
						if (data.blueByEditor)
							blue = this.getMouseTileY();
						this.tiles[tileIndex] = (data.alpha << 24) | (data.red << 16) | (green << 8) | blue;
						this.tilesEditorID[tileIndex] = data.editorID;
						this.triggers.addTriggerById(tileIndex, data.getColorValue(), data.getTileSpecificID(), Trigger.NPC_TRIGGER_ID_NONE);
						panel.greenInputField.setText(Integer.toString(green));
						panel.blueInputField.setText(Integer.toString(blue));
						panel.greenField.setText(Integer.toString(green));
						panel.blueField.setText(Integer.toString(blue));
						break;
					}
					default:
						break;
				}
				break;
			}
			case 0x0E: { // Characters/NPCs
				int dataColor = data.getColorValue();
				int panelRed = panel.getRed();
				int panelGreen = panel.getGreen();
				if (data.red != panelRed)
					data.setColorValue((dataColor & 0xFF00FFFF) | (panelRed << 16));
				if (data.green != panelGreen)
					data.setColorValue((dataColor & 0xFFFF00FF) | (panelGreen << 8));
				this.npcs.add(this.getMouseTileX(), this.getMouseTileY(), data.editorID, data.getColorValue());
				panel.redInputField.setText(Integer.toString(data.red));
				panel.greenInputField.setText(Integer.toString(data.green));
				panel.redField.setText(Integer.toString(data.red));
				panel.greenField.setText(Integer.toString(data.green));

				// Disabling the blue field for now.
				panel.blueInputField.setText(Integer.toString(-1));
				panel.blueField.setText(Integer.toString(-1));
				break;
			}
			default: {
				this.defaultTileProperties(data);
				break;
			}
		}
	}

	public SpriteData getSelectedDataProperties() {
		SpriteData data = new SpriteData();

		int tileIndex = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();
		data.setColorValue(this.tiles[tileIndex]);
		data.setEditorID(this.tilesEditorID[tileIndex]);

		String greenText = Integer.toString(data.green);
		String blueText = Integer.toString(data.blue);
		String areaIDText = Integer.toString(this.editor.getUniqueAreaID());

		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		panel.areaIDInputField.setText(areaIDText);
		panel.greenInputField.setText(greenText);
		panel.blueInputField.setText(blueText);
		panel.greenField.setText(greenText);
		panel.blueField.setText(blueText);

		return data;
	}

	public void start() {
		new Thread(this).start();
	}

	public int getMouseTileX() {
		return this.mouseOnTileX / Tileable.WIDTH;
	}

	public int getMouseTileY() {
		return this.mouseOnTileY / Tileable.HEIGHT;
	}

	public int getBitmapWidth() {
		return this.bitmapWidth;
	}

	public int getBitmapHeight() {
		return this.bitmapHeight;
	}

	/**
	 * Produce a bitmap image containing the triggers data and tilesets data. Some swizzling is
	 * necessary.
	 *
	 * @return <b>BufferedImage</b> object containing the triggers and tilesets data.
	 */
	public BufferedImage getMapImage() {
		if (this.bitmapWidth * this.bitmapHeight == 0)
			return null;
		// Prepare the resulting data.
		List<Integer> pixels = new ArrayList<>();

		// Storing important area information in the first pixel. This should use up all of the number of
		// pixels we had reserved.
		final int areaID = this.editor.getUniqueAreaID();

		// Get the checksum and relevant information.
		final String checksum = this.editor.getChecksum();
		byte[] checksumBytes = checksum.getBytes();

		// Trigger size will always be no more than 65536 triggers. Trigger size will always be 1 + (number
		// of triggers seen in the editor), to account for the Eraser trigger. If there are more triggers
		// than the width of the bitmap, we add however many extra rows to compensate.
		int triggerSize = this.triggers.getSize() & 0xFFFF;

		// Add any NPCs, obstacles, and items into a list.
		int[] npcsData = this.npcs.produce();
		int[] obstaclesData = this.obstacles.produce();
		int[] itemsData = this.items.produce();

		// ----------
		// Step 1 - Set the important map info in the first pixel.
		pixels.add(((areaID & 0xFFFF) << 16) | (triggerSize & 0xFFFF));
		pixels.add(((this.bitmapWidth & 0xFFFF) << 16) | (this.bitmapHeight & 0xFFFF));
		pixels.add(this.tiles.length);

		// Step 2 - Store the area map checksum
		for (int i = 0; i < checksumBytes.length; i += 4) {
			pixels.add((checksumBytes[i] << 24) | (checksumBytes[i + 1] << 16) | (checksumBytes[i + 2] << 8) | checksumBytes[i + 3]);
		}

		// Step 3 - Store the triggers
		List<Integer> triggerData = this.triggers.convertToData();
		triggerData.stream().forEach(pixels::add);

		// Step 4 - Store the NPCs
		Arrays.stream(npcsData).forEach(pixels::add);

		// Step 5 - Store obstacles
		Arrays.stream(obstaclesData).forEach(pixels::add);

		// Step 6 - Store items
		Arrays.stream(itemsData).forEach(pixels::add);

		// Step 6 - Pad the remaining row with -1
		int col = pixels.size();
		for (; (col % this.bitmapWidth) != 0 && (col % this.bitmapWidth) < this.bitmapWidth; col = pixels.size())
			pixels.add(-1);

		// Step 6 - Store the tiles
		Arrays.stream(this.tiles).forEach(pixels::add);

		// Step 7 - Convert List<Integer> to int[]
		int[] data = pixels.parallelStream().mapToInt(Integer::intValue).toArray();
		int newHeight = (data.length % this.bitmapWidth != 0) ? data.length / this.bitmapWidth + 1 : data.length / this.bitmapWidth;
		BufferedImage image = new BufferedImage(this.bitmapWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		final int[] result = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, result, 0, data.length);
		return image;
	}

	public boolean hasBitmap() {
		return (this.bitmapHeight * this.bitmapWidth != 0);
	}

	public boolean isMouseInDrawingBoard() {
		return this.mouseInsideDrawingBoardCheck;
	}

	public void openMapImage(BufferedImage image) {
		int bitmapWidth = image.getWidth();
		int bitmapHeight = image.getHeight();
		if (bitmapWidth * bitmapHeight == 0) {
			Debug.showExceptionCause("Image size is invalid.", new IllegalArgumentException());
			return;
		}

		final int[] pixels = image.getRGB(0, 0, bitmapWidth, bitmapHeight, null, 0, bitmapWidth);
		int pixelIterator = 0;
		try {
			// Step 1 - Get important area information in the first pixel. This should use up all of the number
			// of pixels we had reserved.
			this.editor.setUniqueAreaID((pixels[pixelIterator] >> 16) & 0xFFFF);
			final int triggerSize = pixels[pixelIterator++] & 0xFFFF;
			final int areaInfo = pixels[pixelIterator++];
			final int tileSize = pixels[pixelIterator++];
			int newWidth = (areaInfo >> 16) & 0xFFFF;
			int newHeight = areaInfo & 0xFFFF;
			this.setImageSize(newWidth, newHeight, false);

			// Step 2 - Get the checksum and relevant information.
			final int checksumPixelsCount = LevelEditor.CHECKSUM_MAX_BYTES_LENGTH / 4;
			this.editor.setChecksum(pixels, pixelIterator, checksumPixelsCount);
			final String checksum = this.editor.getChecksum();
			pixelIterator += checksumPixelsCount;

			// Step 3 - Add any triggers into a list. If triggers is null, make sure to append the Eraser
			// trigger, designated as ID 0.
			this.triggers = new TriggerSet(bitmapWidth, bitmapHeight, this.editor.getChecksum());
			if (triggerSize > 0) {
				// Ignoring Eraser trigger.
				pixelIterator++;
				pixelIterator++;

				for (int i = 0; i < triggerSize; i++) {
					int triggerInfo = pixels[pixelIterator++];
					int npcTriggerInfo = pixels[pixelIterator++];
					if (triggerInfo - npcTriggerInfo != 0 || triggerInfo + npcTriggerInfo != 0) {
						// We only want non-Eraser triggers to be added to the trigger set in the level editor.
						int x = (triggerInfo >> 24) & 0xFF;
						int y = (triggerInfo >> 16) & 0xFF;
						short triggerId = (short) (triggerInfo & 0xFFFF);
						short npcTriggerId = (short) (npcTriggerInfo & 0xFFFF);
						this.triggers.addTriggerById(y * bitmapWidth + x, triggerInfo, triggerId, npcTriggerId);
					}
				}
			}
			else {
				pixelIterator++;
				pixelIterator++;
			}

			// Step 4 - Get the NPCs data.
			this.npcs = new NpcSet(checksum);
			int npcSize = pixels[pixelIterator++];
			for (int i = 0; i < npcSize; i++) {
				int triggerInfo = pixels[pixelIterator++];
				int x = (triggerInfo >> 24) & 0xFF;
				int y = (triggerInfo >> 16) & 0xFF;
				int editorID = (triggerInfo & 0xFFFF);
				int data = pixels[pixelIterator++];
				this.npcs.add(x, y, editorID, data);
			}

			// Step 5 - Get obstacles.
			this.obstacles = new ObstacleSet(checksum);
			int obstacleSize = pixels[pixelIterator++];
			for (int i = 0; i < obstacleSize; i++) {
				int triggerInfo = pixels[pixelIterator++];
				int x = (triggerInfo >> 24) & 0xFF;
				int y = (triggerInfo >> 16) & 0xFF;
				int editorID = (triggerInfo & 0xFFFF);
				int data = pixels[pixelIterator++];
				this.obstacles.add(x, y, editorID, data);
			}

			// Step 6 - Get items.
			this.items = new ItemSet(checksum);
			int itemSize = pixels[pixelIterator++];
			for (int i = 0; i < itemSize; i++) {
				int triggerInfo = pixels[pixelIterator++];
				int x = (triggerInfo >> 24) & 0xFF;
				int y = (triggerInfo >> 16) & 0xFF;
				int editorID = (triggerInfo & 0xFFFF);
				int data = pixels[pixelIterator++];
				this.items.add(x, y, editorID, data);
			}

			// Step 6 - Skip the padding
			int col = pixelIterator % bitmapWidth;
			for (; pixelIterator % bitmapWidth != 0 && col < bitmapWidth; pixelIterator++)
				;

			// Step 7 - Get the tiles.
			System.arraycopy(pixels, pixelIterator, this.tiles, 0, tileSize);
			pixelIterator += tileSize;

			// Step 8 - Get and fill in the data based on the tiles obtained from above.
			List<Map.Entry<Integer, SpriteData>> list = EditorConstants.getInstance().getDatas();
			for (int i = 0; i < this.tiles.length; i++) {
				int alpha = ((this.tiles[i] >> 24) & 0xFF);
				FOR_LOOP:
				for (int j = 0; j < list.size(); j++) {
					SpriteData d = list.get(j).getValue();
					if (alpha != Integer.valueOf(d.alpha))
						continue;
					switch (alpha) {
						case 0x01: // Path
						case 0x02: // Ledges
						case 0x03: // Obstacles
						case 0x06: // Stairs
						case 0x07: // Water
						case 0x08: // House
							// Extended Tile IDs are used to differentiate tiles.
							int red = ((this.tiles[i] >> 16) & 0xFF);
							if (red == Integer.valueOf(d.red)) {
								this.tilesEditorID[i] = d.editorID;
								break FOR_LOOP;
							}
							continue;
						case 0x05: {// Area Zone
							// Extended Tile IDs are used to differentiate tiles.
							int blue = this.tiles[i] & 0xFF;
							if (blue == d.blue) {
								this.tilesEditorID[i] = d.editorID;
								break FOR_LOOP;
							}
							continue;
						}
						case 0x0E: {// Characters/NPCs
							// Alpha value is only used.
							this.tilesEditorID[i] = d.editorID;
							break FOR_LOOP;
						}
						default: {
							// Alpha value is only used.
							this.tilesEditorID[i] = d.editorID;
							break FOR_LOOP;
						}
					}
				}
			}
		}
		catch (NegativeArraySizeException e) {
			Debug.showExceptionCause("Incorrect file format. The file does not contain necessary metadata.", e);
		}
		catch (Exception e) {
			Debug.showExceptionCause(e);
		}
	}

	// --------------------------------------------------------------------------------------------
	// Private methods

	private void hoverOver() {
		try {
			int w = -1;
			int h = -1;
			int temp = -1;

			// try...catch for X position
			try {
				temp = (this.editor.input.offsetX + this.editor.input.mouseX);
				if (temp >= 0)
					w = temp / Tileable.WIDTH;
			}
			catch (Exception e) {
				w = (this.editor.input.offsetX + this.editor.input.mouseX) / (LevelEditor.WIDTH * LevelEditor.SIZE);
			}

			// try catch for Y position
			try {
				temp = (this.editor.input.offsetY + this.editor.input.mouseY);
				if (temp >= 0)
					h = temp / Tileable.HEIGHT;
			}
			catch (Exception e) {
				h = (this.editor.input.offsetY + this.editor.input.mouseY) / (LevelEditor.WIDTH * LevelEditor.SIZE);
			}

			if (w < 0 || h < 0 || w >= this.bitmapWidth || h >= this.bitmapHeight) {
				TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
				panel.alphaField.setText("");
				panel.redField.setText("");
				panel.greenField.setText("");
				panel.blueField.setText("");
				panel.isNpcTriggerBox.setSelected(false);
				return;
			}

			TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
			int value = 0;
			panel.isNpcTriggerBox.setSelected(false);
			switch (EditorConstants.metadata) {
				default:
				case Tilesets:
					value = this.tiles[h * this.bitmapWidth + w];
					break;
				case Triggers:
					Trigger first = null;
					Trigger second = null;
					var keys = this.triggers.getAllTriggers().entrySet();
					FIRST_LOOP:
					for (var entry : keys) {
						Set<Trigger> set = entry.getValue();
						for (Trigger t : set) {
							if (t.isNpcTrigger()) {
								first = t;
							}
							else {
								second = t;
							}
						}
						if (first != null || second != null)
							break FIRST_LOOP;
					}
					if (first != null && first.isNpcTrigger()) {
						value = first.getNpcTriggerID();
						panel.isNpcTriggerBox.setSelected(true);
					}
					else if (second != null) {
						value = second.getTriggerID();
						panel.isNpcTriggerBox.setSelected(false);
					}
					break;
				case NonPlayableCharacters:
					EditorData npc = this.npcs.get(w, h);
					if (npc != null) {
						value = npc.getColorData();
					}
					break;
			}

			// checks for mouse hoving above tiles, regardless of what editor metadata we are on.
			panel.alphaField.setText(Integer.toString((value >> 24) & 0xFF));
			panel.redField.setText(Integer.toString((value >> 16) & 0xFF));
			panel.greenField.setText(Integer.toString((value >> 8) & 0xFF));
			panel.blueField.setText(Integer.toString(value & 0xFF));
		}
		catch (Exception e) {
			Debug.error("Unable to handle mouse hover cursor position to determine which tile was hovered on.", e);
			TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
			panel.alphaField.setText("");
			panel.redField.setText("");
			panel.greenField.setText("");
			panel.blueField.setText("");
		}
	}

	private void defaultTileProperties(SpriteData d) {
		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		int i = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();
		SpriteData temp = null;
		for (Map.Entry<Integer, SpriteData> entry : EditorConstants.getInstance().getDatas()) {
			if (panel.dataValue == entry.getKey()) {
				temp = entry.getValue();
				break;
			}
		}
		if (temp != null) {
			this.tiles[i] = panel.dataValue;
			this.tilesEditorID[i] = temp.editorID;
		}
		else {
			this.tiles[i] = d.getColorValue();
			this.tilesEditorID[i] = d.editorID;
		}
	}

	private SpriteData manualInputTileProperties(SpriteData d) {
		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		int i = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();
		this.tiles[i] = panel.dataValue;
		this.tilesEditorID[i] = d.editorID;
		d.setColorValue(panel.dataValue);
		return d;
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

}