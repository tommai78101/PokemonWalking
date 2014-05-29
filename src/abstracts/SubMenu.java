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
import main.Game;
import main.Keys;
import screen.BaseScreen;

public abstract class SubMenu {
	
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
	
	public abstract SubMenu initialize(Keys keys);
	
	public abstract void tick();
	
	public abstract void render(BaseScreen output, Graphics graphics);
}
