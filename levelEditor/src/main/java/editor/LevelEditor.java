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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import script_editor.ScriptEditor;
import entity.Tile;
import editor.EditorConstants.Metadata;

//TODO(6/23/2015): Redo reading/writing level files. Next time, aim for binary files, instead of PNG bitmap files. This is for incorporating 
//maps and scripts together.

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = -8739477187675627751L;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 144;
	public static final int SIZE = 4;
	public static final String NAME_TITLE = "Level Editor (Hobby)";
	public static final String SAVED_PATH_DATA = "cache.ini";

	private ArrayList<Data> filepaths = new ArrayList<Data>();

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
		running = true;
		Dimension size = new Dimension(WIDTH * SIZE, HEIGHT * SIZE);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(size);// Mac issue.
		this.setPreferredSize(size); // Mac issue.
		this.setMinimumSize(size); // Mac issue.
		this.setMaximumSize(size); // Mac issue.
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				running = false;
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
				if (input == null) {
					input = new EditorInput(editor.LevelEditor.this);
					addMouseListener(input);
					addMouseMotionListener(input);
				}
				if (fileControlPanel == null) {
					fileControlPanel = new FileControl(editor.LevelEditor.this);
					fileControlPanel.addMouseListener(input);
					fileControlPanel.addMouseMotionListener(input);
					add(fileControlPanel, BorderLayout.NORTH);
					validate();
				}
				if (controlPanel == null) {
					controlPanel = new ControlPanel(editor.LevelEditor.this);
					controlPanel.addMouseListener(input);
					controlPanel.addMouseMotionListener(input);
					add(controlPanel, BorderLayout.WEST);
					validate();
				}
				if (drawingBoardPanel == null) {
					drawingBoardPanel = new DrawingBoard(editor.LevelEditor.this, 20, 20);
					drawingBoardPanel.addMouseListener(input);
					drawingBoardPanel.addMouseMotionListener(input);
					add(drawingBoardPanel, BorderLayout.CENTER);
					drawingBoardPanel.start();
				}

				// TODO: Add Trigger properties here.
				if (properties == null) {
					properties = new Properties(editor.LevelEditor.this);
					properties.addMouseListener(input);
					properties.addMouseMotionListener(input);
					add(properties, BorderLayout.EAST);
					validate();
				}

				if (statusPanel == null) {
					statusPanel = new StatusPanel();
					statusPanel.addMouseListener(input);
					statusPanel.addMouseMotionListener(input);
					add(statusPanel, BorderLayout.SOUTH);
					validate();
				}
				initialize();
			}
		});

		createCache();

	}

	/**
	 * <p>
	 * Generates a cache file for saving the last known directory the editor knew
	 * of.
	 * </p>
	 * 
	 * <p>
	 * Upon initialization, the default saved directory will be the editor's file
	 * location path.
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
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (raf != null) 
						raf.close();
				} catch (IOException e) {
				}
			}
		} else {
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
			} catch (IOException | NullPointerException e) {
				FileControl.lastSavedDirectory = fileBackup1;
				ScriptEditor.LAST_SAVED_DIRECTORY = fileBackup2;
				isCorrupted = true;
			} finally {
				try {
					raf.close();
				} catch (IOException e) {
				}
				if (isCorrupted) {
					file.delete();
				}
			}
		}
	}

	@Override
	public void validate() {
		super.validate();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (statusPanel != null) {
					StringBuilder builder = new StringBuilder();
					builder.append("Picked: " + editor.LevelEditor.this.controlPanel.getPickedEntityName() + " ");
					if (!input.isDragging()) {
						// This is how we do the [panning + pixel position] math.
						int w = 0;
						int h = 0;
						try {
							w = (input.offsetX + input.mouseX) / Tile.WIDTH;
						} catch (Exception e) {
							w = (input.offsetX + input.mouseX) / (WIDTH * SIZE);
						}
						try {
							h = (input.offsetY + input.mouseY) / Tile.HEIGHT;
						} catch (Exception e) {
							h = (input.offsetY + input.mouseY) / (WIDTH * SIZE);
						}
						statusPanel.setMousePositionText(w, h);
					} else {
						try {
							statusPanel.setMousePositionText(input.oldX / drawingBoardPanel.getBitmapWidth(),
									input.oldY / drawingBoardPanel.getBitmapHeight());
						} catch (Exception e) {
							statusPanel.setMousePositionText(0, 0);
						}
					}
					statusPanel.setStatusMessageText(builder.toString());
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
	}

	public final void initialize() {
		EditorConstants.metadata = Metadata.Pixel_Data;
		this.drawingBoardPanel.newImage(15, 15);
		this.setMapAreaName("Untitled");
	}

	public ArrayList<Data> getResourceFilePaths() {
		return this.filepaths;
	}

	public void setMapAreaName(String name) {
		this.mapAreaName = name;
	}

	public static void main(String[] args) {
		new LevelEditor(LevelEditor.NAME_TITLE);
	}
}