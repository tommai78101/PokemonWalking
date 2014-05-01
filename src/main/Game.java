package main;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import level.OverWorld;
import screen.BaseScreen;
import abstracts.SubMenu;
import abstracts.World;
import dialogue.Dialogue;
import dialogue.StartMenu;
import entity.Player;

public class Game {
	//Handles different types of worlds for the game to use.
	//It acts as a pointer that points to many different worlds.
	//Each world stays loaded, while the pointer just "slides" across towards the next world it is transitioning to.
	public static Random Randomize = new Random(System.currentTimeMillis());
	private final BaseScreen screen;
	//private final Keys inputs;
	//private final Gui guiInterface;
	private final List<World> worlds;
	private StartMenu startMenu;
	private SubMenu subMenu;
	private World overworld;
	private final Player player;
	
	private Dialogue dialogue;
	
	private enum State {
		GAME, PAUSED, INVENTORY
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
	 *            Takes the Keys object the input handler receives from the player for the game to handle. The input handler must control
	 *            this Keys object.
	 * @see BaseScreen
	 * @see NewInputHandler
	 * */
	public Game(MainComponent main, Keys input) {
		this.screen = main.getBaseScreen();
		this.player = new Player(input);
		this.player.setCenterCamPosition(this.screen);
		this.dialogue = new Dialogue(input, this);
		this.overworld = new OverWorld(player, this.dialogue);
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
		//TODO: Do rendering by calling "BaseScreen" variable and call one of many draw methods provided.
		//TODO: Re-create the player's fixed position to camera's center, while everything else moves around.
		//TODO: Overworld must be drawn while the player is moving around. Small areas can only be seen after the camera culls out the overworld.
		
		switch (this.state) {
			case GAME: {
				screen.clear(0xA4E767);
				overworld.render(screen, player.getX(), player.getY());
				//dialogue.displayText("Hello World.", screen, Dial)
				//dialogue.render(screen, 6, 0, 3, 8);
				//FIXME: Need to do something about the font having to be rendered by graphics and not screen.
				//dialogue.renderTextGraphics(graphics);
				//dialogue.renderText(graphics);
				if (dialogue.isDisplayingDialogue())
					dialogue.render(screen, player.getX(), player.getY(), graphics);
				else
					graphics.drawImage(MainComponent.createCompatibleBufferedImage(screen.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
				break;
			}
			case INVENTORY: {
				if (screen.getRenderingEffectTick() < (byte) 0x7) {
					screen.flashing();
					graphics.drawImage(MainComponent.createCompatibleBufferedImage(screen.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
				}
				else {
					if (this.subMenu != null)
						this.subMenu.render(screen, graphics);
				}
				break;
			}
			case PAUSED: {
				if (screen.getRenderingEffectTick() < (byte) 0x7) {
					screen.flashing();
					graphics.drawImage(MainComponent.createCompatibleBufferedImage(screen.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
				}
				else {
					screen.clear(0xA4E767);
					overworld.render(screen, player.getX(), player.getY());
					//dialogue.render(screen, player.getX(), player.getY(), graphics);
					if (startMenu.isActivated()) {
						startMenu.render(screen, graphics);
					}
					else {
						screen.clear(0xA4E767);
						overworld.render(screen, player.getX(), player.getY());
						dialogue.render(screen, player.getX(), player.getY(), graphics);
						graphics.drawImage(MainComponent.createCompatibleBufferedImage(screen.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
					}
				}
				break;
			}
		}
	}
	
	/**
	 * Updates the game.
	 * 
	 * All tick() methods must be placed in here, in order to correctly update the game.
	 * 
	 * @return Nothing.
	 * */
	public void tick() {
		//TODO: This updates for all objects given.
		/*
		for (World w : worlds)
			if (w != null)
				w.tick();
		*/
		//player.tick();
		switch (this.state) {
			case GAME: {
				overworld.tick();
				dialogue.tick();
				checkPausing();
				break;
			}
			case INVENTORY: {
				if (this.subMenu != null)
					this.subMenu.tick();
				else
					break;
				if (!this.subMenu.isActivated()) {
					this.state = State.PAUSED;
					screen.setRenderingEffectTick((byte) 0x0);
				}
				break;
			}
			case PAUSED: {
				if (!startMenu.isActivated()) {
					startMenu.openMenu();
				}
				startMenu.tick();
				checkUnpausing();
				if (startMenu.isActionEventAvailable())
					handleActionEvent(startMenu.getActionEvent());
				break;
			}
		}
	}
	
	/**
	 * Currently unused.
	 * */
	public void save() {
		//TODO: Save data.
	}
	
	/**
	 * Currently unused.
	 * */
	public void load() {
		//TODO: Load data.
	}
	
	/**
	 * Currently unused.
	 * */
	public void setScrollOffset(int xCamCenter, int yCamCenter) {
		//CamCenter: the coordinates of the center of camera.
		//this.xScroll = xCamCenter;
		//this.yScroll = yCamCenter;
	}
	
	/**
	 * Currently unused. However, this is executed in the render() code.
	 * */
	public void setCameraRelativeToArea(int areaXPos, int areaYPos) {
		//Not used at the moment.
		
		//cam(x,y) = area(cam.x * -1 + xConstantOffset, cam.y * -1 + yConstantOffset)
		//this.xCamera = (-areaXPos + this.xScroll) / Tile.WIDTH;
		//this.yCamera = (-areaYPos + this.yScroll) / Tile.HEIGHT;
	}
	
	//	public void sendAction(Map.Entry<String, String> entry) {
	//		//TODO: Add actual game menu actions the player can do.
	//		String key = entry.getKey();
	//		if (key.equals("BICYCLE")) {
	//			if (!this.player.isRidingBicycle())
	//				this.player.startsRidingBicycle();
	//			else
	//				this.player.getsOffBicycle();
	//		}
	//	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	//----------------------------------------------       PRIVATE METHODS      -------------------------------------------------
	
	private void handleActionEvent(Map.Entry<Integer, SubMenu> entry) {
		String str = entry.getValue().getName();
		if (str.equals(StartMenu.ITEM_NAME_BICYCLE)) {
			if (this.state != State.GAME)
				this.state = State.GAME;
			if (!this.player.isRidingBicycle())
				this.player.startsRidingBicycle();
			else
				this.player.getsOffBicycle();
			if (this.subMenu != null)
				this.subMenu.disableSubMenu();
			this.subMenu = null;
		}
		else if (str.equals(StartMenu.ITEM_NAME_INVENTORY)) {
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
		if ((!keys.START.lastKeyState && keys.START.keyStateDown) || ((keys.X.keyStateDown || keys.PERIOD.keyStateDown) && (!keys.X.lastKeyState || !keys.PERIOD.lastKeyState))) {
			switch (this.state) {
				case PAUSED:
					this.state = State.GAME;
					if (Player.isMovementsLocked())
						Player.unlockMovements();
					break;
				default:
					break;
			}
			keys.START.lastKeyState = true;
		}
	}
}
