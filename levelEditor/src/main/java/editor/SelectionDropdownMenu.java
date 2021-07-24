package editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import editor.EditorConstants.Tools;

public class SelectionDropdownMenu extends JPanel {
	private static final long serialVersionUID = -8555733808183623384L;

	private static final Dimension DropdownMenuSize = new Dimension(150, 30);
	private static final Dimension LabelSize = new Dimension(150, 25);

	private JComboBox<Category> tileCategory;
	private JComboBox<Data> tiles;
	private JComboBox<Trigger> triggers;
	private Data selectedData;
	private Trigger selectedTrigger;
	private JPanel triggerEditorInfo;

	public final LevelEditor editor;

	public SelectionDropdownMenu(final LevelEditor editor) {
		this.editor = editor;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.loadTiles();
		this.loadCategory();
		this.loadTriggers();

		JLabel label = new JLabel("Tileset Category");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setMinimumSize(SelectionDropdownMenu.LabelSize);
		label.setMaximumSize(SelectionDropdownMenu.LabelSize);
		this.add(label);
		this.add(this.tileCategory);
		this.add(this.tiles);
		label = new JLabel("Triggers");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setMinimumSize(SelectionDropdownMenu.LabelSize);
		label.setMaximumSize(SelectionDropdownMenu.LabelSize);
		this.add(label);
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

	public Data getSelectedData() {
		return this.selectedData;
	}

	public JComboBox<Trigger> getTriggerList() {
		return this.triggers;
	}

	public Trigger getSelectedTrigger() {
		return this.selectedTrigger;
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
		this.tileCategory.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				EditorConstants.chooser = Tools.Properties;
				SelectionDropdownMenu.this.editor.input.forceCancelDrawing();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Category c = (Category) e.getItem();
					DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) SelectionDropdownMenu.this.tiles.getModel();
					for (Data d : c.nodes) {
						model.addElement(d);
					}
				}
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) SelectionDropdownMenu.this.tiles.getModel();
					model.removeAllElements();
				}
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
				Data d = (Data) item;
				return d.name;
			}
		};
		this.tiles.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				EditorConstants.chooser = Tools.Properties;
				SelectionDropdownMenu.this.editor.input.forceCancelDrawing();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Data d = (Data) e.getItem();
					SelectionDropdownMenu.this.selectedData = d;
					TilePropertiesPanel panel = SelectionDropdownMenu.this.editor.controlPanel.getPropertiesPanel();
					panel.alphaInputField.setText(Integer.toString(d.alpha));
					panel.redInputField.setText(Integer.toString(d.red));
					panel.greenInputField.setText(Integer.toString(d.green));
					panel.blueInputField.setText(Integer.toString(d.blue));
					SelectionDropdownMenu.this.editor.controlPanel.setSelectedData(d);
				}
			}
		});

		// No need to add all of the data elements into this model. Rather, remove all of them, so the
		// Category can load the Tile model elements in.
		// Category c = EditorConstants.getInstance().getCategories().get(0);
		DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) this.tiles.getModel();
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
		this.triggers.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
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
			}
		});

		DefaultComboBoxModel<Trigger> model = (DefaultComboBoxModel<Trigger>) this.triggers.getModel();
		List<Trigger> list = EditorConstants.getInstance().getTriggers();
		for (Trigger t : list) {
			if (t != null)
				model.addElement(t);
		}
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

	public void setDataAsSelected(Data data) {
		DefaultComboBoxModel<Category> categoryModel = (DefaultComboBoxModel<Category>) this.tileCategory.getModel();
		DefaultComboBoxModel<Data> tilesModel = (DefaultComboBoxModel<Data>) this.tiles.getModel();
		int categorySize = categoryModel.getSize();

		// Tiles Category
		nested:
		for (int i = 0; i < categorySize; i++) {
			Category category = categoryModel.getElementAt(i);
			if (category.matchesData(data)) {
				// Tiles
				int tilesSize = category.nodes.size();
				for (int j = 0; j < tilesSize; j++) {
					Data tilesData = category.nodes.get(j);
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
			case Pixel_Data:
				this.tileCategory.setEnabled(true);
				this.tiles.setEnabled(true);
				this.triggers.setEnabled(false);
				this.triggerEditorInfo.setVisible(false);
				break;
			case Triggers:
				this.tileCategory.setEnabled(false);
				this.tiles.setEnabled(false);
				this.triggers.setEnabled(true);
				this.triggerEditorInfo.setVisible(true);
				break;
		}
		super.validate();
	}
}
