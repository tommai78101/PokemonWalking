package script;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import abstracts.Entity;
import dialogue.NewDialogue;

public class Script {
	public int triggerID;
	public ArrayList<Map.Entry<Integer, Movement>> moves;
	public ArrayList<Map.Entry<Integer, Movement>> affirmativeMoves;
	public ArrayList<Map.Entry<Integer, Movement>> negativeMoves;
	public ArrayList<Map.Entry<Integer, NewDialogue>> dialogues;
	public ArrayList<Map.Entry<Integer, NewDialogue>> affirmativeDialogues;
	public ArrayList<Map.Entry<Integer, NewDialogue>> negativeDialogues;
	public int iteration;
	public int affirmativeIteration;
	public int negativeIteration;
	public Boolean questionResponse;
	
	public Script() {
		this.triggerID = 0;
		this.moves = new ArrayList<Map.Entry<Integer, Movement>>();
		this.affirmativeMoves = new ArrayList<Map.Entry<Integer, Movement>>();
		this.negativeMoves = new ArrayList<Map.Entry<Integer, Movement>>();
		this.dialogues = new ArrayList<Map.Entry<Integer, NewDialogue>>();
		this.affirmativeDialogues = new ArrayList<Map.Entry<Integer, NewDialogue>>();
		this.negativeDialogues = new ArrayList<Map.Entry<Integer, NewDialogue>>();
		this.iteration = 0;
		this.affirmativeIteration = 0;
		this.negativeIteration = 0;
		this.questionResponse = null;
	}
	
	public Movement getIteratedMoves() {
		if (this.questionResponse == null) {
			for (Map.Entry<Integer, Movement> entry : this.moves) {
				if (entry.getKey() == this.iteration)
					return entry.getValue();
			}
		}
		else if (this.questionResponse == Boolean.TRUE) {
			for (Map.Entry<Integer, Movement> entry : this.affirmativeMoves) {
				if (entry.getKey() == this.affirmativeIteration)
					return entry.getValue();
			}
		}
		else if (this.questionResponse == Boolean.FALSE) {
			for (Map.Entry<Integer, Movement> entry : this.negativeMoves) {
				if (entry.getKey() == this.negativeIteration)
					return entry.getValue();
			}
		}
		return null;
	}
	
	public NewDialogue getIteratedDialogues() {
		if (this.questionResponse == null) {
			for (Map.Entry<Integer, NewDialogue> entry : this.dialogues) {
				if (entry.getKey() == this.iteration) {
					return entry.getValue();
				}
			}
		}
		else if (this.questionResponse == Boolean.TRUE) {
			for (Map.Entry<Integer, NewDialogue> entry : this.affirmativeDialogues) {
				if (entry.getKey() == this.affirmativeIteration) {
					return entry.getValue();
				}
			}
		}
		else if (this.questionResponse == Boolean.FALSE) {
			for (Map.Entry<Integer, NewDialogue> entry : this.negativeDialogues) {
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
		else {  //FALSE
			this.resetResponseFlag();
			this.negativeIteration++;
			int size = this.negativeMoves.size() + this.negativeDialogues.size();
			if (this.negativeIteration < size)
				return true;
			return false;
		}
	}
	
	public static ArrayList<Script> loadScript(String filename) {
		ArrayList<Script> result = new ArrayList<Script>();
		
		// Scripts must contain a trigger data for Area to use, and must contain movements for Area to read.
		
		Script script = null;
		int iteration = 0;
		int affirmativeIteration = 0;
		int negativeIteration = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Script.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("/") || line.startsWith("@")) // Ignored tags
					continue;
				else if (line.startsWith("$")) { // Start of script
					int triggerID = Integer.valueOf(line.substring(1));
					if (triggerID > 0) {
						if (script == null)
							script = new Script();
						script.triggerID = triggerID;
					}
					
				}
				else if (line.startsWith("^")) { // Movement
					if (script != null) {
						Movement moves = new Movement();
						append(moves, line.substring(1).toCharArray());
						script.moves.add(new AbstractMap.SimpleEntry<Integer, Movement>(iteration, moves));
						iteration++;
					}
				}
				else if (line.startsWith("%")) { // Script delimiter
					if (script != null) {
						result.add(script);
						script = null;
						iteration = 0;
					}
				}
				else if (line.startsWith("#")) { // speech dialogue
					NewDialogue d = NewDialogue.createText(line.substring(1).replace("_", " "), NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, true);
					script.dialogues.add(new AbstractMap.SimpleEntry<Integer, NewDialogue>(iteration, d));
					iteration++;
				}
				else if (line.startsWith("?")) { // question dialogue
					NewDialogue d = NewDialogue.createText(line.substring(1).replace("_", " "), NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_QUESTION, true);
					script.dialogues.add(new AbstractMap.SimpleEntry<Integer, NewDialogue>(iteration, d));
					iteration++;
				}
				else if (line.startsWith("+")) { // affirmative dialogue
					NewDialogue d = NewDialogue.createText(line.substring(1).replace("_", " "), NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, true);
					script.affirmativeDialogues.add(new AbstractMap.SimpleEntry<Integer, NewDialogue>(affirmativeIteration, d));
					affirmativeIteration++;
				}
				else if (line.startsWith("-")) { // negative dialogue
					NewDialogue d = NewDialogue.createText(line.substring(1).replace("_", " "), NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, true);
					script.negativeDialogues.add(new AbstractMap.SimpleEntry<Integer, NewDialogue>(negativeIteration, d));
					negativeIteration++;
				}
				else if (line.startsWith("[")) { // affirmative action
					if (script != null) {
						Movement moves = new Movement();
						append(moves, line.substring(1).toCharArray());
						script.affirmativeMoves.add(new AbstractMap.SimpleEntry<Integer, Movement>(affirmativeIteration, moves));
						affirmativeIteration++;
					}
				}
				else if (line.startsWith("]")) { // negative action
					if (script != null) {
						Movement moves = new Movement();
						append(moves, line.substring(1).toCharArray());
						script.negativeMoves.add(new AbstractMap.SimpleEntry<Integer, Movement>(negativeIteration, moves));
						negativeIteration++;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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
						direction = Entity.UP;
						break;
					case 'D':
						direction = Entity.DOWN;
						break;
					case 'L':
						direction = Entity.LEFT;
						break;
					case 'R':
						direction = Entity.RIGHT;
						break;
					default:
						direction = -1;
						break;
				}
				steps = Character.getNumericValue(s);
				if (direction != -1 && steps != -1 && steps != -2 && (steps <= 9 && steps >= 0)) {
					Map.Entry<Integer, Integer> entry = new AbstractMap.SimpleEntry<Integer, Integer>(direction, steps);
					moves.moves.add(entry);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new NumberFormatException("Incorrect script syntax from \"script.txt\"");
		}
	}
}
