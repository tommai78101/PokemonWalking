package entity;

import interfaces.Interactable;
import level.Area;
import main.Keys;
import resources.Art;
import screen.BaseScreen;
import abstracts.BaseWorld;
import abstracts.Entity;
import abstracts.Tile;

public class Player extends Entity {
	private final int UP = 2;
	private final int DOWN = 0;
	private final int LEFT = 1;
	private final int RIGHT = 3;
	
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
	boolean[] facingsBlocked = new boolean[4];

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
		//if (!this.lockWalking)
		int result = (xPosition / Tile.WIDTH);
		if (this.lockWalking)
			switch (facing) {
				case LEFT:
					break;
				case RIGHT:
					result += 1;
					break;
			}
		//System.out.println("" + result);

		return result;
		//return this.oldXPosition / Tile.WIDTH;
	}
	
	public int getYInArea() {
		//Returns area position Y.
		//if (!this.lockWalking)
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
		//return this.oldYPosition / Tile.HEIGHT;
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
				else if (keys.up.isPressedDown || keys.W.isPressedDown)
					pressed();
			}
			if (!this.facingsBlocked[DOWN]) {
				if (keys.down.isTappedDown || keys.S.isTappedDown)
					tapped();
				else if (keys.down.isPressedDown || keys.S.isPressedDown)
					pressed();
			}
			if (!this.facingsBlocked[LEFT]) {
				if (keys.left.isTappedDown || keys.A.isTappedDown)
					tapped();
				else if (keys.left.isPressedDown || keys.A.isPressedDown)
					pressed();
			}
			if (!this.facingsBlocked[RIGHT]) {
				if (keys.right.isTappedDown || keys.D.isTappedDown)
					tapped();
				else if (keys.right.isPressedDown || keys.D.isPressedDown)
					pressed();
			}
			
			//			
			//			if (!this.isBlocked) {
			//				if (keys.up.isTappedDown || keys.down.isTappedDown || keys.left.isTappedDown || keys.right.isTappedDown || keys.W.isTappedDown || keys.S.isTappedDown || keys.A.isTappedDown || keys.D.isTappedDown)
			//					tapped();
			//				else if (keys.up.isPressedDown || keys.down.isPressedDown || keys.left.isPressedDown || keys.right.isPressedDown || keys.W.isPressedDown || keys.S.isPressedDown || keys.A.isPressedDown || keys.D.isPressedDown)
			//					pressed();
			//			}
		}
		handleMovementCheck();
		controlTick();
	}
	
	public void collide(Tile tile) {
		xPosition += (-xAccel);
		yPosition += (-yAccel);
	}
	
	@Override
	public void render(BaseScreen screen) {
		/*//Blits the entity onto the screen, being offsetted to the left, which fits snugly in the world grids.
		if (lockWalking) {
			//screen.blit(Art.player[walking][animationPointer], (screen.getWidth() - Tile.WIDTH * 2) / 2 + xPosition, (screen.getHeight() - Tile.HEIGHT) / 2 + yPosition, Tile.WIDTH, Tile.HEIGHT);
			screen.blit(Art.player[walking][animationPointer], xPosition - Tile.WIDTH, yPosition - Tile.HEIGHT / 2);
		}
		else
			//screen.blit(Art.player[facing][0], (screen.getWidth() - Tile.WIDTH * 2) / 2 + xPosition, (screen.getHeight() - Tile.HEIGHT) / 2 + yPosition, Tile.WIDTH, Tile.HEIGHT);
			screen.blit(Art.player[facing][0], xPosition - Tile.WIDTH, yPosition - Tile.HEIGHT / 2);*/
		
	}
	
	public void render(BaseScreen output, int x, int y) {
		
		//render(BaseScreen output, int x, int y)
		//output: where to blit the bitmap
		//x: Pixel X offset
		//y: Pixel Y offset

		if (this.lockWalking) {
			output.blit(Art.player[walking][animationPointer], this.xOffset + x, this.yOffset + y);
		}
		else {
			if (keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown || keys.right.isPressedDown
				|| keys.S.isPressedDown || keys.W.isPressedDown || keys.A.isPressedDown || keys.D.isPressedDown)
				output.blit(Art.player[facing][animationPointer], this.xOffset + x, this.yOffset + y);
			else
				output.blit(Art.player[facing][0], this.xOffset + x, this.yOffset + y);
		}
	}
	
	//	@Override
	//	public void handleCollision(Tile tile, int xAcceleration, int yAcceleration) {
	//		int a = this.xPosition + Tile.WIDTH;
	//		int b = this.xPosition - Tile.WIDTH;
	//		int c = this.yPosition + Tile.HEIGHT;
	//		int d = this.yPosition - Tile.HEIGHT;
	//		if (tile == null)
	//			return;
	//		switch (tile.getType()) {
	//			case Entity.PLAYER:
	//			default:
	//				break;
	//			case Entity.TREE:
	//				this.collide(tile);
	//				isBlocked = true;
	//				break;
	//		}
	//	}

	public int getFacing() {
		return facing;
	}
	
	public int getLastFacing() {
		return lastFacing;
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
	
	//	public void blockPath(boolean value) {
	//		this.isBlocked = value;
	//	}
	
	public boolean isNotLockedWalking() {
		return !this.lockWalking;
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
			
			xPosition += xAccel;
			yPosition += yAccel;

			//Needs to get out of being locked to walking.
			//Note that we cannot compare using ||, what if the player is moving in one direction? What about the other axis?
			if ((xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0)) {
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
			//checkFacingObstacles();
			
			//Now about to walk. First, check to see if there's an obstacle blocking the path.
			if (this.facingsBlocked[UP] || this.facingsBlocked[DOWN] || this.facingsBlocked[LEFT] || this.facingsBlocked[RIGHT]) {
				this.lockWalking = false;
			}
			
			//			if (this.isBlocked) {
			//				this.lockWalking = false;
			//			}
		}
		
		//		if (xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0) {
		//			if (this.isBlocked) {
		//				xAccel = 0;
		//				yAccel = 0;
		//				walking = facing;
		//				this.lockWalking = false;
		//				return;
		//			}
		//		}
		//
		//		if (this.lockWalking) {
		//			if (xAccel > 1)
		//				xAccel = 1;
		//			else if (xAccel < -1)
		//				xAccel = -1;
		//			else if (yAccel > 1)
		//				yAccel = 1;
		//			else if (yAccel < -1)
		//				yAccel = -1;
		//			
		//			xPosition += xAccel;
		//			yPosition += yAccel;
		//			checkFacing();
		//			if (xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0) {
		//				xAccel = 0;
		//				yAccel = 0;
		//				this.lockWalking = false;
		//				walking = facing;
		//			}
		//		}
		//
		//		if (Math.abs(xPosition - oldXPosition) == Tile.WIDTH || Math.abs(yPosition - oldYPosition) == Tile.HEIGHT) {
		//			oldXPosition = xPosition;
		//			oldYPosition = yPosition;
		//		}
	}
	
	//	
	//	private void checkFacingObstacles() {
	//		this.isBlocked = false;
	//		if (this.facing == UP) {
	//			if (this.facingsBlocked[UP])
	//				this.isBlocked = true;
	//		}
	//		else if (this.facing == DOWN) {
	//			if (this.facingsBlocked[DOWN])
	//				this.isBlocked = true;
	//		}
	//		else if (this.facing == LEFT) {
	//			if (this.facingsBlocked[LEFT])
	//				this.isBlocked = true;
	//		}
	//		else if (this.facing == RIGHT) {
	//			if (this.facingsBlocked[RIGHT])
	//				this.isBlocked = true;
	//		}
	//	}

	//	private boolean checkObstacles() {
	//		//Something something
	//		return isBlocked;
	//	}
	
	//	private boolean facingObstacle(Tile object) {
	//		this.handleCollision(object, 0, 0);
	//		return isBlocked;
	//	}
	
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
		//if (this.isBlocked) {
		if ((this.facing == UP && this.facingsBlocked[UP])) {
			if (animationTick >= 20) {
				animationTick = 0;
			}
		}
		else if ((this.facing == DOWN && this.facingsBlocked[DOWN])) {
			if (animationTick >= 20) {
				animationTick = 0;
			}
		}
		else if ((this.facing == LEFT && this.facingsBlocked[LEFT])) {
			if (animationTick >= 20) {
				animationTick = 0;
			}
		}
		else if ((this.facing == RIGHT && this.facingsBlocked[RIGHT])) {
			if (animationTick >= 20) {
				animationTick = 0;
			}
		}
		else {
			if (animationTick >= 8)
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
	public void initialize(BaseWorld world) {

	}
	
	@Override
	public void handleCollision(Tile tile, int xAcceleration, int yAcceleration) {
	}
	
	@Override
	public void tick() {
		
		//Tile object = this.world.getTile(nextX, nextY);
		//if (!facingObstacle(object)){ 
		walk();
		//}
		//else {
		//	bang();
		//}
	}
}