package main;

public class StateManager {
	public enum GameState {
		INTRO,
		LOADING,
		SAVING,
		MAIN_GAME,
		START_MENU,
		INVENTORY,
		CUTSCENE,
		DIALOGUE,
		RESERVED
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
		return currentGameState;
	}

	public void setCurrentGameState(GameState currentGameState) {
		this.previousGameState = this.currentGameState;
		this.currentGameState = currentGameState;
	}

	public GameState getPreviousGameState() {
		return previousGameState;
	}

	public void setPreviousGameState(GameState previousGameState) {
		this.previousGameState = previousGameState;
	}
}
