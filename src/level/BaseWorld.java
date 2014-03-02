package level;

import java.util.ArrayList;
import java.util.List;

import screen.BaseScreen;
import abstracts.Entity;

public abstract class BaseWorld {
	
	public List<Entity> entities = new ArrayList<Entity>();
	
	//If parameters, width and height, are allowed, it's mostly for setting up boundaries.
	//In our case, we don't have boundaries in acts. Only worlds.
	//	public BaseWorld() {
	//		//this.addEntity(new TestEntity());
	//	}
	
	public void tick() {
		
	}
	
	public void render(BaseScreen screen, int xScroll, int yScroll) {
	}
	
	public abstract void addEntity(Entity e);
	
	public abstract int getWidth();
	
	public abstract int getHeight();
}
