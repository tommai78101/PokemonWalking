package level;

import level.tile.Tile;
import resources.Art;
import screen.BaseScreen;
import abstracts.Entity;

public class AnotherWorld extends BaseWorld {
	public int initialX;
	public int initialY;
	public int width;
	public int height;
	
	public AnotherWorld(int x, int y, int w, int h) {
		this.initialX = x;
		this.initialY = y;
		this.width = w;
		this.height = h;
	}
	
	@Override
	public void tick() {
		for (Entity e : this.entities)
			e.tick();
	}
	
	@Override
	public void render(BaseScreen screen, int xScroll, int yScroll) {
		for (int y = this.initialY; y < this.height * 2; y++) {
			for (int x = this.initialX; x < this.width * 2; x++) {
				screen.blit(Art.smallTree, (xScroll + x) * Tile.WIDTH, (yScroll + y) * Tile.HEIGHT);
			}
		}
		
	}
	
	@Override
	public void addEntity(Entity e) {
		e.initialize(this);
		this.entities.add(e);
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	public int getX() {
		return initialX;
	}
	
	public int getY() {
		return initialY;
	}
}
