package entity;

import level.BaseWorld;
import screen.BaseScreen;

public abstract class Entity {
	public boolean isRemoved;
	
	public abstract void initialize(BaseWorld world);
	
	public abstract void tick();
	
	public abstract void render(BaseScreen screen);
}
