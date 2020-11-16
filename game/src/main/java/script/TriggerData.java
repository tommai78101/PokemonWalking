package script;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dialogue.Dialogue;
import entity.Player;
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

	private Set<Script> scripts;
	private Script currentScript;
	private boolean finished;
	private boolean repeat;
	private MovementData moves;
	private Dialogue dialogue;

	// TODO: Add entity ID for NPCs.

	// TriggerData is for the game. Area uses TriggerData to communicate with
	// Scripts to obtain Movements.

	public TriggerData() {
		this.x = this.y = 0;
		this.currentScript = null;
		this.scripts = new HashSet<>();
		this.finished = false;
	}

	public TriggerData(TriggerData t) {
		this.x = t.x;
		this.y = t.y;
		this.currentScript = new Script(t.currentScript);
		this.scripts = new HashSet<>(t.scripts);
		this.finished = t.finished;
	}

	public TriggerData loadTriggerData(int pixel) {
		this.x = (pixel >> 24) & 0xFF;
		this.y = (pixel >> 16) & 0xFF;
		if (this.finished)
			this.finished = false;
		List<Script> scriptList = (WorldConstants.isModsEnabled.booleanValue() ? WorldConstants.moddedScripts
		    : WorldConstants.scripts);
		for (Script s : scriptList) {
			if (s.triggerID == (pixel & 0xFFFF)) {
				this.currentScript = s;
				if (s.repeat)
					this.setRepeating();
				break;
			}
		}
		return this;
	}

	public void tick(Area area, int entityX, int entityY) {
		if (this.currentScript != null) {
			this.moves = this.currentScript.getIteratedMoves();
			this.dialogue = this.currentScript.getIteratedDialogues();

			if (this.moves != null && this.dialogue == null) {
				area.getPlayer().keys.resetInputs();
				if (area.getPlayer().isLockedWalking())
					return;
				ArrayList<Map.Entry<Integer, Integer>> list = this.moves.getAllMoves();
				if (this.iteration < list.size()) {
					Map.Entry<Integer, Integer> entry = list.get(0);
					if (entry.getKey() != area.getPlayer().getFacing()) {
						area.getPlayer().setFacing(entry.getKey());
						return;
					}
					int steps = entry.getValue();
					if (steps >= 0) {
						if (steps == 0)
							area.getPlayer().setFacing(entry.getKey());
						else
							area.getPlayer().forceLockWalking();
						steps--;

						// Replace the Map.Entry with a new Map.Entry that contains the updated values.
						// Map.Entry is actually immutable, so we cannot use an unsupported operation of setting new values.
						Map.Entry<Integer, Integer> newEntry = Map.entry(entry.getKey(), steps);
						list.set(this.iteration, newEntry);
					}
					else {
						list.remove(entry);
						if (list.isEmpty()) {
							this.moves = null;
							try {
								if (!this.currentScript.incrementIteration())
									this.finished = true;
							}
							catch (Exception e) {
								this.finished = true;
								return;
							}
						}
						else {
							entry = list.get(0);
							if (entry.getKey() != area.getPlayer().getFacing())
								area.getPlayer().setFacing(entry.getKey());
						}
					}
				}
			}
			else if (this.moves == null && this.dialogue != null) {
				switch (this.dialogue.getDialogueType()) {
					case SPEECH:
						if (this.dialogue.isDialogueCompleted()) {
							if (this.dialogue.isScrolling()) {
								Player.unlockMovements();
								this.dialogue.tick();
								try {
									this.finished = !this.currentScript.incrementIteration();
								}
								catch (Exception e) {
									this.finished = true;
									return;
								}
							}
							else {
								if (!this.dialogue.isShowingDialog()) {
									Player.unlockMovements();
									this.dialogue = null;
									try {
										this.finished = !this.currentScript.incrementIteration();
									}
									catch (Exception e) {
										this.finished = true;
										return;
									}
								}
								else
									this.dialogue.tick();
							}
						}
						else if (this.dialogue.isReady()
						    && !(this.dialogue.isDialogueCompleted() && this.dialogue.isShowingDialog())) {
							Player.lockMovements();
							this.dialogue.tick();
						}
						break;
					case QUESTION:
						if (!this.dialogue.yesNoQuestionHasBeenAnswered()) {
							this.dialogue.tick();
							if (!Player.isMovementsLocked())
								Player.lockMovements();
							area.getPlayer().disableAutomaticMode();
						}
						if (this.dialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
							if (Player.isMovementsLocked())
								Player.unlockMovements();
							area.getPlayer().enableAutomaticMode();
							this.dialogue = null;
							try {
								this.finished = !this.currentScript.incrementIteration();
							}
							catch (Exception e) {
								this.finished = true;
								return;
							}
							this.currentScript.setAffirmativeFlag();
							this.finished = false;
						}
						else if (this.dialogue.getAnswerToSimpleQuestion() == Boolean.FALSE) {
							if (Player.isMovementsLocked())
								Player.unlockMovements();
							area.getPlayer().enableAutomaticMode();
							this.dialogue = null;
							try {
								this.finished = !this.currentScript.incrementIteration();
							}
							catch (Exception e) {
								this.finished = true;
								return;
							}
							this.currentScript.setNegativeFlag();
							this.finished = false;
						}
						break;
					case ALERT:
					default:
						break;
				}
			}

		}
	}

	public void setRepeating() {
		this.repeat = true;
	}

	public void stopRepeating() {
		this.repeat = false;
	}

	public boolean isOnRepeat() {
		return this.repeat;
	}

	public TriggerData reset() {
		this.finished = false;
		return this;
	}

	public void render(Scene screen, Graphics2D graphics) {
		if (this.dialogue != null) {
			this.dialogue.render(screen, graphics);
		}
	}

	public boolean isFinished() {
		return this.finished;
	}

	public void turnOffTrigger() {
		this.finished = true;
	}

	public void turnOnTrigger() {
		this.finished = false;
	}
}