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

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import abstracts.SubMenu;
import abstracts.World;
import dialogue.StartMenu;
import entity.Player;
import item.ActionItem;
import level.OverWorld;
import level.WorldConstants;
import main.StateManager.GameState;
import resources.Mod;
import saving.GameSave;
import screen.Scene;
import submenu.Save;

public class Game {
	private static final String SAVE_FILE_NAME = "data.sav";
	private final Scene gameScene;
	private final List<World> worlds;
	private final Player player;
	private StartMenu startMenu;
	private SubMenu subMenu;
	private World overworld;
	private ActionItem registeredItem;
	private StateManager stateManager;

	/**
	 * Creates the core component of the game.
	 * 
	 * All future game components are to be placed here.
	 * 
	 * @param Scene
	 *            Takes in a BaseScreen that displays all rendered graphics to the screen.
	 * @param Keys
	 *            Takes the Keys object the input handler receives from the player for the game to handle. The input handler must control this Keys object.
	 * @see Scene
	 * @see NewInputHandler
	 */
	public Game(MainComponent main, Keys input) {
		this.gameScene = main.getScene();
		this.worlds = new ArrayList<>();
		this.player = new Player(input);

		this.initialize();
	}

	public void initialize() {
		WorldConstants.checkForMods();

		this.player.setCenterCamPosition(this.gameScene);
		this.worlds.add(this.overworld);
		this.startMenu = new StartMenu(this).initialize();
		this.subMenu = null;
		this.stateManager = new StateManager();

		this.load();
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
	 */
	public void render(Graphics graphics) {
		GameState state = this.stateManager.getCurrentGameState();
		switch (state) {
			case MAIN_GAME: {
				gameScene.clear(0xA4E767);
				overworld.render(gameScene, player.getX(), player.getY());
				break;
			}
			case START_MENU: {
				if (gameScene.getRenderingEffectTick() < (byte) 0x7) {
					gameScene.flashing();
				}
				else {
					if (this.subMenu.equals(this.startMenu.getInventory()))
						this.subMenu = this.startMenu.getInventory();
					else
						this.subMenu = this.startMenu.getSubMenu();
					if (this.subMenu != null) {
						this.subMenu.render(gameScene, graphics);
					}
				}
				break;
			}
			case INVENTORY: {
				if (gameScene.getRenderingEffectTick() < (byte) 0x7) {
					gameScene.flashing();
				}
				else {
					gameScene.clear(0xA4E767);
					overworld.render(gameScene, player.getX(), player.getY());
					if (startMenu.isActivated()) {
						startMenu.render(gameScene, graphics);
					}
				}
				break;
			}
			case SAVING: {
				gameScene.clear(0xA4E767);
				overworld.render(gameScene, player.getX(), player.getY());
				if (this.subMenu != null) {
					this.subMenu.render(gameScene, graphics);
				}
				break;
			}
			default: {
				break;
			}
		}
		graphics.drawImage(
			MainComponent.createCompatibleBufferedImage(gameScene.getBufferedImage()), 0, 0,
			MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null
		);
	}

	/**
	 * Updates the game.
	 * 
	 * All tick() methods must be placed in here, in order to correctly update the game.
	 * 
	 * @return Nothing.
	 */
	public void tick() {
		// Debugging purposes
		if (this.player.keys.F1.keyStateDown && !(this.player.keys.F1.lastKeyState)) {
			this.player.keys.F1.lastKeyState = true;
			Mod.resetLoading();
			this.load();
		}
		// End debugging purposes
		GameState state = this.stateManager.getCurrentGameState();
		switch (state) {
			case MAIN_GAME: {
				overworld.tick();
				checkPausing();
				break;
			}
			case INVENTORY: {
				if (!this.subMenu.isActivated()) {
					this.stateManager.setCurrentGameState(GameState.START_MENU);
					gameScene.setRenderingEffectTick((byte) 0x0);
					break;
				}
				this.subMenu = this.startMenu.getSubMenu();
				if (!this.subMenu.isActivated())
					this.subMenu.enableSubMenu();
				this.subMenu.tick();
				break;
			}
			case START_MENU: {
				if (startMenu.isActivated())
					startMenu.tick();
				else
					startMenu.openMenu();
				checkUnpausing();
				if (startMenu.isActionEventAvailable())
					handleActionEvent(startMenu.getActionEvent());
				break;
			}
			case SAVING: {
				if (!this.subMenu.isActivated()) {
					final Save saveSubMenu = (Save) this.subMenu;
					if (saveSubMenu.getState() == Save.State.SAVED || saveSubMenu.getState() == Save.State.ERROR) {
						saveSubMenu.setState(Save.State.ASK);
						this.startMenu.closeMenu();
						this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
						break;
					}
					this.stateManager.setCurrentGameState(GameState.START_MENU);
					break;
				}
				if (this.subMenu != null) {
					this.subMenu.tick();
				}
				break;
			}
			default: {
				break;
			}
		}
	}

	/**
	 * Saves the game.
	 */
	public void save() {
		GameSave.save(this, SAVE_FILE_NAME);
	}

	/**
	 * Checks for any previous saved data.
	 * 
	 * @return True, if it detects previous saved data. False, otherwise.
	 */
	public boolean checkSaveData() {
		return GameSave.check(SAVE_FILE_NAME);
	}

	/**
	 * <p>
	 * Loads the game.
	 * </p>
	 * 
	 * <p>
	 * Currently used for debugging purposes only. Press F1 to re-load.
	 * </p>
	 */
	private void load() {
		// TODO: Load data.
		this.gameScene.reload();
		this.player.reload();
		
		Mod.loadModdedResources();
		if (WorldConstants.isModsEnabled == null)
			WorldConstants.isModsEnabled = Boolean.FALSE;
		this.overworld = new OverWorld(this.player, this);
		GameSave.load(this, SAVE_FILE_NAME);
		
		this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
	}

	/**
	 * Currently unused.
	 */
	public void setScrollOffset(int xCamCenter, int yCamCenter) {
		// CamCenter: the coordinates of the center of camera.
		// this.xScroll = xCamCenter;
		// this.yScroll = yCamCenter;
	}

	/**
	 * Currently unused. However, this is executed in the render() code.
	 */
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

	public Scene getBaseScreen() {
		return this.gameScene;
	}

	public StateManager getStateManager() {
		return this.stateManager;
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

	// ------------------------------------------------- 
	// PRIVATE METHODS
	// -------------------------------------------------

	private void handleActionEvent(Map.Entry<Integer, SubMenu> entry) {
		String str = entry.getValue().getName();
		if (str.equals(StartMenu.ITEM_NAME_INVENTORY)) {
			this.stateManager.setCurrentGameState(GameState.INVENTORY);
			this.subMenu = entry.getValue();
			if (!this.subMenu.isActivated())
				this.subMenu.enableSubMenu();
			gameScene.setRenderingEffectTick((byte) 0x0);
		}
		else if (str.equals(StartMenu.ITEM_NAME_EXIT)) {
			this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
			if (this.subMenu != null)
				this.subMenu.disableSubMenu();
			this.subMenu = null;
		}
		else if (str.equals(StartMenu.ITEM_NAME_SAVE)) {
			this.stateManager.setCurrentGameState(GameState.SAVING);
			this.subMenu = entry.getValue();
			if (!this.subMenu.isActivated())
				this.subMenu.enableSubMenu();
		}
		this.startMenu.clearActionEvent();
		this.startMenu.closeMenu();
	}

	private void checkPausing() {
		Keys keys = this.player.keys;
		GameState state = this.stateManager.getCurrentGameState();
		if (!keys.START.lastKeyState && keys.START.keyStateDown) {
			switch (state) {
				case MAIN_GAME:
					if (this.player.isLockedWalking() || this.player.isLockedJumping()) {
						break;
					}
					this.stateManager.setCurrentGameState(GameState.START_MENU);
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
		GameState state = this.stateManager.getCurrentGameState();
		switch (state) {
			case START_MENU:
				boolean startKeyStateDown = (!keys.START.lastKeyState && keys.START.keyStateDown);
				boolean cancelKeyStateDown = (keys.X.keyStateDown || keys.PERIOD.keyStateDown);
				boolean cancelLastKeyState = (!keys.X.lastKeyState || !keys.PERIOD.lastKeyState);
				if (startKeyStateDown || (cancelKeyStateDown && cancelLastKeyState)) {
					this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
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
