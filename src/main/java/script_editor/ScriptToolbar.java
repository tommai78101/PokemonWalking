package script_editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
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
import javax.swing.plaf.metal.MetalFileChooserUI;

import editor.FileControl;
import editor.LevelEditor;
import editor.Trigger;

//TODO (6/25/2015): Save the already-opened file without needing to open up a JFileChooser.

public class ScriptToolbar extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final ScriptEditor editor;
	private final String[] tags = { "New", "Save", "Open", "" };
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
			case 0: {// New
				editor.scriptChanger.clear();
				JList<Trigger> triggerList = editor.scriptViewer.getTriggerList();
				DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) triggerList.getModel();
				model.clear();

				JComboBox<Trigger> triggerComboBox = editor.parent.properties.getTriggerList();
				DefaultComboBoxModel<Trigger> triggerComboModel = (DefaultComboBoxModel<Trigger>) triggerComboBox.getModel();
				triggerComboModel.removeAllElements();

				Trigger trigger = new Trigger();
				trigger.setTriggerID((short) 0);
				trigger.setName("Eraser");
				triggerComboModel.addElement(trigger);
				triggerComboBox.setSelectedIndex(0);

				triggerList.clearSelection();
				this.editor.setModifiedFlag(false);
				this.editor.setTitle("Script Editor (Hobby) - Untitled.script");
				this.editor.setScriptName("Untitled");
				this.editor.scriptChanger.disable();

				editor.parent.revalidate();
				break;
			}
			case 1: { // Save
				RandomAccessFile raf = null;
				try {
					raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
					raf.readLine(); // The second line in the cache is for the Script Editor.
					ScriptEditor.LAST_SAVED_DIRECTORY = new File(raf.readLine());
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					ScriptEditor.LAST_SAVED_DIRECTORY = FileControl.lastSavedDirectory;
				}
				finally {
					try {
						raf.close();
					}
					catch (IOException e) {
					}
				}

				final JFileChooser saver = new JFileChooser();
				JList<Class<?>> list = findFileList(saver);
				LOOP_TEMP: for (MouseListener l : list.getMouseListeners()){
					if (l.getClass().getName().indexOf("FilePane") >= 0){
						list.removeMouseListener(l);
						list.addMouseListener(new MouseListener(){
							@Override
							public void mouseClicked(MouseEvent e) {
								if (e.getClickCount() == 1){
									File file = saver.getSelectedFile();
									if (file != null){
										MetalFileChooserUI ui = (MetalFileChooserUI) saver.getUI();
										ui.setFileName(file.getName());											
									}
								}
								else if (e.getClickCount() == 2){
									File file = saver.getSelectedFile();
									if (file != null){
										if (file.isDirectory()){
											saver.setCurrentDirectory(file);
										}
										else if (file.isFile()){
											saver.setSelectedFile(file);
										}
										MetalFileChooserUI ui = (MetalFileChooserUI) saver.getUI();
										ui.setFileName(file.getName());	
									}
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
							}
							@Override
							public void mouseExited(MouseEvent e) {
							}
							@Override
							public void mousePressed(MouseEvent e) {
							}
							@Override
							public void mouseReleased(MouseEvent e) {
							}
						});
						break LOOP_TEMP;
					}
				}
				saver.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				saver.setCurrentDirectory(ScriptEditor.LAST_SAVED_DIRECTORY);
				saver.setFileFilter(new FileNameExtensionFilter("SCRIPT files", "script"));
				saver.setVisible(true);
				int answer = saver.showSaveDialog(null);
				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = saver.getSelectedFile();
					ScriptEditor.LAST_SAVED_DIRECTORY = f.getParentFile();

					if (f.getName().endsWith(".script")) {
						this.editor.setTitle("Script Editor (Hobby) - " + f.getName());
						this.editor.save(new File(f.getParentFile(), f.getName()));
						this.editor.setScriptName(f.getName().substring(0, (f.getName().length() - ".script".length())));
					}
					else {
						this.editor.setTitle("Script Editor (Hobby) - " + f.getName() + ".script");
						this.editor.save(new File(f.getParentFile(), f.getName() + ".script"));
						this.editor.setScriptName(f.getName());
					}
					this.editor.setModifiedFlag(false);

					RandomAccessFile rf = null;
					try {
						rf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
						rf.readLine();
						rf.writeBytes(ScriptEditor.LAST_SAVED_DIRECTORY.getAbsolutePath());
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					finally {
						try {
							rf.close();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			}
			case 2: { // Open

				RandomAccessFile raf = null;
				try {
					raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
					raf.readLine(); // The second line in the cache is for the Script Editor.
					ScriptEditor.LAST_SAVED_DIRECTORY = new File(raf.readLine());
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					ScriptEditor.LAST_SAVED_DIRECTORY = FileControl.lastSavedDirectory;
				}
				finally {
					try {
						raf.close();
					}
					catch (IOException e) {
					}
				}

				final JFileChooser opener = new JFileChooser();
				JList<Class<?>> list = findFileList(opener);
				LOOP_TEMP: for (MouseListener l : list.getMouseListeners()){
					if (l.getClass().getName().indexOf("FilePane") >= 0){
						list.removeMouseListener(l);
						list.addMouseListener(new MouseListener(){
							@Override
							public void mouseClicked(MouseEvent e) {
								if (e.getClickCount() == 1){
									File file = opener.getSelectedFile();
									if (file != null){
										MetalFileChooserUI ui = (MetalFileChooserUI) opener.getUI();
										ui.setFileName(file.getName());											
									}
								}
								else if (e.getClickCount() == 2){
									File file = opener.getSelectedFile();
									if (file != null){
										if (file.isDirectory()){
											opener.setCurrentDirectory(file);
										}
										else if (file.isFile()){
											opener.setSelectedFile(file);
										}
										MetalFileChooserUI ui = (MetalFileChooserUI) opener.getUI();
										ui.setFileName(file.getName());	
									}
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
							}
							@Override
							public void mouseExited(MouseEvent e) {
							}
							@Override
							public void mousePressed(MouseEvent e) {
							}
							@Override
							public void mouseReleased(MouseEvent e) {
							}
						});
						break LOOP_TEMP;
					}
				}
				opener.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				opener.setCurrentDirectory(ScriptEditor.LAST_SAVED_DIRECTORY);
				opener.setFileFilter(new FileNameExtensionFilter("SCRIPT files", "script"));
				opener.setVisible(true);
				int answer = opener.showOpenDialog(null);
				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = opener.getSelectedFile();
					ScriptEditor.LAST_SAVED_DIRECTORY = f.getParentFile();
					this.editor.setTitle("Script Editor (Hobby) - " + f.getName());
					this.editor.load(f);
					this.editor.setModifiedFlag(false);
					this.editor.setScriptName(f.getName().substring(0, (f.getName().length() - ".script".length())));
					this.editor.scriptChanger.enable();

					RandomAccessFile rf = null;
					try {
						rf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
						rf.readLine();
						rf.writeBytes(ScriptEditor.LAST_SAVED_DIRECTORY.getAbsolutePath());
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					finally {
						try {
							rf.close();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private JList<Class<?>> findFileList(Component comp){
		if (comp instanceof JList){
			return (JList<Class<?>>) comp;
		}
		if (comp instanceof Container){
			for (Component c : ((Container) comp).getComponents()){
				JList<Class<?>> list = findFileList(c);
				if (list != null){
					return list;
				}
			}
		}
		return null;
	}
}
