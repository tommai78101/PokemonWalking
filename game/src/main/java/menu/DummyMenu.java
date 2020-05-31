/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

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
