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

import item.ActionItem;
import item.Bicycle;
import item.DummyItem;
import item.ItemText;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
import dialogue.NewDialogue;
import entity.Player;

public class Inventory extends SubMenu {
	
	private enum State {
		SELECTION, MENU, USE, TOSS, SET
	};
	
	private Keys keys;
	private final ArrayList<Map.Entry<Item, Integer>> potions;
	private final ArrayList<Map.Entry<Item, Integer>> keyItems;
	private final ArrayList<Map.Entry<Item, Integer>> pokéballs;
	private final ArrayList<Map.Entry<Item, Integer>> TMs_HMs;
	private final ArrayList<String> selectionMenu;
	private int itemCursor;
	private int arrowPosition;
	private int itemListSpan = 0;
	private Category category;
	private byte tick = (byte) 0x0;
	private State state;
	private int stateArrowPosition = 0;
	private int amountToToss = 0;
	private int set_tokenIterator = 0;
	private boolean set_end;
	private int set_subStringIterator = 0;
	private final ArrayList<String> set_completedLines;
	private Thread inventoryDialogueThread;
	
	public static final String MENU_USE = "USE";
	public static final String MENU_SET = "SET";
	public static final String MENU_TOSS = "TOSS";
	public static final String MENU_CANCEL = "CANCEL";
	
	/**
	 * Creates the Inventory submenu, with all the default settings.
	 * 
	 * @param name
	 *            The submenu title that is to be displayed in the start menu.
	 * @param enabled
	 *            The description that is to be shown when the submenu is activated/enabled.
	 * @param disabled
	 *            The description that is to be shown when the submenu is deactivated/disabled.
	 * @param game
	 *            The Game object that controls most of the actions/events the player has done when managing/using the Inventory.
	 * @return Nothing. It's a constructor after all.
	 * */
	public Inventory(String name, String enabled, String disabled, Game game) {
		super(name, enabled, disabled, game);
		this.itemCursor = 0;
		this.potions = new ArrayList<Map.Entry<Item, Integer>>();
		this.keyItems = new ArrayList<Map.Entry<Item, Integer>>();
		this.pokéballs = new ArrayList<Map.Entry<Item, Integer>>();
		this.TMs_HMs = new ArrayList<Map.Entry<Item, Integer>>();
		this.selectionMenu = new ArrayList<String>();
		ItemText itemText = null;
		for (Map.Entry<ItemText, Item> e: WorldConstants.items){
			if (e.getKey().id == WorldConstants.ITEM_RETURN){
				itemText = e.getKey();
				break;
			}
		}
		Item returnExit = new DummyItem(game, itemText.itemName, itemText.description, null, 0);
		this.potions.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.keyItems.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.pokéballs.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.TMs_HMs.add(new AbstractMap.SimpleEntry<Item, Integer>(returnExit, Integer.MAX_VALUE));
		this.arrowPosition = 0;
		this.category = Category.POTIONS;
		this.state = State.SELECTION;
		this.set_completedLines = new ArrayList<String>();
	}
	
	/**
	 * Adds an item with its text description into the Inventory, being categorized into its relevant "pocket" of the player's bag.
	 * 
	 * @param itemText
	 *            The item description of the item that is to be added into the Inventory.
	 * @param item
	 *            The item object that is to be added into the Inventory.
	 * @return Nothing.
	 * */
	public void addItem(ItemText itemText) {
		boolean heldItemExists = false;
		ArrayList<Map.Entry<Item, Integer>> list = this.getItemCategoryList(itemText);
		CHECK_LOOP:
		for (int i = 0; i < list.size(); i++) {
			Map.Entry<Item, Integer> entry = list.get(i);
			if (entry.getKey().getID() == itemText.id) {
				switch (entry.getKey().getCategory()) {
					case KEYITEMS: {
						if (entry.getValue() < 1) {
							heldItemExists = false;
							break CHECK_LOOP;
						}
						break;
					}
					case POKEBALLS:
					case POTIONS:
					case TM_HM:
						entry.setValue(entry.getValue().intValue() + 1);
						break;
				}
				heldItemExists= true;
				break;
			}
		}
		if (!heldItemExists) {
			Item item = null;
			switch (itemText.type) {
				case DUMMY:
					item = new DummyItem(this.game, itemText);
					list.add(0, new AbstractMap.SimpleEntry<Item, Integer>(item, 1));
					break;
				case ACTION: {
					// Action items must have ID, else it would be a dummy item.
					switch (itemText.id) {
						case WorldConstants.ITEM_BICYCLE:
							item = new Bicycle(this.game, itemText);
							list.add(0, new AbstractMap.SimpleEntry<Item, Integer>(item, 1));
							break;
						default:
							// Dummy item creation.
							item = new DummyItem(this.game, itemText);
							list.add(0, new AbstractMap.SimpleEntry<Item, Integer>(item, 1));
							break;
					}
					break;
				}
				default:
					// Nothing to see here.
					break;
			}
			
		}
	}
	
	/**
	 * Initializes the Inventory with the inputs.
	 * 
	 * <p>
	 * Does not initialize anything else.
	 * 
	 * @param keys
	 *            The input keys the player is using.
	 * @return Itself.
	 * */
	@Override
	public SubMenu initialize(Keys keys) {
		// TODO: Add new inventory art for background.
		this.keys = keys;
		return Inventory.this;
	}
	
	/**
	 * Renders the Inventory to the screen.
	 * 
	 * <p>
	 * Note that it doesn't render the {@link screen.BaseScreen#getBufferedImage() BufferedImage} to the actual {@link main.MainComponent#getBufferStrategy() BufferStrategy}.
	 * 
	 * @param output
	 *            The display that is to be rendered.
	 * @param graphics
	 *            The Graphics object that the main component creates using {@link java.awt.Canvas#getBufferStrategy() BufferStrategy} object.
	 * @return Nothing.
	 * */
	@Override
	public void render(BaseScreen output, Graphics graphics) {
		// WARNING: Due to the way it was rendered, the most direct method of rendering is used.
		// Do not edit the order or shorten it into multiple calls of private methods, it has been done before, and has made this problem even harder to solve.
		if (this.subMenuActivation) {
			switch (this.state) {
				default: {
					output.blit(Art.inventory_gui, 0, 0);
					NewDialogue.renderDialogBox(output, 0, 6, 9, 2);
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
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					renderText(g2d);
					g2d.dispose();
					break;
				}
				case MENU: {
					output.blit(Art.inventory_gui, 0, 0);
					NewDialogue.renderDialogBox(output, 0, 6, 9, 2);
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
					BufferedImage old = output.getBufferedImage();
					Graphics2D g2d = old.createGraphics();
					renderText(g2d);
					if (!this.selectionMenu.isEmpty()) {
						NewDialogue.renderDialogBox(output, 5, 5 - (this.selectionMenu.size() - 1), 4, this.selectionMenu.size() - 1);
						output.blit(Art.dialogue_pointer, 30 * MainComponent.GAME_SCALE, (12 * this.stateArrowPosition + Tile.HEIGHT * (7 - this.selectionMenu.size())) - 8);
						ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
						renderItemMenuText(list, g2d);
					}
					g2d.dispose();
					break;
				}
				case TOSS: {
					output.blit(Art.inventory_gui, 0, 0);
					NewDialogue.renderDialogBox(output, 0, 6, 9, 2);
					renderListBox(output, 3, 1, 7, 5);
					output.blit(Art.dialogue_pointer, 18 * MainComponent.GAME_SCALE, ((Tile.HEIGHT * this.arrowPosition)) + 12);
					NewDialogue.renderDialogBox(output, 5, 4, 4, 1);
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
					BufferedImage old = output.getBufferedImage();
					Graphics2D g2d = old.createGraphics();
					renderText(g2d);
					ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
					renderItemMenuText(list, g2d);
					g2d.dispose();
					break;
				}
				case SET: {
					output.blit(Art.inventory_gui, 0, 0);
					NewDialogue.renderDialogBox(output, 0, 6, 9, 2);
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
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					renderText(g2d);
					g2d.dispose();
					break;
				}
			}
		}
	}
	
	/**
	 * Resets the position of the pointer that points to the items.
	 * 
	 * @return Nothing.
	 * */
	public void resetCursor() {
		this.itemCursor = 0;
		this.arrowPosition = 0;
		this.itemListSpan = 0;
		this.resetSelectionCursor();
		this.category = Category.POTIONS;
		this.state = State.SELECTION;
	}
	
	/**
	 * Resets the position of the pointer and delete the selection menu commands of the item that the player is allowed to do.
	 * 
	 * @return Nothing.
	 * */
	public void resetSelectionCursor() {
		this.selectionMenu.clear();
		this.stateArrowPosition = 0;
	}
	
	/**
	 * Updates the Inventory each tick.
	 * 
	 * @return Nothing.
	 * */
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
					ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
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
					this.keys.Z.lastKeyState = true;
					this.keys.SLASH.lastKeyState = true;
					
					// This state is used when the player has selected an item, and wants to do something to the item.
					// Example, using the item, tossing the item, etc.
					ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
					Map.Entry<Item, Integer> entry = list.get(itemCursor);
					if (entry.getKey().getCategory() == null && entry.getValue() == Integer.MAX_VALUE) {
						// If getCategory() returns null, it is not an item.
						// Return option.
						this.resetCursor();
						this.subMenuActivation = false;
						break;
					}
					this.state = State.MENU;
					this.resetSelectionCursor();
					if (this.selectionMenu.isEmpty()) {
						Item item = list.get(itemCursor).getKey();
						if (item != null && list.get(itemCursor).getValue() != Integer.MAX_VALUE) {
							switch (item.getCategory()) {
								case POTIONS:
								case POKEBALLS:
								case TM_HM:
									this.selectionMenu.addAll(item.getAvailableCommands());
									break;
								case KEYITEMS:
									this.selectionMenu.addAll(item.getAvailableCommands());
									break;
							}
						}
					}
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
						this.state = State.SET;
						this.resetSelectionCursor();
						this.set_end = false;
						this.set_tokenIterator = 0;
						this.set_subStringIterator = 0;
						this.set_completedLines.clear();
					}
				}
				break;
			}
			case USE: {
				ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
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
					ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
					Map.Entry<Item, Integer> entry = list.get(itemCursor);
					if (entry.getValue() > this.amountToToss)
						this.amountToToss++;
				}
				else if ((this.keys.down.keyStateDown || this.keys.S.keyStateDown) && (!this.keys.down.lastKeyState || !this.keys.S.lastKeyState)) {
					this.keys.down.lastKeyState = true;
					this.keys.S.lastKeyState = true;
					if (this.amountToToss > 0)
						this.amountToToss--;
				}
				else if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && (!this.keys.X.lastKeyState || !this.keys.PERIOD.lastKeyState)) {
					this.keys.X.lastKeyState = true;
					this.keys.PERIOD.lastKeyState = true;
					this.resetSelectionCursor();
					this.state = State.MENU;
					if (this.selectionMenu.isEmpty()) {
						ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
						Item item = list.get(itemCursor).getKey();
						if (item != null && list.get(itemCursor).getValue() != Integer.MAX_VALUE) {
							switch (item.getCategory()) {
								case POTIONS:
								case POKEBALLS:
								case TM_HM:
									this.selectionMenu.addAll(item.getAvailableCommands());
									break;
								case KEYITEMS:
									this.selectionMenu.addAll(item.getAvailableCommands());
									break;
							}
						}
					}
				}
				else if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
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
			case SET: {
				Map.Entry<Item, Integer> entry = this.getCurrentList().get(itemCursor);
				ActionItem actionItem = (ActionItem) entry.getKey();
				if (!this.game.itemHasBeenRegistered(actionItem)) {
					this.game.setRegisteredItem(actionItem);
				}
				if (this.set_end) {
					if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
						this.keys.Z.lastKeyState = true;
						this.keys.SLASH.lastKeyState = true;
						this.resetSelectionCursor();
						this.state = State.SELECTION;
					}
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
		if (tick < 0xE)
			tick++;
	}
	
	/**
	 * Toss away an item.
	 * 
	 * <p>
	 * Can be put in a loop to toss away multiple items at the same time.
	 * 
	 * @return Nothing.
	 * */
	public void tossItem() {
		ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
		Map.Entry<Item, Integer> entry = list.get(itemCursor);
		if (entry.getValue() - 1 <= 0)
			list.remove(itemCursor);
		else
			entry.setValue(entry.getValue().intValue() - 1);
	}
	
	public List<ArrayList<Map.Entry<Item, Integer>>> getAllItemsList() {
		List<ArrayList<Map.Entry<Item, Integer>>> result = new ArrayList<ArrayList<Map.Entry<Item, Integer>>>();
		result.add(potions);
		result.add(keyItems);
		result.add(pokéballs);
		result.add(TMs_HMs);
		return result;
	}
	
	public ArrayList<Map.Entry<Item, Integer>> getPotions(){
		return this.potions;
	}
	
	public ArrayList<Map.Entry<Item, Integer>> getKeyItems(){
		return this.keyItems;
	}
	
	public ArrayList<Map.Entry<Item, Integer>> getPokeballs(){
		return this.pokéballs;
	}
	
	public ArrayList<Map.Entry<Item, Integer>> getTM_HM(){
		return this.TMs_HMs;
	}
	
	// ------------------------------------ PRIVATE METHODS -----------------------------------------
	
	/**
	 * Obtains the list of items the player is currently browsing in the Inventory.
	 * 
	 * @return A list of all the items and their corresponding amount of the items that the player is currently browsing in.
	 * */
	private ArrayList<Map.Entry<Item, Integer>> getCurrentList() {
		ArrayList<Map.Entry<Item, Integer>> result = null;
		switch (this.category) {
			case POTIONS:
				result = this.potions;
				break;
			case KEYITEMS:
				result = this.keyItems;
				break;
			case POKEBALLS:
				result = this.pokéballs;
				break;
			case TM_HM:
				result = this.TMs_HMs;
				break;
		}
		return result;
	}
	
	/**
	 * Obtains the list of items of the category the item belongs to in the Inventory.
	 * 
	 * @param item
	 *            The target item that is used to get the list of items that the targeted item belongs to.
	 * @return A list of all the items and their corresponding amount of the items that the targeted item belongs to.
	 * */
	private ArrayList<Map.Entry<Item, Integer>> getItemCategoryList(ItemText itemText) {
		ArrayList<Map.Entry<Item, Integer>> result = null;
		switch (itemText.category) {
			case POTIONS:
				result = potions;
				break;
			case KEYITEMS:
				result = keyItems;
				break;
			case POKEBALLS:
				result = pokéballs;
				break;
			case TM_HM:
				result = TMs_HMs;
				break;
		}
		return result;
	}
	
	/**
	 * Retrieves the list of available commands the item allows from the given list of items.
	 * 
	 * @param list
	 *            The list of all the items that is currently being active and browsed by the player.
	 * 
	 * @param graphics
	 *            The Graphics object that renders the custom font to the screen.
	 * 
	 * @return Nothing.
	 * */
	private void renderItemMenuText(List<Map.Entry<Item, Integer>> list, Graphics graphics) {
		switch (this.state) {
			case TOSS: {
				Item item = list.get(itemCursor).getKey();
				if (item.getCategory() == null)
					break;
				if (item != null && list.get(itemCursor).getValue() != Integer.MAX_VALUE && list.get(itemCursor).getValue() > 0 && this.selectionMenu.isEmpty()) {
					this.selectionMenu.add(0, "TOSS*");
				}
				
				graphics.setFont(Art.font.deriveFont(8f));
				graphics.setColor(Color.black);
				
				try {
					String tossAmount = amountToToss < 10 ? "0" + Integer.toString(amountToToss) : Integer.toString(amountToToss);
					graphics.drawString(this.selectionMenu.get(0), (4 * Tile.WIDTH + (3 * (Tile.WIDTH / 2))), (Tile.HEIGHT * 5 + 6));
					graphics.drawString(tossAmount, (4 * Tile.WIDTH + ((11 - tossAmount.length()) * (Tile.WIDTH / 2))), (Tile.HEIGHT * 5 + 6));
				}
				catch (Exception e) {
				}
				// This needs to be separated, else if there's a problem, the latter won't render anything.
				break;
			}
			case MENU: {
				Item item = list.get(itemCursor).getKey();
				if (item.getCategory() == null)
					break;
				if (item != null && list.get(itemCursor).getValue() != Integer.MAX_VALUE) {
					graphics.setFont(Art.font.deriveFont(8f));
					graphics.setColor(Color.black);
					try {
						for (int i = 0; i < this.selectionMenu.size(); i++) {
							graphics.drawString(this.selectionMenu.get(i), (Tile.WIDTH * 6 + 4), ((12 * i) + Tile.HEIGHT * (7 - this.selectionMenu.size())));
						}
					}
					catch (Exception e) {
					}
					// This needs to be separated, else if there's a problem, the latter won't render anything.
					break;
				}
			}
			default:
				break;
		}
		try {
			Map.Entry<Item, Integer> entry = list.get(itemCursor);
			ArrayList<Map.Entry<String, Boolean>> tokens = NewDialogue.toLines(entry.getKey().getDescription(), NewDialogue.MAX_STRING_LENGTH);
			graphics.drawString(tokens.get(0).getKey(), 8, 144 - 32 + 7);
			graphics.drawString(tokens.get(1).getKey(), 8, 144 - 16 + 7);
		}
		catch (Exception e) {
		}
	}
	
	/**
	 * Renders the background of the Inventory, which its rendering area is where the list of items are to be drawn on top of.
	 * 
	 * @param output
	 *            The screen that the game used to draw for the player to see.
	 * @param x
	 *            The X coordinates of where the text is to be drawn at.
	 * @param y
	 *            The Y coordinates of where the text is to be drawn at.
	 * @param width
	 *            The width of the list box that is to be drawn.
	 * @param height
	 *            The height of the lsit box that is too be drawn
	 * 
	 * @return Nothing.
	 * */
	private void renderListBox(BaseScreen output, int x, int y, int width, int height) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (i * Tile.WIDTH), (y * Tile.HEIGHT - 7) + (j * Tile.HEIGHT));
			}
		}
		for (int k = 0; k < width; k++)
			output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (k * Tile.WIDTH), (height * Tile.HEIGHT));
	}
	
	/**
	 * Sets the inventory state.
	 * 
	 * @return Nothing.
	 * */
	private void setState(State value) {
		this.state = value;
	}
	
	/**
	 * Renders all the texts that are to be drawn to the screen.
	 * 
	 * <p>
	 * Does not draw text for the dialogues.
	 * 
	 * @param g
	 *            The Graphics object that is passed to draw the text messages. It can be a Graphics object that is created from a BufferedImage, or a Graphics object that is created from a BufferStrategy().
	 * 
	 * @return Nothing.
	 */
	private void renderText(Graphics g) {
		g.setFont(Art.font.deriveFont(8f));
		g.setColor(Color.black);
		ArrayList<Map.Entry<Item, Integer>> list = this.getCurrentList();
		switch (this.state) {
			default: {
				if (tick >= (byte) 0x4) {
					try {
						for (int i = 0; i < 5; i++) {
							if (i >= list.size())
								break;
							Map.Entry<Item, Integer> entry = list.get(itemListSpan + i);
							g.drawString(entry.getKey().getName(), 8 * (Tile.WIDTH / 2), ((Tile.HEIGHT) + (Tile.HEIGHT * i)) + 3);
							int value = entry.getValue().intValue();
							if (value != Integer.MAX_VALUE && entry.getKey().getCategory() != null && this.category != Category.KEYITEMS) {
								String string = "*" + Integer.toString(value);
								g.drawString(string, 8 * (Tile.WIDTH / 2) + ((12 - string.length()) * (Tile.WIDTH / 2)), ((Tile.HEIGHT) + (Tile.HEIGHT * i)) + 4);
							}
						}
					}
					catch (Exception e) {
					}
					// This needs to be separated, else if there's a problem, the latter won't render anything.
					try {
						Map.Entry<Item, Integer> entry = list.get(itemCursor);
						ArrayList<Map.Entry<String, Boolean>> tokens = NewDialogue.toLines(entry.getKey().getDescription(), NewDialogue.MAX_STRING_LENGTH);
						g.drawString(tokens.get(0).getKey(), 8, 144 - 32 + 7);
						g.drawString(tokens.get(1).getKey(), 8, 144 - 16 + 7);
					}
					catch (Exception e) {
//						e.printStackTrace();
					}
				}
				break;
			}
			case SET: {
				try {
					for (int i = 0; i < 5; i++) {
						if (i >= list.size())
							break;
						Map.Entry<Item, Integer> entry = list.get(itemListSpan + i);
						g.drawString(entry.getKey().getName(), 8 * (Tile.WIDTH / 2), ((Tile.HEIGHT) + (Tile.HEIGHT * i)) + 3);
						int value = entry.getValue().intValue();
						if (value != Integer.MAX_VALUE && entry.getKey().getCategory() != null && this.category != Category.KEYITEMS) {
							String string = "*" + Integer.toString(value);
							g.drawString(string, 8 * (Tile.WIDTH / 2) + ((12 - string.length()) * (Tile.WIDTH / 2)), ((Tile.HEIGHT) + (Tile.HEIGHT * i)) + 4);
						}
					}
				}
				catch (Exception e) {
				}
				try {
					Map.Entry<Item, Integer> entry = list.get(itemCursor);
					ArrayList<Map.Entry<String, Boolean>> tokens = NewDialogue.toLines(entry.getKey().getName() + " has been registered.", NewDialogue.MAX_STRING_LENGTH);
					switch (this.set_completedLines.size()) {
						case 0:
							g.drawString(tokens.get(set_tokenIterator).getKey().substring(0, set_subStringIterator), 8, 144 - 32 + 7);
							break;
						case 1:
							g.drawString(this.set_completedLines.get(0), 8, 144 - 32 + 7);
							g.drawString(tokens.get(set_tokenIterator).getKey().substring(0, set_subStringIterator), 8, 144 - 16 + 7);
							break;
						case 2:
							g.drawString(this.set_completedLines.get(0), 8, 144 - 32 + 7);
							g.drawString(this.set_completedLines.get(1), 8, 144 - 16 + 7);
							break;
					}
					if (this.set_completedLines.size() < 2) {
						this.set_subStringIterator++;
						if (set_subStringIterator > tokens.get(set_tokenIterator).getKey().length()) {
							this.set_completedLines.add(tokens.get(set_tokenIterator).getKey());
							this.set_subStringIterator = 0;
							this.set_tokenIterator++;
						}
					}
					else {
						if (this.inventoryDialogueThread != null && this.inventoryDialogueThread.getState() == Thread.State.TERMINATED) {
							this.inventoryDialogueThread = null;
							this.set_end = true;
							return;
						}
						if (this.inventoryDialogueThread == null) {
							this.inventoryDialogueThread = new Thread(new Runnable() {
								@Override
								public void run() {
									Player.lockMovements();
									try {
										Thread.sleep(1500);
									}
									catch (InterruptedException e) {
									}
									setState(State.SELECTION);
									set_end = true;
									Player.unlockMovements();
								}
							});
							this.inventoryDialogueThread.setName("Inventory Dialogue Thread");
						}
						if (this.inventoryDialogueThread.getState() == Thread.State.NEW)
							this.inventoryDialogueThread.start();
					}
					break;
				}
				catch (Exception e) {
					if (this.inventoryDialogueThread != null && this.inventoryDialogueThread.getState() == Thread.State.TERMINATED) {
						this.inventoryDialogueThread = null;
						this.set_end = true;
						return;
					}
					if (this.inventoryDialogueThread == null) {
						this.inventoryDialogueThread = new Thread(new Runnable() {
							@Override
							public void run() {
								Player.lockMovements();
								try {
									Thread.sleep(2000);
								}
								catch (InterruptedException e) {
								}
								setState(State.SELECTION);
								set_end = true;
								Player.unlockMovements();
							}
						});
						this.inventoryDialogueThread.setName("Inventory Dialogue Thread");
					}
					if (this.inventoryDialogueThread.getState() == Thread.State.NEW)
						this.inventoryDialogueThread.start();
				}
			}
		}
		
	}
}
