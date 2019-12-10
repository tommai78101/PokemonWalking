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
	public static final int UP = 2;
	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 3;

	public int id;
	public int facing = 0;

	protected int xPosition;
	protected int yPosition;

	protected int xOffset;
	protected int yOffset;

	protected BaseBitmap bitmap;

	protected String name;

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
	 * Gets a value that determines where the direction the entity is currently
	 * facing towards.
	 * 
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN,
	 *         Entity.LEFT, Entity.RIGHT.
	 */
	public int getFacing() {
		return this.facing;
	}

	/**
	 * Sets a value that determines where the direction the entity is currently
	 * facing towards.
	 */
	public void setFacing(int value) {
		this.facing = value;
	}
}
