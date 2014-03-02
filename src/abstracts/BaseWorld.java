package abstracts;

import java.util.ArrayList;
import java.util.List;

import screen.BaseScreen;

public abstract class BaseWorld {
	
	public List<Tile> tiles = new ArrayList<Tile>();
	public List<Entity> entities = new ArrayList<Entity>();
	
	//If parameters, width and height, are allowed, it's mostly for setting up boundaries.
	//In our case, we don't have boundaries in acts. Only worlds.
	//	public BaseWorld() {
	//		//this.addEntity(new TestEntity());
	//	}
	
	public void tick() {
		for (int i = 0; i < tiles.size(); i++) {
			Entity e = (Entity) tiles.get(i);
			if (!e.isRemoved) {
				e.tick();
			}
			if (e.isRemoved) {
				tiles.remove(i--);
			}
		}
	}
	
	public void render(BaseScreen screen, int x, int y) {
		int gridX0 = x / Tile.WIDTH - 1;
		int gridY0 = y / Tile.HEIGHT - 1;
		int gridX1 = (x + screen.getWidth()) / Tile.WIDTH + 2;
		int gridY1 = (y + screen.getHeight()) / Tile.HEIGHT + 2;
		
		screen.setOffset(-x, -y);
		
		renderTiles(screen, gridX0, gridY0, gridX1, gridY1);
		
		render(screen);
		
		screen.setOffset(0, 0);
	}
	
	public void render(BaseScreen screen) {
		for (Tile t : this.tiles)
			t.render(screen);
		for (Entity e : this.entities)
			e.render(screen);
	}
	
	public Tile getTile(int xPosition, int yPosition) {
		Tile result = null;
		for (Tile t : tiles) {
			if (t.xPosition == xPosition && t.yPosition == yPosition) {
				result = t;
				break;
			}
		}
		return result;
	}
	
	public Entity getEntity(byte entityId) {
		Entity result = null;
		for (Entity e : this.entities) {
			if (entityId == e.getType()) {
				result = e;
				break;
			}
		}
		return result;
	}
	
	protected abstract void renderTiles(BaseScreen screen, int x0, int y0, int x1, int y1);
	
	public abstract void addEntity(Entity e);
	
	public abstract void addTile(Tile t);
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
}
