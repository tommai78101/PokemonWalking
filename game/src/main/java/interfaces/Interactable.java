package interfaces;

import abstracts.Entity;
import level.Area;

public interface Interactable {
	void interact(Area area, Entity target);
}