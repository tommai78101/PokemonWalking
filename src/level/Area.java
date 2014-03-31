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
	private boolean isInConnectionPoint;
	private PixelData currentPixelData;
	private final int areaID;
	private int sectorID;
	
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
		this.isInWarpZone = false;
		this.isInConnectionPoint = false;
		
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
				if (xPlayerPosition < 0 || xPlayerPosition >= this.width || yPlayerPosition < 0 || yPlayerPosition >= this.height)
					return;
				
				try {
					//Up, Down, Left, Right
					this.player.setAllBlockingDirections(checkSurroundingData(0, -1), checkSurroundingData(0, 1), checkSurroundingData(-1, 0), checkSurroundingData(1, 0));
				}
				catch (Exception e) {
					this.player.setAllBlockingDirections(checkSurroundingData(0, -1), checkSurroundingData(0, 1), checkSurroundingData(-1, 0), checkSurroundingData(1, 0));
				}
				
				//Target pixel is used to determine what pixel the player is currently standing on
				//(or what pixel the player is currently on top of).
				this.currentPixelData = areaData.get(this.yPlayerPosition).get(xPlayerPosition);
				this.checkCurrentPositionDataAndSetProperties();
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
				this.checkCurrentPositionDataAndSetProperties();
			}
			else {
				this.currentPixelData = this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
			}
		}
	}
	
	/**
	 * Checks the pixel data the player is currently on, and sets the tile properties according to the documentation provided. The tile the
	 * pixel data is representing determines the properties this will set, and will affect how the game interacts with the player.
	 * 
	 * @return Nothing.
	 * */
	private void checkCurrentPositionDataAndSetProperties() {
		//TODO: Fix this checkup.
		int pixel = this.getCurrentPixelData().getColor();
		int alpha = (pixel >> 24) & 0xFF;
		int red = (pixel >> 16) & 0xFF;
		int green = (pixel >> 8) & 0xFF;
		int blue = pixel & 0xFF;
		switch (alpha) {
			case 0x02: //Ledges
			{
				switch (red) {
					case 0x00: //Bottom
						this.player.setLockJumping(red, green, blue, Player.UP, Player.DOWN);
						break;
					case 0x01: //Bottom Left
						//this.player.setLockJumping(red, green, blue, Player.UP, Player.DOWN);
						break;
					case 0x02: //left
						this.player.setLockJumping(red, green, blue, Player.LEFT, Player.RIGHT);
						break;
					case 0x03: //top left
						break;
					case 0x04: //top
						this.player.setLockJumping(red, green, blue, Player.DOWN, Player.UP);
						break;
					case 0x05: //top right
						break;
					case 0x06: //right
						this.player.setLockJumping(red, green, blue, Player.RIGHT, Player.LEFT);
						break;
					case 0x07: //bottom right
						break;
					default:
						break;
				}
				break;
			}
			case 0x04: //Determines warp zone.
				if (!this.player.isLockedWalking()) {
					this.isInWarpZone = true;
				}
				break;
			case 0x05: //Area Connection Point.
				if (!this.player.isLockedWalking() && !this.isInWarpZone) {
					this.isInConnectionPoint = true;
					this.sectorID = this.currentPixelData.getTargetSectorID();
				}
				break;
			default:
				//If no special tiles, then it will keep reseting the flags.
				if (!this.player.isLockedWalking() || !this.player.isLockedJumping()) {
					this.isInWarpZone = false;
					this.isInConnectionPoint = false;
				}
				break;
		}
	}
	
	/**
	 * Checks the pixel data and sets properties according to the documentation provided. The tile the pixel data is representing determines
	 * whether it should allow or block the player from walking towards it.
	 * 
	 * @param xOffset
	 *            Sets the offset of the PixelData it should check by the X axis.
	 * @param yOffset
	 *            Sets the offset of the PixelData it should check by the Y axis.
	 * @return The value determining if this PixelData is to block or allow the player to pass/walk/jump through. Returns true to allow
	 *         player to walk from the player's last position to this tile. Returns false to block the player from walking from the player's last
	 *         position to this tile.
	 * */
	private boolean checkSurroundingData(int xOffset, int yOffset) {
		PixelData data = null;
		try {
			data = this.areaData.get(this.yPlayerPosition + yOffset).get(this.xPlayerPosition + xOffset);
		}
		catch (Exception e) {
			data = null;
		}
		if (data != null) {
			int color = data.getColor();
			int alpha = (color >> 24) & 0xFF;
			int red = (color >> 16) & 0xFF;
			//int green = (color >> 8) & 0xFF;
			//int blue = color & 0xFF;
			switch (alpha) {
				case 0x01: //Flat grass
					return false;
				case 0x02: //Ledge
				{
					switch (red) {
					/*TODO: Incorporate pixel data facingsBlocked variable to this section.
					 * Currently, the facingsBlocked[] variable for each pixel data isn't
					 * used.
					 */
						case 0x00: { //Bottom
							int y = this.yPlayerPosition + yOffset;
							if (this.yPlayerPosition < y)
								return false;
							return true;
						}
						case 0x01: //Bottom Left
							return true;
						case 0x02: {//Left
							int x = this.xPlayerPosition + xOffset;
							if (this.xPlayerPosition > x)
								return false;
							return true;
						}
						case 0x03: //Top Left
							return true;
						case 0x04: {//Top
							int y = this.yPlayerPosition + yOffset;
							if (this.yPlayerPosition > y)
								return false;
							return true;
						}
						case 0x05: //Top Right
							return true;
						case 0x06: { //Right
							int x = this.xPlayerPosition + xOffset;
							if (this.xPlayerPosition < x)
								return false;
							return true;
						}
						case 0x07: //Bottom Right
							return true;
						default:
							break;
					}
					break;
				}
				case 0x03: //Small tree
					return true;
				case 0x04: //Warp point
					//this.isInWarpZone = true;
					return false;
				case 0x05: //Area Connection point.
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
				PixelData data = this.areaData.get(y).get(x);
				screen.blitBiome(data.bitmap, x * Tile.WIDTH - xOff, y * Tile.HEIGHT - yOff, data);
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
		player.setAreaPosition(5, 5);
	}
	
	public void setDefaultPosition(PixelData data) {
		int color = data.getColor();
		int alpha = (color >> 24) & 0xFF;
		switch (alpha) {
			case 0x04: //Warp point
			{
				int green = (color >> 8) & 0xFF;
				int blue = color & 0xFF;
				this.player.setAreaPosition(green, blue);
				break;
			}
		}
	}
	
	public boolean playerIsInWarpZone() {
		return this.isInWarpZone;
	}
	
	public PixelData getCurrentPixelData() {
		//Return the pixel data the player is currently on top of.
		return this.currentPixelData;
		//return this.areaData.get(this.yPlayerPosition).get(this.xPlayerPosition);
	}
	
	public int getAreaID() {
		return this.areaID;
	}
	
	public boolean playerIsInConnectionPoint() {
		return this.isInConnectionPoint;
	}
	
	public boolean playerHasLeftConnectionPoint() {
		if (this.isInConnectionPoint) {
			if (this.player.isLockedWalking()) {
				//Leaving
				return true;
			}
		}
		return false;
	}
	
	public int getSectorID() {
		return this.sectorID;
	}
}
