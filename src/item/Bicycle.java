package item;

import level.WorldConstants;
import main.Game;
import main.Game.State;
import entity.Player;

public class Bicycle extends ActionItem {

	private static final String ENABLING_DESCRIPTION = "Get on bicycle.";
	private static final String DISABLING_DESCRIPTION = "Get off bicycle.";

	public Bicycle(Game game, String name, String description, Category category) {
		super(game, "BICYCLE", ENABLING_DESCRIPTION, Category.KEYITEMS, WorldConstants.ITEM_BICYCLE);
	}

	@Override
	public void enable() {
		super.enable();
		final Player player = this.game.getPlayer();
		if (!player.isRidingBicycle()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(800);
						player.startsRidingBicycle();
					}
					catch (InterruptedException e) {
					}
				}
			}).start();
			this.description = DISABLING_DESCRIPTION;
			this.game.setState(State.GAME);
			this.game.getStartMenu().closeMenu();
		}
	}

	@Override
	public void disable() {
		super.disable();
		final Player player = this.game.getPlayer();
		if (player.isRidingBicycle()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(800);
						player.getsOffBicycle();
					}
					catch (InterruptedException e) {
					}
				}
			}).start();
			this.description = ENABLING_DESCRIPTION;
			this.game.setState(State.GAME);
			this.game.getStartMenu().closeMenu();
		}
	}
}
