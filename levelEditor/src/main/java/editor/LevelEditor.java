/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import editor.EditorConstants.Metadata;
import interfaces.Tileable;
import script_editor.ScriptEditor;

//TODO(6/23/2015): Redo reading/writing level files. Next time, aim for binary files, instead of PNG bitmap files. This is for incorporating 
//maps and scripts together.

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = -8739477187675627751L;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 144;
	public static final int SIZE = 4;
	public static final String NAME_TITLE = "Level Editor (Hobby)";
	public static final String SAVED_PATH_DATA = "cache.ini";

	private List<Data> filepaths = new ArrayList<>();

	public ControlPanel controlPanel;
	public FileControl fileControlPanel;
	public DrawingBoard drawingBoardPanel;
	public TilePropertiesPanel propertiesPanel;
	public StatusPanel statusPanel;
	public Properties properties;
	public ScriptEditor scriptEditor;

	public String message;
	public boolean running;
	public EditorInput input;

	@SuppressWarnings("unused")
	private String mapAreaName;

	public LevelEditor(String name) {
		super(name);
		this.running = true;
		Dimension size = new Dimension(LevelEditor.WIDTH * LevelEditor.SIZE, LevelEditor.HEIGHT * LevelEditor.SIZE);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(size);// Mac issue.
		this.setPreferredSize(size); // Mac issue.
		this.setMinimumSize(size); // Mac issue.
		this.setMaximumSize(size); // Mac issue.
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				LevelEditor.this.running = false;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Runtime.getRuntime().exit(0);
					}
				});
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (LevelEditor.this.input == null) {
					LevelEditor.this.input = new EditorInput(editor.LevelEditor.this);
					LevelEditor.this.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.addMouseMotionListener(LevelEditor.this.input);
				}
				if (LevelEditor.this.fileControlPanel == null) {
					LevelEditor.this.fileControlPanel = new FileControl(editor.LevelEditor.this);
					LevelEditor.this.fileControlPanel.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.fileControlPanel.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.fileControlPanel, BorderLayout.NORTH);
					LevelEditor.this.validate();
				}
				if (LevelEditor.this.controlPanel == null) {
					LevelEditor.this.controlPanel = new ControlPanel(editor.LevelEditor.this);
					LevelEditor.this.controlPanel.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.controlPanel.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.controlPanel, BorderLayout.WEST);
					LevelEditor.this.validate();
				}
				if (LevelEditor.this.drawingBoardPanel == null) {
					LevelEditor.this.drawingBoardPanel = new DrawingBoard(editor.LevelEditor.this, 20, 20);
					LevelEditor.this.drawingBoardPanel.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.drawingBoardPanel.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.drawingBoardPanel, BorderLayout.CENTER);
					LevelEditor.this.drawingBoardPanel.start();
				}

				// TODO: Add Trigger properties here.
				if (LevelEditor.this.properties == null) {
					LevelEditor.this.properties = new Properties(editor.LevelEditor.this);
					LevelEditor.this.properties.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.properties.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.properties, BorderLayout.EAST);
					LevelEditor.this.validate();
				}

				if (LevelEditor.this.statusPanel == null) {
					LevelEditor.this.statusPanel = new StatusPanel();
					LevelEditor.this.statusPanel.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.statusPanel.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.statusPanel, BorderLayout.SOUTH);
					LevelEditor.this.validate();
				}
				LevelEditor.this.initialize();
			}
		});

		this.createCache();

	}

	/**
	 * <p>
	 * Generates a cache file for saving the last known directory the editor knew of.
	 * </p>
	 * 
	 * <p>
	 * Upon initialization, the default saved directory will be the editor's file location path.
	 * </p>
	 * 
	 * @return Nothing.
	 */
	public void createCache() {
		File file = new File(LevelEditor.SAVED_PATH_DATA);
		RandomAccessFile raf = null;
		if (!file.exists()) {
			try {
				// Creates a cache file that contains both the File Control's last saved
				// directory and
				// The script editor's last saved directory. It will be separated by two lines.

				// Made it so that it immediately saves the current editor file location upon
				// initialization.
				raf = new RandomAccessFile(file, "rw");
				raf.setLength(0);
				raf.seek(0);
				raf.writeBytes(FileControl.lastSavedDirectory.getAbsolutePath());
				raf.writeBytes(System.getProperty("line.separator"));
				raf.writeBytes(ScriptEditor.LAST_SAVED_DIRECTORY.getAbsolutePath());
				raf.writeBytes(System.getProperty("line.separator"));
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if (raf != null)
						raf.close();
				}
				catch (IOException e) {}
			}
		}
		else {
			// Reads in two last saved directories in the cache.
			// If it fails, revert back to original saved directories.
			File fileBackup1 = FileControl.lastSavedDirectory;
			File fileBackup2 = ScriptEditor.LAST_SAVED_DIRECTORY;
			boolean isCorrupted = false;
			try {
				raf = new RandomAccessFile(file, "r");
				raf.seek(0);
				String fileControlFilePath = raf.readLine();
				String scriptEditorFilePath = raf.readLine();
				FileControl.lastSavedDirectory = new File(fileControlFilePath);
				ScriptEditor.LAST_SAVED_DIRECTORY = new File(scriptEditorFilePath);
			}
			catch (IOException | NullPointerException e) {
				FileControl.lastSavedDirectory = fileBackup1;
				ScriptEditor.LAST_SAVED_DIRECTORY = fileBackup2;
				isCorrupted = true;
			}
			finally {
				try {
					raf.close();
				}
				catch (IOException e) {}
				if (isCorrupted) {
					file.delete();
				}
			}
		}
	}

	@Override
	public void validate() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (LevelEditor.this.statusPanel != null) {
					StringBuilder builder = new StringBuilder();
					builder.append("Picked: " + editor.LevelEditor.this.controlPanel.getPickedEntityName() + " ");
					if (!LevelEditor.this.input.isDragging()) {
						// This is how we do the [panning + pixel position] math.
						int w = 0;
						int h = 0;
						try {
							w = (LevelEditor.this.input.offsetX + LevelEditor.this.input.mouseX) / Tileable.WIDTH;
						}
						catch (Exception e) {
							w = (LevelEditor.this.input.offsetX + LevelEditor.this.input.mouseX) / (LevelEditor.WIDTH * LevelEditor.SIZE);
						}
						try {
							h = (LevelEditor.this.input.offsetY + LevelEditor.this.input.mouseY) / Tileable.HEIGHT;
						}
						catch (Exception e) {
							h = (LevelEditor.this.input.offsetY + LevelEditor.this.input.mouseY) / (LevelEditor.WIDTH * LevelEditor.SIZE);
						}
						LevelEditor.this.statusPanel.setMousePositionText(w, h);
					}
					else {
						try {
							LevelEditor.this.statusPanel.setMousePositionText(
								LevelEditor.this.input.oldX / LevelEditor.this.drawingBoardPanel.getBitmapWidth(),
								LevelEditor.this.input.oldY / LevelEditor.this.drawingBoardPanel.getBitmapHeight()
							);
						}
						catch (Exception e) {
							LevelEditor.this.statusPanel.setMousePositionText(0, 0);
						}
					}
					LevelEditor.this.statusPanel.setStatusMessageText(builder.toString());
				}

				if (LevelEditor.this.controlPanel != null)
					LevelEditor.this.controlPanel.validate();
				if (LevelEditor.this.fileControlPanel != null)
					LevelEditor.this.fileControlPanel.validate();
				if (LevelEditor.this.drawingBoardPanel != null)
					LevelEditor.this.drawingBoardPanel.validate();
				if (LevelEditor.this.properties != null)
					LevelEditor.this.properties.validate();
				if (LevelEditor.this.statusPanel != null)
					LevelEditor.this.statusPanel.validate();
			}
		});
		super.validate();
	}

	public final void initialize() {
		EditorConstants.metadata = Metadata.Pixel_Data;
		this.drawingBoardPanel.newImage(15, 15);
		this.setMapAreaName("Untitled");
	}

	public List<Data> getResourceFilePaths() {
		return this.filepaths;
	}

	public void setMapAreaName(String name) {
		this.mapAreaName = name;
	}

	public static void main(String[] args) {
		new LevelEditor(LevelEditor.NAME_TITLE);
	}
}