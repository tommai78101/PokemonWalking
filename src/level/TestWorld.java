package level;

import level.tile.Tile;
import resources.Art;
import screen.BaseScreen;
import entity.Entity;

public class TestWorld extends BaseWorld {
	
	final int width;
	final int height;
	
	public TestWorld(int w, int h) {
		super();
		this.width = w;
		this.height = h;
		//this.addEntity(new TestEntity(new Keys()));
	}
	
	public void addEntity(Entity e) {
		e.initialize(this);
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		e.isRemoved = true;
	}
	
	@Override
	public void tick() {
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (!e.isRemoved) {
				e.tick();
			}
			if (e.isRemoved) {
				entities.remove(i--);
			}
		}
	}
	
	@Override
	public void render(BaseScreen screen, int xScroll, int yScroll) {
		int gridX0 = xScroll / Tile.WIDTH - 1;
		int gridY0 = yScroll / Tile.HEIGHT - 1;
		int gridX1 = (xScroll + screen.getWidth()) / Tile.WIDTH + 1;
		int gridY1 = (yScroll + screen.getHeight()) / Tile.HEIGHT + 1;
		
		if (xScroll < 0)
			gridX0--;
		if (yScroll < 0)
			gridY0--;
		
		screen.setOffset(-xScroll, -yScroll);
		
		this.renderTiles(screen, gridX0, gridY0, gridX1, gridY1);
		
		for (Entity e : this.entities) {
			e.render(screen);
		}
		screen.setOffset(0, 0);
	}
	
	private void renderTiles(BaseScreen screen, int x0, int y0, int x1, int y1) {
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				//TODO: Work more on tile stuffs here.
				
				//draw something outside the world
				if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
					screen.blit(Art.smallTree, x * Tile.WIDTH, y * Tile.HEIGHT);
					continue;
				}
				
				screen.blit(Art.testTile, x * Tile.WIDTH, y * Tile.HEIGHT);
			}
		}
	}
}
