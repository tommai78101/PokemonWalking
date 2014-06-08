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

import resources.Art;
import screen.BaseBitmap;

public class PixelData {
	// This class contains all of the area's pixel color, pixel's properties, pixel flags to check, etc.
	// This object will be loaded along with other PixelData objects when loading an area.
	
	/*
	 * Pixel data types: (Including alpha values)
	 * 
	 * 0xFF00FF00: Flat grass (Can be walked, no Pokémon) 0xFF0000DD: Ledges Horizontal 0xFF0000AA: Small tree
	 * 
	 * Anything else: Flat grass.
	 */
	
	// This is also the ID number for the pixel.
	private int color;
	// If false, it's an obstacle.
	private boolean[] facingsBlocked = new boolean[4];
	public int xPosition;
	public int yPosition;
	// This represents the art resource this PixelData object is representing.
	// This can also give flexibility when it comes to puzzle-themed areas.
	// Now adding animations.
	public BaseBitmap[] bitmap;
	public BaseBitmap[] biomeBitmap;
	public int bitmapTick;
	public int biomeBitmapTick;
	
	// This can also be "isWayPoint".
	private boolean isWarpZone;
	private int targetArea;
	private int targetSector;
	private int groundHeight;
	
	// private int parentArea;
	
	public PixelData(int pixel, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.color = pixel;
		this.targetArea = -1;
		this.targetSector = -1;
		this.groundHeight = 0; // Default
		this.bitmapTick = 0;
		this.biomeBitmapTick = 0;
		
		int alpha = (pixel >> 24) & 0xFF;
		int red = (pixel >> 16) & 0xFF;
		int green = (pixel >> 8) & 0xFF;
		int blue = pixel & 0xFF;
		
		setProperties(alpha, red, green, blue);
		prepareBitmap(alpha, red, green, blue);
	}
	
	public void disableWarpZone() {
		this.isWarpZone = false;
	}
	
	public void enableWarpZone() {
		this.isWarpZone = true;
	}
	
	public boolean isWarpZoneEnabled() {
		return this.isWarpZone;
	}
	
	/**
	 * Sets the bitmap tile the pixel data is representing.
	 * 
	 * <p>
	 * When setting the bitmap, first it must set the bitmap to something other than null. Since the bitmap variable holds an array, it takes in at least 1 bitmap pre-loaded by the Art class. Then, once the bitmap is set, it must break all the way outside of the nested switch conditions, otherwise,
	 * setting bitmaps will overwrite correct data with incorrect data.
	 * 
	 * <p>
	 * If the bitmap stays null, the bitmap will then be set to "NO PNG" error bitmap, which when loaded into the game, the game will not crash, and the developers/players can tell where the bitmap loading has gone wrong.
	 * 
	 * <p>
	 * If the bitmap is an animated bitmap, the Art class will load the animated bitmap into an array. The next step would be to just pass the array to this bitmap.
	 * 
	 * @param alpha
	 *            The alpha value of the pixel data's color.
	 * @param red
	 *            The red value of the pixel data's color.
	 * @param green
	 *            The green value of the pixel data's color.
	 * @param blue
	 *            The blue value of the pixel data's color.
	 * @return Nothing.
	 * */
	public void prepareBitmap(int alpha, int red, int green, int blue) {
		switch (alpha) {
			case 0x01: // Path
				this.bitmap = new BaseBitmap[1];
				// Tile Type
				switch (red) {
					case 0x00: // Grass Path
						this.bitmap[0] = Art.grass;
						break;
					case 0x01: // Mountain Ground Path
						this.bitmap[0] = Art.mt_ground;
						break;
					case 0x02: // Road Path
						this.bitmap[0] = Art.path;
						break;
					case 0x03: // Carpet Floor (Indoors)
						this.bitmap[0] = Art.carpet_indoors;
						this.biomeBitmap = Art.exit_arrow;
						break;
					case 0x04: // Carpet Floor (Outdoors)
						this.bitmap[0] = Art.carpet_outdoors;
						this.biomeBitmap = Art.exit_arrow;
						break;
					case 0x05: // Hardwood Floor (Indoors)
						this.bitmap[0] = Art.hardwood_indoors;
						break;
					case 0x06: // Tatami Floor (Indoors)
						this.bitmap[0] = Art.tatami_indoors;
						break;
					default:
						break;
				}
				if (this.biomeBitmap == null) {
					switch (green) { // Area Type
						case 0x00:
							this.biomeBitmap = new BaseBitmap[1];
							this.biomeBitmap[0] = Art.grass; // Forest
							break;
						case 0x02:
							this.biomeBitmap = new BaseBitmap[1];
							this.biomeBitmap[0] = Art.mt_ground; // Mountain
							break;
						default:
							break;
					}
				}
				break;
			case 0x02: // Ledge
			{
				this.bitmap = new BaseBitmap[1];
				switch (red) {
					case 0x00: // Bottom
						this.bitmap[0] = Art.ledge_bottom;
						break;
					case 0x01: // Bottom left
						this.bitmap[0] = Art.ledge_bottom_left;
						break;
					case 0x02: // Right
						this.bitmap[0] = Art.ledge_left;
						break;
					case 0x03: // Top Left
						this.bitmap[0] = Art.ledge_top_left;
						break;
					case 0x04: // Top
						this.bitmap[0] = Art.ledge_top;
						break;
					case 0x05: // Top Right
						this.bitmap[0] = Art.ledge_top_right;
						break;
					case 0x06: // Left
						this.bitmap[0] = Art.ledge_right;
						break;
					case 0x07: // Bottom Right
						this.bitmap[0] = Art.ledge_bottom_right;
						break;
					// ---------------------------------------------------------
					case 0x08:
						this.bitmap[0] = Art.ledge_mt_bottom;
						break;
					case 0x09:
						this.bitmap[0] = Art.ledge_mt_bottom_left;
						break;
					case 0x0A:
						this.bitmap[0] = Art.ledge_mt_left;
						break;
					case 0x0B:
						this.bitmap[0] = Art.ledge_mt_top_left;
						break;
					case 0x0C:
						this.bitmap[0] = Art.ledge_mt_top;
						break;
					case 0x0D:
						this.bitmap[0] = Art.ledge_mt_top_right;
						break;
					case 0x0E:
						this.bitmap[0] = Art.ledge_mt_right;
						break;
					case 0x0F:
						this.bitmap[0] = Art.ledge_mt_bottom_right;
						break;
					case 0x10:
						this.bitmap[0] = Art.ledge_inner_bottom;
						break;
					case 0x11:
						this.bitmap[0] = Art.ledge_inner_bottom_left;
						break;
					case 0x12:
						this.bitmap[0] = Art.ledge_inner_left;
						break;
					case 0x13:
						this.bitmap[0] = Art.ledge_inner_top_left;
						break;
					case 0x14:
						this.bitmap[0] = Art.ledge_inner_top;
						break;
					case 0x15:
						this.bitmap[0] = Art.ledge_inner_top_right;
						break;
					case 0x16:
						this.bitmap[0] = Art.ledge_inner_right;
						break;
					case 0x17:
						this.bitmap[0] = Art.ledge_inner_bottom_right;
						break;
				}
				break;
			}
			case 0x03: // Small tree
				this.bitmap = new BaseBitmap[1];
				this.bitmap[0] = Art.smallTree;
				break;
			case 0x04: // Warp point (Refer to documentation for flaws.)
				this.bitmap = new BaseBitmap[1];
				this.bitmap[0] = Art.forestEntrance;
				break;
			case 0x05: // ACP (Refer to documentation.)
				this.bitmap = new BaseBitmap[1];
				// TODO: Add new bitmaps for connection points to make them blend in with the surroundings.
				this.bitmap[0] = Art.grass;
				break;
			case 0x06: // Stairs
				this.bitmap = new BaseBitmap[1];
				switch (red) {
					case 0x00:
						this.bitmap[0] = Art.stairs_bottom;
						break;
					case 0x01:
						this.bitmap[0] = Art.stairs_left;
						break;
					case 0x02:
						this.bitmap[0] = Art.stairs_top;
						break;
					case 0x03:
						this.bitmap[0] = Art.stairs_right;
						break;
					case 0x04:
						this.bitmap[0] = Art.stairs_mt_bottom;
						break;
					case 0x05:
						this.bitmap[0] = Art.stairs_mt_left;
						break;
					case 0x06:
						this.bitmap[0] = Art.stairs_mt_top;
						break;
					case 0x07:
						this.bitmap[0] = Art.stairs_mt_right;
						break;
				}
				break;
			case 0x07: // Water
				// Always start with the first frame of any animation.
			{
				switch (red) {
					case 0x00: // Pure water, no border.
						this.bitmap = Art.water;
						break;
					case 0x01: // Left Border
						this.bitmap = Art.water_left;
						break;
					case 0x02: // Top Left Border
						this.bitmap = Art.water_top_left;
						break;
					case 0x03: // Top Border
						this.bitmap = Art.water_top;
						break;
					case 0x04: // Top Right Border
						this.bitmap = Art.water_top_right;
						break;
					case 0x05: // Right Border
						this.bitmap = Art.water_right;
						break;
				}
				break;
			}
			case 0x08: // Sign
				this.bitmap = new BaseBitmap[1];
				this.bitmap[0] = Art.sign;
				break;
			case 0x09: // House
				this.bitmap = new BaseBitmap[1];
				switch (red) { // House related tiles. Way too many to list them orderly.
					case 0x00: // Door
						this.bitmap[0] = Art.house_bottom;
						break;
					case 0x01: // Bottom building
						this.bitmap[0] = Art.house_bottom_left;
						break;
					case 0x02: // Bottom left building
						this.bitmap[0] = Art.house_bottom_right;
						break;
					case 0x03: // Roof left
						this.bitmap[0] = Art.changeColors(Art.house_roof_left, WorldConstants.convertToAreaColor(green), WorldConstants.convertToAreaColor(green));
						// this.bitmap[0] = Art.house_roof_left;
						break;
					case 0x04: // Roof middle
						this.bitmap[0] = Art.changeColors(Art.house_roof_middle, WorldConstants.convertToAreaColor(green), WorldConstants.convertToAreaColor(green));
						break;
					case 0x05: // Roof right
						this.bitmap[0] = Art.changeColors(Art.house_roof_right, WorldConstants.convertToAreaColor(green), WorldConstants.convertToAreaColor(green));
						break;
				}
				break;
			case 0x0A: // House Door
				this.bitmap = new BaseBitmap[1];
				this.bitmap[0] = Art.house_door;
				break;
			case 0x0B: // Item
				this.bitmap = new BaseBitmap[1];
				this.bitmap[0] = Art.item;
				break;
			default: // Any other type of tiles.
				break;
		}
		if (this.bitmap == null) {
			this.bitmap = new BaseBitmap[1];
			this.bitmap[0] = Art.error;
		}
		if (this.biomeBitmap == null) {
			this.biomeBitmap = new BaseBitmap[1];
			this.biomeBitmap[0] = Art.grass; // By default, biome bitmap should be grass.
		}
	}
	
	/**
	 * Prepares the bitmap if the color has been pre-determined.
	 * 
	 * <p>
	 * Will cause undefined behaviors if the colors have not been set yet.
	 * 
	 * @return Nothing.
	 * */
	public void prepareBitmap() {
		int alpha = (this.color >> 24) & 0xFF;
		int red = (this.color >> 16) & 0xFF;
		int green = (this.color >> 8) & 0xFF;
		int blue = this.color & 0xFF;
		this.prepareBitmap(alpha, red, green, blue);
	}
	
	/**
	 * Sets the properties of a given pixel data. This is where the game gets the area's information on what the player should do and don't.
	 * 
	 * <p>
	 * Some of the features are currently unused. Especially collision detection.
	 * 
	 * <p>
	 * Only the ones that set target areas, warp zone areas, etc. are the ones being used.
	 * 
	 * @param alpha
	 *            The alpha value of the pixel data's color.
	 * @param red
	 *            The red value of the pixel data's color.
	 * @param green
	 *            The green value of the pixel data's color.
	 * @param blue
	 *            The blue value of the pixel data's color.
	 * @return Nothing.
	 * */
	public void setProperties(int alpha, int red, int green, int blue) {
		// TODO: Refactor the code to make it more readable and more modular than if...elses.
		this.targetArea = 0;
		this.isWarpZone = false;
		this.groundHeight = 0;
		switch (alpha) {
			case 0x01: // Grass
				this.groundHeight = blue;
				break;
			case 0x02: // Ledges
				// switch (red) {
				// case 0x00: //Bottom
				// this.facingsBlocked[Player.UP] = false;
				// this.facingsBlocked[Player.DOWN] = true;
				// this.facingsBlocked[Player.LEFT] = false;
				// this.facingsBlocked[Player.RIGHT] = false;
				// break;
				// case 0x01: //Bottom left
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				// break;
				// case 0x02: //Left
				// this.facingsBlocked[Player.UP] = false;
				// this.facingsBlocked[Player.DOWN] = false;
				// this.facingsBlocked[Player.LEFT] = false;
				// this.facingsBlocked[Player.RIGHT] = false;
				// break;
				// case 0x03: //Top left
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				// break;
				// case 0x04: //Top
				// this.facingsBlocked[Player.UP] = true;
				// this.facingsBlocked[Player.DOWN] = false;
				// this.facingsBlocked[Player.LEFT] = false;
				// this.facingsBlocked[Player.RIGHT] = false;
				// break;
				// case 0x05: //Top Right
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				// break;
				// case 0x06: //Right
				// this.facingsBlocked[Player.UP] = false;
				// this.facingsBlocked[Player.DOWN] = false;
				// this.facingsBlocked[Player.LEFT] = false;
				// this.facingsBlocked[Player.RIGHT] = false;
				// break;
				// case 0x07: //Bottom Right
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				// break;
				// //------------------------------------------------------------
				// //Same order, but with mountain ledges.
				// case 0x08:
				// case 0x09:
				// case 0x0A:
				// case 0x0B:
				// case 0x0C:
				// case 0x0D:
				// case 0x0E:
				// case 0x0F:
				// case 0x10:
				// case 0x11:
				// case 0x12:
				// case 0x13:
				// case 0x14:
				// case 0x15:
				// case 0x16:
				// case 0x17:
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				// break;
				// default:
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				// break;
				// }
				break;
			case 0x03: // Trees
				this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				break;
			case 0x04: // Warp Point
				// this.targetArea = red;
				// FIXME: Level Editor must set target area ID to at least 1 or above.
				this.targetArea = WorldConstants.isModsEnabled.booleanValue() ? red + 1001 : red;
				this.isWarpZone = true;
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
				break;
			case 0x05: // ACP (Refer to documentation.)
				// this.targetArea = red;
				// FIXME: Level Editor must set target area ID to at least 1 or above.
				this.targetArea = WorldConstants.isModsEnabled.booleanValue() ? red + 1001 : red;
				this.targetSector = green;
				this.isWarpZone = false;
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
				break;
			case 0x06: // Stairs
				break;
			case 0x07: // Water
				// TODO: Needs to do something with this. It must not block the player, however, without special boolean value, it will always block player from advancing. 
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
			case 0x08: // Sign
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				break;
			case 0x09: // House
				// this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				break;
			case 0x0A: // House Door
				// this.targetArea = red;
				// FIXME: Level Editor must set target area ID to at least 1 or above.
				this.targetArea = WorldConstants.isModsEnabled.booleanValue() ? red + 1001 : red;
				this.isWarpZone = true;
				break;
			case 0x0B: // Item
				break;
			default:
				this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				break;
		}
	}
	
	public int getColor() {
		return this.color;
	}
	
	// public int getParentAreaID() {
	// return parentArea;
	// }
	
	public int getTargetAreaID() {
		return targetArea;
	}
	
	public boolean[] isWalkThroughable() {
		return this.facingsBlocked;
	}
	
	public int getTargetSectorID() {
		return this.targetSector;
	}
	
	public void tick() {
		this.bitmapTick++;
		if (this.bitmapTick >= this.bitmap.length)
			this.bitmapTick = 0;
		this.biomeBitmapTick++;
		if (this.biomeBitmapTick >= this.biomeBitmap.length)
			this.biomeBitmapTick = 0;
	}
	
	public BaseBitmap getBitmap() {
		return this.bitmap[this.bitmapTick];
	}
	
	public int getGroundHeight() {
		return this.groundHeight;
	}
	
	public BaseBitmap getBiomeBitmap() {
		return this.biomeBitmap[this.biomeBitmapTick];
	}
}
