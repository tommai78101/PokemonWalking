package abstracts;

import item.ItemText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

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

	public static HashMap<Integer, ItemText> loadItemResources(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Item.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			ItemText itemText = new ItemText();
			HashMap<Integer, ItemText> result = new HashMap<Integer, ItemText>();
			int id = 0;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("%")) {
					itemText.type = ItemText.Type.getType(line.split("%")[1]);
				} else if (line.startsWith("#")) {
					itemText.itemName = line.split("#")[1];
				} else if (line.startsWith("@")) {
					itemText.description = line.split("@")[1];
				}
				if (itemText.isComplete()) {
					itemText.id = id;
					result.put(id, itemText);
					itemText = new ItemText();
					id++;
				}
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
