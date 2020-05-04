/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package item;

import java.awt.Graphics;

import abstracts.Item;
import entity.Player;
import level.Area;
import main.Game;
import main.StateManager.GameState;
import screen.Scene;

public class DummyItem extends Item {
	public DummyItem(Game game, String name, String description, Category category, int id) {
		super(game, name, description, category, id);
	}

	public DummyItem(Game game, ItemText text) {
		super(game, text);
	}

	@Override
	public void doAction() {
		this.game.getBaseScreen().setRenderingEffectTick((byte) 0x0);
		this.game.getStateManager().setCurrentGameState(GameState.START_MENU);
	}

	@Override
	public void dropAt(Area area, Player player) {
		// TODO: Continue with this action.
	}

	@Override
	public void tick() {
		return;
	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		return;
	}
}
