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

import abstracts.SubMenu;
import entity.Player;
import item.ActionItem;
import level.OverWorld;
import level.WorldConstants;
import main.SaveDataManager.SaveStatus;
import main.StateManager.GameState;
import menu.Inventory;
import menu.MainMenu;
import resources.Art;
import resources.Mod;
import saving.GameSave;
import screen.Scene;

public class Game {
	private static final String SAVE_FILE_NAME = "data.sav";
	public static final Keys keys = new Keys();

	private final Scene gameScene;
	private final List<OverWorld> worlds;
	private final Player player;
	private MainMenu startMenu;
	private OverWorld overworld;
	private ActionItem registeredItem;
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
	 *            Takes the Keys object the input handler receives from the player for the game to handle. The input handler must control this Keys object.
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
		this.player = new Player(Game.keys);
		this.player.setCenterCamPosition(this.gameScene);

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
		this.gameScene.clear(Art.COLOR_DEBUG_GREEN);
		GameState state = this.stateManager.getCurrentGameState();
		switch (state) {
			case MAIN_GAME: {
				this.overworld.render(this.gameScene, this.player.getX(), this.player.getY());
				break;
			}
			case START_MENU: {
				if (this.gameScene.getRenderingEffectTick() < (byte) 0x7) {
					this.gameScene.flashing();
				}
				else {
					this.overworld.render(this.gameScene, this.player.getX(), this.player.getY());
					this.startMenu.render(this.gameScene, graphics);
				}
				break;
			}
			case INVENTORY: {
				if (this.gameScene.getRenderingEffectTick() < (byte) 0x7) {
					this.gameScene.flashing();
				}
				else {
					this.overworld.render(this.gameScene, this.player.getX(), this.player.getY());
					this.inventoryManager.render(this.gameScene, graphics);
				}
				break;
			}
			case SAVING: {
				this.overworld.render(this.gameScene, this.player.getX(), this.player.getY());
				SubMenu subMenu = this.startMenu.getActiveItem();
				if (subMenu != null) {
					subMenu.render(this.gameScene, graphics);
				}
				break;
			}
			default: {
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
		if (this.player.keys.F1.keyStateDown && !(this.player.keys.F1.lastKeyState)) {
			this.player.keys.F1.lastKeyState = true;
			Mod.resetLoading();
			this.load();
		}
		// End debugging purposes
		GameState state = this.stateManager.getCurrentGameState();
		SubMenu subMenu = this.startMenu.getActiveItem();
		if (subMenu != null && subMenu.isExiting()) {
			this.stateManager.setCurrentGameState(GameState.START_MENU);
			this.gameScene.setRenderingEffectTick((byte) 0x0);
			this.startMenu.clearActiveItem();
			subMenu.resetExitState();
		}
		switch (state) {
			case MAIN_GAME: {
				this.overworld.tick();
				this.checkPausing();
				break;
			}
			case INVENTORY: {
				subMenu.tick();
				break;
			}
			case START_MENU: {
				this.startMenu.tick();
				if (subMenu != null) {
					subMenu.getEvent().trigger(() -> {
						this.stateManager.setCurrentGameState(subMenu.getGameState());
						this.gameScene.setRenderingEffectTick((byte) 0x0);
					});
				}
				this.checkUnpausing();
				break;
			}
			case SAVING: {
				if (!subMenu.getGameState().equals(GameState.SAVING)) {
					if (this.saveManager.getSaveStatus().equals(SaveStatus.SAVED) || this.saveManager.getSaveStatus().equals(SaveStatus.ERROR)) {
						this.saveManager.setSaveStatus(SaveStatus.ASK);
						this.stateManager.setCurrentGameState(GameState.MAIN_GAME);
						break;
					}
					this.stateManager.setCurrentGameState(GameState.START_MENU);
					break;
				}
				subMenu.tick();
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
		GameSave.save(this, Game.SAVE_FILE_NAME);
	}

	/**
	 * Checks for any previous saved data.
	 * 
	 * @return True, if it detects previous saved data. False, otherwise.
	 */
	public boolean checkSaveData() {
		return GameSave.check(Game.SAVE_FILE_NAME);
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
		GameSave.load(this, Game.SAVE_FILE_NAME);

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

	public MainMenu getStartMenu() {
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

	public OverWorld getWorld() {
		return this.overworld;
	}

	public Inventory getInventory() {
		return this.inventoryManager;
	}

	public SaveDataManager getSaveManager() {
		return this.saveManager;
	}

	// ------------------------------------------------- 
	// PRIVATE METHODS
	// -------------------------------------------------

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
