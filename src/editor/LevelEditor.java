/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = -8739477187675627751L;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 144;
	public static final int SIZE = 3;

	private ArrayList<Data> filepaths = new ArrayList<Data>();

	public ControlPanel controlPanel;
	public FileControl fileControlPanel;
	public DrawingBoard drawingBoardPanel;
	public TilePropertiesPanel propertiesPanel;
	public StatusPanel statusPanel;

	public String message;
	public boolean running;
	public EditorInput input;

	public LevelEditor(String name) {
		super(name);
		running = true;
		Dimension size = new Dimension(WIDTH * SIZE, HEIGHT * SIZE);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				running = false;
			}
		});

		// final File folder = new File("res");
		// getAllFiles(folder);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (input == null) {
					input = new EditorInput(editor.LevelEditor.this);
					addMouseListener(input);
					// addMouseMotionListener(input);
				}
				if (fileControlPanel == null) {
					fileControlPanel = new FileControl(editor.LevelEditor.this);
					fileControlPanel.addMouseListener(input);
					// fileControlPanel.addMouseMotionListener(input);
					add(fileControlPanel, BorderLayout.NORTH);
					validate();
				}
				if (controlPanel == null) {
					controlPanel = new ControlPanel(editor.LevelEditor.this);
					controlPanel.addMouseListener(input);
					// controlPanel.addMouseMotionListener(input);
					add(controlPanel, BorderLayout.WEST);
					validate();
				}
				if (drawingBoardPanel == null) {
					drawingBoardPanel = new DrawingBoard(editor.LevelEditor.this);
					drawingBoardPanel.addMouseListener(input);
					drawingBoardPanel.addMouseMotionListener(input);
					drawingBoardPanel.setSize(20, 20);
					add(drawingBoardPanel, BorderLayout.CENTER);
					drawingBoardPanel.start();
				}
				if (statusPanel == null) {
					statusPanel = new StatusPanel();
					statusPanel.addMouseListener(input);
					// statusPanel.addMouseMotionListener(input);
					add(statusPanel, BorderLayout.SOUTH);
				}
			}
		});
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
					if (!input.isDragging())
						// This is how we do the [panning + pixel position] math.
						statusPanel.setMousePositionText((input.dx + input.mouseX), (input.dy + input.mouseY));
					else
						statusPanel.setMousePositionText(input.oldX, input.oldY);
					statusPanel.setStatusMessageText(builder.toString());
				}
			}
		});
	}

	public ArrayList<Data> getResourceFilePaths() {
		return this.filepaths;
	}

	public static void main(String[] args) {
		new LevelEditor("Level Editor (Hobby)");
	}
}