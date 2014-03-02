package math;

import interfaces.BoundingBoxCollidable;

public class Collision {
	public int top;
	public int bottom;
	public int left;
	public int right;
	public BoundingBoxCollidable collidableOwner;
	
	public Collision(BoundingBoxCollidable bbCollidable, int left, int top, int right, int bottom) {
		this.collidableOwner = bbCollidable;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}
	
	public boolean isIntersecting(Collision object) {
		if (object.top <= this.bottom || object.bottom >= this.top || object.left <= this.right || object.right >= this.left)
			return true;
		return false;
	}
	
	public boolean isIntersecting(int left, int top, int right, int bottom) {
		if (top <= this.bottom || bottom >= this.top || left <= this.right || right >= this.left)
			return true;
		return false;
	}
}
