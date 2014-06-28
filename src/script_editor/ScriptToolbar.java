package script_editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ScriptToolbar extends JPanel implements ActionListener {
	private final ScriptEditor editor;
	private final String[] tags = {
			"Test", ""
	};
	private final HashMap<String, JButton> buttonCache = new HashMap<String, JButton>();
	
	public ScriptToolbar(ScriptEditor editor) {
		super();
		this.editor = editor;
		this.setLayout(new GridLayout(1, tags.length));
		
		createButtons();
	}
	
	private void createButtons() {
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].isEmpty() || tags[i].equals("")) {
				this.add(new JSeparator(SwingConstants.VERTICAL));
				continue;
			}
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
		switch (Integer.valueOf(event.getActionCommand())) {
			case 0: { // Test
				break;
			}
		}
		
	}
}
