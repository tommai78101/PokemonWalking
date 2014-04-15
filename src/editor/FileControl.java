package editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JPanel;

public class FileControl extends JPanel implements ActionListener {
	private static final String ID_TAG = "id";
	private static final int NUM_BUTTONS = 3;
	
	HashMap<String, JButton> buttonCache = new HashMap<String, JButton>();
	
	public FileControl() {
		super();
		
		this.setLayout(new GridLayout(1, NUM_BUTTONS));
		
		for (int i = 0; i < NUM_BUTTONS; i++) {
			JButton button = new JButton("Test " + Integer.toString(i));
			button.addActionListener(this);
			String actionCommand = Integer.toString(i);
			button.setActionCommand(actionCommand);
			buttonCache.put(actionCommand, button);
			this.add(button);
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = ((JButton) event.getSource()).getActionCommand();
		try {
			int value = Integer.valueOf(command);
			switch (value) {
				case 0:
				case 1:
				case 2:
					System.out.println("Test " + Integer.toString(value) + " has been pressed.");
					break;
			}
		}
		catch (NumberFormatException e) {
			return;
		}
	}
	
}
