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
import abstracts.Item.Category;
import abstracts.SubMenu;
import abstracts.Tile;
import dialogue.Dialogue;

public class Inventory extends SubMenu {
	
	private enum State {
		SELECTION, MENU, USE, TOSS
	};
	
	private Keys keys;
	private List<Map.Entry<Item, Integer>> potions;
	private List<Map.Entry<Item, Integer>> keyItems;
	private List<Map.Entry<Item, Integer>> pokeballs;
	private List<Map.Entry<Item, Integer>> TMs_HMs;
	private List<String> selectionMenu;
	private int itemCursor;
	private int arrowPosition;
	private int itemListSpan = 0;
	private Category category;
	private byte tick = (byte) 0x0;
	private State state;
	private int stateArrowPosition = 0;
	private int amountToToss = 0;
	
	public static final String MENU_USE = "USE";
	public static final String MENU_SET = "SET";
	public static final String MENU_TOSS = "TOSS";
	public static final String MENU_CANCEL = "CANCEL";
	
	// TODO: Continue to work on this.
	public Inventory(String name, String enabled, String disabled, Game game) {
		super(name, enabled, disabled, game);
		this.itemCursor = 0;
		this.potions = new ArrayList<Map.Entry<Item, Integer>>();
		this.keyItems = new ArrayList<Map.Entry<Item, Integer>>();
		this.pokeballs = new ArrayList<Map.Entry<Item, Integer>>();
		this.TMs_HMs = new ArrayList<Map.Entry<Item, Integer>>();
		this.selectionMenu = new ArrayList<String>();
		ItemText itemText = WorldConstants.items.get(WorldConstants.ITEM_RETURN);
		Item returnExit = new DummyItem(game, itemText.itemName, itemText.description, null);
		this.potions.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.keyItems.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.pokeballs.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.TMs_HMs.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.arrowPosition = 0;
		this.category = Category.POTIONS;
		this.state = State.SELECTION;
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
					if (value != Integer.MAX_VALUE && entry.getKey().getCategory() != null && this.category != Category.KEYITEMS) {
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
		switch (this.state) {
			case SELECTION: {
				if ((this.keys.up.keyStateDown || this.keys.W.keyStateDown) && (!this.keys.up.lastKeyState || !this.keys.W.lastKeyState)) {
					if (itemCursor > 0) {
						itemCursor--;
						if (arrowPosition > 0)
							arrowPosition--;
					}
					this.keys.up.lastKeyState = true;
					this.keys.W.lastKeyState = true;
				}
				else if ((this.keys.down.keyStateDown || this.keys.S.keyStateDown) && (!this.keys.down.lastKeyState || !this.keys.S.lastKeyState)) {
					List<Map.Entry<Item, Integer>> list = this.getCurrentList();
					if (itemCursor < list.size() - 1) {
						itemCursor++;
						if (arrowPosition < 4)
							arrowPosition++;
					}
					this.keys.down.lastKeyState = true;
					this.keys.S.lastKeyState = true;
				}
				else if ((this.keys.left.keyStateDown || this.keys.A.keyStateDown) && (!this.keys.left.lastKeyState || !this.keys.A.lastKeyState)) {
					this.category = Category.getWrapped(this.category.getID() - 1);
					this.tick = 0x0;
					this.itemCursor = this.arrowPosition = 0;
					this.keys.left.lastKeyState = true;
					this.keys.A.lastKeyState = true;
				}
				else if ((this.keys.right.keyStateDown || this.keys.D.keyStateDown) && (!this.keys.right.lastKeyState || !this.keys.D.lastKeyState)) {
					this.category = Category.getWrapped(this.category.getID() + 1);
					this.tick = 0x0;
					this.itemCursor = this.arrowPosition = 0;
					this.keys.right.lastKeyState = true;
					this.keys.D.lastKeyState = true;
				}
				else if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && (!this.keys.X.lastKeyState || !this.keys.PERIOD.lastKeyState)) {
					this.keys.X.lastKeyState = true;
					this.keys.PERIOD.lastKeyState = true;
					this.resetCursor();
					this.subMenuActivation = false;
				}
				else if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
					//TODO: Add a submenu and confirmation dialogues before initiating the doAction().
					this.keys.Z.lastKeyState = true;
					this.keys.SLASH.lastKeyState = true;
					
					//This state is used when the player has selected an item, and wants to do something to the item.
					//Example, using the item, tossing the item, etc.
					List<Map.Entry<Item, Integer>> list = this.getCurrentList();
					Map.Entry<Item, Integer> entry = list.get(itemCursor);
					if (entry.getKey().getCategory() == null && entry.getValue() == Integer.MAX_VALUE) {
						//If getCategory() returns null, it is not an item.
						//Return option.
						this.resetCursor();
						this.subMenuActivation = false;
						break;
					}
					this.state = State.MENU;
					this.resetSelectionCursor();
				}
				break;
			}
			case MENU: {
				if ((this.keys.up.keyStateDown || this.keys.W.keyStateDown) && (!this.keys.up.lastKeyState || !this.keys.W.lastKeyState)) {
					this.keys.up.lastKeyState = true;
					this.keys.W.lastKeyState = true;
					if (stateArrowPosition > 0)
						stateArrowPosition--;
				}
				else if ((this.keys.down.keyStateDown || this.keys.S.keyStateDown) && (!this.keys.down.lastKeyState || !this.keys.S.lastKeyState)) {
					this.keys.down.lastKeyState = true;
					this.keys.S.lastKeyState = true;
					if (stateArrowPosition < this.selectionMenu.size() - 1)
						stateArrowPosition++;
				}
				else if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && (!this.keys.X.lastKeyState || !this.keys.PERIOD.lastKeyState)) {
					this.keys.X.lastKeyState = true;
					this.keys.PERIOD.lastKeyState = true;
					this.state = State.SELECTION;
					this.resetSelectionCursor();
				}
				else if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
					//TODO: Add a submenu and confirmation dialogues before initiating the doAction().
					this.keys.Z.lastKeyState = true;
					this.keys.SLASH.lastKeyState = true;
					
					String command = this.selectionMenu.get(stateArrowPosition);
					if (command.equals(MENU_CANCEL)) {
						this.state = State.SELECTION;
						this.resetSelectionCursor();
					}
					else if (command.equals(MENU_TOSS)) {
						this.state = State.TOSS;
						this.resetSelectionCursor();
					}
					else if (command.equals(MENU_USE)) {
						this.state = State.USE;
						this.resetSelectionCursor();
					}
					else if (command.equals(MENU_SET)) {
						//TODO: Work more on "SET" command.
					}
				}
				break;
			}
			case USE: {
				List<Map.Entry<Item, Integer>> list = this.getCurrentList();
				Map.Entry<Item, Integer> entry = list.get(itemCursor);
				entry.getKey().doAction();
				this.resetCursor();
				this.subMenuActivation = false;
				break;
			}
			case TOSS: {
				if ((this.keys.up.keyStateDown || this.keys.W.keyStateDown) && (!this.keys.up.lastKeyState || !this.keys.W.lastKeyState)) {
					this.keys.up.lastKeyState = true;
					this.keys.W.lastKeyState = true;
					List<Map.Entry<Item, Integer>> list = this.getCurrentList();
					Map.Entry<Item, Integer> entry = list.get(itemCursor);
					if (entry.getValue() > this.amountToToss)
						this.amountToToss++;
				}
				else if ((this.keys.down.keyStateDown || this.keys.S.keyStateDown) && (!this.keys.down.lastKeyState || !this.keys.S.lastKeyState)) {
					this.keys.down.lastKeyState = true;
					this.keys.S.lastKeyState = true;
					List<Map.Entry<Item, Integer>> list = this.getCurrentList();
					Map.Entry<Item, Integer> entry = list.get(itemCursor);
					if (this.amountToToss > 0)
						this.amountToToss--;
				}
				else if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && (!this.keys.X.lastKeyState || !this.keys.PERIOD.lastKeyState)) {
					this.keys.X.lastKeyState = true;
					this.keys.PERIOD.lastKeyState = true;
					this.state = State.MENU;
				}
				else if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
					//TODO: Add a submenu and confirmation dialogues before initiating the doAction().
					this.keys.Z.lastKeyState = true;
					this.keys.SLASH.lastKeyState = true;
					for (int i = 0; i < this.amountToToss; i++) {
						this.tossItem();
					}
					this.resetSelectionCursor();
					this.state = State.SELECTION;
				}
				break;
			}
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
		//WARNING: Due to the way it was rendered, the most direct method of rendering is used.
		//Do not edit the order or shorten it into multiple calls of private methods, it has been done before, and has made this problem even harder to solve.
		if (this.subMenuActivation) {
			switch (this.state) {
				default: {
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
					break;
				}
				case MENU: {
					output.blit(Art.inventory_gui, 0, 0);
					Dialogue.renderBox(output, 0, 6, 9, 2);
					renderListBox(output, 3, 1, 7, 5);
					output.blit(Art.dialogue_pointer, 18 * MainComponent.GAME_SCALE, ((Tile.HEIGHT * this.arrowPosition)) + 12);
					Dialogue.renderBox(output, 5, 5 - (this.selectionMenu.size() - 1), 4, this.selectionMenu.size() - 1);
					output.blit(Art.dialogue_pointer, 30 * MainComponent.GAME_SCALE, (12 * this.stateArrowPosition + Tile.HEIGHT * (7 - this.selectionMenu.size())) - 8);
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
					List<Map.Entry<Item, Integer>> list = this.getCurrentList();
					renderItemMenuText(list, graphics);
					break;
				}
				case TOSS: {
					output.blit(Art.inventory_gui, 0, 0);
					Dialogue.renderBox(output, 0, 6, 9, 2);
					renderListBox(output, 3, 1, 7, 5);
					output.blit(Art.dialogue_pointer, 18 * MainComponent.GAME_SCALE, ((Tile.HEIGHT * this.arrowPosition)) + 12);
					Dialogue.renderBox(output, 5, 4, 4, 1);
					//output.blit(Art.dialogue_pointer, 30 * MainComponent.GAME_SCALE, ((12 * this.stateArrowPosition) + Tile.HEIGHT * 3 + 8));
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
					List<Map.Entry<Item, Integer>> list = this.getCurrentList();
					renderItemMenuText(list, graphics);
					break;
				}
			}
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
		this.resetSelectionCursor();
		this.category = Category.POTIONS;
		this.state = State.SELECTION;
	}
	
	public void resetSelectionCursor() {
		this.selectionMenu.clear();
		this.stateArrowPosition = 0;
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
	
	private void renderItemMenuText(List<Map.Entry<Item, Integer>> list, Graphics graphics) {
		switch (this.state) {
			case TOSS: {
				Item item = list.get(itemCursor).getKey();
				if (item.getCategory() == null)
					break;
				if (item != null && list.get(itemCursor).getValue() != Integer.MAX_VALUE && list.get(itemCursor).getValue() > 0 && this.selectionMenu.isEmpty()) {
					this.selectionMenu.add(0, "TOSS*");
				}
				
				graphics.setFont(Art.font);
				graphics.setColor(Color.black);
				
				try {
					String tossAmount = amountToToss < 10 ? "0" + Integer.toString(amountToToss) : Integer.toString(amountToToss);
					graphics.drawString(this.selectionMenu.get(0), (4 * Tile.WIDTH + (3 * Dialogue.TEXT_SPACING_WIDTH)) * MainComponent.GAME_SCALE, MainComponent.GAME_SCALE * (Tile.HEIGHT * 5 + 6));
					graphics.drawString(tossAmount, (4 * Tile.WIDTH + ((11 - tossAmount.length()) * Dialogue.TEXT_SPACING_WIDTH)) * MainComponent.GAME_SCALE, MainComponent.GAME_SCALE * (Tile.HEIGHT * 5 + 6));
				}
				catch (Exception e) {
				}
				//This needs to be separated, else if there's a problem, the latter won't render anything.
				break;
			}
			case MENU: {
				Item item = list.get(itemCursor).getKey();
				if (item.getCategory() == null)
					break;
				if (item != null && list.get(itemCursor).getValue() != Integer.MAX_VALUE) {
					switch (item.getCategory()) {
						case POTIONS:
						case POKEBALLS:
						case TM_HM:
							if (this.selectionMenu.isEmpty())
								this.selectionMenu.addAll(item.getAvailableCommands());
							break;
						case KEYITEMS:
							if (this.selectionMenu.isEmpty())
								this.selectionMenu.addAll(item.getAvailableCommands());
							break;
					}
					graphics.setFont(Art.font);
					graphics.setColor(Color.black);
					try {
						for (int i = 0; i < this.selectionMenu.size(); i++) {
							graphics.drawString(this.selectionMenu.get(i), MainComponent.GAME_SCALE * (Tile.WIDTH * 6 + 4), MainComponent.GAME_SCALE * ((12 * i) + Tile.HEIGHT * (7 - this.selectionMenu.size())));
						}
					}
					catch (Exception e) {
					}
					//This needs to be separated, else if there's a problem, the latter won't render anything.
					break;
				}
			}
			default:
				break;
		}
		try {
			Map.Entry<Item, Integer> entry = list.get(itemCursor);
			String[] tokens = Dialogue.toLines(entry.getKey().getDescription(), Dialogue.MAX_STRING_LENGTH);
			graphics.drawString(tokens[0], Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
			graphics.drawString(tokens[1], Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
		}
		catch (Exception e) {
		}
	}
}
