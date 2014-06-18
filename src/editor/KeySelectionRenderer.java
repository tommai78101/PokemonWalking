package editor;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

@SuppressWarnings("unchecked")
public abstract class KeySelectionRenderer extends BasicComboBoxRenderer implements JComboBox.KeySelectionManager {
	private static final long serialVersionUID = 2334229124492928364L;
	private long currentTime;
	private long lastTime;
	private final long timeFactor;
	private String prefix = "";
	
	/**
	 * This class can be used as the renderer and KeySelectionManager for an Object added to the ComboBoxModel.
	 * 
	 * <p>
	 * The class must be extended and the getDisplayValue() method must be implemented. This method will return a String to be rendered in the JComboBox. The same String will be used to do key selection of an item in the ComboBoxModel.
	 * 
	 * @param box
	 *            A JComboBox object that is to use this KeySelectionRenderer.
	 */
	public KeySelectionRenderer(JComboBox box){
		box.setRenderer(this);
		box.setKeySelectionManager(this);
		
		Long value = (Long) UIManager.get("ComboBox.timeFactor");
		timeFactor = (value == null ? 250L : value.longValue());
	}
	
	/**
	 * Implements the renderer.
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object item, int index, boolean isSelected, boolean hasFocus) {
		super.getListCellRendererComponent(list, item, index, isSelected, hasFocus);
		if (item != null) {
			this.setText(getDisplayValue(item));
		}
		return this;
	}
	
	/**
	 * Implements the KeySelectionManager.
	 * */
	@Override
	public int selectionForKey(char aKey, ComboBoxModel model) {
		this.currentTime = System.currentTimeMillis();
		
		// Get the index of the currently selected item
		int size = model.getSize();
		int startIndex = -1;
		Object selectedItem = model.getSelectedItem();
		if (selectedItem != null) {
			for (int i = 0; i < size; i++) {
				if (selectedItem == model.getElementAt(i)) {
					startIndex = i;
					break;
				}
			}
		}
		
		// Determine the "prefix" to be used when searching the model. The
		// prefix can be a single letter or multiple letters depending on how
		// fast the user has been typing and on which letter has been typed.
		if (currentTime - lastTime < timeFactor) {
			if ((prefix.length() == 1) && (aKey == prefix.charAt(0)))
				// Subsequent same key presses move the keyboard focus to the next
				// object that starts with the same letter.
				startIndex++;
			else
				prefix += aKey;
		}
		else {
			startIndex++;
			prefix = "" + aKey;
		}
		
		lastTime = currentTime;
		
		// Search from the current selection and wrap when no match is found
		if (startIndex < 0 || startIndex >= size)
			startIndex = 0;
		int index = getNextMatch(prefix, startIndex, size, model);
		if (index < 0)
			// Wrap
			index = getNextMatch(prefix, 0, startIndex, model);
		return index;
	}
	
	/**
	 * Finds the index of the item in the model that starts with the prefix.
	 */
	private int getNextMatch(String prefix, int start, int end, ComboBoxModel model) {
		for (int i = start; i < end; i++) {
			Object item = model.getElementAt(i);
			if (item != null) {
				String displayValue = getDisplayValue(item).toLowerCase();
				if (displayValue.startsWith(prefix))
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * This method must be implemented in the extended class.
	 *
	 * @param item
	 *            an item from the ComboBoxModel
	 * @returns a String containing the text to be rendered for this item.
	 */
	public abstract String getDisplayValue(Object item);
}
