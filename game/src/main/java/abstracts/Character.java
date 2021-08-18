package abstracts;

import common.Tileable;
import entity.Joe;
import interfaces.CharacterActionable;
import interfaces.Interactable;
import level.PixelData;

public abstract class Character extends Entity implements Interactable, CharacterActionable {
	/**
	 * Entity class objects include NPC, Player, and monsters. Thus, it is fitting for the Entity
	 * objects to include a GenderType.
	 */
	public enum GenderType {
		// @formatter:off
		Nondetermined((byte) 0x7F), 
		Male((byte) 0x1), 
		Female((byte) 0xFF);
		// @formatter:on

		private byte typeId;

		private GenderType(byte value) {
			this.typeId = value;
		}

		public byte getByte() {
			return this.typeId;
		}

		public static GenderType determineGender(byte value) {
			if (value == Male.typeId)
				return Male;
			if (value == Female.typeId)
				return Female;
			return Nondetermined;
		}
	}

	// These numbers correspond to the index number of the columns of the character sprite sheet.
	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int UP = 2;
	public static final int RIGHT = 3;

	private boolean isPlayable;
	private int interactionDataColorID = 0;
	private GenderType gender = GenderType.Nondetermined;

	protected byte animationTick = 0;
	protected byte animationPointer = 0;
	protected boolean isLockedWalking;
	protected boolean isFacingBlocked[] = new boolean[4];

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

	// ----------------------------------------------------------------------
	// Abstract methods

	abstract protected void controlTick();

	// ----------------------------------------------------------------------
	// Static methods

	public static Character build(int pixel, int x, int y) {
		return Character.build(new PixelData(pixel, x, y), x, y);
	}

	public static Character build(PixelData pixelData, int x, int y) {
		int red = pixelData.getRed();
		switch (red) {
			case 0x1: {// Joe
				return new Joe().setOriginPosition(x, y);
			}
			default:
				return null;
		}
	}
}