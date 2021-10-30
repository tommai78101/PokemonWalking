package editor;

import java.util.Arrays;

public class Trigger {
	/**
	 * Used to represent a trigger that is not any trigger.
	 */
	public static final short ID_NONE = 0;

	private static final int FLAG_PositionX = 0;
	private static final int FLAG_PositionY = 1;
	private static final int FLAG_TriggerID = 2;
	private static final int FLAG_TriggerScript = 3;
	private static final int FLAG_NpcTriggerID = 4;
	private static final int FLAG_EventTriggerID = 5;
	private byte x, y; // This occupies the AR value in the pixel color, ARGB space.
	private short triggerID; // This occupies the GB value in the pixel color, ARGB space.
	private short npcTriggerID; // This occupies the GB value in the pixel color, ARGB space.
	private short eventTriggerID; // This occupies the GB value in the pixel color, ARGB space.

	private String name;
	private String script;
	private String checksum;
	private boolean isNpcTrigger;
	private boolean isEventTrigger;
	private boolean[] valuesHasBeenSet;

	public Trigger() {
		this.valuesHasBeenSet = new boolean[Arrays.asList(
			Trigger.FLAG_PositionX,
			Trigger.FLAG_PositionY,
			Trigger.FLAG_TriggerID,
			Trigger.FLAG_TriggerScript,
			Trigger.FLAG_NpcTriggerID,
			Trigger.FLAG_EventTriggerID
		).size()];
		this.reset();
	}

	public static Trigger createEraser() {
		Trigger trigger = new Trigger();
		trigger.setTriggerID((short) 0);
		trigger.setName("Eraser");
		return trigger;
	}

	public boolean areRequiredFieldsAllSet() {
		boolean result = this.isPositionXSet();
		result = result && this.isPositionYSet();
		return result && this.isTriggerIDSet();
	}

	public boolean checkEventTriggerID(int eventTriggerID) {
		return this.checkEventTriggerID((short) eventTriggerID);
	}

	public boolean checkEventTriggerID(short eventTriggerID) {
		return this.eventTriggerID == eventTriggerID;
	}

	public boolean checkNpcTriggerID(int npcTriggerId) {
		return this.checkNpcTriggerID((short) npcTriggerId);
	}

	public boolean checkNpcTriggerID(short npcTriggerId) {
		return this.npcTriggerID == npcTriggerId;
	}

	public boolean checkTriggerID(int triggerId) {
		return this.checkTriggerID((short) triggerId);
	}

	public boolean checkTriggerID(short triggerId) {
		return this.triggerID == triggerId;
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

	public boolean equalsTriggerId(int otherTriggerId) {
		return this.equalsTriggerId((short) (otherTriggerId & 0xFFFF));
	}

	public boolean equalsTriggerId(short otherTriggerId) {
		return this.triggerID == otherTriggerId;
	}

	public String getChecksum() {
		return this.checksum;
	}

	public int getDataValue() {
		return (this.x << 24) | (this.y << 16) | (this.triggerID & 0xFFFF);
	}

	public short getEventTriggerID() {
		return this.eventTriggerID;
	}

	public String getName() {
		return this.name;
	}

	public int getNpcDataValue() {
		// The higher 8 bits are to be reserved for something else.
		return this.npcTriggerID;
	}

	public short getNpcTriggerID() {
		return this.npcTriggerID;
	}

	public byte getPositionX() {
		return this.x;
	}

	public byte getPositionY() {
		return this.y;
	}

	public String getScript() {
		return this.script;
	}

	public short getTriggerID() {
		return this.triggerID;
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

	public boolean hasValidPosition() {
		return !(this.x == -1 || this.y == -1);
	}

	public boolean isEraser() {
		// An Eraser trigger ID is when the summation of all trigger IDs is zero.
		return (this.triggerID + this.npcTriggerID + this.eventTriggerID) == 0;
	}

	public boolean isEventTrigger() {
		return this.isEventTrigger;
	}

	public boolean isNpcTrigger() {
		return this.isNpcTrigger;
	}

	public boolean isPositionXSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_PositionX];
	}

	public boolean isPositionYSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_PositionY];
	}

	public boolean isTriggerIDSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_TriggerID] ||
			this.valuesHasBeenSet[Trigger.FLAG_NpcTriggerID] ||
			this.valuesHasBeenSet[Trigger.FLAG_EventTriggerID];
	}

	public boolean isTriggerScriptSet() {
		return this.valuesHasBeenSet[Trigger.FLAG_TriggerScript];
	}

	public void reset() {
		this.x = this.y = -1;
		this.triggerID = Trigger.ID_NONE;
		this.eventTriggerID = Trigger.ID_NONE;
		this.npcTriggerID = Trigger.ID_NONE;
		this.name = "<Untitled>";
		Arrays.fill(this.valuesHasBeenSet, false);
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public void setEventTrigger(boolean state) {
		this.isEventTrigger = state;
	}

	public void setEventTriggerID(short eventTriggerId) {
		this.eventTriggerID = eventTriggerId;
		this.npcTriggerID = Trigger.ID_NONE;
		if (!this.isEventTrigger && eventTriggerId > Trigger.ID_NONE) {
			this.setEventTrigger(true);
			this.valuesHasBeenSet[Trigger.FLAG_EventTriggerID] = true;
			this.valuesHasBeenSet[Trigger.FLAG_TriggerID] = false;
			this.valuesHasBeenSet[Trigger.FLAG_NpcTriggerID] = false;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNpcTrigger(boolean state) {
		this.isNpcTrigger = state;
	}

	public void setNpcTriggerID(short npcTriggerId) {
		this.npcTriggerID = npcTriggerId;
		this.eventTriggerID = Trigger.ID_NONE;
		if (!this.isNpcTrigger && npcTriggerId > Trigger.ID_NONE) {
			this.setNpcTrigger(true);
			this.valuesHasBeenSet[Trigger.FLAG_NpcTriggerID] = true;
			this.valuesHasBeenSet[Trigger.FLAG_TriggerID] = false;
			this.valuesHasBeenSet[Trigger.FLAG_EventTriggerID] = false;
		}
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setTriggerID(short value) {
		// No need to set whether it's a trigger.
		this.triggerID = value;
		this.npcTriggerID = Trigger.ID_NONE;
		this.eventTriggerID = Trigger.ID_NONE;
		this.valuesHasBeenSet[Trigger.FLAG_TriggerID] = true;
		this.valuesHasBeenSet[Trigger.FLAG_NpcTriggerID] = false;
		this.valuesHasBeenSet[Trigger.FLAG_EventTriggerID] = false;
	}

	public void setTriggerPositionX(byte x) {
		this.valuesHasBeenSet[Trigger.FLAG_PositionX] = true;
		this.x = x;
	}

	public void setTriggerPositionY(byte y) {
		this.valuesHasBeenSet[Trigger.FLAG_PositionY] = true;
		this.y = y;
	}
}
