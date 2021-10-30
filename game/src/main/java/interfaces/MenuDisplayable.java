package interfaces;

import java.awt.Graphics;

import screen.Scene;

public interface MenuDisplayable {
	public void render(Scene screen, Graphics graphics);

	public void tick();
}