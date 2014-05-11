/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package dialogue;

import java.awt.Color;
import java.awt.Graphics;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import main.Game;
import main.Keys;
import main.MainComponent;
import resources.Art;
import screen.BaseScreen;
import submenu.DummyMenu;
import submenu.Inventory;
import abstracts.SubMenu;
import abstracts.Tile;
import entity.Player;

public class StartMenu {
	// Description area
	private static final int DESCRIPTION_FIRST_LINE_Y = (Tile.HEIGHT * 7) * MainComponent.GAME_SCALE + Tile.HEIGHT * 2;
	private static final int DESCRIPTION_SECOND_LINE_Y = (Tile.HEIGHT * 8) * MainComponent.GAME_SCALE + Tile.HEIGHT * 2;

	// String constants
	public static final String ITEM_NAME_BICYCLE = "BICYCLE";
	public static final String ITEM_NAME_INVENTORY = "INVENTORY";
	public static final String ITEM_NAME_EXIT = "EXIT";

	private boolean activation;
	private ArrayList<Map.Entry<Integer, SubMenu>> items = new ArrayList<Map.Entry<Integer, SubMenu>>();
	private int menuCursorPosition;
	private Keys keys;
	private Game game;
	private String[] tokens;
	private Map.Entry<Integer, SubMenu> actionEvent;
	private Inventory inventory;

	public StartMenu(Game game) {
		this.activation = false;
		this.menuCursorPosition = 0;
		this.game = game;
		this.keys = game.getPlayer().keys;
		this.actionEvent = null;
	}

	public StartMenu initialize() {
		SubMenu bicycle = new DummyMenu(ITEM_NAME_BICYCLE, "Use the bicycle", "Get off bicycle", this.game);
		inventory = (Inventory) new Inventory(ITEM_NAME_INVENTORY, "Open the bag.", "Open the bag.", this.game).initialize(keys);
		SubMenu exit = new DummyMenu(ITEM_NAME_EXIT, "Close this menu", "Close this menu", this.game);
		this.addMenuItem(bicycle);
		this.addMenuItem(inventory);
		this.addMenuItem(exit);
		return this;
	}

	public void addMenuItem(SubMenu SubMenu) {
		this.items.add(new AbstractMap.SimpleEntry<Integer, SubMenu>(items.size(), SubMenu));
	}

	public void removeMenuItem(int position) {
		for (int i = 0; i < items.size(); i++) {
			Map.Entry<Integer, SubMenu> entry = items.get(i);
			if (entry.getKey().intValue() == position) {
				items.remove(i);
				if (this.menuCursorPosition > 0)
					this.menuCursorPosition--;
				break;
			}
		}
	}

	public void tick() {
		// if (!this.keys.START.lastKeyState && this.keys.START.keyStateDown) {
		// if (!Player.isMovementsLocked())
		// Player.lockMovements();
		// final Player player = this.game.getPlayer();
		// if (!player.isLockedWalking() && !player.isLockedJumping()) {
		// activation = !activation;
		// this.menuCursorPosition = 0;
		// }
		// this.keys.START.lastKeyState = true;
		// }
		if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && this.activation)
			this.activation = false;
		if (this.activation) {
			prepareMenuText();
			handleMenuSelection();
		} else if (Player.isMovementsLocked())
			Player.unlockMovements();
	}

	public void render(BaseScreen output, Graphics graphics) {
		if (this.activation) {
			Dialogue.renderBox(output, 5, 0, 4, items.size());
			StartMenu.renderDescriptionBox(output, 0, 7, 5, 3);
			output.blit(Art.dialogue_pointer, Tile.WIDTH * 5 + 8, Tile.HEIGHT + this.menuCursorPosition * Tile.HEIGHT);
			graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
			this.renderMenuText(graphics);
			this.renderMenuDescriptionText(graphics);
		}
	}

	public Map.Entry<Integer, SubMenu> getActionEvent() {
		return this.actionEvent;
	}

	public boolean isActionEventAvailable() {
		return (this.actionEvent != null);
	}

	public void clearActionEvent() {
		this.actionEvent = null;
	}

	public void closeMenu() {
		this.activation = false;
		if (Player.isMovementsLocked())
			Player.unlockMovements();
	}

	public void openMenu() {
		this.activation = true;
		this.menuCursorPosition = 0;
	}

	public boolean isActivated() {
		return this.activation;
	}

	public SubMenu getSubMenu() {
		return this.items.get(this.menuCursorPosition).getValue();
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	// ------------------------- PRIVATE METHODS -----------------------------------
	private void prepareMenuText() {
		Map.Entry<Integer, SubMenu> entry = this.items.get(this.menuCursorPosition);
		SubMenu item = entry.getValue();
		// TODO: Make this modular.
		if (item.getName().equals(StartMenu.ITEM_NAME_BICYCLE)) {
			Player player = this.game.getPlayer();
			if (player.isRidingBicycle())
				item.toggleDescription(false);
			else
				item.toggleDescription(true);
		}
		this.tokens = Dialogue.toLines(item.getDescription(), Dialogue.HALF_STRING_LENGTH);
	}

	private void handleMenuSelection() {
		if (!Player.isMovementsLocked())
			Player.lockMovements();
		if (!this.keys.down.lastKeyState && this.keys.down.keyStateDown) {
			this.menuCursorPosition++;
			if (this.menuCursorPosition > this.items.size() - 1)
				this.menuCursorPosition = 0;
			this.keys.down.lastKeyState = true;
		} else if (!this.keys.up.lastKeyState && this.keys.up.keyStateDown) {
			this.menuCursorPosition--;
			if (this.menuCursorPosition < 0)
				this.menuCursorPosition = this.items.size() - 1;
			this.keys.up.lastKeyState = true;
		}

		// Menu input mechanism
		if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
			this.keys.Z.lastKeyState = true;
			this.keys.SLASH.lastKeyState = true;
			this.actionEvent = items.get(menuCursorPosition);
			this.activation = true;
		}
	}

	private void renderMenuText(Graphics g) {
		g.setFont(Art.font);
		g.setColor(Color.black);
		if (this.activation) {
			for (int i = 0; i < this.items.size(); i++) {
				// TODO: We need to have an arrow pointing at the menu items.
				g.drawString(this.items.get(i).getValue().getName(), MainComponent.GAME_SCALE * (Tile.WIDTH * 6), (((Tile.HEIGHT * 2 - 8) + i * 16) * MainComponent.GAME_SCALE));
			}
		}
	}

	private void renderMenuDescriptionText(Graphics g) {
		g.setFont(Art.font);
		g.setColor(Color.black);
		try {
			g.drawString(tokens[0], 0, StartMenu.DESCRIPTION_FIRST_LINE_Y);
			g.drawString(tokens[1], 0, StartMenu.DESCRIPTION_SECOND_LINE_Y);
		} catch (Exception e) {
		}
	}

	// ------------------------- STATIC METHODS --------------------------------------

	public static void renderDescriptionBox(BaseScreen output, int x, int y, int width, int height) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (i * Tile.WIDTH), ((y - 1) * Tile.HEIGHT + 8) + j * Tile.HEIGHT);
			}
		}
	}
}
