package screen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Random;
import level.PixelData;
import resources.Art;

public class BaseScreen extends BaseBitmap {
	
	//TODO: Add more drawing functions that enable more controls when it comes to rendering assets.
	
	protected BufferedImage image;
	protected int xOffset;
	protected int yOffset;
	
	private byte invertTick = 0x7;
	
	public BaseScreen(int w, int h) {
		super(w, h);
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
	public void loadResources() {
		Art.loadAllResources(this);
	}
	
	public BufferedImage getBufferedImage() {
		return image;
	}
	
	public void clear(int color) {
		Arrays.fill(pixels, color);
	}
	
	public void blit(BaseBitmap bitmap, int x, int y) {
		if (bitmap != null)
			this.blit(bitmap, x + this.xOffset, y + this.yOffset, bitmap.width, bitmap.height);
	}
	
	public void blitBiome(BaseBitmap bitmap, int x, int y, PixelData data) {
		if (bitmap != null)
			this.blitBiome(bitmap, x + this.xOffset, y + this.yOffset, bitmap.width, bitmap.height, data);
	}
	
	public void npcBlit(BaseBitmap bitmap, int x, int y) {
		if (bitmap != null)
			this.blit(bitmap, x + this.xOffset, y + this.yOffset - 4, bitmap.width, bitmap.height);
	}
	
	public void blit(BaseBitmap bitmap, int x, int y, int w, int h) {
		//This directly blits the bitmap to the X and Y coordinates within the screen area.
		//The Rect adjusts the blitting area to within the screen area.
		if (w <= -1)
			w = bitmap.width;
		if (h <= -1)
			h = bitmap.height;
		Rect blitArea = new Rect(x, y, w, h).adjust(this);
		int blitWidth = blitArea.bottomRightCorner_X - blitArea.topLeftCorner_X;
		
		for (int yy = blitArea.topLeftCorner_Y; yy < blitArea.bottomRightCorner_Y; yy++) {
			int tgt = yy * this.width + blitArea.topLeftCorner_X;
			int src = (yy - y) * bitmap.width + (blitArea.topLeftCorner_X - x);
			tgt -= src;
			for (int xx = src; xx < src + blitWidth; xx++) {
				int color = bitmap.pixels[xx];
				int alpha = (color >> 24) & 0xFF;
				if (alpha == 0xFF) {
					this.pixels[tgt + xx] = color;
				}
				else {
					this.pixels[tgt + xx] = blendPixels(this.pixels[tgt + xx], color);
				}
			}
		}
	}
	
	public void blitBiome(BaseBitmap bitmap, int x, int y, int w, int h, PixelData data) {
		//This directly blits the bitmap to the X and Y coordinates within the screen area.
		//The Rect adjusts the blitting area to within the screen area.
		if (w <= -1)
			w = bitmap.width;
		if (h <= -1)
			h = bitmap.height;
		Rect blitArea = new Rect(x, y, w, h).adjust(this);
		int blitWidth = blitArea.bottomRightCorner_X - blitArea.topLeftCorner_X;
		
		int dataColor = data.getColor();
		int tileID = (dataColor >> 24) & 0xFF;
		int red = (dataColor >> 16) & 0xFF;
		int green = (dataColor >> 8) & 0xFF;
		int blue = dataColor & 0xFF;
		int biomeColor = getBiomeBaseColor(tileID, red, green, blue);
		
		for (int yy = blitArea.topLeftCorner_Y; yy < blitArea.bottomRightCorner_Y; yy++) {
			int tgt = yy * this.width + blitArea.topLeftCorner_X;
			int src = (yy - y) * bitmap.width + (blitArea.topLeftCorner_X - x);
			tgt -= src;
			for (int xx = src; xx < src + blitWidth; xx++) {
				int color = bitmap.pixels[xx];
				int alpha = (color >> 24) & 0xFF;
				switch (alpha) {
					case 0x0:
						this.pixels[tgt + xx] = biomeColor;
						break;
					case 0x32:
						this.pixels[tgt + xx] = lighten(biomeColor, 0.2f);
						break;
					case 0x64:
						this.pixels[tgt + xx] = lighten(biomeColor, 0.4f);
						break;
					default:
						this.pixels[tgt + xx] = blendPixels(this.pixels[tgt + xx], color);
						break;
				}
			}
		}
	}
	
	public void setOffset(int xOff, int yOff) {
		this.xOffset = xOff;
		this.yOffset = yOff;
	}
	
	public void createStaticNoise(Random r) {
		for (int p = 0; p < pixels.length; p++)
			pixels[p] = r.nextInt();
	}
	
	public int getXOffset() {
		return xOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
	
	public boolean invert() {
		if (this.invertTick < 0x2) {
			for (int i = 0; i < this.pixels.length; i++)
				this.pixels[i] = 0xFF000000 | (0xAAAAAA - (this.pixels[i] & 0xFFFFFF));
			this.invertTick++;
			return true;
		}
		else if (this.invertTick < 0x6) {
			for (int i = 0; i < this.pixels.length; i++)
				this.pixels[i] = 0xFFAAAAAA;
			this.invertTick++;
			return true;
		}
		this.invertTick = 0x7;
		return false;
	}
	
	public void setInvertTick(byte value) {
		this.invertTick = value;
	}
	
	public byte getInvertTick() {
		return this.invertTick;
	}
	
	//-------------------------------------------
	//Private methods
	
	private int blendPixels(int bgColor, int blendColor) {
		int alphaBlend = (blendColor >> 24) & 0xFF;
		int alphaBackground = 256 - alphaBlend;
		
		int bgRed = bgColor & 0xFF0000;
		int bgGreen = bgColor & 0xFF00;
		int bgBlue = bgColor & 0xFF;
		
		int blendRed = blendColor & 0xFF0000;
		int blendGreen = blendColor & 0xFF00;
		int blendBlue = blendColor & 0xFF;
		
		int red = ((blendRed * alphaBlend + bgRed * alphaBackground) >> 8) & 0xFF0000;
		int green = ((blendGreen * alphaBlend + bgGreen * alphaBackground) >> 8) & 0xFF00;
		int blue = ((blendBlue * alphaBlend + bgBlue * alphaBackground) >> 8) & 0xFF;
		
		return 0xFF000000 | red | green | blue;
	}
	
	private int getBiomeBaseColor(int tileID, int red, int green, int blue) {
		int color = 0xFFA4E767;
		switch (tileID) {
			case 0x01: //Grass
				switch (red) {
					case 0x00:
						switch (green) {
							case 0x00:
								break;
							default:
								break;
						}
						break;
					default:
						break;
				}
				break;
			case 0x02: //Ledges
				switch (green) {
					case 0x00:
						break;
					case 0x01:
						color = 0xFF7F411D;
						break;
				}
				break;
			default:
				break;
		}
		return color;
	}
	
	private int lighten(int color, float amount) {
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		return 0xFF000000 | ((int) Math.min(255, r + 255 * amount) & 0xFF) << 16 | ((int) Math.min(255, g + 255 * amount) & 0xFF) << 8 | (int) Math.min(255, b + 255 * amount) & 0xFF;
	}
}
