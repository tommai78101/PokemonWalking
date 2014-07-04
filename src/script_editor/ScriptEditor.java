package script_editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import editor.FileControl;
import editor.LevelEditor;
import editor.Trigger;

public class ScriptEditor extends JFrame {
	public static final String TITLE = "Script Editor (Hobby)";
	public static final int WIDTH = 700;
	public static final int HEIGHT = 400;
	
	public static File LAST_SAVED_DIRECTORY = FileControl.lastSavedDirectory;
	
	public LevelEditor parent;
	public ScriptInput input;
	public ScriptToolbar scriptToolbar;
	public ScriptViewer scriptViewer;
	public ScriptChanger scriptChanger;
	private boolean modifiedFlag = false;
	
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
				
				// TODO: 2014-6-26: Add the ability to save temp script data on the fly. Closing included.
				
				ScriptEditor.this.dispose();
				// 7 is a magic number for "Script Editor" button action command. I don't like to make a new variable just for this.
				JButton button = ScriptEditor.this.parent.fileControlPanel.buttonCache.get(Integer.toString(7));
				button.setEnabled(true);
				ScriptEditor.this.parent.scriptEditor = null;
			}
		});
		
		addingComponents();
		
		LAST_SAVED_DIRECTORY = parent.fileControlPanel.lastSavedDirectory;
		ToolTipManager.sharedInstance().setInitialDelay(1);
		this.setTitle(this.getTitle() + " - Untitled.script");
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
	
	public void load(File script) {
		String format = script.getName();
		if (!format.endsWith(".script")) {
			JOptionPane.showMessageDialog(null, "Incorrect file format - Please open files ending with \".script\"");
			return;
		}
		System.out.println("Opened Location: " + script.getAbsolutePath());
		BufferedReader reader = null;
		try {
			
			// TODO: Loading goes here.
			
			JList<Trigger> triggerList = this.scriptViewer.getTriggerList();
			DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) triggerList.getModel();
			model.clear();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(script)));
			String line = null;
			String[] tokens;
			Trigger trigger = null;
			StringBuilder builder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("$")){
					tokens = line.split("\\$");
					trigger = new Trigger();
					trigger.setTriggerID(Short.valueOf(tokens[1]));
				}
				else if (line.startsWith("@")){
					tokens = line.split("@");
					trigger.setName(tokens[1]);
				}
				else if (line.startsWith("%")){
					trigger.setScript(builder.toString());
					model.addElement(trigger);
					builder.setLength(0);
				}
				else {
					builder.append(line).append("\n");
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.scriptViewer.getTriggerList().clearSelection();
		this.scriptViewer.revalidate();
	}
	
	public boolean isBeingModified(){
		return this.modifiedFlag;
	}
	
	public void setModifiedFlag(boolean value){
		if (!value){
			String str = this.getTitle();
			if (str.endsWith("*"))
				this.setTitle(str.substring(0, str.length()-1));
		}
		this.modifiedFlag = value;
	}
	
	public void save(File script) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(script)));
			DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) this.scriptViewer.getTriggerList().getModel();
			
			for (int i = 0; i < model.getSize(); i++) {
				Trigger t = model.get(i);
				try {
					writer.write("$" + Short.toString(t.getTriggerID()));
					writer.newLine();
					writer.write("@" + t.getName().replace(" ", "_"));
					writer.newLine();
					JTextArea area = this.scriptChanger.getScriptArea();
					writer.write(area.getText());
					writer.newLine();
					writer.write("%");
					writer.newLine();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.flush();
				writer.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Saved Location: " + script.getAbsolutePath());
	}
}
