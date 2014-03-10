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
	
	private BaseBitmap bitmap;

	private final ArrayList<ArrayList<PixelData>> areaData = new ArrayList<ArrayList<PixelData>>();
	
	public Area(BaseBitmap bitmap) {
		this.bitmap = bitmap;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.pixels = bitmap.getPixels();
		
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
			PixelData data = null;
			
			xPlayerPosition = player.getXInArea();
			yPlayerPosition = player.getYInArea();
			//System.out.println("X: " + xPlayerPosition + " Y: " + yPlayerPosition);
			if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
				return;
			
			//Target pixel is now used to determine the pixel data the player is currently 
			//facing (or what pixel is currently in front of the player?).
			
			switch (player.getFacing()) {
				case 0:
					//Down
					if (this.yPlayerPosition + 1 < this.height)
						data = areaData.get(this.yPlayerPosition + 1).get(this.xPlayerPosition);
					break;
				case 1:
					//Left
					if (this.xPlayerPosition - 1 >= 0)
						data = areaData.get(this.yPlayerPosition).get(this.xPlayerPosition - 1);
					break;
				case 2:
					//Up
					if (this.yPlayerPosition - 1 >= 0)
						data = areaData.get(this.yPlayerPosition - 1).get(this.xPlayerPosition);
					break;
				case 3:
					//Right
					if (this.xPlayerPosition + 1 < this.width)
						data = areaData.get(this.yPlayerPosition).get(this.xPlayerPosition + 1);
					break;
			}
			if (data != null) {
				//if (player.isNotLockedWalking()) {
				//System.out.println("Player is not facing an Obstacle.");
				switch (data.getColor()) {
					case 0xFFFF00FF:
						player.blockPath(true);
						break;
					case 0xFF0000EE:
						System.out.println("Facing warp zone.");
						player.blockPath(false);
						break;
					case 0xFF00FF00:
						player.blockPath(false);
						break;
					default:
						player.blockPath(true);
						break;
				//}
				}
			}
			//Target pixel is used to determine what pixel the player is currently standing on
			//(or what pixel the player is currently on top of).
			data = areaData.get(this.yPlayerPosition).get(xPlayerPosition);
			switch (data.getColor()) {
				case 0xFF0000EE:
					System.out.println("Is in warp zone.");
					//TODO: Do something about this. Player supposed to be teleported to a new area at this point.
				default:
					break;
			}
		}
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
	
}
