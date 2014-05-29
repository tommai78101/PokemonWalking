package submenu;

import java.awt.Graphics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import main.Game;
import main.Keys;
import screen.BaseScreen;
import abstracts.SubMenu;
import dialogue.NewDialogue;
import entity.Player;

public class Save extends SubMenu {
	
	public enum State {
		ASK, OVERWRITE, SAVING, SAVED, ERROR
	}
	
	private State state;
	private NewDialogue newDialogue;
	ExecutorService executor;
	
	public Save(String name, String enabled, String disabled, Game game) {
		super(name, enabled, disabled, game);
		this.state = State.ASK;
		this.newDialogue = NewDialogue.createEmptyDialogue();
	}
	
	@Override
	public SubMenu initialize(Keys keys) {
		return Save.this;
	}
	
	@Override
	public void tick() {
		if (!Player.isMovementsLocked())
			Player.lockMovements();
		switch (this.state) {
			case ASK: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = NewDialogue.createText("Do you want to save the game?", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_QUESTION);
				if (this.newDialogue.isDialogueTextSet() && !this.newDialogue.isDialogueCompleted()) {
					this.newDialogue.tick();
				}
				else {
					if (this.newDialogue.getDialogueType() == NewDialogue.DIALOGUE_QUESTION) {
						if (!this.newDialogue.yesNoQuestionHasBeenAnswered())
							this.newDialogue.tick();
						if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
							//TODO: This is the place to check to see if there exists an old save file.
							if (this.game.checkSaveData())
								this.state = State.OVERWRITE;
							else
								this.state = State.SAVING;
							this.newDialogue.clearDialogueLines();
						}
						else if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.FALSE)
							this.disableSubMenu();
					}
				}
				break;
			}
			case OVERWRITE: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = NewDialogue.createText("There is already an old save file. TESTING SCROLLING.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH);
				if (!this.newDialogue.isDialogueCompleted()) {
					this.newDialogue.tick();
				}
				else {
					switch (this.newDialogue.getDialogueType()) {
						case NewDialogue.DIALOGUE_SPEECH:
							this.newDialogue = NewDialogue.createText("Do you want to overwrite it?", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_QUESTION);
							break;
						case NewDialogue.DIALOGUE_QUESTION:
							if (!this.newDialogue.yesNoQuestionHasBeenAnswered())
								this.newDialogue.tick();
							if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.TRUE) {
								this.state = State.SAVING;
								this.newDialogue.clearDialogueLines();
							}
							else if (this.newDialogue.getAnswerToSimpleQuestion() == Boolean.FALSE)
								this.disableSubMenu();
							break;
					}
				}
				break;
			}
			case SAVING: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = NewDialogue.createText("Saving...", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH);
				if (!this.newDialogue.isDialogueCompleted()) {
					this.newDialogue.tick();
					if (executor == null) {
						executor = Executors.newFixedThreadPool(1);
						executor.execute(new Runnable() {
							@Override
							public void run() {
								game.save();
							}
						});
						executor.shutdown();
						try {
							executor.awaitTermination(1, TimeUnit.MINUTES);
						}
						catch (InterruptedException e) {
							this.state = State.ERROR;
							this.newDialogue.clearDialogueLines();
						}
					}
				}
				else {
					if (executor.isTerminated()) {
						executor = null;
						this.state = State.SAVED;
						this.newDialogue.clearDialogueLines();
					}
				}
				break;
			}
			case SAVED: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = NewDialogue.createText("Saving Complete.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH);
				if (!this.newDialogue.isDialogueCompleted())
					this.newDialogue.tick();
				else
					disableSubMenu();
				break;
			}
			case ERROR: {
				if (!this.newDialogue.isDialogueTextSet())
					this.newDialogue = NewDialogue.createText("I am ERROR.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH);
				if (!this.newDialogue.isDialogueCompleted())
					this.newDialogue.tick();
				else
					disableSubMenu();
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
	public void render(BaseScreen output, Graphics graphics) {
		if (this.newDialogue != null)
			this.newDialogue.render(output, graphics);
	}
	
	@Override
	public void disableSubMenu() {
		switch (this.state) {
			default:
				this.state = State.ASK;
				break;
			case SAVED:
			case ERROR:
				break;
		}
		if (Player.isMovementsLocked())
			Player.unlockMovements();
		this.newDialogue = NewDialogue.createEmptyDialogue();
		super.disableSubMenu();
	}
}
