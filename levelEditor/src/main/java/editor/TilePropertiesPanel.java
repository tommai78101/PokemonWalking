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
			return SIZE;
		}

		@Override
		public Dimension getPreferredSize() {
			return SIZE;
		}

		@Override
		public Dimension getMaximumSize() {
			return SIZE;
		}

		@Override
		public Dimension getMinimumSize() {
			return SIZE;
		}
	}

	public class CustomJTextField extends JTextField {
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getSize() {
			return INPUT_SIZE;
		}

		@Override
		public Dimension getPreferredSize() {
			return INPUT_SIZE;
		}

		@Override
		public Dimension getMaximumSize() {
			return INPUT_SIZE;
		}

		@Override
		public Dimension getMinimumSize() {
			return INPUT_SIZE;
		}
	}

	public TilePropertiesPanel() {
		super();

		tileID = new CustomJLabel("Tile ID:");
		extendedTileID = new CustomJLabel("Ext. ID:");
		tileSpecificID = new CustomJLabel("Other ID:");
		fullDataInput = new CustomJLabel("Edit Data:");

		alphaField = new CustomJTextField(); // AA
		redField = new CustomJTextField(); // RR
		greenField = new CustomJTextField(); // GG
		blueField = new CustomJTextField(); // BB

		alphaInputField = new CustomJTextField();
		redInputField = new CustomJTextField();
		greenInputField = new CustomJTextField();
		blueInputField = new CustomJTextField();

		tileID.setHorizontalAlignment(SwingConstants.CENTER);
		extendedTileID.setHorizontalAlignment(SwingConstants.CENTER);
		tileSpecificID.setHorizontalAlignment(SwingConstants.CENTER);

		alphaField.setHorizontalAlignment(SwingConstants.CENTER);
		redField.setHorizontalAlignment(SwingConstants.CENTER);
		greenField.setHorizontalAlignment(SwingConstants.CENTER);
		blueField.setHorizontalAlignment(SwingConstants.CENTER);

		alphaField.setEditable(false);
		redField.setEditable(false);
		greenField.setEditable(false);
		blueField.setEditable(false);

		alphaInputField.setHorizontalAlignment(SwingConstants.CENTER);
		redInputField.setHorizontalAlignment(SwingConstants.CENTER);
		greenInputField.setHorizontalAlignment(SwingConstants.CENTER);
		blueInputField.setHorizontalAlignment(SwingConstants.CENTER);

		alphaInputField.getDocument().addDocumentListener(this);
		redInputField.getDocument().addDocumentListener(this);
		greenInputField.getDocument().addDocumentListener(this);
		blueInputField.getDocument().addDocumentListener(this);

		this.setLayout(new GridLayout(0, 1));
		this.add(tileID);
		this.add(alphaField);
		this.add(extendedTileID);
		this.add(redField);
		this.add(tileSpecificID);
		this.add(greenField);
		this.add(blueField);
		this.add(fullDataInput);
		this.add(alphaInputField);
		this.add(redInputField);
		this.add(greenInputField);
		this.add(blueInputField);

		this.validate();
	}

	public char getAlpha() {
		try {
			return (char) (Integer.valueOf(alphaInputField.getText()) & 0xFF);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getTileIDString() {
		return alphaField.getText();
	}

	public char getRed() {
		try {
			return (char) (Integer.valueOf(redInputField.getText()) & 0xFF);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getExtendedTileIDString() {
		return redField.getText();
	}

	public char getGreen() {
		try {
			return (char) (Integer.valueOf(greenInputField.getText()) & 0xFF);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getTileIDGString() {
		return greenField.getText();
	}

	public char getBlue() {
		try {
			return (char) (Integer.valueOf(blueInputField.getText()) & 0xFF);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public void clearInputFields() {
		this.alphaInputField.setText("");
		this.redInputField.setText("");
		this.greenInputField.setText("");
		this.blueInputField.setText("");
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		try {
			byte a = (byte) (Integer.valueOf(this.alphaInputField.getText()) & 0xFF);
			byte r = (byte) (Integer.valueOf(this.redInputField.getText()) & 0xFF);
			byte g = (byte) (Integer.valueOf(this.greenInputField.getText()) & 0xFF);
			byte b = (byte) (Integer.valueOf(this.blueInputField.getText()) & 0xFF);
			this.dataValue = (a << 24) | (r << 16) | (g << 8) | b;
		} catch (Exception e) {
			dataValue = 0;
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
		} catch (Exception e) {
			dataValue = 0;
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
		} catch (Exception e) {
			dataValue = 0;
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
