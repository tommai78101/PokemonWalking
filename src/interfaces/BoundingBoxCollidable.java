package interfaces;

import abstracts.Tile;

public interface BoundingBoxCollidable {
	public void handleCollision(Tile tile, int xAcceleration, int yAcceleration);
}
