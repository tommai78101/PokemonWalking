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
		}; // RR
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
		}; // GG
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
		}; // BB
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
	
	public char getAlpha() {
		try {
			return (char) (Integer.valueOf(tileIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDString() {
		return tileIDField.getText();
	}
	
	public char getRed() {
		try {
			return (char) (Integer.valueOf(extTileIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getExtendedTileIDString() {
		return extTileIDField.getText();
	}
	
	public char getGreen() {
		try {
			return (char) (Integer.valueOf(tileGGIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDGString() {
		return tileGGIDField.getText();
	}
	
	public char getBlue() {
		try {
			return (char) (Integer.valueOf(tileBBIDField.getText()) & 0xFF);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public String getTileIDBString() {
		return tileBBIDField.getText();
	}
}
