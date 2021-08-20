package abstracts;

import java.awt.Graphics;

import common.Tileable;
import entity.Joe;
import enums.GenderType;
import interfaces.CharacterActionable;
import interfaces.Interactable;
import level.PixelData;
import screen.Bitmap;
import screen.Scene;

public abstract class Character extends Entity implements Interactable, CharacterActionable {
	// These numbers correspond to the index number of the columns of the character sprite sheet.
	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int UP = 2;
	public static final int RIGHT = 3;

	private boolean isPlayable;
	private int interactionDataColorID = 0;
	private GenderType gender = GenderType.Nondetermined;

	// General character animations.
	protected byte animationTick = 0;
	protected byte animationPointer = 0;
	protected byte animationDefaultFacing = 0;
	protected boolean isLockedWalking;
	protected boolean isFacingBlocked[] = new boolean[4];
	protected int xAccel;
	protected int yAccel;

	public Character() {
		this.setCharacterPlayable(false);
	}

	public Character setOriginPosition(int x, int y) {
		this.setPosition(x, y);
		return this;
	}

	protected void setCharacterPlayable(final boolean value) {
		this.isPlayable = value;
	}

	public boolean isCharacterPlayable() {
		return this.isPlayable;
	}

	@Deprecated
	public int getInteractableID() {
		return this.interactionDataColorID;
	}

	@Deprecated
	public void setInteractableID(int dataColor) {
		this.interactionDataColorID = dataColor;
	}

	public void setGender(final GenderType value) {
		this.gender = value;
	}

	public GenderType getGender() {
		return this.gender;
	}

	public void stopAnimation() {
		this.animationTick = 0;
		this.animationPointer = 0;
	}

	/**
	 * On the X axis, Properly calculates the rendering offset for the character entity by using the
	 * player offset.
	 *
	 * @param playerOffsetX
	 *            The player offset that is passed on down from the Game object,
	 *            {@linkplain main.Game#render Game.render()}.
	 * @return The calculated rendering offset on the X axis.
	 */
	public int getRenderPositionX(int playerOffsetX) {
		return this.xPosition * Tileable.WIDTH - playerOffsetX;
	}

	/**
	 * On the Y axis, Properly calculates the rendering offset for the character entity by using the
	 * player offset.
	 *
	 * @param playerOffsetY
	 *            The player offset that is passed on down from the Game object,
	 *            {@linkplain main.Game#render Game.render()}.
	 * @return The calculated rendering offset on the Y axis.
	 */
	public int getRenderPositionY(int playerOffsetY) {
		return this.yPosition * Tileable.HEIGHT - playerOffsetY;
	}

	public void characterRender(Scene screen, Bitmap[][] sprite, Graphics graphics, int playerX, int playerY) {
		if (this.isLockedWalking) {
			screen.npcBlit(sprite[this.getFacing()][this.animationPointer], this.getRenderPositionX(playerX), this.getRenderPositionY(playerY));
		}
		else {
			// Idle animation
			screen.npcBlit(sprite[this.getFacing()][this.animationDefaultFacing], this.getRenderPositionX(playerX), this.getRenderPositionY(playerY));
		}
	}

	public void setDefaultAnimationFacing(int facing) {
		this.animationDefaultFacing = (byte) (facing & 0xFF);
	}

	public byte getDefaultAnimationFacing() {
		return this.animationDefaultFacing;
	}

	// ----------------------------------------------------------------------
	// Protected methods

	protected void controlTick() {
		if (this.isLockedWalking) {
			this.animationTick++;
			if ((this.getFacing() == Character.UP && this.isFacingBlocked[Character.UP]) || (this.getFacing() == Character.DOWN && this.isFacingBlocked[Character.DOWN])) {
				if (this.animationTick >= 10) {
					this.animationTick = 0;
				}
			}
			else if ((this.getFacing() == Character.LEFT && this.isFacingBlocked[Character.LEFT]) || (this.getFacing() == Character.RIGHT && this.isFacingBlocked[Character.RIGHT])) {
				if (this.animationTick >= 10) {
					this.animationTick = 0;
				}
			}
			else {
				if (this.animationTick >= 4)
					this.animationTick = 0;
			}
			if (this.animationTick == 0) {
				this.animationPointer++;
				if (this.animationPointer > 3)
					this.animationPointer = 0;
			}
		}
	}

	protected void handleMovement() {
		if (this.isLockedWalking)
			this.walk();
		else
			this.isLockedWalking = false;
	}

	// ----------------------------------------------------------------------
	// Private methods

	@Override
	public void walk() {
		// Makes sure the acceleration stays limited to 1 pixel/tick.
		if (this.xAccel > 1)
			this.xAccel = 1;
		if (this.xAccel < -1)
			this.xAccel = -1;
		if (this.yAccel > 1)
			this.yAccel = 1;
		if (this.yAccel < -1)
			this.yAccel = -1;
		// Needs to get out of being locked to walking/jumping.
		// Note that we cannot compare using ||, what if the player is moving in one
		// direction? What about the other axis?
		if ((this.xPosition % Tileable.WIDTH == 0 && this.yPosition % Tileable.HEIGHT == 0)) {
			// Resets every flag that locks the player.
			this.isLockedWalking = false;
		}
	}

	// ----------------------------------------------------------------------
	// Static methods

	public static Character build(int pixel, int x, int y) {
		return Character.build(new PixelData(pixel, x, y), x, y);
	}

	public static Character build(PixelData pixelData, int x, int y) {
		Character result = null;
		int red = pixelData.getRed();
		switch (red) {
			case 0x1: {// Joe
				result = new Joe();
				result.setOriginPosition(x, y);
				result.setPixelData(pixelData);
				break;
			}
			default:
				break;
		}
		return result;
	}
}