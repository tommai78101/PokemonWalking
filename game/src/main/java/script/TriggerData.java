package script;

import java.awt.Graphics2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

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
	public int x, y;
	public int iteration;

	private Deque<Script> scripts;
	private Deque<Script> finishedScripts;
	private Script currentScript;
	private int debugIteration = 51;
	private boolean isPaused = false;

	// TODO: Add entity ID for NPCs.

	// TriggerData is for the game. Area uses TriggerData to communicate with
	// Scripts to obtain Movements.

	public TriggerData() {
		this.x = this.y = 0;
		this.currentScript = null;
		this.scripts = new LinkedList<>();
		this.finishedScripts = new LinkedList<>();
		this.isPaused = false;
	}

	public TriggerData(TriggerData t) {
		this.x = t.x;
		this.y = t.y;
		this.currentScript = new Script(t.currentScript);
		this.scripts = new LinkedList<>(t.scripts);
		this.finishedScripts = new LinkedList<>(t.finishedScripts);
		this.isPaused = t.isPaused;
	}

	public TriggerData loadTriggerData(int pixel) {
		this.x = (pixel >> 24) & 0xFF;
		this.y = (pixel >> 16) & 0xFF;
		Set<Script> scriptList = (WorldConstants.isModsEnabled.booleanValue() ? WorldConstants.moddedScripts
		    : WorldConstants.scripts).parallelStream().collect(Collectors.toSet());
		this.scripts.addAll(scriptList);
		return this;
	}

	public boolean hasScripts() {
		return !this.scripts.isEmpty();
	}

	public boolean hasActiveScript() {
		if (this.currentScript == null) {
			if (!this.scripts.isEmpty() && !this.isPaused) {
				this.currentScript = this.scripts.pop();
			}
		}
		return this.currentScript != null;
	}

	public void prepareActiveScript() {
		if (this.currentScript != null) {
			if (!this.currentScript.isScriptEnabled() && this.currentScript.hasReset()) {
				this.currentScript.turnOnScript();
				this.currentScript.clearReset();
			}
		}
	}

	public void setPaused(boolean value) {
		this.isPaused = value;
	}

	public boolean isPaused() {
		return this.isPaused;
	}

	public void tick(Area area, int entityX, int entityY) {
		// No else condition. We check if the current script is no longer null.
		if (this.currentScript != null) {
			// The current script exists. We do actions to this script.
			if (this.currentScript.isScriptEnabled()) {
				if (!this.currentScript.isFinished()) {
					this.currentScript.tick(area, entityX, entityY);
				}
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

	public void render(Scene screen, Graphics2D graphics) {
		if (this.currentScript != null) {
			this.currentScript.render(screen, graphics);
		}
	}
}