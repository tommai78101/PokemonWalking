package script;

import java.util.ArrayList;
import java.util.Map;

import level.Area;
import level.WorldConstants;

public class TriggerData {
	public int x, y;
	public Script script;
	public int iteration;
	private boolean finished;
	
	// TODO: Add entity ID for NPCs.
	
	// TriggerData is for the game. Area uses TriggerData to communicate with
	// Scripts to obtain Movements.
	
	public TriggerData() {
		x = y = 0;
		script = null;
		finished = false;
	}
	
	public TriggerData loadTriggerData(int pixel) {
		this.x = (pixel >> 24) & 0xFF;
		this.y = (pixel >> 16) & 0xFF;
		if (WorldConstants.scripts.isEmpty())
			System.out.println("Scripts are empty");
		for (Script s : WorldConstants.scripts) {
			if (s.triggerID == (pixel & 0xFFFF)) {
				this.script = s;
				break;
			}
		}
		return this;
	}
	
	public void tick(Area area, int entityX, int entityY) {
		// TODO: Continue from here.
		if (this.script != null) {
			ArrayList<Map.Entry<Integer, Integer>> list = this.script.moves.getAllMoves();
			if (iteration < list.size()) {
				Map.Entry<Integer, Integer> entry = list.get(0);
				int steps = entry.getValue();
				if (steps >= 0) {
					area.getPlayer().setFacing(entry.getKey());
					if (steps > 0) 
						area.getPlayer().forceLockWalking();
					steps--;
					entry.setValue(steps);
				}
				else {
					list.remove(entry);
					if (list.isEmpty())
						this.finished = true;
				}
			}
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
}
