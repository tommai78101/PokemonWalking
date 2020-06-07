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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalFileChooserUI;

import editor.EditorConstants.Metadata;
import script_editor.ScriptEditor;

public class FileControl extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	public static final String[] TAGS = new String[] {
		"New", "Save", "Open", "", "Tileset", "Trigger", "", "Script"
	};
	public static File lastSavedDirectory = new File(Paths.get("").toAbsolutePath().toString());
	public HashMap<String, JButton> buttonCache = new HashMap<>();
	private LevelEditor editor;

	public FileControl(LevelEditor editor) {
		super();
		this.editor = editor;
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
		JButton button = (JButton) event.getSource();
		String command = button.getActionCommand();
		try {
			int value = Integer.valueOf(command);
			switch (value) {
				case 0: // New
				{
					this.editor.drawingBoardPanel.newImage();
					this.editor.setMapAreaName("Untitled");
					break;
				}
				case 1: {// Save
					if (!this.editor.drawingBoardPanel.hasBitmap()) {
						JOptionPane.showMessageDialog(null, "No created maps to save.");
						break;
					}

					RandomAccessFile raf = null;
					try {
						raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
						FileControl.lastSavedDirectory = new File(raf.readLine());
					}
					catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					finally {
						try {
							raf.close();
						}
						catch (IOException e) {}
					}

					final JFileChooser chooser = new JFileChooser();

					JList<Class<?>> list = this.findFileList(chooser);
					LOOP_TEMP:
					for (MouseListener l : list.getMouseListeners()) {
						if (l.getClass().getName().indexOf("FilePane") >= 0) {
							list.removeMouseListener(l);
							list.addMouseListener(new MouseListener() {
								@Override
								public void mouseClicked(MouseEvent e) {
									if (e.getClickCount() == 1) {
										File file = chooser.getSelectedFile();
										if (file != null) {
											MetalFileChooserUI ui = (MetalFileChooserUI) chooser.getUI();
											ui.setFileName(file.getName());
										}
									}
									else if (e.getClickCount() == 2) {
										File file = chooser.getSelectedFile();
										if (file != null) {
											if (file.isDirectory()) {
												chooser.setCurrentDirectory(file);
											}
											else if (file.isFile()) {
												chooser.setSelectedFile(file);
											}
											MetalFileChooserUI ui = (MetalFileChooserUI) chooser.getUI();
											ui.setFileName(file.getName());
										}
									}
								}

								@Override
								public void mouseEntered(MouseEvent e) {}

								@Override
								public void mouseExited(MouseEvent e) {}

								@Override
								public void mousePressed(MouseEvent e) {}

								@Override
								public void mouseReleased(MouseEvent e) {}
							});
							break LOOP_TEMP;
						}
					}

					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					chooser.setCurrentDirectory(FileControl.lastSavedDirectory);
					chooser.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
					chooser.setVisible(true);
					int result = chooser.showSaveDialog(null);
					if (result == JFileChooser.APPROVE_OPTION) {
						try {
							BufferedImage img = this.editor.drawingBoardPanel.getMapImage();
							if (img != null) {
								File file = chooser.getSelectedFile();
								String filename = file.getName();
								while (filename.endsWith(".png"))
									filename = filename.substring(0, filename.length() - ".png".length());
								FileControl.lastSavedDirectory = chooser.getCurrentDirectory();
								ImageIO.write(
									img, "png", new File(
										FileControl.lastSavedDirectory.getAbsolutePath() + "\\" + filename + ".png"
									)
								);
								this.editor.setMapAreaName(filename);

								RandomAccessFile f = null;
								try {
									f = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
									f.seek(0);
									f.writeBytes(FileControl.lastSavedDirectory.getAbsolutePath());
								}
								catch (IOException e) {
									e.printStackTrace();
								}
								finally {
									try {
										f.close();
									}
									catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				}
				case 2: { // Open
					RandomAccessFile raf = null;
					// String backupPath = LevelEditor.SAVED_PATH_DATA;
					try {
						raf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
						FileControl.lastSavedDirectory = new File(raf.readLine());
					}
					catch (FileNotFoundException e) {
						FileControl.lastSavedDirectory = new File(System.getProperty("user.dir"));
					}
					catch (IOException e) {
						FileControl.lastSavedDirectory = new File(System.getProperty("user.dir"));
					}
					catch (NullPointerException e) {
						// If nothing has been found, set the last saved directory to where the VM
						// initialized.
						FileControl.lastSavedDirectory = new File(System.getProperty("user.dir"));
					}
					finally {
						try {
							raf.close();
						}
						catch (IOException e) {}
					}

					final JFileChooser opener = new JFileChooser();

					JList<Class<?>> list = this.findFileList(opener);
					LOOP_TEMP1:
					for (MouseListener l : list.getMouseListeners()) {
						if (l.getClass().getName().indexOf("FilePane") >= 0) {
							list.removeMouseListener(l);
							list.addMouseListener(new MouseListener() {
								@Override
								public void mouseClicked(MouseEvent e) {
									if (e.getClickCount() == 1) {
										File file = opener.getSelectedFile();
										if (file != null) {
											MetalFileChooserUI ui = (MetalFileChooserUI) opener.getUI();
											ui.setFileName(file.getName());
										}
									}
									else if (e.getClickCount() == 2) {
										File file = opener.getSelectedFile();
										if (file != null) {
											if (file.isDirectory()) {
												opener.setCurrentDirectory(file);
											}
											else if (file.isFile()) {
												opener.setSelectedFile(file);
											}
											MetalFileChooserUI ui = (MetalFileChooserUI) opener.getUI();
											ui.setFileName(file.getName());
										}
									}
								}

								@Override
								public void mouseEntered(MouseEvent e) {}

								@Override
								public void mouseExited(MouseEvent e) {}

								@Override
								public void mousePressed(MouseEvent e) {}

								@Override
								public void mouseReleased(MouseEvent e) {}
							});
							break LOOP_TEMP1;
						}
					}

					opener.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					opener.setCurrentDirectory(FileControl.lastSavedDirectory);
					opener.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
					opener.setVisible(true);
					int answer = opener.showOpenDialog(null);
					if (answer == JFileChooser.APPROVE_OPTION) {
						try {
							File f = opener.getSelectedFile();
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
						catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					break;
				}
				case 4: { // Tileset
					EditorConstants.metadata = Metadata.Pixel_Data;
					this.editor.validate();
					break;
				}
				case 5: { // Trigger
					EditorConstants.metadata = Metadata.Triggers;
					this.editor.validate();
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
