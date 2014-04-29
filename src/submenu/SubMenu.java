package submenu;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import screen.BaseScreen;
import dialogue.MenuItem;

public abstract class SubMenu extends MenuItem {
	
	private boolean subMenuActivation;
	protected BufferedImage bgImage;
	
	public SubMenu(String name, String enabled, String disabled) {
		super(name, enabled, disabled);
		bgImage = null;
	}
	
	public boolean isActivated() {
		return this.subMenuActivation;
	}
	
	public void enableSubMenu() {
		this.subMenuActivation = true;
	}
	
	public void disableSubMenu() {
		this.subMenuActivation = false;
	}
	
	public abstract SubMenu initialize();
	
	public abstract void tick();
	
	public abstract void render(BaseScreen output, Graphics graphics);
}
