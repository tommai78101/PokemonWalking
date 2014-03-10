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
	public Area activeArea;
	public Player player;
	
	public OverWorld(Player player) {
		this.player = player;
		//TODO: Work more on this "activeArea" part.
		//What it should do: activeArea points to the area the player is in (making the area active).
		activeArea = new Area(Art.testArea);
		Area secondary = new Area(Art.testArea2);
		
		//Needs a marker in the area that points to where the area connects together.
		
		areas.add(activeArea);
		areas.add(secondary);
		
		initialize();
	}
	
	public void initialize() {
		//Primarily the default area the player is to be in.
		activeArea.setPlayer(player);
		activeArea.setDefaultPosition(player);
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
		//TODO: Pick the active area from the ArrayList<Area>, and point it to this variable here.
		//Then activate the tick().		
		activeArea.tick();
	}
	
	protected void renderTiles(BaseScreen screen, int x0, int y0, int x1, int y1) {
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				activeArea.renderTiles(screen, x, y);
			}
		}
	}
	
	@Override
	public void render(BaseScreen screen, int xPlayerPos, int yPlayerPos) {
		//OverWorld offsets are not set.
		//Setting area offsets with player positions

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
	
	public void transitionTo(World world, Area area) {
		
	}

	public void initialize(Player player) {
		activeArea.setPlayer(player);
		player.initialize(activeArea);
	}
}
