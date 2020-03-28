package menu;

import java.awt.Graphics;

import abstracts.SubMenu;
import main.Game;
import main.StateManager.GameState;
import screen.Scene;

public class Exit extends SubMenu {
	private Game game;

	public Exit(String name, String description, Game game) {
		super(name, description, GameState.EXIT);
		this.game = game;
	}

	@Override
	public void tick() {}

	@Override
	public void render(Scene output, Graphics graphics) {}
}
