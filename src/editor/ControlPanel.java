package editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import abstracts.Tile;

public class ControlPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7481148146432931992L;
	
	private TilePropertiesPanel propertiesPanel; //Part of Control Panel 
	private String iconName; //Used to display name in the status panel of Level Editor
	private LevelEditor editor; //reference to parent
	private Data selectedData; //reference to selected data.
	
	public ControlPanel(LevelEditor editor) {
		this.editor = editor;
		this.selectedData = new Data();
		this.selectedData.editorID = 0;
		this.selectedData.filepath = "no_png.png";
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JPanel iconsPanel = new JPanel();
		iconsPanel.setLayout(new BoxLayout(iconsPanel, BoxLayout.Y_AXIS));
		EditorConstants constants = EditorConstants.getInstance();
		for (Iterator<Map.Entry<Integer, Data>> it = constants.getSortedTileMap().iterator(); it.hasNext();) {
			Map.Entry<Integer, Data> entry = it.next();
			Data d = entry.getValue();
			d.button.setActionCommand(Integer.toString((d.alpha << 24) | (d.red << 16) | (d.green << 8) | (d.blue)));
			d.button.addActionListener(this);
			iconsPanel.add(d.button);
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
		scrollPanel.setVisible(true);
		this.add(scrollPanel);
		
		this.propertiesPanel = new TilePropertiesPanel();
		this.add(propertiesPanel);
		
		this.propertiesPanel.setVisible(true);
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();
		int color = Integer.parseInt(button.getActionCommand());
		Data d = EditorConstants.getInstance().getTileMap().get(color);
		if (d != null) {
			this.selectedData = d;
			this.iconName = d.name;
			this.propertiesPanel.tileIDField.setText(Byte.toString(d.alpha));
			this.propertiesPanel.extTileIDField.setText(Byte.toString(d.red));
			this.propertiesPanel.tileGGIDField.setText(Byte.toString(d.green));
			this.propertiesPanel.tileBBIDField.setText(Byte.toString(d.blue));
		}
		editor.validate();
	}
	
	public String getPickedEntityName() {
		return this.iconName;
	}
	
	public Data getSelectedData() {
		return this.selectedData;
	}
	
	public TilePropertiesPanel getPropertiesPanel() {
		return this.propertiesPanel;
	}
}
