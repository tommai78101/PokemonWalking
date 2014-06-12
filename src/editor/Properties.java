package editor;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import editor.EditorConstants.Tools;

public class Properties extends JPanel {
	private static final long serialVersionUID = -8555733808183623384L;
	
	private static final Dimension SIZE = new Dimension(150, 80);
	
	private JComboBox<Category> tileCategory;
	private JComboBox<Data> tiles;
	private Data selectedData;
	private final LevelEditor editor;
	
	public Properties(final LevelEditor editor) {
		this.editor = editor;
		this.setLayout(new GridLayout(16, 1));
		
		tileCategory = new JComboBox<Category>();
		tileCategory.setPreferredSize(SIZE);
		new KeySelectionRenderer(tileCategory) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public String getDisplayValue(Object item) {
				Category c = (Category) item;
				return c.name;
			}
		};
		tiles = new JComboBox<Data>();
		tiles.setPreferredSize(SIZE);
		new KeySelectionRenderer(tiles) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public String getDisplayValue(Object item) {
				Data d = (Data) item;
				return d.name;
			}
		};
		
		loadCategory();
		loadTiles();
		
		tileCategory.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				EditorConstants.chooser = Tools.Properties;
				editor.input.forceCancelDrawing();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Category c = (Category) e.getItem();
					DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) tiles.getModel();
					for (Data d : c.nodes) {
						model.addElement(d);
					}
				}
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) tiles.getModel();
					model.removeAllElements();
				}
			}
		});
		tiles.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				EditorConstants.chooser = Tools.Properties;
				editor.input.forceCancelDrawing();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Data d = (Data) e.getItem();
					Properties.this.selectedData = d;
					TilePropertiesPanel panel = editor.controlPanel.getPropertiesPanel();
					panel.alphaField.setText(Integer.toString(d.alpha));
					panel.redField.setText(Integer.toString(d.red));
					panel.greenField.setText(Integer.toString(d.green));
					panel.blueField.setText(Integer.toString(d.blue));
					editor.controlPanel.setSelectedData(d);
					editor.validate();
				}
			}
		});
		
		this.add(tileCategory);
		this.add(tiles);
		
		tileCategory.setSelectedIndex(0);
		tiles.setSelectedIndex(0);
	}
	
	public Data getSelectedData() {
		return this.selectedData;
	}
	
	private void loadCategory() {
		DefaultComboBoxModel<Category> model = (DefaultComboBoxModel<Category>) this.tileCategory.getModel();
		ArrayList<Category> list = EditorConstants.getInstance().getCategories();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			model.addElement(list.get(i));
		}
	}
	
	private void loadTiles() {
		Category c = EditorConstants.getInstance().getCategories().get(0);
		DefaultComboBoxModel<Data> model = (DefaultComboBoxModel<Data>) tiles.getModel();
		for (Data d : c.nodes) {
			model.addElement(d);
		}
	}
}
