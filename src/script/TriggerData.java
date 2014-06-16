package script;

import level.WorldConstants;

public class TriggerData {
	public int x, y;
	public Script script;
	
	//TODO: Add entity ID for NPCs.
	
	
	//TriggerData is for the game. Area uses TriggerData to communicate with
	//Scripts to obtain Movements.
	
	public TriggerData(){
		x = y = 0;
		script = null;
	}	
	
	public TriggerData loadTriggerData(int pixel){
		this.x = (pixel >> 24) & 0xFF;
		this.y = (pixel >> 16) & 0xFF;
		for (Script s: WorldConstants.scripts){
			if (s.triggerID == (pixel & 0xFFFF)){
				this.script = s;
				break;
			}
		}
		return this;
	}
}
