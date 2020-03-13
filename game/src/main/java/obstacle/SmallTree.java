package obstacle;

import java.util.ArrayList;
import java.util.List;

import abstracts.Obstacle;
import dialogue.NewDialogue;
import level.PixelData;
import screen.BaseScreen;

public class SmallTree extends Obstacle {
	private PixelData pixelData;

	public SmallTree(PixelData data, int id) {
		super();
		this.pixelData = data;

		List<NewDialogue> dialogues = new ArrayList<>();
		dialogues.add(
			NewDialogue.createText(
				"This tree is born from the ground that was created 6 months ago.", 
				NewDialogue.MAX_STRING_LENGTH,
				NewDialogue.DIALOGUE_SPEECH, 
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
	public void render(BaseScreen screen, int offsetX, int offsetY) {
	}

}
