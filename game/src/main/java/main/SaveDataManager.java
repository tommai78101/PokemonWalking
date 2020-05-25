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
	private Dialogue saveDialogue;
	ExecutorService executor;
	private Game game;

	public SaveDataManager(Game game) {
		super(WorldConstants.MENU_ITEM_NAME_SAVE, WorldConstants.MENU_ITEM_DESC_SAVE, GameState.SAVE);
		this.game = game;
		this.saveStatus = SaveStatus.ASK;
		this.saveDialogue = new Dialogue();
		this.needsFlashingAnimation = false;
		this.exitsToGame = true;
	}

	@Override
	public void tick() {
		if (!Player.isMovementsLocked())
			Player.lockMovements();
		switch (this.saveStatus) {
			case ASK: {
				if (!this.saveDialogue.isReady()) {
					this.saveDialogue = DialogueBuilder.createText(
						"Do you want to save the game?",
						Dialogue.MAX_STRING_LENGTH, Dialogue.DialogueType.QUESTION, true
					);
				}
				if (!(this.saveDialogue.isDialogueCompleted() && this.saveDialogue.isShowingDialog())) {
					this.saveDialogue.tick();
				}
				else {
					if (!this.saveDialogue.yesNoQuestionHasBeenAnswered())
						this.saveDialogue.tick();
					if (this.saveDialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
						if (GameSave.check(SaveDataManager.SAVE_FILE_NAME))
							this.saveStatus = SaveStatus.OVERWRITE;
						else
							this.saveStatus = SaveStatus.SAVING;
						this.saveDialogue.clearDialogueLines();
					}
					else if (this.saveDialogue.getAnswerToSimpleQuestion() == Boolean.FALSE) {
						// Save operation has been cancelled.
						this.saveStatus = SaveStatus.SAVE_COMPLETE;
						this.saveDialogue.clearDialogueLines();
					}
					else {
						// Intentionally doing nothing.
					}
				}
				break;
			}
			case OVERWRITE: {
				if (!this.saveDialogue.isReady())
					this.saveDialogue = DialogueBuilder.createText(
						"There is already an old save file.",
						Dialogue.MAX_STRING_LENGTH, Dialogue.DialogueType.SPEECH, true
					);
				if (!(this.saveDialogue.isDialogueCompleted() && this.saveDialogue.isShowingDialog())) {
					this.saveDialogue.tick();
				}
				else {
					switch (this.saveDialogue.getDialogueType()) {
						case SPEECH:
							this.saveDialogue = DialogueBuilder.createText(
								"Do you want to overwrite it?",
								Dialogue.MAX_STRING_LENGTH, Dialogue.DialogueType.QUESTION, true
							);
							break;
						case QUESTION:
							if (!this.saveDialogue.yesNoQuestionHasBeenAnswered())
								this.saveDialogue.tick();
							if (this.saveDialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
								this.saveStatus = SaveStatus.SAVING;
								this.saveDialogue.clearDialogueLines();
							}
							break;
						case ALERT:
						default:
							break;
					}
				}
				break;
			}
			case SAVING: {
				if (!this.saveDialogue.isReady())
					this.saveDialogue = DialogueBuilder.createText(
						"Saving...", Dialogue.MAX_STRING_LENGTH,
						Dialogue.DialogueType.SPEECH, true, true
					);
				if (!(this.saveDialogue.isDialogueCompleted() && this.saveDialogue.isShowingDialog())) {
					this.saveDialogue.tick();
				}
				else if (this.executor == null) {
					this.executor = Executors.newFixedThreadPool(1);
					this.executor.execute(new Runnable() {
						@Override
						public void run() {
							// GameSave.save(SaveDataManager.this.game, SaveDataManager.SAVE_FILE_NAME);
							GameSave.saveExperimental(SaveDataManager.this.game, SaveDataManager.SAVE_FILE_NAME);
						}
					});
					this.executor.shutdown();
					try {
						if (!this.executor.awaitTermination(1, TimeUnit.MINUTES)) {
							this.saveStatus = SaveStatus.ERROR;
							this.saveDialogue.clearDialogueLines();
							break;
						}
					}
					catch (InterruptedException e) {
						this.saveStatus = SaveStatus.ERROR;
						this.saveDialogue.clearDialogueLines();
					}
				}
				else {
					if (this.executor.isTerminated()) {
						this.executor = null;
						this.saveStatus = SaveStatus.SAVED;
						this.saveDialogue.clearDialogueLines();
					}
				}
				break;
			}
			case SAVED: {
				if (!this.saveDialogue.isReady())
					this.saveDialogue = DialogueBuilder.createText(
						"Saving Complete.", Dialogue.MAX_STRING_LENGTH,
						Dialogue.DialogueType.SPEECH, true, true
					);
				if (!(this.saveDialogue.isDialogueCompleted() && this.saveDialogue.isShowingDialog()))
					this.saveDialogue.tick();
				else {
					this.saveStatus = SaveStatus.SAVE_COMPLETE;
					this.saveDialogue.clearDialogueLines();
				}
				break;
			}
			case SAVE_COMPLETE: {
				this.exitsToGame();
				break;
			}
			case ERROR: {
				if (!(this.saveDialogue.isDialogueCompleted() && this.saveDialogue.isShowingDialog()))
					this.saveDialogue = DialogueBuilder.createText(
						"I am ERROR.", Dialogue.MAX_STRING_LENGTH,
						Dialogue.DialogueType.SPEECH, true
					);
				if (!(this.saveDialogue.isDialogueCompleted() && this.saveDialogue.isShowingDialog()))
					this.saveDialogue.tick();
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
		if (this.saveDialogue != null)
			this.saveDialogue.render(output, graphics);
	}
}
