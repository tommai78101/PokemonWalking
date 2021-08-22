/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package abstracts;

import interfaces.Interactable;
import level.Area;
import level.PixelData;
import obstacle.ProgrammerArtTable;
import obstacle.Sign;
import obstacle.SmallDeadTree;
import obstacle.SmallTree;

public abstract class Obstacle extends Entity implements Interactable {
	protected int color;
	protected int id;

	public static Obstacle build(final Area area, final PixelData data, final int xPosition, final int yPosition) {
		int red = data.getRed();
		Obstacle obj = null;
		switch (red) {
			case 0x00:
				obj = new SmallTree(data);
				break;
			case 0x05:
				obj = new Sign(data);
				break;
			case 0x08:
				obj = new SmallDeadTree(data);
				break;

			// Programmer art goes here. Marked as deprecated for this reason.
			// Rock
			case 0x01:
				// Lumber
			case 0x02:
				// Trash (left) and (right)
			case 0x03:
			case 0x04:
				// Workbench (left) and (right)
			case 0x06:
			case 0x07:
				obj = new ProgrammerArtTable(data);
				break;
			default:
				System.err.println("Unknown obstacle " + red + " at [" + xPosition + "," + yPosition + "] found.");
				break;
		}
		if (obj != null) {
			obj.setArea(area);
			obj.loadTriggerData();
		}
		return obj;
	}

	public static Obstacle build(final Area area, final int pixel, final int xPosition, final int yPosition) {
		return Obstacle.build(area, new PixelData(pixel, xPosition, yPosition), xPosition, yPosition);
	}

	// public Obstacle(PixelData data, int id) {
	// this.id = id;
	// this.color = data.getColor();
	// this.dialogues = new ArrayList<>();
	// this.overriddenDialogues = new ArrayList<>();
	// this.reset();
	// }

	// public void reset() {
	// this.dialogues.clear();
	//
	// // Sets the dialogues. Note there there are no error checking here.
	// switch (id) {
	// case 0x00: // Small Tree
	// this.dialogues.add(NewDialogue.createText(
	// "This tree is born from the ground that was created 6 months ago.",
	// NewDialogue.MAX_STRING_LENGTH,
	// NewDialogue.DIALOGUE_SPEECH, false));
	// break;
	// case 0x01: // Logs
	// this.dialogues.add(NewDialogue.createText("A pile of wooden logs. That's where raw materials are
	// get.",
	// NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
	// break;
	// case 0x02: // Planks
	// this.dialogues.add(NewDialogue.createText("Some wooden planks. Used in the house construction.",
	// NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
	// break;
	// case 0x03: // Scaffolding Left
	// case 0x04: // Scaffolding Right
	// this.dialogues.add(NewDialogue.createText(
	// "Some rubbles. Was used as scaffoldings during most of the construction.",
	// NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
	// break;
	// case 0x05: // Signs - We ignore in this case.
	// // TODO: Move function code from Overworld, Area to here.
	// break;
	// case 0x06: // Workbench Left
	// case 0x07: // Workbench Right
	// this.dialogues.add(NewDialogue.createText("Workbench table. This is where raw materials are
	// refined.",
	// NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false));
	// break;
	// case 0x08: // Dead small tree
	// this.dialogues.add(NewDialogue.createText("This tree looks barren.",
	// NewDialogue.MAX_STRING_LENGTH,
	// NewDialogue.DIALOGUE_SPEECH, false));
	// this.dialogues.add(NewDialogue.createText("Want to pick branches off?",
	// NewDialogue.MAX_STRING_LENGTH,
	// NewDialogue.DIALOGUE_QUESTION, false));
	// this.dialogues.add(NewDialogue.createText("Wooden logs have been picked.",
	// NewDialogue.MAX_STRING_LENGTH,
	// NewDialogue.DIALOGUE_SPEECH, false));
	// break;
	// }
	// }

	public int getID() {
		return this.id;
	}

	public int getColor() {
		return this.color;
	}

	@Override
	public PixelData getPixelData() {
		return this.pixelData;
	}
}