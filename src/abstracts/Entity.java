package abstracts;

import interfaces.Collidable;
import interfaces.Interactable;
import screen.BaseBitmap;
import screen.BaseScreen;

public abstract class Entity extends Tile implements Collidable, Interactable {
	
	public int id;
	
	protected int xPosition;
	protected int yPosition;
	
	protected int xOffset;
	protected int yOffset;
	
	protected BaseBitmap bitmap;
	
	public boolean isRemoved;
	protected byte typeId = 0;
	
	//public abstract void initialize(BaseWorld world);
	
	public abstract void tick();
	
	@Override
	public abstract void render(BaseScreen screen, int x, int y);
	
	public int getX() {
		return xPosition;
	}
	
	public int getY() {
		return yPosition;
	}
	
	@Override
	public void collide(Collidable object) {
	}
	
	public void setPosition(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
	}
}
