/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */
package item;

import abstracts.Item;
import entity.Player;
import level.Area;
import main.Game;

/**
 * Any base implementations of the abstract class object, Item, will need to implement or devise a
 * way to create Dialogues associated with that item object.
 * 
 * @author tlee
 *
 */
public class KeyItem extends Item {

	protected boolean enabled;

	public KeyItem(String name, String description, Category category, int id) {
		super(name, description, category, id);
	}

	public KeyItem(ItemText text) {
		super(text);
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
	public boolean canBeTossed() {
		return false;
	}

	@Override
	public void doAction(Game game) {
		if (this.enabled)
			this.disable();
		else
			this.enable();
	}

	@Override
	public void dropAt(Area area, Player player) {
		// TODO: Continue to work on this.
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
