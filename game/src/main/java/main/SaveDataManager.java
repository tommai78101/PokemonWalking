package main;

import java.awt.Graphics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import abstracts.SubMenu;
import dialogue.Dialogue;
import entity.Player;
import level.WorldConstants;
import screen.Scene;
import utility.DialogueBuilder;

public class SaveDataManager extends SubMenu {
	public enum State {
		ASK,
		OVERWRITE,
		SAVING,
		SAVED,
		ERROR
	}

	private State state;
	private Dialogue newDialogue;
	ExecutorService executor;
	private Game game;

	public SaveDataManager(Game game) {
		super(WorldConstants.MENU_ITEM_NAME_SAVE, WorldConstants.MENU_ITEM_DESC_INVENTORY, SubMenu.Type.SAVE);
		this.game = game;
		this.state = State.ASK;
		this.newDialogue = new Dialogue();
	}

	@Override
	public void tick() {
		if (!Player.isMovementsLocked())
			Player.lockMovements();
		switch (this.state) {
			case ASK: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = DialogueBuilder.createText(
						"Do you want to save the game?",
						Dialogue.MAX_STRING_LENGTH, Dialogue.Type.DIALOGUE_QUESTION, true
					);
				if (this.newDialogue.isDialogueTextSet()
					&& !(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog())) {
					this.newDialogue.tick();
				}
				else {
					if (this.newDialogue.getDialogueType() == Dialogue.Type.DIALOGUE_QUESTION) {
						if (!this.newDialogue.yesNoQuestionHasBeenAnswered())
							this.newDialogue.tick();
						if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
							if (this.game.checkSaveData())
								this.state = State.OVERWRITE;
							else
								this.state = State.SAVING;
							this.newDialogue.clearDialogueLines();
						}
					}
				}
				break;
			}
			case OVERWRITE: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = DialogueBuilder.createText(
						"There is already an old save file.",
						Dialogue.MAX_STRING_LENGTH, Dialogue.Type.DIALOGUE_SPEECH, true
					);
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog())) {
					this.newDialogue.tick();
				}
				else {
					switch (this.newDialogue.getDialogueType()) {
						case DIALOGUE_SPEECH:
							this.newDialogue = DialogueBuilder.createText(
								"Do you want to overwrite it?",
								Dialogue.MAX_STRING_LENGTH, Dialogue.Type.DIALOGUE_QUESTION, true
							);
							break;
						case DIALOGUE_QUESTION:
							if (!this.newDialogue.yesNoQuestionHasBeenAnswered())
								this.newDialogue.tick();
							if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
								this.state = State.SAVING;
								this.newDialogue.clearDialogueLines();
							}
							break;
						case DIALOGUE_ALERT:
						default:
							break;
					}
				}
				break;
			}
			case SAVING: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = DialogueBuilder.createText(
						"Saving...", Dialogue.MAX_STRING_LENGTH,
						Dialogue.Type.DIALOGUE_SPEECH, true
					);
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog())) {
					this.newDialogue.tick();
					if (this.executor == null) {
						this.executor = Executors.newFixedThreadPool(1);
						this.executor.execute(new Runnable() {
							@Override
							public void run() {
								SaveDataManager.this.game.save();
							}
						});
						this.executor.shutdown();
						try {
							if (!this.executor.awaitTermination(1, TimeUnit.MINUTES)) {
								this.state = State.ERROR;
								this.newDialogue.clearDialogueLines();
								break;
							}
						}
						catch (InterruptedException e) {
							this.state = State.ERROR;
							this.newDialogue.clearDialogueLines();
						}
					}
				}
				else {
					if (this.executor.isTerminated()) {
						this.executor = null;
						this.state = State.SAVED;
						this.newDialogue.clearDialogueLines();
					}
				}
				break;
			}
			case SAVED: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = DialogueBuilder.createText(
						"Saving Complete.", Dialogue.MAX_STRING_LENGTH,
						Dialogue.Type.DIALOGUE_SPEECH, true
					);
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog()))
					this.newDialogue.tick();
				break;
			}
			case ERROR: {
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog()))
					this.newDialogue = DialogueBuilder.createText(
						"I am ERROR.", Dialogue.MAX_STRING_LENGTH,
						Dialogue.Type.DIALOGUE_SPEECH, true
					);
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog()))
					this.newDialogue.tick();
				break;
			}
		}
	}

	public State getState() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public void render(Scene output, Graphics graphics) {
		if (this.newDialogue != null)
			this.newDialogue.render(output, graphics);
	}
}
