/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package item;

import entity.Player;
import enums.ItemCategories;
import level.PixelData;
import level.WorldConstants;
import main.StateManager.GameState;

public class Bicycle extends KeyItem {

	private static final String ENABLING_DESCRIPTION = "Get on bicycle.";
	private static final String DISABLING_DESCRIPTION = "Get off bicycle.";

	public Bicycle(ModdedItem text) {
		super(text);
		this.description = Bicycle.ENABLING_DESCRIPTION;
	}

	/**
	 * The correct constructor format to use. (May 10, 2020)
	 *
	 * @param game
	 * @param pixelData
	 */
	public Bicycle(PixelData pixelData, int x, int y) {
		super("BICYCLE", "Ride bike.", ItemCategories.KEYITEMS, pixelData.getRed());
		this.pixelData = pixelData;
		this.pixelData.setAsItem(true);
		this.setPosition(x, y);
	}

	public Bicycle(String name, String description, ItemCategories category) {
		super("BICYCLE", Bicycle.ENABLING_DESCRIPTION, ItemCategories.KEYITEMS, WorldConstants.ITEM_BICYCLE);
	}

	@Override
	public void disable() {
		super.disable();
		final Player player = this.inventory.getPlayer();
		if (player.isRidingBicycle()) {
			Player.lockMovements();
			player.enableAutomaticMode();
			new Thread(() -> {
				try {
					Thread.sleep(800);
					player.getsOffBicycle();
					Thread.sleep(800);
					Player.unlockMovements();
					player.disableAutomaticMode();
				}
				catch (InterruptedException e) {}
			}).start();
			this.description = Bicycle.ENABLING_DESCRIPTION;
			this.inventory.getStateManager().setCurrentGameState(GameState.MAIN_GAME);
		}
	}

	@Override
	public void enable() {
		super.enable();
		final Player player = this.inventory.getPlayer();
		if (!player.isRidingBicycle()) {
			Player.lockMovements();
			player.enableAutomaticMode();
			new Thread(() -> {
				try {
					Thread.sleep(800);
					player.startsRidingBicycle();
					Thread.sleep(800);
					Player.unlockMovements();
					player.disableAutomaticMode();
				}
				catch (InterruptedException e) {}
			}).start();
			this.description = Bicycle.DISABLING_DESCRIPTION;
			this.inventory.getStateManager().setCurrentGameState(GameState.MAIN_GAME);
		}
	}
}
