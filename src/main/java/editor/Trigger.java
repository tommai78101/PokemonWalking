package editor;

public class Trigger {
	private byte x, y;
	private short triggerID;
	private String name;
	private String script;

	private boolean[] valuesHasBeenSet = new boolean[4];
	private static final int FLAG_PositionX = 0;
	private static final int FLAG_PositionY = 1;
	private static final int FLAG_TriggerID = 2;
	private static final int FLAG_TriggerScript = 3;

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
		this.valuesHasBeenSet[FLAG_PositionX] = true;
		this.x = x;
	}

	public void setTriggerPositionY(byte y) {
		this.valuesHasBeenSet[FLAG_PositionY] = true;
		this.y = y;
	}

	public void setTriggerID(short value) {
		this.valuesHasBeenSet[FLAG_TriggerID] = true;
		this.triggerID = value;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public boolean isPositionXSet() {
		return this.valuesHasBeenSet[FLAG_PositionX];
	}

	public boolean isPositionYSet() {
		return this.valuesHasBeenSet[FLAG_PositionY];
	}

	public boolean isTriggerIDSet() {
		return this.valuesHasBeenSet[FLAG_TriggerID];
	}

	public boolean isTriggerScriptSet() {
		return this.valuesHasBeenSet[FLAG_TriggerScript];
	}

	public boolean areRequiredFieldsAllSet() {
		boolean result = true;
		for (int i = 0; i < 3; i++)
			if (!this.valuesHasBeenSet[i])
				result = false;
		return result;
	}

	public boolean isEmptyScriptTrigger() {
		return this.valuesHasBeenSet[3];
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

	public String getScript() {
		return this.script;
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
