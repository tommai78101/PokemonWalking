package editor;

public class Trigger {
	private byte x, y;
	private short triggerID;
	private String name;
	
	private boolean[] valuesHasBeenSet = new boolean[3];
	
	public Trigger() {
		reset();
	}
	
	public void reset() {
		x = y = 0;
		triggerID = 0;
		name = "<Untitled>";
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
	
	public void setTriggerID(short value) {
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
	
	public boolean isAllSet() {
		boolean result = true;
		for (int i = 0; i < this.valuesHasBeenSet.length; i++)
			if (!this.valuesHasBeenSet[i])
				result = false;
		return result;
	}
	
	public byte getPositionX() {
		return this.x;
	}
	
	public byte getPositionY() {
		return this.y;
	}
	
	public short getTriggerID() {
		return this.triggerID;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + triggerID;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Trigger other = (Trigger) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!name.equals(other.name)) {
			return false;
		}
		if (triggerID != other.triggerID) {
			return false;
		}
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}
}
