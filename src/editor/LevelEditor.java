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
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = -8739477187675627751L;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 144;
	public static final int SIZE = 4;
	
	private ArrayList<Data> filepaths = new ArrayList<Data>();
	
	public ControlPanel controlPanel;
	public FileControl fileControlPanel;
	public DrawingBoard drawingBoardPanel;
	public TilePropertiesPanel propertiesPanel;
	public StatusPanel statusPanel;
	public Properties properties;
	
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
					drawingBoardPanel = new DrawingBoard(editor.LevelEditor.this);
					drawingBoardPanel.addMouseListener(input);
					drawingBoardPanel.addMouseMotionListener(input);
					drawingBoardPanel.setSize(20, 20);
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
					if (!input.isDragging()) {
						// This is how we do the [panning + pixel position] math.
						int w = 0;
						int h = 0;
						try {
							w = (input.offsetX + input.mouseX) / drawingBoardPanel.getBitmapWidth();
						}
						catch (Exception e) {
							w = (input.offsetX + input.mouseX) / (WIDTH * SIZE);
						}
						try {
							h = (input.offsetY + input.mouseY) / drawingBoardPanel.getBitmapHeight();
						}
						catch (Exception e) {
							h = (input.offsetY + input.mouseY) / (WIDTH * SIZE);
						}
						statusPanel.setMousePositionText(w, h);
					}
					else {
						try {
							statusPanel.setMousePositionText(input.oldX / drawingBoardPanel.getBitmapWidth(), input.oldY / drawingBoardPanel.getBitmapHeight());
						}
						catch (Exception e) {
							statusPanel.setMousePositionText(0, 0);
						}
					}
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