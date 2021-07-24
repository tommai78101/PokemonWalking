/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel centerPanel;
	private JPanel rightPanel;
	private JLabel mousePosition;
	private JLabel statusMessage;
	private JLabel checksumLabel;

	public StatusPanel() {
		super();

		// this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.setLayout(new GridBagLayout());

		this.centerPanel = new JPanel();
		this.rightPanel = new JPanel();

		this.mousePosition = new JLabel();
		this.statusMessage = new JLabel();
		this.checksumLabel = new JLabel();

		this.centerPanel.add(this.mousePosition);
		this.centerPanel.add(this.statusMessage);
		this.rightPanel.add(this.checksumLabel);

		// Left corner: mouse positions
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0 / 10.0;
		this.add(this.centerPanel, constraints);

		// Empty space.
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridx = 1;
		constraints.gridwidth = 8;
		constraints.weightx = 8.0 / 10.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JPanel(), constraints);

		// Right corner: checksum hash string
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridx = 9;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0 / 10.0;
		this.add(this.rightPanel);
	}

	public void setMousePositionText(int x, int y) {
		this.mousePosition.setText("[" + x + ", " + y + "]");
		this.update();
	}

	public void setStatusMessageText(String msg) {
		this.statusMessage.setText(msg);
		this.update();
	}

	public void setChecksumLabel(String checksum) {
		this.checksumLabel.setText(checksum);
		this.update();
	}

	private void update() {
		super.revalidate();
		super.repaint();
	}
}
