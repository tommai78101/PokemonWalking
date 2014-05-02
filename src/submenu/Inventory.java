/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package submenu;

import item.DummyItem;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import main.Game;
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
	private int arrowPosition;
	private int itemListSpan = 0;

	//TODO: Continue to work on this.
	public Inventory(String name, String enabled, String disabled, Game game) {
		super(name, enabled, disabled, game);
		this.itemCursor = 0;
		this.items = new ArrayList<Item>();
		this.items.add(new DummyItem(game, "RETURN", "Exit the inventory."));
		this.arrowPosition = 0;
	}
	
	private void renderText(Graphics g) {
		g.setFont(Art.font);
		g.setColor(Color.black);
		
		try {
			for (int i = 0; i < 5; i++) {
				if (i > items.size())
					break;
				g.drawString(items.get(itemListSpan + i).getName(), 8 * Dialogue.TEXT_SPACING_WIDTH * MainComponent.GAME_SCALE, MainComponent.GAME_SCALE * (Tile.HEIGHT + 5) + ((i) * Tile.HEIGHT * MainComponent.GAME_SCALE));
			}
		}
		catch (Exception e) {
		}
		try {
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
			this.resetCursor();
			this.subMenuActivation = false;
		}
		if ((this.keys.up.keyStateDown || this.keys.W.keyStateDown) && (!this.keys.up.lastKeyState || !this.keys.W.lastKeyState)) {
			if (itemCursor > 0) {
				itemCursor--;
				if (arrowPosition > 0)
					arrowPosition--;
			}
			this.keys.up.lastKeyState = true;
			this.keys.W.lastKeyState = true;
		}
		if ((this.keys.down.keyStateDown || this.keys.S.keyStateDown) && (!this.keys.down.lastKeyState || !this.keys.S.lastKeyState)) {
			if (itemCursor < items.size() - 1) {
				itemCursor++;
				if (arrowPosition < 4)
					arrowPosition++;
			}
			this.keys.down.lastKeyState = true;
			this.keys.S.lastKeyState = true;
		}
		if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
			this.keys.Z.lastKeyState = true;
			this.keys.SLASH.lastKeyState = true;
			this.items.get(itemCursor).doAction();
			//this.subMenuActivation = false;
		}

		if (itemCursor >= (itemListSpan + 5)) {
			itemListSpan++;
		}
		else if (itemCursor < itemListSpan) {
			itemListSpan--;
		}
	}
	
	@Override
	public void render(BaseScreen output, Graphics graphics) {
		if (this.subMenuActivation) {
			output.blit(Art.inventory_gui, 0, 0);
			Dialogue.renderBox(output, 0, 6, 9, 2);
			renderListBox(output, 3, 1, 7, 5);
			output.blit(Art.dialogue_pointer, 18 * MainComponent.GAME_SCALE, MainComponent.GAME_SCALE * (5 + (arrowPosition * 5)));
			graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
			renderText(graphics);
		}
	}
	
	public void addItem(Item item) {
		this.items.add(items.size() - 1, item);
	}
	
	public void tossItem() {
		this.items.remove(itemCursor);
	}
	
	public void resetCursor() {
		this.itemCursor = 0;
		this.arrowPosition = 0;
		this.itemListSpan = 0;
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
