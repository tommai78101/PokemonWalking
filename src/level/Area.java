package level;

import java.util.ArrayList;
import java.util.List;

import resources.Art;
import screen.BaseBitmap;
import screen.BaseScreen;
import abstracts.Tile;
import abstracts.World;
import entity.Player;

public class Area extends World {
	
	//private final List<Tile> tiles = new ArrayList<Tile>();
	
	private final int width;
	private final int height;
	private final int[] pixels;
	
	private int xPlayerPosition;
	private int yPlayerPosition;
	private Player player;
	
	private final List<WayPoint> wayPoints = new ArrayList<WayPoint>();
	
	public Area(BaseBitmap bitmap) {
		this.bitmap = bitmap;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.pixels = bitmap.getPixels();
		
		//		for (int y = 0; y < this.height; y++) {
		//			for (int x = 0; x < this.width; x++) {
		//				if ((pixels[y * this.width + x] & 0xFF) == 0xFF) {
		//					wayPoints.add(new WayPoint(x, y, (pixels[y * this.width + x] & 0xFFFF00) >> 8));
		//				}
		//			}
		//		}
		//		for (int p : this.pixels) {
		//			//I like the color blue, therefore blue represents waypoint, and green/red both represents the data id for the waypoints.
		//			
		//		}
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
	
	@Override
	public void tick() {
		xPlayerPosition = player.getXInArea();
		yPlayerPosition = player.getYInArea();
		if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
			return;
		
		int targetPixel = 0;
		switch (player.getFacing()) {
			case 0:
				//Down
				if (this.yPlayerPosition + 1 < this.height)
					targetPixel = pixels[(this.yPlayerPosition + 1) * this.width + this.xPlayerPosition];
				break;
			case 1:
				//Left
				if (this.xPlayerPosition - 1 >= 0)
					targetPixel = pixels[this.yPlayerPosition * this.width + (this.xPlayerPosition - 1)];
				break;
			case 2:
				//Up
				if (this.yPlayerPosition - 1 >= 0)
					targetPixel = pixels[(this.yPlayerPosition - 1) * this.width + this.xPlayerPosition];
				break;
			case 3:
				//Right
				if (this.xPlayerPosition + 1 < this.width)
					targetPixel = pixels[this.yPlayerPosition * this.width + (this.xPlayerPosition + 1)];
				break;
		}
		if (targetPixel == 0)
			return;
		if (player.isNotLockedWalking()) {
			//System.out.println("Player is not facing an Obstacle.");
			if (targetPixel != 0xFFFF00FF) {
				player.blockPath(false);
			}
			else {
				player.blockPath(true);
			}
		}
		
	}
	
	public void renderTiles(BaseScreen screen, int xTgt, int yTgt) {
		if (xTgt < 0 || xTgt >= this.width || yTgt < 0 || yTgt >= this.height)
			return;
		drawTiles(screen, xTgt, yTgt);
	}
	
	private void drawTiles(BaseScreen screen, int x, int y) {
		switch (pixels[y * this.width + x]) {
			case 0xFF00FF00:
				screen.blit(Art.testTile, (x - 1) * Tile.WIDTH, (y - 1) * Tile.HEIGHT);
				break;
			default:
				screen.blit(Art.smallTree, (x - 1) * Tile.WIDTH, (y - 1) * Tile.HEIGHT);
				break;
		}
	}
	
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
}
