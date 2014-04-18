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
		
		this.add(mousePosition);
		this.add(statusMessage);
	}
	
	public void setMousePositionText(int x, int y) {
		this.mousePosition.setText("[" + x + ", " + y + "]");
		this.validate();
	}
	
	public void setStatusMessageText(String msg) {
		this.statusMessage.setText(msg);
		this.validate();
	}
}
