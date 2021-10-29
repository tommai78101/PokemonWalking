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
//import javax.swing.ListModel;
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
		this.editor = editor;
		this.selectedTrigger = null;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Trigger List:"));
		this.constructingList();
		this.constructingButtons();
		this.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (Integer.parseInt(event.getActionCommand())) {
			default:
				break;
			case 0: {// Create button
				Trigger t = new Trigger();
				t.setTriggerID((short) 0);
				t.setName("<Untitled>");
				this.model.addElement(t);
				this.validate();
				JScrollBar vertical = this.scrollPane.getVerticalScrollBar();
				vertical.setValue(vertical.getMaximum() + 1);
				this.triggerList.setSelectedIndex(this.model.getSize() - 1);
				this.editor.scriptChanger.enableComponent();
				break;
			}
			case 1: {// Remove button
				int index = this.triggerList.getSelectedIndex();
				if (index != -1 && !this.model.isEmpty())
					this.model.remove(index);
				else if (!this.model.isEmpty()) {
					this.model.remove(this.model.getSize() - 1);
				}
				if (this.model.isEmpty()) {
					ScriptViewer.this.editor.scriptChanger.clearTextFields();
					ScriptViewer.this.editor.scriptChanger.disableComponent();
				}
				break;
			}
			case 2: {// Clear button
				this.clearTriggerModel();
				ScriptViewer.this.editor.scriptChanger.clearTextFields();
				ScriptViewer.this.editor.scriptChanger.disableComponent();
				break;
			}
		}
		this.editor.getLevelEditorParent().properties.reloadTriggers();
		this.triggerList.validate();
		this.editor.refresh();
	}

	public void addTrigger(Trigger t) {
		this.model.add(0, t);
		this.validate();
	}

	public void clearTriggerModel() {
		this.model.removeAllElements();
	}

	public void clearTriggers() {
		this.triggerList.removeAll();
	}

	public Trigger getSelectedTrigger() {
		return this.selectedTrigger;
	}

	public JList<Trigger> getTriggerList() {
		return this.triggerList;
	}

	public void removeTrigger() {
		this.model.remove(this.triggerList.getSelectedIndex());
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		if (event.getValueIsAdjusting())
			return;
		Object source = event.getSource();
		if (source instanceof JList<?> list) {
			this.selectedTrigger = (Trigger) list.getSelectedValue();
			if (this.selectedTrigger != null) {
				boolean isNpcTrigger = this.selectedTrigger.isNpcTrigger();
				short idValue = isNpcTrigger ? this.selectedTrigger.getNpcTriggerID() : this.selectedTrigger.getTriggerID();
				this.editor.scriptChanger.disallowFieldsToUpdate();
				this.editor.scriptChanger.getNameField().setText(this.selectedTrigger.getName());
				this.editor.scriptChanger.getIDField().setText(Short.toString(idValue));
				this.editor.scriptChanger.getNpcTriggerFlag().setSelected(isNpcTrigger);
				this.editor.scriptChanger.getScriptArea().setText(this.selectedTrigger.getScript());
				this.editor.scriptChanger.allowFieldsToUpdate();
			}
		}
		super.revalidate();
		super.repaint();
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
		panel.add(this.createButton);

		panel.add(new JSeparator(SwingConstants.HORIZONTAL));

		this.removeButton = new JButton("Remove");
		this.removeButton.setActionCommand(Integer.toString(1));
		this.removeButton.addActionListener(this);
		this.removeButton.setSize(size);
		this.removeButton.setMaximumSize(size);
		this.removeButton.setMinimumSize(size);
		this.removeButton.setPreferredSize(size);
		panel.add(this.removeButton);

		panel.add(new JSeparator(SwingConstants.HORIZONTAL));

		this.clearButton = new JButton("Clear");
		this.clearButton.setActionCommand(Integer.toString(2));
		this.clearButton.addActionListener(this);
		this.clearButton.setSize(size);
		this.clearButton.setMaximumSize(size);
		this.clearButton.setMinimumSize(size);
		this.clearButton.setPreferredSize(size);
		panel.add(this.clearButton);

		this.add(panel);
	}

	private void constructingList() {
		this.model = new DefaultListModel<>();
		final Dimension size = new Dimension(100, 300);

		this.triggerList = new JList<>();
		this.triggerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.triggerList.setLayoutOrientation(JList.VERTICAL);
		this.triggerList.setVisibleRowCount(0);
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

		this.scrollPane = new JScrollPane(this.triggerList);
		this.scrollPane.setSize(size);
		this.scrollPane.setMinimumSize(size);

		this.scrollPane.setVisible(true);
		this.add(this.scrollPane);
	}
}
