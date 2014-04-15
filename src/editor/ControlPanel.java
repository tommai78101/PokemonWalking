package editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import main.MainComponent;
import abstracts.Tile;

public class ControlPanel extends JPanel implements ActionListener {
	
	private ArrayList<String> filepaths = new ArrayList<String>();
	
	public ControlPanel() {
		final File folder = new File("res");
		getAllFiles(folder);
		
		JPanel iconsPanel = new JPanel();
		iconsPanel.setLayout(new BoxLayout(iconsPanel, BoxLayout.Y_AXIS));
		
		for (String s : filepaths) {
			ImageIcon test = new ImageIcon(s);
			JLabel label = new JLabel(test) {
				@Override
				public Dimension getMinimumSize() {
					return new Dimension(Tile.WIDTH * MainComponent.GAME_SCALE, Tile.HEIGHT * MainComponent.GAME_SCALE);
				}
				
				@Override
				public Dimension getSize() {
					return new Dimension(Tile.WIDTH * MainComponent.GAME_SCALE, Tile.HEIGHT * MainComponent.GAME_SCALE);
				}
				
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(Tile.WIDTH * MainComponent.GAME_SCALE, Tile.HEIGHT * MainComponent.GAME_SCALE);
				}
			};
			label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			iconsPanel.add(label);
		}
		
		JScrollPane scrollPanel = new JScrollPane(iconsPanel) {
			@Override
			public Dimension getPreferredSize() {
				int maxWidth = 100;
				int maxHeight = Tile.HEIGHT * 20;
				Dimension dim = super.getPreferredSize();
				if (dim.width > maxWidth)
					dim.width = maxWidth;
				if (dim.height > maxHeight)
					dim.height = maxHeight;
				return dim;
			}
		};
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.createVerticalScrollBar();
		this.add(scrollPanel);
	}
	
	public void getAllFiles(final File folder) {
		for (final File entry : folder.listFiles()) {
			if (entry.isDirectory()) {
				if (!(entry.getName().equals("animation") || entry.getName().equals("player")))
					getAllFiles(entry);
			}
			else {
				String path = entry.getPath();
				if (path.endsWith(".png")) {
					filepaths.add(path);
					System.out.println(entry.getPath());
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		
	}
}
