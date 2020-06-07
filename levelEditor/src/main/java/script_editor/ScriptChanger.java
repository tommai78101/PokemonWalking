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
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import common.MinMaxFilter;
import editor.Trigger;

public class ScriptChanger extends JPanel implements ActionListener, DocumentListener {
	private static final long serialVersionUID = 1L;

	private final ScriptEditor editor;

	// TODO (6/25/2015): ID Field needs to default to a number > 0. New triggers
	// currently does not auto-set itself.

	private JTextField nameField, idField;
	private JButton upButton, downButton, leftButton, rightButton;
	private JButton questionDialogue, affirmativeDialogue, negativeDialogue, speechDialogue;
	private JTextArea scriptArea;
	private boolean allowUpdate;
	private boolean isEnabled;
	private int movementCounter = 0;

	private static final String UP = "UP";
	private static final String DOWN = "DOWN";
	private static final String LEFT = "LEFT";
	private static final String RIGHT = "RIGHT";

	private ActionListener dialogueActions = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			JTextArea area = ScriptChanger.this.editor.scriptChanger.getScriptArea();
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
		this.add((this.nameField = new JTextField()), c);
		this.nameField.getDocument().addDocumentListener(this);

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
		this.add((this.idField = new JTextField()), c);
		((AbstractDocument) this.idField.getDocument()).setDocumentFilter(new MinMaxFilter(1, Short.MAX_VALUE - Short.MIN_VALUE));
		this.idField.getDocument().addDocumentListener(this);

		// Empty panel for adding spaces only.
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 0;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		this.add(this.constructScripts(), c);

		// Fifth row (panel)
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.CENTER;
		this.add(this.constructDirections(), c);

		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;
		c.weightx = 3.5;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.CENTER;
		this.add(this.constructDialogues(), c);

		this.setBorder(BorderFactory.createTitledBorder("Trigger:"));
		this.validate();

		this.disableComponent();
	}

	private JPanel constructDirections() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		final Dimension size = new Dimension(30, 10);
		final Insets inset = new Insets(0, 1, 0, 1);

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		this.upButton = new JButton("Up");
		this.upButton.setMargin(inset);
		this.upButton.setSize(size);
		this.upButton.addActionListener(this);
		this.upButton.setActionCommand(ScriptChanger.UP);
		top.add(this.upButton);

		JPanel across = new JPanel();
		across.setLayout(new BoxLayout(across, BoxLayout.X_AXIS));
		this.leftButton = new JButton("Left");
		this.leftButton.setMargin(inset);
		this.leftButton.setSize(size);
		this.leftButton.addActionListener(this);
		this.leftButton.setActionCommand(ScriptChanger.LEFT);
		this.downButton = new JButton("Down");
		this.downButton.setMargin(inset);
		this.downButton.setSize(size);
		this.downButton.addActionListener(this);
		this.downButton.setActionCommand(ScriptChanger.DOWN);
		this.rightButton = new JButton("Right");
		this.rightButton.setMargin(inset);
		this.rightButton.setSize(size);
		this.rightButton.addActionListener(this);
		this.rightButton.setActionCommand(ScriptChanger.RIGHT);
		across.add(this.leftButton);
		across.add(this.downButton);
		across.add(this.rightButton);

		panel.add(top);
		panel.add(across);
		panel.setBorder(BorderFactory.createTitledBorder("Movements:"));
		return panel;
	}

	private JPanel constructDialogues() {
		final Insets inset = new Insets(0, 1, 0, 1);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.speechDialogue = new JButton("#");
		this.speechDialogue.setMargin(inset);
		this.speechDialogue.setToolTipText(
			"<html><b>Speech Dialogue:</b><br/> A normal dialogue the player is to trigger. <br/><br/><b>Usage:</b><br/> #[One-line-only Sentence]<br/><br/><b>Example:</b><br/>#Hello World.</html>"
		);
		this.speechDialogue.setActionCommand("#");
		this.speechDialogue.addActionListener(this.dialogueActions);
		panel.add(this.speechDialogue);
		this.questionDialogue = new JButton("?");
		this.questionDialogue.setMargin(inset);
		this.questionDialogue.setToolTipText(
			"<html><b>Question Dialogue:</b><br/> A question dialogue asking for the player's response to YES or NO.<br/><br/><b>WARNING:</b><br/>A single question must be followed by an Affirmative and a Negative Dialogue.<br/><br/><b>Usage:</b><br/>?[One-line-only Question] <br/><br/><b>Example:</b><br/>?Do you want to trade your Bulbasaur for my Pikachu?<br/>+Great! Let's trade!<br/>-Aw... I thought you had one.</html>"
		);
		this.questionDialogue.setActionCommand("?");
		this.questionDialogue.addActionListener(this.dialogueActions);
		panel.add(this.questionDialogue);
		this.affirmativeDialogue = new JButton("+");
		this.affirmativeDialogue.setMargin(inset);
		this.affirmativeDialogue.setToolTipText(
			"<html><b>Affirmative Dialogue:</b><br/> If a question dialogue has been asked, and the player reponded to YES, this and similar consecutive dialogues will be shown. <br/><br/><b>Usage:</b><br/>+[One-line-only Sentence]<br/><br/><b>Example:</b><br/>+Great! Let's trade!</html>"
		);
		this.affirmativeDialogue.setActionCommand("+");
		this.affirmativeDialogue.addActionListener(this.dialogueActions);
		panel.add(this.affirmativeDialogue);
		this.negativeDialogue = new JButton("-");
		this.negativeDialogue.setMargin(inset);
		this.negativeDialogue.setToolTipText(
			"<html><b>Negative Dialogue:</b><br/> If a question dialogue has been asked, and the player reponded to NO, this and similar consecutive dialogues will be shown. <br/><br/><b>Usage:</b><br/>-[One-line-only Sentence]<br/><br/><b>Example:</b><br/>-Aw... I thought you had one.</html>"
		);
		this.negativeDialogue.setActionCommand("-");
		this.negativeDialogue.addActionListener(this.dialogueActions);
		panel.add(this.negativeDialogue);

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
		this.nameField.setText("");
		// xField.setText("");
		// yField.setText("");
		this.idField.setText("");
		this.scriptArea.setText("");
	}

	public void disableComponent() {
		this.nameField.setEnabled(false);
		this.idField.setEnabled(false);

		this.upButton.setEnabled(false);
		this.downButton.setEnabled(false);
		this.leftButton.setEnabled(false);
		this.rightButton.setEnabled(false);

		this.questionDialogue.setEnabled(false);
		this.affirmativeDialogue.setEnabled(false);
		this.negativeDialogue.setEnabled(false);
		this.speechDialogue.setEnabled(false);

		this.scriptArea.setEnabled(false);
		this.isEnabled = false;
	}

	public void enableComponent() {
		this.nameField.setEnabled(true);
		this.idField.setEnabled(true);

		this.upButton.setEnabled(true);
		this.downButton.setEnabled(true);
		this.leftButton.setEnabled(true);
		this.rightButton.setEnabled(true);

		this.questionDialogue.setEnabled(true);
		this.affirmativeDialogue.setEnabled(true);
		this.negativeDialogue.setEnabled(true);
		this.speechDialogue.setEnabled(true);

		this.scriptArea.setEnabled(true);
		this.isEnabled = true;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	// ActionListener
	@Override
	public void actionPerformed(ActionEvent event) {
		JTextArea area = this.editor.scriptChanger.getScriptArea();
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
		if (lastDirection.equals("U") || lastDirection.equals("D") || lastDirection.equals("L")
			|| lastDirection.equals("R")) {
			switch (event.getActionCommand()) {
				case UP: {
					this.inputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					this.inputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					this.inputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					this.inputChange(doc, area, "R");
					break;
				}
			}
		}
		else if (lastCharacter.equals("^")) {
			switch (event.getActionCommand()) {
				case UP: {
					this.defaultInputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					this.defaultInputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					this.defaultInputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					this.defaultInputChange(doc, area, "R");
					break;
				}
			}
		}
		else if (lastCharacter.equals("\n") || lastCharacter.equals("")) {
			area.append("^");
			switch (event.getActionCommand()) {
				case UP: {
					this.defaultInputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					this.defaultInputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					this.defaultInputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					this.defaultInputChange(doc, area, "R");
					break;
				}
			}
		}
		else {
			area.append("\n^");
			switch (event.getActionCommand()) {
				case UP: {
					this.defaultInputChange(doc, area, "U");
					break;
				}
				case DOWN: {
					this.defaultInputChange(doc, area, "D");
					break;
				}
				case LEFT: {
					this.defaultInputChange(doc, area, "L");
					break;
				}
				case RIGHT: {
					this.defaultInputChange(doc, area, "R");
					break;
				}
			}
		}
	}

	private void inputChange(PlainDocument doc, JTextArea area, String directionToCompare) {
		try {
			String str = doc.getText(doc.getLength() - 2, 1);
			if (str.equals(directionToCompare)) {
				if (this.movementCounter < 9) {
					this.movementCounter++;
					doc.remove(doc.getLength() - 1, 1);
					area.append(Integer.toString(this.movementCounter));
				}
				else {
					this.movementCounter = 0;
					area.append(directionToCompare + Integer.toString(this.movementCounter));
				}
			}
			else {
				this.movementCounter = 0;
				area.append(directionToCompare + Integer.toString(this.movementCounter));
			}
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void defaultInputChange(PlainDocument doc, JTextArea area, String directionToCompare) {
		this.movementCounter = 0;
		area.append(directionToCompare + Integer.toString(this.movementCounter));
	}

	// DocumentListener
	@Override
	public void changedUpdate(DocumentEvent event) {}

	// DocumentListener
	@Override
	public void insertUpdate(DocumentEvent event) {
		if (this.allowUpdate) {
			Trigger selectedTrigger = this.editor.scriptViewer.getSelectedTrigger();
			if (selectedTrigger != null) {
				String test = "";

				try {
					test = this.editor.scriptChanger.getNameField().getText();
					if (!test.isEmpty() || !test.equals(""))
						selectedTrigger.setName(test);
				}
				catch (Exception e) {}

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
				// JOptionPane.showMessageDialog(null, "Please input numbers in range 0 ~
				// 255.");
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
				// JOptionPane.showMessageDialog(null, "Please input numbers in range 0 ~
				// 255.");
				// SwingUtilities.invokeLater(new Runnable() {
				// @Override
				// public void run() {
				// editor.scriptChanger.getYField().setText("");
				// }
				// });
				// }

				try {
					test = this.editor.scriptChanger.getIDField().getText();
					if (!test.isEmpty() || !test.equals("")) {
						int n = Integer.valueOf(test);
						if (n > 0x0FFFF)
							throw new NumberFormatException();
						selectedTrigger.setTriggerID((short) (n & 0xFFFF));
					}
				}
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(
						null,
						"Please input numbers in range 0 ~ 65535.\n\n0 is reserved for \"Eraser\", which is used to erase triggers from the map."
					);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							ScriptChanger.this.editor.scriptChanger.getIDField().setText("");
						}
					});
				}

				try {
					test = this.editor.scriptChanger.getScriptArea().getText();
					if (!test.isEmpty() || !test.equals(""))
						selectedTrigger.setScript(test);
				}
				catch (Exception e) {}

				JList<Trigger> list = this.editor.scriptViewer.getTriggerList();
				DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) list.getModel();
				model.setElementAt(selectedTrigger, list.getSelectedIndex());
			}
			if (!this.editor.isBeingModified()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String str = ScriptChanger.this.editor.getTitle();
						if (!str.endsWith("*"))
							ScriptChanger.this.editor.setTitle(str + "*");
						ScriptChanger.this.editor.setModifiedFlag(true);
					}
				});
			}
		}
	}

	// DocumentListener
	@Override
	public void removeUpdate(DocumentEvent event) {
		if (this.allowUpdate) {
			Trigger selectedTrigger = this.editor.scriptViewer.getSelectedTrigger();
			if (selectedTrigger != null) {
				String test = this.editor.scriptChanger.getNameField().getText();
				if (!test.isEmpty() || !test.equals(""))
					selectedTrigger.setName(test);
				// test = editor.scriptChanger.getXField().getText();
				// if (!test.isEmpty() || !test.equals(""))
				// selectedTrigger.setTriggerPositionX((byte) (Integer.valueOf(test) & 0xFF));
				// test = editor.scriptChanger.getYField().getText();
				// if (!test.isEmpty() || !test.equals(""))
				// selectedTrigger.setTriggerPositionY((byte) (Integer.valueOf(test) & 0xFF));
				test = this.editor.scriptChanger.getIDField().getText();
				if (!test.isEmpty() || !test.equals(""))
					selectedTrigger.setTriggerID((short) (Integer.valueOf(test) & 0xFFFF));
				test = this.editor.scriptChanger.getScriptArea().getText();
				if (!test.isEmpty() || !test.equals(""))
					selectedTrigger.setScript(test);

				JList<Trigger> list = this.editor.scriptViewer.getTriggerList();
				DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) list.getModel();
				model.setElementAt(selectedTrigger, list.getSelectedIndex());
			}
			if (!this.editor.isBeingModified()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String str = ScriptChanger.this.editor.getTitle();
						if (!str.endsWith("*"))
							ScriptChanger.this.editor.setTitle(str + "*");
						ScriptChanger.this.editor.setModifiedFlag(true);
					}
				});
			}
		}
	}
}
