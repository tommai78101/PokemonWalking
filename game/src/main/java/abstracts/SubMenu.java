/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package abstracts;

/**
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE.
 * */

import java.awt.Graphics;

import event.MenuEvent;
import interfaces.MenuDisplayable;
import main.StateManager.GameState;
import screen.Scene;

public abstract class SubMenu implements MenuDisplayable {
	protected String name;
	protected String description;
	protected MenuEvent menuEvent;
	protected GameState stateType;
	protected boolean isExitingMenu;
	protected boolean exitsToGame;
	protected boolean needsFlashingAnimation;

	public SubMenu(String name, String description, GameState type) {
		this.name = name;
		this.description = description;
		this.stateType = type;
		this.menuEvent = new MenuEvent(this, type);
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public GameState getGameState() {
		return this.stateType;
	}

	/**
	 * For saving data.
	 * 
	 * @return
	 */
	public byte[] getSubMenuData() {
		return this.name.getBytes();
	}

	public Event getEvent() {
		return this.menuEvent;
	}

	public boolean isExiting() {
		return this.isExitingMenu;
	}

	public void exit() {
		this.isExitingMenu = true;
	}

	public void resetExitState() {
		this.isExitingMenu = false;
	}

	/**
	 * Should the game flash a bit when exiting the submenu?
	 * 
	 * @return True, if the submenu needs to show flashing animation when exiting submenu. False, if otherwise.
	 */
	public boolean needsFlashing() {
		return this.needsFlashingAnimation;
	}

	/**
	 * Does the submenu exits straight to the main game?
	 * 
	 * @return True, if jumping straight to the game. False, if jumping back to the main menu.
	 */
	public boolean exitsToGame() {
		return this.exitsToGame;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass() || !(obj instanceof SubMenu))
			return false;
		SubMenu other = (SubMenu) obj;
		if (this.description == null) {
			if (other.description != null)
				return false;
		}
		else if (!this.description.equals(other.description))
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		}
		else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public abstract void render(Scene output, Graphics graphics);
}
