package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Obstacle;
import common.Debug;
import dialogue.Dialogue.DialogueType;
import level.Area;
import level.PixelData;
import screen.Scene;
import utility.DialogueBuilder;

public class SmallTree extends Obstacle {
	public SmallTree(PixelData data, int id) {
		this.pixelData = data;

		// Little easter egg, I suppose.
		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"This tree is born from the ground that was created 6 months ago.",
				DialogueType.SPEECH
			)
		);
	}

	@Override
	public void interact(Area area, Entity target) {
		Debug.error("SmallTree is not allowed to interact with other entities, nor be interacted with.");
	}

	@Override
	public void tick() {
		// SmallTree should not be interacted with.
		if (this.interactingState) {
			this.setInteractingState(false);
		}
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		// Left intentionally blank.
	}
}
