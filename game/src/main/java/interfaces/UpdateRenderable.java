package interfaces;

import screen.BaseScreen;

public interface UpdateRenderable {
	public void tick();
	public void render(BaseScreen screen, int offsetX, int offsetY);
}