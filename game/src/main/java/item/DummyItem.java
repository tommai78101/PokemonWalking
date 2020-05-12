/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package item;

import abstracts.Item;
import entity.Player;
import level.Area;
import main.Game;
import main.StateManager.GameState;

public class DummyItem extends Item {
	public DummyItem(String name, String description, Category category, int id) {
		super(name, description, category, id);
	}

	public DummyItem(ItemText text) {
		super(text);
	}

	@Override
	public void doAction(Game game) {
		game.getBaseScreen().setRenderingEffectTick((byte) 0x0);
		game.getStateManager().setCurrentGameState(GameState.START_MENU);
	}

	@Override
	public void dropAt(Area area, Player player) {
		// TODO: Continue with this action.
	}
}
