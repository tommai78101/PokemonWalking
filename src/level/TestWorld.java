package level;

import resources.Art;
import screen.BaseScreen;
import abstracts.BaseWorld;
import abstracts.Entity;
import abstracts.Tile;

public class TestWorld extends BaseWorld {
	
	final int width;
	final int height;
	
	public TestWorld(int w, int h) {
		super();
		this.width = w;
		this.height = h;
		//this.addEntity(new TestEntity(new Keys()));
	}
	
	@Override
	public void addEntity(Entity e) {
		e.initialize(this);
		tiles.add(e);
	}
	
	public void removeEntity(Entity e) {
		e.isRemoved = true;
	}
	
	@Override
	protected void renderTiles(BaseScreen screen, int x0, int y0, int x1, int y1) {
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				//TODO: Work more on tile stuffs here.
				//draw something outside the world
				if (x < -1 || x >= this.width || y < 0 || y >= this.height) {
					screen.blit(Art.smallTree, x * Tile.WIDTH, y * Tile.HEIGHT - Tile.HEIGHT / 2);
					continue;
				}
				
				screen.blit(Art.grass, x * Tile.WIDTH, y * Tile.HEIGHT - Tile.HEIGHT / 2);
			}
		}
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public void addTile(Tile t) {
	}
}
