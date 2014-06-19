/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

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
	
	public Bicycle(Game game, ItemText text){
		super(game, text);
		this.description = ENABLING_DESCRIPTION;
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
