package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Obstacle;
import dialogue.Dialogue;
import dialogue.Dialogue.DialogueType;
import level.Area;
import level.PixelData;
import main.Game;
import screen.Scene;
import utility.Debug;
import utility.DialogueBuilder;

public class Sign extends Obstacle {
	public Sign(PixelData data, int id) {
		this.pixelData = data;

		// TODO(4/04/2020): Figure out a way to load dialogue scripts for Signs based on the pixel data's
		// green and blue values.
		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"This is a default sign post.", DialogueType.SPEECH
			)
		);
	}

	@Override
	public void interact(Area area, Entity target) {
		Debug.error("Sign is not allowed to interact with other entities. It should only be interacted with.");
	}

	@Override
	public void tick() {
		if (this.interactingState) {
			this.setInteractingState(true);
			Dialogue currentDialogue = this.getCurrentDialogue();
			if (currentDialogue == null || !currentDialogue.isReady()) {
				this.setInteractingState(false);
				return;
			}
			if (!currentDialogue.isDialogueCompleted()) {
				currentDialogue.tick();
			}
			else {
				if (Game.keys.isPrimaryPressed()) {
					Game.keys.primaryReceived();
					this.setInteractingState(false);
					this.endDialogue();
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
