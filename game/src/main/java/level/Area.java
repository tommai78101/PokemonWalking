/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import abstracts.Character;
import abstracts.Entity;
import abstracts.Item;
import abstracts.Obstacle;
import common.Tileable;
import entity.Player;
import interfaces.UpdateRenderable;
import screen.Bitmap;
import screen.Scene;
import script.TriggerData;

public class Area implements Tileable, UpdateRenderable {
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
	private String checksum;
	// TODO: Add area type.
	// private int areaType;

	private boolean isExitArrowDisplayed;
	private boolean isTriggerTriggered;
	private TriggerData trigger;

	private final List<List<PixelData>> areaData = new ArrayList<>();
	private final Set<PixelData> modifiedAreaData = new HashSet<>();

	// Area data hash maps.
	private final Map<Map.Entry<Integer, Integer>, Obstacle> areaObstacles = new HashMap<>();
	private final Map<Map.Entry<Integer, Integer>, Character> areaCharacters = new HashMap<>();
	private final Map<Map.Entry<Integer, Integer>, Item> areaItems = new HashMap<>();
	private final Map<Map.Entry<Integer, Integer>, TriggerData> triggerDatas = new HashMap<>();

	public Area(Bitmap bitmap) {
		int[] tempPixels = bitmap.getPixels();
		int pixelIterator = 0;

		// Step 1 - Get all the important information
		final int areaInfo = tempPixels[pixelIterator++];
		this.areaID = (areaInfo >> 16) & 0xFFFF;
		final int triggerSize = areaInfo & 0xFFFF;
		final int areaSize = tempPixels[pixelIterator++];
		this.width = (areaSize >> 16) & 0xFFFF;
		this.height = areaSize & 0xFFFF;
		final int pixelSize = tempPixels[pixelIterator++];

		// Step 2 - Get checksum first. Checksum is set immediately after the first pixel.
		final int checksumPixelsCount = WorldConstants.CHECKSUM_MAX_BYTES_LENGTH / 4;
		StringBuilder checksumBuilder = new StringBuilder();
		for (int i = 0; i < checksumPixelsCount; i++) {
			int pixel = tempPixels[pixelIterator++];
			// There are a total of 4 bytes in an "int" type.
			char ch1 = (char) ((pixel & 0xFF000000) >> 24);
			char ch2 = (char) ((pixel & 0x00FF0000) >> 16);
			char ch3 = (char) ((pixel & 0x0000FF00) >> 8);
			char ch4 = (char) (pixel & 0x000000FF);
			checksumBuilder.append(ch1).append(ch2).append(ch3).append(ch4);
		}
		this.checksum = checksumBuilder.toString();

		// Step 3 - Get any triggers and put them into a triggers list.
		// If the trigger size is larger than 1 (meaning there are triggers other than Eraser), we parse the
		// trigger data.
		if (triggerSize > 0) {
			// Ignoring Eraser trigger.
			pixelIterator++;
			pixelIterator++;

			for (int i = 0; i < triggerSize; i++) {
				// The "color" is the ID.
				// ID must not be negative. ID = 0 is reserved.
				int color = tempPixels[pixelIterator++];
				int npcInfo = tempPixels[pixelIterator++];
				if (color - npcInfo != 0 || color + npcInfo != 0) {
					int xPosition = (color >> 24) & 0xFF;
					int yPosition = (color >> 16) & 0xFF;
					this.triggerDatas.put(Map.entry(xPosition, yPosition), new TriggerData().loadTriggerData(color, npcInfo));
				}
			}
		}
		else {
			pixelIterator++;
			pixelIterator++;
		}

		// Step 4 - Get the NPCs data.
		final int npcSize = tempPixels[pixelIterator++];
		for (int i = 0; i < npcSize; i++) {
			int triggerInfo = tempPixels[pixelIterator++];
			int data = tempPixels[pixelIterator++];
			int x = (triggerInfo >> 24) & 0xFF;
			int y = (triggerInfo >> 16) & 0xFF;
			this.areaCharacters.put(Map.entry(x, y), Character.build(this, data, x, y));
		}

		// Step 5 - Get obstacles
		final int obstaclesSize = tempPixels[pixelIterator++];
		for (int i = 0; i < obstaclesSize; i++) {
			int triggerInfo = tempPixels[pixelIterator++];
			int data = tempPixels[pixelIterator++];
			int x = (triggerInfo >> 24) & 0xFF;
			int y = (triggerInfo >> 16) & 0xFF;
			this.areaObstacles.put(Map.entry(x, y), Obstacle.build(this, data, x, y));
		}

		// Step 6 - Get items
		final int itemsSize = tempPixels[pixelIterator++];
		for (int i = 0; i < itemsSize; i++) {
			int triggerInfo = tempPixels[pixelIterator++];
			int data = tempPixels[pixelIterator++];
			int x = (triggerInfo >> 24) & 0xFF;
			int y = (triggerInfo >> 16) & 0xFF;
			this.areaItems.put(Map.entry(x, y), Item.build(data, x, y));
		}

		// Step 6 - Skip the padding
		int col = pixelIterator % this.width;
		for (; pixelIterator % this.width != 0 && col < this.width; pixelIterator++)
			;

		// Step 7 - Get the tiles
		this.pixels = new int[pixelSize];
		System.arraycopy(tempPixels, pixelIterator, this.pixels, 0, pixelSize);
		pixelIterator += pixelSize;

		// Step 8 - Get and fill in the area data.
		for (int y = 0; y < this.height; y++) {
			this.areaData.add(new ArrayList<PixelData>());
			for (int x = 0; x < this.width; x++) {
				int pixel = this.pixels[y * this.width + x];
				this.areaData.get(y).add(new PixelData(pixel, x, y));
			}
		}
		this.isInWarpZone = false;
		this.isInSectorPoint = false;
		this.isExitArrowDisplayed = false;
		this.isTriggerTriggered = false;
		this.areaName = "";
	}

	public Area(Bitmap bitmap, final String areaName) {
		this(bitmap);
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
		if (this.isTriggerBeingTriggered()) {
			this.xPlayerPosition = this.player.getXInArea();
			this.yPlayerPosition = this.player.getYInArea();

			// Do some bounds checking on the X and Y player positions.
			boolean isXOutOfBounds = this.xPlayerPosition < 0 || this.xPlayerPosition >= this.width;
			boolean isYOutOfBounds = this.yPlayerPosition < 0 || this.yPlayerPosition >= this.height;
			if (isXOutOfBounds || isYOutOfBounds)
				return;

			this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			this.checkCurrentPositionDataAndSetProperties(this.currentPixelData);
		}
		else {
			if (!this.player.isLockedWalking()) {
				this.trigger = this.checkForTrigger(this.xPlayerPosition, this.yPlayerPosition);
				if (this.trigger != null) {
					this.setTriggerBeingTriggered();
				}
			}
		}
		if (this.isTriggerBeingTriggered() && this.trigger != null)
			this.handleTriggerActions();
		else if ((this.isTriggerBeingTriggered() && this.trigger == null) || !this.isTriggerBeingTriggered()) {
			this.unsetTriggerBeingTriggered();
			this.handlePlayerActions();
		}

		// Area specific entities are updated at the end.
		this.areaObstacles.forEach(
			(key, obstacleValue) -> {
				obstacleValue.tick();
			}
		);

		this.areaCharacters.forEach(
			(key, character) -> {
				character.tick();
			}
		);
	}

	private void handleTriggerActions() {
		if (this.trigger.hasActiveScript(this)) {
			this.trigger.prepareActiveScript();
			this.player.enableAutomaticMode();
			this.trigger.tick(this);
		}
		else {
			this.player.disableAutomaticMode();
			this.unsetTriggerBeingTriggered();
			this.trigger = null;
		}
	}

	private void handlePlayerActions() {
		if (!this.player.isLockedWalking()) {
			// Update the area's X and Y player position from the Player object.
			this.xPlayerPosition = this.player.getXInArea();
			this.yPlayerPosition = this.player.getYInArea();

			// Do some bounds checking on the X and Y player positions.
			boolean isXOutOfBounds = this.xPlayerPosition < 0 || this.xPlayerPosition >= this.width;
			boolean isYOutOfBounds = this.yPlayerPosition < 0 || this.yPlayerPosition >= this.height;
			if (isXOutOfBounds || isYOutOfBounds)
				return;

			// Target pixel is used to determine what pixel the player is currently standing
			// on (or what pixel the player is currently on top of).
			this.player.handleSurroundingTiles(this);
			this.checkCurrentPositionDataAndSetProperties(this.getPixelData(this.xPlayerPosition, this.yPlayerPosition));
		}
		else {
			if (!this.player.isLockedJumping() && this.player.isLockedWalking()) {
				// It may be possible the player is still in the air, and hasn't done checking
				// if the current pixel data is a ledge or not. This continues the data checking. It's required.
				this.xPlayerPosition = this.player.getXInArea();
				this.yPlayerPosition = this.player.getYInArea();

				// Do some bounds checking on the X and Y player positions.
				boolean isXOutOfBounds = this.xPlayerPosition < 0 || this.xPlayerPosition >= this.width;
				boolean isYOutOfBounds = this.yPlayerPosition < 0 || this.yPlayerPosition >= this.height;
				if (isXOutOfBounds || isYOutOfBounds)
					return;
			}
			this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			this.checkCurrentPositionDataAndSetProperties(this.getCurrentPixelData());
		}
	}

	private TriggerData checkForTrigger(int playerX, int playerY) {
		if (this.triggerDatas.isEmpty())
			return null;
		TriggerData data = this.triggerDatas.get(Map.entry(playerX, playerY));
		TriggerData oldData = this.triggerDatas.get(Map.entry(this.oldXTriggerPosition, this.oldYTriggerPosition));
		if (oldData != null && !oldData.isNpcTrigger() && oldData.isPaused() && (playerX != this.oldXTriggerPosition || playerY != this.oldYTriggerPosition)) {
			// Need to unpause old trigger data if the trigger data was previously paused and the player has
			// left the trigger tile.
			oldData.setPaused(false);
			this.oldXTriggerPosition = -1;
			this.oldYTriggerPosition = -1;
		}
		if (data != null && !data.isNpcTrigger() && data.getXAreaPosition() == playerX && data.getYAreaPosition() == playerY) {
			this.oldXTriggerPosition = data.getXAreaPosition();
			this.oldYTriggerPosition = data.getYAreaPosition();
			return data;
		}
		return null;
	}

	/**
	 * Checks the pixel data the player is currently on, and sets the tile properties according to the
	 * documentation provided. The tile the pixel data is representing determines the properties this
	 * will set, and will affect how the game interacts with the player.
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
						this.player.setLockJumping(red, green, blue, Character.UP, Character.DOWN);
						break;
					case 0x01: // Bottom Left
						// this.player.setLockJumping(red, green, blue, Player.UP, Player.DOWN);
						break;
					case 0x02: // left
						this.player.setLockJumping(red, green, blue, Character.LEFT, Character.RIGHT);
						break;
					case 0x03: // top left
						break;
					case 0x04: // top
						if (this.checkIfValuesAreAllowed(this.getSurroundingTileID(0, -1), 0x01))
							this.player.setLockJumping(red, green, blue, Character.DOWN, Character.UP);
						break;
					case 0x05: // top right
						break;
					case 0x06: // right
						this.player.setLockJumping(red, green, blue, Character.RIGHT, Character.LEFT);
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
			case 0x0D: // Triggers
				if (red == 0x00) { // Default starting Point
					this.setPixelData(
						new PixelData(0x01000000, this.currentPixelData.xPosition, this.currentPixelData.yPosition),
						this.currentPixelData.xPosition, this.currentPixelData.yPosition
					);
				}
				break;
			default:
				// If no special tiles, then it will keep reseting the flags.
				if (!this.player.isLockedWalking() || !this.player.isLockedJumping()) {
					this.isInWarpZone = false;
					this.isInSectorPoint = false;
				}
				// This is to check to see if player has left the water.
				if (this.player.isInWater())
					this.player.startsWalking();
				break;
		}
	}

	/**
	 * Renders the bitmap tiles based on the given pixel data.
	 *
	 * <p>
	 * Note that this is where the bitmap animation works by updating the bitmap after it has been
	 * rendered to the screen.
	 *
	 * @param screen
	 *            The screen display where the bitmaps are to output to.
	 * @param xOff
	 *            The X offset based on the player's X position in absolute world coordinates. The
	 *            absolute world coordinates mean the precise X position on the Canvas.
	 * @param yOff
	 *            The Y offset based on the player's Y position in absolute world coordinates. The
	 *            absolute world coordinates mean the precise Y position on the Canvas.
	 * @return Nothing.
	 *
	 */
	@Override
	public void render(Scene screen, Graphics graphics, int xOff, int yOff) {
		// Rendering area background tiles.
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				PixelData data = this.areaData.get(y).get(x);
				screen.blitBiome(data.getBiomeBitmap(), x * Tileable.WIDTH - xOff, y * Tileable.HEIGHT - yOff, data);

				// We do not render the pixel data if the pixel data is hidden from view.
				if (!data.isHidden()) {
					screen.blitBiome(data.getBitmap(), x * Tileable.WIDTH - xOff, y * Tileable.HEIGHT - yOff, data);
				}
				// This is for rendering exit arrows when you are in front of the exit/entrance doors.
				if (x == this.player.getXInArea() && y == this.player.getYInArea()
					&& ((((data.getColor() >> 24) & 0xFF) == 0x0B) || (((data.getColor() >> 24) & 0xFF) == 0x04)))
					this.renderExitArrow(screen, xOff, yOff, data, x, y);

				// Each time the area background tile is rendered, it also updates the bitmap tick updates.
				data.renderTick();
			}
		}

		// Obstacle dialogues are rendered on top of the area background tiles.
		this.areaObstacles.forEach(
			(k, obstacle) -> {
				obstacle.dialogueRender(screen);
			}
		);

		// Entities are rendered here.
		this.areaCharacters.forEach(
			(k, character) -> {
				character.render(screen, graphics, xOff, yOff);
			}
		);

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
		for (List<PixelData> pixelDataRow : this.areaData) {
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
		if (this.currentPixelData != null)
			return this.currentPixelData;

		// If current pixel data is null, it means the player has not moved off the tile after the area has
		// been loaded in, and did not trigger an update to set this variable to be the current pixel data
		// tile. So, we need to fetch the current pixel data from the "pool of area pixel data" using the
		// current X and Y player positions to handle this edge case.
		this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
		return this.currentPixelData;
	}

	public int getAreaID() {
		return this.areaID;
	}

	public boolean playerIsInConnectionPoint() {
		return this.isInSectorPoint;
	}

	public boolean playerHasLeftConnectionPoint() {
		if (this.isInSectorPoint && this.player.isLockedWalking()) {
			// Leaving
			return true;
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
			data = this.areaData.get(this.yPlayerPosition + yOffset).get(this.xPlayerPosition + xOffset);
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
	 * Obtains the tile ID of the tile being offset by the Character's position.
	 *
	 * @param xOffset
	 *            The X value offset from the player's X position.
	 * @param yOffset
	 *            The Y value offset from the player's Y position.
	 * @return The tile ID of the tile chosen.
	 */
	public int getCharacterSurroundingTileID(Character entity, int xOffset, int yOffset) {
		PixelData data;
		try {
			data = this.areaData.get(entity.getY() + yOffset).get(entity.getX() + xOffset);
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
	 * Compares target tile ID with other multiple tile IDs to see if they are one of many tiles that
	 * the player is allowed to walk on, or when the conditions are right for the player to move on the
	 * tile.
	 *
	 * @param targetIDToCompare
	 *            The target tile ID used to test and see if it's allowable for the player to move/walk
	 *            on. Use getSurroundingTileID() to fetch the target tile ID.
	 * @param multipleTileIDs
	 *            The many tile IDs that are to be compared to the target tile ID to see if the target
	 *            tile ID is one of the allowed tile IDs. You may use as many tile IDs for comparison as
	 *            you wished.
	 * @return True, if the target tile ID is one of the many tile IDs that's allowable. False, if none
	 *         of the tile IDs match the target tile ID.
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
			data = this.areaData.get(this.yPlayerPosition + yOffset).get(this.xPlayerPosition + xOffset);
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

	public void updateItem(Item item) {
		PixelData data = item.getPixelData();
		// It is written like this to prevent possible exceptions complaining about "cannot modify list
		// while it is reading data."
		final int x = data.xPosition;
		final int y = data.yPosition;
		this.areaData.get(y).set(x, data);
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

	/**
	 * This is the main function that determines whether the pixel data is walkable, or it is occupied
	 * by an entity.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public PixelData getPixelData(int x, int y) {
		// These objects have the same higher priority over tileset data.

		// NPC data has higher priority over obstacles and items.
		Character npcData = this.areaCharacters.get(Map.entry(x, y));
		if (npcData != null) {
			return npcData.getPixelData();
		}
		// Obstacles and items are both on the same priority.
		Obstacle obstacleData = this.areaObstacles.get(Map.entry(x, y));
		Item itemData = this.areaItems.get(Map.entry(x, y));
		if (obstacleData != null || itemData != null) {
			return (obstacleData != null ? obstacleData : itemData).getPixelData();
		}
		// If nothing else, tileset data is the final priority.
		return this.areaData.get(y).get(x);
	}

	public List<List<PixelData>> getAllPixelDatas() {
		return this.areaData;
	}

	public Map<Map.Entry<Integer, Integer>, Character> getCharactersMap() {
		return this.areaCharacters;
	}

	public Map<Map.Entry<Integer, Integer>, Obstacle> getObstaclesMap() {
		return this.areaObstacles;
	}

	public Map<Map.Entry<Integer, Integer>, TriggerData> getTriggerDatasMap() {
		return this.triggerDatas;
	}

	public boolean isDisplayingExitArrow() {
		return this.isExitArrowDisplayed;
	}

	public Player getPlayer() {
		return this.player;
	}

	public TriggerData getTriggerData() {
		return this.trigger;
	}

	public boolean isTriggerBeingTriggered() {
		return this.isTriggerTriggered;
	}

	public void setTriggerBeingTriggered() {
		this.isTriggerTriggered = true;
	}

	public void unsetTriggerBeingTriggered() {
		this.isTriggerTriggered = false;
	}

	public Entity getEntity(int x, int y) {
		// Characters are movable entities, so it needs a different method of fetching them.
		Character character = this.findCharacterAt(x, y);
		if (character != null)
			return character;

		// Only obstacles and items are immovable entities.
		PixelData data = this.getPixelData(x, y);
		if (Entity.isObstacle(data)) {
			Obstacle obstacle = this.areaObstacles.get(Map.entry(x, y));
			if (obstacle != null && obstacle.getPixelData().equals(data)) {
				return obstacle;
			}
			else if (obstacle == null) {
				this.areaObstacles.put(Map.entry(x, y), (obstacle = Obstacle.build(this, data, x, y)));
			}
		}
		else if (Entity.isItem(data)) {
			Item item = this.areaItems.get(Map.entry(x, y));
			if (item != null && item.getPixelData().equals(data)) {
				return item;
			}
			else if (item == null) {
				this.areaItems.put(Map.entry(x, y), (item = Item.build(data, x, y)));
			}
		}
		return null;
	}

	public Character findCharacterAt(int x, int y) {
		List<Character> npcs = this.areaCharacters.entrySet().parallelStream().map(Map.Entry::getValue).collect(Collectors.toList());
		for (Character c : npcs) {
			if (c.isLockedWalking()) {
				int oldX = c.getOldX();
				int oldY = c.getOldY();
				int newX = c.getPredictedX();
				int newY = c.getPredictedY();
				if ((x == oldX && y == oldY) || (x == newX && y == newY) || (x == newX && y == oldY) || (x == oldX && y == newY)) {
					return c;
				}
			}
			else if (x == c.getX() && y == c.getY()) {
				return c;
			}
		}
		return null;
	}

	public final String getChecksum() {
		return this.checksum;
	}

	// --------------------- STATIC METHODS -----------------------

	public static int getAreaIDFromBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			throw new IllegalArgumentException("Bitmap is null. Cannot identify area ID from null bitmap.");
		}
		int[] pixels = bitmap.getPixels();
		return (pixels[0] >> 16) & 0xFFFF;
	}

	// --------------------- OVERRIDDEN METHODS -------------------

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof String str) {
			if (!(this.areaName.equals(str))) {
				return false;
			}
			return true;
		}
		else if (obj != null && obj instanceof Integer BigInt) {
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
		if (y + 1 == height && this.player.getFacing() == Character.DOWN) {
			screen.blitBiome(data.getBiomeBitmap(), x * Tileable.WIDTH - xOff + 4, (y + 1) * Tileable.HEIGHT - yOff + 2, data);
			this.isExitArrowDisplayed = true;
		}
		else if (y == 0 && this.player.getFacing() == Character.UP) {
			// TODO: Draw exit arrow point upwards.
		}
		else
			this.isExitArrowDisplayed = false;
	}
}
