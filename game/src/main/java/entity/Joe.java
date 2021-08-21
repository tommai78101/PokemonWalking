package entity;

import java.awt.Graphics;

import abstracts.Character;
import abstracts.Entity;
import common.Debug;
import common.Randomness;
import common.Tileable;
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
	public void walk() {
		if (this.getArea() == null) {
			Debug.error("Area is not set for character. This shouldn't be happening.");
			return;
		}

		// Check for collisions
		this.handleSurroundingTiles();
		if (this.isFacingBlocked[this.getFacing()]) {
			this.isLockedWalking = false;

			// This is one of those cases where the tile alignment is incorrect. We need to reset the positions
			// back inside the tiles.
			this.xPixelPosition = this.oldXAreaPosition * Tileable.WIDTH;
			this.yPixelPosition = this.oldYAreaPosition * Tileable.HEIGHT;
			return;
		}

		// Makes sure the acceleration stays limited to 1 pixel/tick.
		if (this.xAccel > 1)
			this.xAccel = 1;
		if (this.xAccel < -1)
			this.xAccel = -1;
		if (this.yAccel > 1)
			this.yAccel = 1;
		if (this.yAccel < -1)
			this.yAccel = -1;

		this.xPixelPosition += this.xAccel;
		this.yPixelPosition += this.yAccel;

		// Needs to get out of being locked to walking/jumping.
		// Note that we cannot compare using ||, what if the player is moving in one direction? What about
		// the other axis? Now about to walk. First, check to see if there's an obstacle blocking the path.
		if ((this.xPixelPosition % Tileable.WIDTH == 0 && this.yPixelPosition % Tileable.HEIGHT == 0)) {
			// Resets every flag that locks the player.
			this.isLockedWalking = false;
			this.xAreaPosition = this.xPixelPosition / Tileable.WIDTH;
			this.yAreaPosition = this.yPixelPosition / Tileable.HEIGHT;

			// Before we walk, check to see if the oldX and oldY are up-to-date with the
			// latest X and Y.
			if (this.oldXAreaPosition != this.xAreaPosition)
				this.oldXAreaPosition = this.xAreaPosition;
			if (this.oldYAreaPosition != this.yAreaPosition)
				this.oldYAreaPosition = this.yAreaPosition;

		}
		else if (this.isChangingPositions) {
			this.isChangingPositions = false;
			this.predictedXAreaPosition = this.xAreaPosition + this.xAccel;
			this.predictedYAreaPosition = this.yAreaPosition + this.yAccel;
		}
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