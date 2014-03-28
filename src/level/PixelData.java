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
	
	//private int parentArea;
	
	public PixelData(int pixel, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.color = pixel;
		this.targetArea = 0x0;
		//this.parentArea = 0x0;
		
		int alpha = (pixel >> 24) & 0xFF;
		int red = (pixel >> 16) & 0xFF;
		int green = (pixel >> 8) & 0xFF;
		int blue = pixel & 0xFF;
		
		setProperties(alpha, red, green, blue);
		prepareBitmap(alpha, red, green, blue);
	}
	
	//	public void setAsWarpZone(int targetArea,) {
	//		//Enabled by default.
	//		//this.parentArea = parentArea;
	//		this.targetArea = targetArea;
	//		this.isWarpZone = true;
	//	}
	
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
		//This is the only way to separate warp zones from regular tiles.		
		//		if (this.isWarpZone) {
		//			//Default implementation of the warp zone tile bitmap.
		//			this.bitmap = null;
		//			//If Area ID is 0 ~ 15, then it's a forest type area. 
		//			if ((this.parentArea & 0xF) < 0x10)
		//				this.bitmap = Art.forestEntrance;
		//			return;
		//		}
		
		//Tiles only.
		//Check the documentation for official implementation.
		//		switch (color) {
		//			case 0xFFFF0000:
		//				this.bitmap = Art.grass;
		//				break;
		//			case 0xFF0000DD:
		//				//Green values determine the orientation.
		//				//Red values determine the type (Mountain rock climb trails, plain ledge, bicycle ledges etc.)
		//				this.bitmap = Art.ledge_horizontal;
		//				break;
		//			case 0xFF0000AA:
		//				this.bitmap = Art.smallTree;
		//				break;
		//			default:
		//				this.bitmap = Art.grass;
		//				break;
		//		}
		switch (alpha) {
			case 0x01: //Flat grass
				this.bitmap = Art.grass;
				break;
			case 0x02: //Ledge
			{
				switch (red) {
					case 0x00: //Horizontal Bottom
						this.bitmap = Art.ledge_horizontal_bottom;
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
			case 0x05:
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
		
		//		if (r == 0x7F) {
		//			//Warp Zone
		//			//r: Parent Area
		//			//g: Target Area
		//			
		//			//TODO: Set orientations of the warp zone.
		//			
		//			this.parentArea = g;
		//			this.targetArea = b;
		//			this.isWarpZone = true;
		//			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
		//			return;
		//		}
		//		if (b == 0xDD) {
		//			//Ledges
		//			if (g == 0x00) {
		//				//Horizontal ledges - Jump from top
		//				this.facingsBlocked[Player.UP] = false;
		//				this.facingsBlocked[Player.DOWN] = true;
		//				this.facingsBlocked[Player.LEFT] = false;
		//				this.facingsBlocked[Player.RIGHT] = false;
		//				return;
		//			}
		//			return;
		//		}
		//		if (b == 0xAA) {
		//			//Trees
		//			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
		//			return;
		//		}
		switch (alpha) {
			case 0x01: //Grass
				break;
			case 0x02: //Ledges
				switch (red) {
					case 0x00:
						this.facingsBlocked[Player.UP] = false;
						this.facingsBlocked[Player.DOWN] = true;
						this.facingsBlocked[Player.LEFT] = false;
						this.facingsBlocked[Player.RIGHT] = false;
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
			case 0x05:
				this.targetArea = red;
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
}
