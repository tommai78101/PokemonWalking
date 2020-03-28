package main;

public class StateManager {
	public enum GameState {
		INTRO,
		LOADING,
		SAVE,
		MAIN_GAME,
		START_MENU,
		INVENTORY,
		CUTSCENE,
		DIALOGUE,
		EXIT,
		RESERVED,
	}

	protected GameState currentGameState;
	protected GameState previousGameState;

	public StateManager() {
		this.initialize();
	}

	public void initialize() {
		this.currentGameState = GameState.LOADING;
	}

	public boolean isLockedTo(GameState state) {
		if (this.currentGameState == state)
			return true;
		return false;
	}

	public GameState getCurrentGameState() {
		return this.currentGameState;
	}

	public void setCurrentGameState(GameState currentGameState) {
		this.previousGameState = this.currentGameState;
		this.currentGameState = currentGameState;
	}

	public GameState getPreviousGameState() {
		return this.previousGameState;
	}

	public void setPreviousGameState(GameState previousGameState) {
		this.previousGameState = previousGameState;
	}
}
