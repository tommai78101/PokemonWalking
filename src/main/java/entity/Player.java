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

import level.Area;
import main.Keys;
import resources.Art;
import screen.BaseScreen;
import abstracts.Entity;
import abstracts.Tile;
import interfaces.Gender;

public class Player extends Entity implements Gender {
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
	private Gender.GenderType gender = GenderType.Nondetermined;

	// These are based on the art sprite in the resource folder. The numbers are
	// used to get elements from a 2D array.
	private int lastFacing = 0;
	private int walking = 0;
	private int xAccel;
	private int yAccel;
	private int oldXPosition;
	private int oldYPosition;
	private boolean lockWalking;
	private boolean lockJumping;
	private boolean[] facingsBlocked = new boolean[4];
	private boolean isInWater;
	private boolean isOnBicycle;
	private boolean isColliding;
	private static boolean movementLock;
	private int interactionID;
	private boolean enableInteraction;
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
	 * @param Keys Takes in the Keys object the input handler is controlling. It
	 *             must not take in an uncontrolled Keys object.
	 */
	public Player(Keys keys) {
		this.keys = keys;
		this.automaticMode = false;
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
		this.lockWalking = true;
		this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;

		if (this.xAccel == 0 && this.yAccel == 0) {
			switch (this.facing) {
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

	public int getInteractionID() {
		return this.interactionID;
	}

	/**
	 * Gets a value that determines the direction the player had last been facing
	 * towards at.
	 * 
	 * @return An integer of one of the followings: Player.UP, Player.DOWN,
	 *         Player.LEFT, Player.RIGHT.
	 */
	public int getLastFacing() {
		return lastFacing;
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
				} catch (InterruptedException e) {
				}
				isOnBicycle = false;
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				Player.unlockMovements();
			}
		}).start();
	}

	public int getXInArea() {
		// Returns area position X.
		int result = (xPosition / Tile.WIDTH);
		if (this.lockWalking)
			switch (facing) {
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
		int result = (yPosition / Tile.HEIGHT);
		if (this.lockWalking)
			switch (facing) {
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

	public boolean hasChangedFacing() {
		// True, if current facing has been changed.
		return this.lastFacing != this.facing;
	}

	public void initialize(Area area) {
		area.setPlayerX(this.getXInArea());
		area.setPlayerY(this.getYInArea());
	}

	/**
	 * Lets the player interact with the data tile ID.
	 * 
	 * @param dataColor The tile ID's full data (the color of the tile).
	 */
	public void interact(int dataColor) {
		int alpha = (dataColor >> 24) & 0xFF;
		// int red = (dataColor >> 16) & 0xFF;
		switch (alpha) {
		case 0x03: {// Obstacles
			// case 0x00: // Small tree
			// case 0x01: //Logs
			// case 0x02: //Planks
			// case 0x03: //Scaffolding Left
			// case 0x04: //Scaffolding Right
			// case 0x05: //Sign
			if (this.keys.X.isTappedDown || this.keys.X.isPressedDown || this.keys.PERIOD.isTappedDown
					|| this.keys.PERIOD.isPressedDown) {
				this.enableInteraction = false;
				if (Player.isMovementsLocked())
					Player.unlockMovements();
				break;
			}
			if (!movementLock) {
				if (this.interactionID != 0) {
					this.enableInteraction = false;
					return;
				}
				if (!this.enableInteraction)
					this.enableInteraction = true;
			}
			if (this.enableInteraction)
				this.interactionID = dataColor;
			break;
		}
		case 0x0A: {// Item
			// if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) &&
			// (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)){
			// this.keys.Z.lastKeyState = true;
			// this.keys.SLASH.lastKeyState = true;
			if (!movementLock) {
				if (this.interactionID != 0) {
					this.enableInteraction = false;
					return;
				}
				if (!this.enableInteraction)
					this.enableInteraction = true;
				// }
			}
			if (this.enableInteraction)
				this.interactionID = dataColor;
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

	public boolean isFacingAt(int x, int y) {
		int xTgt = 0, yTgt = 0;
		switch (this.facing) {
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
		return this.enableInteraction;
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
		return this.lockJumping;
	}

	/**
	 * Checks to see if the player is currently locked to walking.
	 * 
	 * @return True, if the player is walking right now. False, otherwise.
	 */
	public boolean isLockedWalking() {
		return this.lockWalking;
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
	 * Changes the player's state to Walking.
	 */
	public void leavesWater() {
		this.isInWater = false;
		this.isOnBicycle = false;
	}

	public void reload() {
		this.lockWalking = false;
		this.lockJumping = false;
		this.enableInteraction = false;
		this.facing = Player.DOWN;
		this.animationTick = 0x7;
	}

	/**
	 * Blits the entity onto the screen, being offsetted to the left, which fits
	 * snugly in the world grids.
	 * 
	 * @param output Where the bitmap is to be blitted.
	 * @param x      Pixel X offset.
	 * @param y      Pixel Y offset.
	 * @return Nothing.
	 * 
	 */
	@Override
	public void render(BaseScreen output, int x, int y) {
		if (this.lockWalking && !this.lockJumping) {
			// Walking animation
			if (this.isInWater)
				output.npcBlit(Art.player_surf[walking][animationPointer], this.xOffset + x, this.yOffset + y);
			else if (this.isOnBicycle)
				output.npcBlit(Art.player_bicycle[walking][animationPointer], this.xOffset + x, this.yOffset + y);
			else
				output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y);
		} else if (this.lockJumping) {
			output.blit(Art.shadow, this.xOffset + x, this.yOffset + y + 4);
			// Walking animation while in the air. Shouldn't jump when in water.
			if (this.isOnBicycle)
				output.npcBlit(Art.player_bicycle[walking][animationPointer], this.xOffset + x,
						this.yOffset + y - this.varyingJumpHeight);
			else
				output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x,
						this.yOffset + y - this.varyingJumpHeight);

		} else {
			// Blocking animation. Possibly done to create a perfect loop.
			if (!this.isInWater && !this.isOnBicycle) {
				// Walking
				if ((keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown
						|| keys.right.isPressedDown || keys.S.isPressedDown || keys.W.isPressedDown
						|| keys.A.isPressedDown || keys.D.isPressedDown) && !Player.movementLock)
					output.npcBlit(Art.player[facing][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player[facing][0], this.xOffset + x, this.yOffset + y);
			} else if (this.isInWater && !this.isOnBicycle) {
				// Surfing
				if ((keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown
						|| keys.right.isPressedDown || keys.S.isPressedDown || keys.W.isPressedDown
						|| keys.A.isPressedDown || keys.D.isPressedDown) && !Player.movementLock)
					output.npcBlit(Art.player_surf[facing][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player_surf[facing][0], this.xOffset + x, this.yOffset + y);
			} else if (!this.isInWater && this.isOnBicycle) {
				// Riding
				if ((keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown
						|| keys.right.isPressedDown || keys.S.isPressedDown || keys.W.isPressedDown
						|| keys.A.isPressedDown || keys.D.isPressedDown) && !Player.movementLock)
					output.npcBlit(Art.player_bicycle[facing][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player_bicycle[facing][0], this.xOffset + x, this.yOffset + y);
			}
		}
	}

	/**
	 * Sets where each of the four directions are blocked by obstacles in front of
	 * the player. The obstacles are in front of the player, when the player is
	 * facing towards them. That is the time to check and see if the obstacle is
	 * blocking the player or not.
	 * 
	 * @param up    If an obstacle is in front of the player when the player is
	 *              facing towards NORTH, or UP, then up is true. False, otherwise.
	 * @param down  If an obstacle is below of the player when the player is facing
	 *              towards SOUTH, or DOWN, then down is true. False, otherwise.
	 * @param left  If an obstacle is to the left of the player when the player is
	 *              facing towards WEST, or LEFT, then left is true. False,
	 *              otherwise.
	 * @param right If an obstacle is to the right of the player when the player is
	 *              facing towards EAST, or RIGHT, then right is true. False,
	 *              otherwise.
	 */
	public void setAllBlockingDirections(boolean up, boolean down, boolean left, boolean right) {
		this.facingsBlocked[UP] = up;
		this.facingsBlocked[DOWN] = down;
		this.facingsBlocked[LEFT] = left;
		this.facingsBlocked[RIGHT] = right;
	}

	/**
	 * Sets the player's current area position to the corresponding X and Y
	 * coordinates given.
	 * 
	 * <p>
	 * It uses the 2D Cartesian coordinates used in bitmaps. Positive X: Right.
	 * Positive Y: Down.
	 * 
	 * <p>
	 * <i>Note that the player's X and Y positions are overwritten by the loading
	 * system.</i>
	 * 
	 * @param x The X coordinate the player is to be positioned at.
	 * @param y The Y coordinate the player is to be positioned at.
	 * @return Nothing.
	 */
	public void setAreaPosition(int x, int y) {
		this.setPosition(x * Tile.WIDTH, y * Tile.HEIGHT);
	}

	/**
	 * Moves the Player object to the center of the screen.
	 * 
	 * @param BaseScreen Pans the screen immediately so that the Player object is in
	 *                   the center of the screen.
	 * @return Nothing.
	 */
	public void setCenterCamPosition(BaseScreen screen) {
		this.setRenderOffset(screen.getWidth() / 2 - Tile.WIDTH, (screen.getHeight() - Tile.HEIGHT) / 2);
	}

	/**
	 * Locks the player into a jumping state. In this state, the Player cannot
	 * listen to any key inputs received during the jump.
	 * <p>
	 * 
	 * Note: An example on how to determine player direction for the tile to allow
	 * and block:
	 * <ul>
	 * Let's say the tile, X, is located at (1, 1), if using bitmap coordinates. If
	 * the tile allows the player to jump from top to bottom, the parameters, "from"
	 * and "to" would be Player.UP and Player.DOWN respectively, which is the UP
	 * tile at (1, 0) and DOWN tile at (1, 2). It means, the tile above X is the UP
	 * position of X, and the tile below X is the DOWN position of X. Therefore, X
	 * allows the player on the tile above X (the UP tile) to jump across to the
	 * tile below X, but not the other way around.
	 * </ul>
	 * 
	 * Parameters must be either Player.UP, Player.DOWN, Player.LEFT, or
	 * Player.RIGHT.
	 * <p>
	 * 
	 * @param red   The red value of the pixel color.
	 * @param green The green value of the pixel color.
	 * @param blue  The blue value of the pixel color.
	 * @param from  The player direction the tile allows the player to jump from.
	 *              Player direction is determined from where the tile is located.
	 *              The player direction must not be the same as the "to" parameter.
	 * @param to    The player direction the tile allows the player to jump to.
	 *              Player direction is determined from where the tile is located.
	 *              The player direction must not be the same as the "from"
	 *              parameter.
	 * @return Nothing.
	 */
	public void setLockJumping(int red, int green, int blue, int from, int to) {
		if (from == to)
			throw new IllegalArgumentException("The parameters, from and to, must not be the same.");
		switch (red) {
		case 0x00: // Bottom
			// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] =
			// this.facingsBlocked[3] = true;
			this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = true;
			this.facingsBlocked[UP] = false;
			this.lockJumping = true;
			break;
		case 0x01: // Bottom Left
			this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
			break;
		case 0x02: // Left
			this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[UP] = true;
			this.facingsBlocked[LEFT] = false;
			this.lockJumping = true;
			break;
		case 0x03: // Top Left
			this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
			break;
		case 0x04: // Top
			this.facingsBlocked[UP] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = true;
			this.facingsBlocked[DOWN] = false;
			this.lockJumping = true;
			break;
		case 0x05: // Top Right
			this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
			break;
		case 0x06: // Right
			this.facingsBlocked[DOWN] = this.facingsBlocked[UP] = this.facingsBlocked[LEFT] = true;
			this.facingsBlocked[RIGHT] = false;
			this.lockJumping = true;
			break;
		case 0x07: // Bottom Right
			this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
			break;
		default: // Any other tiles should not cause the player to jump.
			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
			this.lockJumping = false;
			break;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRenderOffset(int x, int y) {
		this.xOffset = x;
		this.yOffset = y;
	}

	public void startInteraction() {
		if (this.enableInteraction)
			return;
		this.enableInteraction = true;
		this.interactionID = 0;
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
					} catch (InterruptedException e) {
					}
					isOnBicycle = true;
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
					}
					Player.unlockMovements();
				}
			}).start();
		} else
			this.warningsTriggered = true;
	}

	public void stopAnimation() {
		this.animationTick = 0;
		this.animationPointer = 0;
	}

	public void stopInteraction() {
		this.enableInteraction = false;
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
		if (!this.lockJumping) {
			if (!this.enableInteraction) {
				walk();
				handleMovementCheck();
				controlTick();
			} else {
				stopAnimation();
			}
		} else
			jump();
	}

	@Override
	public void setGender(GenderType value) {
		this.gender = value;
	}

	@Override
	public GenderType getGender() {
		return this.gender;
	}

	// ---------------------------------------------------------------------
	// Private methods

	private void checkFacing() {
		if (keys.up.isTappedDown || keys.up.isPressedDown || keys.W.isTappedDown || keys.W.isPressedDown) {
			facing = UP;
		} else if (keys.down.isTappedDown || keys.down.isPressedDown || keys.S.isTappedDown || keys.S.isPressedDown) {
			facing = DOWN;
		} else if (keys.left.isTappedDown || keys.left.isPressedDown || keys.A.isTappedDown || keys.A.isPressedDown) {
			facing = LEFT;
		} else if (keys.right.isTappedDown || keys.right.isPressedDown || keys.D.isTappedDown || keys.D.isPressedDown) {
			facing = RIGHT;
		}
	}

	private void controlTick() {
		if (!this.lockWalking || !this.lockJumping) {
			animationTick++;
			if ((this.facing == UP && this.facingsBlocked[UP])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			} else if ((this.facing == DOWN && this.facingsBlocked[DOWN])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			} else if ((this.facing == LEFT && this.facingsBlocked[LEFT])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			} else if ((this.facing == RIGHT && this.facingsBlocked[RIGHT])) {
				if (animationTick >= 10) {
					animationTick = 0;
				}
			} else {
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
		if (this.walking != this.facing) {
			this.walking = this.facing;
		}
	}

	/**
	 * Makes adjustments to the player's position when the player is walking.
	 * 
	 * <p>
	 * If the conditions are met, such as a tile has been fully moved to, it will
	 * check to make sure the player has stopped walking, until the player wanted to
	 * walk.
	 * 
	 * @return Nothing.
	 */
	private void handleMovementCheck() {
		// Check if player is currently locked to walking.
		if (this.lockWalking) {
			// When being locked to walking, facing must stay constant.
			handleFacingCheck();

			// Makes sure the acceleration stays limited to 1 pixel/tick.
			if (xAccel > 1)
				xAccel = 1;
			if (xAccel < -1)
				xAccel = -1;
			if (yAccel > 1)
				yAccel = 1;
			if (yAccel < -1)
				yAccel = -1;

			if (!this.isOnBicycle && !Player.isMovementsLocked()) {
				xPosition += xAccel * 2;
				yPosition += yAccel * 2;
			} else {
				xPosition += xAccel * 4;
				yPosition += yAccel * 4;
			}

			// Needs to get out of being locked to walking/jumping.
			// Note that we cannot compare using ||, what if the player is moving in one
			// direction? What about the other axis?
			if ((xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0)) {
				// Resets every flag that locks the player.
				this.lockWalking = false;
			}
		} else {
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
				checkFacing();

			// Now about to walk. First, check to see if there's an obstacle blocking the
			// path.
			if (this.facingsBlocked[this.facing]) {
				this.lockWalking = false;
			}
		}
	}

	private void jump() {
		if (this.lockJumping) {
			// When being locked to walking, facing must stay constant.
			if (this.walking != this.facing)
				this.walking = this.facing;

			// Also make sure it's currently not being blocked by anything (You're in the
			// air)
			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;

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
			if ((xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0)) {
				// Resets every flag that locks the player.
				this.lockWalking = false;
				this.lockJumping = false;
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
		if (keys.up.isPressedDown || keys.W.isPressedDown) {
			if (facing != UP) {
				facing = UP;
			}
			if (!this.facingsBlocked[UP]) {
				this.lockWalking = true;
				yAccel--;
			}
		} else if (keys.down.isPressedDown || keys.S.isPressedDown) {
			if (facing != DOWN) {
				facing = DOWN;
			}
			if (!this.facingsBlocked[DOWN]) {
				this.lockWalking = true;
				yAccel++;
			}
		} else if (keys.left.isPressedDown || keys.A.isPressedDown) {
			if (facing != LEFT) {
				facing = LEFT;
			}
			if (!this.facingsBlocked[LEFT]) {
				this.lockWalking = true;
				xAccel--;
			}
		} else if (keys.right.isPressedDown || keys.D.isPressedDown) {
			if (facing != RIGHT) {
				facing = RIGHT;
			}
			if (!this.facingsBlocked[RIGHT]) {
				this.lockWalking = true;
				xAccel++;
			}
		}
	}

	private void tapped() {
		animationTick = 0;
		animationPointer = 0;
		if (keys.up.isTappedDown || keys.W.isTappedDown) {
			if (facing != UP) {
				facing = UP;
			}
		} else if (keys.down.isTappedDown || keys.S.isTappedDown) {
			if (facing != DOWN) {
				facing = DOWN;
			}
		} else if (keys.left.isTappedDown || keys.A.isTappedDown) {
			if (facing != LEFT) {
				facing = LEFT;
			}
		} else if (keys.right.isTappedDown || keys.D.isTappedDown) {
			if (facing != RIGHT) {
				facing = RIGHT;
			}
		}
	}

	private void walk() {
		this.isColliding = false;
		if (!this.lockWalking) {
			if (!this.facingsBlocked[UP] && !movementLock) {
				if (keys.up.isTappedDown || keys.W.isTappedDown)
					tapped();
				else if (keys.up.isPressedDown || keys.W.isPressedDown) {
					pressed();
					return;
				}
			} else if (keys.up.isPressedDown || keys.W.isPressedDown) {
				this.isColliding = true;
				return;
			}
			if (!this.facingsBlocked[DOWN] && !movementLock) {
				if (keys.down.isTappedDown || keys.S.isTappedDown)
					tapped();
				else if (keys.down.isPressedDown || keys.S.isPressedDown) {
					pressed();
					return;
				}
			} else if (keys.down.isPressedDown || keys.S.isPressedDown) {
				this.isColliding = true;
				return;
			}
			if (!this.facingsBlocked[LEFT] && !movementLock) {
				if (keys.left.isTappedDown || keys.A.isTappedDown)
					tapped();
				else if (keys.left.isPressedDown || keys.A.isPressedDown) {
					pressed();
					return;
				}
			} else if (keys.left.isPressedDown || keys.A.isPressedDown) {
				this.isColliding = true;
				return;
			}
			if (!this.facingsBlocked[RIGHT] && !movementLock) {
				if (keys.right.isTappedDown || keys.D.isTappedDown)
					tapped();
				else if (keys.right.isPressedDown || keys.D.isPressedDown) {
					pressed();
					return;
				}
			} else if (keys.right.isPressedDown || keys.D.isPressedDown) {
				this.isColliding = true;
				return;
			}
		}
	}
}