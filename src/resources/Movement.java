package resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import abstracts.Entity;

public class Movement {
	
	int iterator;
	ArrayList<Map.Entry<Integer, Integer>> moves;
	
	public Movement() {
		this.iterator = 0;
		moves = new ArrayList<Map.Entry<Integer, Integer>>();
	}
	
	public ArrayList<Map.Entry<Integer, Integer>> getAllMoves() {
		return this.moves;
	}
	
	public Map.Entry<Integer, Integer> getNextMove() {
		Map.Entry<Integer, Integer> entry = this.moves.get(iterator);
		iterator++;
		return entry;
	}
	
	public void startFromBeginning() {
		this.iterator = 0;
	}
	
	public static ArrayList<Map.Entry<Integer, Movement>> loadMovement(String filename) {
		ArrayList<Map.Entry<Integer, Movement>> results = new ArrayList<Map.Entry<Integer, Movement>>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Movement.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			String[] tokens;
			Movement temp = null;
			int id = -1;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("/"))
					continue;
				else if (line.startsWith("$")) {
					tokens = line.split("$");
					temp = new Movement();
					id = Integer.valueOf(tokens[1]);
				}
				else if (line.startsWith("^")) {
					tokens = line.split("^");
					append(temp, tokens[1].toCharArray());
				}
				else if (line.startsWith("%")) {
					if (id != -1) {
						results.add(new AbstractMap.SimpleEntry<Integer, Movement>(id, temp));
						id = -1;
						temp = null;
					}
					else
						throw new Exception("Error in labelling movement ID. ID value == -1");
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Scripting syntax error. Please check.", e);
		}
		return results;
	}
	
	private static void append(Movement moves, char[] list) {
		int direction = -1;
		int steps = -1;
		
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
			}
			steps = Character.getNumericValue(s);
			if (direction != -1 || steps != -1) {
				Map.Entry<Integer, Integer> entry = new AbstractMap.SimpleEntry<Integer, Integer>(direction, steps);
				moves.moves.add(entry);
			}
		}
	}
}
