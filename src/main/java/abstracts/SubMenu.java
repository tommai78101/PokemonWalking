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

import interfaces.InterfaceMenu;
import main.Game;
import main.Keys;
import screen.BaseScreen;

public abstract class SubMenu implements InterfaceMenu {

	protected boolean subMenuActivation;

	private String name;
	private String enabledDescription;
	private String disabledDescription;
	protected Game game;
	private boolean enabled;

	public SubMenu(String name, String enabled, String disabled, Game game) {
		this.name = name;
		this.enabledDescription = enabled;
		this.disabledDescription = disabled;
		this.game = game;
		this.subMenuActivation = false;
	}

	public SubMenu() {
		this.name = null;
		this.enabledDescription = "";
		this.disabledDescription = "";
		this.game = null;
		this.subMenuActivation = false;
	}

	public SubMenu(Game game) {
		this.name = null;
		this.enabledDescription = "";
		this.disabledDescription = "";
		this.game = game;
		this.subMenuActivation = false;
	}

	public boolean isActivated() {
		return this.subMenuActivation;
	}

	public void enableSubMenu() {
		this.subMenuActivation = true;
	}

	public void disableSubMenu() {
		this.subMenuActivation = false;
	}

	public void toggleDescription(boolean value) {
		this.enabled = value;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		if (this.enabled)
			return this.enabledDescription;
		else
			return this.disabledDescription;
	}

	public byte[] getSubMenuData() {
		return this.name.getBytes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disabledDescription == null) ? 0 : disabledDescription.hashCode());
		result = prime * result + ((enabledDescription == null) ? 0 : enabledDescription.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubMenu other = (SubMenu) obj;
		if (disabledDescription == null) {
			if (other.disabledDescription != null)
				return false;
		} else if (!disabledDescription.equals(other.disabledDescription))
			return false;
		if (enabledDescription == null) {
			if (other.enabledDescription != null)
				return false;
		} else if (!enabledDescription.equals(other.enabledDescription))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public abstract SubMenu initialize(Keys keys);

	public abstract void render(BaseScreen output, Graphics graphics);
}
