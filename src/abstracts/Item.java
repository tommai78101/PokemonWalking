package abstracts;

import level.Area;
import main.Game;
import resources.Art;
import screen.BaseScreen;
import entity.Player;

public abstract class Item {

	protected String name;
	protected String description;
	protected Game game;
	protected boolean picked;

	public Item(Game game, String name, String description) {
		setName(name);
		setDescription(description);
		this.game = game;
		this.picked = false;
	}

	public void setName(String value) {
		this.name = value;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public void renderTiles(BaseScreen output, int xOffset, int yOffset) {
		if (!this.picked) {
			output.blit(Art.item, xOffset, yOffset);
		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void pick() {
		this.picked = true;
	}

	public void drop() {
		this.picked = false;
	}

	public void dropAt(Area area, Player player) {
		this.picked = false;
		// TODO: Add function that allows the item to be placed at.
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
			// if ((this.description == null) ? (item.getDescription() != null) : !this.description.equals(item.getDescription()))
			// return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + ((this.name != null) ? this.name.hashCode() : 0);
		return hash;
	}

	public abstract void doAction();
}
