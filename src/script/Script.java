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
	public ArrayList<Map.Entry<Integer, NewDialogue>> dialogues;
	public int iteration;
	
	public Script(){
		this.triggerID = 0;
		this.moves = new ArrayList<Map.Entry<Integer, Movement>> ();
		this.dialogues = new ArrayList<Map.Entry<Integer, NewDialogue>>();
		this.iteration = 0;
	}
	
	public Movement getIteratedMoves(){
		for (Map.Entry<Integer, Movement> entry: this.moves){
			if (entry.getKey() == this.iteration)
				return entry.getValue();
		}
		return null;
	}
	
	public NewDialogue getIteratedDialogues(){
		for (Map.Entry<Integer, NewDialogue> entry: this.dialogues){
			if (entry.getKey() == this.iteration){
				return entry.getValue();
			}
		}
		return null;
	}
	
	public boolean incrementIteration(){
		this.iteration++;
		int size = this.moves.size() + this.dialogues.size();
		if (this.iteration < size)
			return true;
		return false;
	}
	
	public static ArrayList<Script> loadScript(String filename) {
		ArrayList<Script> result = new ArrayList<Script>();
		
		// Scripts must contain a trigger data for Area to use, and must contain movements for Area to read.
		
		Script script = null;
		int iteration = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Script.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("/") || line.startsWith("@"))
					continue;
				else if (line.startsWith("$")) {
					int triggerID = Integer.valueOf(line.substring(1));
					if (triggerID > 0) {
						if (script == null)
							script = new Script();
						script.triggerID = triggerID;
					}
					
				}
				else if (line.startsWith("^")) {
					if (script != null) {
						Movement moves = new Movement();
						append(moves, line.substring(1).toCharArray());
						script.moves.add(new AbstractMap.SimpleEntry<Integer, Movement>(iteration, moves));
						iteration++;
					}
				}
				else if (line.startsWith("%")) {
					if (script != null){
						result.add(script);
						script = null;
						iteration = 0;
					}
				}
				else if (line.startsWith("#")){
					NewDialogue d = NewDialogue.createText(line.substring(1).replace("_", " "), NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, true);
					script.dialogues.add(new AbstractMap.SimpleEntry<Integer, NewDialogue>(iteration, d));
					iteration++;
				}
				else if (line.startsWith("?")){
					NewDialogue d = NewDialogue.createText(line.substring(1).replace("_", " "), NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_QUESTION, true);
					script.dialogues.add(new AbstractMap.SimpleEntry<Integer, NewDialogue>(iteration, d));
					iteration++;
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
