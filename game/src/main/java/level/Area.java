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

	private final int ReservedUsedPixelCount = 5;

	// Area data hash maps.
	private final Map<Map.Entry<Integer, Integer>, Obstacle> areaObstacles = new HashMap<>();
	private final Map<Map.Entry<Integer, Integer>, Character> areaCharacters = new HashMap<>();
	private final Map<Map.Entry<Integer, Integer>, Item> areaItems = new HashMap<>();
	private final Map<Map.Entry<Integer, Integer>, TriggerData> triggerDatas = new HashMap<>();

	public Area(Bitmap bitmap) {
		int[] tempPixels = bitmap.getPixels();
		this.areaID = (tempPixels[0] >> 16) & 0xFFFF;
		int triggerSize = tempPixels[0] & 0xFFFF;
		int row = 0;
		int column = 1;
		int stride = bitmap.getWidth();

		// Get checksum first. Checksum is set immediately after the first pixel.
		StringBuilder checksumBuilder = new StringBuilder();
		for (; (row * stride + column) < this.ReservedUsedPixelCount;) {
			int pixel = tempPixels[row * stride + column];
			// There are a total of 4 bytes in an "int" type.
			char ch1 = (char) ((pixel & 0xFF000000) >> 24);
			char ch2 = (char) ((pixel & 0x00FF0000) >> 16);
			char ch3 = (char) ((pixel & 0x0000FF00) >> 8);
			char ch4 = (char) (pixel & 0x000000FF);
			checksumBuilder.append(ch1).append(ch2).append(ch3).append(ch4);

			column++;
			if (column >= stride) {
				column %= stride;
				row++;
			}
		}
		this.checksum = checksumBuilder.toString();

		// After checksum, the rest of the pixels in the current row are just padded pixels. Skip them until
		// we reach next row.
		row++;
		column = 0;

		// If the trigger size is larger than 1 (meaning there are triggers other than Eraser), we parse the
		// trigger data.
		if (triggerSize > 1) {
			column++;

			// TODO(2021-July-24): This is the place to insert a static method to "search for any trigger
			// scripts matching the checksum" and load the trigger script into TriggerData.loadTriggerData().

			for (; column < triggerSize; column++) {
				// The "color" is the ID.
				// ID must not be negative. ID = 0 is reserved.
				int color = tempPixels[column + (stride * row)];
				if (color > 0) {
					int xPosition = (color >> 24) & 0xFF;
					int yPosition = (color >> 16) & 0xFF;
					this.triggerDatas.put(Map.entry(xPosition, yPosition), new TriggerData().loadTriggerData(color));
				}
				if (column >= stride) {
					row++;
					column -= stride;
				}
			}
		}

		// We need to add the row by 1 for the last row with trailing empty trigger IDs.
		row++;
		column = 0;

		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight() - row;
		this.pixels = new int[this.width * this.height];
		System.arraycopy(bitmap.getPixels(), this.width * row, this.pixels, 0, this.pixels.length);

		for (int y = 0; y < this.height; y++) {
			this.areaData.add(new ArrayList<PixelData>());
			for (int x = 0; x < this.width; x++) {
				int pixel = this.pixels[y * this.width + x];
				PixelData pixelData = new PixelData(pixel, x, y);

				if (Entity.isObstacle(pixelData)) {
					Obstacle entity = Obstacle.build(pixelData, x, y);
					if (entity != null) {
						this.areaObstacles.put(Map.entry(x, y), entity);
					}
				}
				if (Entity.isCharacter(pixelData)) {
					Character entity = Character.build(pixelData, x, y);
					if (entity != null) {
						this.areaCharacters.put(Map.entry(x, y), entity);
					}
				}
				if (Entity.isItem(pixelData)) {
					Item entity = Item.build(pixelData);
					if (entity != null) {
						this.areaItems.put(Map.entry(x, y), entity);
					}
				}
				this.areaData.get(y).add(pixelData);
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
	}

	private void handleTriggerActions() {
		if (this.trigger.hasActiveScript(this)) {
			this.trigger.prepareActiveScript();
			this.player.enableAutomaticMode();
			this.trigger.tick(this, this.xPlayerPosition, this.yPlayerPosition);
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
		else if (!this.player.isLockedJumping() && this.player.isLockedWalking()) {
			// It may be possible the player is still in the air, and hasn't done checking
			// if the current pixel data is a ledge or not. This continues the data checking. It's required.
			this.xPlayerPosition = this.player.getXInArea();
			this.yPlayerPosition = this.player.getYInArea();

			// Do some bounds checking on the X and Y player positions.
			boolean isXOutOfBounds = this.xPlayerPosition < 0 || this.xPlayerPosition >= this.width;
			boolean isYOutOfBounds = this.yPlayerPosition < 0 || this.yPlayerPosition >= this.height;
			if (isXOutOfBounds || isYOutOfBounds)
				return;

			this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			this.checkCurrentPositionDataAndSetProperties(this.getCurrentPixelData());
		}
		else {
			this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			this.checkCurrentPositionDataAndSetProperties(this.getCurrentPixelData());
		}
	}

	private TriggerData checkForTrigger(int playerX, int playerY) {
		if (this.triggerDatas.isEmpty())
			return null;
		TriggerData data = this.triggerDatas.get(Map.entry(playerX, playerY));
		TriggerData oldData = this.triggerDatas.get(Map.entry(this.oldXTriggerPosition, this.oldYTriggerPosition));
		if (oldData != null && oldData.isPaused() && (playerX != this.oldXTriggerPosition || playerY != this.oldYTriggerPosition)) {
			// Need to unpause old trigger data if the trigger data was previously paused and the player has
			// left the trigger tile.
			oldData.setPaused(false);
			this.oldXTriggerPosition = -1;
			this.oldYTriggerPosition = -1;
		}
		if (data != null && data.getXAreaPosition() == playerX && data.getYAreaPosition() == playerY) {
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
				obstacle.renderDialogue(screen, graphics);
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
		this.areaData.get(data.yPosition).set(data.xPosition, data);
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

	public List<List<PixelData>> getAllPixelDatas() {
		return this.areaData;
	}

	public boolean isDisplayingExitArrow() {
		return this.isExitArrowDisplayed;
	}

	public Map<Map.Entry<Integer, Integer>, Obstacle> getObstaclesList() {
		return this.areaObstacles;
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
		// Only obstacles and characters are entities.
		PixelData data = this.getPixelData(x, y);
		if (Entity.isObstacle(data)) {
			Obstacle obstacle = this.areaObstacles.get(Map.entry(x, y));
			if (obstacle != null && obstacle.getPixelData().equals(data)) {
				return obstacle;
			}
			else {
				throw new NullPointerException("The obstacle shouldn't be null.");
			}
		}
		else if (Entity.isCharacter(data)) {
			Character character = this.areaCharacters.get(Map.entry(x, y));
			if (character != null && character.getPixelData().equals(data)) {
				return character;
			}
			else {
				throw new NullPointerException("The character shouldn't be null.");
			}
		}
		else if (Entity.isItem(data)) {
			Item item = this.areaItems.get(Map.entry(x, y));
			if (item != null && item.getPixelData().equals(data)) {
				return item;
			}
			else {
				throw new NullPointerException("The item shouldn't be null.");
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
