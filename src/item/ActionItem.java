package item;

import main.Game;
import abstracts.Item;

public class ActionItem extends Item {
	
	protected boolean enabled;
	
	public ActionItem(Game game, String name, String description, Category category, int id) {
		super(game, name, description, category, id);
	}
	
	@Override
	public void doAction() {
		if (enabled)
			disable();
		else
			enable();
	}
	
	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
}
