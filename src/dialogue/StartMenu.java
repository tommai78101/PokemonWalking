package dialogue;

import java.awt.Graphics;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import main.Game;
import main.Keys;
import main.MainComponent;
import abstracts.Tile;
import entity.Player;

public class StartMenu {
	//Description area
	private static final int DESCRIPTION_STARTING_X = Dialogue.TEXT_SPACING_WIDTH * MainComponent.GAME_SCALE;
	private static final int DESCRIPTION_FIRST_LINE_Y = 120 * MainComponent.GAME_SCALE;
	private static final int DESCRIPTION_SECOND_LINE_Y = 136 * MainComponent.GAME_SCALE;
	
	//String constants
	private static final String ITEM_BICYCLE = "BICYCLE";
	
	private boolean activation;
	private ArrayList<Map.Entry<Integer, MenuItem>> items = new ArrayList<Map.Entry<Integer, MenuItem>>();
	private int menuCursorPosition;
	private Keys keys;
	private Game game;
	private String[] tokens;
	private Map.Entry<Integer, MenuItem> actionEvent;
	
	public StartMenu(Game game) {
		this.activation = false;
		this.menuCursorPosition = 0;
		this.game = game;
		this.keys = game.getPlayer().keys;
		this.actionEvent = null;
	}
	
	public void addMenuItem(MenuItem menuItem) {
		this.items.add(new AbstractMap.SimpleEntry<Integer, MenuItem>(items.size(), menuItem));
	}
	
	public void removeMenuItem(int position) {
		for (int i = 0; i < items.size(); i++) {
			Map.Entry<Integer, MenuItem> entry = items.get(i);
			if (entry.getKey().intValue() == position) {
				items.remove(i);
				if (this.menuCursorPosition > 0)
					this.menuCursorPosition--;
				break;
			}
		}
	}
	
	public void renderMenuText(Graphics g) {
		if (this.activation) {
			for (int i = 0; i < this.items.size(); i++) {
				//TODO: We need to have an arrow pointing at the menu items.
				g.drawString(this.items.get(i).getValue().getDescription(), MainComponent.GAME_SCALE * (Tile.WIDTH * 6), (((Tile.HEIGHT * 2 - 8) + i * 16) * MainComponent.GAME_SCALE));
			}
		}
	}
	
	public void renderMenuDescriptionText(Graphics g) {
		try {
			g.drawString(tokens[0], StartMenu.DESCRIPTION_STARTING_X, StartMenu.DESCRIPTION_FIRST_LINE_Y);
			g.drawString(tokens[1], StartMenu.DESCRIPTION_STARTING_X, StartMenu.DESCRIPTION_SECOND_LINE_Y);
		}
		catch (Exception e) {
		}
	}
	
	public void tick() {
		if (!this.keys.START.lastKeyState && this.keys.START.keyStateDown) {
			activation = !activation;
			this.keys.START.lastKeyState = true;
		}
		if ((this.keys.X.keyStateDown || this.keys.PERIOD.keyStateDown) && this.activation)
			this.activation = false;
		if (this.activation) {
			prepareMenuText();
			handleMenuSelection();
		}
	}
	
	public Map.Entry<Integer, MenuItem> getActionEvent() {
		return this.actionEvent;
	}
	
	public boolean isActionEventAvailable() {
		return (this.actionEvent != null);
	}
	
	//-------------------------  PRIVATE METHODS  -----------------------------------
	private void prepareMenuText() {
		Map.Entry<Integer, MenuItem> entry = this.items.get(this.menuCursorPosition);
		MenuItem item = entry.getValue();
		//TODO: Make this modular.
		if (item.getName().equals(StartMenu.ITEM_BICYCLE)) {
			Player player = this.game.getPlayer();
			if (player.isRidingBicycle())
				item.toggleDescription(false);
			else
				item.toggleDescription(true);
			this.tokens = Dialogue.toLines(item.getDescription(), Dialogue.HALF_STRING_LENGTH);
		}
	}
	
	private void handleMenuSelection() {
		if (!Player.isMovementsLocked())
			Player.lockMovements();
		if (!this.keys.down.lastKeyState && this.keys.down.keyStateDown) {
			this.menuCursorPosition++;
			if (this.menuCursorPosition > this.items.size() - 1)
				this.menuCursorPosition = 0;
			this.keys.down.lastKeyState = true;
		}
		else if (!this.keys.up.lastKeyState && this.keys.up.keyStateDown) {
			this.menuCursorPosition--;
			if (this.menuCursorPosition < 0)
				this.menuCursorPosition = this.items.size() - 1;
			this.keys.up.lastKeyState = true;
		}
		
		//Menu input mechanism
		if ((this.keys.Z.keyStateDown || this.keys.SLASH.keyStateDown) && (!this.keys.Z.lastKeyState || !this.keys.SLASH.lastKeyState)) {
			this.actionEvent = items.get(menuCursorPosition);
			this.keys.Z.lastKeyState = true;
			this.keys.SLASH.lastKeyState = true;
			this.activation = true;
		}
	}
	
}
