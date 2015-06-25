package script;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Map;

import level.Area;
import level.WorldConstants;
import screen.BaseScreen;
import dialogue.NewDialogue;
import entity.Player;

public class TriggerData {
	public int x, y;
	public Script script;
	public int iteration;
	private boolean finished;
	private boolean repeat;
	
	private Movement moves;
	private NewDialogue dialogue;
	
	// TODO: Add entity ID for NPCs.
	
	// TriggerData is for the game. Area uses TriggerData to communicate with
	// Scripts to obtain Movements.
	
	public TriggerData() {
		x = y = 0;
		script = null;
		finished = false;
	}
	
	public TriggerData(TriggerData t) {
		this.x = t.x;
		this.y = t.y;
		this.script = new Script(t.script);
		this.finished = t.finished;
	}

	public TriggerData loadTriggerData(int pixel) {
		this.x = (pixel >> 24) & 0xFF;
		this.y = (pixel >> 16) & 0xFF;
		if (this.finished)
			this.finished = false;
		if (WorldConstants.scripts.isEmpty())
			System.out.println("Scripts are empty");
		for (Script s : WorldConstants.scripts) {
			if (s.triggerID == (pixel & 0xFFFF)) {
				this.script = s;
				if (s.repeat)
					this.setRepeating();
				break;
			}
		}
		return this;
	}
	
	public void tick(Area area, int entityX, int entityY) {
		// TODO: Continue from here.
		if (this.script != null) {
			
			moves = this.script.getIteratedMoves();
			dialogue = this.script.getIteratedDialogues();
			
			if (moves != null && dialogue == null) {
				area.getPlayer().keys.resetInputs();
				if (area.getPlayer().isLockedWalking())
					return;
				ArrayList<Map.Entry<Integer, Integer>> list = moves.getAllMoves();
				if (iteration < list.size()) {
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
						entry.setValue(steps);
					}
					else {
						list.remove(entry);
						if (list.isEmpty()) {
							moves = null;
							try {
								if (!this.script.incrementIteration())
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
			else if (moves == null && dialogue != null) {
				switch (dialogue.getDialogueType()) {
					case NewDialogue.DIALOGUE_SPEECH:
						if (dialogue.isDialogueCompleted() && dialogue.isScrolling()) {
							Player.unlockMovements();
							// dialogue.resetDialogue();
							this.dialogue = null;
							try {
								this.finished = !this.script.incrementIteration();
							}
							catch (Exception e) {
								this.finished = true;
								return;
							}
						}
						else if (dialogue.isDialogueCompleted() && !dialogue.isScrolling()) {
							if (!dialogue.isShowingDialog()) {
								Player.unlockMovements();
								// dialogue.resetDialogue();
								this.dialogue = null;
								try {
									this.finished = !this.script.incrementIteration();
								}
								catch (Exception e) {
									this.finished = true;
									return;
								}
							}
							else
								dialogue.tick();
						}
						else if (dialogue.isDialogueTextSet() && !(dialogue.isDialogueCompleted() && dialogue.isShowingDialog())) {
							Player.lockMovements();
							dialogue.tick();
						}
						break;
					case NewDialogue.DIALOGUE_QUESTION:
						if (!dialogue.yesNoQuestionHasBeenAnswered()) {
							dialogue.tick();
							if (!Player.isMovementsLocked())
								Player.lockMovements();
							area.getPlayer().disableAutomaticMode();
						}
						if (dialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
							if (Player.isMovementsLocked())
								Player.unlockMovements();
							area.getPlayer().enableAutomaticMode();
							this.dialogue = null;
							try {
								this.finished = !this.script.incrementIteration();
							}
							catch (Exception e) {
								this.finished = true;
								return;
							}
							this.script.setAffirmativeFlag();
							this.finished = false;
						}
						else if (dialogue.getAnswerToSimpleQuestion() == Boolean.FALSE) {
							if (Player.isMovementsLocked())
								Player.unlockMovements();
							area.getPlayer().enableAutomaticMode();
							this.dialogue = null;
							try {
								this.finished = !this.script.incrementIteration();
							}
							catch (Exception e) {
								this.finished = true;
								return;
							}
							this.script.setNegativeFlag();
							this.finished = false;
						}
						break;
				}
			}
			
		}
	}
	
	public void setRepeating(){
		this.repeat = true;
	}
	
	public void stopRepeating(){
		this.repeat = false;
	}
	
	public boolean isOnRepeat(){
		return this.repeat;
	}
	
	public TriggerData reset(){
		this.finished = false;
		return this;
	}
	
	public void render(BaseScreen screen, Graphics2D graphics) {
		if (this.dialogue != null) {
			this.dialogue.render(screen, graphics);
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void turnOffTrigger(){
		this.finished = true;
	}
	
	public void turnOnTrigger(){
		this.finished = false;
	}
}