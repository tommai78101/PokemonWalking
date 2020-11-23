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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.Debug;
import common.Tileable;

class TriggerSet {
	// Map of <Tile ID (current tile index), Set of triggers>
	private Map<Integer, Set<Trigger>> triggers;
	// Since this is part of Javax Swing, I might as well use it to store the width and height of the
	// bitmap.
	private Dimension size;

	public TriggerSet(int width, int height) {
		this.size = new Dimension(width, height);
		this.triggers = new HashMap<>();
	}

	public int getSize() {
		return this.triggers.size();
	}

	public Trigger validityCheck(int triggerId) {
		Trigger triggerValidityCheck = null;
		try {
			triggerValidityCheck = EditorConstants.getInstance().getTriggers().get(triggerId);
		}
		catch (Exception e) {
			// Eraser.
			triggerValidityCheck = EditorConstants.getInstance().getTriggers().get(0);
		}
		return triggerValidityCheck;
	}

	public Set<Trigger> getTriggers(int currentTriggerIndex) {
		return this.triggers.get(currentTriggerIndex);
	}

	public void addTrigger(int index, byte newX, byte newY, Trigger trigger) {
		Set<Trigger> set = this.triggers.get(index);
		if (set == null) {
			set = new HashSet<>();
			this.triggers.put(index, set);
		}
		Trigger modifiedTrigger = new Trigger();
		modifiedTrigger.setTriggerPositionX(newX);
		modifiedTrigger.setTriggerPositionY(newY);
		modifiedTrigger.setTriggerID(trigger.getTriggerID());
		set.add(modifiedTrigger);
	}

	public void removeTrigger(int index, Trigger trigger) {
		Set<Trigger> set = this.triggers.get(index);
		if (set == null) {
			return;
		}
		Trigger delete = null;
		for (Trigger t : set) {
			if (t.getTriggerID() == trigger.getTriggerID()) {
				delete = t;
				break;
			}
		}
		if (delete != null) {
			set.remove(delete);
		}
	}

	public void toggleTrigger(int index, Trigger trigger) {
		Debug.log("Toggling trigger");
		Set<Trigger> set = this.triggers.get(index);
		if (set == null) {
			set = new HashSet<>();
			set.add(trigger);
			this.triggers.put(index, set);
			return;
		}
		if (set.contains(trigger)) {
			set.remove(trigger);
		}
		else {
			set.add(trigger);
		}
	}

	public List<Integer> convertToData() {
		List<Integer> output = new ArrayList<>();
		// Eraser trigger by default.
		output.add(0);
		Set<Integer> keySet = this.triggers.keySet();
		for (int tileId : keySet) {
			Set<Trigger> triggerSet = this.triggers.get(tileId);
			if (triggerSet == null || triggerSet.isEmpty()) {
				continue;
			}
			for (Trigger trigger : triggerSet) {
				output.add(trigger.getDataValue());
			}
		}
		return output;
	}

	public void addTriggerById(int index, int triggerId) {
		Trigger trigger = this.validityCheck(triggerId);
		if (trigger != null && !trigger.isEraser()) {
			byte x = (byte) (index % this.size.width);
			byte y = (byte) (index / this.size.width);
			this.addTrigger(index, x, y, trigger);
		}
		else {
			Debug.error("Unrecognized trigger ID: " + triggerId + " at tile: " + index + " located at: (" + (index % this.size.width) + "," + (index / this.size.width) + ").");
		}
	}

	public void clearAllTriggers(int index) {
		Set<Trigger> set = this.triggers.get(index);
		if (set != null)
			set.clear();
	}

	public boolean contains(int index, Trigger trigger) {
		Set<Trigger> set = this.triggers.get(index);
		if (set == null || set.isEmpty())
			return false;
		for (Trigger t : set) {
			if (t.getTriggerID() == trigger.getTriggerID())
				return true;
		}
		return false;
	}

	public boolean hasTriggers(int index) {
		Set<Trigger> set = this.triggers.get(index);
		return (set != null && !set.isEmpty());
	}
}

public class DrawingBoard extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private LevelEditor editor;

	private BufferedImage image;
	private int[] tiles;
	private int[] tilesEditorID;
	private TriggerSet triggers;
	private int bitmapWidth, bitmapHeight;
	private int offsetX, offsetY;
	private int mouseOnTileX, mouseOnTileY;
	private boolean mouseInsideDrawingBoardCheck = false;

	public DrawingBoard(final LevelEditor editor, int width, int height) {
		super();
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
		this.validate();
		editor.validate();
	}

	@Override
	public void run() {
		long now, lastTime = System.nanoTime();
		double unprocessed = 0.0;
		final double nsPerTick = 1000000000.0 / 30.0;
		while (this.editor.running) {
			now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;

			if (unprocessed >= 40.0)
				unprocessed = 40.0;
			if (unprocessed <= 0.0)
				unprocessed = 1.0;

			while (unprocessed >= 1.0) {
				this.tick();
				unprocessed -= 1.0;
			}
			try {
				this.render();
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
		if (this.image != null) {
			this.image.flush();
			this.image = null;
		}
		this.tiles = new int[w * h];
		this.tilesEditorID = new int[w * h];
		this.triggers = new TriggerSet(w, h);
		for (int i = 0; i < this.tiles.length; i++) {
			this.tiles[i] = 0;
			this.tilesEditorID[i] = 0;
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
		this.bitmapWidth = w;
		this.bitmapHeight = h;
		this.offsetX = -((this.getWidth() - (w * Tileable.WIDTH)) / 2);
		this.offsetY = -((this.getHeight() - (h * Tileable.HEIGHT)) / 2);
		this.editor.input.offsetX = this.offsetX;
		this.editor.input.offsetY = this.offsetY;
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
					result = JOptionPane.showConfirmDialog(
					    null, panel, "Create New Area", JOptionPane.OK_CANCEL_OPTION,
					    JOptionPane.PLAIN_MESSAGE
					);
				}
				while (Integer.valueOf(widthField.getText()) <= 0 || Integer.valueOf(heightField.getText()) <= 0);
				if (result == JOptionPane.OK_OPTION) {
					DrawingBoard.this.setImageSize(Integer.valueOf(widthField.getText()), Integer.valueOf(heightField.getText()));

					// Script editor
					if (DrawingBoard.this.editor.scriptEditor != null) {
						// Start a clean slate for the Script Editor.
						DrawingBoard.this.editor.scriptEditor.resetComponents();
					}
				}
			}
		});
	}

	public void newImage(final int x, final int y) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DrawingBoard.this.setImageSize(x, y);
			}
		});
	}

	public void render() throws Exception {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}
		this.hoverOver();
		switch (EditorConstants.metadata) {
			case Pixel_Data: {
				if (this.tilesEditorID != null) {
					for (int j = 0; j < this.tilesEditorID.length; j++) {
						if (this.bitmapWidth <= 0 || this.bitmapHeight <= 0)
							break;
						int w = j % this.bitmapWidth;
						int h = j / this.bitmapWidth;

						Map.Entry<Integer, Data> entry = EditorConstants.getInstance().getDatas().get(this.tilesEditorID[j]);
						Data data = entry.getValue();
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
					if (this.bitmapWidth <= 0)
						break;
					if (this.bitmapHeight <= 0)
						break;

					Data data = null;
					try {
						Map.Entry<Integer, Data> entry = EditorConstants.getInstance().getDatas().get(this.tilesEditorID[currentTriggerIndex]);
						data = entry.getValue();
					}
					catch (Exception e) {
						data = EditorConstants.getInstance().getDatas().get(0).getValue();
					}

					int w = currentTriggerIndex % this.bitmapWidth;
					int h = currentTriggerIndex / this.bitmapWidth;

					Graphics g = this.image.getGraphics();

					// This check makes sure that the iterating current tile contains a trigger.
					if (this.triggers.hasTriggers(currentTriggerIndex)) {
						if (this.triggers.contains(currentTriggerIndex, selectedTrigger)) {
							g.setColor(Color.cyan);
							g.fillRect(w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT);
						}
						else {
							g.setColor(Color.yellow);
							g.fillRect(w * Tileable.WIDTH, h * Tileable.HEIGHT, Tileable.WIDTH, Tileable.HEIGHT);
						}
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
						g.dispose();
					}
					g.dispose();
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
	}

	public void tick() {
		switch (EditorConstants.metadata) {
			case Pixel_Data: {
				if (this.editor.input.isDragging()) {
					this.offsetX = this.editor.input.offsetX;
					this.offsetY = this.editor.input.offsetY;
				}
				else if (this.isMouseInDrawingBoard() && this.editor.input.isDrawing()) {
					this.mouseOnTileX = this.offsetX + this.editor.input.drawingX;
					this.mouseOnTileY = this.offsetY + this.editor.input.drawingY;
					if (this.mouseOnTileX < 0 || this.mouseOnTileX >= this.bitmapWidth * Tileable.WIDTH)
						return;
					if (this.mouseOnTileY < 0 || this.mouseOnTileY >= this.bitmapHeight * Tileable.HEIGHT)
						return;
					Data selectedData = this.editor.controlPanel.getSelectedData();
					if (selectedData != null) {
						if (selectedData.name.equals("Select")) {
							this.editor.controlPanel.setSelectedData(selectedData);

							// NOTE(Thompson): Do not set the control panel's selected data to the picked data.
							Data tilePickerData = this.getSelectedDataProperties();

							// Overwrite the Mouse Select data with the new data from the DrawingBoard.
							this.editor.controlPanel.getPropertiesPanel().setDataProperties(tilePickerData);
						}
						else {
							this.setDataProperties(selectedData);
						}
					}
					this.editor.validate();
				}
				break;
			}
			case Triggers: {
				if (this.editor.input.isDragging()) {
					this.offsetX = this.editor.input.offsetX;
					this.offsetY = this.editor.input.offsetY;
				}
				// Only Triggers Editing is subjected to clicking only.
				else if (this.isMouseInDrawingBoard() && this.editor.input.isClicking()) {
					this.mouseOnTileX = this.editor.input.offsetX + this.editor.input.drawingX;
					this.mouseOnTileY = this.editor.input.offsetY + this.editor.input.drawingY;
					if (this.mouseOnTileX < 0 || this.mouseOnTileX >= this.bitmapWidth * Tileable.WIDTH)
						return;
					if (this.mouseOnTileY < 0 || this.mouseOnTileY >= this.bitmapHeight * Tileable.HEIGHT)
						return;
					Trigger selectedTrigger = this.editor.controlPanel.getSelectedTrigger();
					if (selectedTrigger != null && !selectedTrigger.isEraser()) {
						int x = this.getMouseTileX();
						int y = this.getMouseTileY();
						int i = y * this.bitmapWidth + x;
						if (this.triggers.contains(i, selectedTrigger)) {
							this.triggers.removeTrigger(i, selectedTrigger);
						}
						else {
							this.triggers.addTrigger(i, (byte) x, (byte) y, selectedTrigger);
						}
					}
					else if (selectedTrigger != null && selectedTrigger.isEraser()) {
						int x = this.getMouseTileX();
						int y = this.getMouseTileY();
						int i = y * this.bitmapWidth + x;
						this.triggers.clearAllTriggers(i);
					}
					this.editor.validate();
				}
				break;
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
	public void setDataProperties(Data selectedData) {
		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		panel.areaIDInputField.setText(Integer.toString(this.editor.getUniqueAreaID()));

		int tileIndex = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();

		// Search for the data from the actual collection of game data we have loaded in.
		Data data = null;
		int tempColorValue = selectedData.getColorValue();
		List<Map.Entry<Integer, Data>> dataList = EditorConstants.getInstance().getDatas();
		for (Map.Entry<Integer, Data> entry : dataList) {
			if (tempColorValue == entry.getKey()) {
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
						this.tiles[tileIndex] = (data.alpha << 24) | (data.red << 16) | (green << 8) | blue;
						this.tilesEditorID[tileIndex] = data.editorID;
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
				this.manualInputTileProperties(data);
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
						panel.greenInputField.setText(Integer.toString(green));
						panel.blueInputField.setText(Integer.toString(blue));
						panel.greenField.setText(Integer.toString(green));
						panel.blueField.setText(Integer.toString(blue));
						panel.validate();
						break;
					}
					default:
						break;
				}
				break;
			}
			default: {
				this.defaultTileProperties(data);
				break;
			}
		}
	}

	public Data getSelectedDataProperties() {
		Data data = new Data();

		int tileIndex = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();
		data.setColorValue(this.tiles[tileIndex]);
		data.editorID = this.tilesEditorID[tileIndex];

		String greenText = Integer.toString(data.green);
		String blueText = Integer.toString(data.blue);
		String areaIDText = Integer.toString(this.editor.getUniqueAreaID());

		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		panel.areaIDInputField.setText(areaIDText);
		panel.greenInputField.setText(greenText);
		panel.blueInputField.setText(blueText);
		panel.greenField.setText(greenText);
		panel.blueField.setText(blueText);
		panel.validate();

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

		// Represents how many reserved pixels that will be used before creating the bitmap.
		final int usedReservedPixelsCount = 1;

		// Add any triggers into a list. If triggers is null, make sure to append the Eraser trigger,
		// designated as ID 0.
		if (this.triggers == null) {
			this.triggers = new TriggerSet(this.bitmapWidth, this.bitmapHeight);
		}
		List<Integer> list = this.triggers.convertToData();

		// Trigger size will always be no more than 65536 triggers. Trigger size will always be 1 + (number
		// of triggers seen in the editor), to account for the Eraser trigger. If there are more triggers
		// than the width of the bitmap, we add however many extra rows to compensate.
		int triggerSize = list.size() & 0xFFFF;
		int rows = (triggerSize + usedReservedPixelsCount) / this.bitmapWidth;
		int triggerRowHeight = ((triggerSize + usedReservedPixelsCount) % this.bitmapWidth > 0 ? rows + 1 : rows);

		BufferedImage buffer = new BufferedImage(this.bitmapWidth, this.bitmapHeight + triggerRowHeight, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();

		// Storing important area information in the first pixel. This should use up all of the number of
		// pixels we had reserved.
		int areaID = this.editor.getUniqueAreaID();
		pixels[0] = (((areaID & 0xFFFF) << 16) | (triggerSize));

		// Place the trigger data inside the trigger section in the bitmap file. This is usually located at
		// the top of the bitmap file.
		int columnIndex = 0;
		int rowIndex = 0;
		for (int i = 0; i < triggerSize; i++) {
			// The index "i" must be in range of 0 ~ list.size(). The "width" must include the reserved pixels
			// count as offset.
			int pixelIndex = i + usedReservedPixelsCount;
			columnIndex = pixelIndex % this.bitmapWidth;
			rowIndex = pixelIndex / this.bitmapWidth;
			pixels[columnIndex + (rowIndex * this.bitmapWidth)] = list.get(i).intValue();
		}

		// Pad out the trigger section with -1 if the current iterator is not inside the tileset section.
		// This marks null trigger data, and should be ignored when the game is reading this padded part of
		// the triggers section.
		for (int i = columnIndex + usedReservedPixelsCount; i < this.bitmapWidth; i++) {
			pixels[i + (rowIndex * this.bitmapWidth)] = -1;
		}

		// Then place the tileset data inside the tileset section in the bitmap file. This is the rest of
		// the bitmap file, right after the trigger section.
		for (int iterator = 0; iterator < this.tiles.length; iterator++) {
			pixels[this.bitmapWidth * triggerRowHeight + iterator] = this.tiles[iterator];
		}
		return buffer;
	}

	public boolean hasBitmap() {
		return (this.bitmapHeight * this.bitmapWidth != 0);
	}

	public boolean isMouseInDrawingBoard() {
		return this.mouseInsideDrawingBoardCheck;
	}

	public void openMapImage(BufferedImage image) {
		try {
			int[] srcTiles = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			this.editor.setUniqueAreaID((srcTiles[0] >> 16) & 0xFFFF);
			int triggerCount = srcTiles[0] & 0xFFFF;
			int triggerRows = (triggerCount / image.getWidth()) + 1;
			this.setImageSize(image.getWidth(), (image.getHeight() - triggerRows));
			if (this.triggers != null)
				this.triggers = null;
			this.triggers = new TriggerSet(image.getWidth(), (image.getHeight() - triggerRows));
			for (int i = 0; i < triggerCount; i++) {
				int x = (srcTiles[i + 1] >> 24) & 0xFF;
				int y = (srcTiles[i + 1] >> 16) & 0xFF;
				this.triggers.addTriggerById(y * image.getWidth() + x, srcTiles[i + 1]);
			}
			for (int i = 0; i < srcTiles.length - (triggerRows * image.getWidth()); i++)
				this.tiles[i] = srcTiles[i + (triggerRows * image.getWidth())];
			List<Map.Entry<Integer, Data>> list = EditorConstants.getInstance().getDatas();
			for (int i = 0; i < this.tiles.length; i++) {
				int alpha = ((this.tiles[i] >> 24) & 0xFF);
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
								int red = ((this.tiles[i] >> 16) & 0xFF);
								if (red == Integer.valueOf(d.red)) {
									this.tilesEditorID[i] = d.editorID;
								}
							}
							continue;
						case 0x05: {// Area Zone
							// Extended Tile IDs are used to differentiate tiles.
							if (alpha == Integer.valueOf(d.alpha)) {
								int blue = this.tiles[i] & 0xFF;
								if (blue == d.blue) {
									this.tilesEditorID[i] = d.editorID;
									break;
								}
							}
							continue;
						}
						default: {
							// Alpha value is only used.
							if (alpha == Integer.valueOf(d.alpha)) {
								this.tilesEditorID[i] = d.editorID;
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

	// --------------------------------------------------------------------------------------------
	// Private methods

	private void hoverOver() {
		try {
			int w = 0;
			int h = 0;

			// try...catch for X position
			try {
				w = (this.editor.input.offsetX + this.editor.input.mouseX) / Tileable.WIDTH;
			}
			catch (Exception e) {
				w = (this.editor.input.offsetX + this.editor.input.mouseX) / (LevelEditor.WIDTH * LevelEditor.SIZE);
			}

			// try catch for Y position
			try {
				h = (this.editor.input.offsetY + this.editor.input.mouseY) / Tileable.HEIGHT;
			}
			catch (Exception e) {
				h = (this.editor.input.offsetY + this.editor.input.mouseY) / (LevelEditor.WIDTH * LevelEditor.SIZE);
			}

			// checks for mouse hoving above triggers
			switch (EditorConstants.metadata) {
				case Pixel_Data:
				default:
					int value = this.tiles[h * this.bitmapWidth + w];
					// Show trigger IDs and stuffs.
					TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
					panel.alphaField.setText(Integer.toString((value >> 24) & 0xFF));
					panel.redField.setText(Integer.toString((value >> 16) & 0xFF));
					panel.greenField.setText(Integer.toString((value >> 8) & 0xFF));
					panel.blueField.setText(Integer.toString(value & 0xFF));
					panel.validate();
					break;
				case Triggers:
					// TODO(Jul/4/2020): Figure out how to display a list of triggers for a single tile in the
					// TilePropertiesPanel.
					break;
			}
		}
		catch (Exception e) {
			TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
			panel.alphaField.setText("");
			panel.redField.setText("");
			panel.greenField.setText("");
			panel.blueField.setText("");
			panel.validate();
		}
	}

	private void defaultTileProperties(Data d) {
		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		int i = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();
		Data temp = null;
		for (Map.Entry<Integer, Data> entry : EditorConstants.getInstance().getDatas()) {
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

	private void manualInputTileProperties(Data d) {
		TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
		int i = this.getMouseTileY() * this.bitmapWidth + this.getMouseTileX();
		this.tiles[i] = panel.dataValue;
		this.tilesEditorID[i] = d.editorID;
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