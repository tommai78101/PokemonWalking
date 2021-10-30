package main;

public class StateManager {
	protected GameState currentGameState;

	protected GameState previousGameState;

	public StateManager() {
		this.initialize();
	}

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

	public GameState getCurrentGameState() {
		return this.currentGameState;
	}

	public GameState getPreviousGameState() {
		return this.previousGameState;
	}

	public void initialize() {
		this.currentGameState = GameState.LOADING;
	}

	public boolean isLockedTo(GameState state) {
		if (this.currentGameState == state)
			return true;
		return false;
	}

	public void setCurrentGameState(GameState currentGameState) {
		this.previousGameState = this.currentGameState;
		this.currentGameState = currentGameState;
	}

	public void setPreviousGameState(GameState previousGameState) {
		this.previousGameState = previousGameState;
	}
}
