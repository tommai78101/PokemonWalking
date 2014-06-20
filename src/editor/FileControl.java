/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.EditorConstants.Metadata;

public class FileControl extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private String[] tags = new String[] { "New", "Save", "Open", "", "Tileset", "Trigger" };
	private LevelEditor editor;
	HashMap<String, JButton> buttonCache = new HashMap<String, JButton>();
	public static File lastSavedDirectory;
	
	public FileControl(LevelEditor editor) {
		super();
		this.editor = editor;
		this.setLayout(new GridLayout(1, tags.length));
		FileControl.lastSavedDirectory = new File("C:\\");
		
		for (int i = 0; i < tags.length; i++) {
			if (i == 3) {
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
		JButton button = (JButton) event.getSource();
		String command = button.getActionCommand();
		try {
			int value = Integer.valueOf(command);
			switch (value) {
				case 0: // New
				{
					editor.drawingBoardPanel.newImage();
					break;
				}
				case 1: {// Save
					if (!editor.drawingBoardPanel.hasBitmap()) {
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
						catch (IOException e) {
						}
					}
					
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					chooser.setCurrentDirectory(lastSavedDirectory);
					chooser.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
					chooser.setVisible(true);
					int result = chooser.showSaveDialog(null);
					if (result == JFileChooser.APPROVE_OPTION) {
						try {
							BufferedImage img = editor.drawingBoardPanel.getMapImage();
							if (img != null) {
								File file = chooser.getSelectedFile();
								String filename = file.getName();
								while (filename.endsWith(".png"))
									filename = filename.substring(0, filename.length() - ".png".length());
								FileControl.lastSavedDirectory = chooser.getCurrentDirectory();
								ImageIO.write(img, "png", new File(FileControl.lastSavedDirectory.getAbsolutePath() + "\\" + filename + ".png"));
								
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
						catch (IOException e) {
						}
					}
					
					JFileChooser opener = new JFileChooser();
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
							editor.drawingBoardPanel.openMapImage(image);
							RandomAccessFile rf = null;
							try {
								rf = new RandomAccessFile(LevelEditor.SAVED_PATH_DATA, "rw");
								rf.seek(0);
								rf.writeBytes(FileControl.lastSavedDirectory.getAbsolutePath());
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
						catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					break;
				}
				case 4: { // Tileset
					EditorConstants.metadata = Metadata.Pixel_Data;
					editor.validate();
					break;
				}
				case 5: { // Trigger
					EditorConstants.metadata = Metadata.Triggers;
					editor.validate();
					break;
				}
			}
		}
		catch (NumberFormatException e) {
			return;
		}
	}
}
