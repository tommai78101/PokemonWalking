package script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dialogue.Dialogue;
import utility.Debug;
import utility.DialogueBuilder;

public class Script {
	private enum ScriptTag {
		ScriptName("@"),
		Comment("/"),
		BeginScript("$"),
		PathData("^"),
		EndScript("%"),
		Speech("#"),
		Question("?"),
		Affirm("+"),
		Reject("-"),
		Confirm("["),
		Deny("]"),
		Repeat(";"),
		Repeatable(";");

		private String symbol;

		private ScriptTag(String sym) {
			this.symbol = sym;
		}

		/**
		 * Checks if the line starts with either the symbol representation, or the tag name.
		 * 
		 * @param line
		 * @return True if either the symbol or the tag name matches. False, if otherwise.
		 */
		public boolean beginsAt(String line) {
			if (line == null || line.isEmpty() || line.isBlank())
				return false;
			if (line.length() < this.name().length())
				return line.startsWith(this.symbol);
			else if (line.startsWith(this.symbol))
				return true;
			else
				return line.regionMatches(true, 0, this.name(), 0, this.name().length());
		}

		/**
		 * Replaces the first occurrence of the tag name with the equivalent symbol representation.
		 * <p>
		 * Otherwise, if the line already has the symbol representation, then it does nothing.
		 * 
		 * @param line
		 * @return The replaced line.
		 */
		public String replace(String line) {
			if (line.startsWith(this.symbol))
				return line;
			return line.replaceFirst(Pattern.quote(this.name()), Matcher.quoteReplacement(this.symbol));
		}
	}

	public int triggerID;
	public ArrayList<Map.Entry<Integer, MovementData>> moves;
	public ArrayList<Map.Entry<Integer, MovementData>> affirmativeMoves;
	public ArrayList<Map.Entry<Integer, MovementData>> negativeMoves;
	public ArrayList<Map.Entry<Integer, Dialogue>> dialogues;
	public ArrayList<Map.Entry<Integer, Dialogue>> affirmativeDialogues;
	public ArrayList<Map.Entry<Integer, Dialogue>> negativeDialogues;
	public int iteration;
	public int affirmativeIteration;
	public int negativeIteration;
	public Boolean questionResponse;
	public boolean repeat;

	public Script() {
		this.triggerID = 0;
		this.moves = new ArrayList<>();
		this.affirmativeMoves = new ArrayList<>();
		this.negativeMoves = new ArrayList<>();
		this.dialogues = new ArrayList<>();
		this.affirmativeDialogues = new ArrayList<>();
		this.negativeDialogues = new ArrayList<>();
		this.iteration = 0;
		this.affirmativeIteration = 0;
		this.negativeIteration = 0;
		this.questionResponse = null;
		this.repeat = false;
	}

	public Script(Script s) {
		// Deep copy
		this.triggerID = s.triggerID;

		this.moves = new ArrayList<>();
		for (Map.Entry<Integer, MovementData> e : s.moves)
			this.moves.add(Map.entry(e.getKey(), new MovementData(e.getValue())));

		this.affirmativeMoves = new ArrayList<>();
		for (Map.Entry<Integer, MovementData> e : s.affirmativeMoves)
			this.affirmativeMoves
				.add(Map.entry(e.getKey(), new MovementData(e.getValue())));

		this.negativeMoves = new ArrayList<>();
		for (Map.Entry<Integer, MovementData> e : s.negativeMoves)
			this.negativeMoves
				.add(Map.entry(e.getKey(), new MovementData(e.getValue())));

		this.dialogues = new ArrayList<>();
		for (Map.Entry<Integer, Dialogue> e : s.dialogues)
			this.dialogues
				.add(Map.entry(e.getKey(), new Dialogue(e.getValue())));

		this.affirmativeDialogues = new ArrayList<>();
		for (Map.Entry<Integer, Dialogue> e : s.affirmativeDialogues)
			this.affirmativeDialogues
				.add(Map.entry(e.getKey(), new Dialogue(e.getValue())));

		this.negativeDialogues = new ArrayList<>();
		for (Map.Entry<Integer, Dialogue> e : s.negativeDialogues)
			this.negativeDialogues
				.add(Map.entry(e.getKey(), new Dialogue(e.getValue())));

		this.iteration = s.iteration;
		this.affirmativeIteration = s.affirmativeIteration;
		this.negativeIteration = s.negativeIteration;
		this.questionResponse = s.questionResponse;
		this.repeat = s.repeat;
	}

	public MovementData getIteratedMoves() {
		if (this.questionResponse == null) {
			for (Map.Entry<Integer, MovementData> entry : this.moves) {
				if (entry.getKey() == this.iteration)
					return entry.getValue();
			}
		}
		else if (this.questionResponse == Boolean.TRUE) {
			for (Map.Entry<Integer, MovementData> entry : this.affirmativeMoves) {
				if (entry.getKey() == this.affirmativeIteration)
					return entry.getValue();
			}
		}
		else if (this.questionResponse == Boolean.FALSE) {
			for (Map.Entry<Integer, MovementData> entry : this.negativeMoves) {
				if (entry.getKey() == this.negativeIteration)
					return entry.getValue();
			}
		}
		return null;
	}

	public Dialogue getIteratedDialogues() {
		if (this.questionResponse == null) {
			for (Map.Entry<Integer, Dialogue> entry : this.dialogues) {
				if (entry.getKey() == this.iteration) {
					return entry.getValue();
				}
			}
		}
		else if (this.questionResponse == Boolean.TRUE) {
			for (Map.Entry<Integer, Dialogue> entry : this.affirmativeDialogues) {
				if (entry.getKey() == this.affirmativeIteration) {
					return entry.getValue();
				}
			}
		}
		else if (this.questionResponse == Boolean.FALSE) {
			for (Map.Entry<Integer, Dialogue> entry : this.negativeDialogues) {
				if (entry.getKey() == this.negativeIteration) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	public void setAffirmativeFlag() {
		this.questionResponse = Boolean.TRUE;
	}

	public void setNegativeFlag() {
		this.questionResponse = Boolean.FALSE;
	}

	private void resetResponseFlag() {
		this.questionResponse = null;
	}

	public boolean incrementIteration() throws Exception {
		if (this.questionResponse == null) {
			this.iteration++;
			int size = this.moves.size() + this.dialogues.size();
			if (this.iteration < size)
				return true;
			return false;
		}
		else if (this.questionResponse == Boolean.TRUE) {
			this.resetResponseFlag();
			this.affirmativeIteration++;
			int size = this.affirmativeMoves.size() + this.affirmativeDialogues.size();
			if (this.affirmativeIteration < size)
				return true;
			return false;
		}
		else { // FALSE
			this.resetResponseFlag();
			this.negativeIteration++;
			int size = this.negativeMoves.size() + this.negativeDialogues.size();
			if (this.negativeIteration < size)
				return true;
			return false;
		}
	}

	/**
	 * Loads triggers according to the script file.
	 * <p>
	 * The script file is a database of all triggers of a a certain map. All scripts within the file can
	 * only be triggered by that area.
	 * <p>
	 * Currently needs fixing and testing. Will be completed when all issues have been sorted out.
	 * 
	 * @param filename
	 *            - A String object of the file name of the SCRIPT file.
	 * @return An ArrayList<Script> object, containing all of the triggers and scripted events located
	 *         within the SCRIPT file.
	 * 
	 */
	public static List<Script> loadScript(String filename, boolean isModdedScript) {
		List<Script> result = new ArrayList<>();

		// Scripts must contain a trigger data for Area to use, and must contain
		// movements for Area to read.

		Script script = null;
		int iteration = 0;
		int affirmativeIteration = 0;
		int negativeIteration = 0;
		InputStream inputStream = null;

		// Check if the file is a modded script file, or a default script file.
		try {
			if (isModdedScript) {
				inputStream = new FileInputStream(new File(filename));
			}
			else {
				inputStream = Script.class.getClassLoader().getResourceAsStream(filename);
			}
		}
		catch (FileNotFoundException e) {
			Debug.error("Unable to locate " + (isModdedScript ? "modded" : "default") + " script input file.", e);
		}

		// Try-with-resource
		try (
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader)
		) {
			String line;
			while ((line = reader.readLine()) != null) {
				// Ignored tags
				if (ScriptTag.Comment.beginsAt(line) || ScriptTag.ScriptName.beginsAt(line) || line.startsWith(" ") || line.isEmpty())
					continue;

				// Start of script
				else if (ScriptTag.BeginScript.beginsAt(line)) {
					line = ScriptTag.BeginScript.replace(line);
					int triggerID = Integer.valueOf(line.substring(1).trim());
					if (triggerID > 0) {
						if (script == null)
							script = new Script();
						script.triggerID = triggerID;
					}
				}

				// Movement Data
				else if (ScriptTag.PathData.beginsAt(line)) {
					if (script != null) {
						MovementData moves = new MovementData();
						line = ScriptTag.PathData.replace(line);
						Script.append(moves, line.substring(1).trim().toCharArray());
						script.moves.add(Map.entry(iteration, moves));
						iteration++;
					}
				}

				// Script delimiter
				else if (ScriptTag.EndScript.beginsAt(line)) {
					if (script != null) {
						result.add(script);
						script = null;
						iteration = 0;
					}
				}

				// Speech dialogue
				else if (ScriptTag.Speech.beginsAt(line)) {
					line = ScriptTag.Speech.replace(line);
					Dialogue d = DialogueBuilder.createText(
						line.substring(1).trim().replace("_", " "),
						Dialogue.MAX_STRING_LENGTH, Dialogue.DialogueType.SPEECH, true
					);
					script.dialogues.add(Map.entry(iteration, d));
					iteration++;
				}

				// Question dialogue
				else if (ScriptTag.Question.beginsAt(line)) {
					line = ScriptTag.Question.replace(line);
					Dialogue d = DialogueBuilder.createText(
						line.substring(1).trim().replace("_", " "),
						Dialogue.MAX_STRING_LENGTH, Dialogue.DialogueType.QUESTION, true
					);
					script.dialogues.add(Map.entry(iteration, d));
					iteration++;
				}

				// Affirmative Response dialogue
				else if (ScriptTag.Affirm.beginsAt(line)) {
					line = ScriptTag.Affirm.replace(line);
					Dialogue d = DialogueBuilder.createText(
						line.substring(1).trim().replace("_", " "),
						Dialogue.MAX_STRING_LENGTH, Dialogue.DialogueType.SPEECH, true
					);
					script.affirmativeDialogues
						.add(Map.entry(affirmativeIteration, d));
					affirmativeIteration++;
				}

				// Negative Response dialogue
				else if (ScriptTag.Reject.beginsAt(line)) {
					line = ScriptTag.Reject.replace(line);
					Dialogue d = DialogueBuilder.createText(
						line.substring(1).trim().replace("_", " "),
						Dialogue.MAX_STRING_LENGTH, Dialogue.DialogueType.SPEECH, true
					);
					script.negativeDialogues
						.add(Map.entry(negativeIteration, d));
					negativeIteration++;
				}

				// Affirmative Response action
				else if (ScriptTag.Confirm.beginsAt(line)) {
					if (script != null) {
						MovementData moves = new MovementData();
						line = ScriptTag.Confirm.replace(line);
						Script.append(moves, line.substring(1).trim().toCharArray());
						script.affirmativeMoves
							.add(Map.entry(affirmativeIteration, moves));
						affirmativeIteration++;
					}
				}

				// Negative Response action
				else if (ScriptTag.Deny.beginsAt(line)) {
					if (script != null) {
						MovementData moves = new MovementData();
						line = ScriptTag.Deny.replace(line);
						Script.append(moves, line.substring(1).trim().toCharArray());
						script.negativeMoves
							.add(Map.entry(negativeIteration, moves));
						negativeIteration++;
					}
				}

				// Is a Repeating Trigger
				else if (ScriptTag.Repeat.beginsAt(line) || ScriptTag.Repeatable.beginsAt(line)) {
					if (script != null) {
						script.repeat = true;
					}
				}
			}
		}
		catch (Exception e) {
			Debug.error("Script loading error: ", e);
		}

		// Closing the input stream.
		try {
			inputStream.close();
		}
		catch (IOException e) {
			Debug.error("Unable to close input stream.", e);
		}
		return result;
	}

	private static void append(MovementData moves, char[] list) {
		int direction = -1;
		int steps = -1;

		try {
			for (int i = 0; i < list.length; i += 2) {
				char d = list[i];
				char s = list[i + 1];
				switch (d) {
					case 'U':
						direction = abstracts.Character.UP;
						break;
					case 'D':
						direction = abstracts.Character.DOWN;
						break;
					case 'L':
						direction = abstracts.Character.LEFT;
						break;
					case 'R':
						direction = abstracts.Character.RIGHT;
						break;
					default:
						direction = -1;
						break;
				}
				steps = Character.getNumericValue(s);
				if (direction != -1 && steps != -1 && steps != -2 && (steps <= 9 && steps >= 0)) {
					Map.Entry<Integer, Integer> entry = Map.entry(direction, steps);
					moves.moves.add(entry);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new NumberFormatException("Incorrect script syntax from \"script.txt\"");
		}
	}

	// -------------------------------------------------------------
	// Static Methods
	// -------------------------------------------------------------

	/**
	 * Load all default scripts.
	 */
	public static List<Script> loadDefaultScripts() {
		return Script.loadScript("art/script/scripts.txt", false);
	}

	/**
	 * Load all modded scripts.
	 */
	public static List<Script> loadModdedScripts() {
		List<Script> results = null;
		File modDirectory = new File("mod");
		if (modDirectory.exists() && modDirectory.isDirectory()) {
			String[] folders = modDirectory.list();
			LOOP:
			for (int i = 0; i < folders.length; i++) {
				File directory = new File(modDirectory.getPath() + File.separator + folders[i]);
				if (directory.exists() && directory.isDirectory() && directory.getName().equals("script")) {
					String[] scripts = directory.list();
					for (int j = 0; j < scripts.length; j++) {
						String filePath = directory.getPath() + File.separator + scripts[j];
						if (filePath.endsWith(".script")) {
							if (results == null)
								results = Script.loadScript(filePath, true);
							else
								results.addAll(Script.loadScript(filePath, true));
						}
					}
					break LOOP;
				}
			}
		}
		return results;
	}

	/**
	 * Better way to load all modded scripts. Must return a non-null list of scripts.
	 * <p>
	 * If null is returned, OverWorld class will fail to instantiate.
	 * 
	 * @return A list of Script objects.
	 */
	public static List<Script> loadModdedScriptsNew() {
		List<Script> results = new ArrayList<>();
		File modDirectory = new File("mod" + File.separator + "script");
		if (modDirectory.exists() && modDirectory.isDirectory()) {
			String[] scripts = modDirectory.list();
			for (int i = 0; i < scripts.length; i++) {
				String scriptFile = scripts[i];
				if (scriptFile.endsWith(".script")) {
					results.addAll(Script.loadScript(modDirectory.getPath() + File.separator + scriptFile, true));
				}
			}
		}
		return results;
	}
}