package editor;

public class Trigger {
	private byte x, y;
	private char triggerID;
	private String name;
	
	private boolean[] valuesHasBeenSet = new boolean[3];
	
	public Trigger() {
		reset();
	}
	
	public void reset() {
		x = y = 0;
		triggerID = 0;
		for (int i = 0; i < valuesHasBeenSet.length; i++)
			valuesHasBeenSet[i] = false;
	}
	
	public int getDataValue() {
		return (x << 24) | (y << 16) | (triggerID & 0xFFFF);
	}
	
	public void setTriggerPositionX(byte x) {
		this.valuesHasBeenSet[0] = true;
		this.x = x;
	}
	
	public void setTriggerPositionY(byte y) {
		this.valuesHasBeenSet[1] = true;
		this.y = y;
	}
	
	public void setTriggerID(char value) {
		this.valuesHasBeenSet[2] = true;
		this.triggerID = value;
	}
	
	public boolean isPositionXSet() {
		return this.valuesHasBeenSet[0];
	}
	
	public boolean isPositionYSet() {
		return this.valuesHasBeenSet[1];
	}
	
	public boolean isTriggerIDSet() {
		return this.valuesHasBeenSet[2];
	}
	
	public byte getPositionX() {
		return this.x;
	}
	
	public byte getPositionY() {
		return this.y;
	}
	
	public char getTriggerID() {
		return this.triggerID;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
