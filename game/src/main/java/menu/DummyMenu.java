/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package menu;

import java.awt.Graphics;

import abstracts.SubMenu;
import main.Game;
import main.StateManager.GameState;
import screen.Scene;

public class DummyMenu extends SubMenu {

	public DummyMenu(String name, String description, Game game) {
		super(name, description, GameState.RESERVED);
	}

	@Override
	public void tick() {
		this.exit();
	}

	@Override
	public void render(Scene output, Graphics graphics) {}

}
