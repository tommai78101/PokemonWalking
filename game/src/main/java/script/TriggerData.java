package script;

import java.awt.Graphics2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import editor.Trigger;
import level.Area;
import level.WorldConstants;
import screen.Scene;

/**
 * TriggerData refers to a tile in the Area where one or many {@linkplain Script scripts} can occur
 * throughout the entire game. Depending on the script conditions necessary to trigger the "events"
 * on the same tile, the script will trigger when the player lands on the TriggerData where the
 * script is located.
 * <p>
 * TriggerData stores all possible {@linkplain Script scripts} that occurs on the same exact tile
 * location. Each script should be unique from one another.
 *
 * @author tlee
 */
public class TriggerData {
	private int x;
	private int y;
	private short triggerID;
	private short npcTriggerID;
	private Deque<Script> scripts;
	private Deque<Script> finishedScripts;
	private Script currentScript;
	private boolean isPaused = false;

	// TODO: Add entity ID for NPCs.

	/**
	 * Constructor of the TriggerData object, which stores anything related to triggers placed on the
	 * tile.
	 * <p>
	 * TriggerData is for use in the Game. Area uses TriggerData to communicate with Scripts to obtain
	 * Movements.
	 */
	public TriggerData() {
		this.x = this.y = 0;
		this.triggerID = 0;
		this.npcTriggerID = 0;
		this.currentScript = null;
		this.scripts = new LinkedList<>();
		this.finishedScripts = new LinkedList<>();
		this.isPaused = false;
	}

	/**
	 * Deep copy of the TriggerData object.
	 *
	 * @param t
	 */
	public TriggerData(TriggerData t) {
		this.x = t.x;
		this.y = t.y;
		this.triggerID = t.triggerID;
		this.npcTriggerID = t.npcTriggerID;
		this.currentScript = new Script(t.currentScript);
		this.scripts = new LinkedList<>(t.scripts);
		this.finishedScripts = new LinkedList<>(t.finishedScripts);
		this.isPaused = t.isPaused;
	}

	/**
	 * Initializes the trigger tile data with the information from the pixel color value.
	 * <p>
	 * TODO: Currently, it initializes the trigger tile data's current area position from the pixel
	 * color value. This will need to be updated, so that we can store the position of the trigger tile
	 * data, and match up the trigger data identification code with the right script file.
	 *
	 * @param triggerInfo
	 * @return
	 */
	public TriggerData loadTriggerData(int triggerInfo, int npcInfo) {
		this.x = (triggerInfo >> 24) & 0xFF;
		this.y = (triggerInfo >> 16) & 0xFF;
		this.triggerID = (short) (triggerInfo & 0xFFFF);
		this.npcTriggerID = (short) (npcInfo & 0xFFFF);
		Set<Script> scriptList = (WorldConstants.isModsEnabled.booleanValue() ? WorldConstants.moddedScripts
			: WorldConstants.scripts).parallelStream().collect(Collectors.toSet());
		this.scripts.addAll(scriptList);
		return this;
	}

	/**
	 * Checks if the trigger tile contains available scripts.
	 *
	 * @return
	 */
	public boolean hasScripts() {
		return !this.scripts.isEmpty();
	}

	public Deque<Script> getScripts() {
		return this.scripts;
	}

	public Deque<Script> getFinishedScripts() {
		return this.finishedScripts;
	}

	/**
	 * Checks if the trigger tile has an active script.
	 *
	 * @return
	 */
	public boolean hasActiveScript(Area area) {
		if ((this.currentScript == null) && (!this.scripts.isEmpty() && !this.isPaused)) {
			for (Script s : this.scripts) {
				if (s.isMatchingArea(area)) {
					this.currentScript = s;
					break;
				}
			}
		}
		return this.currentScript != null;
	}

	/**
	 * Prepares the trigger tile's current active script from a collection of scripts that may occur on
	 * the same tile.
	 */
	public void prepareActiveScript() {
		if (this.currentScript != null) {
			if (!this.currentScript.isScriptEnabled() && this.currentScript.hasReset()) {
				this.currentScript.turnOnScript();
				this.currentScript.clearReset();
			}
			else if (this.currentScript.hasReset()) {
				this.currentScript.clearReset();
			}
		}
	}

	/**
	 * Resets the current script.
	 */
	public void resetCurrentScript(Area area) {
		this.setPaused(false);
		if (this.currentScript == null && this.hasActiveScript(area)) {
			this.currentScript.reset();
		}
	}

	public boolean hasFinishedInteracting() {
		if (this.currentScript == null || this.currentScript.hasReset()) {
			// Meaning this entity has finished interacting with the player.
			return true;
		}
		return this.currentScript.isFinished();
	}

	/**
	 * Sets the trigger tile to be paused or unpaused, determined by the boolean argument.
	 * <p>
	 * Need to unpause old trigger data if the trigger data was previously paused and the player has
	 * left the trigger tile, or has ended interacting with an entity that holds a trigger script.
	 *
	 * @param value
	 */
	public void setPaused(boolean value) {
		this.isPaused = value;
	}

	/**
	 * Checks if the trigger tile is paused.
	 *
	 * @return
	 */
	public boolean isPaused() {
		return this.isPaused;
	}

	/**
	 * Returns the NPC trigger ID, which is used to load custom scripts and dialogues into the
	 * respective NPCs.
	 *
	 * @return
	 */
	public short getNpcTriggerID() {
		return this.npcTriggerID;
	}

	/**
	 * Returns the Trigger ID, which is used to load custom scripts and dialogues into the respective
	 * non-Character entities.
	 *
	 * @return
	 */
	public short getTriggerID() {
		return this.triggerID;
	}

	/**
	 * Checks whether the trigger data is an NPC trigger data.
	 *
	 * @return
	 */
	public boolean isNpcTrigger() {
		return this.npcTriggerID != Trigger.NPC_TRIGGER_ID_NONE;
	}

	/**
	 * Returns the X position relative to the Area's origin.
	 *
	 * @return
	 */
	public int getXAreaPosition() {
		return this.x;
	}

	/**
	 * Returns the Y position relative to the Area's origin.
	 *
	 * @return
	 */
	public int getYAreaPosition() {
		return this.y;
	}

	/**
	 * Updates the triggered script.
	 *
	 * @param area
	 * @param entityX
	 * @param entityY
	 */
	public void tick(Area area) {
		// No else condition. We check if the current script is no longer null.
		if (this.currentScript != null) {
			// The current script exists. We do actions to this script.
			if (this.currentScript.isScriptEnabled() && !this.currentScript.isFinished()) {
				this.currentScript.tick(area);
			}
			if (this.currentScript.isFinished()) {
				if (this.currentScript.isOnRepeat()) {
					this.currentScript.turnOffScript();
					this.currentScript.reset();
					// Add only the non-null script to the back of the queue.
					this.scripts.add(this.currentScript);
				}
				else {
					this.currentScript.turnOffScript();
					// Since the script is not repeatable, we should pop the current script.
					this.finishedScripts.add(this.currentScript);
				}
				this.setPaused(true);
				this.currentScript = null;
			}
		}
	}

	/**
	 * Renders the triggered script scenes.
	 *
	 * @param screen
	 * @param graphics
	 */
	public void render(Scene screen, Graphics2D graphics) {
		if (this.currentScript != null) {
			this.currentScript.render(screen, graphics);
		}
	}
}