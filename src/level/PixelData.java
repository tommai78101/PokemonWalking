package level;

import resources.Art;
import screen.BaseBitmap;

public class PixelData {
	//This class contains all of the area's pixel color, pixel's properties, pixel flags to check, etc.
	//This object will be loaded along with other PixelData objects when loading an area.
	
	//This is also the ID number for the pixel.
	private int color;
	//If false, it's an obstacle.
	private boolean canBeWalkedThrough;
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
		
		int red = (pixel & 0xFF0000) >> 16;
		int green = (pixel & 0xFF00) >> 8;
		int blue = pixel & 0xFF;
		
		setProperties(red, green, blue);
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
		if (this.isWarpZone) {
			//Default implementation of the warp zone tile bitmap.
			this.bitmap = null;
			//If Area ID is 0 ~ 15, then it's a forest type area. 
			if ((this.parentArea & 0xF) < 0x10)
				this.bitmap = Art.forestEntrance;
			return;
		}

		switch (color) {
			case 0xFF00FF00:
				this.bitmap = Art.grass;
				break;
			default:
				//TODO: Change the default pixel data to something else more representative.
				this.bitmap = Art.smallTree;
				this.canBeWalkedThrough = false;
				break;
		}
	}
	
	public void setProperties(int r, int g, int b) {
		if (b == 0xEE) {
			//Warp Zone
			//r: Parent Area
			//g: Target Area			

			this.parentArea = r;
			this.targetArea = g;
			this.isWarpZone = true;
			this.canBeWalkedThrough = true;
		}
		else {
			this.isWarpZone = false;
			this.parentArea = 0;
			this.targetArea = 0;
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
	
	public boolean isWalkThroughable() {
		return this.canBeWalkedThrough;
	}
}
