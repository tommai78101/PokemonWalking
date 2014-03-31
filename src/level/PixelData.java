package level;

import resources.Art;
import screen.BaseBitmap;
import entity.Player;

public class PixelData {
	//This class contains all of the area's pixel color, pixel's properties, pixel flags to check, etc.
	//This object will be loaded along with other PixelData objects when loading an area.
	
	/*
	 * Pixel data types: (Including alpha values)
	 * 
	 * 0xFF00FF00: Flat grass (Can be walked, no Pokémon)
	 * 0xFF0000DD: Ledges Horizontal
	 * 0xFF0000AA: Small tree
	 * 
	 * Anything else: Flat grass.
	 * 
	 */
	
	//This is also the ID number for the pixel.
	private int color;
	//If false, it's an obstacle.
	private boolean[] facingsBlocked = new boolean[4];
	public int xPosition;
	public int yPosition;
	//This represents the art resource this PixelData object is representing.
	//This can also give flexibility when it comes to puzzle-themed areas.
	public BaseBitmap bitmap;
	
	//This can also be "isWayPoint".
	private boolean isWarpZone;
	private int targetArea;
	private int targetSector;
	
	//private int parentArea;
	
	public PixelData(int pixel, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.color = pixel;
		this.targetArea = -1;
		this.targetSector = -1;
		
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
	
	public void prepareBitmap(int alpha, int red, int green, int blue) {
		switch (alpha) {
			case 0x01: //Flat grass
				this.bitmap = Art.grass;
				break;
			case 0x02: //Ledge
			{
				switch (red) {
					case 0x00: //Bottom
						this.bitmap = Art.ledge_bottom;
						break;
					case 0x01: //Bottom left
						this.bitmap = Art.ledge_bottom_left;
						break;
					case 0x02: //Right
						this.bitmap = Art.ledge_left;
						break;
					case 0x03: //Top Left
						this.bitmap = Art.ledge_top_left;
						break;
					case 0x04: //Top
						this.bitmap = Art.ledge_top;
						break;
					case 0x05: //Top Right
						this.bitmap = Art.ledge_top_right;
						break;
					case 0x06: //Left
						this.bitmap = Art.ledge_right;
						break;
					case 0x07: //Bottom Right
						this.bitmap = Art.ledge_bottom_right;
						break;
					//---------------------------------------------------------
					case 0x08:
						this.bitmap = Art.ledge_mt_bottom;
						break;
					case 0x09:
						this.bitmap = Art.ledge_mt_bottom_left;
						break;
					case 0x0A:
						this.bitmap = Art.ledge_mt_left;
						break;
					case 0x0B:
						this.bitmap = Art.ledge_mt_top_left;
						break;
					case 0x0C:
						this.bitmap = Art.ledge_mt_top;
						break;
					case 0x0D:
						this.bitmap = Art.ledge_mt_top_right;
						break;
					case 0x0E:
						this.bitmap = Art.ledge_mt_right;
						break;
					case 0x0F:
						this.bitmap = Art.ledge_mt_bottom_right;
						break;
				}
				break;
			}
			case 0x03: //Small tree
				this.bitmap = Art.smallTree;
				break;
			case 0x04: //Warp point
				//TODO: Implement an Area Type ID for warp points. Entrances must fit the theme of the biome 
				//the warp point is in.
				this.bitmap = Art.forestEntrance;
				break;
			case 0x05: //ACP (Refer to documentation.)
				//TODO: Add new bitmaps for connection points to make them blend in with the surroundings.
				this.bitmap = Art.grass;
				break;
			default: //Any other type of tiles.
				break;
		}
	}
	
	public void setProperties(int alpha, int red, int green, int blue) {
		//TODO: Refactor the code to make it more readable and more modular than if...elses.
		this.targetArea = 0;
		this.isWarpZone = false;
		switch (alpha) {
			case 0x01: //Grass
				break;
			case 0x02: //Ledges
				switch (red) {
					case 0x00: //Bottom
						this.facingsBlocked[Player.UP] = false;
						this.facingsBlocked[Player.DOWN] = true;
						this.facingsBlocked[Player.LEFT] = false;
						this.facingsBlocked[Player.RIGHT] = false;
						break;
					case 0x01: //Bottom left
						this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
						break;
					case 0x02: //Left
						this.facingsBlocked[Player.UP] = false;
						this.facingsBlocked[Player.DOWN] = false;
						this.facingsBlocked[Player.LEFT] = false;
						this.facingsBlocked[Player.RIGHT] = false;
						break;
					case 0x03: //Top left
						this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
						break;
					case 0x04: //Top
						this.facingsBlocked[Player.UP] = true;
						this.facingsBlocked[Player.DOWN] = false;
						this.facingsBlocked[Player.LEFT] = false;
						this.facingsBlocked[Player.RIGHT] = false;
						break;
					case 0x05: //Top Right
						this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
						break;
					case 0x06: //Right
						this.facingsBlocked[Player.UP] = false;
						this.facingsBlocked[Player.DOWN] = false;
						this.facingsBlocked[Player.LEFT] = false;
						this.facingsBlocked[Player.RIGHT] = false;
						break;
					case 0x07: //Bottom Right
						this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
						break;
					//------------------------------------------------------------
					//Same order, but with mountain ledges.
					case 0x08:
					case 0x09:
					case 0x0A:
					case 0x0B:
					case 0x0C:
					case 0x0D:
					case 0x0E:
					case 0x0F:
						this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
						break;
				}
				break;
			case 0x03: //Trees
				this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
				break;
			case 0x04: //Warp Point
				this.targetArea = red;
				this.isWarpZone = true;
				this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
				break;
			case 0x05: //ACP (Refer to documentation.)
				this.targetArea = red;
				this.targetSector = green;
				this.isWarpZone = false;
				this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
				break;
		}
	}
	
	public int getColor() {
		return this.color;
	}
	
	//	public int getParentAreaID() {
	//		return parentArea;
	//	}
	
	public int getTargetAreaID() {
		return targetArea;
	}
	
	public boolean[] isWalkThroughable() {
		return this.facingsBlocked;
	}
	
	public int getTargetSectorID() {
		return this.targetSector;
	}
}
