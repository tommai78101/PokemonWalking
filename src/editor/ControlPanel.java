package editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import abstracts.Tile;

public class ControlPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7481148146432931992L;
	
	public HashMap<Integer, Data> buttonCache = new HashMap<Integer, Data>();
	private TilePropertiesPanel propertiesPanel;
	private String iconName;
	private LevelEditor editor;
	private Data selectedData;
	
	public ControlPanel(LevelEditor editor) {
		this.editor = editor;
		this.selectedData = new Data();
		this.selectedData.editorID = 0;
		this.selectedData.filepath = "no_png.png";
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JPanel iconsPanel = new JPanel();
		iconsPanel.setLayout(new BoxLayout(iconsPanel, BoxLayout.Y_AXIS));
		for (Data s : editor.getResourceFilePaths()) {
			ImageIcon test = new ImageIcon(s.filepath);
			JButton button = new JButton(test) {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Dimension getMinimumSize() {
					return new Dimension(Tile.WIDTH, Tile.HEIGHT);
				}
				
				@Override
				public Dimension getSize() {
					return new Dimension(Tile.WIDTH, Tile.HEIGHT);
				}
				
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(Tile.WIDTH, Tile.HEIGHT);
				}
			};
			button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setBorder(null);
			String actionCommand = Integer.toString(s.editorID);
			button.setActionCommand(actionCommand);
			button.setName(s.filepath);
			button.addActionListener(this);
			iconsPanel.add(button);
			s.image = test.getImage();
			s.button = button;
			if (s != null) {
				buttonCache.put(s.editorID, s);
			}
		}
		
		JScrollPane scrollPanel = new JScrollPane(iconsPanel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Dimension getPreferredSize() {
				int maxWidth = 50;
				int maxHeight = Tile.HEIGHT * 20;
				Dimension dim = super.getPreferredSize();
				if (dim.width > maxWidth)
					dim.width = maxWidth;
				if (dim.height > maxHeight)
					dim.height = maxHeight;
				return dim;
			}
			
			@Override
			public Dimension getMaximumSize() {
				return getPreferredSize();
			}
		};
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.createVerticalScrollBar();
		this.add(scrollPanel);
		
		this.propertiesPanel = new TilePropertiesPanel();
		this.add(propertiesPanel);
		
		this.propertiesPanel.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();
		String[] tokens = button.getName().split("\\\\");
		String imageName = tokens[tokens.length - 1];
		if (imageName.equals("no_png.png"))
			this.iconName = "NONE";
		else
			this.iconName = (imageName.substring(0, imageName.length() - 4));
		
		//		for (Iterator<Data> it = editor.getResourceFilePaths().iterator(); it.hasNext();) {
		//			Data d = it.next();
		//			if (d.editorID == id) {
		//				this.selectedData = d;
		//				break;
		//			}
		//		}
		int editorID = Integer.valueOf(event.getActionCommand());
		Data d = this.buttonCache.get(editorID);
		if (d != null) {
			this.selectedData = d;
			this.propertiesPanel.tileIDField.setText(Integer.toString((editorID >> 24) & 0xFF));
			this.propertiesPanel.extTileIDField.setText(Integer.toString((editorID >> 16) & 0xFF));
			this.propertiesPanel.tileGGIDField.setText(Integer.toString((editorID >> 8) & 0xFF));
			this.propertiesPanel.tileBBIDField.setText(Integer.toString(editorID & 0xFF));
		}
		editor.validate();
	}
	
	public String getPickedEntityName() {
		return this.iconName;
	}
	
	public Data getSelectedData() {
		return this.selectedData;
	}
}
