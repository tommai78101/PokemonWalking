package interfaces;

import java.awt.Graphics;

import screen.Scene;

public interface UpdateRenderable {
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY);

	public void tick();
}