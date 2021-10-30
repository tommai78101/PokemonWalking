package menu;

import java.awt.Graphics;

import abstracts.SubMenu;
import main.Game;
import main.StateManager.GameState;
import screen.Scene;

public class Exit extends SubMenu {
	public Exit(String name, String description, Game unused) {
		super(name, description, GameState.EXIT);
		this.needsFlashingAnimation = false;
		this.exitsToGame = true;
	}

	@Override
	public void render(Scene output, Graphics graphics) {}

	@Override
	public void tick() {}
}
