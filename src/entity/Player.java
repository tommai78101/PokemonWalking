package entity;

import level.Area;
import main.Keys;
import main.NewInputHandler;
import resources.Art;
import screen.BaseScreen;
import abstracts.Entity;
import abstracts.Tile;

public class Player extends Entity {
	public static final int UP = 2;
	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 3;
	
	public Keys keys;
	
	byte animationTick = 0;
	byte animationPointer = 0;
	
	//These are based on the art sprite in the resource folder. The numbers are used to get elements from a 2D array.
	int facing = 0;
	int lastFacing = 0;
	int walking = 0;
	
	int xAccel;
	int yAccel;
	
	int oldXPosition;
	int oldYPosition;
	
	boolean lockWalking;
	boolean lockJumping;
	boolean[] facingsBlocked = new boolean[4];
	boolean isInWater;
	boolean isOnBicycle;
	
	int interactionID;
	boolean enableInteraction;
	
	boolean jumpHeightSignedFlag = false;
	int varyingJumpHeight = 0;
	
	//--------------------------------------------------------------------------
	
	/**
	 * Constructs a Player object in the game. This must be loaded in ONCE.
	 * 
	 * @param Keys
	 *            Takes in the Keys object the input handler is controlling. It must not take in an uncontrolled Keys object.
	 * */
	public Player(Keys keys) {
		this.keys = keys;
	}
	
	/**
	 * Moves the Player object to the center of the screen.
	 * 
	 * @param BaseScreen
	 *            Pans the screen immediately so that the Player object is in the center of the screen.
	 * @return Nothing.
	 * */
	public void setCenterCamPosition(BaseScreen screen) {
		this.setRenderOffset(screen.getWidth() / 2 - Tile.WIDTH, (screen.getHeight() - Tile.HEIGHT) / 2);
	}
	
	public void setRenderOffset(int x, int y) {
		this.xOffset = x;
		this.yOffset = y;
	}
	
	public int getXInArea() {
		//Returns area position X.
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
		//Returns area position Y.
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
	
	public void initialize(Area area) {
		area.setPlayerX(this.getXInArea());
		area.setPlayerY(this.getYInArea());
	}
	
	private void tapped() {
		animationTick = 0;
		animationPointer = 0;
		if (keys.up.isTappedDown || keys.W.isTappedDown) {
			if (facing != UP) {
				facing = UP;
			}
		}
		else if (keys.down.isTappedDown || keys.S.isTappedDown) {
			if (facing != DOWN) {
				facing = DOWN;
			}
		}
		else if (keys.left.isTappedDown || keys.A.isTappedDown) {
			if (facing != LEFT) {
				facing = LEFT;
			}
		}
		else if (keys.right.isTappedDown || keys.D.isTappedDown) {
			if (facing != RIGHT) {
				facing = RIGHT;
			}
		}
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
		}
		else if (keys.down.isPressedDown || keys.S.isPressedDown) {
			if (facing != DOWN) {
				facing = DOWN;
			}
			if (!this.facingsBlocked[DOWN]) {
				this.lockWalking = true;
				yAccel++;
			}
		}
		else if (keys.left.isPressedDown || keys.A.isPressedDown) {
			if (facing != LEFT) {
				facing = LEFT;
			}
			if (!this.facingsBlocked[LEFT]) {
				this.lockWalking = true;
				xAccel--;
			}
		}
		else if (keys.right.isPressedDown || keys.D.isPressedDown) {
			if (facing != RIGHT) {
				facing = RIGHT;
			}
			if (!this.facingsBlocked[RIGHT]) {
				this.lockWalking = true;
				xAccel++;
			}
		}
	}
	
	private void walk() {
		if (!this.lockWalking) {
			if (!this.facingsBlocked[UP]) {
				if (keys.up.isTappedDown || keys.W.isTappedDown)
					tapped();
				else if (keys.up.isPressedDown || keys.W.isPressedDown) {
					pressed();
					return;
				}
			}
			if (!this.facingsBlocked[DOWN]) {
				if (keys.down.isTappedDown || keys.S.isTappedDown)
					tapped();
				else if (keys.down.isPressedDown || keys.S.isPressedDown) {
					pressed();
					return;
				}
			}
			if (!this.facingsBlocked[LEFT]) {
				if (keys.left.isTappedDown || keys.A.isTappedDown)
					tapped();
				else if (keys.left.isPressedDown || keys.A.isPressedDown) {
					pressed();
					return;
				}
			}
			if (!this.facingsBlocked[RIGHT]) {
				if (keys.right.isTappedDown || keys.D.isTappedDown)
					tapped();
				else if (keys.right.isPressedDown || keys.D.isPressedDown) {
					pressed();
					return;
				}
			}
		}
	}
	
	public void collide(Tile tile) {
		xPosition += (-xAccel);
		yPosition += (-yAccel);
	}
	
	private void jump() {
		if (this.lockJumping) {
			//When being locked to walking, facing must stay constant.
			if (this.walking != this.facing)
				this.walking = this.facing;
			
			//Also make sure it's currently not being blocked by anything (You're in the air)
			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
			
			//Makes sure the acceleration stays limited to 1 pixel/tick.
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
			
			//Jumping stuffs go here.
			if (!this.jumpHeightSignedFlag)
				this.varyingJumpHeight++;
			else
				this.varyingJumpHeight--;
			if (this.varyingJumpHeight >= 10.0)
				this.jumpHeightSignedFlag = true;
			
			//Needs to get out of being locked to walking/jumping.
			//Note that we cannot compare using ||, what if the player is moving in one direction? What about the other axis?
			if ((xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0)) {
				//Resets every flag that locks the player.
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
	
	/**
	 * Locks the player into a jumping state. In this state, the Player cannot listen to any key
	 * inputs received during the jump.
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
	 *            The player direction the tile allows the player to jump from. Player direction is determined from
	 *            where the tile is located. The player direction must not be the same as the "to" parameter.
	 * @param to
	 *            The player direction the tile allows the player to jump to. Player direction is determined from
	 *            where the tile is located. The player direction must not be the same as the "from" parameter.
	 * @return Nothing.
	 * */
	public void setLockJumping(int red, int green, int blue, int from, int to) {
		if (from == to)
			throw new IllegalArgumentException("The parameters, from and to, must not be the same.");
		switch (red) {
			case 0x00: //Bottom
				//this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
				this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = true;
				this.facingsBlocked[UP] = false;
				this.lockJumping = true;
				break;
			case 0x01: //Bottom Left
				this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
				break;
			case 0x02: //Left
				this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[UP] = true;
				this.facingsBlocked[LEFT] = false;
				this.lockJumping = true;
				break;
			case 0x03: //Top Left
				this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
				break;
			case 0x04: //Top
				this.facingsBlocked[UP] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = true;
				this.facingsBlocked[DOWN] = false;
				this.lockJumping = true;
				break;
			case 0x05: //Top Right
				this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
				break;
			case 0x06: //Right
				this.facingsBlocked[DOWN] = this.facingsBlocked[UP] = this.facingsBlocked[LEFT] = true;
				this.facingsBlocked[RIGHT] = false;
				this.lockJumping = true;
				break;
			case 0x07: //Bottom Right
				this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = this.facingsBlocked[UP] = true;
				break;
			default: //Any other tiles should not cause the player to jump.
				this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
				this.lockJumping = false;
				break;
		}
	}
	
	/**
	 * Gets a value that determines the direction the player is currently facing towards.
	 * 
	 * @return An integer of one of the followings: Player.UP, Player.DOWN, Player.LEFT, Player.RIGHT.
	 * */
	public int getFacing() {
		return facing;
	}
	
	/**
	 * Gets a value that determines the direction the player had last been facing towards at.
	 * 
	 * @return An integer of one of the followings: Player.UP, Player.DOWN, Player.LEFT, Player.RIGHT.
	 * */
	public int getLastFacing() {
		return lastFacing;
	}
	
	/**
	 * Checks to see if the player is currently locked to walking.
	 * 
	 * @return True, if the player is walking right now. False, otherwise.
	 * */
	public boolean isLockedWalking() {
		return this.lockWalking;
	}
	
	/**
	 * Sets where each of the four directions are blocked by obstacles in front of the player. The
	 * obstacles are in front of the player, when the player is facing towards them. That is the time
	 * to check and see if the obstacle is blocking the player or not.
	 * 
	 * @param up
	 *            If an obstacle is in front of the player when the player is facing towards NORTH, or UP,
	 *            then up is true. False, otherwise.
	 * @param down
	 *            If an obstacle is below of the player when the player is facing towards SOUTH, or DOWN,
	 *            then down is true. False, otherwise.
	 * @param left
	 *            If an obstacle is to the left of the player when the player is facing towards WEST, or LEFT,
	 *            then left is true. False, otherwise.
	 * @param right
	 *            If an obstacle is to the right of the player when the player is facing towards EAST, or RIGHT,
	 *            then right is true. False, otherwise.
	 * */
	public void setAllBlockingDirections(boolean up, boolean down, boolean left, boolean right) {
		this.facingsBlocked[UP] = up;
		this.facingsBlocked[DOWN] = down;
		this.facingsBlocked[LEFT] = left;
		this.facingsBlocked[RIGHT] = right;
	}
	
	/**
	 * Sets the player's current area position to the corresponding X and Y coordinates given.
	 * 
	 * <p>
	 * It uses the 2D Cartesian coordinates used in bitmaps. Positive X: Right. Positive Y: Down.
	 * 
	 * @param x
	 *            The X coordinate the player is to be positioned at.
	 * @param y
	 *            The Y coordinate the player is to be positioned at.
	 * @return Nothing.
	 * */
	public void setAreaPosition(int x, int y) {
		this.setPosition(x * Tile.WIDTH, y * Tile.HEIGHT);
	}
	
	public boolean hasChangedFacing() {
		//True, if current facing has been changed.
		return this.lastFacing != this.facing;
	}
	
	/**
	 * Forces the player to continue to walk for more than 1 tile.
	 * 
	 * Used when the player has entered an entrance.
	 * 
	 * @return Nothing.
	 * */
	public void forceLockWalking() {
		this.lockWalking = true;
		this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
	}
	
	public boolean isLockedJumping() {
		return this.lockJumping;
	}
	
	/**
	 * Returns the player's state (Surfing or Walking).
	 * 
	 * @return True, if player is surfing. False, if player is walking on land.
	 * */
	public boolean isInWater() {
		return this.isInWater;
	}
	
	/**
	 * Changes the player's state to Surfing.
	 * */
	public void goesInWater() {
		this.isInWater = true;
		this.isOnBicycle = false;
	}
	
	/**
	 * Changes the player's state to Walking.
	 * */
	public void leavesWater() {
		this.isInWater = false;
		this.isOnBicycle = false;
	}
	
	/**
	 * Lets the player interact with the data tile ID.
	 * 
	 * @param dataColor
	 *            The tile ID's full data (the color of the tile).
	 * */
	public void interact(int dataColor) {
		int alpha = (dataColor >> 24) & 0xFF;
		switch (alpha) {
			case 0x08: {
				if (this.keys.X.isTappedDown || this.keys.X.isPressedDown || this.keys.PERIOD.isTappedDown || this.keys.PERIOD.isPressedDown) {
					this.enableInteraction = false;
					NewInputHandler.unlockInputs();
				}
				if (this.keys.Z.isTappedDown || this.keys.SLASH.isTappedDown || this.keys.Z.isPressedDown || this.keys.SLASH.isPressedDown) {
					this.enableInteraction = true;
				}
				if (this.enableInteraction) {
					this.interactionID = dataColor & 0xFFFF;
				}
				break;
			}
			default:
				stopInteraction();
				break;
		}
	}
	
	public void stopInteraction() {
		this.enableInteraction = false;
		this.interactionID = 0;
		NewInputHandler.unlockInputs();
	}
	
	public int getInteractionID() {
		return this.interactionID;
	}
	
	public boolean isInteracting() {
		return this.enableInteraction;
	}
	
	public void stopAnimation() {
		this.animationTick = 0;
		this.animationPointer = 0;
	}
	
	//-------------------------------------------------------------------------------------
	//Private methods
	
	/**
	 * Makes adjustments to the player's position when the player is walking.
	 * 
	 * <p>
	 * If the conditions are met, such as a tile has been fully moved to, it will check to make sure the player has stopped walking, until the player wanted to walk.
	 * 
	 * @return Nothing.
	 * */
	private void handleMovementCheck() {
		//Check if player is currently locked to walking.
		if (this.lockWalking) {
			
			//When being locked to walking, facing must stay constant.
			if (this.walking != this.facing)
				this.walking = this.facing;
			
			//Makes sure the acceleration stays limited to 1 pixel/tick.
			if (xAccel > 1)
				xAccel = 1;
			if (xAccel < -1)
				xAccel = -1;
			if (yAccel > 1)
				yAccel = 1;
			if (yAccel < -1)
				yAccel = -1;
			
			if (!this.isOnBicycle) {
				xPosition += xAccel * 2;
				yPosition += yAccel * 2;
			}
			else {
				xPosition += xAccel * 4;
				yPosition += yAccel * 4;
			}
			
			//Needs to get out of being locked to walking/jumping.
			//Note that we cannot compare using ||, what if the player is moving in one direction? What about the other axis?
			if ((xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0)) {
				//Resets every flag that locks the player.
				this.lockWalking = false;
			}
		}
		else {
			//Before we walk, check to see if the oldX and oldY are up-to-date with the latest X and Y.
			if (this.oldXPosition != this.xPosition)
				this.oldXPosition = this.xPosition;
			if (this.oldYPosition != this.yPosition)
				this.oldYPosition = this.yPosition;
			
			//Reset the acceleration values, since we're not really walking.
			xAccel = 0;
			yAccel = 0;
			
			//Check for inputs the player wants to face. Tapping in a direction turns the player around.
			checkFacing();
			
			//Now about to walk. First, check to see if there's an obstacle blocking the path.
			if (this.facingsBlocked[UP] || this.facingsBlocked[DOWN] || this.facingsBlocked[LEFT] || this.facingsBlocked[RIGHT]) {
				this.lockWalking = false;
			}
		}
	}
	
	private void checkFacing() {
		if (keys.up.isTappedDown || keys.up.isPressedDown || keys.W.isTappedDown || keys.W.isPressedDown) {
			facing = UP;
		}
		else if (keys.down.isTappedDown || keys.down.isPressedDown || keys.S.isTappedDown || keys.S.isPressedDown) {
			facing = DOWN;
		}
		else if (keys.left.isTappedDown || keys.left.isPressedDown || keys.A.isTappedDown || keys.A.isPressedDown) {
			facing = LEFT;
		}
		else if (keys.right.isTappedDown || keys.right.isPressedDown || keys.D.isTappedDown || keys.D.isPressedDown) {
			facing = RIGHT;
		}
	}
	
	private void controlTick() {
		animationTick++;
		if ((this.facing == UP && this.facingsBlocked[UP])) {
			if (animationTick >= 10) {
				animationTick = 0;
			}
		}
		else if ((this.facing == DOWN && this.facingsBlocked[DOWN])) {
			if (animationTick >= 10) {
				animationTick = 0;
			}
		}
		else if ((this.facing == LEFT && this.facingsBlocked[LEFT])) {
			if (animationTick >= 10) {
				animationTick = 0;
			}
		}
		else if ((this.facing == RIGHT && this.facingsBlocked[RIGHT])) {
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
	
	//---------------------------------------------------------------------
	//Override methods
	
	@Override
	public int getX() {
		return this.xPosition;
	}
	
	@Override
	public int getY() {
		return this.yPosition;
	}
	
	@Override
	public void tick() {
		//TODO: Find some way of allowing players to ride bicycle.
		if (!this.lockJumping) {
			if (!this.enableInteraction) {
				walk();
				handleMovementCheck();
				controlTick();
			}
			else {
				stopAnimation();
			}
		}
		else
			jump();
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
	 * */
	@Override
	public void render(BaseScreen output, int x, int y) {
		if (this.lockWalking && !this.lockJumping) {
			//Walking animation
			if (this.isInWater)
				output.npcBlit(Art.player_surf[walking][animationPointer], this.xOffset + x, this.yOffset + y);
			else if (this.isOnBicycle)
				output.npcBlit(Art.player_bicycle[walking][animationPointer], this.xOffset + x, this.yOffset + y);
			else
				output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y);
		}
		else if (this.lockJumping) {
			output.blit(Art.shadow, this.xOffset + x, this.yOffset + y + 4);
			//Walking animation while in the air. Shouldn't jump when in water.
			if (this.isOnBicycle)
				output.npcBlit(Art.player_bicycle[walking][animationPointer], this.xOffset + x, this.yOffset + y - this.varyingJumpHeight);
			else
				output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y - this.varyingJumpHeight);
			
		}
		else {
			//Blocking animation. Possibly done to create a perfect loop.
			if (!this.isInWater && !this.isOnBicycle) {
				if (keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown || keys.right.isPressedDown
					|| keys.S.isPressedDown || keys.W.isPressedDown || keys.A.isPressedDown || keys.D.isPressedDown)
					output.npcBlit(Art.player[facing][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player[facing][0], this.xOffset + x, this.yOffset + y);
			}
			else if (this.isInWater && !this.isOnBicycle) {
				if (keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown || keys.right.isPressedDown
					|| keys.S.isPressedDown || keys.W.isPressedDown || keys.A.isPressedDown || keys.D.isPressedDown)
					output.npcBlit(Art.player_surf[facing][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player_surf[facing][0], this.xOffset + x, this.yOffset + y);
			}
			else if (!this.isInWater && this.isOnBicycle) {
				if (keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown || keys.right.isPressedDown
					|| keys.S.isPressedDown || keys.W.isPressedDown || keys.A.isPressedDown || keys.D.isPressedDown)
					output.npcBlit(Art.player_bicycle[facing][animationPointer], this.xOffset + x, this.yOffset + y);
				else
					output.npcBlit(Art.player_bicycle[facing][0], this.xOffset + x, this.yOffset + y);
			}
		}
	}
}