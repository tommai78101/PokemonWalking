package interfaces;

import abstracts.Entity;
import abstracts.Item;

public interface Interactable {
	void interact(Entity target);

	void interact(Entity target, Item item);
}