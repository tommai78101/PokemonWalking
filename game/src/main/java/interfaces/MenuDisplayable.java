package interfaces;

import java.awt.Graphics;

import screen.BaseScreen;

public interface MenuDisplayable {
	public void tick();
	public void render(BaseScreen screen, Graphics graphics);
}