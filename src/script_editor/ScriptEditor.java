package script_editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import editor.LevelEditor;

public class ScriptEditor extends JFrame {
	public static final String TITLE = "Script Editor (Hobby)";
	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;
	
	public LevelEditor parent;
	public ScriptInput input;
	public ScriptToolbar scriptToolbar;
	public ScriptViewer scriptViewer;
	public ScriptChanger scriptChanger;
	
	public ScriptEditor(String title, LevelEditor parent) {
		super(title);
		this.parent = parent;
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // For good measure.
		this.pack();
		this.setVisible(true);
		
		Dimension size = new Dimension(WIDTH, HEIGHT);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				
				//TODO: 2014-6-26: Add the ability to save temp script data on the fly. Closing included.
				
				
				ScriptEditor.this.dispose();
				// 7 is a magic number for "Script Editor" button action command. I don't like to make a new variable just for this.
				JButton button =ScriptEditor.this.parent.fileControlPanel.buttonCache.get(Integer.toString(7));
				button.setEnabled(true);
				ScriptEditor.this.parent.scriptEditor = null;
			}
		});
		
		addingComponents();
	}
	
	public void addingComponents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (input == null) {
					input = new ScriptInput();
					addMouseListener(input);
					addMouseMotionListener(input);
				}
				if (scriptToolbar == null) {
					scriptToolbar = new ScriptToolbar(ScriptEditor.this);
					scriptToolbar.addMouseListener(input);
					scriptToolbar.addMouseMotionListener(input);
					add(scriptToolbar, BorderLayout.NORTH);
					validate();
				}
				if (scriptViewer == null) {
					scriptViewer = new ScriptViewer(ScriptEditor.this);
					scriptViewer.addMouseListener(input);
					scriptViewer.addMouseMotionListener(input);
					add(scriptViewer, BorderLayout.WEST);
					validate();
				}
				if (scriptChanger == null) {
					scriptChanger = new ScriptChanger(ScriptEditor.this);
					scriptChanger.addMouseListener(input);
					scriptChanger.addMouseMotionListener(input);
					add(scriptChanger, BorderLayout.CENTER);
					validate();
				}
			}
		});
	}
}
