package main;

import java.awt.Graphics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import abstracts.SubMenu;
import data.GameSave;
import dialogue.Dialogue;
import entity.Player;
import level.WorldConstants;
import main.StateManager.GameState;
import screen.Scene;
import utility.DialogueBuilder;

public class SaveDataManager extends SubMenu {
	public static final String SAVE_FILE_NAME = "data.sav";

	public enum SaveStatus {
		ASK,
		OVERWRITE,
		SAVING,
		SAVED,
		SAVE_COMPLETE,
		ERROR
	}

	private SaveStatus saveStatus;
	private Dialogue newDialogue;
	ExecutorService executor;
	private Game game;

	public SaveDataManager(Game game) {
		super(WorldConstants.MENU_ITEM_NAME_SAVE, WorldConstants.MENU_ITEM_DESC_SAVE, GameState.SAVE);
		this.game = game;
		this.saveStatus = SaveStatus.ASK;
		this.newDialogue = new Dialogue();
		this.needsFlashingAnimation = false;
		this.exitsToGame = true;
	}

	@Override
	public void tick() {
		if (!Player.isMovementsLocked())
			Player.lockMovements();
		switch (this.saveStatus) {
			case ASK: {
				if (!this.newDialogue.isDialogueTextSet()) {
					this.newDialogue = DialogueBuilder.createText(
						"Do you want to save the game?",
						Dialogue.MAX_STRING_LENGTH, Dialogue.Type.DIALOGUE_QUESTION, true
					);
				}
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog())) {
					this.newDialogue.tick();
				}
				else {
					if (this.newDialogue.getDialogueType() == Dialogue.Type.DIALOGUE_QUESTION) {
						if (!this.newDialogue.yesNoQuestionHasBeenAnswered())
							this.newDialogue.tick();
						if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
							if (GameSave.check(SaveDataManager.SAVE_FILE_NAME))
								this.saveStatus = SaveStatus.OVERWRITE;
							else
								this.saveStatus = SaveStatus.SAVING;
							this.newDialogue.clearDialogueLines();
						}
						else if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.FALSE) {
							//Save operation has been cancelled.
							this.saveStatus = SaveStatus.SAVE_COMPLETE;
							this.newDialogue.clearDialogueLines();
						}
						else {
							//Intentionally doing nothing.
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
								this.saveStatus = SaveStatus.SAVING;
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
						Dialogue.Type.DIALOGUE_SPEECH, true, true
					);
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog())) {
					this.newDialogue.tick();
				}
				else if (this.executor == null) {
					this.executor = Executors.newFixedThreadPool(1);
					this.executor.execute(new Runnable() {
						@Override
						public void run() {
							GameSave.save(SaveDataManager.this.game, SaveDataManager.SAVE_FILE_NAME);
						}
					});
					this.executor.shutdown();
					try {
						if (!this.executor.awaitTermination(1, TimeUnit.MINUTES)) {
							this.saveStatus = SaveStatus.ERROR;
							this.newDialogue.clearDialogueLines();
							break;
						}
					}
					catch (InterruptedException e) {
						this.saveStatus = SaveStatus.ERROR;
						this.newDialogue.clearDialogueLines();
					}
				}
				else {
					if (this.executor.isTerminated()) {
						this.executor = null;
						this.saveStatus = SaveStatus.SAVED;
						this.newDialogue.clearDialogueLines();
					}
				}
				break;
			}
			case SAVED: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = DialogueBuilder.createText(
						"Saving Complete.", Dialogue.MAX_STRING_LENGTH,
						Dialogue.Type.DIALOGUE_SPEECH, true, true
					);
				if (!(this.newDialogue.isDialogueCompleted() && this.newDialogue.isShowingDialog()))
					this.newDialogue.tick();
				else {
					this.saveStatus = SaveStatus.SAVE_COMPLETE;
					this.newDialogue.clearDialogueLines();
				}
				break;
			}
			case SAVE_COMPLETE: {
				this.exitsToGame();
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
				else {
					this.exit();
				}
				break;
			}
		}
	}

	public SaveStatus getSaveStatus() {
		return this.saveStatus;
	}

	public void resetSaveStatus() {
		this.saveStatus = SaveStatus.ASK;
	}

	@Override
	public void render(Scene output, Graphics graphics) {
		if (this.newDialogue != null)
			this.newDialogue.render(output, graphics);
	}
}
