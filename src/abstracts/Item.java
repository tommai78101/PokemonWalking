package abstracts;

import level.Area;
import resources.Art;
import screen.BaseScreen;
import entity.Player;


public abstract class Item {
	
	protected String name;
	protected String description;
	protected boolean picked;
	
	public Item(String name, String description) {
		setName(name);
		setDescription(description);
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
		//TODO: Add function that allows the item to be placed at.
	}
}
