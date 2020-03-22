package script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import dialogue.Dialogue;
import entity.Player;
import utility.DialogueBuilder;

public class Script {
	public int triggerID;
	public ArrayList<Map.Entry<Integer, Movement>> moves;
	public ArrayList<Map.Entry<Integer, Movement>> affirmativeMoves;
	public ArrayList<Map.Entry<Integer, Movement>> negativeMoves;
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
		for (Map.Entry<Integer, Movement> e : s.moves)
			this.moves.add(new AbstractMap.SimpleEntry<>(e.getKey(), new Movement(e.getValue())));

		this.affirmativeMoves = new ArrayList<>();
		for (Map.Entry<Integer, Movement> e : s.affirmativeMoves)
			this.affirmativeMoves
					.add(new AbstractMap.SimpleEntry<>(e.getKey(), new Movement(e.getValue())));

		this.negativeMoves = new ArrayList<>();
		for (Map.Entry<Integer, Movement> e : s.negativeMoves)
			this.negativeMoves
					.add(new AbstractMap.SimpleEntry<>(e.getKey(), new Movement(e.getValue())));

		this.dialogues = new ArrayList<>();
		for (Map.Entry<Integer, Dialogue> e : s.dialogues)
			this.dialogues
					.add(new AbstractMap.SimpleEntry<>(e.getKey(), new Dialogue(e.getValue())));

		this.affirmativeDialogues = new ArrayList<>();
		for (Map.Entry<Integer, Dialogue> e : s.affirmativeDialogues)
			this.affirmativeDialogues
					.add(new AbstractMap.SimpleEntry<>(e.getKey(), new Dialogue(e.getValue())));

		this.negativeDialogues = new ArrayList<>();
		for (Map.Entry<Integer, Dialogue> e : s.negativeDialogues)
			this.negativeDialogues
					.add(new AbstractMap.SimpleEntry<>(e.getKey(), new Dialogue(e.getValue())));

		this.iteration = s.iteration;
		this.affirmativeIteration = s.affirmativeIteration;
		this.negativeIteration = s.negativeIteration;
		this.questionResponse = s.questionResponse;
		this.repeat = s.repeat;
	}

	public Movement getIteratedMoves() {
		if (this.questionResponse == null) {
			for (Map.Entry<Integer, Movement> entry : this.moves) {
				if (entry.getKey() == this.iteration)
					return entry.getValue();
			}
		} else if (this.questionResponse == Boolean.TRUE) {
			for (Map.Entry<Integer, Movement> entry : this.affirmativeMoves) {
				if (entry.getKey() == this.affirmativeIteration)
					return entry.getValue();
			}
		} else if (this.questionResponse == Boolean.FALSE) {
			for (Map.Entry<Integer, Movement> entry : this.negativeMoves) {
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
		} else if (this.questionResponse == Boolean.TRUE) {
			for (Map.Entry<Integer, Dialogue> entry : this.affirmativeDialogues) {
				if (entry.getKey() == this.affirmativeIteration) {
					return entry.getValue();
				}
			}
		} else if (this.questionResponse == Boolean.FALSE) {
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
		} else if (this.questionResponse == Boolean.TRUE) {
			this.resetResponseFlag();
			this.affirmativeIteration++;
			int size = this.affirmativeMoves.size() + this.affirmativeDialogues.size();
			if (this.affirmativeIteration < size)
				return true;
			return false;
		} else { // FALSE
			this.resetResponseFlag();
			this.negativeIteration++;
			int size = this.negativeMoves.size() + this.negativeDialogues.size();
			if (this.negativeIteration < size)
				return true;
			return false;
		}
	}

	/**
	 * <p>
	 * Loads triggers according to the script file.
	 * </p>
	 * 
	 * <p>
	 * The script file is a database of all triggers of a a certain map. All scripts
	 * within the file can only be triggered by that area.
	 * </p>
	 * 
	 * <p>
	 * Currently needs fixing and testing. Will be completed when all issues have
	 * been sorted out.
	 * </p>
	 * 
	 * @param filename - A String object of the file name of the SCRIPT file.
	 * @return An ArrayList<Script> object, containing all of the triggers and
	 *         scripted events located within the SCRIPT file.
	 * 
	 */
	public static ArrayList<Script> loadScript(String filename, boolean isModdedScript) {
		ArrayList<Script> result = new ArrayList<>();

		// Scripts must contain a trigger data for Area to use, and must contain
		// movements for Area to read.

		Script script = null;
		int iteration = 0;
		int affirmativeIteration = 0;
		int negativeIteration = 0;
		BufferedReader reader = null;
		try {
			if (isModdedScript) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
			} else {
				reader = new BufferedReader(
						new InputStreamReader(Script.class.getClassLoader().getResourceAsStream(filename)));
			}
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("/") || line.startsWith("@") || line.isEmpty()) // Ignored tags
					continue;
				else if (line.startsWith("$")) { // Start of script
					int triggerID = Integer.valueOf(line.substring(1));
					if (triggerID > 0) {
						if (script == null)
							script = new Script();
						script.triggerID = triggerID;
					}

				} else if (line.startsWith("^")) { // Movement
					if (script != null) {
						Movement moves = new Movement();
						append(moves, line.substring(1).toCharArray());
						script.moves.add(new AbstractMap.SimpleEntry<>(iteration, moves));
						iteration++;
					}
				} else if (line.startsWith("%")) { // Script delimiter
					if (script != null) {
						result.add(script);
						script = null;
						iteration = 0;
					}
				} else if (line.startsWith("#")) { // speech dialogue
					Dialogue d = DialogueBuilder.createText(line.substring(1).replace("_", " "),
							Dialogue.MAX_STRING_LENGTH, Dialogue.DIALOGUE_SPEECH, true);
					script.dialogues.add(new AbstractMap.SimpleEntry<>(iteration, d));
					iteration++;
				} else if (line.startsWith("?")) { // question dialogue
					Dialogue d = DialogueBuilder.createText(line.substring(1).replace("_", " "),
							Dialogue.MAX_STRING_LENGTH, Dialogue.DIALOGUE_QUESTION, true);
					script.dialogues.add(new AbstractMap.SimpleEntry<>(iteration, d));
					iteration++;
				} else if (line.startsWith("+")) { // affirmative dialogue
					Dialogue d = DialogueBuilder.createText(line.substring(1).replace("_", " "),
							Dialogue.MAX_STRING_LENGTH, Dialogue.DIALOGUE_SPEECH, true);
					script.affirmativeDialogues
							.add(new AbstractMap.SimpleEntry<>(affirmativeIteration, d));
					affirmativeIteration++;
				} else if (line.startsWith("-")) { // negative dialogue
					Dialogue d = DialogueBuilder.createText(line.substring(1).replace("_", " "),
							Dialogue.MAX_STRING_LENGTH, Dialogue.DIALOGUE_SPEECH, true);
					script.negativeDialogues
							.add(new AbstractMap.SimpleEntry<>(negativeIteration, d));
					negativeIteration++;
				} else if (line.startsWith("[")) { // affirmative action
					if (script != null) {
						Movement moves = new Movement();
						append(moves, line.substring(1).toCharArray());
						script.affirmativeMoves
								.add(new AbstractMap.SimpleEntry<>(affirmativeIteration, moves));
						affirmativeIteration++;
					}
				} else if (line.startsWith("]")) { // negative action
					if (script != null) {
						Movement moves = new Movement();
						append(moves, line.substring(1).toCharArray());
						script.negativeMoves
								.add(new AbstractMap.SimpleEntry<>(negativeIteration, moves));
						negativeIteration++;
					}
				} else if (line.startsWith(";")) {
					if (script != null) {
						script.repeat = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static void append(Movement moves, char[] list) {
		int direction = -1;
		int steps = -1;

		try {
			for (int i = 0; i < list.length; i += 2) {
				char d = list[i];
				char s = list[i + 1];
				switch (d) {
				case 'U':
					direction = Player.UP;
					break;
				case 'D':
					direction = Player.DOWN;
					break;
				case 'L':
					direction = Player.LEFT;
					break;
				case 'R':
					direction = Player.RIGHT;
					break;
				default:
					direction = -1;
					break;
				}
				steps = Character.getNumericValue(s);
				if (direction != -1 && steps != -1 && steps != -2 && (steps <= 9 && steps >= 0)) {
					Map.Entry<Integer, Integer> entry = new AbstractMap.SimpleEntry<>(direction, steps);
					moves.moves.add(entry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new NumberFormatException("Incorrect script syntax from \"script.txt\"");
		}
	}

	/**
	 * Load all default scripts.
	 */
	public static ArrayList<Script> loadDefaultScripts() {
		return Script.loadScript("art/script/scripts.txt", false);
	}

	/**
	 * Load all modded scripts.
	 */
	public static ArrayList<Script> loadModdedScripts() {
		ArrayList<Script> results = null;
		File modDirectory = new File("mod");
		if (modDirectory.exists() && modDirectory.isDirectory()) {
			String[] folders = modDirectory.list();
			LOOP: for (int i = 0; i < folders.length; i++) {
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
}