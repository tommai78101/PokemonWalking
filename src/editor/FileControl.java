package editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JPanel;

public class FileControl extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private String[] tags = new String[]{"New", "Save", "Open"};
	private LevelEditor editor;
	HashMap<String, JButton> buttonCache = new HashMap<String, JButton>();
	
	public FileControl(LevelEditor editor) {
		super();
		this.editor = editor;
		this.setLayout(new GridLayout(1, tags.length));
		
		for (int i = 0; i < tags.length; i++) {
			JButton button = new JButton(tags[i]);
			button.addActionListener(this);
			String actionCommand = Integer.toString(i);
			button.setActionCommand(actionCommand);
			buttonCache.put(actionCommand, button);
			this.add(button);
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();
		String command = button.getActionCommand();
		try {
			int value = Integer.valueOf(command);
			switch (value) {
				case 0: //New
				{
					editor.drawingBoardPanel.newImage();
					break;
				}
				case 1: //Save
					System.out.println("Save");
					break;
				case 2: //Open
					System.out.println("Open");
					break;
			}
		}
		catch (NumberFormatException e) {
			return;
		}
	}
	
}
