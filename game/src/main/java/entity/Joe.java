package entity;

import abstracts.Character;

public class Joe extends Character {
	public Joe() {
		this.setAutoWalking(true);
	}

	@Override
	public int getAutoWalkTickFrequency() {
		return Character.AUTO_WALK_SLOW;
	}
}