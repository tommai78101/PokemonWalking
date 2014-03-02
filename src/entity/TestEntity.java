package entity;

import level.BaseWorld;
import level.tile.Tile;
import main.Keys;
import resources.Art;
import screen.BaseScreen;

public class TestEntity extends Entity {
	
	public Keys keys;
	public int xPosition;
	public int yPosition;
	int facing = 0;
	int turning = 0;
	byte animationTick = 0;
	byte animationPointer = 0;
	
	int testValue = 0;
	
	int xAccel;
	int yAccel;
	
	boolean lockWalking;
	
	public TestEntity(Keys keys) {
		this.keys = keys;
	}
	
	public void setCenter(BaseScreen screen) {
	}
	
	@Override
	public void initialize(BaseWorld world) {
		
	}
	
	@Override
	public void tick() {
		if (!lockWalking) {
			if (keys.up.isTappedDown) {
				facing = 2;
				animationTick = 0;
				animationPointer = 0;
			}
			else if (keys.down.isTappedDown) {
				facing = 0;
				animationTick = 0;
				animationPointer = 0;
			}
			else if (keys.left.isTappedDown) {
				facing = 1;
				animationTick = 0;
				animationPointer = 0;
			}
			else if (keys.right.isTappedDown) {
				facing = 3;
				animationTick = 0;
				animationPointer = 0;
			}
		}
		if (!lockWalking) {
			if (keys.up.isPressedDown) {
				if (facing == 2)
					yAccel--;
				else
					facing = 2;
				lockWalking = true;
			}
			if (keys.down.isPressedDown) {
				if (facing == 0)
					yAccel++;
				else
					facing = 0;
				lockWalking = true;
			}
			if (keys.left.isPressedDown) {
				if (facing == 1)
					xAccel--;
				else
					facing = 1;
				lockWalking = true;
			}
			if (keys.right.isPressedDown) {
				if (facing == 3)
					xAccel++;
				else
					facing = 3;
				lockWalking = true;
			}
		}
		handleMovement();
		animationTick++;
		if (animationTick >= 8) {
			animationTick = 0;
			animationPointer++;
			if (animationPointer > 3) {
				animationPointer = 0;
			}
		}
	}
	
	@Override
	public void render(BaseScreen screen) {
		//Blits the entity onto the screen, being offsetted to the left, which fits snugly in the world grids.
		if (lockWalking) {
			screen.blit(Art.player[turning][animationPointer], (screen.getWidth() - Tile.WIDTH * 2) / 2 + xPosition, (screen.getHeight() - Tile.HEIGHT) / 2 + yPosition, Tile.WIDTH, Tile.HEIGHT);
		}
		else
			screen.blit(Art.player[facing][0], (screen.getWidth() - Tile.WIDTH * 2) / 2 + xPosition, (screen.getHeight() - Tile.HEIGHT) / 2 + yPosition, Tile.WIDTH, Tile.HEIGHT);
		
	}
	
	//-----------------------------------
	//Private methods
	
	private void handleMovement() {
		if (lockWalking) {
			xPosition += xAccel;
			yPosition += yAccel;
		}
		if (xPosition % Tile.WIDTH == 0 && yPosition % Tile.HEIGHT == 0) {
			lockWalking = false;
			xAccel = 0;
			yAccel = 0;
			turning = facing;
		}
		else
			lockWalking = true;
	}
}
