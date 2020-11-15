/**
 * 
 */
package editor;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * Enhanced JFileChooser class object. Built to override the approved selection of directory so it doesn't make mistakes.
 * 
 * @author tlee
 */
public class EditorFileChooser extends JFileChooser {
	private static final long serialVersionUID = -649119858083751845L;

	@Override
	public void approveSelection() {
		File selectedFile = super.getSelectedFile();
		if (selectedFile.isDirectory()) {
			super.setCurrentDirectory(selectedFile);
			return;
		}
		super.approveSelection();
	}
}
