package item;

import level.WorldConstants;
import main.Game;
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
		Player player = this.game.getPlayer();
		if (!player.isRidingBicycle()) {
			player.startsRidingBicycle();
			this.description = DISABLING_DESCRIPTION;
		}
	}
	
	@Override
	public void disable() {
		super.disable();
		Player player = this.game.getPlayer();
		if (player.isRidingBicycle()) {
			player.getsOffBicycle();
			this.description = ENABLING_DESCRIPTION;
		}
	}
}
