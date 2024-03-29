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
import java.io.FileWriter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import common.Debug;
import common.ScriptTagsResult;
import editor.FileControl;
import editor.LevelEditor;
import editor.Trigger;
import enums.ScriptJsonTags;
import enums.ScriptTags;

public class ScriptEditor extends JFrame {
	private static final long serialVersionUID = 1L;

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
	private String fileExtension = "script";
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
		this.setFileExtension("script");

		// Generates a scripting tutorial upon loading script editor. This generated
		// file will not be persistent and the script editor will not overwrite if the
		// file exists.
		this.generateScriptingTutorial();

		ScriptEditor.lastSavedDirectory = FileControl.lastSavedDirectory;
		ToolTipManager.sharedInstance().setInitialDelay(1);

		// Auto-opens the script file if it exists within the same directory as the area map file. This is
		// always done last.
		SwingUtilities.invokeLater(() -> {
			String[] filePaths = ScriptEditor.lastSavedDirectory.list((directory, filename) -> {
				boolean hasExtension = filename.toLowerCase().endsWith(".script") || filename.toLowerCase().endsWith(".json");
				return filename.contains(this.parent.getMapAreaName()) && hasExtension;
			});

			boolean success = false;
			String lastKnownFile = "";
			try {
				// Try each one.
				for (String file : filePaths) {
					lastKnownFile = file;
					if (success = this.load(new File(ScriptEditor.lastSavedDirectory, file)))
						break;
				}
			}
			catch (Exception e) {
				// Do nothing.
				Debug.error("Unable to load script file: " + lastKnownFile, e);
			}

			if (!success) {
				this.scriptToolbar.makeNewScript();
			}
			else {
				this.setModifiedFlag(false);
				// Legacy file format support.
				if (lastKnownFile.endsWith(".script"))
					this.setScriptName(lastKnownFile.substring(0, (lastKnownFile.length() - ".script".length())));
				else
					this.setScriptName(lastKnownFile.substring(0, (lastKnownFile.length() - ".json".length())));
				this.setEditorTitle();
				this.scriptChanger.updateComponent();
			}
		});
	}

	public void addingComponents() {
		SwingUtilities.invokeLater(() -> {
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
		});
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
		if (file.exists())
			return;

		// There are many different ways you can do to write data to files. This is one
		// of them.
		// @formatter:off
		String[] tutorialLines = {
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
			"/ $ <BeginScript>: Start of script. Always appear at beginning of script. Range [1~65535]",
			"/ & <NpcScript>: Start of NPC script. Appears at the beginning of script. Range [1~255]",
			"/ % <EndScript>: Script delimiter. Always appear at end of script.",
			"/ # <Speech>: Speech Dialogue.",
			"/ ? <Question>: Question Dialogue.",
			"/ + <Affirm>: Affirmative dialogue.",
			"/ - <Reject>: Negative dialogue",
			"/ [ <Confirm>: Affirmative Action",
			"/ ] <Deny>: Negative Action",
			"/ ; <Repeat, Repeatable>: Repeat Flag. If contains ';', it means it's enabled by default.",
			" ",
			"/ Example Legacy Format:",
			"$0",
			"@Eraser",
			"%",
			" ",
			"/ Example JSON Format:",
			"{\r\n"
			+ "   \"data\": [\r\n"
			+ "      {\r\n"
			+ "         \"BEGIN\": \"1\",\r\n"
			+ "         \"CONTENT\": [\r\n"
			+ "            {\"1\": \"#First speech begins.\"},\r\n"
			+ "            {\"2\": \"?First question?\"},\r\n"
			+ "            {\"3\": \"#Second speech begins.\"},\r\n"
			+ "            {\"4\": \"#Explaining stuffs.\"},\r\n"
			+ "            {\"5\": \"#Ending first trigger.\"}\r\n"
			+ "         ],\r\n"
			+ "         \"NAME\": \"Sample trigger #1\"\r\n"
			+ "      },\r\n"
			+ "      {\r\n"
			+ "         \"NPC\": \"2\",\r\n"
			+ "         \"CONTENT\": [\r\n"
			+ "            {\"1\": \"#First speech.\"},\r\n"
			+ "            {\"2\": \"#NPC talks second dialogue.\"},\r\n"
			+ "            {\"3\": \"#Nothing else.\"},\r\n"
			+ "            {\"4\": \"#Ending NPC speech.\"}\r\n"
			+ "         ],\r\n"
			+ "         \"NAME\": \"Sample NPC trigger #2\"\r\n"
			+ "      }\r\n"
			+ "   ],\r\n"
			+ "   \"CHKSUM\": \"ce42c3f6a6f16392\"\r\n"
			+ "}"
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

	public String getEditorChecksum() {
		return this.parent.getChecksum();
	}

	public String getFileExtension() {
		return this.fileExtension;
	}

	public LevelEditor getLevelEditorParent() {
		return this.parent;
	}

	public String getScriptName() {
		return this.scriptName;
	}

	public boolean isBeingModified() {
		return this.modifiedFlag;
	}

	// (11/24/2014): This is where I load triggers at. This is completed, but may require
	// double-checking to be very sure.
	/**
	 * Loads the file script that is formatted as JSON. Script files with file extensions, .SCRIPT and
	 * .JSON, are both accepted.
	 * <p>
	 * This method may require double-checking in the codes, just to be very sure that it is absolutely
	 * working as intended. Reason for this is that this method is used as a guideline for loading
	 * custom scripts into the game itself.
	 *
	 * @param script
	 *            - Takes in a SCRIPT file object, which is the scripting file the game and the script
	 *            editor uses.
	 * @return True, if the file successfully loads. False, if otherwise.
	 */
	public boolean load(File script) {
		Debug.info("Opened Location: " + script.getAbsolutePath());
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(script)))) {
			JSONTokener tokener = new JSONTokener(reader);
			JSONObject scriptJson = new JSONObject(tokener);

			// First, check if the TDIC (trigger data identification checksum) matches our level editor's
			// checksum.
			final String checksum = scriptJson.optString(ScriptJsonTags.CHECKSUM.getKey());
			if (checksum.isEmpty() || !this.getEditorChecksum().equals(checksum)) {
				// Checksum does not match. We quickly return false. This is not the script we are looking for.
				return false;
			}

			JList<Trigger> triggerList = this.scriptViewer.getTriggerList();
			DefaultListModel<Trigger> scriptTriggerListModel = (DefaultListModel<Trigger>) triggerList.getModel();
			scriptTriggerListModel.clear();

			JComboBox<Trigger> comboTriggerList = this.parent.properties.getTriggerList();
			DefaultComboBoxModel<Trigger> editorTriggerComboModel = (DefaultComboBoxModel<Trigger>) comboTriggerList
				.getModel();
			editorTriggerComboModel.removeAllElements();

			Trigger trigger = new Trigger();
			trigger.setTriggerID((short) 0);
			trigger.setNpcTriggerID(Trigger.NPC_TRIGGER_ID_NONE);
			trigger.setNpcTrigger(false);
			trigger.setName("Eraser");
			editorTriggerComboModel.addElement(trigger);
			comboTriggerList.setSelectedIndex(0);

			// Parse the main script JSON's data.
			JSONArray data = scriptJson.getJSONArray(ScriptJsonTags.DATA.getKey());
			int length = data.length();
			for (int i = 0; i < length; i++) {
				JSONObject element = data.getJSONObject(i);
				trigger = new Trigger();

				// Determine if it's a trigger script or NPC script. It cannot be anything else.
				String idType = element.optString(ScriptJsonTags.TRIGGER_TYPE.getKey());
				if (idType.equals(ScriptJsonTags.TRIGGER_TYPE_NPC.getKey())) {
					// NPC trigger script
					trigger.setNpcTrigger(true);
					trigger.setNpcTriggerID(Short.parseShort(element.getString(ScriptJsonTags.TRIGGER_ID.getKey())));
				}
				else {
					// Scene trigger script
					trigger.setNpcTrigger(false);
					trigger.setTriggerID(Short.parseShort(element.getString(ScriptJsonTags.TRIGGER_ID.getKey())));
				}

				// Add name
				trigger.setName(element.getString(ScriptJsonTags.NAME.getKey()));

				// Add checksum from the main script JSON.
				trigger.setChecksum(checksum);

				// Add script contents
				JSONArray dataContents = element.getJSONArray(ScriptJsonTags.SEQUENCE.getKey());
				StringBuilder builder = new StringBuilder();
				for (int cIndex = 0; cIndex < dataContents.length(); cIndex++) {
					// The JSON array is semantically ordered, therefore there is no concern for the script's sequence
					// order.
					JSONObject content = dataContents.getJSONObject(cIndex);
					ScriptTags tag = ScriptTags.valueOf(content.getString(ScriptJsonTags.TYPE.getKey()));
					builder.append(tag.getSymbol());
					builder.append(content.getString(ScriptJsonTags.CONTENT.getKey())).append("\n");
				}
				trigger.setScript(builder.toString());

				// Finally, add trigger to list models
				scriptTriggerListModel.addElement(trigger);
				editorTriggerComboModel.addElement(trigger);
			}
		}
		catch (JSONException e) {
			// Expected an error. Return false.
			return false;
		}
		catch (IOException e) {
			Debug.error("Unable to open file.", e);
			return false;
		}
		var triggerList = this.scriptViewer.getTriggerList();
		triggerList.clearSelection();
		if (triggerList.getModel().getSize() > 0)
			triggerList.setSelectedIndex(0);

		this.scriptViewer.revalidate();
		this.scriptViewer.repaint();
		this.parent.revalidate();
		this.parent.repaint();
		super.revalidate();
		super.repaint();

		return true;
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
	 * @deprecated We will be removing the old custom way of loading trigger scripts, and will be
	 *             transitioning to use JSON formatted scripts instead.
	 */
	@Deprecated(since = "v0.10.2", forRemoval = true)
	public void load_legacy(File script) {
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
			trigger.setNpcTriggerID(Trigger.NPC_TRIGGER_ID_NONE);
			trigger.setName("Eraser");
			editorTriggerComboModel.addElement(trigger);
			comboTriggerList.setSelectedIndex(0);

			String line = null;
			trigger = null;
			String checksum = null;
			StringBuilder builder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				if (ScriptTags.BeginScript.beginsAt(line)) {
					trigger = new Trigger();
					trigger.setTriggerID(Short.parseShort(ScriptTags.BeginScript.removeScriptTag(line)));
				}
				else if (ScriptTags.NpcScript.beginsAt(line)) {
					trigger = new Trigger();
					trigger.setNpcTriggerID(Short.parseShort(ScriptTags.NpcScript.removeScriptTag(line)));
				}
				else if (ScriptTags.ScriptName.beginsAt(line)) {
					line = line.replaceAll("_", " ");
					trigger.setName(ScriptTags.ScriptName.removeScriptTag(line));
				}
				else if (ScriptTags.EndScript.beginsAt(line)) {
					trigger.setScript(builder.toString());
					trigger.setChecksum(checksum);
					scriptTriggerListModel.addElement(trigger);
					editorTriggerComboModel.addElement(trigger);
					builder.setLength(0);
				}
				else if (ScriptTags.Checksum.beginsAt(line)) {
					line = ScriptTags.Checksum.removeScriptTag(line);
					checksum = line;
				}
				else if (ScriptTags.Speech.beginsAt(line) || ScriptTags.Question.beginsAt(line) || ScriptTags.Affirm.beginsAt(line) || ScriptTags.Reject.beginsAt(line) || ScriptTags.Confirm.beginsAt(line) || ScriptTags.Cancel.beginsAt(line)) {
					builder.append(line).append("\n");
				}
				else {
					// Ignore lines.
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		var triggerList = this.scriptViewer.getTriggerList();
		triggerList.clearSelection();
		if (triggerList.getModel().getSize() > 0)
			triggerList.setSelectedIndex(0);

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

	public void resetComponents() {
		SwingUtilities.invokeLater(() -> {
			if (ScriptEditor.this.scriptViewer != null) {
				ScriptEditor.this.scriptViewer.clearTriggerModel();
				ScriptEditor.this.scriptViewer.clearTriggers();
			}
			if (ScriptEditor.this.scriptChanger != null) {
				ScriptEditor.this.scriptChanger.clearTextFields();
			}
			ScriptEditor.this.scriptChanger.updateComponent();
			ScriptEditor.this.refresh();
		});
	}

	/**
	 * There is no need to add Eraser into the triggers list file.
	 *
	 * @param script
	 */
	public void save(File script) {
		// FileWriter(File, false) will explicitly clear the target file first before writing text.
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(script, false))) {
			JSONObject scriptJson = new JSONObject();

			// Write checksum to main JSON script.
			final String checksum = this.getEditorChecksum();
			scriptJson.put(ScriptJsonTags.CHECKSUM.getKey(), checksum);

			// Write all trigger scripts to main JSON script as data.
			JSONArray data = new JSONArray();
			DefaultListModel<Trigger> model = (DefaultListModel<Trigger>) this.scriptViewer.getTriggerList().getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Trigger t = model.get(i);
				JSONObject element = new JSONObject();

				if (t.isNpcTrigger() && !t.isEraser()) {
					element.put(ScriptJsonTags.TRIGGER_TYPE.getKey(), ScriptJsonTags.TRIGGER_TYPE_NPC.getKey());
					element.put(ScriptJsonTags.TRIGGER_ID.getKey(), Short.toString(t.getNpcTriggerID()));
				}
				else {
					element.put(ScriptJsonTags.TRIGGER_TYPE.getKey(), ScriptJsonTags.TRIGGER_TYPE_SCENE.getKey());
					element.put(ScriptJsonTags.TRIGGER_ID.getKey(), Short.toString(t.getTriggerID()));
				}
				element.put(ScriptJsonTags.NAME.getKey(), t.getName());

				// Write script contents cleanly
				String[] contents = t.getScript().split("\n");
				JSONArray orderedList = new JSONArray();
				for (int cIndex = 0; cIndex < contents.length; cIndex++) {
					String content = contents[cIndex];
					if (content == null || content.isBlank())
						continue;
					JSONObject order = new JSONObject();
					ScriptTagsResult result = ScriptTags.determineType(content);
					// Only for human-readability.
					order.put(ScriptJsonTags.ARRAY_INDEX.getKey(), cIndex + 1);
					order.put(ScriptJsonTags.TYPE.getKey(), result.getType().name());
					order.put(ScriptJsonTags.CONTENT.getKey(), result.getContent());
					orderedList.put(order);
				}
				element.put(ScriptJsonTags.SEQUENCE.getKey(), orderedList);

				// Write trigger element to main script data.
				data.put(element);
			}
			scriptJson.put(ScriptJsonTags.DATA.getKey(), data);

			// Write to file
			scriptJson.write(writer, 3, 0);

			Debug.warn("Saved Location: " + script.getAbsolutePath());
		}
		catch (IOException e) {
			Debug.error("Script editor failed to save script.", e);
		}
		super.revalidate();
		super.repaint();
	}

	public void setEditorTitle() {
		this.setTitle(ScriptEditor.TITLE + " - " + this.getScriptName() + "." + this.fileExtension);
	}

	public void setEditorTitle(String extension) {
		this.setTitle(ScriptEditor.TITLE + " - " + this.getScriptName() + "." + extension);
	}

	public void setEditorTitle(String scriptName, String extension) {
		this.setTitle(ScriptEditor.TITLE + " - " + scriptName + "." + extension);
	}

	public void setFileExtension(String extension) {
		this.fileExtension = extension;
	}

	public void setModifiedFlag(boolean value) {
		if (!value) {
			String str = this.getTitle();
			if (str.endsWith("*"))
				this.setEditorTitle(this.fileExtension);
		}
		else {
			String str = this.getTitle();
			if (!str.endsWith("*"))
				this.setTitle(str + "*");
		}
		this.modifiedFlag = value;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
}
