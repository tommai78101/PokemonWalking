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
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileControl extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private String[] tags = new String[] { "New", "Save", "Open", "", "Tileset", "Trigger"};
	private LevelEditor editor;
	HashMap<String, JButton> buttonCache = new HashMap<String, JButton>();
	private File lastSavedDirectory;
	
	public FileControl(LevelEditor editor) {
		super();
		this.editor = editor;
		this.setLayout(new GridLayout(1, tags.length));
		this.lastSavedDirectory = new File("C:\\Users\\Student\\Desktop");
		
		for (int i = 0; i < tags.length; i++) {
			if (i == 3){
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
				case 1: // Save
					if (!editor.drawingBoardPanel.hasBitmap()) {
						JOptionPane.showMessageDialog(null, "No created maps to save.");
						break;
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
								this.lastSavedDirectory = chooser.getCurrentDirectory();
								ImageIO.write(img, "png", new File(this.lastSavedDirectory.getAbsolutePath() + "\\" + filename + ".png"));
							}
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case 2: // Open
					JFileChooser opener = new JFileChooser();
					opener.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					opener.setCurrentDirectory(this.lastSavedDirectory);
					opener.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
					opener.setVisible(true);
					int answer = opener.showOpenDialog(null);
					if (answer == JFileChooser.APPROVE_OPTION) {
						try {
							File f = opener.getSelectedFile();
							this.lastSavedDirectory = f.getParentFile();
							BufferedImage image = ImageIO.read(f);
							editor.drawingBoardPanel.openMapImage(image);
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					break;
				case 3: { //Refresh
					break;
				}
				case 4: { //Tileset
					break;
				}
				case 5: { //Trigger
					break;
				}
			}
		}
		catch (NumberFormatException e) {
			return;
		}
	}
}
