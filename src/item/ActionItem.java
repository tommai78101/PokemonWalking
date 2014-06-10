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
	
	@Override
	public boolean equals(Object object) {
		try {
			if (object == null)
				return false;
			if (this.getClass() != object.getClass())
				return false;
			final Item item = (Item) object;
			if ((this.name == null) ? (item.getName() != null) : !this.name.equals(item.getName()))
				return false;
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + ((this.name != null) ? this.name.hashCode() : 0);
		return hash;
	}
}
