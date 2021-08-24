package editor;

import java.util.Arrays;

public class Trigger {
	/**
	 * This represents the trigger ID is not an NPC trigger.
	 */
	public static final short NPC_TRIGGER_ID_NONE = 0;

	private byte x, y; // This occupies the AR value in the pixel color, ARGB space.
	private short triggerID; // This occupies the GB value in the pixel color, ARGB space.
	private short npcTriggerID; // This occupies the GB value in the pixel color, ARGB space.
	private String name;
	private String script;
	private String checksum;
	private boolean isNpcTrigger;

	private boolean[] valuesHasBeenSet;

	private static final int FLAG_PositionX = 0;
	private static final int FLAG_PositionY = 1;
	private static final int FLAG_TriggerID = 2;
	private static final int FLAG_TriggerScript = 3;
	private static final int FLAG_NpcTriggerID = 4;

	public Trigger() {
		this.valuesHasBeenSet = new boolean[Arrays.asList(
			Trigger.FLAG_PositionX,
			Trigger.FLAG_PositionY,
			Trigger.FLAG_TriggerID,
			Trigger.FLAG_TriggerScript,
			Trigger.FLAG_NpcTriggerID
		).size()];
		this.reset();
	}

	public void reset() {
		this.x = this.y = -1;
		this.triggerID = 0;
		this.npcTriggerID = Trigger.NPC_TRIGGER_ID_NONE;
		this.name = "<Untitled>";
		Arrays.fill(this.valuesHasBeenSet, false);
	}

	public int getDataValue() {
		return (this.x << 24) | (this.y << 16) | (this.triggerID & 0xFFFF);
	}

	public int getNpcDataValue() {
		// The higher 8 bits are to be reserved for something else.
		return this.npcTriggerID;
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
		return this.valuesHasBeenSet[Trigger.FLAG_TriggerID] || this.valuesHasBeenSet[Trigger.FLAG_NpcTriggerID];
	}

	public boolean isTriggerScriptSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_TriggerScript];
	}

	public boolean isNpcTrigger() {
		return this.isNpcTrigger;
	}

	public void setNpcTrigger(boolean state) {
		this.isNpcTrigger = state;
	}

	public void setNpcTriggerID(short npcTriggerId) {
		this.npcTriggerID = npcTriggerId;
		if (!this.isNpcTrigger && npcTriggerId > Trigger.NPC_TRIGGER_ID_NONE) {
			this.setNpcTrigger(true);
			this.valuesHasBeenSet[Trigger.FLAG_NpcTriggerID] = true;
		}
	}

	public boolean areRequiredFieldsAllSet() {
		boolean result = this.isPositionXSet();
		result = result && this.isPositionYSet();
		return result && this.isTriggerIDSet();
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

	public short getNpcTriggerID() {
		return this.npcTriggerID;
	}

	public boolean checkTriggerID(short triggerId) {
		return this.triggerID == triggerId;
	}

	public boolean checkTriggerID(int triggerId) {
		return this.checkTriggerID((short) triggerId);
	}

	public boolean checkNpcTriggerID(short npcTriggerId) {
		return this.npcTriggerID == npcTriggerId;
	}

	public boolean checkNpcTriggerID(int npcTriggerId) {
		return this.checkNpcTriggerID((short) npcTriggerId);
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

	public String getChecksum() {
		return this.checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public boolean equalsTriggerId(short otherTriggerId) {
		return this.triggerID == otherTriggerId;
	}

	public boolean equalsTriggerId(int otherTriggerId) {
		return this.equalsTriggerId((short) (otherTriggerId & 0xFFFF));
	}

	public boolean isEraser() {
		return (this.triggerID == 0 && this.npcTriggerID == 0);
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
		return prime * result + this.y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (this.getClass() != obj.getClass())) {
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
		if ((this.triggerID != other.triggerID) || (this.x != other.x) || (this.y != other.y)) {
			return false;
		}
		return true;
	}
}
