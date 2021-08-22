package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Obstacle;
import common.Debug;
import level.Area;
import level.PixelData;
import screen.Scene;

public class SmallDeadTree extends Obstacle {
	public SmallDeadTree(PixelData data, int id) {
		this.pixelData = data;
	}

	@Override
	public void interact(Area area, Entity target) {
		Debug.error("SmallDeadTree is not allowed to interact with other entities, nor be interacted with.");
	}

	@Override
	public void tick() {
		// SmallDeadTree should not be interacted with.
		if (this.interactingState) {
			this.setInteractingState(false);
		}
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		// Left intentionally blank.
	}
}
