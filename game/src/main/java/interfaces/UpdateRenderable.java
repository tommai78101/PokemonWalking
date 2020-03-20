package interfaces;

import screen.Scene;

public interface UpdateRenderable {
	public void tick();
	public void render(Scene screen, int offsetX, int offsetY);
}