/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package submenu;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import main.Keys;
import main.MainComponent;
import resources.Art;
import screen.BaseScreen;
import abstracts.Item;
import abstracts.SubMenu;
import abstracts.Tile;
import dialogue.Dialogue;

public class Inventory extends SubMenu {
	
	private Keys keys;
	private List<Item> items;
	private int itemCursor;

	//TODO: Continue to work on this.
	public Inventory(String name, String enabled, String disabled) {
		super(name, enabled, disabled);
		this.itemCursor = 0;
		this.items = new ArrayList<Item>();
	}
	
	private void renderText(Graphics g) {
		g.setFont(Art.font);
		g.setColor(Color.black);
		
		try {
			
			for (int i = 0; i < items.size(); i++)
				g.drawString(items.get(i).getName(), 3 * Dialogue.TEXT_SPACING_WIDTH * MainComponent.GAME_SCALE, items.size() * Tile.HEIGHT);
			
			String[] tokens = Dialogue.toLines(items.get(itemCursor).getDescription(), Dialogue.MAX_STRING_LENGTH);
			g.drawString(tokens[0], Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
			g.drawString(tokens[1], Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
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
			Dialogue.renderBox(output, 0, 6, 9, 2);
			renderListBox(output, 3, 1, 7, 5);
			graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
			renderText(graphics);
		}
	}
	
	public void addItem(Item item) {
		this.items.add(item);
	}
	
	public void tossItem() {
		this.items.remove(itemCursor);
	}
	
	//------------------------------------        PRIVATE METHODS        -----------------------------------------

	private void renderListBox(BaseScreen output, int x, int y, int width, int height) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (i * Tile.WIDTH), (y * Tile.HEIGHT - 8) + (j * Tile.HEIGHT));
			}
		}
		for (int k = 0; k < width; k++)
			output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (k * Tile.WIDTH), (height * Tile.HEIGHT));
	}
}
