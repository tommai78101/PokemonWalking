package obstacle;

import java.util.ArrayList;
import java.util.List;

import abstracts.Obstacle;
import dialogue.Dialogue;
import level.PixelData;
import screen.Scene;

public class SmallTree extends Obstacle {
	private PixelData pixelData;

	public SmallTree(PixelData data, int id) {
		super();
		this.pixelData = data;

		List<Dialogue> dialogues = new ArrayList<>();
		dialogues.add(
			Dialogue.createText(
				"This tree is born from the ground that was created 6 months ago.", 
				Dialogue.MAX_STRING_LENGTH,
				Dialogue.DIALOGUE_SPEECH, 
				false
			)
		);
		super.initializeDialogues(dialogues);
	}

	@Override
	public PixelData getPixelData() {
		return this.pixelData;
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Scene screen, int offsetX, int offsetY) {
	}

}
