package editor;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import editor.EditorConstants.Tools;

public class Properties extends JPanel {
	private static final long serialVersionUID = -8555733808183623384L;

	private static final Dimension SIZE = new Dimension(150, 80);

	private JComboBox<Category> tileCategory;
	private JComboBox<Data> tiles;
	private JComboBox<Trigger> triggers;
	private Data selectedData;
	private Trigger selectedTrigger;

	public final LevelEditor editor;

	public Properties(final LevelEditor editor) {
		this.editor = editor;
		this.setLayout(new GridLayout(16, 1));

		this.loadTiles();
		this.loadCategory();
		this.loadTriggers();

		JLabel label = new JLabel("Tileset Category");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(label);
		this.add(this.tileCategory);
		this.add(this.tiles);
		label = new JLabel("Triggers");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(label);
		this.add(this.triggers);

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
		this.tileCategory.setPreferredSize(Properties.SIZE);
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
				Properties.this.editor.input.forceCancelDrawing();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Category c = (Category) e.getItem();
					DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) Properties.this.tiles.getModel();
					for (Data d : c.nodes) {
						model.addElement(d);
					}
				}
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) Properties.this.tiles.getModel();
					model.removeAllElements();
				}
			}
		});

		DefaultComboBoxModel<Category> model = (DefaultComboBoxModel<Category>) this.tileCategory.getModel();
		List<Category> list = EditorConstants.getInstance().getCategories();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			model.addElement(list.get(i));
		}

	}

	private void loadTiles() {
		this.tiles = new JComboBox<>();
		this.tiles.setPreferredSize(Properties.SIZE);
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
				Properties.this.editor.input.forceCancelDrawing();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Data d = (Data) e.getItem();
					Properties.this.selectedData = d;
					TilePropertiesPanel panel = Properties.this.editor.controlPanel.getPropertiesPanel();
					panel.alphaInputField.setText(Integer.toString(d.alpha));
					panel.redInputField.setText(Integer.toString(d.red));
					panel.greenInputField.setText(Integer.toString(d.green));
					panel.blueInputField.setText(Integer.toString(d.blue));
					Properties.this.editor.controlPanel.setSelectedData(d);
					Properties.this.editor.validate();
				}
			}
		});

		Category c = EditorConstants.getInstance().getCategories().get(0);
		DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) this.tiles.getModel();
		for (Data d : c.nodes) {
			model.addElement(d);
		}

	}

	private void loadTriggers() {
		this.triggers = new JComboBox<>();
		this.triggers.setPreferredSize(Properties.SIZE);
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
					TilePropertiesPanel panel = Properties.this.editor.controlPanel.getPropertiesPanel();
					panel.alphaField.setText(Integer.toString(t.getPositionX()));
					panel.redField.setText(Integer.toString(t.getPositionY()));
					panel.greenField.setText(Integer.toString((t.getTriggerID() >> 8) & 0xFF));
					panel.blueField.setText(Integer.toString(t.getTriggerID() & 0xFF));
					Properties.this.selectedTrigger = t;
					Properties.this.editor.controlPanel.setSelectedTrigger(t);
					Properties.this.editor.validate();
				}
			}
		});

		DefaultComboBoxModel<Trigger> model = (DefaultComboBoxModel<Trigger>) this.triggers.getModel();
		List<Trigger> list = EditorConstants.getInstance().getTriggers();
		for (Trigger t : list) {
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

			this.revalidate();
		}
	}

	@Override
	public void validate() {
		switch (EditorConstants.metadata) {
			case Pixel_Data:
				this.tileCategory.setEnabled(true);
				this.tiles.setEnabled(true);
				this.triggers.setEnabled(false);
				break;
			case Triggers:
				this.tileCategory.setEnabled(false);
				this.tiles.setEnabled(false);
				this.triggers.setEnabled(true);
				break;
		}
		super.validate();
	}
}
