package entity;

import abstracts.Character;
import dialogue.Dialogue.DialogueType;
import utility.DialogueBuilder;

public class Joe extends Character {
	public Joe() {
		// TODO(4/04/2020): Figure out a way to load dialogue scripts for Signs based on the pixel data's
		// green and blue values.
		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"Hello world.", DialogueType.SPEECH
			)
		);
		this.setAutoWalking(true);
	}

	@Override
	public int getAutoWalkTickFrequency() {
		return Character.AUTO_WALK_SLOW;
	}
}