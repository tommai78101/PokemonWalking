package item;

import main.Game;
import main.Game.State;
import submenu.Inventory;
import abstracts.Item;

public class DummyItem extends Item {
	public DummyItem(Game game, String name, String description, Inventory.Category category) {
		super(game, name, description, category);
	}

	@Override
	public void doAction() {
		game.getBaseScreen().setRenderingEffectTick((byte) 0x0);
		game.setState(State.PAUSED);
		game.getStartMenu().openMenu();
	}
}
