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

import main.Game;
import main.Game.State;
import abstracts.Item;

public class DummyItem extends Item {
	public DummyItem(Game game, String name, String description, Category category) {
		super(game, name, description, category);
	}
	
	@Override
	public void doAction() {
		game.getBaseScreen().setRenderingEffectTick((byte) 0x0);
		game.setState(State.PAUSED);
		game.getStartMenu().openMenu();
	}
}
