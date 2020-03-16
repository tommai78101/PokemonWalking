/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package entity;

import abstracts.Character;
import interfaces.Tileable;
import level.Area;
import level.PixelData;
import main.Keys;
import resources.Art;
import screen.BaseScreen;

public class Player extends Character {
	public static boolean isMovementsLocked() {
		return movementLock;
	}

	public static void lockMovements() {
		movementLock = true;
	}

	public static void unlockMovements() {
		movementLock = false;
	}

	public Keys keys;
	private byte animationTick = 0;
	private byte animationPointer = 0;

	// These are based on the art sprite in the resource folder. The numbers are
	// used to get elements from a 2D array.
	private int walking = 0;
	private int xAccel;
	private int yAccel;
	private int oldXPosition;
	private int oldYPosition;
	private boolean isLockedWalking;
	private boolean isLockedSprinting;
	private boolean isLockedJumping;
	private final boolean[] isFacingBlocked = new boolean[4];
	private boolean isInWater;
	private boolean isOnBicycle;
	private boolean isColliding;
	private static boolean movementLock;

	private boolean isInteractionEnabled;
	private boolean jumpHeightSignedFlag = false;
	private int varyingJumpHeight = 0;
	private boolean automaticMode;

	// This variable is set to true, no matter what, in the Player class if the
	// player tries to do action that's not allowed.
	// It must be turned off (set to False) somewhere else in other classes. By
	// design!
	public boolean warningsTriggered;

	/**
	 * Constructs a Player object in the game. This must be loaded in ONCE.
	 * 
	 * @param Keys
	 *            Takes in the Keys object the input handler is controlling. It must not take in an uncontrolled Keys object.
	 */
	public Player(final Keys keys) {
		this.keys = keys;
		this.automaticMode = false;
		this.setCharacterPlayable(true);
	}

	/**
	 * Forces the player to continue to walk for more than 1 tile.
	 * 
	 * Used when the player has entered an entrance.
	 * 
	 * @return Nothing.
	 */
	public void forceLockWalking() {
		this.keys.resetInputs();
		this.isLockedWalking = true;
		this.isFacingBlocked[0] = this.isFacingBlocked[1] = this.isFacingBlocked[2] = this.isFacingBlocked[3] = false;

		if (this.xAccel == 0 && this.yAccel == 0) {
			switch (this.getFacing()) {
				case UP:
					this.yAccel--;
					break;
				case DOWN:
					this.yAccel++;
					break;
				case LEFT:
					this.xAccel--;
					break;
				case RIGHT:
					this.xAccel++;
					break;
			}
		}
	}

	/**
	 * Changes the player's state to Walking.
	 */
	public void getsOffBicycle() {
		Player.lockMovements();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(250);
				}
				catch (final InterruptedException e) {}
				isOnBicycle = false;
				try {
					Thread.sleep(250);
				}
				catch (final InterruptedException e) {}
				Player.unlockMovements();
			}
		}).start();
	}

	public int getXInArea() {
		// Returns area position X.
		int result = (this.xPosition / Tileable.WIDTH);
		if (this.isLockedWalking)
			switch (this.getFacing()) {
				case LEFT:
					break;
				case RIGHT:
					result += 1;
					break;
			}
		return result;
	}

	public int getYInArea() {
		// Returns area position Y.
		int result = (this.yPosition / Tileable.HEIGHT);
		if (this.isLockedWalking)
			switch (this.getFacing()) {
				case UP:
					break;
				case DOWN:
					result += 1;
					break;
			}
		return result;
	}

	/**
	 * Changes the player's state to Surfing.
	 */
	public void goesInWater() {
		this.isInWater = true;
		this.isOnBicycle = false;
	}

	/**
	 * Lets the player interact with the data tile ID.
	 * 
	 * @param dataColor
	 *            The tile ID's full data (the color of the tile).
	 */
	@Override
	public void interact(PixelData data) {
		final int dataColor = data.getColor();
		final int alpha = (dataColor >> 24) & 0xFF;
		// int red = (dataColor >> 16) & 0xFF;
		switch (alpha) {
			case 0x03: {// Obstacles
				// Red color values indicate the type of obstacles to filter:
				// case 0x00: // Small tree
				// case 0x01: //Logs
				// case 0x02: //Planks
				// case 0x03: //Scaffolding Left
				// case 0x04: //Scaffolding Right
				// case 0x05: //Sign
				if (this.keys.X.isTappedDown || this.keys.X.isPressedDown || this.keys.PERIOD.isTappedDown || this.keys.PERIOD.isPressedDown) {
					this.isInteractionEnabled = false;
					if (Player.isMovementsLocked())
						Player.unlockMovements();
					break;
				}
				if (!Player.isMovementsLocked()) {
					if (this.getInteractableID() != 0) {
						this.isInteractionEnabled = false;
						return;
					}
					if (!this.isInteractionEnabled)
						this.isInteractionEnabled = true;
				}
				if (this.isInteractionEnabled)
					this.setInteractableID(dataColor);
				break;
			}
			case 0x0A: {// Item
				// if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) &&
				// (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)){
				// this.keys.Z.lastKeyState = true;
				// this.keys.SLASH.lastKeyState = true;
				if (!Player.isMovementsLocked()) {
					if (this.getInteractableID() != 0) {
						this.isInteractionEnabled = false;
						return;
					}
					if (!this.isInteractionEnabled)
						this.isInteractionEnabled = true;
					// }
				}
				if (this.isInteractionEnabled)
					this.setInteractableID(dataColor);
				break;
			}
			default:
				// stopInteraction();
				break;
		}
	}

	public boolean isColliding() {
		return this.isColliding;
	}

	public boolean isFacingAt(final int x, final int y) {
		int xTgt = 0, yTgt = 0;
		switch (this.getFacing()) {
			case Player.UP:
				xTgt = this.getXInArea();
				yTgt = this.getYInArea() - 1;
				break;
			case Player.DOWN:
				xTgt = this.getXInArea();
				yTgt = this.getYInArea() + 1;
				break;
			case Player.LEFT:
				xTgt = this.getXInArea() - 1;
				yTgt = this.getYInArea();
				break;
			case Player.RIGHT:
				xTgt = this.getXInArea() + 1;
				yTgt = this.getYInArea();
				break;
		}
		return ((x == xTgt) && (y == yTgt));
	}

	public boolean isInteracting() {
		return this.isInteractionEnabled;
	}

	/**
	 * Returns the player's state (Surfing or Walking).
	 * 
	 * @return True, if player is surfing. False, if player is walking on land.
	 */
	public boolean isInWater() {
		return this.isInWater;
	}

	public boolean isLockedJumping() {
		return this.isLockedJumping;
	}

	public boolean isLockedSprinting() {
		return this.isLockedSprinting;
	}

	/**
	 * Checks to see if the player is currently locked to walking.
	 * 
	 * @return True, if the player is walking right now. False, otherwise.
	 */
	public boolean isLockedWalking() {
		return this.isLockedWalking;
	}

	/**
	 * Returns the player's state (Riding or Not Riding Bicycle).
	 * 
	 * @return True, if player is riding. False, if player is not.
	 */
	public boolean isRidingBicycle() {
		return this.isOnBicycle;
	}

	/**
	 * <p>
	 * Handles the 4 surrounding tiles around the player character, in the cardinal directions of north, west, south, and east. Once the player is interacting with one of the tiles, the area will remember and mark the tile's interaction ID, and pass it to the OverWorld to handle.
	 * </p>
	 * 
	 * @return Nothing.
	 */
	public void handleSurroundingTiles(Area area) {
		boolean upDirection = this.checkSurroundingData(area, 0, -1);
		boolean downDirection = this.checkSurroundingData(area, 0, 1);
		boolean leftDirection = this.checkSurroundingData(area, -1, 0);
		boolean rightDirection = this.checkSurroundingData(area, 1, 0);
		this.setAllBlockingDirections(upDirection, downDirection, leftDirection, rightDirection);

		int playerAreaX = this.getXInArea();
		int playerAreaY = this.getYInArea();

		try {
			if (this.isInteracting()) {
				switch (this.getFacing()) {
					case Player.UP:
						this.interact(area.getPixelData(playerAreaX, playerAreaY - 1));
						break;
					case Player.DOWN:
						this.interact(area.getPixelData(playerAreaX, playerAreaY + 1));
						break;
					case Player.LEFT:
						this.interact(area.getPixelData(playerAreaX - 1, playerAreaY));
						break;
					case Player.RIGHT:
						this.interact(area.getPixelData(playerAreaX + 1, playerAreaY));
						break;
				}
			}
		}
		catch (Exception e) {
			this.stopInteraction();
		}
	}

	/**
	 * Checks the pixel data and sets properties according to the documentation provided. The tile the pixel data is representing determines whether it should allow or block the player from walking towards it.
	 * 
	 * <p>
	 * In other words, this is the method call that works out the collision detection/response in the game.
	 * 
	 * @param xOffset
	 *            Sets the offset of the PixelData it should check by the X axis.
	 * @param yOffset
	 *            Sets the offset of the PixelData it should check by the Y axis.
	 * @return The value determining if this PixelData is to block or allow the player to pass/walk/jump through. Returns true to block the player from walking from the player's last position to this tile. Returns false to allow player to walk from the player's last position to this tile.
	 */
	public boolean checkSurroundingData(Area area, int xOffset, int yOffset) {
		PixelData data = null;
		int playerAreaX = this.getXInArea();
		int playerAreaY = this.getYInArea();
		try {
			data = area.getPixelData(playerAreaX + xOffset, playerAreaY + yOffset);
		}
		catch (Exception e) {
			// This means it is out of the area boundaries.
			data = null;
		}
		if (data == null)
			return true;
		int color = data.getColor();
		int alpha = (color >> 24) & 0xFF;
		int red = (color >> 16) & 0xFF;
		// int green = (color >> 8) & 0xFF;
		// int blue = color & 0xFF;
		switch (alpha) {
			case 0x01: // Paths
				return false;
			case 0x02: // Ledge
			{
				switch (red) {
					/*
					 * TODO: Incorporate pixel data facingsBlocked variable to this section.
					 * Currently, the facingsBlocked[] variable for each pixel data isn't used.
					 */
					case 0x00: { // Bottom
						int y = playerAreaY + yOffset;
						if (area.checkIfValuesAreAllowed((area.getTileColor(0, 2) >> 24) & 0xFF, 0x02, 0x03))
							return true;
						if (playerAreaY < y)
							return false;
						return true;
					}
					case 0x01: // Bottom Left
						return true;
					case 0x02: {// Left
						int x = playerAreaX + xOffset;
						if (area.checkIfValuesAreAllowed((area.getTileColor(-2, 0) >> 24) & 0xFF, 0x02, 0x03))
							return true;
						if (playerAreaX > x)
							return false;
						return true;
					}
					case 0x03: // Top Left
						return true;
					case 0x04: {// Top
						int y = playerAreaY + yOffset;
						if (playerAreaY > y)
							return false;
						if (area.checkIfValuesAreAllowed((area.getTileColor(0, -2) >> 24) & 0xFF, 0x02))
							return true;
						if (area.checkIfValuesAreAllowed((area.getTileColor(-1, 0) >> 16) & 0xFF, 0x04))
							return false;
						if (area.checkIfValuesAreAllowed((area.getTileColor(1, 0) >> 16) & 0xFF, 0x04))
							return false;
						if (area.checkIfValuesAreAllowed((area.getTileColor(0, -2) >> 24) & 0xFF, 0x03))
							return true;
						return true;
					}
					case 0x05: // Top Right
						return true;
					case 0x06: { // Right
						int x = playerAreaX + xOffset;
						if (area.checkIfValuesAreAllowed((area.getTileColor(2, 0) >> 24) & 0xFF, 0x02, 0x03))
							return true;
						if (playerAreaX < x)
							return false;
						return true;
					}
					case 0x07: // Bottom Right
						// TODO: DO SOMETHING WITH WATER, MAKE PLAYER SURF!
						return true;
					case 0x18: // Inner bottom left
					case 0x19: // Inner bottom right
						return true;

					// ------------------------- MOUNTAIN LEDGES ------------------------
					case 0x0C:
						int y = playerAreaY + yOffset;
						if (playerAreaY > y)
							return false;
						if (area.checkIfValuesAreAllowed((area.getTileColor(-1, 0) >> 16) & 0xFF, 0x0C))
							return false;
						if (area.checkIfValuesAreAllowed((area.getTileColor(1, 0) >> 16) & 0xFF, 0x0C))
							return false;
						return true;
					default:
						break;
				}
				break;
			}
			case 0x03: // Obstacle
				switch (red) {
					default:
						if (this.isInteracting())
							return true;
						if (this.isFacingAt(playerAreaX + xOffset, playerAreaY + yOffset)) {
							if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown)
								&& (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
								this.startInteraction();
								this.keys.Z.lastKeyState = true;
								this.keys.SLASH.lastKeyState = true;
							}
						}
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
				if (this.isInteracting())
					return true;
				if (this.isFacingAt(playerAreaX + xOffset, playerAreaY + yOffset)) {
					if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown)
						&& (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
						this.startInteraction();
						this.keys.Z.lastKeyState = true;
						this.keys.SLASH.lastKeyState = true;
					}
				}
				return true; // Cannot go through items on the ground.
			case 0x0B: // Carpet (Indoors)
			case 0x0C: // Carpet (Outdoors)
			case 0x0D: // Default starting point.
				return false;
			default: // Any other type of tiles should be walkable, for no apparent reasons.
				return false;
		}
		return true;
	}

	/**
	 * Changes the player's state to Walking.
	 */
	public void leavesWater() {
		this.isInWater = false;
		this.isOnBicycle = false;
	}

	public void reload() {
		this.isLockedWalking = false;
		this.isLockedJumping = false;
		this.isInteractionEnabled = false;
		this.animationTick = 0x7;
		this.setFacing(Player.DOWN);
	}

	/**
	 * Blits the entity onto the screen, being offsetted to the left, which fits snugly in the world grids.
	 * 
	 * @param output
	 *            Where the bitmap is to be blitted.
	 * @param x
	 *            Pixel X offset.
	 * @param y
	 *            Pixel Y offset.
	 * @return Nothing.
	 * 
	 */
	@Override
	public void render(final BaseScreen output, final int x, final int y) {
		if (this.isLockedJumping) {
			// Jumping has a higher priority than walking.
			output.blit(Art.shadow, this.xOffset + x, this.yOffset + y + 4);
			// Walking animation while in the air. Shouldn't jump when in water.
			if (this.isOnBicycle)
				output.npcBlit(Art.player_bicycle[walking][animationPointer], this.xOffset + x, this.yOffset + y - this.varyingJumpHeight);
			else
				output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y - this.varyingJumpHeight);
		}
		else if (this.isLockedWalking) {
			// Walking animation
			if (this.isInWater)
				output.npcBlit(Art.player_surf[walking][animationPointer], this.xOffset + x, this.yOffset + y);
			else if (this.isOnBicycle)
				output.npcBlit(Art.player_bicycle[walking][animationPointer], this.xOffset + x, this.yOffset + y);
			else
				output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y);
		}
		else {
			// Key press detection.
			final boolean wasdKeyPressedDown = (this.keys.S.isPressedDown || this.keys.W.isPressedDown || this.keys.A.isPressedDown || this.keys.D.isPressedDown);
			final boolean arrowKeyPressedDown = (this.keys.down.isPressedDown || this.keys.up.isPressedDown || this.keys.left.isPressedDown || this.keys.right.isPressedDown);
			final boolean canUserMove = (wasdKeyPressedDown || arrowKeyPressedDown) && !Player.movementLock;

			// Player state
			if (this.isInWater && this.isOnBicycle) {
				// Player has entered an impossible state.
				return;
			}

			// Blocking animation. Animation pointer index is reset to zero, to create a perfect loop.
			if (this.isInWater) {
				// Surfing (has higher priority than bicycling)
				if (canUserMove)
					output.npcBlit(Art.player_surf[this.getFacing()][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player_surf[this.getFacing()][0], this.xOffset + x, this.yOffset + y);
			}
			else if (this.isOnBicycle) {
				// Riding (has lower priority than surfing)
				if (canUserMove)
					output.npcBlit(Art.player_bicycle[this.getFacing()][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player_bicycle[this.getFacing()][0], this.xOffset + x, this.yOffset + y);
			}
			else {
				// Walking
				if (canUserMove)
					output.npcBlit(Art.player[this.getFacing()][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player[this.getFacing()][0], this.xOffset + x, this.yOffset + y);
			}
		}
	}

	/**
	 * Sets where each of the four directions are blocked by obstacles in front of the player. The obstacles are in front of the player, when the player is facing towards them. That is the time to check and see if the obstacle is blocking the player or not.
	 * 
	 * @param up
	 *            If an obstacle is in front of the player when the player is facing towards NORTH, or UP, then up is true. False, otherwise.
	 * @param down
	 *            If an obstacle is below of the player when the player is facing towards SOUTH, or DOWN, then down is true. False, otherwise.
	 * @param left
	 *            If an obstacle is to the left of the player when the player is facing towards WEST, or LEFT, then left is true. False, otherwise.
	 * @param right
	 *            If an obstacle is to the right of the player when the player is facing towards EAST, or RIGHT, then right is true. False, otherwise.
	 */
	public void setAllBlockingDirections(final boolean up, final boolean down, final boolean left, final boolean right) {
		this.isFacingBlocked[UP] = up;
		this.isFacingBlocked[DOWN] = down;
		this.isFacingBlocked[LEFT] = left;
		this.isFacingBlocked[RIGHT] = right;
	}

	/**
	 * Sets the player's current area position to the corresponding X and Y coordinates given.
	 * 
	 * <p>
	 * It uses the 2D Cartesian coordinates used in bitmaps. Positive X: Right. Positive Y: Down.
	 * 
	 * <p>
	 * <i>Note that the player's X and Y positions are overwritten by the loading system.</i>
	 * 
	 * @param x
	 *            The X coordinate the player is to be positioned at.
	 * @param y
	 *            The Y coordinate the player is to be positioned at.
	 * @return Nothing.
	 */
	public void setAreaPosition(final int x, final int y) {
		this.setPosition(x * Tileable.WIDTH, y * Tileable.HEIGHT);
	}

	/**
	 * Same with "setAreaPosition(int x, int y)", but only setting the X position.
	 * 
	 * @param x
	 *            The X coordinate the player is to be positioned at.
	 * @return Nothing.
	 */
	public void setAreaX(final int x) {
		this.xPosition = x * Tileable.WIDTH;
	}

	/**
	 * Same with "setAreaPosition(int x, int y)", but only setting the Y position.
	 * 
	 * @param y
	 *            The Y coordinate the player is to be positioned at.
	 * @return Nothing.
	 */
	public void setAreaY(final int y) {
		this.yPosition = y * Tileable.HEIGHT;
	}

	/**
	 * Moves the Player object to the center of the screen.
	 * 
	 * @param BaseScreen
	 *            Pans the screen immediately so that the Player object is in the center of the screen.
	 * @return Nothing.
	 */
	public void setCenterCamPosition(final BaseScreen screen) {
		this.setRenderOffset(screen.getWidth() / 2 - Tileable.WIDTH, (screen.getHeight() - Tileable.HEIGHT) / 2);
	}

	/**
	 * Locks the player into a jumping state. In this state, the Player cannot listen to any key inputs received during the jump.
	 * <p>
	 * 
	 * Note: An example on how to determine player direction for the tile to allow and block:
	 * <ul>
	 * Let's say the tile, X, is located at (1, 1), if using bitmap coordinates. If the tile allows the player to jump from top to bottom, the parameters, "from" and "to" would be Player.UP and Player.DOWN respectively, which is the UP tile at (1, 0) and DOWN tile at (1, 2). It means, the tile above
	 * X is the UP position of X, and the tile below X is the DOWN position of X. Therefore, X allows the player on the tile above X (the UP tile) to jump across to the tile below X, but not the other way around.
	 * </ul>
	 * 
	 * Parameters must be either Player.UP, Player.DOWN, Player.LEFT, or Player.RIGHT.
	 * <p>
	 * 
	 * @param red
	 *            The red value of the pixel color.
	 * @param green
	 *            The green value of the pixel color.
	 * @param blue
	 *            The blue value of the pixel color.
	 * @param from
	 *            The player direction the tile allows the player to jump from. Player direction is determined from where the tile is located. The player direction must not be the same as the "to" parameter.
	 * @param to
	 *            The player direction the tile allows the player to jump to. Player direction is determined from where the tile is located. The player direction must not be the same as the "from" parameter.
	 * @return Nothing.
	 */
	public void setLockJumping(final int red, final int green, final int blue, final int from, final int to) {
		if (from == to)
			throw new IllegalArgumentException("The parameters, from and to, must not be the same.");
		switch (red) {
			case 0x00: // Bottom
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] =
				// this.facingsBlocked[3] = true;
				this.isFacingBlocked[DOWN] = this.isFacingBlocked[LEFT] = this.isFacingBlocked[RIGHT] = true;
				this.isFacingBlocked[UP] = false;
				this.isLockedJumping = true;
				break;
			case 0x01: // Bottom Left
				this.isFacingBlocked[DOWN] = this.isFacingBlocked[LEFT] = this.isFacingBlocked[RIGHT] = this.isFacingBlocked[UP] = true;
				break;
			case 0x02: // Left
				this.isFacingBlocked[DOWN] = this.isFacingBlocked[LEFT] = this.isFacingBlocked[UP] = true;
				this.isFacingBlocked[LEFT] = false;
				this.isLockedJumping = true;
				break;
			case 0x03: // Top Left
				this.isFacingBlocked[DOWN] = this.isFacingBlocked[LEFT] = this.isFacingBlocked[RIGHT] = this.isFacingBlocked[UP] = true;
				break;
			case 0x04: // Top
				this.isFacingBlocked[UP] = this.isFacingBlocked[LEFT] = this.isFacingBlocked[RIGHT] = true;
				this.isFacingBlocked[DOWN] = false;
				this.isLockedJumping = true;
				break;
			case 0x05: // Top Right
				this.isFacingBlocked[DOWN] = this.isFacingBlocked[LEFT] = this.isFacingBlocked[RIGHT] = this.isFacingBlocked[UP] = true;
				break;
			case 0x06: // Right
				this.isFacingBlocked[DOWN] = this.isFacingBlocked[UP] = this.isFacingBlocked[LEFT] = true;
				this.isFacingBlocked[RIGHT] = false;
				this.isLockedJumping = true;
				break;
			case 0x07: // Bottom Right
				this.isFacingBlocked[DOWN] = this.isFacingBlocked[LEFT] = this.isFacingBlocked[RIGHT] = this.isFacingBlocked[UP] = true;
				break;
			default: // Any other tiles should not cause the player to jump.
				this.isFacingBlocked[0] = this.isFacingBlocked[1] = this.isFacingBlocked[2] = this.isFacingBlocked[3] = true;
				this.isLockedJumping = false;
				break;
		}
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRenderOffset(final int x, final int y) {
		this.xOffset = x;
		this.yOffset = y;
	}

	public void startInteraction() {
		if (this.isInteractionEnabled)
			return;
		this.isInteractionEnabled = true;
		this.setInteractableID(0);
	}

	// -------------------------------------------------------------------------------------
	// Private methods

	/**
	 * Changes the player's state to Riding.
	 */
	public void startsRidingBicycle() {
		if (!this.isInWater) {
			Player.lockMovements();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(250);
					}
					catch (final InterruptedException e) {}
					Player.this.isOnBicycle = true;
					try {
						Thread.sleep(250);
					}
					catch (final InterruptedException e) {}
					Player.unlockMovements();
				}
			}).start();
		}
		else
			this.warningsTriggered = true;
	}

	public void stopAnimation() {
		this.animationTick = 0;
		this.animationPointer = 0;
	}

	public void stopInteraction() {
		this.isInteractionEnabled = false;
	}

	public void enableAutomaticMode() {
		this.automaticMode = true;
	}

	public void disableAutomaticMode() {
		this.automaticMode = false;
	}

	// ---------------------------------------------------------------------
	// Override methods

	@Override
	public void tick() {
		if (this.automaticMode) {
			this.keys.resetInputs();
			handleFacingCheck();
		}
		if (!this.isLockedJumping) {
			if (!this.isInteractionEnabled) {
				input();
				handleMovement();
				controlTick();
			}
			else {
				stopAnimation();
			}
		}
		else
			jump();
	}

	// ---------------------------------------------------------------------
	// Private methods

	private void checkFacingInput() {
		if (this.keys.up.isTappedDown || this.keys.up.isPressedDown || this.keys.W.isTappedDown || this.keys.W.isPressedDown) {
			this.setFacing(Player.UP);
		}
		else if (this.keys.down.isTappedDown || this.keys.down.isPressedDown || this.keys.S.isTappedDown || this.keys.S.isPressedDown) {
			this.setFacing(Player.DOWN);
		}
		else if (this.keys.left.isTappedDown || this.keys.left.isPressedDown || this.keys.A.isTappedDown || this.keys.A.isPressedDown) {
			this.setFacing(Player.LEFT);
		}
		else if (this.keys.right.isTappedDown || this.keys.right.isPressedDown || this.keys.D.isTappedDown || this.keys.D.isPressedDown) {
			this.setFacing(Player.RIGHT);
		}
		else {
			// Do nothing with the directional facing here.
		}
	}

	private void controlTick() {
		if (!this.isLockedWalking || !this.isLockedJumping) {
			animationTick++;
			if ((this.getFacing() == UP && this.isFacingBlocked[UP])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			}
			else if ((this.getFacing() == DOWN && this.isFacingBlocked[DOWN])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			}
			else if ((this.getFacing() == LEFT && this.isFacingBlocked[LEFT])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			}
			else if ((this.getFacing() == RIGHT && this.isFacingBlocked[RIGHT])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			}
			else {
				if (animationTick >= 4)
					animationTick = 0;
			}
			if (animationTick == 0) {
				animationPointer++;
				if (animationPointer > 3)
					animationPointer = 0;
			}
		}
	}

	private void handleFacingCheck() {
		if (this.walking != this.getFacing()) {
			this.walking = this.getFacing();
		}
	}

	/**
	 * Makes adjustments to the player's position when the player is walking.
	 * 
	 * <p>
	 * If the conditions are met, such as a tile has been fully moved to, it will check to make sure the player has stopped walking, until the player wanted to walk.
	 * 
	 * @return Nothing.
	 */
	private void handleMovement() {
		// Check if player is currently locked to walking.
		if (this.isLockedWalking) {
			this.walk();
		}
		else if (this.isLockedSprinting) {
			this.sprint();
		}
		else if (this.isOnBicycle) {
			this.ride();
		}
		else {
			// Before we walk, check to see if the oldX and oldY are up-to-date with the
			// latest X and Y.
			if (this.oldXPosition != this.xPosition)
				this.oldXPosition = this.xPosition;
			if (this.oldYPosition != this.yPosition)
				this.oldYPosition = this.yPosition;

			// Reset the acceleration values, since we're not really walking.
			xAccel = 0;
			yAccel = 0;

			// Check for inputs the player wants to face. Tapping in a direction turns the
			// player around.
			if (!movementLock)
				checkFacingInput();

			// Now about to walk. First, check to see if there's an obstacle blocking the
			// path.
			if (this.isFacingBlocked[this.getFacing()]) {
				this.isLockedWalking = false;
			}
		}
	}

	@Override
	public void jump() {
		if (this.isLockedJumping) {
			// When being locked to walking, facing must stay constant.
			if (this.walking != this.getFacing())
				this.walking = this.getFacing();

			// Also make sure it's currently not being blocked by anything (You're in the
			// air)
			this.isFacingBlocked[0] = this.isFacingBlocked[1] = this.isFacingBlocked[2] = this.isFacingBlocked[3] = false;

			// Makes sure the acceleration stays limited to 1 pixel/tick.
			if (xAccel > 1)
				xAccel = 1;
			if (xAccel < -1)
				xAccel = -1;
			if (yAccel > 1)
				yAccel = 1;
			if (yAccel < -1)
				yAccel = -1;

			xPosition += xAccel * 2;
			yPosition += yAccel * 2;

			// Jumping stuffs go here.
			if (!this.jumpHeightSignedFlag)
				this.varyingJumpHeight++;
			else
				this.varyingJumpHeight--;
			if (this.varyingJumpHeight >= 10.0)
				this.jumpHeightSignedFlag = true;

			// Needs to get out of being locked to walking/jumping.
			// Note that we cannot compare using ||, what if the player is moving in one
			// direction? What about the other axis?
			if ((xPosition % Tileable.WIDTH == 0 && yPosition % Tileable.HEIGHT == 0)) {
				// Resets every flag that locks the player.
				this.isLockedWalking = false;
				this.isLockedJumping = false;
				if (this.jumpHeightSignedFlag) {
					this.jumpHeightSignedFlag = false;
					this.varyingJumpHeight = 0;
				}
			}
		}
		controlTick();
	}

	private void pressed() {
		xAccel = yAccel = 0;
		if (this.keys.up.isPressedDown || this.keys.W.isPressedDown) {
			if (this.getFacing() != Player.UP) {
				this.setFacing(Player.UP);
			}
			if (!this.isFacingBlocked[UP]) {
				this.isLockedWalking = true;
				yAccel--;
			}
		}
		else if (this.keys.down.isPressedDown || this.keys.S.isPressedDown) {
			if (this.getFacing() != Player.DOWN) {
				this.setFacing(Player.DOWN);
			}
			if (!this.isFacingBlocked[Player.DOWN]) {
				this.isLockedWalking = true;
				yAccel++;
			}
		}
		else if (this.keys.left.isPressedDown || this.keys.A.isPressedDown) {
			if (this.getFacing() != Player.LEFT) {
				this.setFacing(Player.LEFT);
			}
			if (!this.isFacingBlocked[Player.LEFT]) {
				this.isLockedWalking = true;
				xAccel--;
			}
		}
		else if (this.keys.right.isPressedDown || this.keys.D.isPressedDown) {
			if (this.getFacing() != Player.RIGHT) {
				this.setFacing(Player.RIGHT);
			}
			if (!this.isFacingBlocked[Player.RIGHT]) {
				this.isLockedWalking = true;
				xAccel++;
			}
		}
	}

	private void tapped() {
		animationTick = 0;
		animationPointer = 0;
		if (this.keys.up.isTappedDown || this.keys.W.isTappedDown) {
			if (this.getFacing() != Player.UP) {
				this.setFacing(Player.UP);
			}
		}
		else if (this.keys.down.isTappedDown || this.keys.S.isTappedDown) {
			if (this.getFacing() != Player.DOWN) {
				this.setFacing(Player.DOWN);
			}
		}
		else if (this.keys.left.isTappedDown || this.keys.A.isTappedDown) {
			if (this.getFacing() != Player.LEFT) {
				this.setFacing(Player.LEFT);
			}
		}
		else if (this.keys.right.isTappedDown || this.keys.D.isTappedDown) {
			if (this.getFacing() != Player.RIGHT) {
				this.setFacing(Player.RIGHT);
			}
		}
	}

	public void input() {
		this.isColliding = false;
		if (!this.isLockedWalking) {
			if (!this.isFacingBlocked[UP] && !movementLock) {
				if (this.keys.up.isTappedDown || this.keys.W.isTappedDown)
					tapped();
				else if (this.keys.up.isPressedDown || this.keys.W.isPressedDown) {
					pressed();
					return;
				}
			}
			else if (this.keys.up.isPressedDown || this.keys.W.isPressedDown) {
				this.isColliding = true;
				return;
			}
			if (!this.isFacingBlocked[DOWN] && !movementLock) {
				if (this.keys.down.isTappedDown || this.keys.S.isTappedDown)
					tapped();
				else if (this.keys.down.isPressedDown || this.keys.S.isPressedDown) {
					pressed();
					return;
				}
			}
			else if (this.keys.down.isPressedDown || this.keys.S.isPressedDown) {
				this.isColliding = true;
				return;
			}
			if (!this.isFacingBlocked[LEFT] && !movementLock) {
				if (this.keys.left.isTappedDown || this.keys.A.isTappedDown)
					tapped();
				else if (this.keys.left.isPressedDown || this.keys.A.isPressedDown) {
					pressed();
					return;
				}
			}
			else if (this.keys.left.isPressedDown || this.keys.A.isPressedDown) {
				this.isColliding = true;
				return;
			}
			if (!this.isFacingBlocked[RIGHT] && !movementLock) {
				if (this.keys.right.isTappedDown || this.keys.D.isTappedDown)
					tapped();
				else if (this.keys.right.isPressedDown || this.keys.D.isPressedDown) {
					pressed();
					return;
				}
			}
			else if (this.keys.right.isPressedDown || this.keys.D.isPressedDown) {
				this.isColliding = true;
				return;
			}
		}
	}

	@Override
	public void walk() {
		// When being locked to walking, facing must stay constant.
		handleFacingCheck();

		// Makes sure the acceleration stays limited to 1 pixel/tick.
		if (this.xAccel > 1)
			this.xAccel = 1;
		if (this.xAccel < -1)
			this.xAccel = -1;
		if (this.yAccel > 1)
			this.yAccel = 1;
		if (this.yAccel < -1)
			this.yAccel = -1;

		if (!this.isOnBicycle && !Player.isMovementsLocked()) {
			this.xPosition += this.xAccel * 2;
			this.yPosition += this.yAccel * 2;
		}

		// Needs to get out of being locked to walking/jumping.
		// Note that we cannot compare using ||, what if the player is moving in one
		// direction? What about the other axis?
		if ((this.xPosition % Tileable.WIDTH == 0 && this.yPosition % Tileable.HEIGHT == 0)) {
			// Resets every flag that locks the player.
			this.isLockedWalking = false;
		}
	}

	@Override
	public void sprint() {
		// When being locked to walking, facing must stay constant.
		handleFacingCheck();

		// Makes sure the acceleration stays limited to 1 pixel/tick.
		if (this.xAccel > 1)
			this.xAccel = 1;
		if (this.xAccel < -1)
			this.xAccel = -1;
		if (this.yAccel > 1)
			this.yAccel = 1;
		if (this.yAccel < -1)
			this.yAccel = -1;

		if (!this.isOnBicycle && !Player.isMovementsLocked()) {
			this.xPosition += this.xAccel * 4;
			this.yPosition += this.yAccel * 4;
		}

		// Needs to get out of being locked to walking/jumping.
		// Note that we cannot compare using ||, what if the player is moving in one
		// direction? What about the other axis?
		if ((this.xPosition % Tileable.WIDTH == 0 && this.yPosition % Tileable.HEIGHT == 0)) {
			// Resets every flag that locks the player.
			this.isLockedSprinting = false;
		}
	}

	@Override
	public void ride() {
		// When being locked to walking, facing must stay constant.
		handleFacingCheck();

		// Makes sure the acceleration stays limited to 1 pixel/tick.
		if (this.xAccel > 1)
			this.xAccel = 1;
		if (this.xAccel < -1)
			this.xAccel = -1;
		if (this.yAccel > 1)
			this.yAccel = 1;
		if (this.yAccel < -1)
			this.yAccel = -1;

		if (this.isOnBicycle && !Player.isMovementsLocked()) {
			this.xPosition += this.xAccel * 8;
			this.yPosition += this.yAccel * 8;
		}

		// Needs to get out of being locked to walking/jumping.
		// Note that we cannot compare using ||, what if the player is moving in one
		// direction? What about the other axis?
		if ((this.xPosition % Tileable.WIDTH == 0 && this.yPosition % Tileable.HEIGHT == 0)) {
			// Resets every flag that locks the player.
			this.isLockedWalking = false;
		}
	}

	@Override
	public void swim() {}
}