/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package abstracts;

import java.util.List;

import dialogue.Dialogue;
import interfaces.Interactable;
import level.PixelData;
import obstacle.SmallTree;

public abstract class Obstacle extends Entity implements Interactable {
	protected int color;
	protected int id;
	protected List<Dialogue> defaultDialogues;
	protected List<Dialogue> overrideDialogues;

	public static Obstacle build(final PixelData data, final int xPosition, final int yPosition) {
		int red = data.getRed();
		switch (red) {
			case 0x00:
				return new SmallTree(data, red);
			default:
				return null;
		}
	}

	public static Obstacle build(final int pixel, final int xPosition, final int yPosition) {
		return Obstacle.build(new PixelData(pixel, xPosition, yPosition), xPosition, yPosition);
	}

//	public Obstacle(PixelData data, int id) {
//		this.id = id;
//		this.color = data.getColor();
//		this.dialogues = new ArrayList<>();
//		this.overriddenDialogues = new ArrayList<>();
//		this.reset();
//	}

//	public void reset() {
//		this.dialogues.clear();
//		
//		// Sets the dialogues. Note there there are no error checking here.
//		switch (id) {
//		case 0x00: // Small Tree
//			this.dialogues.add(NewDialogue.createText(
//					"This tree is born from the ground that was created 6 months ago.", NewDialogue.MAX_STRING_LENGTH,
//					NewDialogue.DIALOGUE_SPEECH, false));
//			break;
//		case 0x01: // Logs
//			this.dialogues.add(NewDialogue.createText("A pile of wooden logs. That's where raw materials are get.",
//					NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
//			break;
//		case 0x02: // Planks
//			this.dialogues.add(NewDialogue.createText("Some wooden planks. Used in the house construction.",
//					NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
//			break;
//		case 0x03: // Scaffolding Left
//		case 0x04: // Scaffolding Right
//			this.dialogues.add(NewDialogue.createText(
//					"Some rubbles. Was used as scaffoldings during most of the construction.",
//					NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
//			break;
//		case 0x05: // Signs - We ignore in this case.
//			// TODO: Move function code from Overworld, Area to here.
//			break;
//		case 0x06: // Workbench Left
//		case 0x07: // Workbench Right
//			this.dialogues.add(NewDialogue.createText("Workbench table. This is where raw materials are refined.",
//					NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
//			break;
//		case 0x08: // Dead small tree
//			this.dialogues.add(NewDialogue.createText("This tree looks barren.", NewDialogue.MAX_STRING_LENGTH,
//					NewDialogue.DIALOGUE_SPEECH, false));
//			this.dialogues.add(NewDialogue.createText("Want to pick branches off?", NewDialogue.MAX_STRING_LENGTH,
//					NewDialogue.DIALOGUE_QUESTION, false));
//			this.dialogues.add(NewDialogue.createText("Wooden logs have been picked.", NewDialogue.MAX_STRING_LENGTH,
//					NewDialogue.DIALOGUE_SPEECH, false));
//			break;
//		}
//	}

	public int getID() {
		return this.id;
	}

	public int getColor() {
		return this.color;
	}

	public List<Dialogue> getDialogues() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return null;
			}
			return this.defaultDialogues;
		}
		return this.overrideDialogues;
	}

	@Override
	public PixelData getPixelData() {
		return this.pixelData;
	}
}
