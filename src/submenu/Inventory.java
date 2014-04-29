package submenu;

import java.awt.Graphics;
import screen.BaseScreen;

public class Inventory extends SubMenu {
	//TODO: Continue to work on this.
	public Inventory(String name, String enabled, String disabled) {
		super(name, enabled, disabled);
	}
	
	@Override
	public SubMenu initialize() {
		//TODO: Add new inventory art for background.
		this.bgImage = null;
		return this;
	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public void render(BaseScreen output, Graphics graphics) {
	}
	
}
