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
	
	public int getTileIDValue() {
		return Integer.valueOf(tileIDField.getText());
	}
	
	public String getTileIDString() {
		return tileIDField.getText();
	}
	
	public int getExtendedTileIDValue() {
		return Integer.valueOf(extTileIDField.getText());
	}
	
	public String getExtendedTileIDString() {
		return extTileIDField.getText();
	}
	
	public int getTileIDGValue() {
		return Integer.valueOf(tileGGIDField.getText());
	}
	
	public String getTileIDGString() {
		return tileGGIDField.getText();
	}
	
	public int getTileIDBValue() {
		return Integer.valueOf(tileBBIDField.getText());
	}
	
	public String getTileIDBString() {
		return tileBBIDField.getText();
	}
}
