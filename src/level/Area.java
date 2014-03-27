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
			else if (!this.player.isLockedJumping() && this.player.isLockedWalking()) {
				//A
				//This goes with B. (30 lines down below.)
				//It may be possible the player is still in the air, and hasn't done checking if the current pixel
				//data is a ledge or not. This continues the data checking. It's required.
				xPlayerPosition = player.getXInArea();
				yPlayerPosition = player.getYInArea();
				if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
					return;
				this.currentPixelData = areaData.get(this.yPlayerPosition).get(xPlayerPosition);
				int pixel = this.currentPixelData.getColor();
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;
				if (blue == 0xDD) {
					this.player.setLockJumping(red, green, blue, Player.UP, Player.DOWN);
				}
			}
		}
	}
	
	public void checkData() {
		//TODO: Fix this checkup.
		int pixel = this.currentPixelData.getColor();
		//Determines warp zone.
		int red = (pixel >> 16) & 0xFF;
		if (red == 0x7F) {
			this.isInWarpZone = true;
			return;
		}
		
		//B
		//This goes with A. (30 lines up above.)
		int green = (pixel >> 8) & 0xFF;
		int blue = pixel & 0xFF;
		
		//Determine ledges (horizontal) 
		if (blue == 0xDD) {
			this.player.setLockJumping(Player.UP, Player.DOWN);
		}
	}
	
	/**
	 * Checks the pixel data and sets properties according to the documentation provided.
	 * 
	 * @param data
	 *            Checks the PixelData that was passed in.
	 * @param xOffset
	 *            Sets the offset of the PixelData it should check by the X axis.
	 * @param yOffset
	 *            Sets the offset of the PixelData it should check by the Y axis.
	 * @return The value determining if this PixelData is to block or allow the player to pass/walk/jump through.
	 * */
	public boolean checkData(PixelData data, int xOffset, int yOffset) {
		if (data != null) {
			int color = data.getColor();
			int alpha = (color >> 24) & 0xFF;
			int red = (color >> 16) & 0xFF;
			int green = (color >> 8) & 0xFF;
			int blue = color & 0xFF;
			switch (alpha) {
				case 0x01: //Flat grass
					return false;
				case 0x02: //Ledge
				{
					switch (red) {
						case 0x00: //Horizontal Bottom
							this.player.setLockJumping(Player.UP, Player.DOWN);
							int y = this.yPlayerPosition + yOffset;
							if (this.yPlayerPosition < y)
								return false;
							return true;
					}
				}
				case 0x03: //Small tree
					return true;
				case 0x04: //Warp point
					this.isInWarpZone = true;
					return false;
				default: //Any other type of tiles.
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
	
	public void setDebugDefaultPosition() {
		//When the game starts from the very beginning, the player must always start from the very first way point.
		player.setAreaPosition(1, 1);
	}
	
	public void setDefaultPosition(PixelData data) {
		PixelData targetData = null;
		if (data.isWarpZoneEnabled()) {
			LOOP_BREAK_Area_setDefaultPosition_1: for (ArrayList<PixelData> list : this.areaData) {
				for (PixelData p : list) {
					if (((p.getColor() & 0xFF0000) >> 16) == 0x7F) {
						targetData = p;
						break LOOP_BREAK_Area_setDefaultPosition_1;
					}
				}
			}
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
