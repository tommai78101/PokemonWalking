package obstacle;

import level.PixelData;
import dialogue.NewDialogue;

public class Obstacle {
	private NewDialogue dialogue;
	private int color;
	private int id;
	
	public Obstacle(PixelData data, int id) {
		this.id = id;
		this.color = data.getColor();
		reset();
	}
	
	public void reset(){
		switch (id){
			case 0x00: //Small Tree
				this.dialogue = null;
				break;
			case 0x01: //Logs
				this.dialogue = NewDialogue.createText("A pile of wooden logs. That's where raw materials are get.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
			case 0x02: //Planks
				this.dialogue = NewDialogue.createText("Some wooden planks. Used in the house construction.",NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
			case 0x03: //Scaffolding Left
			case 0x04: //Scaffolding Right
				this.dialogue = NewDialogue.createText("Some rubbles. Was used as scaffoldings during most of the construction.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
		}
	}
	
	public int getID(){
		return this.id;
	}
	
	public int getColor(){
		return this.color;
	}
	
	public NewDialogue getDialogue(){
		return this.dialogue;
	}
}
