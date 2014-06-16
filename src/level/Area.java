/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package level;

import java.util.ArrayList;

import obstacle.Obstacle;
import screen.BaseBitmap;
import screen.BaseScreen;
import script.TriggerData;
import abstracts.Tile;
import entity.Player;

public class Area {
	private final int width;
	private final int height;
	private final int[] pixels;
	
	private int xPlayerPosition;
	private int yPlayerPosition;
	private Player player;
	
	private boolean isInWarpZone;
	private boolean isInSectorPoint;
	private PixelData currentPixelData;
	private final int areaID;
	private int sectorID;
	// TODO: Add area type.
	// private int areaType;
	
	private boolean displayExitArrow;
	
	private final ArrayList<ArrayList<PixelData>> areaData = new ArrayList<ArrayList<PixelData>>();
	private final ArrayList<Obstacle> areaObstacles = new ArrayList<Obstacle>();
	private final ArrayList<PixelData> modifiedAreaData = new ArrayList<PixelData>();
	private final ArrayList<TriggerData> triggerDatas = new ArrayList<TriggerData>();
	
	public Area(BaseBitmap bitmap, final int areaID) {
		int[] tempPixels =bitmap.getPixels();
		int triggerSize = tempPixels[0];
		int row = 0;
		int column = 0;
		for (int i = 0; i < triggerSize; i++) {
			column = i + 1;
			int color = tempPixels[column + (bitmap.getHeight()* row)];
			//ID must not be negative. ID = 0 is reserved.
			if (color > 0)
				triggerDatas.add(new TriggerData().loadTriggerData(color));
			if (column >= bitmap.getWidth()) {
				row++;
				column -= bitmap.getWidth();
			}
		}
		//We need to add the row by 1 for the last row with trailing empty trigger IDs.
		row++;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight() - row;
		this.pixels = new int[this.width * this.height];
		System.arraycopy(bitmap.getPixels(), this.width * row, this.pixels, 0, this.pixels.length);
		
		for (int y = 0; y < this.height; y++) {
			areaData.add(new ArrayList<PixelData>());
			for (int x = 0; x < this.width; x++) {
				int pixel = this.pixels[y * this.width + x];
				PixelData px = new PixelData(pixel, x, y);
				if (((pixel >> 24) & 0xFF) == 0x03)
					areaObstacles.add(new Obstacle(px, (pixel >> 16) & 0xFF));
				areaData.get(y).add(px);
			}
		}
		
		this.areaID = areaID;
		this.isInWarpZone = false;
		this.isInSectorPoint = false;
		this.displayExitArrow = false;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Updates the area.
	 * 
	 * @return Nothing.
	 * */
	public void tick() {
		// Since "setPlayer" method isn't always set, there should be checks everywhere to make sure "player" isn't null.
		if (this.player != null) {
			// PixelData data = null;
			if (!this.player.isLockedWalking()) {
				xPlayerPosition = player.getXInArea();
				yPlayerPosition = player.getYInArea();
				if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
					return;
				this.player.setAllBlockingDirections(checkSurroundingData(0, -1), checkSurroundingData(0, 1), checkSurroundingData(-1, 0), checkSurroundingData(1, 0));
				try {
					if (this.player.isInteracting()) {
						switch (this.player.getFacing()) {
							case Player.UP:
								this.player.interact(areaData.get(this.yPlayerPosition - 1).get(xPlayerPosition).getColor());
								break;
							case Player.DOWN:
								this.player.interact(areaData.get(this.yPlayerPosition + 1).get(xPlayerPosition).getColor());
								break;
							case Player.LEFT:
								this.player.interact(areaData.get(this.yPlayerPosition).get(xPlayerPosition - 1).getColor());
								break;
							case Player.RIGHT:
								this.player.interact(areaData.get(this.yPlayerPosition).get(xPlayerPosition + 1).getColor());
								break;
						}
					}
				}
				catch (Exception e) {
					this.player.stopInteraction();
				}
				
				// Target pixel is used to determine what pixel the player is currently standing on
				// (or what pixel the player is currently on top of).
				this.currentPixelData = areaData.get(this.yPlayerPosition).get(xPlayerPosition);
				this.checkCurrentPositionDataAndSetProperties();
			}
			else if (!this.player.isLockedJumping() && this.player.isLockedWalking()) {
				// A
				// This goes with B. (30 lines down below.)
				// It may be possible the player is still in the air, and hasn't done checking if the current pixel
				// data is a ledge or not. This continues the data checking. It's required.
				xPlayerPosition = player.getXInArea();
				yPlayerPosition = player.getYInArea();
				if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
					return;
				this.currentPixelData = areaData.get(this.yPlayerPosition).get(xPlayerPosition);
				this.checkCurrentPositionDataAndSetProperties();
			}
			else {
				this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			}
		}
	}
	
	/**
	 * Checks the pixel data the player is currently on, and sets the tile properties according to the documentation provided. The tile the pixel data is representing determines the properties this will set, and will affect how the game interacts with the player.
	 * 
	 * @return Nothing.
	 * */
	private void checkCurrentPositionDataAndSetProperties() {
		// TODO: Fix this checkup.
		int pixel = this.getCurrentPixelData().getColor();
		int alpha = (pixel >> 24) & 0xFF;
		int red = (pixel >> 16) & 0xFF;
		int green = (pixel >> 8) & 0xFF;
		int blue = pixel & 0xFF;
		switch (alpha) {
			case 0x02: // Ledges
			{
				switch (red) {
					case 0x00: // Bottom
						this.player.setLockJumping(red, green, blue, Player.UP, Player.DOWN);
						break;
					case 0x01: // Bottom Left
						// this.player.setLockJumping(red, green, blue, Player.UP, Player.DOWN);
						break;
					case 0x02: // left
						this.player.setLockJumping(red, green, blue, Player.LEFT, Player.RIGHT);
						break;
					case 0x03: // top left
						break;
					case 0x04: // top
						if (this.checkIfValuesAreAllowed(this.getSurroundingTileID(0, -1), 0x01))
							this.player.setLockJumping(red, green, blue, Player.DOWN, Player.UP);
						break;
					case 0x05: // top right
						break;
					case 0x06: // right
						this.player.setLockJumping(red, green, blue, Player.RIGHT, Player.LEFT);
						break;
					case 0x07: // bottom right
						break;
					default:
						break;
				}
				break;
			}
			case 0x04: // Determines warp zone.
				if (!this.player.isLockedWalking()) {
					this.isInWarpZone = true;
				}
				break;
			case 0x05: // Area Connection Point.
				if (!this.player.isLockedWalking() && !this.isInWarpZone) {
					this.isInSectorPoint = true;
					this.sectorID = this.currentPixelData.getTargetSectorID();
				}
				break;
			case 0x07: // Water tiles. Checks to see if player is in the water.
				if (!this.player.isInWater())
					this.player.goesInWater();
				break;
			case 0x0A: // House Doors are a type of warp zones.
				if (!this.player.isLockedWalking()) {
					this.isInWarpZone = true;
				}
				break;
			case 0x0C: // Carpet Indoors
				//
				// this.displayExitArrow = true;
				break;
			case 0x0D: // Carpet Outdoors
				// this.displayExitArrow = true;
				break;
			default:
				// If no special tiles, then it will keep reseting the flags.
				if (!this.player.isLockedWalking() || !this.player.isLockedJumping()) {
					this.isInWarpZone = false;
					this.isInSectorPoint = false;
				}
				// This is to check to see if player has left the water.
				if (this.player.isInWater())
					this.player.leavesWater();
				break;
		}
	}
	
	/**
	 * Checks the pixel data and sets properties according to the documentation provided. The tile the pixel data is representing determines whether it should allow or block the player from walking towards it.
	 * 
	 * <p>
	 * In other words, this is the method call that works out the collision detection/response in the game.
	 * 
	 * @param xOffset
	 *            Sets the offset of the PixelData it should check by the X axis.
	 * @param yOffset
	 *            Sets the offset of the PixelData it should check by the Y axis.
	 * @return The value determining if this PixelData is to block or allow the player to pass/walk/jump through. Returns true to allow player to walk from the player's last position to this tile. Returns false to block the player from walking from the player's last position to this tile.
	 * */
	private boolean checkSurroundingData(int xOffset, int yOffset) {
		PixelData data = null;
		try {
			data = this.areaData.get(this.yPlayerPosition + yOffset).get(this.xPlayerPosition + xOffset);
		}
		catch (Exception e) {
			// This means it is out of the area boundaries.
			data = null;
		}
		if (data != null) {
			int color = data.getColor();
			int alpha = (color >> 24) & 0xFF;
			int red = (color >> 16) & 0xFF;
			// int green = (color >> 8) & 0xFF;
			// int blue = color & 0xFF;
			switch (alpha) {
				case 0x01: // Paths
					return false;
				case 0x02: // Ledge
				{
					switch (red) {
					/*
					 * TODO: Incorporate pixel data facingsBlocked variable to this section. Currently, the facingsBlocked[] variable for each pixel data isn't used.
					 */
						case 0x00: { // Bottom
							int y = this.yPlayerPosition + yOffset;
							if (this.checkIfValuesAreAllowed((this.getTileColor(0, 2) >> 24) & 0xFF, 0x02, 0x03))
								return true;
							if (this.yPlayerPosition < y)
								return false;
							return true;
						}
						case 0x01: // Bottom Left
							return true;
						case 0x02: {// Left
							int x = this.xPlayerPosition + xOffset;
							if (this.checkIfValuesAreAllowed((this.getTileColor(-2, 0) >> 24) & 0xFF, 0x02, 0x03))
								return true;
							if (this.xPlayerPosition > x)
								return false;
							return true;
						}
						case 0x03: // Top Left
							return true;
						case 0x04: {// Top
							int y = this.yPlayerPosition + yOffset;
							if (this.yPlayerPosition > y)
								return false;
							if (this.checkIfValuesAreAllowed((this.getTileColor(0, -2) >> 24) & 0xFF, 0x02))
								return true;
							if (this.checkIfValuesAreAllowed((this.getTileColor(-1, 0) >> 16) & 0xFF, 0x04))
								return false;
							if (this.checkIfValuesAreAllowed((this.getTileColor(1, 0) >> 16) & 0xFF, 0x04))
								return false;
							if (this.checkIfValuesAreAllowed((this.getTileColor(0, -2) >> 24) & 0xFF, 0x03))
								return true;
							return true;
						}
						case 0x05: // Top Right
							return true;
						case 0x06: { // Right
							int x = this.xPlayerPosition + xOffset;
							if (this.checkIfValuesAreAllowed((this.getTileColor(2, 0) >> 24) & 0xFF, 0x02, 0x03))
								return true;
							if (this.xPlayerPosition < x)
								return false;
							return true;
						}
						case 0x07: // Bottom Right
							// TODO: DO SOMETHING WITH WATER, MAKE PLAYER SURF!
							return false;
							
							// ------------------------- MOUNTAIN LEDGES ------------------------
						case 0x0C:
							int y = this.yPlayerPosition + yOffset;
							if (this.yPlayerPosition > y)
								return false;
							if (this.checkIfValuesAreAllowed((this.getTileColor(-1, 0) >> 16) & 0xFF, 0x0C))
								return false;
							if (this.checkIfValuesAreAllowed((this.getTileColor(1, 0) >> 16) & 0xFF, 0x0C))
								return false;
							return true;
						default:
							break;
					}
					break;
				}
				case 0x03: // Obstacle
					switch (red) {
						default:
							if (this.player.isInteracting())
								return true;
							if (player.isFacingAt(this.xPlayerPosition + xOffset, this.yPlayerPosition + yOffset)) {
								if ((player.keys.Z.keyStateDown || player.keys.SLASH.keyStateDown) && (!player.keys.Z.lastKeyState || !player.keys.SLASH.lastKeyState)) {
									this.player.startInteraction();
									player.keys.Z.lastKeyState = true;
									player.keys.SLASH.lastKeyState = true;
								}
							}
							return true;
					}
				case 0x04: // Warp point
					return false;
				case 0x05: // Area Connection point.
					return false;
				case 0x06:
					return false;
				case 0x07: // Water
					// TODO: Add something that detects a special boolean value
					// in order to let the player move on water.
					return false;
				case 0x09: // House
					return true;
				case 0x0A: // House Door
					return false;
				case 0x0B: // Item
					if (this.player.isInteracting())
						return true;
					if (player.isFacingAt(this.xPlayerPosition + xOffset, this.yPlayerPosition + yOffset)) {
						if ((player.keys.Z.keyStateDown || player.keys.SLASH.keyStateDown) && (!player.keys.Z.lastKeyState || !player.keys.SLASH.lastKeyState)) {
							this.player.startInteraction();
							player.keys.Z.lastKeyState = true;
							player.keys.SLASH.lastKeyState = true;
						}
					}
					return true; // Cannot go through items on the ground.
				default: // Any other type of tiles should be walkable, for no apparent reasons.
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Renders the bitmap tiles based on the given pixel data.
	 * 
	 * <p>
	 * Note that this is where the bitmap animation works by updating the bitmap after it has been rendered to the screen.
	 * 
	 * @param screen
	 *            The screen display where the bitmaps are to output to.
	 * @param xOff
	 *            The X offset based on the player's X position in absolute world coordinates. The absolute world coordinates mean the precise X position on the Canvas.
	 * @param yOff
	 *            The Y offset based on the player's Y position in absolute world coordinates. The absolute world coordinates mean the precise Y position on the Canvas.
	 * @return Nothing.
	 * 
	 * */
	public void renderTiles(BaseScreen screen, int xOff, int yOff) {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				PixelData data = this.areaData.get(y).get(x);
				screen.blitBiome(data.getBiomeBitmap(), x * Tile.WIDTH - xOff, y * Tile.HEIGHT - yOff, data);
				screen.blitBiome(data.getBitmap(), x * Tile.WIDTH - xOff, y * Tile.HEIGHT - yOff, data);
				if (x == this.player.getXInArea() && y == this.player.getYInArea() && ((((data.getColor() >> 24) & 0xFF) == 0x0C) || (((data.getColor() >> 24) & 0xFF) == 0x04)))
					renderExitArrow(screen, xOff, yOff, data, x, y);
				data.tick();
			}
		}
	}
	
	// Getters/Setters
	public int getPlayerX() {
		return this.player.getX();
	}
	
	public int getPlayerXInArea() {
		return this.player.getXInArea();
	}
	
	public void setPlayerX(int x) {
		this.xPlayerPosition = x;
	}
	
	public void setSectorID(int value) {
		this.sectorID = value;
	}
	
	public int getPlayerY() {
		return this.player.getY();
	}
	
	public int getPlayerYInArea() {
		return this.player.getYInArea();
	}
	
	public void setPlayerY(int y) {
		this.yPlayerPosition = y;
	}
	
	public void setDebugDefaultPosition() {
		// When the game starts from the very beginning, the player must always start from the very first way point.
		SET_LOOP:
		for (ArrayList<PixelData> y : this.areaData) {
			for (PixelData x : y) {
				if (((x.getColor() >> 24) & 0xFF) == 0x01) {
					player.setAreaPosition(x.xPosition, x.yPosition);
					break SET_LOOP;
				}
			}
		}
	}
	
	/**
	 * Sets the player's position according to the given warp point pixel data.
	 * 
	 * It's mostly used in conjunction with initializing the area with the player position set.
	 * 
	 * @param data
	 *            The pixel data used to set the default player's position.
	 * */
	public void setDefaultPosition(PixelData data) {
		int color = data.getColor();
		int alpha = (color >> 24) & 0xFF;
		switch (alpha) {
			case 0x04: // Warp point
			case 0x0A: // Door
			case 0x0C: // Carpet (Indoors)
			case 0x0D: // Carpet (Outdoors)
			{
				int green = (color >> 8) & 0xFF;
				int blue = color & 0xFF;
				this.player.setAreaPosition(green, blue);
				break;
			}
		}
	}
	
	public boolean playerIsInWarpZone() {
		return this.isInWarpZone;
	}
	
	public void playerWentPastWarpZone() {
		this.isInWarpZone = false;
	}
	
	public PixelData getCurrentPixelData() {
		// Return the pixel data the player is currently on top of.
		return this.currentPixelData;
		// return this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
	}
	
	public int getAreaID() {
		return this.areaID;
	}
	
	public boolean playerIsInConnectionPoint() {
		return this.isInSectorPoint;
	}
	
	public boolean playerHasLeftConnectionPoint() {
		if (this.isInSectorPoint) {
			if (this.player.isLockedWalking()) {
				// Leaving
				return true;
			}
		}
		return false;
	}
	
	public int getSectorID() {
		return this.sectorID;
	}
	
	/**
	 * Obtains the tile ID of the tile being offset by the player's position.
	 * 
	 * @param xOffset
	 *            The X value offset from the player's X position.
	 * @param yOffset
	 *            The Y value offset from the player's Y position.
	 * @return The tile ID of the tile chosen.
	 * */
	public int getSurroundingTileID(int xOffset, int yOffset) {
		PixelData data;
		try {
			data = this.areaData.get(yPlayerPosition + yOffset).get(xPlayerPosition + xOffset);
		}
		catch (Exception e) {
			return -1;
		}
		if (data != null) {
			return (data.getColor() >> 24) & 0xFF;
		}
		return -1;
	}
	
	/**
	 * Compares target tile ID with other multiple tile IDs to see if they are one of many tiles that the player is allowed to walk on, or when the conditions are right for the player to move on the tile.
	 * 
	 * @param targetIDToCompare
	 *            The target tile ID used to test and see if it's allowable for the player to move/walk on. Use getSurroundingTileID() to fetch the target tile ID.
	 * @param multipleTileIDs
	 *            The many tile IDs that are to be compared to the target tile ID to see if the target tile ID is one of the allowed tile IDs. You may use as many tile IDs for comparison as you wished.
	 * @return True, if the target tile ID is one of the many tile IDs that's allowable. False, if none of the tile IDs match the target tile ID.
	 * 
	 * */
	public boolean checkIfValuesAreAllowed(int targetIDToCompare, int... multipleTileIDs) {
		boolean result = false;
		for (int a : multipleTileIDs) {
			if (targetIDToCompare == a) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public int getTileColor(int xOffset, int yOffset) {
		PixelData data;
		try {
			data = this.areaData.get(yPlayerPosition + yOffset).get(xPlayerPosition + xOffset);
		}
		catch (Exception e) {
			return 0;
		}
		if (data != null) {
			return (data.getColor());
		}
		return 0;
	}
	
	public void setPixelData(PixelData data, int xPosition, int yPosition) {
		data.xPosition = xPosition;
		data.yPosition = yPosition;
		this.areaData.get(yPosition).set(xPosition, data);
		this.modifiedAreaData.add(data);
	}
	
	public void loadModifiedPixelDataList() {
		for (PixelData px : this.modifiedAreaData) {
			this.areaData.get(px.yPosition).set(px.xPosition, px);
		}
	}
	
	public ArrayList<PixelData> getModifiedPixelDataList() {
		return this.modifiedAreaData;
	}
	
	public PixelData getPixelData(int x, int y) {
		return this.areaData.get(y).get(x);
	}
	
	public ArrayList<ArrayList<PixelData>> getAllPixelDatas() {
		return this.areaData;
	}
	
	public boolean isDisplayingExitArrow() {
		return this.displayExitArrow;
	}
	
	public ArrayList<Obstacle> getObstaclesList() {
		return this.areaObstacles;
	}
	
	
	// --------------------- PRIVATE METHODS ----------------------
	
	private void renderExitArrow(BaseScreen screen, int xOff, int yOff, PixelData data, int x, int y) {
		int height = this.getHeight();
		if (y + 1 == height && this.player.getFacing() == Player.DOWN) {
			screen.blitBiome(data.getBiomeBitmap(), x * Tile.WIDTH - xOff + 4, (y + 1) * Tile.HEIGHT - yOff + 2, data);
			this.displayExitArrow = true;
		}
		else if (y == 0 && this.player.getFacing() == Player.UP) {
			// TODO: Draw exit arrow point upwards.
		}
		else
			this.displayExitArrow = false;
	}
}
