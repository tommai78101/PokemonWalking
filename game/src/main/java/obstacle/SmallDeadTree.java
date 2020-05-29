package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Obstacle;
import dialogue.Dialogue.DialogueType;
import level.Area;
import level.PixelData;
import screen.Scene;
import utility.Debug;
import utility.DialogueBuilder;

public class SmallDeadTree extends Obstacle {
	public SmallDeadTree(PixelData data, int id) {
		this.pixelData = data;

		// Another easter egg.
		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"This dead tree has withered away, but it is still pretty strong.",
				DialogueType.SPEECH
			)
		);
	}

	@Override
	public void interact(Area area, Entity target) {
		Debug.error("SmallDeadTree is not allowed to interact with other entities, nor be interacted with.");
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
