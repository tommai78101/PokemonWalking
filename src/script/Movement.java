package script;

import java.util.ArrayList;
import java.util.Map;

public class Movement {
	
	int iterator;
	ArrayList<Map.Entry<Integer, Integer>> moves; //Direction, Steps
	
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
}
