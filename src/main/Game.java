/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package main;

import item.ActionItem;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import level.OverWorld;
import level.WorldConstants;
import resources.Mod;
import saving.GameSave;
import screen.BaseScreen;
import submenu.Save;
import abstracts.SubMenu;
import abstracts.World;
import dialogue.StartMenu;
import entity.Player;

public class Game {
	private static final String SAVE_FILE_NAME = "data.sav";
	private final BaseScreen screen;
	private final List<World> worlds;
	private StartMenu startMenu;
	private SubMenu subMenu;
	private World overworld;
	private final Player player;
	private ActionItem registeredItem;

	public enum State {
		GAME,
		PAUSED,
		INVENTORY,
		SAVE
	};

	private State state;

	/**
	 * Creates the core component of the game.
	 * 
	 * All future game components are to be placed here.
	 * 
	 * @param BaseScreen
	 *            Takes in a BaseScreen that displays all rendered graphics to the screen.
	 * @param Keys
	 *            Takes the Keys object the input handler receives from the player for the game to handle. The input handler must control this Keys object.
	 * @see BaseScreen
	 * @see NewInputHandler
	 * */
	public Game(MainComponent main, Keys input) {
		this.screen = main.getBaseScreen();
		this.player = new Player(input);
		this.player.setCenterCamPosition(this.screen);
		this.overworld = new OverWorld(player, this);
		this.worlds = new ArrayList<World>();
		this.worlds.add(this.overworld);
		this.startMenu = new StartMenu(this).initialize();
		this.subMenu = null;
		this.state = State.GAME;
	}

	/**
	 * Handles rendered objects.
	 * 
	 * <p>
	 * All render() methods must be placed in here to render to the screen.
	 * 
	 * <p>
	 * Methods placed in here are to be separated by game states, and depending on the game states, they are to be called when necessary.
	 * 
	 * @return Nothing.
	 * */
	public void render(Graphics graphics) {
		switch (this.state) {
			case GAME: {
				screen.clear(0xA4E767);
				overworld.render(screen, player.getX(), player.getY());
				break;
			}
			case INVENTORY: {
				if (screen.getRenderingEffectTick() < (byte) 0x7) {
					screen.flashing();
				}
				else {
					if (this.subMenu.equals(this.startMenu.getInventory()))
						this.subMenu = this.startMenu.getInventory();
					else
						this.subMenu = this.startMenu.getSubMenu();
					if (this.subMenu != null) {
						this.subMenu.render(screen, graphics);
					}
				}
				break;
			}
			case PAUSED: {
				if (screen.getRenderingEffectTick() < (byte) 0x7) {
					screen.flashing();
				}
				else {
					screen.clear(0xA4E767);
					overworld.render(screen, player.getX(), player.getY());
					if (startMenu.isActivated()) {
						startMenu.render(screen, graphics);
					}
				}
				break;
			}
			case SAVE: {
				screen.clear(0xA4E767);
				overworld.render(screen, player.getX(), player.getY());
				if (this.subMenu != null) {
					this.subMenu.render(screen, graphics);
				}
				break;
			}
		}
		graphics.drawImage(MainComponent.createCompatibleBufferedImage(screen.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
	}

	/**
	 * Updates the game.
	 * 
	 * All tick() methods must be placed in here, in order to correctly update the game.
	 * 
	 * @return Nothing.
	 * */
	public void tick() {

		// Debugging purposes
		if (this.player.keys.F1.keyStateDown && !(this.player.keys.F1.lastKeyState)) {
			this.player.keys.F1.lastKeyState = true;
			Mod.resetLoading();
			this.load();
		}
		// End debugging purposes

		switch (this.state) {
			case GAME: {
				overworld.tick();
				checkPausing();
				break;
			}
			case INVENTORY: {
				if (!this.subMenu.isActivated()) {
					this.state = State.PAUSED;
					screen.setRenderingEffectTick((byte) 0x0);
					break;
				}
				this.subMenu = this.startMenu.getSubMenu();
				if (!this.subMenu.isActivated())
					this.subMenu.enableSubMenu();
				this.subMenu.tick();
				break;
			}
			case PAUSED: {
				if (startMenu.isActivated())
					startMenu.tick();
				else
					startMenu.openMenu();
				checkUnpausing();
				if (startMenu.isActionEventAvailable())
					handleActionEvent(startMenu.getActionEvent());
				break;
			}
			case SAVE: {
				if (!this.subMenu.isActivated()) {
					final Save saveSubMenu = (Save) this.subMenu;
					if (saveSubMenu.getState() == Save.State.SAVED || saveSubMenu.getState() == Save.State.ERROR) {
						saveSubMenu.setState(Save.State.ASK);
						this.startMenu.closeMenu();
						this.state = State.GAME;
						break;
					}
					this.state = State.PAUSED;
					break;
				}
				if (this.subMenu != null) {
					this.subMenu.tick();
				}
				break;
			}
		}
	}

	/**
	 * Saves the game.
	 * */
	public void save() {
		GameSave.save(this, SAVE_FILE_NAME);
	}

	/**
	 * Checks for any previous saved data.
	 * 
	 * @return True, if it detects previous saved data. False, otherwise.
	 * */
	public boolean checkSaveData() {
		return GameSave.check(SAVE_FILE_NAME);
	}

	/**
	 * Currently unused.
	 * */
	public void load() {
		// TODO: Load data.
		this.screen.reload();
		Mod.loadModdedResources();
		if (WorldConstants.isModsEnabled == null)
			WorldConstants.isModsEnabled = Boolean.FALSE;
		player.reload();
		this.overworld = new OverWorld(player, this);
		this.state = State.GAME;
		GameSave.load(this, SAVE_FILE_NAME);
	}

	/**
	 * Currently unused.
	 * */
	public void setScrollOffset(int xCamCenter, int yCamCenter) {
		// CamCenter: the coordinates of the center of camera.
		// this.xScroll = xCamCenter;
		// this.yScroll = yCamCenter;
	}

	/**
	 * Currently unused. However, this is executed in the render() code.
	 * */
	public void setCameraRelativeToArea(int areaXPos, int areaYPos) {
		// Not used at the moment.

		// cam(x,y) = area(cam.x * -1 + xConstantOffset, cam.y * -1 + yConstantOffset)
		// this.xCamera = (-areaXPos + this.xScroll) / Tile.WIDTH;
		// this.yCamera = (-areaYPos + this.yScroll) / Tile.HEIGHT;
	}

	public Player getPlayer() {
		return this.player;
	}

	public StartMenu getStartMenu() {
		return this.startMenu;
	}

	public void setState(State state) {
		this.state = state;
	}

	public BaseScreen getBaseScreen() {
		return this.screen;
	}

	public void setRegisteredItem(ActionItem item) {
		this.registeredItem = item;
		// TODO: Continue to handle registered item's action event.
	}

	public boolean itemHasBeenRegistered(ActionItem item) {
		if (this.registeredItem == null)
			return false;
		return this.registeredItem.equals(item);

	}

	public World getWorld() {
		return this.overworld;
	}

	// ---------------------------------------------- PRIVATE METHODS -------------------------------------------------

	private void handleActionEvent(Map.Entry<Integer, SubMenu> entry) {
		String str = entry.getValue().getName();
		if (str.equals(StartMenu.ITEM_NAME_INVENTORY)) {
			if (this.state != State.INVENTORY)
				this.state = State.INVENTORY;
			this.subMenu = entry.getValue();
			if (!this.subMenu.isActivated())
				this.subMenu.enableSubMenu();
			screen.setRenderingEffectTick((byte) 0x0);
		}
		else if (str.equals(StartMenu.ITEM_NAME_EXIT)) {
			if (this.state != State.GAME)
				this.state = State.GAME;
			if (this.subMenu != null)
				this.subMenu.disableSubMenu();
			this.subMenu = null;
		}
		else if (str.equals(StartMenu.ITEM_NAME_SAVE)) {
			if (this.state != State.SAVE)
				this.state = State.SAVE;
			this.subMenu = entry.getValue();
			if (!this.subMenu.isActivated())
				this.subMenu.enableSubMenu();
		}
		this.startMenu.clearActionEvent();
		this.startMenu.closeMenu();
	}

	private void checkPausing() {
		Keys keys = this.player.keys;
		if (!keys.START.lastKeyState && keys.START.keyStateDown) {
			switch (this.state) {
				case GAME:
					if (this.player.isLockedWalking() || this.player.isLockedJumping()) {
						break;
					}
					this.state = State.PAUSED;
					if (!this.startMenu.isActivated())
						this.startMenu.openMenu();
					if (!Player.isMovementsLocked())
						Player.lockMovements();
					break;
				default:
					break;
			}
			keys.START.lastKeyState = true;
		}
	}

	private void checkUnpausing() {
		Keys keys = this.player.keys;
		switch (this.state) {
			case PAUSED:
				if ((!keys.START.lastKeyState && keys.START.keyStateDown) || ((keys.X.keyStateDown || keys.PERIOD.keyStateDown) && (!keys.X.lastKeyState || !keys.PERIOD.lastKeyState))) {
					this.state = State.GAME;
					if (Player.isMovementsLocked())
						Player.unlockMovements();
					keys.START.lastKeyState = true;
				}
				break;
			default:
				break;
		}
	}
}
