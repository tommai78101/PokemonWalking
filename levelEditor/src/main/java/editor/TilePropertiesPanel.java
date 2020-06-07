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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TilePropertiesPanel extends JPanel implements DocumentListener {
	private static final long serialVersionUID = 1L;

	public JTextField alphaField;
	public JTextField redField;
	public JTextField greenField;
	public JTextField blueField;

	public CustomJTextField alphaInputField;
	public CustomJTextField redInputField;
	public CustomJTextField greenInputField;
	public CustomJTextField blueInputField;

	public int dataValue;
	public Data selectData;
	public ControlPanel panel;

	public JLabel tileID, extendedTileID, tileSpecificID, fullDataInput;

	private static final Dimension SIZE = new Dimension(70, 10);
	private static final Dimension INPUT_SIZE = new Dimension(15, 15);

	public class CustomJLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public CustomJLabel(String string) {
			super(string);
		}

		@Override
		public Dimension getSize() {
			return TilePropertiesPanel.SIZE;
		}

		@Override
		public Dimension getPreferredSize() {
			return TilePropertiesPanel.SIZE;
		}

		@Override
		public Dimension getMaximumSize() {
			return TilePropertiesPanel.SIZE;
		}

		@Override
		public Dimension getMinimumSize() {
			return TilePropertiesPanel.SIZE;
		}
	}

	public class CustomJTextField extends JTextField {
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}

		@Override
		public Dimension getPreferredSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}

		@Override
		public Dimension getMaximumSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}

		@Override
		public Dimension getMinimumSize() {
			return TilePropertiesPanel.INPUT_SIZE;
		}
	}

	public TilePropertiesPanel(ControlPanel controlPanel) {
		super();

		this.panel = controlPanel;

		this.tileID = new CustomJLabel("Tile ID:");
		this.extendedTileID = new CustomJLabel("Ext. ID:");
		this.tileSpecificID = new CustomJLabel("Other ID:");
		this.fullDataInput = new CustomJLabel("Edit Data:");

		this.alphaField = new CustomJTextField(); // AA
		this.redField = new CustomJTextField(); // RR
		this.greenField = new CustomJTextField(); // GG
		this.blueField = new CustomJTextField(); // BB

		this.alphaInputField = new CustomJTextField();
		this.redInputField = new CustomJTextField();
		this.greenInputField = new CustomJTextField();
		this.blueInputField = new CustomJTextField();

		this.tileID.setHorizontalAlignment(SwingConstants.CENTER);
		this.extendedTileID.setHorizontalAlignment(SwingConstants.CENTER);
		this.tileSpecificID.setHorizontalAlignment(SwingConstants.CENTER);

		this.alphaField.setHorizontalAlignment(SwingConstants.CENTER);
		this.redField.setHorizontalAlignment(SwingConstants.CENTER);
		this.greenField.setHorizontalAlignment(SwingConstants.CENTER);
		this.blueField.setHorizontalAlignment(SwingConstants.CENTER);

		this.alphaField.setEditable(false);
		this.redField.setEditable(false);
		this.greenField.setEditable(false);
		this.blueField.setEditable(false);

		this.alphaInputField.setHorizontalAlignment(SwingConstants.CENTER);
		this.redInputField.setHorizontalAlignment(SwingConstants.CENTER);
		this.greenInputField.setHorizontalAlignment(SwingConstants.CENTER);
		this.blueInputField.setHorizontalAlignment(SwingConstants.CENTER);

		this.alphaInputField.getDocument().addDocumentListener(this);
		this.redInputField.getDocument().addDocumentListener(this);
		this.greenInputField.getDocument().addDocumentListener(this);
		this.blueInputField.getDocument().addDocumentListener(this);

		this.setLayout(new GridLayout(0, 1));
		this.add(this.tileID);
		this.add(this.alphaField);
		this.add(this.extendedTileID);
		this.add(this.redField);
		this.add(this.tileSpecificID);
		this.add(this.greenField);
		this.add(this.blueField);
		this.add(this.fullDataInput);
		this.add(this.alphaInputField);
		this.add(this.redInputField);
		this.add(this.greenInputField);
		this.add(this.blueInputField);

		this.validate();
	}

	public char getAlpha() {
		try {
			return (char) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getTileIDString() {
		return this.alphaField.getText();
	}

	public char getRed() {
		try {
			return (char) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
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

	public String getTileIDGString() {
		return this.greenField.getText();
	}

	public char getBlue() {
		try {
			return (char) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
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

	/**
	 * Given a Data object, set all of the input field values based on the data's properties.
	 * 
	 * @param selectedData
	 */
	public void setDataProperties(Data selectedData) {
		this.areaIDInputField.setText(Integer.toString(this.panel.getEditor().getUniqueAreaID()));
		this.alphaInputField.setText(Integer.toString(selectedData.alpha));
		this.redInputField.setText(Integer.toString(selectedData.red));
		this.greenInputField.setText(Integer.toString(selectedData.green));
		this.blueInputField.setText(Integer.toString(selectedData.blue));
		this.selectData = this.panel.getEditor().properties.getSelectedData();
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		try {
			byte a = (byte) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
			byte r = (byte) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
			byte g = (byte) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
			byte b = (byte) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
			this.dataValue = (a << 24) | (r << 16) | (g << 8) | b;

			if (this.selectData != null && this.selectData.name.equals("Select")) {
				Data editedData = EditorConstants.getData(this.dataValue);
				this.panel.getEditor().drawingBoardPanel.setDataProperties(editedData);
			}
		}
		catch (Exception e) {
			this.dataValue = 0;
		}
	}

	@Override
	public void insertUpdate(DocumentEvent event) {
		try {
			byte a = (byte) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
			byte r = (byte) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
			byte g = (byte) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
			byte b = (byte) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
			this.dataValue = (a << 24) | (r << 16) | (g << 8) | b;

			if (this.selectData != null && this.selectData.name.equals("Select")) {
				Data editedData = EditorConstants.getData(this.dataValue);
				this.panel.getEditor().drawingBoardPanel.setDataProperties(editedData);
			}
		}
		catch (Exception e) {
			this.dataValue = 0;
		}
	}

	@Override
	public void removeUpdate(DocumentEvent event) {
		try {
			byte a = (byte) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
			byte r = (byte) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
			byte g = (byte) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
			byte b = (byte) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
			this.dataValue = (a << 24) | (r << 16) | (g << 8) | b;

			if (this.selectData != null && this.selectData.name.equals("Select")) {
				Data editedData = EditorConstants.getData(this.dataValue);
				this.panel.getEditor().drawingBoardPanel.setDataProperties(editedData);
			}
		}
		catch (Exception e) {
			this.dataValue = 0;
		}
	}

	@Override
	public void validate() {
		super.validate();
		switch (EditorConstants.metadata) {
			case Pixel_Data:
				this.tileID.setText("Tile ID:");
				this.extendedTileID.setText("Extended ID:");
				this.tileSpecificID.setText("Other IDs:");
				this.fullDataInput.setVisible(true);
				this.alphaInputField.setVisible(true);
				this.redInputField.setVisible(true);
				this.greenInputField.setVisible(true);
				this.blueInputField.setVisible(true);
				break;
			case Triggers:
				this.tileID.setText("X Position:");
				this.extendedTileID.setText("Y Position:");
				this.tileSpecificID.setText("Trigger ID:");
				this.fullDataInput.setVisible(false);
				this.alphaInputField.setVisible(false);
				this.redInputField.setVisible(false);
				this.greenInputField.setVisible(false);
				this.blueInputField.setVisible(false);

				break;
		}
	}
}
