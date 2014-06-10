/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package screen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import level.WorldConstants;

public class BaseBitmap {
	
	protected int[] pixels;
	protected int width;
	protected int height;
	
	private int id;
	
	public BaseBitmap(int w, int h) {
		this.width = w;
		this.height = h;
		this.pixels = new int[w * h];
	}
	
	public BaseBitmap(int w, int h, int[] p) {
		this.width = w;
		this.height = h;
		this.pixels = new int[w * h];
		for (int i = 0; i < p.length; i++)
			this.pixels[i] = p[i];
	}
	
	public void setID(int value) {
		this.id = value;
	}
	
	public int getID() {
		return this.id;
	}
	
	public BaseBitmap load(String filename) {
		try {
			BufferedImage image = ImageIO.read(BaseBitmap.this.getClass().getClassLoader().getResource(filename));
			System.out.println(filename);
			return load(image);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BaseBitmap load(File file) {
		try {
			// Prints out the bitmap filename. If there's something wrong, it won't print it out.
			System.out.println(file.getAbsolutePath());
			BufferedImage image = ImageIO.read(file);
			return load(image);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BaseBitmap load(BufferedImage image) {
		if (image == null)
			return null;
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		BaseBitmap result = new BaseBitmap(width, height, image.getRGB(0, 0, width, height, null, 0, width));
		WorldConstants.bitmaps.add(result);
		return result;
	}
	
	public BaseBitmap[][] cut(String filename, int w, int h, int clipW, int clipH) {
		try {
			// BufferedImage image = ImageIO.read(BaseBitmap.class.getResource(filename));
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
