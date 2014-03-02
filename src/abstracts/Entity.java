package abstracts;

import level.BaseWorld;
import screen.BaseScreen;

public abstract class Entity {
	public boolean isRemoved;
	protected int xPosition;
	protected int yPosition;
	
	public abstract void initialize(BaseWorld world);
	
	public abstract void tick();
	
	public abstract void render(BaseScreen screen);
	
	public int getX() {
		return xPosition;
	}
	
	public int getY() {
		return yPosition;
	}
}
