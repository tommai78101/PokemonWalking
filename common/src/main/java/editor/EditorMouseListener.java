/**
 * 
 */
package editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.plaf.metal.MetalFileChooserUI;

/**
 * For handling the selection of files for the level and script editor.
 * 
 * @author tlee
 */
public class EditorMouseListener implements MouseListener {
	private JFileChooser fileChooser;

	public EditorMouseListener(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			File file = this.fileChooser.getSelectedFile();
			if (file != null) {
				MetalFileChooserUI ui = (MetalFileChooserUI) this.fileChooser.getUI();
				ui.setFileName(file.getName());
			}
		}
		else if (e.getClickCount() == 2) {
			File file = this.fileChooser.getSelectedFile();
			if (file != null) {
				if (file.isDirectory()) {
					this.fileChooser.setCurrentDirectory(file);
				}
				else if (file.isFile()) {
					this.fileChooser.setSelectedFile(file);
				}
				MetalFileChooserUI ui = (MetalFileChooserUI) this.fileChooser.getUI();
				ui.setFileName(file.getName());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}
