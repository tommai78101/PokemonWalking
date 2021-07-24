/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import common.Debug;
import editor.EditorConstants.Metadata;
import script_editor.ScriptEditor;

public class FileControl extends JPanel implements ActionListener {
	public static File lastSavedDirectory = null;
	public static final String[] TAGS = new String[] {
	    "New", "Save", "Open", "", "Tileset", "Trigger", "", "Script"
	};

	private static final long serialVersionUID = 1L;
	private static final String defaultPath = Paths.get("").toAbsolutePath().toString();

	public HashMap<String, JButton> buttonCache = new HashMap<>();
	private LevelEditor editor;
	private List<String> cacheDirectories;

	public FileControl(LevelEditor editor) {
		super();
		this.editor = editor;
		this.cacheDirectories = new ArrayList<>();
		this.setLayout(new GridLayout(1, FileControl.TAGS.length));

		for (int i = 0; i < FileControl.TAGS.length; i++) {
			if (FileControl.TAGS[i].isEmpty() || FileControl.TAGS[i].equals("")) {
				this.add(new JSeparator(SwingConstants.VERTICAL));
				continue;
			}
			JButton button = new JButton(FileControl.TAGS[i]);
			button.addActionListener(this);
			String actionCommand = Integer.toString(i);
			button.setActionCommand(actionCommand);
			this.buttonCache.put(actionCommand, button);
			this.add(button);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		final EditorFileChooser chooser = new EditorFileChooser();
		final EditorMouseListener mouseListener = new EditorMouseListener(chooser);
		JButton button = (JButton) event.getSource();
		String command = button.getActionCommand();
		try {
			int value = Integer.valueOf(command);
			switch (value) {
				case 0: // New
				{
					this.editor.drawingBoardPanel.newImage();
					this.editor.setMapAreaName("Untitled");
					this.editor.generateChecksum();
					break;
				}
				case 1: { // Save
					if (!this.editor.drawingBoardPanel.hasBitmap()) {
						JOptionPane.showMessageDialog(null, "No created maps to save.");
						break;
					}

					try (RandomAccessFile raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "r")) {
						this.fetchCachedDirectories(raf);
						FileControl.lastSavedDirectory = new File(this.cacheDirectories.get(LevelEditor.FileControlIndex));
					}
					catch (IOException e) {
						FileControl.lastSavedDirectory = new File(FileControl.defaultPath);
					}

					JList<Class<?>> list = this.findFileList(chooser);
					LOOP_TEMP:
					for (MouseListener l : list.getMouseListeners()) {
						// If the class name do not contain "FilePane", we continue iterating.
						if (l.getClass().getName().indexOf("FilePane") < 0)
							continue;

						list.removeMouseListener(l);
						list.addMouseListener(mouseListener);
						break LOOP_TEMP;
					}
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					chooser.setCurrentDirectory(FileControl.lastSavedDirectory);
					chooser.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
					chooser.setVisible(true);
					int result = chooser.showSaveDialog(null);
					if (result == JFileChooser.APPROVE_OPTION) {
						// Set the approved current directory to FileControl.
						FileControl.lastSavedDirectory = chooser.getCurrentDirectory();

						try (RandomAccessFile cacheFile = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw")) {
							BufferedImage img = this.editor.drawingBoardPanel.getMapImage();
							if (img == null)
								break;
							File file = chooser.getSelectedFile();
							String filename = file.getName();
							while (filename.endsWith(".png"))
								filename = filename.substring(0, filename.length() - ".png".length());
							ImageIO.write(
							    img, "png", new File(
							        FileControl.lastSavedDirectory.getAbsolutePath() + "\\" + filename + ".png"
							    )
							);
							this.editor.setMapAreaName(filename);

							// Storing the last approved current directory into the cache file.
							this.cacheDirectories.set(LevelEditor.FileControlIndex, FileControl.lastSavedDirectory.getAbsolutePath());
							this.storeCachedDirectories(cacheFile);
						}
						catch (IOException e) {
							Debug.exception(e);
						}
					}
					break;
				}
				case 2: { // Open
					// String backupPath = LevelEditor.SAVED_PATH_DATA;
					try (RandomAccessFile raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "r")) {
						this.fetchCachedDirectories(raf);
						FileControl.lastSavedDirectory = new File(this.cacheDirectories.get(LevelEditor.FileControlIndex));
					}
					catch (IOException e) {
						FileControl.lastSavedDirectory = new File(FileControl.defaultPath);
					}

					JList<Class<?>> list = this.findFileList(chooser);
					LOOP_TEMP1:
					for (MouseListener l : list.getMouseListeners()) {
						// If "FilePane" cannot be found in the name of the class object, we continue iterating.
						if (l.getClass().getName().indexOf("FilePane") < 0)
							continue;

						list.removeMouseListener(l);
						list.addMouseListener(mouseListener);
						break LOOP_TEMP1;
					}
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					chooser.setCurrentDirectory(FileControl.lastSavedDirectory);
					chooser.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
					chooser.setVisible(true);
					int answer = chooser.showOpenDialog(null);
					if (answer == JFileChooser.APPROVE_OPTION) {
						try (RandomAccessFile cacheFile = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw")) {
							File f = chooser.getSelectedFile();
							FileControl.lastSavedDirectory = f.getParentFile();
							this.editor.setTitle(LevelEditor.NAME_TITLE + " - " + f);
							BufferedImage image = ImageIO.read(f);
							this.editor.drawingBoardPanel.openMapImage(image);
							this.editor.setMapAreaName(f.getName().substring(0, f.getName().length() - ".png".length()));

							// Setting Area ID
							TilePropertiesPanel panel = this.editor.controlPanel.getPropertiesPanel();
							panel.areaIDInputField.setText(Integer.toString(this.editor.getUniqueAreaID()));

							// Storing the last approved current directory into the cache file.
							this.cacheDirectories.set(LevelEditor.FileControlIndex, FileControl.lastSavedDirectory.getAbsolutePath());
							this.storeCachedDirectories(cacheFile);
						}
						catch (IOException e) {
							Debug.exception(e);
						}
					}
					break;
				}
				case 4: { // Tileset
					EditorConstants.metadata = Metadata.Pixel_Data;
					break;
				}
				case 5: { // Trigger
					EditorConstants.metadata = Metadata.Triggers;
					break;
				}
				case 7: {// Script editor
					if (this.editor.scriptEditor == null) {
						this.editor.scriptEditor = new ScriptEditor(ScriptEditor.TITLE, this.editor);
						button.setEnabled(false);
					}
					break;
				}
			}
		}
		catch (NumberFormatException e) {
			return;
		}
	}

	// --------------------------------------------------------------------------------
	// Private methods

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

	private void fetchCachedDirectories(RandomAccessFile file) throws IOException {
		String buffer = null;
		file.seek(0);
		this.cacheDirectories.clear();
		while ((buffer = file.readLine()) != null) {
			this.cacheDirectories.add(buffer);
		}
	}

	private void storeCachedDirectories(RandomAccessFile file) throws IOException {
		file.setLength(0);
		file.seek(0);
		for (String buffer : this.cacheDirectories) {
			file.writeBytes(buffer);
			file.write(System.lineSeparator().getBytes());
		}
	}
}
