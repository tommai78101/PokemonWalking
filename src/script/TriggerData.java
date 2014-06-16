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
	
	public TriggerData loadTriggerData(int pixel) {
		this.x = (pixel >> 24) & 0xFF;
		this.y = (pixel >> 16) & 0xFF;
		if (WorldConstants.scripts.isEmpty())
			System.out.println("Scripts are empty");
		for (Script s : WorldConstants.scripts) {
			if (s.triggerID == (pixel & 0xFFFF)) {
				this.script = s;
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
				ArrayList<Map.Entry<Integer, Integer>> list = moves.getAllMoves();
				if (iteration < list.size()) {
					Map.Entry<Integer, Integer> entry = list.get(0);
					int steps = entry.getValue();
					if (steps >= 0) {
						area.getPlayer().setFacing(entry.getKey());
						if (steps > 0)
							area.getPlayer().forceLockWalking();
						steps--;
						entry.setValue(steps);
					}
					else {
						list.remove(entry);
						if (list.isEmpty()) {
							moves = null;
							if (!this.script.incrementIteration())
								this.finished = true;
						}
					}
				}
			}
			else if (moves == null && dialogue != null) {
				switch (dialogue.getDialogueType()) {
					case NewDialogue.DIALOGUE_SPEECH:
						if (dialogue.isDialogueCompleted() && dialogue.isScrolling()) {
							Player.unlockMovements();
							//dialogue.resetDialogue();
							this.dialogue = null;
							if (!this.script.incrementIteration())
								this.finished = true;
						}
						else if (dialogue.isDialogueCompleted() && !dialogue.isScrolling()) {
							if (!dialogue.isShowingDialog()){
								Player.unlockMovements();
								//dialogue.resetDialogue();
								this.dialogue = null;
								if (!this.script.incrementIteration())
									this.finished = true;
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
						}
						if (dialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
							//dialogue.resetDialogue();
							Player.unlockMovements();
							this.dialogue = null;
							if (!this.script.incrementIteration())
								this.finished = true;
						}
						else if (dialogue.getAnswerToSimpleQuestion() == Boolean.FALSE) {
							//dialogue.resetDialogue();
							Player.unlockMovements();
							this.dialogue = null;
							if (!this.script.incrementIteration())
								this.finished = true;
						}
						break;
				}
			}
			
		}
	}
	
	public void render(BaseScreen screen, Graphics2D graphics){
		if (this.dialogue != null){
			this.dialogue.render(screen, graphics);
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
}
