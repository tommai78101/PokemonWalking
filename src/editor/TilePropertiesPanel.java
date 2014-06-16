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

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import editor.EditorConstants.Tools;

public class TilePropertiesPanel extends JPanel implements DocumentListener {
	private static final long serialVersionUID = 1L;
	
	public JTextField alphaField;
	public JTextField redField;
	public JTextField greenField;
	public JTextField blueField;
	public Data selectedData;
	public JLabel tileID, extendedTileID, tileSpecificID;
	private static final Dimension SIZE = new Dimension(70, 10);
	private static final Dimension INPUT_SIZE = new Dimension(15, 15);
	
	@SuppressWarnings({ "serial" })
	public TilePropertiesPanel() {
		super();
		
		this.selectedData = new Data();
		
		tileID = new JLabel("Tile ID:") {
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
		};
		extendedTileID = new JLabel("Ext. ID:") {
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
		};
		tileSpecificID = new JLabel("Other ID:") {
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
		};
		alphaField = new JTextField() {
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
		};
		redField = new JTextField() {
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
		}; // RR
		greenField = new JTextField() {
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
		}; // GG
		blueField = new JTextField() {
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
		}; // BB
		tileID.setHorizontalAlignment(SwingConstants.CENTER);
		extendedTileID.setHorizontalAlignment(SwingConstants.CENTER);
		tileSpecificID.setHorizontalAlignment(SwingConstants.CENTER);
		alphaField.setHorizontalAlignment(SwingConstants.CENTER);
		redField.setHorizontalAlignment(SwingConstants.CENTER);
		greenField.setHorizontalAlignment(SwingConstants.CENTER);
		blueField.setHorizontalAlignment(SwingConstants.CENTER);
		alphaField.getDocument().addDocumentListener(this);
		redField.getDocument().addDocumentListener(this);
		greenField.getDocument().addDocumentListener(this);
		blueField.getDocument().addDocumentListener(this);
		
		this.setLayout(new GridLayout(0, 1));
		this.add(tileID);
		this.add(alphaField);
		this.add(extendedTileID);
		this.add(redField);
		this.add(tileSpecificID);
		this.add(greenField);
		this.add(blueField);
	}
	
	public char getAlpha() {
		try {
			return (char) (Integer.valueOf(alphaField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDString() {
		return alphaField.getText();
	}
	
	public char getRed() {
		try {
			return (char) (Integer.valueOf(redField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getExtendedTileIDString() {
		return redField.getText();
	}
	
	public char getGreen() {
		try {
			return (char) (Integer.valueOf(greenField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDGString() {
		return greenField.getText();
	}
	
	public char getBlue() {
		try {
			return (char) (Integer.valueOf(blueField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDBString() {
		return blueField.getText();
	}
	
	public Data getSelectedData() {
		return this.selectedData;
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		EditorConstants.chooser = Tools.TileProperties;
		try {
			int alpha = Integer.parseInt(this.alphaField.getText());
			int red = Integer.parseInt(this.redField.getText());
			int green = Integer.parseInt(this.greenField.getText());
			int blue = Integer.parseInt(this.blueField.getText());
			this.selectedData = EditorConstants.getData(alpha, red, green, blue);
			if (selectedData.areaTypeIncluded) {
				switch (selectedData.areaTypeIDType) {
					case ALPHA:
					default:
						selectedData.alpha = alpha;
						break;
					case RED:
						selectedData.red = red;
						break;
					case GREEN:
						selectedData.green = green;
						break;
					case BLUE:
						selectedData.blue = blue;
						break;
				}
			}
		}
		catch (Exception e) {
			EditorConstants.chooser = Tools.ControlPanel;
		}
		
	}
	
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		EditorConstants.chooser = Tools.TileProperties;
		try {
			int alpha = Integer.parseInt(this.alphaField.getText());
			int red = Integer.parseInt(this.redField.getText());
			int green = Integer.parseInt(this.greenField.getText());
			int blue = Integer.parseInt(this.blueField.getText());
			this.selectedData = EditorConstants.getData(alpha, red, green, blue);
			if (selectedData.areaTypeIncluded) {
				switch (selectedData.areaTypeIDType) {
					case ALPHA:
					default:
						selectedData.alpha = alpha;
						break;
					case RED:
						selectedData.red = red;
						break;
					case GREEN:
						selectedData.green = green;
						break;
					case BLUE:
						selectedData.blue = blue;
						break;
				}
			}
		}
		catch (Exception e) {
			EditorConstants.chooser = Tools.ControlPanel;
		}
	}
	
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		EditorConstants.chooser = Tools.TileProperties;
		try {
			int alpha = Integer.parseInt(this.alphaField.getText());
			int red = Integer.parseInt(this.redField.getText());
			int green = Integer.parseInt(this.greenField.getText());
			int blue = Integer.parseInt(this.blueField.getText());
			this.selectedData = EditorConstants.getData(alpha, red, green, blue);
			if (selectedData.areaTypeIncluded) {
				switch (selectedData.areaTypeIDType) {
					case ALPHA:
					default:
						selectedData.alpha = alpha;
						break;
					case RED:
						selectedData.red = red;
						break;
					case GREEN:
						selectedData.green = green;
						break;
					case BLUE:
						selectedData.blue = blue;
						break;
				}
			}
		}
		catch (Exception e) {
			EditorConstants.chooser = Tools.ControlPanel;
		}
	}
	
	@Override
	public void validate(){
		super.validate();
		switch (EditorConstants.metadata){
			case Pixel_Data:
				this.tileID.setText("Tile ID:");
				this.extendedTileID.setText("Extended ID:");
				this.tileSpecificID.setText("Other IDs:");
				break;
			case Triggers:
				this.tileID.setText("X Position:");
				this.extendedTileID.setText("Y Position:");
				this.tileSpecificID.setText("Trigger ID:");
				break;
		}
	}
}
