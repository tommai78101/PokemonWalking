/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
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
