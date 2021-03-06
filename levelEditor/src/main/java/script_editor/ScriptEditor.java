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
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

import common.Debug;
import editor.FileControl;
import editor.LevelEditor;
import editor.Trigger;

public class ScriptEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int ScriptDirectoryIndex = 1;

	public static final String TITLE = "Script Editor (Hobby)";
	public static final int WIDTH = 700;
	public static final int HEIGHT = 400;

	public static File lastSavedDirectory = FileControl.lastSavedDirectory;

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

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // For good measure.
		this.pack();
		this.setVisible(true);

		Dimension size = new Dimension(ScriptEditor.WIDTH, ScriptEditor.HEIGHT);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				// TODO: 2014-6-26: Add the ability to save temp script data on the fly. Closing
				// included.

				ScriptEditor.this.dispose();
				// 7 is a magic number for "Script Editor" button action command. I don't like
				// to make a new variable just for this.
				JButton button = ScriptEditor.this.parent.fileControlPanel.buttonCache.get(Integer.toString(7));
				button.setEnabled(true);
				ScriptEditor.this.parent.scriptEditor = null;
			}
		});
		this.addingComponents();

		// Generates a scripting tutorial upon loading script editor. This generated
		// file will not be persistent and the script editor will not overwrite if the
		// file exists.
		this.generateScriptingTutorial();

		ScriptEditor.lastSavedDirectory = FileControl.lastSavedDirectory;
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
	 */
	public void generateScriptingTutorial() {
		File file = new File("readme.txt");
		if (!file.exists()) {
			// There are many different ways you can do to write data to files. This is one
			// of them.
			// @formatter:off
			String[] tutorialLines = new String[] {
				"/ This is an automated content generation.", 
				" ",
				"/ Entities that can walk, or run, must be required to have movements for the game",
				"/ to feel lively.", 
				" ",
				"/ You can either use the corresponding symbols or tag names when adding new scripts.",
				" ",
				"/ More commands will be added.", 
				" ", 
				"/ /: Comments. Gets ignored.",
				"/ _: Whitespaces.",
				"/ @: Trigger name.",
				" ",
				"/ Tag names are wrapped inside < > angle brackets. Similar tags are comma-separated.",
				" ",
				"/ ^ <PathData>: [Direction, Steps]. Can be chained for delaying scripted movements.",
				"/ $ <BeginScript>: Start of script. Always appear at beginning of script.",
				"/ % <EndScript>: Script delimiter. Always appear at end of script.", 
				"/ # <Speech>: Speech Dialogue.",
				"/ ? <Question>: Question Dialogue.", 
				"/ + <Affirm>: Affirmative dialogue.",
				"/ - <Reject>: Negative dialogue", 
				"/ [ <Confirm>: Affirmative Action", 
				"/ ] <Deny>: Negative Action",
				"/ ; <Repeat, Repeatable>: Repeat Flag. If contains ';', it means it's enabled by default.", 
				" ",
				"/ DO NOT CHANGE/REMOVE THIS TRIGGER SCRIPT. THIS IS RESERVED ONLY. FOLLOW THIS FORMAT.", 
				"$0",
				"@Eraser", 
				"%"
			};
			// @formatter:on

			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("readme.txt"), "utf-8"))) {
				for (int i = 0; i < tutorialLines.length; i++) {
					writer.write(tutorialLines[i]);
					writer.newLine();
				}
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void addingComponents() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (ScriptEditor.this.input == null) {
					ScriptEditor.this.input = new ScriptInput();
					ScriptEditor.this.addMouseListener(ScriptEditor.this.input);
					ScriptEditor.this.addMouseMotionListener(ScriptEditor.this.input);
				}
				if (ScriptEditor.this.scriptToolbar == null) {
					ScriptEditor.this.scriptToolbar = new ScriptToolbar(ScriptEditor.this);
					ScriptEditor.this.scriptToolbar.addMouseListener(ScriptEditor.this.input);
					ScriptEditor.this.scriptToolbar.addMouseMotionListener(ScriptEditor.this.input);
					ScriptEditor.this.add(ScriptEditor.this.scriptToolbar, BorderLayout.NORTH);
					ScriptEditor.this.validate();
				}
				if (ScriptEditor.this.scriptViewer == null) {
					ScriptEditor.this.scriptViewer = new ScriptViewer(ScriptEditor.this);
					ScriptEditor.this.scriptViewer.addMouseListener(ScriptEditor.this.input);
					ScriptEditor.this.scriptViewer.addMouseMotionListener(ScriptEditor.this.input);
					ScriptEditor.this.add(ScriptEditor.this.scriptViewer, BorderLayout.WEST);
					ScriptEditor.this.validate();
				}
				if (ScriptEditor.this.scriptChanger == null) {
					ScriptEditor.this.scriptChanger = new ScriptChanger(ScriptEditor.this);
					ScriptEditor.this.scriptChanger.addMouseListener(ScriptEditor.this.input);
					ScriptEditor.this.scriptChanger.addMouseMotionListener(ScriptEditor.this.input);
					ScriptEditor.this.add(ScriptEditor.this.scriptChanger, BorderLayout.CENTER);
					ScriptEditor.this.validate();
				}
				ScriptEditor.this.revalidate();
				ScriptEditor.this.repaint();
			}
		});
	}

	public void resetComponents() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (ScriptEditor.this.scriptViewer != null) {
					ScriptEditor.this.scriptViewer.clearTriggerModel();
					ScriptEditor.this.scriptViewer.clearTriggers();
				}
				if (ScriptEditor.this.scriptChanger != null) {
					ScriptEditor.this.scriptChanger.clearTextFields();
				}
				ScriptEditor.this.scriptChanger.disableComponent();
				ScriptEditor.this.refresh();
			}
		});
	}

	public String getEditorChecksum() {
		return this.parent.getChecksum();
	}

	// (11/24/2014): This is where I load triggers at. This is completed, but may require
	// double-checking to be very sure.
	/**
	 * Loads the file script.
	 * <p>
	 * This method may require double-checking in the codes, just to be very sure that it is absolutely
	 * working as intended. Reason for this is that this method is used as a guideline for loading
	 * custom scripts into the game itself.
	 * 
	 * @param script
	 *            - Takes in a SCRIPT file object, which is the scripting file the game and the script
	 *            editor uses.
	 * @return Nothing.
	 */
	public void load(File script) {
		String format = script.getName();
		if (!format.endsWith(".script")) {
			JOptionPane.showMessageDialog(null, "Incorrect file format - Please open files ending with \".script\"");
			return;
		}
		System.out.println("Opened Location: " + script.getAbsolutePath());
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(script)))) {
			JList<Trigger> triggerList = this.scriptViewer.getTriggerList();
			DefaultListModel<Trigger> scriptTriggerListModel = (DefaultListModel<Trigger>) triggerList.getModel();
			scriptTriggerListModel.clear();

			JComboBox<Trigger> comboTriggerList = this.parent.properties.getTriggerList();
			DefaultComboBoxModel<Trigger> editorTriggerComboModel = (DefaultComboBoxModel<Trigger>) comboTriggerList
			    .getModel();
			editorTriggerComboModel.removeAllElements();

			Trigger trigger = new Trigger();
			trigger.setTriggerID((short) 0);
			trigger.setName("Eraser");
			editorTriggerComboModel.addElement(trigger);
			comboTriggerList.setSelectedIndex(0);

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
		this.scriptViewer.getTriggerList().clearSelection();
		this.scriptViewer.revalidate();
		this.scriptViewer.repaint();
		this.parent.revalidate();
		this.parent.repaint();
		super.revalidate();
		super.repaint();
	}

	public void refresh() {
		this.scriptToolbar.revalidate();
		this.scriptToolbar.repaint();
		this.scriptViewer.revalidate();
		this.scriptViewer.repaint();
		this.scriptChanger.revalidate();
		this.scriptChanger.repaint();
		this.parent.revalidate();
		this.parent.repaint();
		super.revalidate();
		super.repaint();
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

	/**
	 * There is no need to add Eraser into the triggers list file.
	 * 
	 * @param script
	 */
	public void save(File script) {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(script)))) {
			String checksum = this.getEditorChecksum();
			writer.write(checksum);
			writer.newLine();

			DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) this.scriptViewer.getTriggerList().getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Trigger t = model.get(i);
				writer.write("$" + Short.toString(t.getTriggerID()));
				writer.newLine();
				writer.write("@" + t.getName().replace(" ", "_"));
				writer.newLine();
				writer.write(t.getScript());
				writer.newLine();
				writer.write("%");
				writer.newLine();

				// Double blank lines for separation of triggers.
				writer.newLine();
				writer.newLine();
			}
			Debug.log("Saved Location: " + script.getAbsolutePath());
		}
		catch (IOException e) {
			Debug.error("Script editor failed to save script.", e);
		}
		super.revalidate();
		super.repaint();
	}
}
