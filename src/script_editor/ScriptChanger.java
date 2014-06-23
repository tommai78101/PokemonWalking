package script_editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ScriptChanger extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private final ScriptEditor editor;
	
	private JTextField nameField, xField, yField, idField;
	private JButton upButton, downButton, leftButton, rightButton;
	private JTextArea scriptArea;
	
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
		
		// Second row
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.NONE;
		this.add(new JLabel("X Position: "), c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 3.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add((xField = new JTextField()), c);
		
		// Third row
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.NONE;
		this.add(new JLabel("Y Position: "), c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 3.5;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add((yField = new JTextField()), c);
		
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
		
		this.setBorder(BorderFactory.createTitledBorder("Trigger:"));
		this.validate();
	}
	
	private JPanel constructDirections() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		final Dimension size = new Dimension(30, 10);
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		upButton = new JButton("Up");
		upButton.setSize(size);
		top.add(upButton);
		
		JPanel across = new JPanel();
		across.setLayout(new BoxLayout(across, BoxLayout.X_AXIS));
		leftButton = new JButton("Left");
		leftButton.setSize(size);
		downButton = new JButton("Down");
		downButton.setSize(size);
		rightButton = new JButton("Right");
		rightButton.setSize(size);
		across.add(leftButton);
		across.add(downButton);
		across.add(rightButton);
		
		panel.add(top);
		panel.add(across);
		panel.setBorder(BorderFactory.createTitledBorder("Movements:"));
		return panel;
	}
	
	private JPanel constructScripts() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		this.scriptArea = new JTextArea();
		this.scriptArea.setLineWrap(true);
		panel.add(new JScrollPane(this.scriptArea), BorderLayout.CENTER);
		panel.validate();
		panel.setBorder(BorderFactory.createTitledBorder("Script:"));
		return panel;
	}
	
	public JTextField getNameField() {
		return this.nameField;
	}
	
	public JTextField getXField() {
		return this.xField;
	}
	
	public JTextField getYField() {
		return this.yField;
	}
	
	public JTextField getIDField() {
		return this.idField;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
	}
	
}
