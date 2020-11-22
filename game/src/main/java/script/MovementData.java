package script;

import java.util.ArrayList;
import java.util.Map;

public class MovementData {

	/**
	 * The current iterator pointing at the current active move in the data set.
	 */
	private int iterator;

	// Key: Direction, Value: Steps
	private ArrayList<Map.Entry<Integer, Integer>> originalMoves;
	private ArrayList<Map.Entry<Integer, Integer>> currentMoves;

	/**
	 * Constructor of MovementData, which is an object that stores all scripted movement actions. Kind
	 * of like a list of scripted cinematics.
	 */
	public MovementData() {
		this.iterator = 0;
		this.originalMoves = new ArrayList<>();
		this.currentMoves = new ArrayList<>();
	}

	/**
	 * Deep copy of MovementData.
	 * 
	 * @param movement
	 */
	public MovementData(MovementData movement) {
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

	/**
	 * Returns a list of the original scripted movement actions that was loaded from the script file.
	 * 
	 * @return
	 */
	public ArrayList<Map.Entry<Integer, Integer>> getOriginalMoves() {
		return this.originalMoves;
	}

	/**
	 * Returns a list of the currently active scripted movement actions.
	 * 
	 * @return
	 */
	public ArrayList<Map.Entry<Integer, Integer>> getCurrentMoves() {
		return this.currentMoves;
	}

	/**
	 * Returns the upcoming scripted movement action. Takes into account how many remaining steps the
	 * player need to move towards a given direction.
	 * 
	 * @return
	 */
	public Map.Entry<Integer, Integer> getNextMove() {
		Map.Entry<Integer, Integer> entry = this.currentMoves.get(this.iterator);
		if (entry.getValue() <= 0) {
			this.iterator++;
		}
		return entry;
	}

	/**
	 * Returns the current scripted move's directional facing before the end of the scripted movement
	 * action.
	 * 
	 * @return
	 */
	public int getNextMoveDirection() {
		Map.Entry<Integer, Integer> entry = this.currentMoves.get(this.iterator);
		return entry.getKey();
	}

	/**
	 * Returns the current scripted move's remaining number of steps before the end of the scripted
	 * movement action.
	 * 
	 * @return
	 */
	public int getNextMoveSteps() {
		Map.Entry<Integer, Integer> entry = this.currentMoves.get(this.iterator);
		return entry.getValue();
	}

	/**
	 * Checks if there are more scripted movements left.
	 * 
	 * @return
	 */
	public boolean hasNextMove() {
		return this.iterator < this.currentMoves.size();
	}

	/**
	 * Updates the current scripted movement action by overwriting the original number of steps with the
	 * new number of steps.
	 * <p>
	 * It replaces the Map.Entry with a new Map.Entry that contains the updated values. Map.Entry is
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

	/**
	 * Adds the scripted movement, where it can instruct the player to face in the given direction and
	 * take the given number of steps in that direction.
	 * 
	 * @param newDirection
	 * @param newSteps
	 */
	public void writeOriginalMove(int newDirection, int newSteps) {
		this.originalMoves.add(Map.entry(newDirection, newSteps));
	}

	/**
	 * Resets all scripted moves.
	 */
	public void reset() {
		this.iterator = 0;
		this.currentMoves.clear();
		this.currentMoves.addAll(this.originalMoves);
	}
}
