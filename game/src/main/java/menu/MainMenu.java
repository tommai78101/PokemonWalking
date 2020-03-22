/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */
package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import abstracts.SubMenu;
import dialogue.Dialogue;
import entity.Player;
import interfaces.Tileable;
import level.WorldConstants;
import main.Game;
import main.MainComponent;
import resources.Art;
import screen.Scene;
import utility.DialogueBuilder;

public class MainMenu extends SubMenu {
	// Description area
	private static final int DESCRIPTION_FIRST_LINE_Y = (Tileable.HEIGHT * 8) - 8;
	private static final int DESCRIPTION_SECOND_LINE_Y = (Tileable.HEIGHT * 9) - 8;

	private List<SubMenu> items = new ArrayList<>();
	private SubMenu exitItem;
	private SubMenu activeItem;
	private Dialogue mainMenuDialogue;

	private int menuCursorPosition;

	private boolean subMenuActivation;
	private boolean activation;
	private List<Map.Entry<String, Boolean>> tokens;
	private MenuEvent actionEvent;

	public MainMenu() {
		super(null, null, Type.UNUSED);
		this.activation = false;
		this.menuCursorPosition = 0;
		this.actionEvent = null;
	}

	public MainMenu initialize(Game game) {
		this.mainMenuDialogue = new Dialogue();

		this.exitItem = new DummyMenu(WorldConstants.MENU_ITEM_NAME_EXIT, WorldConstants.MENU_ITEM_DESC_EXIT, game);
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
		if ((Game.keys.X.keyStateDown || Game.keys.PERIOD.keyStateDown) && this.activation)
			this.activation = false;
		if (this.activation) {
			this.prepareMenuText();
			this.handleMenuSelection();
		}
		else if (Player.isMovementsLocked())
			Player.unlockMovements();
	}

	@Override
	public void render(Scene output, Graphics graphics) {
		if (!this.activation) {
			return;
		}
		this.mainMenuDialogue.renderInformationBox(output, 5, 0, 4, this.items.size());
		MainMenu.renderDescriptionBox(output, 0, 7, 5, 3);
		output.blit(Art.dialogue_pointer, Tileable.WIDTH * 5 + 8, Tileable.HEIGHT + this.menuCursorPosition * Tileable.HEIGHT);
		Graphics2D g2d = output.getBufferedImage().createGraphics();
		this.renderMenuText(g2d);
		this.renderMenuDescriptionText(g2d);
		g2d.dispose();
		graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
	}

	public MenuEvent getActionEvent() {
		return (this.activeItem != null) ? this.activeItem.getEvent() : null;
	}

	public void closeMenu() {
		this.activation = false;
		if (Player.isMovementsLocked())
			Player.unlockMovements();
	}

	public void openMenu() {
		this.activation = true;
		if (!Player.isMovementsLocked())
			Player.lockMovements();
	}

	public boolean isActivated() {
		return this.activation;
	}

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

	public SubMenu getActiveItem() {
		return this.activeItem;
	}

	public void setActiveItem(SubMenu menu) {
		this.menuCursorPosition = 0;
		while (!menu.equals(this.items.get(this.menuCursorPosition))) {
			this.menuCursorPosition++;
		}
		this.activeItem = menu;
	}

	public void setActiveItem(int index) {
		this.menuCursorPosition = index;
		this.activeItem = this.items.get(index);
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
			this.actionEvent = this.items.get(this.menuCursorPosition).getEvent();
			this.activation = true;
		}
	}

	private void renderMenuText(Graphics g) {
		g.setFont(Art.font.deriveFont(8f));
		g.setColor(Color.black);
		if (this.activation) {
			for (int i = 0; i < this.items.size(); i++) {
				g.drawString(this.items.get(i).getName(), (Tileable.WIDTH * 6), (((Tileable.HEIGHT * 2 - 8) + i * 16)));
			}
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
