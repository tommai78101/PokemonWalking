package entity;

import java.awt.Graphics;

import abstracts.Character;
import abstracts.Entity;
import level.Area;
import resources.Art;
import screen.Scene;

public class Joe extends Character {
	public Joe() {
		super();
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(Scene screen, Graphics graphics, int playerX, int playerY) {
		if (this.isLockedWalking) {
			screen.npcBlit(Art.joe[this.getFacing()][this.animationPointer], this.getRenderPositionX(playerX), this.getRenderPositionY(playerY));
		}
		else {
			// Idle animation
			screen.npcBlit(Art.joe[this.getFacing()][0], this.getRenderPositionX(playerX), this.getRenderPositionY(playerY));
		}
	}

	@Override
	public void walk() {
		// TODO Auto-generated method stub
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

	@Override
	protected void controlTick() {
		if (!this.isLockedWalking) {
			this.animationTick++;
			if ((this.getFacing() == Character.UP && this.isFacingBlocked[Character.UP])) {
				if (this.animationTick >= 10) {
					this.animationTick = 0;
				}
			}
			else if ((this.getFacing() == Character.DOWN && this.isFacingBlocked[Character.DOWN])) {
				if (this.animationTick >= 10) {
					this.animationTick = 0;
				}
			}
			else if ((this.getFacing() == Character.LEFT && this.isFacingBlocked[Character.LEFT])) {
				if (this.animationTick >= 10) {
					this.animationTick = 0;
				}
			}
			else if ((this.getFacing() == Character.RIGHT && this.isFacingBlocked[Character.RIGHT])) {
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
}