package script;

import java.awt.Graphics2D;
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

import common.Debug;
import constants.ScriptTag;
import dialogue.Dialogue;
import entity.Player;
import level.Area;
import screen.Scene;
import utility.DialogueBuilder;

/**
 * A "script", or "event", is a unit of scripted actions in the game. Each unit of scripted actions
 * contains many different event actions, such as:
 * <ul>
 * <li>How many dialogues to display.
 * <li>How many movements a player or an NPC will follow.
 * <li>Affirmative and negative responses and different paths to choose
 * <li>And many more that are related to the scripted event.
 * </ul>
 * <p>
 * A script file will contain one or many "scripts".
 * 
 * @author tlee
 */
public class Script {
	private int triggerID;
	private ArrayList<Map.Entry<Integer, MovementData>> moves;
	private ArrayList<Map.Entry<Integer, MovementData>> affirmativeMoves;
	private ArrayList<Map.Entry<Integer, MovementData>> negativeMoves;
	private ArrayList<Map.Entry<Integer, Dialogue>> dialogues;
	private ArrayList<Map.Entry<Integer, Dialogue>> affirmativeDialogues;
	private ArrayList<Map.Entry<Integer, Dialogue>> negativeDialogues;
	private Map.Entry<Integer, Integer> remainingMoves;
	private int scriptIteration;
	private int affirmativeIteration;
	private int negativeIteration;
	private Boolean questionResponse;
	private boolean finished;
	private boolean repeat;
	private boolean enabled;
	private boolean hasRecentlyReset;

	/**
	 * Constructor of the Script object, which holds a single scripted event defined in the script file.
	 */
	public Script() {
		this.triggerID = 0;
		this.moves = new ArrayList<>();
		this.affirmativeMoves = new ArrayList<>();
		this.negativeMoves = new ArrayList<>();
		this.dialogues = new ArrayList<>();
		this.affirmativeDialogues = new ArrayList<>();
		this.negativeDialogues = new ArrayList<>();
		this.scriptIteration = 0;
		this.affirmativeIteration = 0;
		this.negativeIteration = 0;
		this.questionResponse = null;
		this.repeat = false;
		this.finished = false;
		this.enabled = true;
	}

	/**
	 * Deep copying of the given Script object.
	 * 
	 * @param s
	 */
	public Script(Script s) {
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

		this.scriptIteration = s.scriptIteration;
		this.affirmativeIteration = s.affirmativeIteration;
		this.negativeIteration = s.negativeIteration;
		this.questionResponse = s.questionResponse;
		this.repeat = s.repeat;
		this.finished = s.finished;
		this.enabled = s.enabled;
	}

	// --------------------------------------------------------------------------------------
	// Public methods.

	/**
	 * Returns the current scripted movement action depending on what type of response the player has
	 * given in the game.
	 * 
	 * @return
	 */
	public MovementData getIteratedMoves() {
		if (this.questionResponse == null) {
			for (Map.Entry<Integer, MovementData> entry : this.moves) {
				if (entry.getKey() == this.scriptIteration)
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

	/**
	 * Returns the current scripted dialogue depending on what type of response the player has given in
	 * the game.
	 * 
	 * @return
	 */
	public Dialogue getIteratedDialogues() {
		if (this.questionResponse == null) {
			for (Map.Entry<Integer, Dialogue> entry : this.dialogues) {
				if (entry.getKey() == this.scriptIteration) {
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

	/**
	 * Sets the script to execute a positive dialogue option route when the flag is set.
	 */
	public void setAffirmativeFlag() {
		this.questionResponse = Boolean.TRUE;
	}

	/**
	 * Sets the script to execute a negative dialogue option route when the flag is set.
	 */
	public void setNegativeFlag() {
		this.questionResponse = Boolean.FALSE;
	}

	/**
	 * Updates the script.
	 * 
	 * @param area
	 * @param entityX
	 * @param entityY
	 */
	public void tick(Area area, int entityX, int entityY) {
		MovementData moves = this.getIteratedMoves();
		Dialogue dialogue = this.getIteratedDialogues();

		if (dialogue == null) {
			area.getPlayer().keys.resetInputs();
			if (area.getPlayer().isLockedWalking())
				return;
			if (moves.hasNextMove()) {
				this.remainingMoves = moves.getNextMove();
				if (this.remainingMoves.getKey() != area.getPlayer().getFacing()) {
					area.getPlayer().setFacing(this.remainingMoves.getKey());
					return;
				}

				int steps = this.remainingMoves.getValue();
				if (steps >= 0) {
					if (steps == 0)
						area.getPlayer().setFacing(this.remainingMoves.getKey());
					else
						area.getPlayer().forceLockWalking();
					steps--;

					moves.updateCurrentMove(steps);
				}
				else {
					this.remainingMoves = moves.getNextMove();
					int direction = moves.getNextMoveDirection();
					if (direction != area.getPlayer().getFacing())
						area.getPlayer().setFacing(direction);
				}
			}
			else {
				try {
					if (!this.incrementIteration())
						this.finished = true;
				}
				catch (Exception e) {
					this.finished = true;
					return;
				}
			}
		}
		else {
			switch (dialogue.getDialogueType()) {
				case SPEECH:
					if (dialogue.isDialogueCompleted()) {
						if (dialogue.isScrolling()) {
							Player.unlockMovements();
							dialogue.tick();
							try {
								this.finished = !this.incrementIteration();
							}
							catch (Exception e) {
								this.finished = true;
								return;
							}
						}
						else {
							if (!dialogue.isShowingDialog()) {
								Player.unlockMovements();
								dialogue = null;
								try {
									this.finished = !this.incrementIteration();
								}
								catch (Exception e) {
									this.finished = true;
									return;
								}
							}
							else
								dialogue.tick();
						}
					}
					else if (dialogue.isReady()
					    && !(dialogue.isDialogueCompleted() && dialogue.isShowingDialog())) {
						Player.lockMovements();
						dialogue.tick();
					}
					break;
				case QUESTION:
					if (!dialogue.yesNoQuestionHasBeenAnswered()) {
						dialogue.tick();
						if (!Player.isMovementsLocked())
							Player.lockMovements();
						area.getPlayer().disableAutomaticMode();
					}
					if (dialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
						if (Player.isMovementsLocked())
							Player.unlockMovements();
						area.getPlayer().enableAutomaticMode();
						dialogue = null;
						try {
							this.finished = !this.incrementIteration();
						}
						catch (Exception e) {
							this.finished = true;
							return;
						}
						this.setAffirmativeFlag();
						this.finished = false;
					}
					else if (dialogue.getAnswerToSimpleQuestion() == Boolean.FALSE) {
						if (Player.isMovementsLocked())
							Player.unlockMovements();
						area.getPlayer().enableAutomaticMode();
						dialogue = null;
						try {
							this.finished = !this.incrementIteration();
						}
						catch (Exception e) {
							this.finished = true;
							return;
						}
						this.setNegativeFlag();
						this.finished = false;
					}
					break;
				case ALERT:
				default:
					break;
			}
		}
	}

	/**
	 * Renders the scripted dialogue textbox.
	 * 
	 * @param screen
	 * @param graphics
	 */
	public void render(Scene screen, Graphics2D graphics) {
		Dialogue dialogue = this.getIteratedDialogues();
		if (dialogue != null) {
			dialogue.render(screen, graphics);
		}
	}

	/**
	 * Sets the script to be repeatable.
	 */
	public void setRepeating() {
		this.repeat = true;
	}

	/**
	 * Stops the script from being repeatable.
	 */
	public void stopRepeating() {
		this.repeat = false;
	}

	/**
	 * @return True if the script is set to repeat whenever the player triggers the script. False, if
	 *         the script will fire off once in the game.
	 */
	public boolean isOnRepeat() {
		return this.repeat;
	}

	/**
	 * Resets the script back to its initial state. A reset history flag will be set when invoked.
	 */
	public void reset() {
		this.scriptIteration = 0;
		for (Map.Entry<Integer, MovementData> entry : this.moves) {
			entry.getValue().reset();
		}
		for (Map.Entry<Integer, MovementData> entry : this.affirmativeMoves) {
			entry.getValue().reset();
		}
		for (Map.Entry<Integer, MovementData> entry : this.negativeMoves) {
			entry.getValue().reset();
		}
		for (Map.Entry<Integer, Dialogue> entry : this.dialogues) {
			entry.getValue().resetDialogue();
		}
		for (Map.Entry<Integer, Dialogue> entry : this.affirmativeDialogues) {
			entry.getValue().resetDialogue();
		}
		for (Map.Entry<Integer, Dialogue> entry : this.negativeDialogues) {
			entry.getValue().resetDialogue();
		}
		this.finished = false;
		this.hasRecentlyReset = true;
	}

	/**
	 * Checks whether the reset history flag has been set. This flag is set only if the script has been
	 * reset to its initial state.
	 * 
	 * @return
	 */
	public boolean hasReset() {
		return this.hasRecentlyReset;
	}

	/**
	 * Clears the reset history flag. This flag is set only if the script has been reset to its initial
	 * state.
	 */
	public void clearReset() {
		this.hasRecentlyReset = false;
	}

	/**
	 * Checks whether the script has finished.
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return this.finished;
	}

	/**
	 * Sets the script's status to be finished.
	 */
	public void setCompleted() {
		this.finished = true;
	}

	/**
	 * Turns off the script.
	 */
	public void turnOffScript() {
		this.enabled = false;
	}

	/**
	 * Turns on the script.
	 */
	public void turnOnScript() {
		this.enabled = true;
	}

	/**
	 * Checks if the script is enabled.
	 * 
	 * @return
	 */
	public boolean isScriptEnabled() {
		return this.enabled;
	}

	/**
	 * Increments the current iterator based on the type of response we are giving in the game. If we
	 * are not giving an answer to a question, the current iterator will be incremented. If we are
	 * giving an affirmative response to a question, the affirmative dialogue iterator will be
	 * incremented. If we are giving a negative response to a question, the negative response dialogue
	 * iterator will be incremented.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean incrementIteration() throws Exception {
		if (this.questionResponse == null) {
			this.scriptIteration++;
			int size = this.moves.size() + this.dialogues.size();
			if (this.scriptIteration < size)
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

	// -------------------------------------------------------------------------------
	// Private methods

	private void resetResponseFlag() {
		this.questionResponse = null;
	}

	// -------------------------------------------------------------------------------
	// Static methods

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

	// -------------------------------------------------------------
	// Private static methods

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
					moves.writeOriginalMove(direction, steps);
				}
			}
			moves.reset();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new NumberFormatException("Incorrect script syntax from \"script.txt\"");
		}
	}
}