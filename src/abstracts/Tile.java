package abstracts;

import interfaces.BoundingBoxCollidable;
import resources.Art;
import screen.BaseScreen;

public abstract class Tile implements BoundingBoxCollidable {
	public static int WIDTH = 16;
	public static int HEIGHT = 16;
	
	public static final byte PLAYER = 0;
	public static final byte TREE = 1;
	
	public int xPosition;
	public int yPosition;
	protected byte typeId;
	
	public boolean canPass(Entity e) {
		//Defaults to grass or walkable path.
		return true;
	}
	
	public void render(BaseScreen screen) {
		screen.blit(Art.grass, xPosition * Tile.WIDTH, yPosition * Tile.HEIGHT);
	}
	
	public byte getType() {
		return typeId;
	}
}
