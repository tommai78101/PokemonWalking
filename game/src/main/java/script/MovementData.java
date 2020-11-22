package script;

import java.util.ArrayList;
import java.util.Map;

public class MovementData {

	int iterator;

	// Key: Direction, Value: Steps
	ArrayList<Map.Entry<Integer, Integer>> originalMoves;
	ArrayList<Map.Entry<Integer, Integer>> currentMoves;

	public MovementData() {
		this.iterator = 0;
		this.originalMoves = new ArrayList<>();
		this.currentMoves = new ArrayList<>();
	}

	public MovementData(MovementData movement) {
		// Deep copy
		this.iterator = movement.iterator;
		this.originalMoves = new ArrayList<>();
		for (Map.Entry<Integer, Integer> e : movement.originalMoves) {
			this.originalMoves.add(Map.entry(e.getKey(), e.getValue()));
		}
		this.currentMoves = new ArrayList<>();
		for (Map.Entry<Integer, Integer> e : movement.currentMoves) {
			this.currentMoves.add(Map.entry(e.getKey(), e.getValue()));
		}
	}

	public ArrayList<Map.Entry<Integer, Integer>> getOriginalMoves() {
		return this.originalMoves;
	}

	public ArrayList<Map.Entry<Integer, Integer>> getCurrentMoves() {
		return this.currentMoves;
	}

	public Map.Entry<Integer, Integer> getNextMove() {
		Map.Entry<Integer, Integer> entry = this.currentMoves.get(this.iterator);
		if (entry.getValue() <= 0) {
			this.iterator++;
		}
		return entry;
	}

	public int getNextMoveDirection() {
		Map.Entry<Integer, Integer> entry = this.currentMoves.get(this.iterator);
		return entry.getKey();
	}

	public int getNextMoveSteps() {
		Map.Entry<Integer, Integer> entry = this.currentMoves.get(this.iterator);
		return entry.getValue();
	}

	public boolean hasNextMove() {
		return this.iterator < this.currentMoves.size();
	}

	/**
	 * Replace the Map.Entry with a new Map.Entry that contains the updated values. Map.Entry is
	 * actually immutable, so we cannot use an unsupported operation of setting new values.
	 * 
	 * @param count
	 * @return
	 */
	public void updateCurrentMove(int newStep) {
		Map.Entry<Integer, Integer> entry = this.currentMoves.get(this.iterator);
		int direction = entry.getKey();
		this.currentMoves.remove(this.iterator);
		this.currentMoves.add(this.iterator, Map.entry(direction, newStep));
	}

	public void writeOriginalMove(int newDirection, int newSteps) {
		this.originalMoves.add(Map.entry(newDirection, newSteps));
	}

	public void reset() {
		this.iterator = 0;
		this.currentMoves.clear();
		this.currentMoves.addAll(this.originalMoves);
	}
}
