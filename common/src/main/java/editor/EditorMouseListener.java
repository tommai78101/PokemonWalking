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
	private String currentName = "Untitled.script";
	private String oldName = "";

	public EditorMouseListener(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
		MetalFileChooserUI ui = (MetalFileChooserUI) this.fileChooser.getUI();
		ui.setFileName(this.currentName);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			File file = this.fileChooser.getSelectedFile();
			if (file != null && !file.isDirectory()) {
				this.currentName = file.getName();
			}
			else {
				this.oldName = file.getName();
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
					this.currentName = file.getName();
				}
			}
		}
		if (!this.oldName.equals(this.currentName)) {
			MetalFileChooserUI ui = (MetalFileChooserUI) this.fileChooser.getUI();
			ui.setFileName(this.currentName);
			this.oldName = this.currentName;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		MouseEvent newEvent = new MouseEvent(
		    this.fileChooser,
		    e.getID(),
		    e.getWhen(),
		    e.getModifiersEx(),
		    e.getX(),
		    e.getY(),
		    1,
		    false
		);
		this.mouseClicked(newEvent);
	}
}
