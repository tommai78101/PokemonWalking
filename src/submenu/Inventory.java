/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package submenu;

import java.awt.Color;
import java.awt.Graphics;

import main.Keys;
import main.MainComponent;
import resources.Art;
import screen.BaseScreen;
import abstracts.SubMenu;
import dialogue.Dialogue;

public class Inventory extends SubMenu {
	
	private Keys keys;

	//TODO: Continue to work on this.
	public Inventory(String name, String enabled, String disabled) {
		super(name, enabled, disabled);
	}
	
	private void renderText(Graphics g) {
		g.setFont(Art.font);
		g.setColor(Color.black);
		
		try {
			g.drawString("", Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
		}
		catch (Exception e) {
		}
		
	}

	@Override
	public SubMenu initialize(Keys keys) {
		//TODO: Add new inventory art for background.
		this.keys = keys;
		return Inventory.this;
	}
	
	@Override
	public void tick() {
		if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && (!this.keys.X.lastKeyState || !this.keys.PERIOD.lastKeyState)) {
			this.keys.X.lastKeyState = true;
			this.keys.PERIOD.lastKeyState = true;
			this.subMenuActivation = false;
		}
	}
	
	@Override
	public void render(BaseScreen output, Graphics graphics) {
		if (this.subMenuActivation) {
			output.blit(Art.inventory_gui, 0, 0);
			graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
			renderText(graphics);
		}
	}
	
}
