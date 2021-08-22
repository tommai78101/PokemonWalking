package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Obstacle;
import common.Debug;
import level.Area;
import level.PixelData;
import screen.Scene;

public class Sign extends Obstacle {
	public Sign(PixelData data) {
		this.pixelData = data;
	}

	@Override
	public void interact(Area area, Entity target) {
		Debug.error("Sign is not allowed to interact with other entities. It should only be interacted with.");
	}

	@Override
	public void tick() {
		this.dialogueTick();
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		this.dialogueRender(screen);
	}

}
