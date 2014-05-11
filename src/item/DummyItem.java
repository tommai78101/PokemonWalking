package item;

import main.Game;
import main.Game.State;
import abstracts.Item;

public class DummyItem extends Item {
	public DummyItem(Game game, String name, String description) {
		super(game, name, description);
	}

	@Override
	public void doAction() {
		game.getBaseScreen().setRenderingEffectTick((byte) 0x0);
		game.setState(State.PAUSED);
		game.getStartMenu().openMenu();
	}
}
