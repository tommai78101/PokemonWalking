package script;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class MovementData {

	int iterator;
	ArrayList<Map.Entry<Integer, Integer>> moves; // Direction, Steps

	public MovementData() {
		this.iterator = 0;
		moves = new ArrayList<Map.Entry<Integer, Integer>>();
	}

	public MovementData(MovementData movement) {
		// Deep copy
		this.iterator = movement.iterator;
		this.moves = new ArrayList<Map.Entry<Integer, Integer>>();
		for (Map.Entry<Integer, Integer> e : movement.moves) {
			this.moves.add(new AbstractMap.SimpleEntry<Integer, Integer>(e.getKey(), e.getValue()));
		}
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
}
