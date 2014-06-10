package obstacle;

import level.PixelData;
import dialogue.NewDialogue;

public class Obstacle {
	private NewDialogue[] dialogue;
	private int color;
	private int id;
	
	public Obstacle(PixelData data, int id) {
		this.id = id;
		this.color = data.getColor();
		reset();
	}
	
	public void reset() {
		//Sets how many dialogues an obstacle can have.
		switch (id) {
			case 0x08:
				this.dialogue = new NewDialogue[3];
				break;
			default:
				this.dialogue = new NewDialogue[1];
				break;
		}
		//Sets the dialogues. Note there there are no error checking here.
		switch (id) {
			case 0x00: // Small Tree
				this.dialogue[0] = NewDialogue.createText("This tree is born from the ground that was created 6 months ago.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
			case 0x01: // Logs
				this.dialogue[0] = NewDialogue.createText("A pile of wooden logs. That's where raw materials are get.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
			case 0x02: // Planks
				this.dialogue[0] = NewDialogue.createText("Some wooden planks. Used in the house construction.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
			case 0x03: // Scaffolding Left
			case 0x04: // Scaffolding Right
				this.dialogue[0] = NewDialogue.createText("Some rubbles. Was used as scaffoldings during most of the construction.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
			case 0x05: // Signs - We ignore in this case.
				// TODO: Move function code from Overworld, Area to here.
				break;
			case 0x06: // Workbench Left
			case 0x07: // Workbench Right
				this.dialogue[0] = NewDialogue.createText("Workbench table. This is where raw materials are refined.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
			case 0x08: // Dead small tree
				this.dialogue[0] = NewDialogue.createText("This tree looks barren.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				this.dialogue[1] = NewDialogue.createText("Want to pick branches off?", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_QUESTION, false);
				this.dialogue[2] = NewDialogue.createText("Wooden logs have been picked.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				break;
		}
	}
	
	public int getID() {
		return this.id;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public NewDialogue[] getDialogues() {
		return this.dialogue;
	}
}
