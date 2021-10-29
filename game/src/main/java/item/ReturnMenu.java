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
import enums.ItemCategories;
import level.Area;
import main.Game;
import main.StateManager.GameState;

public class ReturnMenu extends Item {
	public ReturnMenu(ModdedItem text) {
		super(text);
	}

	public ReturnMenu(String name, String description, ItemCategories category, int id) {
		super(name, description, category, id);
	}

	@Override
	public void doAction(Game game) {
		game.getBaseScreen().setRenderingEffectTick((byte) 0x0);
		game.getStateManager().setCurrentGameState(GameState.START_MENU);
	}

	@Override
	public void dropAt(Area area, Player player) {
		// Left intentionally blank.
	}
}
