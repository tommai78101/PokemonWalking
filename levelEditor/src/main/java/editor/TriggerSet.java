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
		return this.triggers.size();
	}

	/**
	 * Validate and check for triggers.
	 *
	 * @param triggerPixelData
	 *            The full RGBA pixel data of the supposed trigger in the area map.
	 * @return
	 */
	public Trigger validityCheck(int colorPixelData, short triggerId) {
		Trigger triggerValidityCheck = null;
		List<Trigger> triggers = EditorConstants.getInstance().getTriggers();
		if (colorPixelData == 0x0) {
			return triggers.get(0);
		}
		final short trigId = triggerId;
		try {
			for (Trigger t : triggers) {
				if (t.checkTriggerID(trigId)) {
					triggerValidityCheck = t;
					break;
				}
			}
		}
		catch (Exception e) {
			Debug.error("Encountered an error related to validating the trigger ID: " + triggerId, e);
			// Eraser.
			triggerValidityCheck = triggers.get(0);
		}
		return triggerValidityCheck;
	}

	public Set<Trigger> getTriggers(int currentTriggerIndex) {
		return this.triggers.get(currentTriggerIndex);
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
			if (t.getTriggerID() == trigger.getTriggerID()) {
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

		Set<Integer> keySet = this.triggers.keySet();
		for (int tileId : keySet) {
			Set<Trigger> triggerSet = this.triggers.get(tileId);
			if (triggerSet == null || triggerSet.isEmpty()) {
				continue;
			}
			for (Trigger trigger : triggerSet) {
				output.add(trigger.getDataValue());
				output.add(trigger.getNpcDataValue());
			}
		}
		return output;
	}

	public void addTriggerById(int index, int pixelColor, short triggerId, short npcTriggerId) {
		Trigger trigger = this.validityCheck(pixelColor, triggerId);
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
			if (t.getTriggerID() == trigger.getTriggerID())
				return true;
		}
		return false;
	}

	public boolean hasTriggers(int index) {
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
