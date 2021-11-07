package script_editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONException;

import editor.EditorFileChooser;
import editor.EditorMouseListener;
import editor.FileControl;
import editor.LevelEditor;
import editor.Trigger;

public class ScriptToolbar extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final ScriptEditor editor;
	private final String[] menuOptionsTags = {
		"New Script", "Save Script", "Open Script", ""
	};
	private final HashMap<String, JButton> buttonCache = new HashMap<>();

	public ScriptToolbar(ScriptEditor editor) {
		this.editor = editor;
		this.setLayout(new GridLayout(1, this.menuOptionsTags.length));

		this.createButtons();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		final EditorFileChooser chooser = new EditorFileChooser();
		final EditorMouseListener mouseListener = new EditorMouseListener(chooser);

		switch (Integer.parseInt(event.getActionCommand())) {
			case 0: { // New script
				this.makeNewScript();
				break;
			}
			case 1: { // Save script
				try (RandomAccessFile raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw")) {
					raf.readLine(); // The second line in the cache is for the Script Editor.
					ScriptEditor.lastSavedDirectory = new File(raf.readLine());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					ScriptEditor.lastSavedDirectory = FileControl.lastSavedDirectory;
				}

				JList<Class<?>> list = this.findFileList(chooser);
				LOOP_TEMP:
				for (MouseListener l : list.getMouseListeners()) {
					if (l.getClass().getName().indexOf("FilePane") >= 0) {
						list.removeMouseListener(l);
						list.addMouseListener(mouseListener);
						break LOOP_TEMP;
					}
				}
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setCurrentDirectory(ScriptEditor.lastSavedDirectory);
				chooser.setFileFilter(new FileNameExtensionFilter("SCRIPT files", "script"));
				chooser.setVisible(true);
				int answer = chooser.showSaveDialog(this.editor);
				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					ScriptEditor.lastSavedDirectory = f.getParentFile();

					if (f.getName().endsWith(".script")) {
						this.editor.setTitle(ScriptEditor.TITLE + " - " + f.getName());
						this.editor.save(new File(f.getParentFile(), f.getName()));
						this.editor.setScriptName(f.getName().substring(0, (f.getName().length() - ".script".length())));
					}
					else {
						this.editor.setTitle(ScriptEditor.TITLE + " - " + f.getName() + ".script");
						this.editor.save(new File(f.getParentFile(), f.getName() + ".script"));
						this.editor.setScriptName(f.getName());
					}
					this.editor.setModifiedFlag(false);

					try (RandomAccessFile rf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw")) {
						rf.readLine(); // The second line in the cache is for the Script Editor.
						rf.writeBytes(ScriptEditor.lastSavedDirectory.getAbsolutePath());
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case 2: { // Open script.
				try (RandomAccessFile raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw")) {
					raf.readLine(); // The second line in the cache is for the Script Editor.
					ScriptEditor.lastSavedDirectory = new File(raf.readLine());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					ScriptEditor.lastSavedDirectory = FileControl.lastSavedDirectory;
				}

				JList<Class<?>> list = this.findFileList(chooser);
				LOOP_TEMP:
				for (MouseListener l : list.getMouseListeners()) {
					if (l.getClass().getName().indexOf("FilePane") >= 0) {
						list.removeMouseListener(l);
						list.addMouseListener(mouseListener);
						break LOOP_TEMP;
					}
				}
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setCurrentDirectory(ScriptEditor.lastSavedDirectory);
				chooser.setFileFilter(new FileNameExtensionFilter("SCRIPT files", "script"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON files", "json"));
				chooser.setVisible(true);
				int answer = chooser.showOpenDialog(this.editor);
				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					ScriptEditor.lastSavedDirectory = f.getParentFile();
					this.editor.setTitle(ScriptEditor.TITLE + " - " + f.getName());
					try {
						this.editor.load(f);
					}
					catch (JSONException e) {
						// Try out the legacy loader
						this.editor.load_legacy(f);
					}
					this.editor.setModifiedFlag(false);
					this.editor.setScriptName(f.getName().substring(0, (f.getName().length() - ".script".length())));
					this.editor.scriptChanger.updateComponent();

					try (RandomAccessFile rf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw")) {
						rf.readLine(); // The second line in the cache is for the Script Editor.
						rf.writeBytes(ScriptEditor.lastSavedDirectory.getAbsolutePath());
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
		super.revalidate();
		super.repaint();
	}

	public void makeNewScript() {
		this.editor.scriptChanger.clearTextFields();
		JList<Trigger> triggerList = this.editor.scriptViewer.getTriggerList();
		DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) triggerList.getModel();
		model.clear();

		JComboBox<Trigger> triggerComboBox = this.editor.parent.properties.getTriggerList();
		DefaultComboBoxModel<Trigger> triggerComboModel = (DefaultComboBoxModel<Trigger>) triggerComboBox
			.getModel();
		triggerComboModel.removeAllElements();

		Trigger trigger = new Trigger();
		trigger.setGameTriggerID((short) 0);
		trigger.setName("Eraser");
		triggerComboModel.addElement(trigger);
		triggerComboBox.setSelectedIndex(0);

		triggerList.clearSelection();
		this.editor.setModifiedFlag(false);
		this.editor.setScriptName("Untitled");
		this.editor.setTitle(ScriptEditor.TITLE + " - " + this.editor.getScriptName() + ".script");
		this.editor.scriptChanger.updateComponent();

		this.editor.parent.revalidate();
	}

	private void createButtons() {
		for (int i = 0; i < this.menuOptionsTags.length; i++) {
			if (this.menuOptionsTags[i].isEmpty() || this.menuOptionsTags[i].equals("")) {
				this.add(new JSeparator(SwingConstants.VERTICAL));
				continue;
			}
			JButton button = new JButton(this.menuOptionsTags[i]);
			button.addActionListener(this);
			String actionCommand = Integer.toString(i);
			button.setActionCommand(actionCommand);
			this.buttonCache.put(actionCommand, button);
			this.add(button);
		}
	}

	@SuppressWarnings("unchecked")
	private JList<Class<?>> findFileList(Component comp) {
		if (comp instanceof JList) {
			return (JList<Class<?>>) comp;
		}
		if (comp instanceof Container) {
			for (Component c : ((Container) comp).getComponents()) {
				JList<Class<?>> list = this.findFileList(c);
				if (list != null) {
					return list;
				}
			}
		}
		return null;
	}
}
