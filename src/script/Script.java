package script;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import abstracts.Entity;

public class Script {
	public int triggerID;
	public Movement moves;
	
	public Script(){
		this.triggerID = 0;
		this.moves = new Movement();
	}
	
	public static ArrayList<Script> loadScript(String filename) {
		ArrayList<Script> result = new ArrayList<Script>();
		
		// Scripts must contain a trigger data for Area to use, and must contain movements for Area to read.
		
		Script script = null;
		
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
						append(script.moves, line.substring(1).toCharArray());
					}
				}
				else if (line.startsWith("%")) {
					if (script != null){
						result.add(script);
						script = null;
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
