package entity;

import interfaces.Collidable;
import interfaces.Interactable;
import level.Area;
import main.Keys;
import resources.Art;
import screen.BaseScreen;
import abstracts.*;

public class Player extends Entity implements Collidable, Interactable {
	private final int UP = 2;
	private final int DOWN = 0;
	private final int LEFT = 1;
	private final int RIGHT = 3;
	
	public Keys keys;
	
	byte animationTick = 0;
	byte animationPointer = 0;
	
	int collidableId = 1;
	int interactableId = 1;
	
	public enum AnimationType {
		WALKING
	};
	
	AnimationType animationType;
	
	public Inventory inventory;
	
	//--------------------------------------------------------------------------
	
	//These are based on the art sprite in the resource folder. The numbers are used to get elements from a 2D array.
	int facing = 0;
	int lastFacing = 0;
	int turning = 0;
	
	int xAccel;
	int yAccel;
	
	int oldXPosition;
	int oldYPosition;

	boolean lockWalking;
	boolean isBlocked;
	
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
	
	public void bang() {
		
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
	
	public void initialize(Area area) {
		area.setPlayerX(this.getXInArea());
		area.setPlayerY(this.getYInArea());
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
	
	public boolean tapped() {
		if (keys.up.isTappedDown || keys.W.isTappedDown) {
			if (facing != UP) {
				lastFacing = facing;
				facing = UP;
				animationTick = 0;
				animationPointer = 0;
			}
			else {
				yAccel--;
				return true;
			}
		}
		else if (keys.down.isTappedDown || keys.S.isTappedDown) {
			if (facing != DOWN) {
				lastFacing = facing;
				facing = DOWN;
				animationTick = 0;
				animationPointer = 0;
			}
			else {
				yAccel++;
				return true;
			}
		}
		else if (keys.left.isTappedDown || keys.A.isTappedDown) {
			if (facing != LEFT) {
				lastFacing = facing;
				facing = LEFT;
				animationTick = 0;
				animationPointer = 0;
			}
			else {
				xAccel--;
				return true;
			}
		}
		else if (keys.right.isTappedDown || keys.D.isTappedDown) {
			if (facing != RIGHT) {
				lastFacing = facing;
				facing = RIGHT;
				animationTick = 0;
				animationPointer = 0;
			}
			else {
				xAccel++;
				return true;
			}

		}
		return false;
	}
	
	public void pressed() {
		if (keys.up.isPressedDown || keys.W.isPressedDown) {
			if (facing == UP)
				yAccel--;
			else {
				facing = UP;
			}
			lockWalking = true;
		}
		if (keys.down.isPressedDown || keys.S.isPressedDown) {
			if (facing == DOWN)
				yAccel++;
			else {
				facing = DOWN;
			}
			lockWalking = true;
		}
		if (keys.left.isPressedDown || keys.A.isPressedDown) {
			if (facing == LEFT)
				xAccel--;
			else {
				facing = LEFT;
			}
			lockWalking = true;
		}
		if (keys.right.isPressedDown || keys.D.isPressedDown) {
			if (facing == RIGHT)
				xAccel++;
			else {
				facing = RIGHT;
			}
			lockWalking = true;
		}
	}
	
	public void walk() {
		if (!this.lockWalking) {
			//			if (!tapped())
			//				pressed();
			tapped();
			pressed();
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
			//screen.blit(Art.player[turning][animationPointer], (screen.getWidth() - Tile.WIDTH * 2) / 2 + xPosition, (screen.getHeight() - Tile.HEIGHT) / 2 + yPosition, Tile.WIDTH, Tile.HEIGHT);
			screen.blit(Art.player[turning][animationPointer], xPosition - Tile.WIDTH, yPosition - Tile.HEIGHT / 2);
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
			output.blit(Art.player[turning][animationPointer], this.xOffset + x, this.yOffset + y);
		}
		else {
			if (keys.down.isPressedDown || keys.up.isPressedDown || keys.left.isPressedDown || keys.right.isPressedDown
				|| keys.S.isPressedDown || keys.W.isPressedDown || keys.A.isPressedDown || keys.D.isPressedDown)
				output.blit(Art.player[facing][animationPointer], this.xOffset + x, this.yOffset + y);
			else
				output.blit(Art.player[facing][0], this.xOffset + x, this.yOffset + y);
		}
	}
	
	@Override
	public void handleCollision(Tile tile, int xAcceleration, int yAcceleration) {
		int a = this.xPosition + Tile.WIDTH;
		int b = this.xPosition - Tile.WIDTH;
		int c = this.yPosition + Tile.HEIGHT;
		int d = this.yPosition - Tile.HEIGHT;
		if (tile == null)
			return;
		isBlocked = false;
		switch (tile.getType()) {
			case Entity.PLAYER:
			default:
				break;
			case Entity.TREE:
				this.collide(tile);
				isBlocked = true;
				break;
		}
	}
	
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
	
	public void blockPath(boolean value) {
		this.isBlocked = value;
	}
	
	public boolean isNotLockedWalking() {
		return !this.lockWalking;
	}
	
	//-----------------------------------
	//Private methods
	
	private void handleMovementCheck() {
		if (xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0) {
			if (this.isBlocked) {
				xAccel = 0;
				yAccel = 0;
				turning = facing;
				this.lockWalking = false;
				return;
			}
		}

		if (this.lockWalking) {
			if (xAccel > 1)
				xAccel = 1;
			else if (xAccel < -1)
				xAccel = -1;
			else if (yAccel > 1)
				yAccel = 1;
			else if (yAccel < -1)
				yAccel = -1;
			
			xPosition += xAccel;
			yPosition += yAccel;
			checkFacing();
			if (xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0) {
				xAccel = 0;
				yAccel = 0;
				this.lockWalking = false;
				turning = facing;
			}
		}

		if (Math.abs(xPosition - oldXPosition) == Tile.WIDTH || Math.abs(yPosition - oldYPosition) == Tile.HEIGHT) {
			oldXPosition = xPosition;
			oldYPosition = yPosition;
		}
	}
	
	private boolean facingObstacle(Tile object) {
		this.handleCollision(object, 0, 0);
		return isBlocked;
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
		if (isBlocked) {
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
	
	public void setAreaPosition(int x, int y) {
		this.setPosition(x * Tile.WIDTH, y * Tile.HEIGHT);
	}
	
	public boolean hasChangedFacing() {
		//True, if current facing has been changed.
		return this.lastFacing != this.facing;
	}
}
