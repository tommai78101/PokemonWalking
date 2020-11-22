/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package screen;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Random;

import level.PixelData;
import level.WorldConstants;
import resources.Art;
import resources.Mod;

public class Scene extends Bitmap {
	public enum FlashingType {
		NORMAL,
		BURST
	}

	// TODO: Add more drawing functions that enable more controls when it comes to
	// rendering assets.

	protected BufferedImage image;
	protected Graphics graphics;
	protected int xOffset;
	protected int yOffset;

	private static final byte MAX_TICK_LIMIT = 0x7;
	private byte tick = Scene.MAX_TICK_LIMIT;

	// private boolean cutScreen;

	public Scene(int w, int h) {
		super(w, h);
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		this.pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
		this.graphics = this.image.getGraphics();
	}

	public void loadResources() {
		Art.loadAllResources(this);
		Mod.loadModdedResources();
	}

	public BufferedImage getBufferedImage() {
		return this.image;
	}

	public Graphics getGraphics() {
		return this.graphics;
	}

	public void clear(int color) {
		Arrays.fill(this.pixels, color);
	}

	public void blit(Bitmap bitmap, int x, int y) {
		if (bitmap != null)
			this.blit(bitmap, x + this.xOffset, y + this.yOffset, bitmap.width, bitmap.height);
	}

	public void blitBiome(Bitmap bitmap, int x, int y, PixelData data) {
		if (bitmap != null) {
			this.blitBiome(bitmap, x + this.xOffset, y + this.yOffset, bitmap.width, bitmap.height, data);
		}
	}

	public void npcBlit(Bitmap bitmap, int x, int y) {
		if (bitmap != null)
			this.blit(bitmap, x + this.xOffset, y + this.yOffset - 4, bitmap.width, bitmap.height);
	}

	public void blit(Bitmap bitmap, int x, int y, int w, int h) {
		// This directly blits the bitmap to the X and Y coordinates within the screen
		// area.
		// The Rect adjusts the blitting area to within the screen area.
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
			}
		}
	}

	public void blitBiome(Bitmap bitmap, int x, int y, int w, int h, PixelData data) {
		// This directly blits the bitmap to the X and Y coordinates within the screen
		// area.
		// The Rect adjusts the blitting area to within the screen area.
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
		int biomeColor = this.getBiomeBaseColor(tileID, red, green, blue);
		int tick = 0;

		for (int yy = blitArea.topLeftCorner_Y; yy < blitArea.bottomRightCorner_Y; yy++) {

			int tgt = yy * this.width + blitArea.topLeftCorner_X;
			int src = (yy - y) * bitmap.width + (blitArea.topLeftCorner_X - x);
			tgt -= src;
			for (int xx = src; xx < src + blitWidth; xx++) {
				int color = bitmap.pixels[xx];
				int alpha = (color >> 24) & 0xFF;
				// This alpha value determines the areas in the bitmap what to draw.
				// Has nothing to do with pixel data properties.

				switch (alpha) {
					case 0x0:
						// Biome Color with a bit of speckled light/dark patches.
						if ((tick++ % 17 < 7) && (tick++ % 21 < 2))
							this.pixels[tgt + xx] = Scene.lighten(biomeColor, 0.07f);
						else if ((tick++ % 23 < 4) && (tick++ % 19 < 3))
							this.pixels[tgt + xx] = Scene.darken(biomeColor, 0.07f);
						else
							this.pixels[tgt + xx] = biomeColor;
						tick++;
						if (tick > w * w)
							tick = 0;
						break;
					case 0x32:
						this.pixels[tgt + xx] = Scene.lighten(biomeColor, 0.003f);
						break;
					case 0x64:
						this.pixels[tgt + xx] = Scene.lighten(biomeColor, 0.006f);
						break;
					default:
						this.pixels[tgt + xx] = color;
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
		for (int p = 0; p < this.pixels.length; p++)
			this.pixels[p] = r.nextInt();
	}

	public int getXOffset() {
		return this.xOffset;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	public boolean invert() {
		if (this.tick < 0x2) {
			for (int i = 0; i < this.pixels.length; i++)
				this.pixels[i] = 0xFF000000 | (0xAAAAAA - (this.pixels[i] & 0xFFFFFF));
			this.tick++;
			return true;
		}
		if (this.tick < 0x6) {
			for (int i = 0; i < this.pixels.length; i++)
				this.pixels[i] = 0xFFAAAAAA;
			this.tick++;
			return true;
		}
		this.tick = Scene.MAX_TICK_LIMIT;
		return false;
	}

	public boolean renderEffectFlashing(FlashingType speed) {
		switch (speed) {
			case NORMAL:
			default: {
				if (this.tick < Scene.MAX_TICK_LIMIT) {
					for (int i = 0; i < this.pixels.length; i++)
						this.pixels[i] = 0xFFFFFFFF;
					this.tick++;
					return true;
				}
				this.tick = Scene.MAX_TICK_LIMIT;
				return false;
			}
			case BURST: {
				if (this.tick < 0x2) {
					for (int i = 0; i < this.pixels.length; i++)
						this.pixels[i] = 0xFFAAAAAA;
					this.tick++;
					return true;
				}
				else if (this.tick < 0x4) {
					for (int i = 0; i < this.pixels.length; i++)
						this.pixels[i] = 0xFFF7F7F7;
					this.tick++;
					return true;
				}
				else if (this.tick < 0x6) {
					for (int i = 0; i < this.pixels.length; i++)
						this.pixels[i] = 0xFFAAAAAA;
					this.tick++;
					return true;
				}
				this.tick = Scene.MAX_TICK_LIMIT;
				return false;
			}
		}
	}

	public void setRenderingEffectTick(byte value) {
		this.tick = value;
	}

	public byte getRenderingEffectTick() {
		return this.tick;
	}

	public boolean isRenderingEffect() {
		return (this.tick < Scene.MAX_TICK_LIMIT);
	}

	public void resetRenderingEffect() {
		this.tick = Scene.MAX_TICK_LIMIT;
	}

	// -------------------------------------------
	// Private methods

	private int getBiomeBaseColor(int tileID, int red, int green, int blue) {
		int color = WorldConstants.GRASS_GREEN;
		switch (tileID) {
			case 0x01: // Grass
				switch (red) {
					case 0x00:
						switch (green) {
							case 0x00:
								break;
							default:
								break;
						}
						break;
					case 0x01: // Mountain ground
						for (int i = 0; i < blue; i++) {
							color = Scene.lighten(color, 0.1f);
						}
						break;
					default:
						break;
				}
				break;
			case 0x02: // Ledges
				switch (green) {
					case 0x00:
						break;
					case 0x01:
						color = WorldConstants.MOUNTAIN_BROWN;
						break;
				}
				break;
			case 0x06: // Stairs
				switch (green) {
					case 0x00:
						break;
					case 0x01:
						color = WorldConstants.MOUNTAIN_BROWN;
						break;
				}
				break;
			default:
				break;
		}
		return color;
	}

	// -------------------------------------------
	// Public static methods

	public static int lighten(int color, float amount) {
		// int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		return 0xFF000000 | ((int) Math.min(255, r + 255 * amount) & 0xFF) << 16
			| ((int) Math.min(255, g + 255 * amount) & 0xFF) << 8 | (int) Math.min(255, b + 255 * amount) & 0xFF;
	}

	public static int darken(int color, float amount) {
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		return 0xFF000000 | ((int) Math.min(255, r - 255 * amount) & 0xFF) << 16
			| ((int) Math.min(255, g - 255 * amount) & 0xFF) << 8 | (int) Math.min(255, b - 255 * amount) & 0xFF;
	}
}
