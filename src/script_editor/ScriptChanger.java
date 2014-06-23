package script_editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScriptChanger extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private final ScriptEditor editor;
	
	private JTextField nameField, xField, yField, idField;
	
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
		c.anchor = GridBagConstraints.WEST;
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
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		this.add(new JPanel(), c);
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		this.add(new JPanel(), c);
		
		this.setBorder(BorderFactory.createTitledBorder("DEBUG"));
		this.validate();
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
