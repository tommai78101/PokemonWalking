package submenu;

import java.awt.Graphics;
import main.Keys;
import screen.BaseScreen;
import abstracts.SubMenu;

public class DummyMenu extends SubMenu {
	
	public DummyMenu(String name, String enabled, String disabled) {
		super(name, enabled, disabled);
	}
	
	@Override
	public SubMenu initialize(Keys keys) {
		//Always return "this" back.
		return this;
	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public void render(BaseScreen output, Graphics graphics) {
	}
	
}
