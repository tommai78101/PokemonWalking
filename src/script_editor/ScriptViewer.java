package script_editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import editor.KeySelectionRenderer;
import editor.Trigger;

public class ScriptViewer extends JPanel implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private final ScriptEditor editor;
	
	private DefaultListModel<Trigger> model;
	private JList<Trigger> triggerList;
	private JScrollPane scrollPane;
	private JButton createButton;
	private JButton removeButton;
	private JButton clearButton;
	private Trigger selectedTrigger;
	@SuppressWarnings("unused")
	private int iterator;
	
	public ScriptViewer(ScriptEditor editor) {
		super();
		this.editor = editor;
		this.selectedTrigger = null;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Trigger List:"));
		constructingList();
		constructingButtons();
		this.revalidate();
	}
	
	private void constructingList() {
		this.model = new DefaultListModel<Trigger>();
		
		this.triggerList = new JList<Trigger>();
		this.triggerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.triggerList.setLayoutOrientation(JList.VERTICAL);
		this.triggerList.setVisibleRowCount(0);
		Dimension size = new Dimension(100, 300);
		this.triggerList.setSize(size);
		this.triggerList.setMinimumSize(size);
		
		this.triggerList.setModel(this.model);
		new KeySelectionRenderer(this.triggerList) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public String getDisplayValue(Object item) {
				Trigger t = (Trigger) item;
				return t.getName();
			}
		};
		this.triggerList.addListSelectionListener(this);
		
		scrollPane = new JScrollPane(this.triggerList);
		scrollPane.setSize(size);
		scrollPane.setMinimumSize(size);
		
		scrollPane.setVisible(true);
		this.add(scrollPane);
	}
	
	private void constructingButtons() {
		JPanel panel = new JPanel();
		Dimension size = new Dimension(90, 20);
		
		this.createButton = new JButton("Create");
		this.createButton.setActionCommand(Integer.toString(0));
		this.createButton.addActionListener(this);
		this.createButton.setSize(size);
		this.createButton.setMaximumSize(size);
		this.createButton.setMinimumSize(size);
		this.createButton.setPreferredSize(size);
		panel.add(createButton);
		
		panel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		this.removeButton = new JButton("Remove");
		this.removeButton.setActionCommand(Integer.toString(1));
		this.removeButton.addActionListener(this);
		this.removeButton.setSize(size);
		this.removeButton.setMaximumSize(size);
		this.removeButton.setMinimumSize(size);
		this.removeButton.setPreferredSize(size);
		panel.add(removeButton);
		
		panel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		this.clearButton = new JButton("Clear");
		this.clearButton.setActionCommand(Integer.toString(2));
		this.clearButton.addActionListener(this);
		this.clearButton.setSize(size);
		this.clearButton.setMaximumSize(size);
		this.clearButton.setMinimumSize(size);
		this.clearButton.setPreferredSize(size);
		panel.add(clearButton);
		
		this.add(panel);
	}
	
	public void addTrigger(Trigger t) {
		this.model.add(0, t);
		this.validate();
	}
	
	public void removeTrigger() {
		this.model.remove(this.triggerList.getSelectedIndex());
	}
	
	public Trigger getSelectedTrigger() {
		return this.selectedTrigger;
	}
	
	public JList<Trigger> getTriggerList() {
		return this.triggerList;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		switch (Integer.valueOf(event.getActionCommand())) {
			default:
				break;
			case 0: {// Create Button
				Trigger t = new Trigger();
				t.setTriggerID((short) 0);
				t.setName("<Untitled>");
				this.model.addElement(t);
				this.validate();
				JScrollBar vertical = scrollPane.getVerticalScrollBar();
				vertical.setValue(vertical.getMaximum() + 1);
				this.triggerList.setSelectedIndex(this.model.getSize() - 1);
				break;
			}
			case 1: {// Remove button
				int index = this.triggerList.getSelectedIndex();
				if (index != -1 && !this.model.isEmpty())
					this.model.remove(index);
				else if (!this.model.isEmpty())
					this.model.remove(this.model.getSize() - 1);
				break;
			}
			case 2: {
				while (!this.model.isEmpty()){
					this.model.remove(0);
				}
				break;
			}
		}
		this.editor.getLevelEditorParent().properties.reloadTriggers();
		this.triggerList.validate();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			if (event.getSource() instanceof JList) {
				@SuppressWarnings("unchecked")
				JList<Trigger> list = (JList<Trigger>) event.getSource();
				this.selectedTrigger = list.getSelectedValue();
				if (this.selectedTrigger != null) {
					this.editor.scriptChanger.disallowFieldsToUpdate();
					this.editor.scriptChanger.getNameField().setText("");
					// this.editor.scriptChanger.getXField().setText("");
					// this.editor.scriptChanger.getYField().setText("");
					this.editor.scriptChanger.getIDField().setText("");
					this.editor.scriptChanger.getScriptArea().setText("");
					
					this.editor.scriptChanger.getNameField().setText(this.selectedTrigger.getName());
					// this.editor.scriptChanger.getXField().setText(Integer.toString(this.selectedTrigger.getPositionX()));
					// this.editor.scriptChanger.getYField().setText(Integer.toString(this.selectedTrigger.getPositionY()));
					this.editor.scriptChanger.getIDField().setText(Integer.toString(this.selectedTrigger.getTriggerID()));
					this.editor.scriptChanger.getScriptArea().setText(this.selectedTrigger.getScript());
					this.editor.scriptChanger.allowFieldsToUpdate();
				}
			}
		}
	}
}
