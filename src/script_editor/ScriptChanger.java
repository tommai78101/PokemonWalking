package script_editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import editor.Trigger;

public class ScriptChanger extends JPanel implements ActionListener, DocumentListener {
	private static final long serialVersionUID = 1L;

	private final ScriptEditor editor;

	private JTextField nameField, idField;
	private JButton upButton, downButton, leftButton, rightButton;
	private JButton questionDialogue, affirmativeDialogue, negativeDialogue, speechDialogue;
	private JTextArea scriptArea;
	private boolean allowUpdate;
	private int movementCounter = 0;

	private static final String UP = "UP";
	private static final String DOWN = "DOWN";
	private static final String LEFT = "LEFT";
	private static final String RIGHT = "RIGHT";

	private ActionListener dialogueActions = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			JTextArea area = editor.scriptChanger.getScriptArea();
			PlainDocument doc = (PlainDocument) area.getDocument();
			switch (event.getActionCommand()) {
				case "#": {
					if (doc.getLength() > 0)
						area.append("\n#");
					else
						area.append("#");
					break;
				}
				case "?": {
					if (doc.getLength() > 0)
						area.append("\n?");
					else
						area.append("?");
					break;
				}
				case "+": {
					if (doc.getLength() > 0)
						area.append("\n+");
					else
						area.append("+");
					break;
				}
				case "-": {
					if (doc.getLength() > 0)
						area.append("\n-");
					else
						area.append("-");
					break;
				}
			}
		}
	};

	public ScriptChanger(ScriptEditor editor) {
		super();
		this.editor = editor;

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// First row
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.NONE;
		this.add(new JLabel("Name: "), c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 3.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add((nameField = new JTextField()), c);
		nameField.getDocument().addDocumentListener(this);

		// // Second row
		// c.gridx = 0;
		// c.gridy = 1;
		// c.gridwidth = 1;
		// c.weightx = 0.5;
		// c.weighty = 0.1;
		// c.fill = GridBagConstraints.NONE;
		// this.add(new JLabel("X Position: "), c);
		//
		// c.gridx = 1;
		// c.gridy = 1;
		// c.gridwidth = 1;
		// c.weightx = 3.5;
		// c.weighty = 0.1;
		// c.fill = GridBagConstraints.HORIZONTAL;
		// this.add((xField = new JTextField()), c);
		// xField.getDocument().addDocumentListener(this);
		//
		// // Third row
		// c.gridx = 0;
		// c.gridy = 2;
		// c.gridwidth = 1;
		// c.weightx = 0.5;
		// c.weighty = 0.1;
		// c.fill = GridBagConstraints.NONE;
		// this.add(new JLabel("Y Position: "), c);
		//
		// c.gridx = 1;
		// c.gridy = 2;
		// c.gridwidth = 1;
		// c.weightx = 3.5;
		// c.weighty = 0.1;
		// c.fill = GridBagConstraints.HORIZONTAL;
		// this.add((yField = new JTextField()), c);
		// yField.getDocument().addDocumentListener(this);

		// Fourth row
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.NONE;
		this.add(new JLabel("ID Value: "), c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		c.weightx = 3.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add((idField = new JTextField()), c);
		idField.getDocument().addDocumentListener(this);

		// Empty panel for adding spaces only.
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 0;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		this.add(constructScripts(), c);

		// Fifth row (panel)
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.CENTER;
		this.add(constructDirections(), c);

		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		c.weightx = 3.5;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.CENTER;
		this.add(constructDialogues(), c);

		this.setBorder(BorderFactory.createTitledBorder("Trigger:"));
		this.validate();
	}

	private JPanel constructDirections() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		final Dimension size = new Dimension(30, 10);
		final Insets inset = new Insets(0, 1, 0, 1);

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		upButton = new JButton("Up");
		upButton.setMargin(inset);
		upButton.setSize(size);
		upButton.addActionListener(this);
		upButton.setActionCommand(UP);
		top.add(upButton);

		JPanel across = new JPanel();
		across.setLayout(new BoxLayout(across, BoxLayout.X_AXIS));
		leftButton = new JButton("Left");
		leftButton.setMargin(inset);
		leftButton.setSize(size);
		leftButton.addActionListener(this);
		leftButton.setActionCommand(LEFT);
		downButton = new JButton("Down");
		downButton.setMargin(inset);
		downButton.setSize(size);
		downButton.addActionListener(this);
		downButton.setActionCommand(DOWN);
		rightButton = new JButton("Right");
		rightButton.setMargin(inset);
		rightButton.setSize(size);
		rightButton.addActionListener(this);
		rightButton.setActionCommand(RIGHT);
		across.add(leftButton);
		across.add(downButton);
		across.add(rightButton);

		panel.add(top);
		panel.add(across);
		panel.setBorder(BorderFactory.createTitledBorder("Movements:"));
		return panel;
	}

	private JPanel constructDialogues() {
		final Insets inset = new Insets(0, 1, 0, 1);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));
		speechDialogue = new JButton("#");
		speechDialogue.setMargin(inset);
		speechDialogue.setToolTipText("<html><b>Speech Dialogue:</b><br/> A normal dialogue the player is to trigger. <br/><br/><b>Usage:</b><br/> #[One-line-only Sentence]<br/><br/><b>Example:</b><br/>#Hello World.</html>");
		speechDialogue.setActionCommand("#");
		speechDialogue.addActionListener(dialogueActions);
		panel.add(speechDialogue);
		questionDialogue = new JButton("?");
		questionDialogue.setMargin(inset);
		questionDialogue.setToolTipText("<html><b>Question Dialogue:</b><br/> A question dialogue asking for the player's response to YES or NO. <br/><br/><b>Usage:</b><br/>?[One-line-only Question] <br/><br/><b>Example:</b><br/>?Do you want to trade your Bulbasaur for my Pikachu?</html>");
		questionDialogue.setActionCommand("?");
		questionDialogue.addActionListener(dialogueActions);
		panel.add(questionDialogue);
		affirmativeDialogue = new JButton("+");
		affirmativeDialogue.setMargin(inset);
		affirmativeDialogue
				.setToolTipText("<html><b>Affirmative Dialogue:</b><br/> If a question dialogue has been asked, and the player reponded to YES, this and similar consecutive dialogues will be shown. <br/><br/><b>Usage:</b><br/>+[One-line-only Sentence]<br/><br/><b>Example:</b><br/>+Great! Let's trade!</html>");
		affirmativeDialogue.setActionCommand("+");
		affirmativeDialogue.addActionListener(dialogueActions);
		panel.add(affirmativeDialogue);
		negativeDialogue = new JButton("-");
		negativeDialogue.setMargin(inset);
		negativeDialogue
				.setToolTipText("<html><b>Negative Dialogue:</b><br/> If a question dialogue has been asked, and the player reponded to NO, this and similar consecutive dialogues will be shown. <br/><br/><b>Usage:</b><br/>-[One-line-only Sentence]<br/><br/><b>Example:</b><br/>-Aw... I thought you had one.</html>");
		negativeDialogue.setActionCommand("-");
		negativeDialogue.addActionListener(dialogueActions);
		panel.add(negativeDialogue);

		panel.validate();
		panel.setBorder(BorderFactory.createTitledBorder("Dialogues (Hover for hints):"));
		return panel;
	}

	private JPanel constructScripts() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		this.scriptArea = new JTextArea();
		this.scriptArea.setLineWrap(true);
		this.scriptArea.getDocument().addDocumentListener(this);
		panel.add(new JScrollPane(this.scriptArea), BorderLayout.CENTER);
		panel.validate();
		panel.setBorder(BorderFactory.createTitledBorder("Script:"));
		return panel;
	}

	public JTextField getNameField() {
		return this.nameField;
	}

	// public JTextField getXField() {
	// return this.xField;
	// }

	// public JTextField getYField() {
	// return this.yField;
	// }

	public JTextField getIDField() {
		return this.idField;
	}

	public JTextArea getScriptArea() {
		return this.scriptArea;
	}

	public void allowFieldsToUpdate() {
		this.allowUpdate = true;
	}

	public void disallowFieldsToUpdate() {
		this.allowUpdate = false;
	}

	public void clear() {
		nameField.setText("");
		// xField.setText("");
		// yField.setText("");
		idField.setText("");
		scriptArea.setText("");
	}

	// ActionListener
	@Override
	public void actionPerformed(ActionEvent event) {
		JTextArea area = editor.scriptChanger.getScriptArea();
		PlainDocument doc = (PlainDocument) area.getDocument();
		String lastCharacter = "";
		String lastDirection = "";
		try {
			lastDirection = doc.getText(area.getCaretPosition() - 2, 1);
			lastCharacter = doc.getText(area.getCaretPosition() - 1, 1);
			if (lastDirection.equals("\n")) {
				System.out.println("__" + lastCharacter + "__LINEBREAK__");
			}
			else {
				System.out.println("__" + lastCharacter + "__" + lastDirection + "__");
			}
		}
		catch (BadLocationException e) {
			lastCharacter = "";
		}
		if (lastDirection.equals("U") || lastDirection.equals("D") || lastDirection.equals("L") || lastDirection.equals("R")) {
			switch (event.getActionCommand()) {
				case UP: {
					inputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					inputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					inputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					inputChange(doc, area, "R");
					break;
				}
			}
		}
		else if (lastCharacter.equals("^")) {
			switch (event.getActionCommand()) {
				case UP: {
					defaultInputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					defaultInputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					defaultInputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					defaultInputChange(doc, area, "R");
					break;
				}
			}
		}
		else if (lastCharacter.equals("\n") || lastCharacter.equals("")) {
			area.append("^");
			switch (event.getActionCommand()) {
				case UP: {
					defaultInputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					defaultInputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					defaultInputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					defaultInputChange(doc, area, "R");
					break;
				}
			}
		}
		else {
			area.append("\n^");
			switch (event.getActionCommand()) {
				case UP: {
					defaultInputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					defaultInputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					defaultInputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					defaultInputChange(doc, area, "R");
					break;
				}
			}
		}
	}

	private void inputChange(PlainDocument doc, JTextArea area, String directionToCompare) {
		try {
			String str = doc.getText(doc.getLength() - 2, 1);
			if (str.equals(directionToCompare)) {
				if (movementCounter < 9) {
					movementCounter++;
					doc.remove(doc.getLength() - 1, 1);
					area.append(Integer.toString(movementCounter));
				}
				else {
					movementCounter = 0;
					area.append(directionToCompare + Integer.toString(movementCounter));
				}
			}
			else {
				movementCounter = 0;
				area.append(directionToCompare + Integer.toString(movementCounter));
			}
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void defaultInputChange(PlainDocument doc, JTextArea area, String directionToCompare) {
		movementCounter = 0;
		area.append(directionToCompare + Integer.toString(movementCounter));
	}

	// DocumentListener
	@Override
	public void changedUpdate(DocumentEvent event) {
	}

	// DocumentListener
	@Override
	public void insertUpdate(DocumentEvent event) {
		if (this.allowUpdate) {
			Trigger selectedTrigger = editor.scriptViewer.getSelectedTrigger();
			if (selectedTrigger != null) {
				String test = "";

				try {
					test = editor.scriptChanger.getNameField().getText();
					if (!test.isEmpty() || !test.equals(""))
						selectedTrigger.setName(test);
				}
				catch (Exception e) {
				}

				// try {
				// test = editor.scriptChanger.getXField().getText();
				// if (!test.isEmpty() || !test.equals("")) {
				// int n = Integer.valueOf(test);
				// if (n > 0x0FF)
				// throw new NumberFormatException();
				// selectedTrigger.setTriggerPositionX((byte) (n & 0xFF));
				// }
				// }
				// catch (NumberFormatException e) {
				// JOptionPane.showMessageDialog(null, "Please input numbers in range 0 ~ 255.");
				// SwingUtilities.invokeLater(new Runnable() {
				// @Override
				// public void run() {
				// editor.scriptChanger.getXField().setText("");
				// }
				// });
				// }
				// try {
				// test = editor.scriptChanger.getYField().getText();
				// if (!test.isEmpty() || !test.equals("")) {
				// int n = Integer.valueOf(test);
				// if (n > 0x0FF)
				// throw new NumberFormatException();
				// selectedTrigger.setTriggerPositionY((byte) (n & 0xFF));
				// }
				// }
				// catch (NumberFormatException e) {
				// JOptionPane.showMessageDialog(null, "Please input numbers in range 0 ~ 255.");
				// SwingUtilities.invokeLater(new Runnable() {
				// @Override
				// public void run() {
				// editor.scriptChanger.getYField().setText("");
				// }
				// });
				// }

				try {
					test = editor.scriptChanger.getIDField().getText();
					if (!test.isEmpty() || !test.equals("")) {
						int n = Integer.valueOf(test);
						if (n > 0x0FFFF)
							throw new NumberFormatException();
						selectedTrigger.setTriggerID((short) (n & 0xFFFF));
					}
				}
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Please input numbers in range 0 ~ 65535.\n\n0 is reserved for \"Eraser\", which is used to erase triggers from the map.");
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							editor.scriptChanger.getIDField().setText("");
						}
					});
				}

				try {
					test = editor.scriptChanger.getScriptArea().getText();
					if (!test.isEmpty() || !test.equals(""))
						selectedTrigger.setScript(test);
				}
				catch (Exception e) {
				}

				JList<Trigger> list = editor.scriptViewer.getTriggerList();
				DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) list.getModel();
				model.setElementAt(selectedTrigger, list.getSelectedIndex());
			}
			if (!editor.isBeingModified()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String str = editor.getTitle();
						if (!str.endsWith("*"))
							editor.setTitle(str + "*");
						editor.setModifiedFlag(true);
					}
				});
			}
		}
	}

	// DocumentListener
	@Override
	public void removeUpdate(DocumentEvent event) {
		if (this.allowUpdate) {
			Trigger selectedTrigger = editor.scriptViewer.getSelectedTrigger();
			if (selectedTrigger != null) {
				String test = editor.scriptChanger.getNameField().getText();
				if (!test.isEmpty() || !test.equals(""))
					selectedTrigger.setName(test);
				// test = editor.scriptChanger.getXField().getText();
				// if (!test.isEmpty() || !test.equals(""))
				// selectedTrigger.setTriggerPositionX((byte) (Integer.valueOf(test) & 0xFF));
				// test = editor.scriptChanger.getYField().getText();
				// if (!test.isEmpty() || !test.equals(""))
				// selectedTrigger.setTriggerPositionY((byte) (Integer.valueOf(test) & 0xFF));
				test = editor.scriptChanger.getIDField().getText();
				if (!test.isEmpty() || !test.equals(""))
					selectedTrigger.setTriggerID((short) (Integer.valueOf(test) & 0xFFFF));
				test = editor.scriptChanger.getScriptArea().getText();
				if (!test.isEmpty() || !test.equals(""))
					selectedTrigger.setScript(test);

				JList<Trigger> list = editor.scriptViewer.getTriggerList();
				DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) list.getModel();
				model.setElementAt(selectedTrigger, list.getSelectedIndex());
			}
			if (!editor.isBeingModified()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String str = editor.getTitle();
						if (!str.endsWith("*"))
							editor.setTitle(str + "*");
						editor.setModifiedFlag(true);
					}
				});
			}
		}
	}
}
