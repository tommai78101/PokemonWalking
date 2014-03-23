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
	private int parentArea;
	
	public PixelData(int pixel, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.color = pixel;
		this.targetArea = 0x0;
		this.parentArea = 0x0;
		
		int alpha = (pixel & 0xFF000000) >> 24;
		int red = (pixel & 0xFF0000) >> 16;
		int green = (pixel & 0xFF00) >> 8;
		int blue = pixel & 0xFF;
		
		setProperties(alpha, red, green, blue);
		prepareBitmap(pixel);
	}

	public void setAsWarpZone(int parentArea, int targetArea) {
		//Enabled by default.
		this.parentArea = parentArea;
		this.targetArea = targetArea;
		this.isWarpZone = true;
	}
	
	public void disableWarpZone() {
		if (this.parentArea != 0x0 && this.targetArea != 0x0) {
			this.isWarpZone = false;
		}
	}
	
	public void enableWarpZone() {
		if (this.parentArea != 0x0 && this.targetArea != 0x0) {
			this.isWarpZone = true;
		}
	}
	
	public boolean isWarpZoneEnabled() {
		if (this.parentArea != 0x0 && this.targetArea != 0x0) {
			return this.isWarpZone;
		}
		return false;
	}
	
	public void prepareBitmap(int color) {
		//This is the only way to separate warp zones from regular tiles.		
		if (this.isWarpZone) {
			//Default implementation of the warp zone tile bitmap.
			this.bitmap = null;
			//If Area ID is 0 ~ 15, then it's a forest type area. 
			if ((this.parentArea & 0xF) < 0x10)
				this.bitmap = Art.forestEntrance;
			return;
		}

		//Tiles only.
		/*
		 * Future references:
		 * 
		 *  0xFFRRGGBB
		 *  
		 *  F: Does nothing.
		 *  R: stands for floor tiles, *WARP ZONES*, conveyor belts, rugs, doormats, terrain, etc.
		 *  G: stands for player, npcs, interactables, signs, items, Pokémon, etc.
		 *  B: stands for walls, obstacles, etc. 
		 * 
		 */

		switch (color) {
			case 0xFFFF0000:
				this.bitmap = Art.grass;
				break;
			case 0xFF0000DD:
				//Green values determine the orientation.
				//Red values determine the type (Mountain rock climb trails, plain ledge, bicycle ledges etc.)
				this.bitmap = Art.ledge_horizontal;
				break;
			case 0xFF0000AA:
				this.bitmap = Art.smallTree;
				break;
			default:
				//TODO: Change the default pixel data to something else more representative.
				this.bitmap = Art.grass;
				break;
		}
	}
	
	public void setProperties(int a, int r, int g, int b) {
		//TODO: Refactor the code to make it more readable and more modular than if...elses.

		this.parentArea = 0;
		this.targetArea = 0;
		this.isWarpZone = false;


		if (r == 0x7F) {
			//Warp Zone
			//r: Parent Area
			//g: Target Area
			
			//TODO: Set orientations of the warp zone.

			this.parentArea = g;
			this.targetArea = b;
			this.isWarpZone = true;
			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = true;
			return;
		}
		if (b == 0xDD) {
			//Ledges
			if (g == 0x00) {
				//Horizontal ledges - Jump from top
				this.facingsBlocked[Player.UP] = false;
				this.facingsBlocked[Player.DOWN] = true;
				this.facingsBlocked[Player.LEFT] = false;
				this.facingsBlocked[Player.RIGHT] = false;
				return;
			}
			return;
		}
		if (b == 0xAA) {
			//Trees
			this.facingsBlocked[0] = this.facingsBlocked[1] = this.facingsBlocked[2] = this.facingsBlocked[3] = false;
			return;
		}
	}

	public int getColor() {
		return color;
	}
	
	public int getParentAreaID() {
		return parentArea;
	}
	
	public int getTargetAreaID() {
		return targetArea;
	}
	
	public boolean[] isWalkThroughable() {
		return this.facingsBlocked;
	}
}
