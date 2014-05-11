/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package submenu;

import java.awt.Graphics;

import main.Game;
import main.Keys;
import screen.BaseScreen;
import abstracts.SubMenu;

public class DummyMenu extends SubMenu {

	public DummyMenu(String name, String enabled, String disabled, Game game) {
		super(name, enabled, disabled, game);
	}

	@Override
	public SubMenu initialize(Keys keys) {
		// Always return "this" back.
		return this;
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(BaseScreen output, Graphics graphics) {
	}

}
