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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import common.Debug;
import common.Sha2Utils;
import common.Tileable;
import editor.EditorConstants.Metadata;
import script_editor.ScriptEditor;

//TODO(6/23/2015): Redo reading/writing level files. Next time, aim for binary files, instead of PNG bitmap files. This is for incorporating 
//maps and scripts together.

public class LevelEditor extends JFrame {
	public static final int WIDTH = 160;
	public static final int HEIGHT = 144;
	public static final int SIZE = 4;
	public static final String NAME_TITLE = "Level Editor (Hobby)";
	public static final String SAVED_PATH_DATA = "cache.ini";
	public static final int CHECKSUM_MAX_BYTES_LENGTH = 16;
	public static final String defaultPath = Paths.get("").toAbsolutePath().toString();

	// For cache directory path index, fixed index in the array list.
	public static final int FileControlIndex = 0;

	private static final long serialVersionUID = -8739477187675627751L;

	public ControlPanel controlPanel;
	public FileControl fileControlPanel;
	public DrawingBoard drawingBoardPanel;
	public StatusPanel statusPanel;
	public SelectionDropdownMenu properties;
	public ScriptEditor scriptEditor;

	public boolean running;
	public String message;
	public EditorInput input;

	private int uniqueAreaID;
	private String sha2Checksum = "";

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
				boolean shouldValidate = false;
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
					shouldValidate = true;
				}
				if (LevelEditor.this.controlPanel == null) {
					LevelEditor.this.controlPanel = new ControlPanel(editor.LevelEditor.this);
					LevelEditor.this.controlPanel.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.controlPanel.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.controlPanel, BorderLayout.WEST);
					shouldValidate = true;
				}
				if (LevelEditor.this.drawingBoardPanel == null) {
					LevelEditor.this.drawingBoardPanel = new DrawingBoard(editor.LevelEditor.this, 20, 20);
					LevelEditor.this.drawingBoardPanel.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.drawingBoardPanel.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.drawingBoardPanel, BorderLayout.CENTER);
					LevelEditor.this.drawingBoardPanel.start();
					shouldValidate = true;
				}

				// TODO: Add Trigger properties here.
				if (LevelEditor.this.properties == null) {
					LevelEditor.this.properties = new SelectionDropdownMenu(editor.LevelEditor.this);
					LevelEditor.this.properties.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.properties.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.properties, BorderLayout.EAST);
					shouldValidate = true;
				}

				if (LevelEditor.this.statusPanel == null) {
					LevelEditor.this.statusPanel = new StatusPanel();
					LevelEditor.this.statusPanel.addMouseListener(LevelEditor.this.input);
					LevelEditor.this.statusPanel.addMouseMotionListener(LevelEditor.this.input);
					LevelEditor.this.add(LevelEditor.this.statusPanel, BorderLayout.SOUTH);
					shouldValidate = true;
				}
				LevelEditor.this.initialize();
				if (shouldValidate) {
					LevelEditor.this.validate();
				}
			}
		});
		this.createOrReadCache();
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
	public void createOrReadCache() {
		List<String> cachedDirectoryPaths = new ArrayList<>();
		File file = new File(LevelEditor.SAVED_PATH_DATA);
		if (!file.exists()) {
			// Set the default paths first
			FileControl.lastSavedDirectory = new File(LevelEditor.defaultPath);
			ScriptEditor.lastSavedDirectory = FileControl.lastSavedDirectory;

			cachedDirectoryPaths.add(FileControl.lastSavedDirectory.getAbsolutePath());

			try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
				this.storeCachedDirectories(raf, cachedDirectoryPaths);
			}
			catch (IOException e) {
				Debug.exception(e);
			}
		}
		else {
			try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
				this.fetchCachedDirectories(raf, cachedDirectoryPaths);
			}
			catch (IOException e) {
				Debug.exception(e);
			}

			FileControl.lastSavedDirectory = new File(cachedDirectoryPaths.get(LevelEditor.FileControlIndex));
			ScriptEditor.lastSavedDirectory = FileControl.lastSavedDirectory;
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
						int temp = 0;
						try {
							temp = (LevelEditor.this.input.offsetX + LevelEditor.this.input.mouseX);
							if (temp >= 0)
								w = temp / Tileable.WIDTH;
							else
								w = (temp / Tileable.WIDTH) - 1;
						}
						catch (Exception e) {
							w = (LevelEditor.this.input.offsetX + LevelEditor.this.input.mouseX) / (LevelEditor.WIDTH * LevelEditor.SIZE);
						}
						try {
							temp = (LevelEditor.this.input.offsetY + LevelEditor.this.input.mouseY);
							if (temp >= 0)
								h = temp / Tileable.HEIGHT;
							else
								h = (temp / Tileable.HEIGHT) - 1;
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
					LevelEditor.this.statusPanel.setChecksumLabel(LevelEditor.this.getChecksum());
				}

				if (LevelEditor.this.controlPanel != null) {
					LevelEditor.this.controlPanel.validate();
					if (LevelEditor.this.controlPanel.getPropertiesPanel() != null)
						LevelEditor.this.controlPanel.getPropertiesPanel().validate();
				}
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
		this.generateChecksum();
		this.drawingBoardPanel.newImage(15, 15);
		this.setMapAreaName("Untitled");
	}

	public void setMapAreaName(String name) {
		this.mapAreaName = name;
	}

	public int getUniqueAreaID() {
		return this.uniqueAreaID;
	}

	public void setUniqueAreaID(int uniqueAreaID) {
		this.uniqueAreaID = uniqueAreaID;
	}

	public String getChecksum() {
		if (this.sha2Checksum == null || this.sha2Checksum.trim().isBlank() || this.sha2Checksum.trim().isEmpty()) {
			return this.generateChecksum();
		}
		return this.sha2Checksum;
	}

	public int setChecksum(int[] pixels, int startIndex, int readLength) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int readIndex = startIndex;
		for (int i = 0; i < readLength; i++, readIndex++) {
			int pixel = pixels[readIndex];
			baos.write((pixel >> 24) & 0xFF);
			baos.write((pixel >> 16) & 0xFF);
			baos.write((pixel >> 8) & 0xFF);
			baos.write(pixel & 0xFF);
		}
		// SHA-512 checksum are written using ASCII.
		String result = baos.toString(StandardCharsets.US_ASCII);
		this.sha2Checksum = result;
		return readIndex;
	}

	public String generateChecksum() {
		this.sha2Checksum = Sha2Utils.generateRandom(UUID.randomUUID().toString()).substring(0, LevelEditor.CHECKSUM_MAX_BYTES_LENGTH);
		return this.sha2Checksum;
	}

	// --------------------------------------------------------------------------------
	// Main method

	public static void main(String[] args) {
		new LevelEditor(LevelEditor.NAME_TITLE);
	}

	// --------------------------------------------------------------------------------
	// Private methods

	private void fetchCachedDirectories(RandomAccessFile file, List<String> output) throws IOException {
		String buffer = null;
		file.seek(0);
		output.clear();
		while ((buffer = file.readLine()) != null) {
			output.add(buffer);
		}
	}

	private void storeCachedDirectories(RandomAccessFile file, List<String> input) throws IOException {
		file.setLength(0);
		file.seek(0);
		for (String buffer : input) {
			file.writeBytes(buffer);
			file.write(System.lineSeparator().getBytes());
		}
	}
}