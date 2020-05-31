/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package abstracts;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import dialogue.Dialogue;
import interfaces.Interactable;
import level.PixelData;
import obstacle.ProgrammerArtTable;
import obstacle.Sign;
import obstacle.SmallDeadTree;
import obstacle.SmallTree;
import screen.Scene;

public abstract class Obstacle extends Entity implements Interactable {
	protected int color;
	protected int id;
	protected int defaultDialogueIterator = 0;
	protected int overrideDialogueIterator = 0;
	protected Dialogue currentDefaultDialogue = null;
	protected Dialogue currentOverrideDialogue = null;
	protected List<Dialogue> defaultDialogues = new ArrayList<>();
	protected List<Dialogue> overrideDialogues = new ArrayList<>();

	public static Obstacle build(final PixelData data, final int xPosition, final int yPosition) {
		int red = data.getRed();
		switch (red) {
			case 0x00:
				return new SmallTree(data, red);
			case 0x05:
				return new Sign(data, red);
			case 0x08:
				return new SmallDeadTree(data, red);

			//Programmer art goes here. Marked as deprecated for this reason.
			//Rock 
			case 0x01:
				//Lumber
			case 0x02:
				//Trash (left) and (right)
			case 0x03:
			case 0x04:
				//Workbench (left) and (right)
			case 0x06:
			case 0x07:
				return new ProgrammerArtTable(data, red);

			default:
				System.err.println("Unknown obstacle " + red + " at [" + xPosition + "," + yPosition + "] found.");
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

	public Dialogue getCurrentDialogue() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return null;
			}
			this.currentDefaultDialogue = this.defaultDialogues.get(this.defaultDialogueIterator);
			return this.currentDefaultDialogue;
		}
		this.currentOverrideDialogue = this.overrideDialogues.get(this.overrideDialogueIterator);
		return this.currentOverrideDialogue;
	}

	public Dialogue nextDialogue() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return null;
			}
			this.defaultDialogueIterator++;
			if (this.defaultDialogueIterator >= this.defaultDialogues.size()) {
				this.endDialogue();
				return null;
			}
			this.currentDefaultDialogue = this.defaultDialogues.get(this.defaultDialogueIterator);
			return this.currentDefaultDialogue;
		}
		this.overrideDialogueIterator++;
		if (this.overrideDialogueIterator >= this.overrideDialogues.size()) {
			this.endDialogue();
			return null;
		}
		this.currentOverrideDialogue = this.overrideDialogues.get(this.overrideDialogueIterator);
		return this.currentOverrideDialogue;
	}

	public void endDialogue() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return;
			}
			this.defaultDialogueIterator = 0;
			this.defaultDialogues.forEach(dialogue -> {
				dialogue.resetDialogue();
			});
			return;
		}
		this.overrideDialogueIterator = 0;
		this.overrideDialogues.forEach(dialogue -> {
			dialogue.resetDialogue();
		});
		return;
	}

	/**
	 * Render the dialogue, while temporarily ignoring previous offsets that were set in the Scene.
	 * 
	 * @param screen
	 * @param graphics
	 */
	public void renderDialogue(Scene screen, Graphics graphics) {
		int xOffset = screen.getXOffset();
		int yOffset = screen.getYOffset();
		screen.setOffset(0, 0);
		this.render(screen, graphics, 0, 0);
		screen.setOffset(xOffset, yOffset);
	}
}