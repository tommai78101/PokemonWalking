package level;

import java.util.ArrayList;
import java.util.List;

import resources.Art;
import screen.BaseScreen;
import abstracts.Tile;
import abstracts.World;
import entity.Player;

public class OverWorld extends World {
	
	private static final int MAX_AREAS = 2;

	//Contains overworld specific areas.
	public List<Area> areas = new ArrayList<Area>();
	
	//Already have currentArea and player.

	public OverWorld(Player player) {
		//Must initialize all overworld specific properties, such as specific areas, specific dialogues, etc. first.		
		initialize();
		
		//Player
		this.player = player;
		

		//Going to set this area as test default only. This will need to change in the future.
		this.currentArea = new Area(Art.testArea);
		this.currentArea.setPlayer(player);
		this.currentArea.setDefaultPosition();
		//Needs a marker in the area that points to where the area connects together.
		
		
	}
	
	public void initialize() {
		//There should be a maximum number of areas available for the OverWorld.
		this.areas.add(new Area(Art.testArea));
		this.areas.add(new Area(Art.testArea2));
	}
	
	//Will add this in the future. Currently, the only entity is Player.
	//	public void addEntity(Entity e) {
	//		//e.initialize(this);
	//		//this.tiles.add(e);
	//	}
	
	//Worlds no longer need to calculate total width and height.
	//	public int getTotalWidth() {
	//		int result = 0;
	//		for (Area a : areas) {
	//			if (a != null)
	//				result += a.getWidth();
	//		}
	//		return result;
	//	}
	//	
	//	public int getTotalHeight() {
	//		int result = 0;
	//		for (Area a : areas) {
	//			if (a != null)
	//				result += a.getHeight();
	//		}
	//		return result;
	//	}
	
	//Not sure if these width and height values are useful...
	public int getCurrentAreaWidth() {
		return this.currentArea.getWidth();
	}
	
	public int getCurrentAreaHeight() {
		return this.currentArea.getHeight();
	}
	
	
	
	
	@Override
	public void tick() {
		//this.player.tick();
		this.currentArea.tick();

	}
	
	protected void renderTiles(BaseScreen screen, int x0, int y0, int x1, int y1) {
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				this.currentArea.renderTiles(screen, x, y);
			}
		}
	}
	
	@Override
	public void render(BaseScreen screen, int xPlayerPos, int yPlayerPos) {
		//OverWorld offsets are not set.
		//Setting area offsets with player positions

		screen.setOffset(screen.getWidth() / 2 - Tile.WIDTH, (screen.getHeight() - Tile.HEIGHT) / 2);
		this.currentArea.renderTiles(screen, xPlayerPos, yPlayerPos);
		screen.setOffset(0, 0);
		
	}
	
	private void renderTiles(BaseScreen screen, Area area, int xPosition, int yPosition, int xOff, int yOff) {
		//Unsure at the moment.
		//		area.setPosition(xPosition, yPosition);
		//		area.renderTiles(screen, -xOff, -yOff);
	}
	
	public Tile getTile(int x, int y) {
		/*if (pixels[y * this.width + x] != 0xFF00FF00)
			return new Tree();*/
		return null;
	}
	
	public void addTile(Tile t) {
		//this.tiles.add(t);
	}
}
