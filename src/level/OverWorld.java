/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package level;

import java.util.ArrayList;
import java.util.List;

import screen.BaseScreen;
import abstracts.Tile;
import abstracts.World;
import dialogue.Dialogue;
import entity.Player;

public class OverWorld extends World {
	// Contains overworld specific areas.
	public List<Area> areas = new ArrayList<Area>();

	// Overworld properties.
	private boolean invertBitmapColors;
	private int currentAreaSectorID;
	private Dialogue dialogue;

	/**
	 * Initializes the overworld in the game.
	 * 
	 * All game entities are to be loaded through this method.
	 * 
	 * @param Player
	 *            Takes a Player object. The overworld then loads all related properties in respect to the Player object.
	 * */
	public OverWorld(Player player, Dialogue dialogue) {
		// There should be a maximum number of areas available for the OverWorld.
		// All areas defined must be placed in WorldConstants.
		this.areas = WorldConstants.getAllAreas();

		// Overworld properties
		this.invertBitmapColors = false;

		// Player
		this.player = player;

		// Going to set this area as test default only. This will need to change in the future.
		this.currentArea = this.areas.get(0);
		this.currentArea.setPlayer(player);
		this.currentArea.setDebugDefaultPosition();
		this.setCurrentAreaSector(-1);
		// TODO: Add a method that executes according to the sector ID. Basically,
		// it needs to tell the player that they entered a new sector.
		// Needs a marker in the area that points to where the area connects together.

		// Dialogue
		this.dialogue = dialogue;
	}

	// Will add this in the future. Currently, the only entity is Player.
	// public void addEntity(Entity e) {
	// //e.initialize(this);
	// //this.tiles.add(e);
	// }

	// Worlds no longer need to calculate total width and height.
	// public int getTotalWidth() {
	// int result = 0;
	// for (Area a : areas) {
	// if (a != null)
	// result += a.getWidth();
	// }
	// return result;
	// }
	//
	// public int getTotalHeight() {
	// int result = 0;
	// for (Area a : areas) {
	// if (a != null)
	// result += a.getHeight();
	// }
	// return result;
	// }

	// Not sure if these width and height values are useful...
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
			this.currentArea = WorldConstants.convertToArea(areas, data.getTargetAreaID());
			this.currentArea.setPlayer(player);
			this.currentArea.setDefaultPosition(data);
			this.invertBitmapColors = true;
			this.player.forceLockWalking();
		}
		if (!this.player.isLockedWalking()) {
			if (this.currentArea.getSectorID() != this.currentAreaSectorID) {
				this.currentAreaSectorID = this.currentArea.getSectorID();
				// This is where you get the latest sector id at.
				System.out.println("Area: " + this.currentArea.getAreaID() + " Sector: " + currentArea.getSectorID());
			}

		}
		// dialogue.displayDialog("Hello World. Press Z, X, /, or . to continue. For the next release, I'll be cleaning up the codes. No more features until then.", 1);
		// dialogue.displayDialog("Hello World. I'm coming for you. Yeah!", 2);
		// if (dialogue.isDisplayingDialogue()) {
		// dialogue.setCheckpoint(2, true);
		// }
		// if (!dialogue.isDialogCheckpointSet(1))

		// TODO: Fix the awkward interaction caused by so many states not working properly.
		if (dialogue.isDoneDisplayingDialogue()) {
			dialogue.reset();
			this.player.stopInteraction();
		} else if (this.player.isInteracting() && this.player.getInteractionID() != 0) {
			int alpha = (player.getInteractionID() >> 24) & 0xFF;
			switch (alpha) {
			case 0x08: {// Sign
				if (!dialogue.isDisplayingDialogue()) {
					dialogue.createText(alpha, this.player.getInteractionID() & 0xFFFF);
				}
				break;
			}
			case 0x0B: {// Item
				int red_itemType = (player.getInteractionID() >> 16) & 0xFF;
				if (!dialogue.isDisplayingDialogue()) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							PixelData data = currentArea.getCurrentPixelData();
							switch (player.getFacing()) {
							case Player.UP:
								currentArea.setPixelData(data, player.getXInArea(), player.getYInArea() - 1);
								break;
							case Player.DOWN:
								currentArea.setPixelData(data, player.getXInArea(), player.getYInArea() + 1);
								break;
							case Player.LEFT:
								currentArea.setPixelData(data, player.getXInArea() - 1, player.getYInArea());
								break;
							case Player.RIGHT:
								currentArea.setPixelData(data, player.getXInArea() + 1, player.getYInArea());
								break;
							}
						}
					}).start();
					dialogue.createText(alpha, red_itemType);
				}
				break;
			}
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
		// OverWorld offsets are not set.
		// Setting area offsets with player positions

		screen.setOffset(screen.getWidth() / 2 - Tile.WIDTH, (screen.getHeight() - Tile.HEIGHT) / 2);
		this.currentArea.renderTiles(screen, xPlayerPos, yPlayerPos);
		screen.setOffset(0, 0);

		if (this.invertBitmapColors) {
			if (screen.getRenderingEffectTick() == (byte) 0x7) {
				screen.setRenderingEffectTick((byte) 0x0);
			}
			this.invertBitmapColors = screen.invert();
		}

		if (screen.getRenderingEffectTick() < (byte) 0x4 || screen.getRenderingEffectTick() >= (byte) 0x7)
			player.render(screen, 0, 0);
	}

	// private void renderTiles(BaseScreen screen, Area area, int xPosition, int yPosition, int xOff, int yOff) {
	// //Unsure at the moment.
	// // area.setPosition(xPosition, yPosition);
	// // area.renderTiles(screen, -xOff, -yOff);
	// }

	public Tile getTile(int x, int y) {
		/*
		 * if (pixels[y * this.width + x] != 0xFF00FF00) return new Tree();
		 */
		return null;
	}

	public void addTile(Tile t) {
		// this.tiles.add(t);
	}

	// ---------------------------------------------------------------------------------------
	// Private methods

	private void setCurrentAreaSector(int i) {
		this.currentAreaSectorID = i;
	}
}
