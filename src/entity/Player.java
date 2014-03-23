package entity;

import interfaces.Interactable;
import level.Area;
import main.Keys;
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
	
	int collidableId = 1;
	int interactableId = 1;
	
	//Not yet used.
	//	public enum AnimationType {
	//		WALKING
	//	};
	//	
	//	AnimationType animationType;
	
	public Inventory inventory;
	
	//--------------------------------------------------------------------------
	
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
	
	//Temporary variables
	boolean jumpHeightSignedFlag = false;
	int varyingJumpHeight = 0;

	//--------------------------------------------------------------------------

	public Player(Keys keys) {
		this.keys = keys;
	}
	
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
	
	public void tapped() {
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
	
	public void pressed() {
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

	public void walk() {
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
	
	public void jump() {
		if (this.lockJumping) {
			//When being locked to walking, facing must stay constant.
			if (this.walking != this.facing)
				this.walking = this.facing;
			
			//Also make sure it's currently not being blocked by anything (You're in the air)
			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;

			//Makes sure the acceleration stays limited to 1 pixel/tick.
			if (xAccel > 1) xAccel = 1;
			if (xAccel < -1) xAccel = -1;
			if (yAccel > 1) yAccel = 1;
			if (yAccel < -1) yAccel = -1;
			
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
	
	public void setLockJumping(int red, int green, int blue, int canAllow_1, int canAllow_2) {
		/*
		 * Pixel color determines the current tile ID the player is on. It's separated into R, G, and B.
		 * canAllow_1 and canAllow_2 determines the direction the player can go while the player is on the current pixel data.
		 * 
		 * canAllow_1 must be UP or LEFT.
		 * canAllow_2 must be DOWN or RIGHT.
		 *
		 * Unfortunately, we have to do another check on the colors.
		 */
		
		if (canAllow_1 == DOWN || canAllow_1 == RIGHT || canAllow_2 == UP || canAllow_2 == LEFT)
			throw new IllegalArgumentException("canAllow_1 must be UP or LEFT. canAllow_2 must be DOWN or RIGHT");
		
		switch (blue) {
			case 0xDD:
				//Since this is a ledge (horizontal), green = orientation, red = ledge type.
				switch (green) {
					case 0x00:
						//Horizontal
						this.facingsBlocked[DOWN] = this.facingsBlocked[LEFT] = this.facingsBlocked[RIGHT] = true;
						this.facingsBlocked[UP] = false;
						this.lockJumping = true;
						break;
					default:
						this.lockJumping = false;
						break;

				}
				break;
			default:
				this.lockJumping = false;
				break;
		}

	}

	public int getFacing() {
		return facing;
	}
	
	public int getLastFacing() {
		return lastFacing;
	}

	public boolean isLockedWalking() {
		return this.lockWalking;
	}
	
	public void setAllBlockingDirections(boolean up, boolean down, boolean left, boolean right) {
		this.facingsBlocked[UP] = up;
		this.facingsBlocked[DOWN] = down;
		this.facingsBlocked[LEFT] = left;
		this.facingsBlocked[RIGHT] = right;
	}

	public void setAreaPosition(int x, int y) {
		this.setPosition(x * Tile.WIDTH, y * Tile.HEIGHT);
	}
	
	public boolean hasChangedFacing() {
		//True, if current facing has been changed.
		return this.lastFacing != this.facing;
	}
	
	public void forceLockWalking() {
		this.lockWalking = true;
		this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
	}
	
	public boolean isLockedJumping() {
		return this.lockJumping;
	}

	//-------------------------------------------------------------------------------------
	//Private methods
	
	private void handleMovementCheck() {
		//Check if player is currently locked to walking.
		if (this.lockWalking) {

			//When being locked to walking, facing must stay constant.
			if (this.walking != this.facing)
				this.walking = this.facing;

			//Makes sure the acceleration stays limited to 1 pixel/tick.
			if (xAccel > 1) xAccel = 1;
			if (xAccel < -1) xAccel = -1;
			if (yAccel > 1) yAccel = 1;
			if (yAccel < -1) yAccel = -1;
			
			xPosition += xAccel * 2;
			yPosition += yAccel * 2;

			//Needs to get out of being locked to walking/jumping.
			//Note that we cannot compare using ||, what if the player is moving in one direction? What about the other axis?
			if ((xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0)) {
				//Resets every flag that locks the player.
				this.lockWalking = false;
			}
		}
		else {
			//Before we walk, check to see if the oldX and oldY are up-to-date with the latest X and Y.
			if (this.oldXPosition != this.xPosition) this.oldXPosition = this.xPosition;
			if (this.oldYPosition != this.yPosition) this.oldYPosition = this.yPosition;
			
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
	public void handleCollision(Tile tile, int xAcceleration, int yAcceleration) {
	}
	
	@Override
	public void tick() {
		if (!this.lockJumping) {
			walk();
			handleMovementCheck();
			controlTick();
		}
		else
			jump();
	}

	@Override
	public void render(BaseScreen output, int x, int y) {
		//Blits the entity onto the screen, being offsetted to the left, which fits snugly in the world grids.
		//render(BaseScreen output, int x, int y)
		//output: where to blit the bitmap
		//x: Pixel X offset
		//y: Pixel Y offset
		
		if (this.lockWalking && !this.lockJumping) {
			//Walking animation
			output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y);
		}
		else if (this.lockJumping) {
			//Walking animation while in the air.
			output.npcBlit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y - this.varyingJumpHeight);
		}
		else {
			//Blocking animation. Possibly done to create a perfect loop.
			if (keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown || keys.right.isPressedDown
				|| keys.S.isPressedDown || keys.W.isPressedDown || keys.A.isPressedDown || keys.D.isPressedDown)
				output.npcBlit(Art.player[facing][animationPointer], this.xOffset + x, this.yOffset + y);
			else
				output.npcBlit(Art.player[facing][0], this.xOffset + x, this.yOffset + y);
		}
	}
	
	@Override
	public void interact(Interactable object) {
	}
	
	@Override
	public int getInteractableId() {
		return this.interactableId;
	}
	
	@Override
	public int getCollidableId() {
		return this.collidableId;
	}
}