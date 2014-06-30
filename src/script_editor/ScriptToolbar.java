package script_editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.FileControl;
import editor.LevelEditor;

public class ScriptToolbar extends JPanel implements ActionListener {
	private final ScriptEditor editor;
	private final String[] tags = {
			"Open", ""
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
			case 0: { // Open
				
				RandomAccessFile raf = null;
				try {
					raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
					raf.readLine(); //The second line in the cache is for the Script Editor.
					ScriptEditor.LAST_SAVED_DIRECTORY = new File(raf.readLine());
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e){
					ScriptEditor.LAST_SAVED_DIRECTORY = FileControl.lastSavedDirectory;
				}
				finally {
					try {
						raf.close();
					}
					catch (IOException e) {
					}
				}
				
				JFileChooser opener = new JFileChooser();
				opener.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				opener.setCurrentDirectory(ScriptEditor.LAST_SAVED_DIRECTORY);
				opener.setFileFilter(new FileNameExtensionFilter("SCRIPT files", "script"));
				opener.setVisible(true);
				int answer = opener.showOpenDialog(null);
				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = opener.getSelectedFile();
					ScriptEditor.LAST_SAVED_DIRECTORY = f.getParentFile();
					this.editor.setTitle(f.getName());
					this.editor.load(f);
					
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
}
