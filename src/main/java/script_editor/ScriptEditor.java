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

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import editor.FileControl;
import editor.LevelEditor;
import editor.Trigger;

public class ScriptEditor extends JFrame {
	private static final long serialVersionUID = 1L;
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
	@SuppressWarnings("unused")
	private String scriptName;

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

		// Generates a scripting tutorial upon loading script editor. This generated file will not be persistent and the script editor will not overwrite if the file exists.
		generateScriptingTutorial();

		LAST_SAVED_DIRECTORY = FileControl.lastSavedDirectory;
		ToolTipManager.sharedInstance().setInitialDelay(1);
		this.setTitle(this.getTitle() + " - Untitled.script");
		this.setScriptName("Untitled");
	}

	/**
	 * 
	 * <p>
	 * Generates a ReadMe text file upon opening the Script Editor.
	 * </p>
	 * 
	 * @return Nothing.
	 * */
	public void generateScriptingTutorial() {
		File file = new File("readme.txt");
		if (!file.exists()) {
			// There are many different ways you can do to write data to files. This is one of them.
			String[] tutorialLines = new String[] { "/ Automation", "/", "/ Entities that can walk, or run, must be required to have movements for the game", "/ to feel lively.", "/", "/ More commands to come.", " ", "/ _: Whitespaces.", "/ @: Trigger name.",
					"/ ^: [Direction, Steps]. Can be chained for delaying scripted movements.", "/ $: Start of script. Always appear at beginning of script.", "/ %: Script delimiter. Always appear at end of script.", "/ #: Speech Dialogue.", "/ /: Comments. Gets ignored.",
					"/ ?: Question Dialogue.", "/ +: Affirmative dialogue.", "/ -: Negative dialogue", "/ [: Affirmative Action", "/ ]: Negative Action", "/ ;: Repeat Flag. If contains ';', it means it's enabled by default.", " ",
					"/ DO NOT CHANGE/REMOVE THIS TRIGGER SCRIPT. THIS IS RESERVED ONLY. FOLLOW THIS FORMAT.", "$0", "@Eraser", "%" };

			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("readme.txt"), "utf-8"));
				for (int i = 0; i < tutorialLines.length; i++) {
					writer.write(tutorialLines[i]);
					writer.newLine();
				}
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				e.printStackTrace();
			}
			finally {
				try {
					writer.close();
				}
				catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					e.printStackTrace();
				}
			}
		}
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

	// (11/24/2014): This is where I load triggers at. This is completed, but may require double-checking to be very sure.
	/**
	 * <p>
	 * Loads the file script.
	 * </p>
	 * 
	 * <p>
	 * This method may require double-checking in the codes, just to be very sure that it is absolutely working as intended. Reason for this is that this method is used as a guideline for loading custom scripts into the game itself.
	 * </p>
	 * 
	 * @param script
	 *            - Takes in a SCRIPT file object, which is the scripting file the game and the script editor uses.
	 * @return Nothing.
	 * */
	public void load(File script) {
		String format = script.getName();
		if (!format.endsWith(".script")) {
			JOptionPane.showMessageDialog(null, "Incorrect file format - Please open files ending with \".script\"");
			return;
		}
		System.out.println("Opened Location: " + script.getAbsolutePath());
		BufferedReader reader = null;
		try {
			JList<Trigger> triggerList = this.scriptViewer.getTriggerList();
			DefaultListModel<Trigger> scriptTriggerListModel = (DefaultListModel<Trigger>) triggerList.getModel();
			scriptTriggerListModel.clear();

			JComboBox<Trigger> comboTriggerList = this.parent.properties.getTriggerList();
			DefaultComboBoxModel<Trigger> editorTriggerComboModel = (DefaultComboBoxModel<Trigger>) comboTriggerList.getModel();
			editorTriggerComboModel.removeAllElements();

			Trigger trigger = new Trigger();
			trigger.setTriggerID((short) 0);
			trigger.setName("Eraser");
			editorTriggerComboModel.addElement(trigger);
			comboTriggerList.setSelectedIndex(0);

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(script)));
			String line = null;
			String[] tokens;
			trigger = null;
			StringBuilder builder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("$")) {
					tokens = line.split("\\$");
					trigger = new Trigger();
					trigger.setTriggerID(Short.valueOf(tokens[1]));
				}
				else if (line.startsWith("@")) {
					tokens = line.split("@");
					trigger.setName(tokens[1]);
				}
				else if (line.startsWith("%")) {
					trigger.setScript(builder.toString());
					scriptTriggerListModel.addElement(trigger);
					editorTriggerComboModel.addElement(trigger);
					builder.setLength(0);
				}
				else if (line.startsWith("#")) {
					// Dialogue
				}
				else if (line.startsWith("?")) {
					// Question Mark
				}
				else if (line.startsWith("+")) {
					// Affirmative Answer
				}
				else if (line.startsWith("-")) {
					// Negative Answer
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
		this.parent.revalidate();
	}

	public boolean isBeingModified() {
		return this.modifiedFlag;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public void setModifiedFlag(boolean value) {
		if (!value) {
			String str = this.getTitle();
			if (str.endsWith("*"))
				this.setTitle(str.substring(0, str.length() - 1));
		}
		this.modifiedFlag = value;
	}

	public LevelEditor getLevelEditorParent() {
		return this.parent;
	}

	public void save(File script) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(script)));
			DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) this.scriptViewer.getTriggerList().getModel();

			writer.write("$0");
			writer.newLine();
			writer.write("@Eraser");
			writer.newLine();
			writer.write("%");
			writer.newLine();
			writer.newLine();
			writer.newLine();

			for (int i = 0; i < model.getSize(); i++) {
				Trigger t = model.get(i);
				try {
					writer.write("$" + Short.toString(t.getTriggerID()));
					writer.newLine();
					writer.write("@" + t.getName().replace(" ", "_"));
					writer.newLine();
					writer.write(t.getScript());
					writer.newLine();
					writer.write("%");
					writer.newLine();
					writer.newLine();
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
