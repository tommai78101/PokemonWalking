package event;

import abstracts.Event;
import abstracts.SubMenu;
import interfaces.Callback;
import main.Game;
import main.StateManager.GameState;

public class MenuEvent extends Event {
	protected GameState stateType;
	protected SubMenu subMenu;
	protected Game game;

	public MenuEvent(SubMenu menu, GameState type) {
		this.subMenu = menu;
		this.stateType = type;
	}

	public GameState getGameState() {
		return this.stateType;
	}

	public SubMenu getMenu() {
		return this.subMenu;
	}

	@Override
	public void trigger(Callback callback) {
		callback.call();
	}
}
