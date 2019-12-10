/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package dialogue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.Game;
import main.Keys;
import main.MainComponent;
import resources.Art;
import screen.BaseScreen;
import submenu.DummyMenu;
import submenu.Inventory;
import submenu.Save;
import abstracts.SubMenu;
import abstracts.Tile;
import entity.Player;

public class StartMenu {
	// Description area
	private static final int DESCRIPTION_FIRST_LINE_Y = (Tile.HEIGHT * 8) - 8;
	private static final int DESCRIPTION_SECOND_LINE_Y = (Tile.HEIGHT * 9) - 8;

	// String constants
	public static final String ITEM_NAME_EXIT = "EXIT";
	public static final String ITEM_NAME_INVENTORY = "PACK";
	public static final String ITEM_NAME_SAVE = "SAVE";

	private boolean activation;
	private ArrayList<Map.Entry<Integer, SubMenu>> items = new ArrayList<Map.Entry<Integer, SubMenu>>();
	private int menuCursorPosition;
	private Keys keys;
	private Game game;
	private ArrayList<Map.Entry<String, Boolean>> tokens;
	private Map.Entry<Integer, SubMenu> actionEvent;
	private Inventory inventory;
	private Save save;

	public StartMenu(Game game) {
		this.activation = false;
		this.menuCursorPosition = 0;
		this.game = game;
		this.keys = game.getPlayer().keys;
		this.actionEvent = null;
	}

	public StartMenu initialize() {
		inventory = (Inventory) new Inventory(ITEM_NAME_INVENTORY, "Open the bag.", "Open the bag.", this.game)
				.initialize(keys);
		save = (Save) new Save(ITEM_NAME_SAVE, "Save the game.", "Save the game.", this.game).initialize(keys);
		SubMenu exit = new DummyMenu(ITEM_NAME_EXIT, "Close this menu", "Close this menu", this.game);
		this.addMenuItem(inventory);
		this.addMenuItem(save);
		this.addMenuItem(exit);
		return this;
	}

	public void addMenuItem(SubMenu submenu) {
		if (!this.items.isEmpty()) {
			for (Map.Entry<Integer, SubMenu> entry : this.items) {
				if (entry.getValue().equals(submenu))
					return;
			}
		}
		this.items.add(new AbstractMap.SimpleEntry<Integer, SubMenu>(items.size(), submenu));
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
			NewDialogue.renderDialogBox(output, 5, 0, 4, items.size());
			StartMenu.renderDescriptionBox(output, 0, 7, 5, 3);
			output.blit(Art.dialogue_pointer, Tile.WIDTH * 5 + 8, Tile.HEIGHT + this.menuCursorPosition * Tile.HEIGHT);
			Graphics2D g2d = output.getBufferedImage().createGraphics();
			this.renderMenuText(g2d);
			this.renderMenuDescriptionText(g2d);
			g2d.dispose();
			graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0,
					MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
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

	/**
	 * Compares all available submenus before returning it.
	 * 
	 * <p>
	 * <b>Note:</b> There's a legitimate logic error resulting in submenus having
	 * different reference values in the Java VM existing in this method. If this
	 * method doesn't do any comparison beforehand, it will result in having the JVM
	 * using the wrong but correct references.
	 * 
	 * @return The chosen submenu that is equal to the submenu used for comparison.
	 */
	public SubMenu getSubMenu() {
		SubMenu menu = this.items.get(this.menuCursorPosition).getValue();
		if (menu.equals(this.inventory))
			menu = this.inventory;
		else if (menu.equals(this.save))
			menu = save;
		return menu;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public List<Map.Entry<Integer, SubMenu>> getSubMenusList() {
		return this.items;
	}

	// ------------------------- PRIVATE METHODS -----------------------------------
	private void prepareMenuText() {
		Map.Entry<Integer, SubMenu> entry = this.items.get(this.menuCursorPosition);
		SubMenu item = entry.getValue();
		this.tokens = NewDialogue.toLines(item.getDescription(), NewDialogue.HALF_STRING_LENGTH);
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
		if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown)
				&& (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
			this.keys.Z.lastKeyState = true;
			this.keys.SLASH.lastKeyState = true;
			this.actionEvent = items.get(menuCursorPosition);
			this.activation = true;
		}
	}

	private void renderMenuText(Graphics g) {
		g.setFont(Art.font.deriveFont(8f));
		g.setColor(Color.black);
		if (this.activation) {
			for (int i = 0; i < this.items.size(); i++) {
				g.drawString(this.items.get(i).getValue().getName(), (Tile.WIDTH * 6),
						(((Tile.HEIGHT * 2 - 8) + i * 16)));
			}
		}
	}

	private void renderMenuDescriptionText(Graphics g) {
		g.setFont(Art.font.deriveFont(8f));
		g.setColor(Color.black);
		try {
			g.drawString(tokens.get(0).getKey(), 0, StartMenu.DESCRIPTION_FIRST_LINE_Y);
			g.drawString(tokens.get(1).getKey(), 0, StartMenu.DESCRIPTION_SECOND_LINE_Y);
		} catch (Exception e) {
		}
	}

	// ------------------------- STATIC METHODS
	// --------------------------------------

	public static void renderDescriptionBox(BaseScreen output, int x, int y, int width, int height) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output.blit(Art.dialogue_background, (x * Tile.WIDTH) + (i * Tile.WIDTH),
						((y - 1) * Tile.HEIGHT + 8) + j * Tile.HEIGHT);
			}
		}
	}
}
