package screen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.imageio.ImageIO;

public class BaseBitmap {
	protected int[] pixels;
	protected int width;
	protected int height;
	
	public BaseBitmap(int w, int h) {
		this.width = w;
		this.height = h;
		this.pixels = new int[w * h];
	}
	
	public BaseBitmap(int w, int h, int[] p) {
		this.width = w;
		this.height = h;
		this.pixels = p;
	}
	
	public BaseBitmap load(String filename) {
		try {
			//Prints out the bitmap filename. If there's something wrong, it won't print it out.
			Enumeration<URL> urls = this.getClass().getClassLoader().getResources(filename);
			for (URL url = null; urls.hasMoreElements();) {
				url = urls.nextElement();
				System.out.println(url.toString());
			}

			BufferedImage image = ImageIO.read(BaseBitmap.this.getClass().getClassLoader().getResource(filename));
			return load(image);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BaseBitmap load(BufferedImage image) {
		if (image == null) return null;
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		return new BaseBitmap(width, height, image.getRGB(0, 0, width, height, null, 0, width));
	}
	
	public BaseBitmap[][] cut(String filename, int w, int h, int clipW, int clipH) {
		try {
			//BufferedImage image = ImageIO.read(BaseBitmap.class.getResource(filename));
			BufferedImage image = ImageIO.read(BaseBitmap.this.getClass().getClassLoader().getResource(filename));
			int xTiles = (image.getWidth() - clipW) / w;
			int yTiles = (image.getHeight() - clipH) / h;
			BaseBitmap[][] results = new BaseBitmap[xTiles][yTiles];
			for (int x = 0; x < xTiles; x++) {
				for (int y = 0; y < yTiles; y++) {
					results[x][y] = new BaseBitmap(w, h);
					image.getRGB(clipW + x * w, clipH + y * h, w, h, results[x][y].pixels, 0, w);
				}
			}
			return results;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int[] getPixels() {
		return this.pixels;
	}
}
