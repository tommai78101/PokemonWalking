package level;

import java.util.ArrayList;
import java.util.List;

import resources.Art;
import screen.BaseScreen;
import abstracts.Entity;
import abstracts.Tile;
import abstracts.World;
import entity.Player;

public class OverWorld extends World {
	
	public List<Area> areas = new ArrayList<Area>();
	public String worldName;
	public Area activeArea;
	
	public OverWorld(String name) {
		this.worldName = name;
		//TODO: Work more on this "activeArea" part.
		//What it should do: activeArea points to the area the player is in (making the area active).
		activeArea = new Area(Art.testArea);
		areas.add(activeArea);
	}
	
	public void addEntity(Entity e) {
		//e.initialize(this);
		//this.tiles.add(e);
	}
	
	public int getTotalWidth() {
		int result = 0;
		for (Area a : areas) {
			if (a != null)
				result += a.getWidth();
		}
		return result;
	}
	
	public int getTotalHeight() {
		int result = 0;
		for (Area a : areas) {
			if (a != null)
				result += a.getHeight();
		}
		return result;
	}
	
	public int getActiveAreaWidth() {
		return activeArea.getWidth();
	}
	
	public int getActiveAreaHeight() {
		return activeArea.getHeight();
	}
	
	@Override
	public void tick() {
		//for (Tile e : this.tiles)
		//	((Entity) e).tick();
		//TODO: Pick the active area from the ArrayList<Area>, and point it to this variable here.
		//Then activate the tick().
		activeArea.tick();
		
	}
	
	protected void renderTiles(BaseScreen screen, int x0, int y0, int x1, int y1) {
		/*for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				//TODO: Work more on tile stuffs here.
				//draw something outside the world
				
				if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
					screen.blit(Art.smallTree, x * Tile.WIDTH - Tile.WIDTH, y * Tile.HEIGHT - Tile.HEIGHT);
					continue;
				}
				if (pixels[y * this.width + x] == 0xFF00FF00) {
					screen.blit(Art.testTile, x * Tile.WIDTH - Tile.WIDTH, y * Tile.HEIGHT - Tile.HEIGHT);
				}
				else {
					screen.blit(Art.smallTree, x * Tile.WIDTH - Tile.WIDTH, y * Tile.HEIGHT - Tile.HEIGHT);
				}
				
				
			}
		}*/
		
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				activeArea.renderTiles(screen, x, y);
			}
		}
	}
	
	@Override
	public void render(BaseScreen screen, int xPlayerPos, int yPlayerPos) {
		/*screen.setOffset(xPosition - Tile.WIDTH, yPosition - Tile.HEIGHT);
		renderTiles(screen, xPosition - screen.getWidth(), yPosition - screen.getHeight(), xPosition + screen.getWidth(), yPosition + screen.getHeight());
		screen.setOffset(0, 0);*/
		
		/*screen.setOffset(xPositionInArea * Tile.WIDTH, yPositionInArea * Tile.HEIGHT);
		renderTiles(screen, activeArea, xPositionInArea, yPositionInArea);
		screen.setOffset(0, 0);*/
		
		/*int xOffset = xPlayerPos + screen.getWidth() / 2;
		int yOffset = yPlayerPos + screen.getHeight() / 2;
		renderTiles(screen, activeArea, xPlayerPos, yPlayerPos, xOffset, yOffset);*/
		
		//OverWorld offsets are not set.
		//Setting area offsets with player positions
		//activeArea.setOffset(xPlayerPos, yPlayerPos);
		screen.setOffset(screen.getWidth() / 2 - Tile.WIDTH, (screen.getHeight() - Tile.HEIGHT) / 2);
		activeArea.renderTiles(screen, xPlayerPos, yPlayerPos);
		screen.setOffset(0, 0);
		
	}
	
	private void renderTiles(BaseScreen screen, Area area, int xPosition, int yPosition, int xOff, int yOff) {
		area.setPosition(xPosition, yPosition);
		area.renderTiles(screen, -xOff, -yOff);
	}
	
	public Tile getTile(int x, int y) {
		/*if (pixels[y * this.width + x] != 0xFF00FF00)
			return new Tree();*/
		return null;
	}
	
	public void addTile(Tile t) {
		//this.tiles.add(t);
	}
	
	@Override
	public void transitionTo(World world) {
		
	}
	
	public void initialize(Player player) {
		activeArea.setPlayer(player);
		player.initialize(activeArea);
	}
}
