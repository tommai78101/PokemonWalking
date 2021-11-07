package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;

import common.Debug;
import editor.EditorConstants.Tools;

public class SelectionDropdownMenu extends JPanel {
	private static final long serialVersionUID = -8555733808183623384L;

	private static final Dimension DropdownMenuSize = new Dimension(150, 30);
	private static final Dimension LabelSize = new Dimension(150, 25);

	// Tilesets
	private JLabel tileCategoryLabel;
	private JComboBox<Category> tileCategory;
	private JComboBox<SpriteData> tiles;

	// Game Triggers
	private JLabel gameTriggersLabel;
	private JComboBox<Trigger> gameTriggers;
	private JPanel gameTriggerEditorInfo;

	// Events
	private JLabel eventTriggersLabel;
	private JComboBox<Trigger> eventTriggers;
	private JPanel eventTriggerEditorInfo;

	private SpriteData selectedData;
	private Trigger selectedTrigger;

	public final LevelEditor editor;

	public SelectionDropdownMenu(final LevelEditor editor) {
		this.editor = editor;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.loadTiles();
		this.loadCategory();
		this.loadGameTriggers();
		this.loadEventTriggers();

		// Temporary variables to be used within the current scope.
		JLabel cyanLabel;
		JLabel yellowLabel;

		// Tilesets
		this.tileCategoryLabel = new JLabel("Tileset Category");
		this.tileCategoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.tileCategoryLabel.setMinimumSize(SelectionDropdownMenu.LabelSize);
		this.tileCategoryLabel.setMaximumSize(SelectionDropdownMenu.LabelSize);
		this.add(this.tileCategoryLabel);
		this.add(this.tileCategory);
		this.add(this.tiles);

		// Game triggers
		this.gameTriggersLabel = new JLabel("Triggers");
		this.gameTriggersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.gameTriggersLabel.setMinimumSize(SelectionDropdownMenu.LabelSize);
		this.gameTriggersLabel.setMaximumSize(SelectionDropdownMenu.LabelSize);
		this.add(this.gameTriggersLabel);
		this.add(this.gameTriggers);

		// Game Trigger information panel
		this.gameTriggerEditorInfo = new JPanel();
		this.gameTriggerEditorInfo.setLayout(new BoxLayout(this.gameTriggerEditorInfo, BoxLayout.Y_AXIS));
		this.gameTriggerEditorInfo.add(Box.createVerticalStrut(20));
		cyanLabel = new JLabel("Cyan: Selected Trigger");
		cyanLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.gameTriggerEditorInfo.add(cyanLabel);
		yellowLabel = new JLabel("Yellow: Other triggers");
		yellowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.gameTriggerEditorInfo.add(yellowLabel);
		this.add(this.gameTriggerEditorInfo);

		// Event triggers
		this.eventTriggersLabel = new JLabel("Events");
		this.eventTriggersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.eventTriggersLabel.setMinimumSize(SelectionDropdownMenu.LabelSize);
		this.eventTriggersLabel.setMaximumSize(SelectionDropdownMenu.LabelSize);
		this.add(this.eventTriggersLabel);
		this.add(this.eventTriggers);

		// Event Trigger information panel
		this.eventTriggerEditorInfo = new JPanel();
		this.eventTriggerEditorInfo.setLayout(new BoxLayout(this.eventTriggerEditorInfo, BoxLayout.Y_AXIS));
		this.eventTriggerEditorInfo.add(Box.createVerticalStrut(20));
		cyanLabel = new JLabel("Cyan: Selected Event");
		cyanLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.eventTriggerEditorInfo.add(cyanLabel);
		yellowLabel = new JLabel("Yellow: Other events");
		yellowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.eventTriggerEditorInfo.add(yellowLabel);
		this.add(this.eventTriggerEditorInfo);

		this.tileCategory.setSelectedIndex(0);
		this.tiles.setSelectedIndex(0);
		this.gameTriggers.setSelectedIndex(0);
		this.eventTriggers.setSelectedIndex(0);

		this.validate();
	}

	public SpriteData getSelectedData() {
		return this.selectedData;
	}

	public Trigger getSelectedTrigger() {
		return this.selectedTrigger;
	}

	public JComboBox<Trigger> getTriggerList() {
		return this.gameTriggers;
	}

	public void reloadTriggers(ListModel<Trigger> scriptModel) {
		// If any of the trigger combo box models are null, we do not reload anything.
		if (this.gameTriggers == null || this.eventTriggers == null) {
			Debug.warn("Unable to reload game and/or event triggers.");
			return;
		}

		DefaultComboBoxModel<Trigger> triggerModel = (DefaultComboBoxModel<Trigger>) this.gameTriggers.getModel();
		DefaultComboBoxModel<Trigger> eventModel = (DefaultComboBoxModel<Trigger>) this.eventTriggers.getModel();
		triggerModel.removeAllElements();
		eventModel.removeAllElements();

		Trigger trigger = new Trigger();
		trigger.setGameTriggerID((short) 0);
		trigger.setName("Eraser");
		triggerModel.addElement(trigger);
		eventModel.addElement(trigger);

		for (int i = 0; i < scriptModel.getSize(); i++) {
			Trigger elem = scriptModel.getElementAt(i);
			if (elem.isEventTrigger())
				eventModel.addElement(elem);
			else
				triggerModel.addElement(elem);
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
			case Tilesets: {
				this.showTileCategory();
				this.hideGameTriggers();
				this.hideEventTriggers();
				break;
			}
			case Triggers: {
				this.hideTileCategory();
				this.showGameTriggers();
				this.hideEventTriggers();
				break;
			}
			case NonPlayableCharacters: {
				this.hideTileCategory();
				this.hideGameTriggers();
				this.hideEventTriggers();
				break;
			}
			case Events: {
				this.hideTileCategory();
				this.hideGameTriggers();
				this.showEventTriggers();
				break;
			}
			default:
				return;
		}
		super.validate();
	}

	private void hideEventTriggers() {
		this.eventTriggersLabel.setVisible(false);
		this.eventTriggers.setVisible(false);
		this.eventTriggerEditorInfo.setVisible(false);
	}

	private void hideGameTriggers() {
		this.gameTriggersLabel.setVisible(false);
		this.gameTriggers.setVisible(false);
		this.gameTriggerEditorInfo.setVisible(false);
	}

	private void hideTileCategory() {
		this.tileCategoryLabel.setVisible(false);
		this.tileCategory.setVisible(false);
		this.tiles.setVisible(false);
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

	private void loadEventTriggers() {
		this.eventTriggers = new JComboBox<>();
		this.eventTriggers.setPreferredSize(SelectionDropdownMenu.DropdownMenuSize);
		this.eventTriggers.setMaximumSize(SelectionDropdownMenu.DropdownMenuSize);
		this.eventTriggers.setMinimumSize(SelectionDropdownMenu.DropdownMenuSize);
		new KeySelectionRenderer(this.eventTriggers) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(Object item) {
				Trigger t = (Trigger) item;
				return t.getName();
			}
		};
		this.eventTriggers.addItemListener(e -> {
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

		DefaultComboBoxModel<Trigger> model = (DefaultComboBoxModel<Trigger>) this.eventTriggers.getModel();
		List<Trigger> list = EditorConstants.getInstance().getTriggers();
		if (list.isEmpty()) {
			Debug.error("The default level editor triggers list shouldn't be empty. It should at least contain Eraser.");
		}
		for (Trigger t : list) {
			if (t != null)
				model.addElement(t);
		}
	}

	private void loadGameTriggers() {
		this.gameTriggers = new JComboBox<>();
		this.gameTriggers.setPreferredSize(SelectionDropdownMenu.DropdownMenuSize);
		this.gameTriggers.setMaximumSize(SelectionDropdownMenu.DropdownMenuSize);
		this.gameTriggers.setMinimumSize(SelectionDropdownMenu.DropdownMenuSize);
		new KeySelectionRenderer(this.gameTriggers) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(Object item) {
				Trigger t = (Trigger) item;
				return t.getName();
			}
		};
		this.gameTriggers.addItemListener(e -> {
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

		DefaultComboBoxModel<Trigger> model = (DefaultComboBoxModel<Trigger>) this.gameTriggers.getModel();
		List<Trigger> list = EditorConstants.getInstance().getTriggers();
		if (list.isEmpty()) {
			Debug.error("The default level editor triggers list shouldn't be empty. It should at least contain Eraser.");
		}
		for (Trigger t : list) {
			if (t != null)
				model.addElement(t);
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

	private void showEventTriggers() {
		this.eventTriggersLabel.setVisible(true);
		this.eventTriggers.setVisible(true);
		this.eventTriggerEditorInfo.setVisible(true);
	}

	private void showGameTriggers() {
		this.gameTriggersLabel.setVisible(true);
		this.gameTriggers.setVisible(true);
		this.gameTriggerEditorInfo.setVisible(true);
	}

	private void showTileCategory() {
		this.tileCategoryLabel.setVisible(true);
		this.tileCategory.setVisible(true);
		this.tiles.setVisible(true);
	}
}
