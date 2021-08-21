package entity;

import java.awt.Graphics;

import abstracts.Character;
import abstracts.Entity;
import common.Randomness;
import dialogue.Dialogue.DialogueType;
import level.Area;
import resources.Art;
import screen.Scene;
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
	public void tick() {
		this.handleAutoWalking();
		this.checkWalkingSpeed();
		this.handleMovement();
		this.controlTick();
		this.dialogueTick();
	}

	@Override
	public void render(Scene screen, Graphics graphics, int playerX, int playerY) {
		this.characterRender(screen, Art.joe, graphics, playerX, playerY);
		this.renderDialogue(screen, graphics);
	}

	@Override
	public void sprint() {
		// TODO Auto-generated method stub
	}

	@Override
	public void jump() {
		// TODO Auto-generated method stub
	}

	@Override
	public void ride() {
		// TODO Auto-generated method stub
	}

	@Override
	public void swim() {
		// TODO Auto-generated method stub
	}

	@Override
	public void interact(Area area, Entity target) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleAutoWalking() {
		if (!this.isAutoWalking) {
			return;
		}
		if (this.autoWalkingTick <= 0) {
			if (this.isLockedWalking || this.isInteracting())
				return;
			this.autoWalkingTick = Randomness.randInt() & 0xF;
			this.setFacing(Randomness.randDirection());
			this.isLockedWalking = Randomness.randBool();
		}
		else {
			this.autoWalkingTick--;
		}
	}

	// -------------------------------------------------------------------
	// Private methods

	private void checkWalkingSpeed() {
		this.xAccel = this.yAccel = 0;
		if (this.isLockedWalking) {
			if (!this.isChangingPositions)
				this.isChangingPositions = true;
			int facing = this.getFacing();
			if (facing == Character.UP && !this.isFacingBlocked[Character.UP]) {
				this.yAccel--;
			}
			else if (facing == Character.DOWN && !this.isFacingBlocked[Character.DOWN]) {
				this.yAccel++;
			}
			else if (facing == Character.LEFT && !this.isFacingBlocked[Character.LEFT]) {
				this.xAccel--;
			}
			else if (facing == Character.RIGHT && !this.isFacingBlocked[Character.RIGHT]) {
				this.xAccel++;
			}
		}
	}
}