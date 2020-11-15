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
		this.reset();
	}

	public void reset() {
		this.x = this.y = -1;
		this.triggerID = 0;
		this.name = "<Untitled>";
		for (int i = 0; i < this.valuesHasBeenSet.length; i++)
			this.valuesHasBeenSet[i] = false;
	}

	public int getDataValue() {
		return (this.x << 24) | (this.y << 16) | (this.triggerID & 0xFFFF);
	}

	public void setTriggerPositionX(byte x) {
		this.valuesHasBeenSet[Trigger.FLAG_PositionX] = true;
		this.x = x;
	}

	public void setTriggerPositionY(byte y) {
		this.valuesHasBeenSet[Trigger.FLAG_PositionY] = true;
		this.y = y;
	}

	public void setTriggerID(short value) {
		this.valuesHasBeenSet[Trigger.FLAG_TriggerID] = true;
		this.triggerID = value;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public boolean isPositionXSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_PositionX];
	}

	public boolean isPositionYSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_PositionY];
	}

	public boolean isTriggerIDSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_TriggerID];
	}

	public boolean isTriggerScriptSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_TriggerScript];
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

	public boolean hasValidPosition() {
		return !(this.x == -1 || this.y == -1);
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

	public boolean equalsTriggerId(short otherTriggerId) {
		return this.triggerID == otherTriggerId;
	}

	public boolean equalsTriggerId(int otherTriggerId) {
		return this.equalsTriggerId((short) (otherTriggerId & 0xFFFF));
	}

	public boolean isEraser() {
		return this.triggerID == 0;
	}

	public static Trigger createEraser() {
		Trigger trigger = new Trigger();
		trigger.setTriggerID((short) 0);
		trigger.setName("Eraser");
		return trigger;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + this.triggerID;
		result = prime * result + this.x;
		result = prime * result + this.y;
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Trigger other = (Trigger) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.triggerID != other.triggerID) {
			return false;
		}
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		return true;
	}
}
