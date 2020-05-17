package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Obstacle;
import dialogue.Dialogue.DialogueType;
import level.Area;
import level.PixelData;
import screen.Scene;
import utility.DialogueBuilder;

public class SmallDeadTree extends Obstacle {
	public SmallDeadTree(PixelData data, int id) {
		this.pixelData = data;

		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"This dead tree has withered away, but it is still pretty strong.",
				DialogueType.SPEECH
			)
		);
	}

	@Override
	public void interact(Area area, Entity target) {
		// TODO Auto-generated method stub
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		// TODO Auto-generated method stub
	}

}
