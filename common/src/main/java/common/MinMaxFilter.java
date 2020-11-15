/**
 * 
 */
package common;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * Used for setting the min/max range for JTextFields.
 * 
 * @author tlee
 */
public class MinMaxFilter extends DocumentFilter {
	private int minimum;
	private int maximum;

	public MinMaxFilter(int min, int max) {
		this.minimum = min;
		this.maximum = max;
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		Document doc = fb.getDocument();
		String replacement = new StringBuilder(doc.getText(0, doc.getLength())).replace(offset, offset + length, text).toString();
		try {
			int value = Integer.parseInt(replacement);
			if (value < this.minimum)
				value = this.minimum;
			if (value > this.maximum)
				value = this.maximum;
			super.replace(fb, 0, doc.getLength(), String.valueOf(value), attrs);
		}
		catch (NumberFormatException e) {
			String max = Integer.toString(this.maximum);
			if (text.length() > max.length() / 2)
				text = max;
			else
				text = Integer.toString(this.minimum);
			super.replace(fb, 0, doc.getLength(), text, attrs);
		}
	}
}