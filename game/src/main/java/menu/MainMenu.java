/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import abstracts.Event;
import abstracts.SubMenu;
import common.Tileable;
import dialogue.Dialogue;
import entity.Player;
import event.MenuEvent;
import level.WorldConstants;
import main.Game;
import main.MainComponent;
import main.StateManager.GameState;
import resources.Art;
import screen.Scene;
import utility.DialogueBuilder;

public class MainMenu extends SubMenu {
	// Description area
	private static final int DESCRIPTION_FIRST_LINE_Y = (Tileable.HEIGHT * 8) - 8;
	private static final int DESCRIPTION_SECOND_LINE_Y = (Tileable.HEIGHT * 9) - 8;

	private List<SubMenu> items = new ArrayList<>();
	private SubMenu exitItem;
	private SubMenu activeMenuItem;
	private Dialogue mainMenuDialogue;

	private int menuCursorPosition;

	private boolean subMenuActivation;
	private List<Map.Entry<String, Boolean>> tokens;
	private MenuEvent actionEvent;

	public MainMenu() {
		super(null, null, GameState.START_MENU);
		this.menuCursorPosition = 0;
		this.actionEvent = null;
		this.needsFlashingAnimation = false;
		this.exitsToGame = true;
	}

	public MainMenu initialize(Game game) {
		this.mainMenuDialogue = new Dialogue();

		this.exitItem = new Exit(WorldConstants.MENU_ITEM_NAME_EXIT, WorldConstants.MENU_ITEM_DESC_EXIT, game);
		this.addMenuItem(game.getInventory());
		this.addMenuItem(game.getSaveManager());
		this.addMenuItem(this.exitItem);
		return this;
	}

	public void addMenuItem(SubMenu submenu) {
		if (!this.items.isEmpty()) {
			for (SubMenu entry : this.items) {
				if (entry.equals(submenu))
					return;
			}
		}
		this.items.add(submenu);
	}

	public void removeMenuItem(int position) {
		if (position < 0 || position >= this.items.size()) {
			return;
		}
		this.items = IntStream
			.range(0, this.items.size())
			.filter(i -> i != position)
			.mapToObj(i -> this.items.get(i))
			.collect(Collectors.toList());
		if (this.menuCursorPosition > 0)
			this.menuCursorPosition--;
	}

	@Override
	public void tick() {
		if (Game.keys.isStartPressed() || Game.keys.isSecondaryPressed()) {
			Game.keys.startReceived();
			Game.keys.secondaryReceived();
			this.exit();
			if (this.actionEvent != null)
				this.actionEvent = null;
			return;
		}
		this.prepareMenuText();
		this.handleMenuSelection();
//		if (Player.isMovementsLocked())
//			Player.unlockMovements();
	}

	@Override
	public void render(Scene output, Graphics graphics) {
		this.mainMenuDialogue.renderInformationBox(output, 5, 0, 4, this.items.size());
		MainMenu.renderDescriptionBox(output, 0, 7, 5, 3);
		output.blit(Art.dialogue_pointer, Tileable.WIDTH * 5 + 8, Tileable.HEIGHT + this.menuCursorPosition * Tileable.HEIGHT);
		Graphics2D g2d = output.getBufferedImage().createGraphics();
		this.renderMenuText(g2d);
		this.renderMenuDescriptionText(g2d);
		g2d.dispose();
		graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
	}

	@Override
	public Event getEvent() {
		return (this.activeMenuItem != null) ? this.activeMenuItem.getEvent() : null;
	}

//	public void closeMenu() {
//		if (Player.isMovementsLocked())
//			Player.unlockMovements();
//	}
//
//	public void openMenu() {
//		if (!Player.isMovementsLocked())
//			Player.lockMovements();
//	}

	/**
	 * Compares all available submenus before returning it.
	 * 
	 * <p>
	 * <b>Note:</b> There's a legitimate logic error resulting in submenus having different reference values in the Java VM existing in this method. If this method doesn't do any comparison beforehand, it will result in having the JVM using the wrong but correct references.
	 * 
	 * @return The chosen submenu that is equal to the submenu used for comparison.
	 */

//	public SubMenu getChosenSubMenu() {
//		return this.items.get(this.menuCursorPosition);
//		SubMenu menu = this.items.get(this.menuCursorPosition);
//		if (menu.equals(this.inventory))
//			menu = this.inventory;
//		else if (menu.equals(this.save))
//			menu = save;
//		return menu;
//	}

	public List<SubMenu> getSubMenusList() {
		return this.items;
	}

	public boolean isSubMenuActivated() {
		return this.subMenuActivation;
	}

	public void enableSubMenu() {
		this.subMenuActivation = true;
	}

	public void disableSubMenu() {
		this.subMenuActivation = false;
	}

	public void setActiveItem(SubMenu menu) {
		this.menuCursorPosition = 0;
		while (!menu.equals(this.items.get(this.menuCursorPosition))) {
			this.menuCursorPosition++;
		}
		this.activeMenuItem = menu;
	}

	public void setActiveItem(int index) {
		this.menuCursorPosition = index;
		this.activeMenuItem = this.items.get(index);
	}

	public void clearActiveItem() {
		this.activeMenuItem = null;
	}

	/**
	 * If no active item is found, it will return null.
	 * 
	 * @return A SubMenu object if there exists a player-chosen submenu item. Otherwise, null.
	 */
	public SubMenu getActiveItem() {
		return this.activeMenuItem;
	}

	// ------------------------- PRIVATE METHODS -----------------------------------

	private void prepareMenuText() {
		SubMenu item = this.items.get(this.menuCursorPosition);
		this.tokens = DialogueBuilder.toLines(item.getDescription(), Dialogue.HALF_STRING_LENGTH);
	}

	private void handleMenuSelection() {
		if (!Player.isMovementsLocked())
			Player.lockMovements();
		if (!Game.keys.down.lastKeyState && Game.keys.down.keyStateDown) {
			this.menuCursorPosition++;
			if (this.menuCursorPosition > this.items.size() - 1)
				this.menuCursorPosition = 0;
			Game.keys.down.lastKeyState = true;
		}
		else if (!Game.keys.up.lastKeyState && Game.keys.up.keyStateDown) {
			this.menuCursorPosition--;
			if (this.menuCursorPosition < 0)
				this.menuCursorPosition = this.items.size() - 1;
			Game.keys.up.lastKeyState = true;
		}

		// Menu input mechanism
		if ((Game.keys.Z.keyStateDown || Game.keys.SLASH.keyStateDown) && (!Game.keys.Z.lastKeyState || !Game.keys.SLASH.lastKeyState)) {
			Game.keys.Z.lastKeyState = true;
			Game.keys.SLASH.lastKeyState = true;
			this.activeMenuItem = this.items.get(this.menuCursorPosition);
//			event.trigger(() -> {
//				MenuEvent menuEvent = (MenuEvent) event;
//				Type type = menuEvent.getType();
//				switch (type) {
//					case INVENTORY:
//						this.stateManager.setCurrentGameState(GameState.INVENTORY);
//						this.gameScene.setRenderingEffectTick((byte) 0x0);
//						break;
//					case SAVE:
//						this.stateManager.setCurrentGameState(GameState.SAVING);
//						break;
//					case EXIT:
//						this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
//						this.startMenu.closeMenu();
//						break;
//					case UNUSED:
//						this.startMenu.closeMenu();
//						break;
//				}
//			});
		}
	}

	private void renderMenuText(Graphics g) {
		g.setFont(Art.font.deriveFont(8f));
		g.setColor(Color.black);
		for (int i = 0; i < this.items.size(); i++) {
			g.drawString(this.items.get(i).getName(), (Tileable.WIDTH * 6), (((Tileable.HEIGHT * 2 - 8) + i * 16)));
		}
	}

	private void renderMenuDescriptionText(Graphics g) {
		g.setFont(Art.font.deriveFont(8f));
		g.setColor(Color.black);
		try {
			g.drawString(this.tokens.get(0).getKey(), 0, MainMenu.DESCRIPTION_FIRST_LINE_Y);
			g.drawString(this.tokens.get(1).getKey(), 0, MainMenu.DESCRIPTION_SECOND_LINE_Y);
		}
		catch (Exception e) {}
	}

	// ------------------------- STATIC METHODS --------------------------------------

	public static void renderDescriptionBox(Scene output, int x, int y, int width, int height) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output.blit(Art.dialogue_background, (x * Tileable.WIDTH) + (i * Tileable.WIDTH), ((y - 1) * Tileable.HEIGHT + 8) + j * Tileable.HEIGHT);
			}
		}
	}
}
