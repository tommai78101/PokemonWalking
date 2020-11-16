/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package main;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import abstracts.SubMenu;
import common.Debug;
import data.GameSave;
import entity.Player;
import item.KeyItem;
import level.OverWorld;
import level.WorldConstants;
import main.SaveDataManager.SaveStatus;
import main.StateManager.GameState;
import menu.Inventory;
import menu.MainMenu;
import resources.Art;
import resources.Mod;
import screen.Scene;
import screen.Scene.FlashingType;

public class Game {
	public static final Keys keys = new Keys();

	private final Scene gameScene;
	private final List<OverWorld> worlds;
	private final Player player;
	private MainMenu startMenu;
	private OverWorld overworld;
	private KeyItem registeredItem;
	private StateManager stateManager;
	private SaveDataManager saveManager;
	private Inventory inventoryManager;

	/**
	 * Creates the core component of the game.
	 * 
	 * All future game components are to be placed here.
	 * 
	 * @param Scene
	 *            Takes in a BaseScreen that displays all rendered graphics to the screen.
	 * @param Keys
	 *            Takes the Keys object the input handler receives from the player for the game to
	 *            handle. The input handler must control this Keys object.
	 * @see Scene
	 * @see NewInputHandler
	 */
	public Game(MainComponent main) {
		WorldConstants.checkForMods();
		this.worlds = new ArrayList<>();

		this.gameScene = main.getScene();
		this.worlds.add(this.overworld);
		this.startMenu = new MainMenu();
		this.stateManager = new StateManager();
		this.saveManager = new SaveDataManager(this);
		this.inventoryManager = new Inventory(this);
		this.startMenu.initialize(this);
		this.player = new Player(this);

		this.load();
	}

	/**
	 * Handles rendered objects and rendering effects.
	 * 
	 * <p>
	 * All render() methods must be placed in here to render to the screen.
	 * 
	 * <p>
	 * Methods placed in here are to be separated by game states, and depending on the game states, they
	 * are to be called when necessary.
	 * 
	 * @return Nothing.
	 */
	public void render(Graphics graphics) {
		this.gameScene.clear(Art.COLOR_DEBUG_GREEN);
		GameState state = this.stateManager.getCurrentGameState();
		switch (state) {
			case MAIN_GAME:
			default: {
				this.overworld.render(this.gameScene, graphics, this.player.getX(), this.player.getY());
				break;
			}
			case START_MENU: {
				if (this.gameScene.isRenderingEffect()) {
					this.gameScene.renderEffectFlashing(FlashingType.NORMAL);
				}
				else {
					this.overworld.render(this.gameScene, graphics, this.player.getX(), this.player.getY());
					this.startMenu.render(this.gameScene, graphics);
				}
				break;
			}
			case INVENTORY: {
				if (this.gameScene.isRenderingEffect()) {
					this.gameScene.renderEffectFlashing(FlashingType.NORMAL);
				}
				else {
					this.overworld.render(this.gameScene, graphics, this.player.getX(), this.player.getY());
					this.inventoryManager.render(this.gameScene, graphics);
				}
				break;
			}
			case SAVE: {
				this.overworld.render(this.gameScene, graphics, this.player.getX(), this.player.getY());
				SubMenu subMenu = this.startMenu.getActiveItem();
				if (subMenu != null) {
					subMenu.render(this.gameScene, graphics);
				}
				break;
			}
		}
		graphics.drawImage(
			MainComponent.createCompatibleBufferedImage(this.gameScene.getBufferedImage()), 0, 0,
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
		if (Game.keys.isDebugPressed()) {
			Game.keys.debugReceived();
			Mod.resetLoading();
			this.load();
		}
		// End debugging purposes

		GameState state = this.prepareMainMenu();
		SubMenu subMenu = this.startMenu.getActiveItem();
		switch (state) {
			case MAIN_GAME: {
				this.overworld.tick();
				this.checkPausing();
				break;
			}
			case START_MENU: {
				if (subMenu != null) {
					final SubMenu menu = subMenu;
					subMenu.getEvent().trigger(() -> {
						if (menu.needsFlashing()) {
							this.gameScene.setRenderingEffectTick((byte) 0x0);
						}
						this.stateManager.setCurrentGameState(menu.getGameState());
					});
				}
				this.startMenu.tick();
				this.checkUnpausing();
				break;
			}
			// The following cases should assume "subMenu" is not null.
			case INVENTORY: {
				subMenu.tick();
				break;
			}
			case SAVE: {
				if (this.saveManager.getSaveStatus().equals(SaveStatus.SAVE_COMPLETE) || this.saveManager.getSaveStatus().equals(SaveStatus.ERROR)) {
					this.saveManager.resetSaveStatus();
					this.closeMainMenu();
					break;
				}
				subMenu.tick();
				break;
			}
			case EXIT:
			default: {
				this.closeMainMenu();
				break;
			}
		}
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
		this.gameScene.resetRenderingEffect();
		this.player.reload();

		Mod.loadModdedResources();
		if (WorldConstants.isModsEnabled == null)
			WorldConstants.isModsEnabled = Boolean.FALSE;
		this.overworld = new OverWorld(this.player, this);
		this.overworld.reloadAllAreas();

		// GameSave.load(this, SaveDataManager.SAVE_FILE_NAME);
		if (WorldConstants.isModsEnabled.booleanValue())
			GameSave.loadExperimental(this, SaveDataManager.MODDED_SAVE_FILE_NAME);
		else
			GameSave.loadExperimental(this, SaveDataManager.SAVE_FILE_NAME);

		this.stateManager.setCurrentGameState(GameState.MAIN_GAME);

		if (WorldConstants.isModsEnabled.booleanValue())
			Debug.log("Loading custom modded game world.");
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

	public MainMenu getStartMenu() {
		return this.startMenu;
	}

	public Scene getBaseScreen() {
		return this.gameScene;
	}

	public StateManager getStateManager() {
		return this.stateManager;
	}

	public void setRegisteredItem(KeyItem item) {
		this.registeredItem = item;
		// TODO: Continue to handle registered item's action event.
	}

	public boolean itemHasBeenRegistered(KeyItem item) {
		if (this.registeredItem == null)
			return false;
		return this.registeredItem.equals(item);
	}

	public OverWorld getWorld() {
		return this.overworld;
	}

	public Inventory getInventory() {
		return this.inventoryManager;
	}

	public SaveDataManager getSaveManager() {
		return this.saveManager;
	}

	public void closeMainMenu() {
		this.gameScene.resetRenderingEffect();
		this.startMenu.clearActiveItem();
		this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
	}

	// -------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------

	private void checkPausing() {
		GameState state = this.stateManager.getCurrentGameState();
		if (Game.keys.isStartPressed()) {
			Game.keys.startReceived();
			switch (state) {
				case MAIN_GAME:
					if (this.player.isLockedWalking() || this.player.isLockedJumping() || this.player.isInteracting() || this.player.isInAutomaticMode()) {
						break;
					}
					this.stateManager.setCurrentGameState(GameState.START_MENU);
					break;
				default:
					break;
			}
		}
	}

	private void checkUnpausing() {
		GameState state = this.stateManager.getCurrentGameState();
		switch (state) {
			case START_MENU:
				if (this.startMenu.isExiting()) {
					this.startMenu.resetExitState();
					this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
					if (Player.isMovementsLocked())
						Player.unlockMovements();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * This handles updating the main menu states, rendering effects, and the sub menu data, all for
	 * preparations.
	 * 
	 * Clearing the main menu data allows the game to reset to the main menu, without the game states
	 * being messed up because of the focus change.
	 */
	private GameState prepareMainMenu() {
		GameState state = this.stateManager.getCurrentGameState();
		SubMenu subMenu = this.startMenu.getActiveItem();
		if (subMenu != null && subMenu.isExiting()) {
			this.startMenu.clearActiveItem();
			if (subMenu.needsFlashing()) {
				this.gameScene.setRenderingEffectTick((byte) 0x0);
			}
			else {
				this.gameScene.resetRenderingEffect();
			}
			if (!subMenu.exitsToGame()) {
				this.stateManager.setCurrentGameState(GameState.START_MENU);
			}
			else {
				this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
			}
			subMenu.resetExitState();
			subMenu = null;
			state = this.stateManager.getCurrentGameState();
		}
		return state;
	}
}
