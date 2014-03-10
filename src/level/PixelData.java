package level;

import resources.Art;
import screen.BaseBitmap;

public class PixelData {
	//This class contains all of the area's pixel color, pixel's properties, pixel flags to check, etc.
	//This object will be loaded along with other PixelData objects when loading an area.
	
	//This is also the ID number for the pixel.
	private int color;
	//If false, it's an obstacle.
	public boolean canBeWalkedThrough;
	public int xPosition;
	public int yPosition;
	//This represents the art resource this PixelData object is representing.
	//This can also give flexibility when it comes to puzzle-themed areas.
	public BaseBitmap bitmap;
	
	//This can also be "isWayPoint".
	private boolean isWarpZone;
	private Area targetArea;
	private Area parentArea;

	public PixelData() {
		this.targetArea = this.parentArea = null;
		this.isWarpZone = false;
	}
	
	public PixelData(int pixel, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		this.color = pixel;
		
		int red = pixel & 0xFF0000;
		int green = pixel & 0xFF00;
		int blue = pixel & 0xFF;
		
		setProperties(red, green, blue);
		prepareBitmap(pixel);
	}

	public void setAsWarpZone(int parentArea, int targetArea) {
		//Enabled by default.
		this.parentArea = WorldConstants.convertToArea(parentArea);
		this.targetArea = WorldConstants.convertToArea(targetArea);
		this.isWarpZone = true;
	}
	
	public void disableWarpZone() {
		if (this.parentArea != null && this.targetArea != null) {
			this.isWarpZone = false;
		}
	}
	
	public void enableWarpZone() {
		if (this.parentArea != null && this.targetArea != null) {
			this.isWarpZone = true;
		}
	}
	
	public boolean isWarpZoneEnabled() {
		if (this.parentArea != null && this.targetArea != null) {
			return this.isWarpZone;
		}
		return false;
	}
	
	public void prepareBitmap(int color) {
		
		//this.color = color;
		
		if (this.isWarpZone)
			return;

		switch (color) {
			case 0xFF00FF00:
				this.bitmap = Art.testTile;
				break;
			default:
				this.bitmap = Art.smallTree;
				break;
		}
	}
	
	public void setProperties(int r, int g, int b) {
		if (b == 0xEE) {
			//Warp Zone
			//r: Parent Area
			//g: Target Area			
			this.parentArea = WorldConstants.convertToArea(r);
			this.targetArea = WorldConstants.convertToArea(g);
			this.isWarpZone = true;
			this.canBeWalkedThrough = false;
		}
	}

	public int getColor() {
		return color;
	}
	
	public Area getParentArea() {
		return parentArea;
	}
	
	public Area getTargetArea() {
		return targetArea;
	}
}
