package obstacle;

import java.awt.Graphics;

import abstracts.Entity;
import abstracts.Item;
import abstracts.Obstacle;
import dialogue.Dialogue.DialogueType;
import level.PixelData;
import screen.Scene;
import utility.DialogueBuilder;

/**
 * This class shouldn't be used for long. Needs to be replaced/removed eventually.
 * 
 * @author tlee
 */
@Deprecated
public class ProgrammerArtTable extends Obstacle {
	public ProgrammerArtTable(PixelData data, int id) {
		this.pixelData = data;

		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"This is programmer art.", DialogueType.SPEECH
			)
		);
	}

	@Override
	public void interact(Entity target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void interact(Entity target, Item item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		this.setInteractingState(false);
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		// TODO Auto-generated method stub

	}

}
