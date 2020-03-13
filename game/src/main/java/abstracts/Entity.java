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

import interfaces.Tileable;
import interfaces.UpdateRenderable;
import level.PixelData;
import screen.BaseBitmap;

/**
 * Parent abstract class of all abstract classes.
 * 
 * Holds all basic data needed for everything Entity.
 * 
 * @author tlee
 *
 */
public abstract class Entity implements Tileable, UpdateRenderable {
	public int id;
	public int interactableID;
	public boolean isRemoved;

	protected byte typeId = 0;
	protected int xPosition;
	protected int yPosition;

	protected int xOffset;
	protected int yOffset;
	protected BaseBitmap bitmap = null;
	
	protected int lastFacing = 0;
	protected int facing = 0;

	protected String name;
	protected PixelData pixelData;

	// public abstract void initialize(BaseWorld world);

	public int getX() {
		return xPosition;
	}

	public int getY() {
		return yPosition;
	}

	protected void setPosition(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
	}

	public byte[] getByteName() {
		if (name == null)
			name = "Joe";
		byte[] result = new byte[16];
		byte[] nameData = name.getBytes();
		for (int i = 0; i < result.length; i++) {
			if (i < name.length())
				result[i] = nameData[i];
			else
				result[i] = 0;
		}
		return result;
	}

	/**
	 * Gets a value that determines where the direction the entity is currently facing towards.
	 * 
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public int getFacing() {
		return this.facing;
	}

	/**
	 * Sets a value that determines where the direction the entity is currently facing towards.
	 */
	public void setFacing(int value) {
		this.lastFacing = this.facing;
		this.facing = value;
	}

	/**
	 * Gets a value that determines the direction the player had last been facing towards at.
	 * 
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public int getLastFacing() {
		return this.lastFacing;
	}

	/**
	 * Checks whether the entity object has recently changed its facing direction.
	 * 
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public boolean hasChangedFacing() {
		// True, if current facing has been changed.
		return this.lastFacing != this.facing;
	}

	public PixelData getPixelData() {
		return this.pixelData;
	}
	
	public void setPixelData(PixelData data) {
		this.pixelData = data;
	}
}
