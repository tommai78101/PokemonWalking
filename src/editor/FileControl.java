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
import javax.swing.JPanel;

public class FileControl extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private String[] tags = new String[]{"New", "Save", "Open"};
	private LevelEditor editor;
	HashMap<String, JButton> buttonCache = new HashMap<String, JButton>();
	
	public FileControl(LevelEditor editor) {
		super();
		this.editor = editor;
		this.setLayout(new GridLayout(1, tags.length));
		
		for (int i = 0; i < tags.length; i++) {
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
				case 0: //New
				{
					editor.drawingBoardPanel.newImage();
					break;
				}
				case 1: //Save
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					chooser.setVisible(true);
					int result = chooser.showSaveDialog(null);
					if (result == JFileChooser.APPROVE_OPTION) {
						try {
							BufferedImage img = editor.drawingBoardPanel.getMapImage();
							File file = chooser.getSelectedFile();
							ImageIO.write(img, "png", new File(file.getAbsolutePath() + "\\" + file.getName() + ".png"));
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case 2: //Open
					JFileChooser opener = new JFileChooser();
					opener.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					opener.setVisible(true);
					int answer = opener.showOpenDialog(null);
					if (answer == JFileChooser.APPROVE_OPTION) {
						try {
							File f = opener.getSelectedFile();
							BufferedImage image = ImageIO.read(f);
							editor.drawingBoardPanel.openMapImage(image);
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					break;
			}
		}
		catch (NumberFormatException e) {
			return;
		}
	}
	
}
