/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package editor;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import common.MinMaxFilter;

public class TilePropertiesPanel extends JPanel implements DocumentListener {
	private static final long serialVersionUID = 1L;
	private static final Dimension SIZE = new Dimension(70, 10);
	private static final Dimension INPUT_SIZE = new Dimension(15, 15);

	public JTextField alphaField;
	public JTextField redField;
	public JTextField greenField;
	public JTextField blueField;

	public CustomJTextField areaIDInputField;
	public CustomJTextField alphaInputField;
	public CustomJTextField redInputField;
	public CustomJTextField greenInputField;
	public CustomJTextField blueInputField;

	public JCheckBox isNpcTriggerBox;
	public JCheckBox isEventTriggerBox;

	public int dataValue;
	public SpriteData selectData;
	public ControlPanel panel;

	public JLabel areaID, tileID, extendedTileID, tileSpecificID, fullDataInput;
	public JLabel npcTriggerField, eventTriggerField;

	private int lastKnownValidAreaID = 0;

	public TilePropertiesPanel(ControlPanel controlPanel) {
		this.panel = controlPanel;

		this.areaID = new CustomJLabel("Area ID:");
		this.tileID = new CustomJLabel("Tile ID:");
		this.extendedTileID = new CustomJLabel("Ext. ID:");
		this.tileSpecificID = new CustomJLabel("Other ID:");
		this.fullDataInput = new CustomJLabel("Edit Data:");
		this.npcTriggerField = new CustomJLabel("For NPC:");
		this.eventTriggerField = new CustomJLabel("For Event:");

		this.alphaField = new CustomJTextField(); // AA
		this.redField = new CustomJTextField(); // RR
		this.greenField = new CustomJTextField(); // GG
		this.blueField = new CustomJTextField(); // BB
		this.isNpcTriggerBox = new JCheckBox(); // For NPC triggers only.
		this.isEventTriggerBox = new JCheckBox(); // For event triggers only.

		this.areaIDInputField = new CustomJTextField();
		this.alphaInputField = new CustomJTextField();
		this.redInputField = new CustomJTextField();
		this.greenInputField = new CustomJTextField();
		this.blueInputField = new CustomJTextField();

		this.areaID.setHorizontalAlignment(SwingConstants.CENTER);
		this.tileID.setHorizontalAlignment(SwingConstants.CENTER);
		this.extendedTileID.setHorizontalAlignment(SwingConstants.CENTER);
		this.tileSpecificID.setHorizontalAlignment(SwingConstants.CENTER);
		this.npcTriggerField.setHorizontalAlignment(SwingConstants.CENTER);
		this.eventTriggerField.setHorizontalAlignment(SwingConstants.CENTER);

		this.alphaField.setHorizontalAlignment(SwingConstants.CENTER);
		this.redField.setHorizontalAlignment(SwingConstants.CENTER);
		this.greenField.setHorizontalAlignment(SwingConstants.CENTER);
		this.blueField.setHorizontalAlignment(SwingConstants.CENTER);
		this.isNpcTriggerBox.setHorizontalAlignment(SwingConstants.CENTER);

		this.alphaField.setEditable(false);
		this.redField.setEditable(false);
		this.greenField.setEditable(false);
		this.blueField.setEditable(false);
		this.isNpcTriggerBox.setEnabled(false);
		this.isEventTriggerBox.setEnabled(false);

		this.areaIDInputField.setHorizontalAlignment(SwingConstants.CENTER);
		this.alphaInputField.setHorizontalAlignment(SwingConstants.CENTER);
		this.redInputField.setHorizontalAlignment(SwingConstants.CENTER);
		this.greenInputField.setHorizontalAlignment(SwingConstants.CENTER);
		this.blueInputField.setHorizontalAlignment(SwingConstants.CENTER);

		// Intentionally setting the default text value for the area ID to be non-empty first.
		this.areaIDInputField.setText("0");

		// Set a min/max range for input fields when entering any value to be of unsigned values.
		((AbstractDocument) this.areaIDInputField.getDocument()).setDocumentFilter(new MinMaxFilter(0, Short.MAX_VALUE - Short.MIN_VALUE));
		((AbstractDocument) this.alphaInputField.getDocument()).setDocumentFilter(new MinMaxFilter(0, Byte.MAX_VALUE - Byte.MIN_VALUE));
		((AbstractDocument) this.redInputField.getDocument()).setDocumentFilter(new MinMaxFilter(0, Byte.MAX_VALUE - Byte.MIN_VALUE));
		((AbstractDocument) this.greenInputField.getDocument()).setDocumentFilter(new MinMaxFilter(0, Byte.MAX_VALUE - Byte.MIN_VALUE));
		((AbstractDocument) this.blueInputField.getDocument()).setDocumentFilter(new MinMaxFilter(0, Byte.MAX_VALUE - Byte.MIN_VALUE));

		this.areaIDInputField.getDocument().addDocumentListener(this);
		this.alphaInputField.getDocument().addDocumentListener(this);
		this.redInputField.getDocument().addDocumentListener(this);
		this.greenInputField.getDocument().addDocumentListener(this);
		this.blueInputField.getDocument().addDocumentListener(this);

		this.setLayout(new GridLayout(0, 1));
		this.add(this.areaID);
		this.add(this.areaIDInputField);

		this.add(this.tileID);
		this.add(this.alphaField);

		this.add(this.extendedTileID);
		this.add(this.redField);

		this.add(this.tileSpecificID);
		this.add(this.greenField);
		this.add(this.blueField);

		this.add(this.npcTriggerField);
		this.add(this.isNpcTriggerBox);

		this.add(this.eventTriggerField);
		this.add(this.isEventTriggerBox);

		this.add(this.fullDataInput);
		this.add(this.alphaInputField);
		this.add(this.redInputField);
		this.add(this.greenInputField);
		this.add(this.blueInputField);
	}

	public class CustomJLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public CustomJLabel(String string) {
			super(string);
		}

		@Override
		public Dimension getMaximumSize() {
			return TilePropertiesPanel.SIZE;
		}

		@Override
		public Dimension getMinimumSize() {
			return TilePropertiesPanel.SIZE;
		}

		@Override
		public Dimension getPreferredSize() {
			return TilePropertiesPanel.SIZE;
		}

		@Override
		public Dimension getSize() {
			return TilePropertiesPanel.SIZE;
		}
	}

	public class CustomJTextField extends JTextField {
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getMaximumSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}

		@Override
		public Dimension getMinimumSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}

		@Override
		public Dimension getPreferredSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}

		@Override
		public Dimension getSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		try {
			this.lastKnownValidAreaID = Integer.parseInt(this.areaIDInputField.getText());
			this.panel.getEditor().setUniqueAreaID(this.lastKnownValidAreaID);
		}
		catch (NumberFormatException e) {
			this.panel.getEditor().setUniqueAreaID(this.lastKnownValidAreaID);
		}

		try {
			byte a = (byte) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
			byte r = (byte) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
			byte g = (byte) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
			byte b = (byte) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
			this.dataValue = (a << 24) | (r << 16) | (g << 8) | b;

			if (this.selectData != null && this.selectData.name.equals("Select")) {
				SpriteData editedData = EditorConstants.getData(this.dataValue);
				this.panel.getEditor().drawingBoardPanel.setDataProperties(editedData);
			}
		}
		catch (Exception e) {
			this.dataValue = 0;
		}
	}

	public void clearInputFields() {
		this.areaIDInputField.setText("");
		this.alphaInputField.setText("");
		this.redInputField.setText("");
		this.greenInputField.setText("");
		this.blueInputField.setText("");
		this.selectData = null;
	}

	public void clearSelectedData() {
		this.selectData = null;
	}

	public char getAlpha() {
		try {
			return (char) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	public char getBlue() {
		try {
			return (char) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getExtendedTileIDString() {
		return this.redField.getText();
	}

	public char getGreen() {
		try {
			return (char) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	public char getRed() {
		try {
			return (char) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getTileIDGString() {
		return this.greenField.getText();
	}

	public String getTileIDString() {
		return this.alphaField.getText();
	}

	@Override
	public void insertUpdate(DocumentEvent event) {
		try {
			this.lastKnownValidAreaID = Integer.parseInt(this.areaIDInputField.getText());
			this.panel.getEditor().setUniqueAreaID(this.lastKnownValidAreaID);
		}
		catch (NumberFormatException e) {
			this.panel.getEditor().setUniqueAreaID(this.lastKnownValidAreaID);
		}

		try {
			byte a = (byte) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
			byte r = (byte) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
			byte g = (byte) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
			byte b = (byte) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
			this.dataValue = (a << 24) | (r << 16) | (g << 8) | b;

			if (this.selectData != null && this.selectData.name.equals("Select")) {
				SpriteData editedData = EditorConstants.getData(this.dataValue);
				this.panel.getEditor().drawingBoardPanel.setDataProperties(editedData);
			}
		}
		catch (Exception e) {
			this.dataValue = 0;
		}
	}

	public boolean isEventTrigger() {
		return this.isEventTriggerBox.isSelected();
	}

	/**
	 * Only for use with EditorConstants Metadata, Triggers.
	 *
	 * @return
	 */
	public boolean isNpcTrigger() {
		return this.isNpcTriggerBox.isSelected();
	}

	@Override
	public void removeUpdate(DocumentEvent event) {
		try {
			this.lastKnownValidAreaID = Integer.parseInt(this.areaIDInputField.getText());
			this.panel.getEditor().setUniqueAreaID(this.lastKnownValidAreaID);
		}
		catch (NumberFormatException e) {
			this.panel.getEditor().setUniqueAreaID(this.lastKnownValidAreaID);
		}

		try {
			byte a = (byte) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
			byte r = (byte) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
			byte g = (byte) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
			byte b = (byte) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
			this.dataValue = (a << 24) | (r << 16) | (g << 8) | b;

			if (this.selectData != null && this.selectData.name.equals("Select")) {
				SpriteData editedData = EditorConstants.getData(this.dataValue);
				this.panel.getEditor().drawingBoardPanel.setDataProperties(editedData);
			}
		}
		catch (Exception e) {
			this.dataValue = 0;
		}
	}

	/**
	 * Given a Data object, set all of the input field values based on the data's properties.
	 *
	 * @param selectedData
	 */
	public void setDataProperties(SpriteData selectedData) {
		this.areaIDInputField.setText(Integer.toString(this.panel.getEditor().getUniqueAreaID()));
		this.alphaInputField.setText(Integer.toString(selectedData.alpha));
		this.redInputField.setText(Integer.toString(selectedData.red));
		this.greenInputField.setText(Integer.toString(selectedData.green));
		this.blueInputField.setText(Integer.toString(selectedData.blue));
		this.selectData = selectedData;
	}

	@Override
	public void validate() {
		switch (EditorConstants.metadata) {
			case Tilesets:
				this.tileID.setText("Tile ID:");
				this.extendedTileID.setText("Extended ID:");
				this.tileSpecificID.setText("Other IDs:");
				this.fullDataInput.setVisible(true);
				this.areaIDInputField.setVisible(true);
				this.alphaInputField.setVisible(true);
				this.redInputField.setVisible(true);
				this.greenInputField.setVisible(true);
				this.blueInputField.setVisible(true);
				this.isNpcTriggerBox.setVisible(false);
				this.isEventTriggerBox.setVisible(false);

				// Label field
				this.areaID.setVisible(true);
				this.blueField.setVisible(true);
				this.npcTriggerField.setVisible(false);
				this.eventTriggerField.setVisible(false);
				break;
			case Events:
			case Triggers:
				this.tileID.setText("X Position:");
				this.extendedTileID.setText("Y Position:");
				this.tileSpecificID.setText("Trigger ID:");
				this.areaIDInputField.setVisible(false);
				this.fullDataInput.setVisible(false);
				this.alphaInputField.setVisible(false);
				this.redInputField.setVisible(false);
				this.greenInputField.setVisible(false);
				this.blueInputField.setVisible(false);
				this.isNpcTriggerBox.setVisible(true);
				this.isEventTriggerBox.setVisible(true);

				// Label field
				this.areaID.setVisible(false);
				this.blueField.setVisible(true);
				this.npcTriggerField.setVisible(true);
				this.eventTriggerField.setVisible(true);
				break;
			case NonPlayableCharacters: {
				this.tileID.setText("NPC Type:");
				this.extendedTileID.setText("Unique ID:");
				this.tileSpecificID.setText("NpcScriptID:");
				this.fullDataInput.setVisible(false);
				this.areaIDInputField.setVisible(false);
				this.alphaInputField.setVisible(true);
				this.redInputField.setVisible(true);
				this.greenInputField.setVisible(true);
				this.blueInputField.setVisible(false);
				this.isNpcTriggerBox.setVisible(false);
				this.isEventTriggerBox.setVisible(false);

				// Label field
				this.areaID.setVisible(false);
				this.blueField.setVisible(false);
				this.npcTriggerField.setVisible(false);
				this.eventTriggerField.setVisible(false);
				break;
			}
			default:
				return;
		}
		super.revalidate();
		super.repaint();
	}
}
