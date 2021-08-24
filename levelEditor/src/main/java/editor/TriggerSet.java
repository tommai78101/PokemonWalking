package editor;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.Debug;

public class TriggerSet {
	// Map of <Tile ID (current tile index), Set of triggers>
	private Map<Integer, Set<Trigger>> triggers;
	// Since this is part of Javax Swing, I might as well use it to store the width and height of the
	// bitmap.
	private Dimension size;
	// This checksum keeps track of the area checksum as well as the trigger scripts.
	private String checksum;

	public TriggerSet(int width, int height, String checksum) {
		this.size = new Dimension(width, height);
		this.triggers = new HashMap<>();
		this.checksum = checksum;
	}

	public int getSize() {
		int result = 0;
		for (var entry : this.triggers.entrySet()) {
			for (var trigger : entry.getValue()) {
				if (!trigger.isEraser())
					result++;
			}
		}
		return result;
	}

	/**
	 * Validate and check for triggers.
	 *
	 * @param triggerPixelData
	 *            The full RGBA pixel data of the supposed trigger in the area map.
	 * @return
	 */
	public Trigger validityCheck(final int colorPixelData, final short triggerId, final short npcTriggerId) {
		Trigger triggerValidityCheck = null;
		List<Trigger> triggers = EditorConstants.getInstance().getTriggers();
		if (colorPixelData == 0x0) {
			return triggers.get(0);
		}
		try {
			for (Trigger t : triggers) {
				if (!t.isNpcTrigger() && t.checkTriggerID(triggerId) || (t.isNpcTrigger() && t.checkNpcTriggerID(npcTriggerId))) {
					triggerValidityCheck = t;
					break;
				}
			}
		}
		catch (Exception e) {
			Debug.error("Encountered error while validating the trigger ID: " + triggerId + ", npc trigger ID: " + npcTriggerId, e);
			// Eraser.
			triggerValidityCheck = triggers.get(0);
		}
		return triggerValidityCheck;
	}

	public Set<Trigger> getTriggers(int currentTriggerIndex) {
		return this.triggers.get(currentTriggerIndex);
	}

	public Map<Integer, Set<Trigger>> getAllTriggers() {
		return this.triggers;
	}

	public void addTrigger(int index, byte newX, byte newY, Trigger trigger) {
		Set<Trigger> set = this.triggers.get(index);
		if (set == null)
			set = new HashSet<>();
		Trigger modifiedTrigger = new Trigger();
		modifiedTrigger.setChecksum(trigger.getChecksum());
		modifiedTrigger.setTriggerPositionX(newX);
		modifiedTrigger.setTriggerPositionY(newY);
		modifiedTrigger.setTriggerID(trigger.getTriggerID());
		modifiedTrigger.setNpcTriggerID(trigger.getNpcTriggerID());
		set.add(modifiedTrigger);
		this.triggers.put(index, set);
	}

	public void removeTrigger(int index, Trigger trigger) {
		Set<Trigger> set = this.triggers.get(index);
		if (set == null) {
			return;
		}
		Trigger delete = null;
		for (Trigger t : set) {
			if (t.getTriggerID() == trigger.getTriggerID() || t.getNpcTriggerID() == trigger.getNpcTriggerID()) {
				delete = t;
				break;
			}
		}
		if (delete != null) {
			set.remove(delete);
		}
	}

	public void toggleTrigger(int index, Trigger trigger) {
		Debug.warn("Toggling trigger");
		Set<Trigger> set = this.triggers.get(index);
		if (set == null) {
			set = new HashSet<>();
			set.add(trigger);
			this.triggers.put(index, set);
			return;
		}
		if (set.contains(trigger)) {
			set.remove(trigger);
		}
		else {
			set.add(trigger);
		}
	}

	public List<Integer> convertToData() {
		List<Integer> output = new ArrayList<>();
		// Eraser trigger by default.
		output.add(0); // Eraser Trigger info
		output.add(0); // Eraser NPC trigger info.

		for (var entry : this.triggers.entrySet()) {
			for (Trigger trigger : entry.getValue()) {
				output.add(trigger.getDataValue());
				output.add(trigger.getNpcDataValue());
			}
		}
		return output;
	}

	public void addTriggerById(int index, int pixelColor, short triggerId, short npcTriggerId) {
		Trigger trigger = this.validityCheck(pixelColor, triggerId, npcTriggerId);
		byte x = (byte) (index % this.size.width);
		byte y = (byte) (index / this.size.width);
		if (trigger == null) {
			Debug.error("Unrecognized trigger ID: " + triggerId + " at tile: " + index + " located at [" + x + "," + y + "].");
			return;
		}
		if (!trigger.isEraser()) {
			trigger.setTriggerID(triggerId);
			trigger.setNpcTriggerID(npcTriggerId);
			this.addTrigger(index, x, y, trigger);
		}
		else {
			// Don't do anything with Erasers.
		}
	}

	public void clearAllTriggers(int index) {
		Set<Trigger> set = this.triggers.get(index);
		if (set != null)
			set.clear();
	}

	public boolean contains(int index, Trigger trigger) {
		Set<Trigger> set = this.triggers.get(index);
		if (set == null || set.isEmpty())
			return false;
		for (Trigger t : set) {
			if (t.getTriggerID() == trigger.getTriggerID() || t.getNpcTriggerID() == trigger.getNpcTriggerID())
				return true;
		}
		return false;
	}

	public boolean hasTriggers(int index) {
		if (this.triggers != null && this.triggers.isEmpty())
			return false;
		Set<Trigger> set = this.triggers.get(index);
		return (set != null && !set.isEmpty());
	}

	public boolean isEmpty() {
		return this.getSize() == 0;
	}

	public boolean matchesChecksum(String checksum) {
		if (this.checksum == null || this.checksum.isBlank() || checksum == null || checksum.isBlank())
			return false;
		return this.checksum.equals(checksum);
	}

	public String getChecksum() {
		return this.checksum;
	}
}
