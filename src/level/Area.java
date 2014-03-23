package level;

import java.util.ArrayList;

import screen.BaseBitmap;
import screen.BaseScreen;
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
	private PixelData currentPixelData;
	private final int areaID;
	
	/*
	 * Area Type:
	 * 
	 * 0x0 = Forest
	 * 0x1 = City
	 * 0x2 = Cave / Mountain
	 * 0x3 = Water
	 * 
	 * */
	//Will implement Area Type sometime in the future.
	//private int areaType;

	private final ArrayList<ArrayList<PixelData>> areaData = new ArrayList<ArrayList<PixelData>>();
	
	public Area(BaseBitmap bitmap, final int areaID) {
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.pixels = bitmap.getPixels();
		this.areaID = areaID;
		
		for (int y = 0; y < this.height; y++) {
			areaData.add(new ArrayList<PixelData>());
			for (int x = 0; x < this.width; x++) {
				int pixel = this.pixels[y * this.width + x];
				PixelData px = new PixelData(pixel, x, y);
				
				//Will probably work more on this in the future once I've moved on to WayPoints.
				/*
				  px.xPosition = x;
				px.yPosition = y;
				px.prepareBitmap(pixel);
				if ((pixel & 0xFF) == 0xFF) {
					//Meaning it's a blue pixel, which represents a way point.
					//wayPoints.add(new WayPoint(x, y, pixels[y * this.width + x]));
					int parentArea = pixel & 0xFF0000; //Red: parent area
					int targetArea = pixel & 0xFF00; //Green: target area
					px.setAsWarpZone(parentArea, targetArea);
					px.canBeWalkedThrough = false;
				}*/

				areaData.get(y).add(px);
			}
		}
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
	
	public void tick() {
		//Since "setPlayer" method isn't always set, there should be checks everywhere to make sure "player" isn't null.
		if (this.player != null) {
			//PixelData data = null;
			if (!this.player.isLockedWalking()) {
				xPlayerPosition = player.getXInArea();
				yPlayerPosition = player.getYInArea();
				//System.out.println("X: " + xPlayerPosition + " Y: " + yPlayerPosition);
				if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
					return;
				
				//			//Target pixel is now used to determine the pixel data the player is currently 
				//			//facing (or what pixel is currently in front of the player?).			
				//			switch (player.getFacing()) {
				//				case 0:
				//					//Down
				//					if (this.yPlayerPosition + 1 < this.height)
				//						data = areaData.get(this.yPlayerPosition + 1).get(this.xPlayerPosition);
				//					break;
				//				case 1:
				//					//Left
				//					if (this.xPlayerPosition - 1 >= 0)
				//						data = areaData.get(this.yPlayerPosition).get(this.xPlayerPosition - 1);
				//					break;
				//				case 2:
				//					//Up
				//					if (this.yPlayerPosition - 1 >= 0)
				//						data = areaData.get(this.yPlayerPosition - 1).get(this.xPlayerPosition);
				//					break;
				//				case 3:
				//					//Right
				//					if (this.xPlayerPosition + 1 < this.width)
				//						data = areaData.get(this.yPlayerPosition).get(this.xPlayerPosition + 1);
				//					break;
				//			}
				//			if (data != null) {
				//				//if (player.isNotLockedWalking()) {
				//				//System.out.println("Player is not facing an Obstacle.");
				//				switch (data.getColor()) {
				//					case 0xFFFF00FF:
				//						player.blockPath(true);
				//						break;
				//					case 0xFF0000EE:
				//						System.out.println("Facing warp zone.");
				//						player.blockPath(false);
				//						break;
				//					case 0xFF00FF00:
				//						player.blockPath(false);
				//						break;
				//					default:
				//						player.blockPath(true);
				//						break;
				//				//}
				//				}
				//			}
				
				//Another method of detecting obstacles: Find all currently blocking obstacles
				//	of each direction at the same time.
				
				PixelData data_up = null;
				PixelData data_down = null;
				PixelData data_left = null;
				PixelData data_right = null;

				try {
					data_up = areaData.get(this.yPlayerPosition - 1).get(this.xPlayerPosition);
					data_down = areaData.get(this.yPlayerPosition + 1).get(this.xPlayerPosition);
					data_left = areaData.get(this.yPlayerPosition).get(this.xPlayerPosition - 1);
					data_right = areaData.get(this.yPlayerPosition).get(this.xPlayerPosition + 1);
					
					this.player.setAllBlockingDirections(checkData(data_up, 0, -1), checkData(data_down, 0, 1), checkData(data_left, -1, 0), checkData(data_right, 1, 0));
				}
				catch (Exception e) {
					this.player.setAllBlockingDirections(checkData(data_up, 0, -1), checkData(data_down, 0, 1), checkData(data_left, -1, 0), checkData(data_right, 1, 0));
				}
				
				//Target pixel is used to determine what pixel the player is currently standing on
				//(or what pixel the player is currently on top of).
				this.currentPixelData = areaData.get(this.yPlayerPosition).get(xPlayerPosition);
				checkData();
			}
			else if (!this.player.isLockedJumping()) {
				xPlayerPosition = player.getXInArea();
				yPlayerPosition = player.getYInArea();
				if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
					return;
				this.currentPixelData = areaData.get(this.yPlayerPosition).get(xPlayerPosition);
				checkData();
			}
		}
	}
	
	public void checkData() {
		int pixel = this.currentPixelData.getColor();
		//Determines warp zone.
		int red = (pixel & 0xFF0000) >> 16;
		if (red == 0x7F) {
			this.isInWarpZone = true;
			return;
		}
		
		int green = (pixel & 0xFF00) >> 8;
		int blue = (pixel & 0xFF);
		
		//Determine ledges (horizontal) 
		if (blue == 0xDD) {
			this.player.setLockJumping(red, green, blue, Player.UP, Player.DOWN);
		}
	}

	public boolean checkData(PixelData data, int xOffset, int yOffset) {
		if (data != null) {
			switch (data.getColor()) {
				case 0xFFFF0000: //Flat grass
					return false;
				case 0xFF0000DD: //Ledge - Horizontal 
				{
					int y = this.yPlayerPosition + yOffset;
					if (this.yPlayerPosition < y)
						return false;
					return true;
				}
				case 0xFF0000AA: //Small tree
					return true;
				default:
					return false;
			}
		}
		return true;
	}

	public void renderTiles(BaseScreen screen, int xOff, int yOff) {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				screen.blit(this.areaData.get(y).get(x).bitmap, x * Tile.WIDTH - xOff, y * Tile.HEIGHT - yOff);
			}
		}
	}
	
	//Getters/Setters
	public int getPlayerX() {
		return this.xPlayerPosition;
	}
	
	public void setPlayerX(int x) {
		this.xPlayerPosition = x;
	}
	
	public int getPlayerY() {
		return this.yPlayerPosition;
	}
	
	public void setPlayerY(int y) {
		this.yPlayerPosition = y;
	}
	
	public void setDefaultPosition() {
		//TODO: When the game starts from the very beginning, the player must always start from the very first way point.
		player.setAreaPosition(1, 1);
	}
	
	public void setDefaultPosition(PixelData data) {
		PixelData targetData = null;
		if (data.isWarpZoneEnabled()) {
			//this.player.setPosition(data.xPosition * Tile.WIDTH, data.yPosition * Tile.HEIGHT);
			LOOP_BREAK_Area_setDefaultPosition_1: for (ArrayList<PixelData> list : this.areaData) {
				for (PixelData p : list) {
					if (((p.getColor() & 0xFF0000) >> 16) == 0x7F) {
						targetData = p;
						break LOOP_BREAK_Area_setDefaultPosition_1;
					}
				}
			}
			//this.setPlayerX(targetData.xPosition);
			//this.setPlayerY(targetData.yPosition);
			this.player.setAreaPosition(targetData.xPosition, targetData.yPosition);
		}
	}

	public boolean playerIsInWarpZone() {
		return this.isInWarpZone;
	}
	
	public PixelData getCurrentPixelData() {
		//Return the pixel data the player is currently on top of.
		return this.currentPixelData;
	}
	
	public int getAreaID() {
		return this.areaID;
	}

}
