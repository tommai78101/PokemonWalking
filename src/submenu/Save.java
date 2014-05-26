package submenu;

import java.awt.Graphics;
import main.Game;
import main.Keys;
import screen.BaseScreen;
import abstracts.SubMenu;
import dialogue.NewDialogue;
import entity.Player;

public class Save extends SubMenu {
	
	private enum State {
		ASK, OVERWRITE, SAVING
	}
	
	private Keys keys;
	private State state;
	private NewDialogue newDialogue;
	
	public Save(String name, String enabled, String disabled, Game game) {
		super(name, enabled, disabled, game);
		this.state = State.ASK;
		this.newDialogue = NewDialogue.createEmptyDialogue();
	}
	
	@Override
	public SubMenu initialize(Keys keys) {
		this.keys = keys;
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
							this.state = State.OVERWRITE;
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
					this.newDialogue = NewDialogue.createText("There is already an old save file.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH);
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
					this.newDialogue = NewDialogue.createText("Saving Complete.", NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH);
				if (!this.newDialogue.isDialogueCompleted())
					this.newDialogue.tick();
				else {
					disableSubMenu();
				}
				break;
			}
		}
		
	}
	
	@Override
	public void render(BaseScreen output, Graphics graphics) {
		if (this.newDialogue != null)
			this.newDialogue.render(output, graphics);
	}
	
	@Override
	public void disableSubMenu() {
		this.state = State.ASK;
		if (Player.isMovementsLocked())
			Player.unlockMovements();
		this.newDialogue = NewDialogue.createEmptyDialogue();
		super.disableSubMenu();
	}
}
