package abstracts;

import java.awt.Graphics;

import common.Debug;
import common.Randomness;
import common.Tileable;
import entity.Joe;
import entity.Player;
import enums.GenderType;
import interfaces.CharacterActionable;
import interfaces.Interactable;
import level.Area;
import level.PixelData;
import resources.Art;
import screen.Bitmap;
import screen.Scene;

public abstract class Character extends Entity implements Interactable, CharacterActionable {
	// Character debug mode only
	private final boolean isDebugMode = false;

	// These numbers correspond to the index number of the columns of the character sprite sheet.
	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int UP = 2;
	public static final int RIGHT = 3;

	// AutoWalk frequency settings
	public static final int AUTO_WALK_DISABLE = 0;
	public static final int AUTO_WALK_VERY_FREQUENT = 0xF;
	public static final int AUTO_WALK_FAST = 0x1F;
	public static final int AUTO_WALK_MODERATE = 0x3F;
	public static final int AUTO_WALK_SLOW = 0x7F;
	public static final int AUTO_WALK_VERY_SLOW = 0xFF;

	private boolean isPlayable;
	private int interactionDataColorID = 0;
	private GenderType gender = GenderType.Nondetermined;
	private Area area;

	// General character animations.
	protected byte animationTick = 0;
	protected byte animationPointer = 0;
	protected byte animationDefaultFacing = 0;
	protected boolean isAutoWalking;
	protected int autoWalkingTick = 0;
	protected boolean isLockedWalking;
	protected boolean isChangingPositions = false;
	protected final boolean isFacingBlocked[] = new boolean[4];
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

	public void stopAutoWalking() {
		this.autoWalkingTick = 0;
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
		return this.xPixelPosition - playerOffsetX;
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
		return this.yPixelPosition - playerOffsetY;
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

	public void setArea(Area area) {
		this.area = area;
	}

	public Area getArea() {
		return this.area;
	}

	public void setAutoWalking(boolean state) {
		this.isAutoWalking = state;
	}

	public boolean isAutoWalking() {
		return this.isAutoWalking;
	}

	public boolean isLockedWalking() {
		return this.isLockedWalking;
	}

	public void setLockedWalking(boolean state) {
		this.isLockedWalking = state;
	}

	public boolean isDebugMode() {
		return this.isDebugMode;
	}

	/**
	 * Sets where each of the four directions are blocked by obstacles in front of the player. The
	 * obstacles are in front of the player, when the player is facing towards them. That is the time to
	 * check and see if the obstacle is blocking the player or not.
	 *
	 * @param up
	 *            If an obstacle is in front of the player when the player is facing towards NORTH, or
	 *            UP, then up is true. False, otherwise.
	 * @param down
	 *            If an obstacle is below of the player when the player is facing towards SOUTH, or
	 *            DOWN, then down is true. False, otherwise.
	 * @param left
	 *            If an obstacle is to the left of the player when the player is facing towards WEST, or
	 *            LEFT, then left is true. False, otherwise.
	 * @param right
	 *            If an obstacle is to the right of the player when the player is facing towards EAST,
	 *            or RIGHT, then right is true. False, otherwise.
	 */
	public void setAllBlockingDirections(final boolean up, final boolean down, final boolean left, final boolean right) {
		this.isFacingBlocked[Character.UP] = up;
		this.isFacingBlocked[Character.DOWN] = down;
		this.isFacingBlocked[Character.LEFT] = left;
		this.isFacingBlocked[Character.RIGHT] = right;
	}

	// ----------------------------------------------------------------------
	// Abstract methods

	public abstract int getAutoWalkTickFrequency();

	// ----------------------------------------------------------------------
	// Override methods

	@Override
	public void tick() {
		this.handleAutoWalking();
		this.checkWalkingSpeed();
		this.handleMovement();
		this.controlTick();
		this.dialogueTick();
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		if (this.isDebugMode) {
			screen.blit(Art.error, this.oldXAreaPosition * Tileable.WIDTH - offsetX, this.oldYAreaPosition * Tileable.HEIGHT - offsetY);
			screen.blit(Art.error, this.predictedXAreaPosition * Tileable.WIDTH - offsetX, this.predictedYAreaPosition * Tileable.HEIGHT - offsetY);
		}
		this.characterRender(screen, Art.joe, graphics, offsetX, offsetY);
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
		else {
			this.isLockedWalking = false;
			this.stopAnimation();
		}
	}

	protected void handleSurroundingTiles() {
		boolean upDirection = this.checkCharacterSurroundingData(this.area, 0, -1);
		boolean downDirection = this.checkCharacterSurroundingData(this.area, 0, 1);
		boolean leftDirection = this.checkCharacterSurroundingData(this.area, -1, 0);
		boolean rightDirection = this.checkCharacterSurroundingData(this.area, 1, 0);
		this.setAllBlockingDirections(upDirection, downDirection, leftDirection, rightDirection);
	}

	// ----------------------------------------------------------------------
	// Private methods

	/**
	 * Checks the pixel data and sets properties according to the documentation provided. The tile the
	 * pixel data is representing determines whether it should allow or block the player from walking
	 * towards it.
	 * <p>
	 * If it's intended for the interacting entity to respond to key presses while dialogues are shown,
	 * you must put a {@linkplain Player#isInteracting isInteracting()} boolean check before checking to
	 * see if the entity is being interacted.
	 * <p>
	 * In short, this is the method call that works out the collision detection/response in the game.
	 *
	 * @param xOffset
	 *            Sets the offset of the PixelData it should check by the X axis.
	 * @param yOffset
	 *            Sets the offset of the PixelData it should check by the Y axis.
	 * @return The value determining if this PixelData is to block or allow the player to pass/walk/jump
	 *         through. Returns true to block the player from walking from the player's last position to
	 *         this tile. Returns false to allow player to walk from the player's last position to this
	 *         tile.
	 */
	private boolean checkCharacterSurroundingData(Area area, int xOffset, int yOffset) {
		PixelData data = null;
		try {
			data = area.getPixelData(this.xAreaPosition + xOffset, this.yAreaPosition + yOffset);
		}
		catch (Exception e) {
			// This means it is out of the area boundaries.
			return true;
		}

		Player player = area.getPlayer();
		int x = player.getXInArea();
		int y = player.getYInArea();
		if ((x == this.oldXAreaPosition + xOffset && y == this.oldYAreaPosition + yOffset)) {
			return true;
		}

		int color = data.getColor();
		int alpha = (color >> 24) & 0xFF;
		int red = (color >> 16) & 0xFF;
		// int green = (color >> 8) & 0xFF;
		// int blue = color & 0xFF;
		switch (alpha) {
			case 0x01: // Paths
				return false;
			case 0x02: // Ledge
				return true;
			case 0x03: // Obstacle
				switch (red) {
					// Item types
					default:
						if (data.isHidden())
							return false;
						return true;
				}
			case 0x04: // Warp point
				return false;
			case 0x05: // Area Connection point.
				return false;
			case 0x06: // Stairs
				return false;
			case 0x07: // Water
				// TODO: Add something that detects a special boolean value in order to let the
				// player move on water.
				return false;
			case 0x08: // House
				return true;
			case 0x09: // House Door
				// TODO (6/18/2015): Door needs to be checked for null areas. If null areas are
				// found, default to locked doors.
				return false;
			case 0x0A: // Item
				if (data.isHidden())
					return false;
				return true;
			case 0x0B: // Carpet (Indoors)
			case 0x0C: // Carpet (Outdoors)
			case 0x0D: // Triggers
				return false;
			case 0x0E: // Characters / NPCs
				return true;
			default: // Any other type of tiles should be walkable, for no apparent reasons.
				return false;
		}
	}

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

	private void handleAutoWalking() {
		if (!this.isAutoWalking) {
			return;
		}
		int frequencyMask = this.getAutoWalkTickFrequency();
		if (frequencyMask <= 0)
			return;
		if (this.autoWalkingTick <= 0) {
			if (this.isLockedWalking || this.isInteracting())
				return;
			this.autoWalkingTick = Randomness.randInt() & frequencyMask;
			this.setFacing(Randomness.randDirection());
			this.isLockedWalking = Randomness.randBool();
		}
		else {
			this.autoWalkingTick--;
		}
	}

	// ----------------------------------------------------------------------
	// Static methods

	public static Character build(final Area area, final int pixel, final int x, final int y) {
		return Character.build(area, new PixelData(pixel, x, y), x, y);
	}

	public static Character build(final Area area, final PixelData pixelData, final int x, final int y) {
		Character result = null;
		int red = pixelData.getRed();
		switch (red) {
			case 0x1: {// Joe
				result = new Joe();
				result.setOriginPosition(x, y);
				result.setPixelData(pixelData);
				result.setArea(area);
				break;
			}
			default:
				break;
		}
		return result;
	}
}