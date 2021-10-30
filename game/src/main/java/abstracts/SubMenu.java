/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

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

	public void exit() {
		this.isExitingMenu = true;
	}

	/**
	 * Does the submenu exits straight to the main game?
	 * 
	 * @return True, if jumping straight to the game. False, if jumping back to the main menu.
	 */
	public boolean exitsToGame() {
		return this.exitsToGame;
	}

	public String getDescription() {
		return this.description;
	}

	public Event getEvent() {
		return this.menuEvent;
	}

	public GameState getGameState() {
		return this.stateType;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * For saving data.
	 * 
	 * @return
	 */
	public byte[] getSubMenuData() {
		return this.name.getBytes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	public boolean isExiting() {
		return this.isExitingMenu;
	}

	/**
	 * Should the game flash a bit when exiting the submenu?
	 * 
	 * @return True, if the submenu needs to show flashing animation when exiting submenu. False, if
	 *         otherwise.
	 */
	public boolean needsFlashing() {
		return this.needsFlashingAnimation;
	}

	@Override
	public abstract void render(Scene output, Graphics graphics);

	public void resetExitState() {
		this.isExitingMenu = false;
	}
}
