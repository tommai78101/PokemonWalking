package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import common.Debug;
import editor.EditorConstants.Tools;

public class SelectionDropdownMenu extends JPanel {
	private static final long serialVersionUID = -8555733808183623384L;

	private static final Dimension DropdownMenuSize = new Dimension(150, 30);
	private static final Dimension LabelSize = new Dimension(150, 25);

	private JLabel tileCategoryLabel;
	private JLabel triggersLabel;
	private JComboBox<Category> tileCategory;
	private JComboBox<SpriteData> tiles;
	private JComboBox<Trigger> triggers;
	private SpriteData selectedData;
	private Trigger selectedTrigger;
	private JPanel triggerEditorInfo;

	public final LevelEditor editor;

	public SelectionDropdownMenu(final LevelEditor editor) {
		this.editor = editor;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.loadTiles();
		this.loadCategory();
		this.loadTriggers();

		this.tileCategoryLabel = new JLabel("Tileset Category");
		this.tileCategoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.tileCategoryLabel.setMinimumSize(SelectionDropdownMenu.LabelSize);
		this.tileCategoryLabel.setMaximumSize(SelectionDropdownMenu.LabelSize);
		this.add(this.tileCategoryLabel);
		this.add(this.tileCategory);
		this.add(this.tiles);

		this.triggersLabel = new JLabel("Triggers");
		this.triggersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.triggersLabel.setMinimumSize(SelectionDropdownMenu.LabelSize);
		this.triggersLabel.setMaximumSize(SelectionDropdownMenu.LabelSize);
		this.add(this.triggersLabel);
		this.add(this.triggers);

		this.triggerEditorInfo = new JPanel();
		this.triggerEditorInfo.setLayout(new BoxLayout(this.triggerEditorInfo, BoxLayout.Y_AXIS));
		this.triggerEditorInfo.add(Box.createVerticalStrut(20));
		JLabel cyanlabel = new JLabel("Cyan: Selected Trigger");
		cyanlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.triggerEditorInfo.add(cyanlabel);
		JLabel yellowlabel = new JLabel("Yellow: Other triggers");
		yellowlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.triggerEditorInfo.add(yellowlabel);
		this.add(this.triggerEditorInfo);

		this.tileCategory.setSelectedIndex(0);
		this.tiles.setSelectedIndex(0);
		this.triggers.setSelectedIndex(0);
		this.validate();
	}

	public SpriteData getSelectedData() {
		return this.selectedData;
	}

	public Trigger getSelectedTrigger() {
		return this.selectedTrigger;
	}

	public JComboBox<Trigger> getTriggerList() {
		return this.triggers;
	}

	public void reloadTriggers() {
		if (this.triggers != null) {
			DefaultComboBoxModel<Trigger> model = (DefaultComboBoxModel<Trigger>) this.triggers.getModel();
			model.removeAllElements();

			Trigger trigger = new Trigger();
			trigger.setTriggerID((short) 0);
			trigger.setName("Eraser");
			model.addElement(trigger);

			JList<Trigger> list = this.editor.scriptEditor.scriptViewer.getTriggerList();
			DefaultListModel<Trigger> scriptModel = (DefaultListModel<Trigger>) list.getModel();
			for (int i = 0; i < scriptModel.getSize(); i++) {
				model.addElement(scriptModel.get(i));
			}
		}
	}

	public void setDataAsSelected(SpriteData data) {
		DefaultComboBoxModel<Category> categoryModel = (DefaultComboBoxModel<Category>) this.tileCategory.getModel();
		DefaultComboBoxModel<SpriteData> tilesModel = (DefaultComboBoxModel<SpriteData>) this.tiles.getModel();
		int categorySize = categoryModel.getSize();

		// Tiles Category
		nested:
		for (int i = 0; i < categorySize; i++) {
			Category category = categoryModel.getElementAt(i);
			if (category.matchesData(data)) {
				// Tiles
				int tilesSize = category.nodes.size();
				for (int j = 0; j < tilesSize; j++) {
					SpriteData tilesData = category.nodes.get(j);
					if (tilesData.compare(data)) {
						// Do all of this in 1 go.
						this.editor.controlPanel.getPropertiesPanel().clearSelectedData();
						categoryModel.setSelectedItem(category);
						tilesModel.setSelectedItem(tilesData);
						break nested;
					}
				}
			}
		}
	}

	@Override
	public void validate() {
		switch (EditorConstants.metadata) {
			case Tilesets:
				this.showTileCategory();
				this.hideTriggers();
				break;
			case Triggers:
				this.hideTileCategory();
				this.showTriggers();
				break;
			case NonPlayableCharacters: {
				this.hideTileCategory();
				this.hideTriggers();
				break;
			}
		}
		super.validate();
	}

	private void hideTileCategory() {
		this.tileCategoryLabel.setVisible(false);
		this.tileCategory.setVisible(false);
		this.tiles.setVisible(false);
	}

	private void hideTriggers() {
		this.triggersLabel.setVisible(false);
		this.triggers.setVisible(false);
		this.triggerEditorInfo.setVisible(false);
	}

	private void loadCategory() {
		this.tileCategory = new JComboBox<>();
		this.tileCategory.setPreferredSize(SelectionDropdownMenu.DropdownMenuSize);
		this.tileCategory.setMaximumSize(SelectionDropdownMenu.DropdownMenuSize);
		this.tileCategory.setMinimumSize(SelectionDropdownMenu.DropdownMenuSize);
		new KeySelectionRenderer(this.tileCategory) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(Object item) {
				Category c = (Category) item;
				return c.name;
			}
		};
		this.tileCategory.addItemListener(e -> {
			EditorConstants.chooser = Tools.Properties;
			SelectionDropdownMenu.this.editor.input.forceCancelDrawing();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Category c = (Category) e.getItem();
				DefaultComboBoxModel<SpriteData> model1 = (DefaultComboBoxModel<SpriteData>) SelectionDropdownMenu.this.tiles.getModel();
				for (SpriteData d : c.nodes) {
					model1.addElement(d);
				}
			}
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				DefaultComboBoxModel<SpriteData> model2 = (DefaultComboBoxModel<SpriteData>) SelectionDropdownMenu.this.tiles.getModel();
				model2.removeAllElements();
			}
		});

		DefaultComboBoxModel<Category> model = (DefaultComboBoxModel<Category>) this.tileCategory.getModel();
		List<Category> list = EditorConstants.getInstance().getCategories();
		int size = list.size();
		model.removeAllElements();
		for (int i = 0; i < size; i++) {
			model.addElement(list.get(i));
		}
	}

	private void loadTiles() {
		this.tiles = new JComboBox<>();
		this.tiles.setPreferredSize(SelectionDropdownMenu.DropdownMenuSize);
		this.tiles.setMaximumSize(SelectionDropdownMenu.DropdownMenuSize);
		this.tiles.setMinimumSize(SelectionDropdownMenu.DropdownMenuSize);
		new KeySelectionRenderer(this.tiles) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(Object item) {
				SpriteData d = (SpriteData) item;
				return d.name;
			}
		};
		this.tiles.addItemListener(e -> {
			EditorConstants.chooser = Tools.Properties;
			SelectionDropdownMenu.this.editor.input.forceCancelDrawing();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				SpriteData d = (SpriteData) e.getItem();
				SelectionDropdownMenu.this.selectedData = d;
				TilePropertiesPanel panel = SelectionDropdownMenu.this.editor.controlPanel.getPropertiesPanel();
				panel.alphaInputField.setText(Integer.toString(d.alpha));
				panel.redInputField.setText(Integer.toString(d.red));
				panel.greenInputField.setText(Integer.toString(d.green));
				panel.blueInputField.setText(Integer.toString(d.blue));
				SelectionDropdownMenu.this.editor.controlPanel.setSelectedData(d);
			}
		});

		// No need to add all of the data elements into this model. Rather, remove all of them, so the
		// Category can load the Tile model elements in.
		// Category c = EditorConstants.getInstance().getCategories().get(0);
		DefaultComboBoxModel<SpriteData> model = (DefaultComboBoxModel<SpriteData>) this.tiles.getModel();
		model.removeAllElements();
	}

	private void loadTriggers() {
		this.triggers = new JComboBox<>();
		this.triggers.setPreferredSize(SelectionDropdownMenu.DropdownMenuSize);
		this.triggers.setMaximumSize(SelectionDropdownMenu.DropdownMenuSize);
		this.triggers.setMinimumSize(SelectionDropdownMenu.DropdownMenuSize);
		new KeySelectionRenderer(this.triggers) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(Object item) {
				Trigger t = (Trigger) item;
				return t.getName();
			}
		};
		this.triggers.addItemListener(e -> {
			EditorConstants.chooser = Tools.Properties;
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Trigger t = (Trigger) e.getItem();
				TilePropertiesPanel panel = SelectionDropdownMenu.this.editor.controlPanel.getPropertiesPanel();
				panel.alphaField.setText(Integer.toString(t.getPositionX()));
				panel.redField.setText(Integer.toString(t.getPositionY()));
				panel.greenField.setText(Integer.toString((t.getTriggerID() >> 8) & 0xFF));
				panel.blueField.setText(Integer.toString(t.getTriggerID() & 0xFF));
				SelectionDropdownMenu.this.selectedTrigger = t;
				SelectionDropdownMenu.this.editor.controlPanel.setSelectedTrigger(t);
				SelectionDropdownMenu.this.editor.validate();
			}
		});

		DefaultComboBoxModel<Trigger> model = (DefaultComboBoxModel<Trigger>) this.triggers.getModel();
		List<Trigger> list = EditorConstants.getInstance().getTriggers();
		if (list.isEmpty()) {
			Debug.error("The default level editor triggers list shouldn't be empty. It should at least contain Eraser.");
		}
		for (Trigger t : list) {
			if (t != null)
				model.addElement(t);
		}
	}

	private void showTileCategory() {
		this.tileCategoryLabel.setVisible(true);
		this.tileCategory.setVisible(true);
		this.tiles.setVisible(true);
	}

	private void showTriggers() {
		this.triggersLabel.setVisible(true);
		this.triggers.setVisible(true);
		this.triggerEditorInfo.setVisible(true);
	}
}
