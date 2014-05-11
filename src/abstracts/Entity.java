/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package abstracts;

import screen.BaseBitmap;
import screen.BaseScreen;

public abstract class Entity extends Tile {

	public int id;

	protected int xPosition;
	protected int yPosition;

	protected int xOffset;
	protected int yOffset;

	protected BaseBitmap bitmap;

	public boolean isRemoved;
	protected byte typeId = 0;

	// public abstract void initialize(BaseWorld world);

	public abstract void tick();

	public abstract void render(BaseScreen screen, int x, int y);

	public int getX() {
		return xPosition;
	}

	public int getY() {
		return yPosition;
	}

	public void setPosition(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
	}
}
