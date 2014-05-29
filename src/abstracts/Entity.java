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
	
	protected String name;
	protected Boolean gender;
	
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
	
	public byte[] getByteGender() {
		byte[] result = new byte[1];
		if (gender == null)
			gender = Boolean.TRUE; //Default gender: Male. False = Female.
		result[0] = (byte) (gender.booleanValue() ? 0x1 : 0xFF);
		return result;
	}
}
