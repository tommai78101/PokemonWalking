package obstacle;

import abstracts.Entity;
import abstracts.Item;
import abstracts.Obstacle;
import dialogue.Dialogue.DialogueType;
import level.PixelData;
import screen.Scene;
import utility.DialogueBuilder;

public class SmallTree extends Obstacle {
	public SmallTree(PixelData data, int id) {
		this.pixelData = data;

		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"This tree is born from the ground that was created 6 months ago.",
				DialogueType.SPEECH
			)
		);
	}

	@Override
	public void tick() {}

	@Override
	public void render(Scene screen, int offsetX, int offsetY) {

	}

	@Override
	public void interact(Entity target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void interact(Entity target, Item item) {
		// TODO Auto-generated method stub

	}

}
