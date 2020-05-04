/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package item;

import entity.Player;
import level.WorldConstants;
import main.Game;
import main.StateManager.GameState;

public class Bicycle extends ActionItem {

	private static final String ENABLING_DESCRIPTION = "Get on bicycle.";
	private static final String DISABLING_DESCRIPTION = "Get off bicycle.";

	public Bicycle(Game game, String name, String description, Category category) {
		super(game, "BICYCLE", Bicycle.ENABLING_DESCRIPTION, Category.KEYITEMS, WorldConstants.ITEM_BICYCLE);
	}

	public Bicycle(Game game, ItemText text) {
		super(game, text);
		this.description = Bicycle.ENABLING_DESCRIPTION;
	}

	@Override
	public void enable() {
		super.enable();
		final Player player = this.game.getPlayer();
		if (!player.isRidingBicycle()) {
			Player.lockMovements();
			player.enableAutomaticMode();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(800);
						player.startsRidingBicycle();
						Thread.sleep(800);
						Player.unlockMovements();
						player.disableAutomaticMode();
					}
					catch (InterruptedException e) {}
				}
			}).start();
			this.description = Bicycle.DISABLING_DESCRIPTION;
			this.game.getStateManager().setCurrentGameState(GameState.MAIN_GAME);
		}
	}

	@Override
	public void disable() {
		super.disable();
		final Player player = this.game.getPlayer();
		if (player.isRidingBicycle()) {
			Player.lockMovements();
			player.enableAutomaticMode();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(800);
						player.getsOffBicycle();
						Thread.sleep(800);
						Player.unlockMovements();
						player.disableAutomaticMode();
					}
					catch (InterruptedException e) {}
				}
			}).start();
			this.description = Bicycle.ENABLING_DESCRIPTION;
			this.game.getStateManager().setCurrentGameState(GameState.MAIN_GAME);
		}
	}
}
