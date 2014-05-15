/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package submenu;

import item.DummyItem;
import item.ItemText;
import java.awt.Color;
import java.awt.Graphics;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import level.WorldConstants;
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
	
	public enum Category {
		POTIONS(0), KEYITEMS(1), POKEBALLS(2), TM_HM(3);
		
		private int id;
		
		private Category(int value) {
			this.id = value;
		}
		
		/**
		 * Obtains a Category enum value that matches the given ID number.
		 * 
		 * <p>
		 * If there is no Category that comes after the last element, it will give the first element, and wraps from there.
		 * 
		 * @param value
		 *            The ID number of the category that is to be obtained.
		 * 
		 * @return The category that matches the given ID number.
		 * */
		public static Category getWrapped(int value) {
			Category[] categories = Category.values();
			if (value < 0)
				value = (categories.length - 1);
			if (value > categories.length - 1)
				value = 0;
			for (Category c : categories) {
				if (c.id == value)
					return categories[value];
			}
			return categories[0];
		}
		
		public int getID() {
			return id;
		}
	};
	
	private Keys keys;
	private List<Map.Entry<Item, Integer>> potions;
	private List<Map.Entry<Item, Integer>> keyItems;
	private List<Map.Entry<Item, Integer>> pokeballs;
	private List<Map.Entry<Item, Integer>> TMs_HMs;
	private int itemCursor;
	private int arrowPosition;
	private int itemListSpan = 0;
	private Category category;
	private byte tick = (byte) 0x0;
	
	// TODO: Continue to work on this.
	public Inventory(String name, String enabled, String disabled, Game game) {
		super(name, enabled, disabled, game);
		this.itemCursor = 0;
		this.potions = new ArrayList<Map.Entry<Item, Integer>>();
		this.keyItems = new ArrayList<Map.Entry<Item, Integer>>();
		this.pokeballs = new ArrayList<Map.Entry<Item, Integer>>();
		this.TMs_HMs = new ArrayList<Map.Entry<Item, Integer>>();
		ItemText itemText = WorldConstants.items.get(WorldConstants.ITEM_RETURN);
		this.potions.add(new AbstractMap.SimpleEntry<Item, Integer>(new DummyItem(game, itemText.itemName, itemText.description, itemText.category), Integer.MAX_VALUE));
		this.keyItems.add(new AbstractMap.SimpleEntry<Item, Integer>(new DummyItem(game, itemText.itemName, itemText.description, itemText.category), Integer.MAX_VALUE));
		this.pokeballs.add(new AbstractMap.SimpleEntry<Item, Integer>(new DummyItem(game, itemText.itemName, itemText.description, itemText.category), Integer.MAX_VALUE));
		this.TMs_HMs.add(new AbstractMap.SimpleEntry<Item, Integer>(new DummyItem(game, itemText.itemName, itemText.description, itemText.category), Integer.MAX_VALUE));
		this.arrowPosition = 0;
		this.category = Category.POTIONS;
	}
	
	private void renderText(Graphics g) {
		if (tick >= (byte) 0x4) {
			g.setFont(Art.font);
			g.setColor(Color.black);
			
			List<Map.Entry<Item, Integer>> list = this.getCurrentList();
			try {
				for (int i = 0; i < 5; i++) {
					if (i >= list.size())
						break;
					Map.Entry<Item, Integer> entry = list.get(itemListSpan + i);
					g.drawString(entry.getKey().getName(), 8 * Dialogue.TEXT_SPACING_WIDTH * MainComponent.GAME_SCALE, MainComponent.GAME_SCALE * ((Tile.HEIGHT) + (Tile.HEIGHT * i)) + 12);
					int value = entry.getValue().intValue();
					if (value != Integer.MAX_VALUE && this.category != Category.KEYITEMS) {
						String string = "*" + Integer.toString(value);
						g.drawString(string, 8 * Dialogue.TEXT_SPACING_WIDTH * MainComponent.GAME_SCALE + ((12 - string.length()) * Dialogue.TEXT_SPACING_WIDTH) * MainComponent.GAME_SCALE, MainComponent.GAME_SCALE * ((Tile.HEIGHT) + (Tile.HEIGHT * i)) + 12);
					}
				}
			}
			catch (Exception e) {
			}
			//This needs to be separated, else if there's a problem, the latter won't render anything.
			try {
				Map.Entry<Item, Integer> entry = list.get(itemCursor);
				String[] tokens = Dialogue.toLines(entry.getKey().getDescription(), Dialogue.MAX_STRING_LENGTH);
				g.drawString(tokens[0], Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
				g.drawString(tokens[1], Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
			}
			catch (Exception e) {
			}
		}
		else
			tick++;
		
	}
	
	@Override
	public SubMenu initialize(Keys keys) {
		// TODO: Add new inventory art for background.
		this.keys = keys;
		return Inventory.this;
	}
	
	@Override
	public void tick() {
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
			List<Map.Entry<Item, Integer>> list = this.getCurrentList();
			if (itemCursor < list.size() - 1) {
				itemCursor++;
				if (arrowPosition < 4)
					arrowPosition++;
			}
			this.keys.down.lastKeyState = true;
			this.keys.S.lastKeyState = true;
		}
		if ((this.keys.left.keyStateDown || this.keys.A.keyStateDown) && (!this.keys.left.lastKeyState || !this.keys.A.lastKeyState)) {
			this.category = Category.getWrapped(this.category.getID() - 1);
			this.tick = 0x0;
			this.itemCursor = this.arrowPosition = 0;
			this.keys.left.lastKeyState = true;
			this.keys.A.lastKeyState = true;
		}
		if ((this.keys.right.keyStateDown || this.keys.D.keyStateDown) && (!this.keys.right.lastKeyState || !this.keys.D.lastKeyState)) {
			this.category = Category.getWrapped(this.category.getID() + 1);
			this.tick = 0x0;
			this.itemCursor = this.arrowPosition = 0;
			this.keys.right.lastKeyState = true;
			this.keys.D.lastKeyState = true;
		}
		if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && (!this.keys.X.lastKeyState || !this.keys.PERIOD.lastKeyState)) {
			this.keys.X.lastKeyState = true;
			this.keys.PERIOD.lastKeyState = true;
			this.resetCursor();
			this.subMenuActivation = false;
		}
		if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
			//TODO: Add a submenu and confirmation dialogues before initiating the doAction().
			this.keys.Z.lastKeyState = true;
			this.keys.SLASH.lastKeyState = true;
			Map.Entry<Item, Integer> entry = potions.get(itemCursor);
			entry.getKey().doAction();
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
			output.blit(Art.dialogue_pointer, 18 * MainComponent.GAME_SCALE, ((Tile.HEIGHT * this.arrowPosition)) + 12);
			switch (this.category) {
				case POTIONS:
				default:
					output.blit(Art.inventory_backpack_potions, 0, 8);
					output.blit(Art.inventory_tag_potions, 0, Tile.HEIGHT * 4 + 3);
					break;
				case KEYITEMS:
					output.blit(Art.inventory_backpack_keyItems, 0, 8);
					output.blit(Art.inventory_tag_keyItems, 0, Tile.HEIGHT * 4 + 3);
					break;
				case POKEBALLS:
					output.blit(Art.inventory_backpack_pokeballs, 0, 8);
					output.blit(Art.inventory_tag_pokeballs, 0, Tile.HEIGHT * 4 + 3);
					break;
				case TM_HM:
					output.blit(Art.inventory_backpack_TM_HM, 0, 8);
					output.blit(Art.inventory_tag_TM_HM, 0, Tile.HEIGHT * 4 + 3);
					break;
			}
			graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
			renderText(graphics);
		}
	}
	
	public void addItem(Item item) {
		boolean heldItemExists = false;
		List<Map.Entry<Item, Integer>> list = this.getItemCategoryList(item);
		for (int i = 0; i < list.size(); i++) {
			Map.Entry<Item, Integer> entry = list.get(i);
			if (entry.getKey().equals(item)) {
				entry.setValue(entry.getValue().intValue() + 1);
				heldItemExists = true;
				break;
			}
		}
		if (!heldItemExists)
			list.add(0, new AbstractMap.SimpleEntry<Item, Integer>(item, 1));
	}
	
	public void tossItem() {
		List<Map.Entry<Item, Integer>> list = this.getCurrentList();
		Map.Entry<Item, Integer> entry = list.get(itemCursor);
		if (entry.getValue() - 1 <= 0)
			list.remove(itemCursor);
		else
			entry.setValue(entry.getValue().intValue() - 1);
	}
	
	public void resetCursor() {
		this.itemCursor = 0;
		this.arrowPosition = 0;
		this.itemListSpan = 0;
		this.category = Category.POTIONS;
	}
	
	// ------------------------------------ PRIVATE METHODS -----------------------------------------
	
	private void renderListBox(BaseScreen output, int x, int y, int width, int height) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (i * Tile.WIDTH), (y * Tile.HEIGHT - 7) + (j * Tile.HEIGHT));
			}
		}
		for (int k = 0; k < width; k++)
			output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (k * Tile.WIDTH), (height * Tile.HEIGHT));
	}
	
	private List<Map.Entry<Item, Integer>> getCurrentList() {
		List<Map.Entry<Item, Integer>> result = null;
		switch (this.category) {
			case POTIONS:
				result = potions;
				break;
			case KEYITEMS:
				result = keyItems;
				break;
			case POKEBALLS:
				result = pokeballs;
				break;
			case TM_HM:
				result = TMs_HMs;
				break;
		}
		return result;
	}
	
	private List<Map.Entry<Item, Integer>> getItemCategoryList(Item item) {
		List<Map.Entry<Item, Integer>> result = null;
		switch (item.getCategory()) {
			case POTIONS:
				result = potions;
				break;
			case KEYITEMS:
				result = keyItems;
				break;
			case POKEBALLS:
				result = pokeballs;
				break;
			case TM_HM:
				result = TMs_HMs;
				break;
		}
		return result;
	}
}
