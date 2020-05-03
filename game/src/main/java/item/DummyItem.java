/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

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
