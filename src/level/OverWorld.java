package level;

import java.util.ArrayList;
import java.util.List;
import screen.BaseScreen;
import abstracts.Tile;
import abstracts.World;
import entity.Player;

public class OverWorld extends World {
	//Contains overworld specific areas.
	public List<Area> areas = new ArrayList<Area>();
	
	//Overworld properties.
	private boolean invertBitmapColors;
	private int currentAreaSectorID;
	
	/**
	 * Initializes the overworld in the game.
	 * 
	 * All game entities are to be loaded through this method.
	 * 
	 * @param Player
	 *            Takes a Player object. The overworld then loads all related properties in respect to the Player object.
	 * */
	public OverWorld(Player player) {
		//Must initialize all overworld specific properties, such as specific areas, specific dialogues, etc. first.		
		initialize();
		
		//Player
		this.player = player;
		
		//Going to set this area as test default only. This will need to change in the future.
		this.currentArea = this.areas.get(0);
		this.currentArea.setPlayer(player);
		this.currentArea.setDebugDefaultPosition();
		this.currentAreaSectorID = 1;
		//Needs a marker in the area that points to where the area connects together.
		
	}
	
	public void initialize() {
		//There should be a maximum number of areas available for the OverWorld.
		//All areas defined must be placed in WorldConstants.
		
		//TODO: Make it so that all areas are connected together.
		this.areas = WorldConstants.getAllAreas();
		
		//Overworld properties
		this.invertBitmapColors = false;
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
		if (!this.invertBitmapColors)
			this.player.tick();
		this.currentArea.tick();
		
		if (this.currentArea.playerIsInWarpZone()) {
			PixelData data = this.currentArea.getCurrentPixelData();
			this.currentArea = WorldConstants.convertToArea(data.getTargetAreaID());
			this.currentArea.setPlayer(player);
			this.currentArea.setDefaultPosition(data);
			this.invertBitmapColors = true;
			this.player.forceLockWalking();
		}
		if (!this.player.isLockedWalking()) {
			if (this.currentArea.getSectorID() != this.currentAreaSectorID) {
				this.currentAreaSectorID = this.currentArea.getSectorID();
				//This is where you get the latest sector id at.
				System.out.println(currentArea.getSectorID());
			}
			
		}
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
		
		if (this.invertBitmapColors) {
			if (screen.getInvertTick() == (byte) 0x7) {
				screen.setInvertTick((byte) 0x0);
			}
			this.invertBitmapColors = screen.invert();
		}
		
		if (screen.getInvertTick() < (byte) 0x4 || screen.getInvertTick() >= (byte) 0x7)
			player.render(screen, 0, 0);
	}
	
	//	private void renderTiles(BaseScreen screen, Area area, int xPosition, int yPosition, int xOff, int yOff) {
	//		//Unsure at the moment.
	//		//		area.setPosition(xPosition, yPosition);
	//		//		area.renderTiles(screen, -xOff, -yOff);
	//	}
	
	public Tile getTile(int x, int y) {
		/*if (pixels[y * this.width + x] != 0xFF00FF00)
			return new Tree();*/
		return null;
	}
	
	public void addTile(Tile t) {
		//this.tiles.add(t);
	}
}
