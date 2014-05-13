/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
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
