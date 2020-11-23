/**
 * 
 */
package editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

/**
 * Enhanced JFileChooser class object. Built to override the approved selection of directory so it
 * doesn't make mistakes.
 * 
 * @author tlee
 */
public class EditorFileChooser extends JFileChooser implements PropertyChangeListener {
	private static final long serialVersionUID = -649119858083751845L;

	private String filename;

	public EditorFileChooser(String filename) {
		super();
		this.addPropertyChangeListener(this);
		this.filename = filename;
	}

	/**
	 * JFileChooser override to fix the issue when double-clicking on the directory, which causes it to
	 * approve the selected directory, rather than opening the directory.
	 */
	@Override
	public void approveSelection() {
		if (this.getFileSelectionMode() == JFileChooser.FILES_AND_DIRECTORIES) {
			File selectedFile = super.getSelectedFile();
			if (selectedFile.isDirectory()) {
				super.setCurrentDirectory(selectedFile);
				return;
			}
		}
		super.approveSelection();
	}

	/**
	 * JFileChooser "Property Change" listener event used to suppress the issue of displaying the full
	 * path of a directory, in a DIRECTORIES_ONLY file selection mode.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		JFileChooser sourceChooser = (JFileChooser) evt.getSource();
		FileChooserUI chooserUI = sourceChooser.getUI();
		((BasicFileChooserUI) chooserUI).setFileName(this.filename);
	}
}
