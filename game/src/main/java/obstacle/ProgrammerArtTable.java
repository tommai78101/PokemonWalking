package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Obstacle;
import level.Area;
import level.PixelData;
import screen.Scene;

/**
 * This class shouldn't be used for long. Needs to be replaced/removed eventually.
 *
 * @author tlee
 */
@Deprecated
public class ProgrammerArtTable extends Obstacle {
	public ProgrammerArtTable(PixelData data) {
		this.pixelData = data;
	}

	@Override
	public void interact(Area area, Entity target) {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		// TODO Auto-generated method stub
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		this.setInteractingState(false);
	}
}
