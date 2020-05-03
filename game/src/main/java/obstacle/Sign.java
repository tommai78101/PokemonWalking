package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Item;
import abstracts.Obstacle;
import dialogue.Dialogue;
import dialogue.Dialogue.DialogueType;
import level.PixelData;
import main.Game;
import screen.Scene;
import utility.Debug;
import utility.DialogueBuilder;

public class Sign extends Obstacle {
	public Sign(PixelData data, int id) {
		this.pixelData = data;

		//TODO(4/04/2020): Figure out a way to load dialogue scripts for Signs based on the pixel data's green and blue values.
		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"This is a default sign post.", DialogueType.SPEECH
			)
		);
	}

	@Override
	public void interact(Entity target) {
		// TODO Auto-generated method stub
		System.err.println("Sign is interacting.");

	}

	@Override
	public void interact(Entity target, Item item) {
		// TODO Auto-generzated method stub
		System.err.println("Sign with item is interacting.");
	}

	@Override
	public void tick() {
		if (this.interactingState) {
			this.setInteractingState(true);
			Dialogue currentDialogue = this.getCurrentDialogue();
			if (currentDialogue == null || !currentDialogue.isReady()) {
				this.setInteractingState(false);
				Debug.log("Sign no longer interacting.");
				return;
			}
			if (!currentDialogue.isDialogueCompleted()) {
				currentDialogue.tick();
				Debug.log("Sign is ticking.");
			}
			else {
				Debug.log("Sign is no longer ticking.");
				if (Game.keys.isPrimaryPressed()) {
					Debug.log("Primary keys are pressed.");
					Game.keys.primaryReceived();
					this.setInteractingState(false);
					this.endDialogue();
					Debug.log("Sign interacted");
				}
			}
		}
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		if (this.interactingState) {
			Dialogue currentDialogue = this.getCurrentDialogue();
			if (currentDialogue != null)
				currentDialogue.render(screen, graphics);
		}
	}

}
