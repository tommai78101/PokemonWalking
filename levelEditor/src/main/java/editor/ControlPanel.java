/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import common.Tileable;
import editor.EditorConstants.Tools;

public class ControlPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7481148146432931992L;

	private TilePropertiesPanel propertiesPanel; // Part of Control Panel
	@SuppressWarnings("unused")
	private String iconName; // Used to display name in the status panel of Level Editor
	private LevelEditor editor; // reference to parent
	private SpriteData selectedData; // reference to selected data.
	private Trigger selectedTrigger; // reference to selected trigger.
	private JPanel iconsPanel; // The small square icon buttons on the left of the Level Editor
	private JPanel npcsPanel; // Same small square NPC buttons panel on the left of the Level Editor.
	private JScrollPane scrollIconsPanel; // The scroll panel of the icon buttons.
	private JScrollPane scrollNpcsPanel; // The scroll panel of the NPC buttons.

	public ControlPanel(LevelEditor editor) {
		this.editor = editor;
		this.selectedData = new SpriteData();
		this.selectedData.editorID = 0;
		this.selectedData.filepath = "no_png.png";

		this.selectedTrigger = null;

		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		final EditorConstants constants = EditorConstants.getInstance();
		// For Tilesets
		this.iconsPanel = new JPanel();
		this.iconsPanel.setLayout(new BoxLayout(this.iconsPanel, BoxLayout.Y_AXIS));
		for (Map.Entry<Integer, SpriteData> entry : constants.getDatas()) {
			SpriteData d = entry.getValue();
			if (d.name.equals("Select"))
				continue;
			d.button.setActionCommand(Integer.toString(d.editorID));
			d.button.addActionListener(this);
			this.iconsPanel.add(d.button);
		}

		// For NPCs
		this.npcsPanel = new JPanel();
		this.npcsPanel.setLayout(new BoxLayout(this.npcsPanel, BoxLayout.Y_AXIS));
		for (Map.Entry<Integer, SpriteData> entry : constants.getNpcs()) {
			SpriteData d = entry.getValue();
			d.button.setActionCommand(Integer.toString(d.editorID));
			d.button.addActionListener(this);
			this.npcsPanel.add(d.button);
		}

		this.scrollIconsPanel = new JScrollPane(this.iconsPanel) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}

			@Override
			public Dimension getMinimumSize() {
				return this.getPreferredSize();
			}

			@Override
			public Dimension getPreferredSize() {
				int maxWidth = 34;
				int maxHeight = Tileable.HEIGHT * 20;
				Dimension dim = super.getPreferredSize();
				if (dim.width < maxWidth)
					dim.width = maxWidth;
				if (dim.height < maxHeight)
					dim.height = maxHeight;
				if (dim.width > maxWidth)
					dim.width = maxWidth;
				if (dim.height > maxHeight)
					dim.height = maxHeight;
				return dim;
			}
		};
		this.scrollIconsPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollIconsPanel.createVerticalScrollBar();
		this.scrollIconsPanel.setVisible(true);
		this.add(this.scrollIconsPanel);

		this.scrollNpcsPanel = new JScrollPane(this.npcsPanel) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}

			@Override
			public Dimension getMinimumSize() {
				return this.getPreferredSize();
			}

			@Override
			public Dimension getPreferredSize() {
				int maxWidth = 34;
				int maxHeight = Tileable.HEIGHT * 20;
				Dimension dim = super.getPreferredSize();
				if (dim.width < maxWidth)
					dim.width = maxWidth;
				if (dim.height < maxHeight)
					dim.height = maxHeight;
				if (dim.width > maxWidth)
					dim.width = maxWidth;
				if (dim.height > maxHeight)
					dim.height = maxHeight;
				return dim;
			}
		};
		this.scrollNpcsPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollNpcsPanel.createVerticalScrollBar();
		this.scrollNpcsPanel.setVisible(false);
		this.add(this.scrollNpcsPanel);

		this.propertiesPanel = new TilePropertiesPanel(this);
		this.add(this.propertiesPanel);

		this.propertiesPanel.setVisible(true);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (EditorConstants.metadata) {
			case Tilesets:
			case NonPlayableCharacters: {
				JButton button = (JButton) event.getSource();
				int id = Integer.parseInt(button.getActionCommand());
				Map.Entry<Integer, SpriteData> entry = EditorConstants.getInstance().getDatas().get(id);
				SpriteData d = entry.getValue();
				if (d != null) {
					button.setToolTipText(d.name);
					this.selectedData = d;
					this.iconName = d.name;
					this.editor.properties.setDataAsSelected(d);
					this.propertiesPanel.alphaInputField.setText(Integer.toString(d.alpha));
					this.propertiesPanel.redInputField.setText(Integer.toString(d.red));
					this.propertiesPanel.greenInputField.setText(Integer.toString(d.green));
					this.propertiesPanel.blueInputField.setText(Integer.toString(d.blue));
					EditorConstants.chooser = Tools.ControlPanel;
				}
				break;
			}
			case Events:
			case Triggers: {
				// Initializing the selected trigger with Eraser.
				this.selectedTrigger = new Trigger();
				this.selectedTrigger.setGameTriggerID((short) 1);
				this.selectedTrigger.setTriggerPositionX((byte) 0xFF);
				this.selectedTrigger.setTriggerPositionY((byte) 0xFF);
				break;
			}
			default:
				return;
		}
		this.editor.validate();
	}

	public LevelEditor getEditor() {
		return this.editor;
	}

	public String getPickedEntityName() {
		switch (EditorConstants.chooser) {
			case ControlPanel:
				return this.selectedData.name;
			case Properties:
				return this.selectedData.name;
		}
		return "";
	}

	public TilePropertiesPanel getPropertiesPanel() {
		return this.propertiesPanel;
	}

	public SpriteData getSelectedData() {
		switch (EditorConstants.chooser) {
			case ControlPanel:
				return this.selectedData;
			case Properties:
				return this.editor.properties.getSelectedData();
		}
		return null;
	}

	public Trigger getSelectedTrigger() {
		return this.selectedTrigger;
	}

	public void setSelectedData(SpriteData data) {
		this.selectedData = data;
	}

	public void setSelectedTrigger(Trigger t) {
		this.selectedTrigger = t;
	}

	@Override
	public void validate() {
		switch (EditorConstants.metadata) {
			case Tilesets:
				this.setIconsPanelVisible(true);
				this.setNpcsPanelVisible(false);
				this.setScrollIconsPanelVisible(true);
				this.setScrollNpcsPanelVisible(false);
				break;
			case Events:
			case Triggers:
				this.setIconsPanelVisible(false);
				this.setNpcsPanelVisible(false);
				this.setScrollIconsPanelVisible(false);
				this.setScrollNpcsPanelVisible(false);
				break;
			case NonPlayableCharacters: {
				this.setIconsPanelVisible(false);
				this.setNpcsPanelVisible(true);
				this.setScrollIconsPanelVisible(false);
				this.setScrollNpcsPanelVisible(true);
				break;
			}
			default:
				return;
		}
		super.revalidate();
		super.repaint();
	}

	private void setIconsPanelVisible(boolean value) {
		this.iconsPanel.setVisible(value);
		this.iconsPanel.setEnabled(value);
	}

	private void setNpcsPanelVisible(boolean value) {
		this.npcsPanel.setVisible(value);
		this.npcsPanel.setEnabled(value);
	}

	private void setScrollIconsPanelVisible(boolean value) {
		this.scrollIconsPanel.setVisible(value);
		int policy = value ? ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
		this.scrollIconsPanel.setVerticalScrollBarPolicy(policy);
	}

	private void setScrollNpcsPanelVisible(boolean value) {
		this.scrollNpcsPanel.setVisible(value);
		int policy = value ? ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
		this.scrollNpcsPanel.setVerticalScrollBarPolicy(policy);
	}
}
