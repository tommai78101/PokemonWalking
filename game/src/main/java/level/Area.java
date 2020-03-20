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
import java.util.HashSet;
import java.util.Set;

import abstracts.Entity;
import abstracts.Obstacle;
import entity.Player;
import interfaces.Tileable;
import screen.BaseBitmap;
import screen.Scene;
import script.TriggerData;

public class Area extends Entity {
	private final int width;
	private final int height;
	private final int[] pixels;

	private int xPlayerPosition;
	private int yPlayerPosition;
	private int oldXTriggerPosition;
	private int oldYTriggerPosition;
	private Player player;

	private boolean isInWarpZone;
	private boolean isInSectorPoint;
	private PixelData currentPixelData;
	private final int areaID;
	private int sectorID;
	private String areaName;
	// TODO: Add area type.
	// private int areaType;

	private boolean isExitArrowDisplayed;
	private boolean isTriggerBeingTriggered;
	private TriggerData trigger;

	private final ArrayList<ArrayList<PixelData>> areaData = new ArrayList<>();
	private final ArrayList<Obstacle> areaObstacles = new ArrayList<>();
	private final Set<PixelData> modifiedAreaData = new HashSet<>();
	private final ArrayList<TriggerData> triggerDatas = new ArrayList<>();

	public Area(BaseBitmap bitmap, final int areaID) {
		int[] tempPixels = bitmap.getPixels();
		int triggerSize = tempPixels[0];
		int row = 0;
		int column = 0;
		for (int i = 0; i < triggerSize; i++) {
			column = i + 1;
			int color = tempPixels[column + (bitmap.getHeight() * row)];
			// ID must not be negative. ID = 0 is reserved.
			if (color > 0)
				triggerDatas.add(new TriggerData().loadTriggerData(color));
			if (column >= bitmap.getWidth()) {
				row++;
				column -= bitmap.getWidth();
			}
		}
		// We need to add the row by 1 for the last row with trailing empty trigger IDs.
		row++;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight() - row;
		this.pixels = new int[this.width * this.height];
		System.arraycopy(bitmap.getPixels(), this.width * row, this.pixels, 0, this.pixels.length);

		for (int y = 0; y < this.height; y++) {
			areaData.add(new ArrayList<PixelData>());
			for (int x = 0; x < this.width; x++) {
				int pixel = this.pixels[y * this.width + x];

				if (Obstacle.test(pixel))
					areaObstacles.add(Obstacle.build(pixel, x, y));

				PixelData pixelData = new PixelData(pixel, x, y);
				areaData.get(y).add(pixelData);
			}
		}

		this.areaID = areaID;
		this.isInWarpZone = false;
		this.isInSectorPoint = false;
		this.isExitArrowDisplayed = false;
		this.isTriggerBeingTriggered = false;
		this.areaName = "";
	}

	public Area(BaseBitmap bitmap, final int areaID, final String areaName) {
		this(bitmap, areaID);
		this.areaName = areaName;
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

	public void setAreaName(String name) {
		this.areaName = name;
	}

	public String getAreaName() {
		return this.areaName;
	}

	/**
	 * Updates the area.
	 * 
	 * @return Nothing.
	 */
	@Override
	public void tick() {
		// Since "setPlayer" method isn't always set, there should be checks everywhere
		// to make sure "player" isn't null.
		if (this.player == null)
			return;

		// PixelData data = null;
		if (this.isTriggerBeingTriggered) {
			this.xPlayerPosition = this.player.getXInArea();
			this.yPlayerPosition = this.player.getYInArea();

			//Do some bounds checking on the X and Y player positions.
			boolean isXOutOfBounds = this.xPlayerPosition < 0 || this.xPlayerPosition >= this.width;
			boolean isYOutOfBounds = this.yPlayerPosition < 0 || this.yPlayerPosition >= this.height;
			if (isXOutOfBounds || isYOutOfBounds)
				return;

			this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			this.checkCurrentPositionDataAndSetProperties(this.currentPixelData);
		}
		else {
			if (!this.player.isLockedWalking()) {
				if (this.trigger == null)
					this.trigger = checkForTrigger(this.xPlayerPosition, this.yPlayerPosition);
				else
					this.isTriggerBeingTriggered = true;
			}
			else {
				this.oldXTriggerPosition = -1;
				this.oldYTriggerPosition = -1;
			}
		}

		if (this.isTriggerBeingTriggered && this.trigger != null)
			this.handleTriggerActions();
		else if ((this.isTriggerBeingTriggered && this.trigger == null) || !this.isTriggerBeingTriggered) {
			this.isTriggerBeingTriggered = false;
			this.handlePlayerActions();
		}
	}

	private void handleTriggerActions() {
		if (!trigger.isFinished()) {
			this.player.enableAutomaticMode();
			trigger.tick(this, xPlayerPosition, yPlayerPosition);
		}
		else {
			this.player.disableAutomaticMode();
			this.oldXTriggerPosition = this.xPlayerPosition;
			this.oldYTriggerPosition = this.yPlayerPosition;
			this.isTriggerBeingTriggered = false;
			trigger = null;
		}
	}

	private void handlePlayerActions() {
		if (!this.player.isLockedWalking()) {
			//Update the area's X and Y player position from the Player object.
			this.xPlayerPosition = this.player.getXInArea();
			this.yPlayerPosition = this.player.getYInArea();

			//Do some bounds checking on the X and Y player positions.
			boolean isXOutOfBounds = this.xPlayerPosition < 0 || this.xPlayerPosition >= this.width;
			boolean isYOutOfBounds = this.yPlayerPosition < 0 || this.yPlayerPosition >= this.height;
			if (isXOutOfBounds || isYOutOfBounds)
				return;

			// Target pixel is used to determine what pixel the player is currently standing
			// on (or what pixel the player is currently on top of).
			this.player.handleSurroundingTiles(this);
			this.checkCurrentPositionDataAndSetProperties(this.getPixelData(xPlayerPosition, yPlayerPosition));
		}
		else if (!this.player.isLockedJumping() && this.player.isLockedWalking()) {
			// A
			// This goes with B. (30 lines down below.)
			// It may be possible the player is still in the air, and hasn't done checking
			// if the current pixel data is a ledge or not. This continues the data checking. It's required.
			this.xPlayerPosition = this.player.getXInArea();
			this.yPlayerPosition = this.player.getYInArea();

			//Do some bounds checking on the X and Y player positions.
			boolean isXOutOfBounds = this.xPlayerPosition < 0 || this.xPlayerPosition >= this.width;
			boolean isYOutOfBounds = this.yPlayerPosition < 0 || this.yPlayerPosition >= this.height;
			if (isXOutOfBounds || isYOutOfBounds)
				return;

			this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(xPlayerPosition);
			this.checkCurrentPositionDataAndSetProperties(this.getCurrentPixelData());
		}
		else {
			this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			this.checkCurrentPositionDataAndSetProperties(this.getCurrentPixelData());
		}
	}

	private TriggerData checkForTrigger(int playerX, int playerY) {
		for (TriggerData t : this.triggerDatas) {
			if (t.x == playerX && t.y == playerY && (!t.isFinished() || t.isOnRepeat())) {
				if (this.oldXTriggerPosition == t.x && this.oldYTriggerPosition == t.y)
					return null;
				return new TriggerData(t).reset();
			}
		}
		return null;
	}

	/**
	 * Checks the pixel data the player is currently on, and sets the tile properties according to the documentation provided. The tile the pixel data is representing determines the properties this will set, and will affect how the game interacts with the player.
	 * 
	 * @return Nothing.
	 */
	public void checkCurrentPositionDataAndSetProperties(PixelData data) {
		// TODO: Fix this checkup.
		int pixel = data.getColor();
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
			case 0x09: // House Doors are a type of warp zones.
				if (!this.player.isLockedWalking()) {
					this.isInWarpZone = true;
				}
				break;
			case 0x0B: // Carpet Indoors
				// this.displayExitArrow = true;
				break;
			case 0x0C: // Carpet Outdoors
				// this.displayExitArrow = true;
				break;
			case 0x0D: // Default starting position
				this.setPixelData(
					new PixelData(0x01000000, this.currentPixelData.xPosition, this.currentPixelData.yPosition),
					this.currentPixelData.xPosition, this.currentPixelData.yPosition
				);
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
	 */
	@Override
	public void render(Scene screen, int xOff, int yOff) {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				PixelData data = this.areaData.get(y).get(x);
				screen.blitBiome(data.getBiomeBitmap(), x * Tileable.WIDTH - xOff, y * Tileable.HEIGHT - yOff, data);
				screen.blitBiome(data.getBitmap(), x * Tileable.WIDTH - xOff, y * Tileable.HEIGHT - yOff, data);
				if (x == this.player.getXInArea() && y == this.player.getYInArea()
					&& ((((data.getColor() >> 24) & 0xFF) == 0x0B) || (((data.getColor() >> 24) & 0xFF) == 0x04)))
					renderExitArrow(screen, xOff, yOff, data, x, y);
				data.tick();
			}
		}

		if (this.trigger != null) {
			screen.setOffset(0, 0);
			this.trigger.render(screen, screen.getBufferedImage().createGraphics());
			screen.setOffset(screen.getWidth() / 2 - Tileable.WIDTH, (screen.getHeight() - Tileable.HEIGHT) / 2);
		}
	}

	// Getters/Setters
	public int getPlayerX() {
		return this.player.getX();
	}

	public int getPlayerXInArea() {
		this.xPlayerPosition = this.player.getXInArea();
		return this.xPlayerPosition;
	}

	public void setPlayerX(int x) {
		this.xPlayerPosition = x;
		this.player.setAreaX(x);
	}

	public void setSectorID(int value) {
		this.sectorID = value;
	}

	public int getPlayerY() {
		return this.player.getY();
	}

	public int getPlayerYInArea() {
		this.yPlayerPosition = this.player.getYInArea();
		return this.yPlayerPosition;
	}

	public void setPlayerY(int y) {
		this.yPlayerPosition = y;
		this.player.setAreaY(y);
	}

	public void setDebugDefaultPosition() {
		// When the game starts from the very beginning, the player must always start
		// from the very first way point.
		SET_LOOP:
		for (ArrayList<PixelData> pixelDataRow : this.areaData) {
			for (PixelData pixelData : pixelDataRow) {
				if (((pixelData.getColor() >> 24) & 0xFF) == 0x0D) {
					this.setPlayerX(pixelData.xPosition);
					this.setPlayerY(pixelData.yPosition);
					this.currentPixelData = this.areaData.get(pixelData.yPosition).get(pixelData.xPosition);
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
	 */
	public void setDefaultPosition(PixelData data) {
		int color = data.getColor();
		int alpha = (color >> 24) & 0xFF;
		switch (alpha) {
			case 0x04: // Warp point
			case 0x09: // Door
			case 0x0B: // Carpet (Indoors)
			case 0x0C: // Carpet (Outdoors)
			case 0x0D: // Default starting point.
			{
				int green = (color >> 8) & 0xFF;
				int blue = color & 0xFF;
				this.setPlayerX(green);
				this.setPlayerY(blue);
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
	 */
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
	 */
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
		this.modifiedAreaData.clear();
	}

	public Set<PixelData> getModifiedPixelDataList() {
		return this.modifiedAreaData;
	}

	public PixelData getPixelData(int x, int y) {
		return this.areaData.get(y).get(x);
	}

	public ArrayList<ArrayList<PixelData>> getAllPixelDatas() {
		return this.areaData;
	}

	public boolean isDisplayingExitArrow() {
		return this.isExitArrowDisplayed;
	}

	public ArrayList<Obstacle> getObstaclesList() {
		return this.areaObstacles;
	}

	public Player getPlayer() {
		return this.player;
	}

	public TriggerData getTriggerData() {
		return this.trigger;
	}

	public boolean isBeingTriggered() {
		return this.isTriggerBeingTriggered;
	}

	// --------------------- OVERRIDDEN METHODS -------------------

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof String) {
			String str = (String) obj;
			if (!(this.areaName.equals(str))) {
				return false;
			}
			return true;
		}
		else if (obj != null && obj instanceof Integer) {
			Integer BigInt = (Integer) obj;
			if (!(this.areaID == BigInt.intValue())) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		byte[] bytes = this.areaName.getBytes();
		int hash = 1;
		for (int i = 0; i < bytes.length; i++) {
			hash = hash * 3 + bytes[i];
		}
		return hash;
	}

	// --------------------- PRIVATE METHODS ----------------------

	private void renderExitArrow(Scene screen, int xOff, int yOff, PixelData data, int x, int y) {
		int height = this.getHeight();
		if (y + 1 == height && this.player.getFacing() == Player.DOWN) {
			screen.blitBiome(data.getBiomeBitmap(), x * Tileable.WIDTH - xOff + 4, (y + 1) * Tileable.HEIGHT - yOff + 2, data);
			this.isExitArrowDisplayed = true;
		}
		else if (y == 0 && this.player.getFacing() == Player.UP) {
			// TODO: Draw exit arrow point upwards.
		}
		else
			this.isExitArrowDisplayed = false;
	}
}
