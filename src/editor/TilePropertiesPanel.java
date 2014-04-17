package editor;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TilePropertiesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public JTextField tileIDField;
	public JTextField extTileIDField;
	public JTextField tileGGIDField;
	public JTextField tileBBIDField;
	
	@SuppressWarnings({"serial"})
	public TilePropertiesPanel() {
		super();
		
		final Dimension size = new Dimension(60, 10);
		final Dimension inputSize = new Dimension(15, 15);
		
		JLabel tileID = new JLabel("Tile ID:") {
			@Override
			public Dimension getSize() {
				return size;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return size;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return size;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return size;
			}
		};
		JLabel extendedTileID = new JLabel("Ext. ID:") {
			@Override
			public Dimension getSize() {
				return size;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return size;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return size;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return size;
			}
		};
		JLabel tileSpecificID = new JLabel("Other ID:") {
			@Override
			public Dimension getSize() {
				return size;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return size;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return size;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return size;
			}
		};
		tileIDField = new JTextField() {
			@Override
			public Dimension getSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return inputSize;
			}
		};
		extTileIDField = new JTextField() {
			@Override
			public Dimension getSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return inputSize;
			}
		}; //RR
		tileGGIDField = new JTextField() {
			@Override
			public Dimension getSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return inputSize;
			}
		}; //GG
		tileBBIDField = new JTextField() {
			@Override
			public Dimension getSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getPreferredSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return inputSize;
			}
			
			@Override
			public Dimension getMinimumSize() {
				return inputSize;
			}
		}; //BB
		tileID.setHorizontalAlignment(SwingConstants.CENTER);
		extendedTileID.setHorizontalAlignment(SwingConstants.CENTER);
		tileSpecificID.setHorizontalAlignment(SwingConstants.CENTER);
		tileIDField.setHorizontalAlignment(SwingConstants.CENTER);
		extTileIDField.setHorizontalAlignment(SwingConstants.CENTER);
		tileGGIDField.setHorizontalAlignment(SwingConstants.CENTER);
		tileBBIDField.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.setLayout(new GridLayout(0, 1));
		this.add(tileID);
		this.add(tileIDField);
		this.add(extendedTileID);
		this.add(extTileIDField);
		this.add(tileSpecificID);
		this.add(tileGGIDField);
		this.add(tileBBIDField);
	}
	
	public byte getAlpha() {
		try {
			return (byte) (Integer.valueOf(tileIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDString() {
		return tileIDField.getText();
	}
	
	public byte getRed() {
		try {
			return (byte) (Integer.valueOf(extTileIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getExtendedTileIDString() {
		return extTileIDField.getText();
	}
	
	public byte getGreen() {
		try {
			return (byte) (Integer.valueOf(tileGGIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDGString() {
		return tileGGIDField.getText();
	}
	
	public byte getBlue() {
		try {
			return (byte) (Integer.valueOf(tileBBIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDBString() {
		return tileBBIDField.getText();
	}
}
