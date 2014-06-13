/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

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
