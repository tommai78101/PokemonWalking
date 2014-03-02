package screen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Random;

import resources.Art;

public class BaseScreen extends BaseBitmap {
	
	//TODO: Add more drawing functions that enable more controls when it comes to rendering assets.
	
	protected BufferedImage image;
	protected int xOffset;
	protected int yOffset;
	
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
		this.blit(bitmap, x + this.xOffset, y + this.yOffset, bitmap.width, bitmap.height);
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
	
	public void setOffset(int xOff, int yOff) {
		this.xOffset = xOff;
		this.yOffset = yOff;
	}
	
	public void createStaticNoise(Random r) {
		for (int p = 0; p < pixels.length; p++)
			pixels[p] = r.nextInt();
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
	
	public int getXOffset() {
		return xOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
}
