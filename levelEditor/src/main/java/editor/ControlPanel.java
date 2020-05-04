/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import editor.EditorConstants.Tools;
import interfaces.Tileable;

public class ControlPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7481148146432931992L;

	private TilePropertiesPanel propertiesPanel; // Part of Control Panel
	@SuppressWarnings("unused")
	private String iconName; // Used to display name in the status panel of Level Editor
	private LevelEditor editor; // reference to parent
	private Data selectedData; // reference to selected data.
	private Trigger selectedTrigger; // reference to selected trigger.
	private JPanel iconsPanel;
	private JScrollPane scrollPanel;

	public ControlPanel(LevelEditor editor) {
		this.editor = editor;
		this.selectedData = new Data();
		this.selectedData.editorID = 0;
		this.selectedData.filepath = "no_png.png";

		this.selectedTrigger = null;

		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.iconsPanel = new JPanel();
		this.iconsPanel.setLayout(new BoxLayout(this.iconsPanel, BoxLayout.Y_AXIS));
		EditorConstants constants = EditorConstants.getInstance();
		for (Map.Entry<Integer, Data> entry : constants.getDatas()) {
			Data d = entry.getValue();
			d.button.setActionCommand(Integer.toString(d.editorID));
			d.button.addActionListener(this);
			this.iconsPanel.add(d.button);
		}

		this.scrollPanel = new JScrollPane(this.iconsPanel) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				int maxWidth = 50;
				int maxHeight = Tileable.HEIGHT * 20;
				Dimension dim = super.getPreferredSize();
				if (dim.width > maxWidth)
					dim.width = maxWidth;
				if (dim.height > maxHeight)
					dim.height = maxHeight;
				return dim;
			}

			@Override
			public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}
		};
		this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPanel.createVerticalScrollBar();
		this.scrollPanel.setVisible(true);
		this.add(this.scrollPanel);

		this.propertiesPanel = new TilePropertiesPanel();
		this.add(this.propertiesPanel);

		this.propertiesPanel.setVisible(true);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (EditorConstants.metadata) {
			case Pixel_Data: {
				JButton button = (JButton) event.getSource();
				int id = Integer.parseInt(button.getActionCommand());
				Map.Entry<Integer, Data> entry = EditorConstants.getInstance().getDatas().get(id);
				Data d = entry.getValue();
				if (d != null) {
					button.setToolTipText(d.name);
					this.selectedData = d;
					this.iconName = d.name;
					this.propertiesPanel.alphaInputField.setText(Integer.toString(d.alpha));
					this.propertiesPanel.redInputField.setText(Integer.toString(d.red));
					this.propertiesPanel.greenInputField.setText(Integer.toString(d.green));
					this.propertiesPanel.blueInputField.setText(Integer.toString(d.blue));
					EditorConstants.chooser = Tools.ControlPanel;
				}
				this.editor.validate();
				break;
			}
			case Triggers: {
				this.selectedTrigger = new Trigger();
				this.selectedTrigger.setTriggerID((short) 1);
				this.selectedTrigger.setTriggerPositionX((byte) 0x1);
				this.selectedTrigger.setTriggerPositionY((byte) 0x1);
				break;
			}
		}
	}

	public String getPickedEntityName() {
		switch (EditorConstants.chooser) {
			case ControlPanel:
				return this.selectedData.name;
			case Properties:
				return this.selectedData.name;
		}
		return "";
	}

	public Data getSelectedData() {
		switch (EditorConstants.chooser) {
			case ControlPanel:
				return this.selectedData;
			case Properties:
				return this.editor.properties.getSelectedData();
		}
		return null;
	}

	public void setSelectedData(Data data) {
		this.selectedData = data;
	}

	public Trigger getSelectedTrigger() {
		return this.selectedTrigger;
	}

	public void setSelectedTrigger(Trigger t) {
		this.selectedTrigger = t;
	}

	public TilePropertiesPanel getPropertiesPanel() {
		return this.propertiesPanel;
	}

	@Override
	public void validate() {
		switch (EditorConstants.metadata) {
			case Pixel_Data:
				this.iconsPanel.setVisible(true);
				this.iconsPanel.setEnabled(true);
				this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				break;
			case Triggers:
				this.iconsPanel.setVisible(false);
				this.iconsPanel.setEnabled(false);
				this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
				break;
		}
		super.validate();
		if (this.propertiesPanel != null)
			this.propertiesPanel.validate();
	}
}
