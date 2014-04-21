package main;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import level.OverWorld;
import screen.BaseScreen;
import screen.Dialogue;
import abstracts.World;
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
	private World overworld;
	private final Player player;
	
	private Dialogue dialogue;
	
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
	}
	
	/**
	 * Handles rendered objects.
	 * 
	 * All render() methods must be placed in here, in order to correctly render to the screen.
	 * 
	 * @return Nothing.
	 * */
	public void render(Graphics graphics) {
		//TODO: Do rendering by calling "BaseScreen" variable and call one of many draw methods provided.
		//TODO: Re-create the player's fixed position to camera's center, while everything else moves around.
		//TODO: Overworld must be drawn while the player is moving around. Small areas can only be seen after the camera culls out the overworld.
		/*
		for (World w : worlds)
			if (w != null) {
				w.render(screen, player.getX(), player.getY());
			}
		*/
		screen.clear(0xA4E767);
		overworld.render(screen, player.getX(), player.getY());
		//dialogue.displayText("Hello World.", screen, Dial)
		//dialogue.render(screen, 6, 0, 3, 8);
		//FIXME: Need to do something about the font having to be rendered by graphics and not screen.
		//dialogue.renderTextGraphics(graphics);
		//dialogue.renderText(graphics);
		dialogue.render(screen, player.getX(), player.getY(), graphics);
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
		overworld.tick();
		dialogue.tick();
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
	
	public void prepareAction(Map.Entry<String, String> entry) {
		String key = entry.getKey();
		
		//TODO: Add actual game menu actions the player can do.
		if (key.equals("BICYCLE")) {
			if (!this.player.isRidingBicycle()) {
				this.player.startsRidingBicycle();
				entry.setValue("Get off bicycle.");
			}
			else {
				this.player.getsOffBicycle();
				entry.setValue("Use your bicycle.");
			}
		}
	}
}
