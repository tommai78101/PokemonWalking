/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package editor;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel mousePosition;
	private JLabel statusMessage;

	public StatusPanel() {
		super();

		this.setLayout(new FlowLayout(FlowLayout.CENTER));

		this.mousePosition = new JLabel();
		this.statusMessage = new JLabel();

		this.add(this.mousePosition);
		this.add(this.statusMessage);
	}

	public void setMousePositionText(int x, int y) {
		this.mousePosition.setText("[" + x + ", " + y + "]");
		super.revalidate();
		super.repaint();
	}

	public void setStatusMessageText(String msg) {
		this.statusMessage.setText(msg);
		super.revalidate();
		super.repaint();
	}
}
