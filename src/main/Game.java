package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import level.OverWorld;
import screen.BaseScreen;
import abstracts.Tile;
import abstracts.World;
import entity.Data;
import entity.Player;
import entity.Plot;

public class Game {
	//Handles different types of worlds for the game to use.
	//It acts as a pointer that points to many different worlds.
	//Each world stays loaded, while the pointer just "slides" across towards the next world it is transitioning to.
	public static Random Randomize = new Random(System.currentTimeMillis());
	private final BaseScreen screen;
	private final Keys inputs;
	private final Gui guiInterface;
	private final List<World> worlds;
	private final Player player;
	private Plot storyPlot;
	private Data data;
	
	private int xScroll;
	private int yScroll;
	
	private int xCamera;
	private int yCamera;
	
	public Game(BaseScreen output, Keys input) {
		this.screen = output;
		this.inputs = input;
		player = new Player(input);
		this.guiInterface = new Gui();
		
		worlds = new ArrayList<World>();
		
		OverWorld world = new OverWorld("Test");
		world.initialize(player);
		worlds.add(world);
	}
	
	public void render() {
		//TODO: Do rendering by calling "BaseScreen" variable and call one of many draw methods provided.
		//screen.createStaticNoise(Randomize);
		//TODO: Re-create the player's fixed position to camera's center, while everything else moves around.
		for (World w : worlds)
			if (w != null) {
				//w.render(screen, xScroll - player.getX(), yScroll - player.getY());
				w.render(screen, player.getX(), player.getY());
			}
		//player.render(screen, xScroll - Tile.WIDTH, yScroll - Tile.HEIGHT);
		player.render(screen, screen.getWidth() / 2 - Tile.WIDTH, (screen.getHeight() - Tile.HEIGHT) / 2);
	}
	
	public void tick() {
		//TODO: This updates for all objects given.
		for (World w : worlds)
			if (w != null)
				w.tick();
		player.tick();
	}
	
	public void save() {
		//TODO: Save data.
	}
	
	public void load() {
		//TODO: Load data.
	}
	
	public void setScrollOffset(int xCamCenter, int yCamCenter) {
		//CamCenter: the coordinates of the center of camera.
		this.xScroll = xCamCenter;
		this.yScroll = yCamCenter;
	}
	
	public void setCameraRelativeToArea(int areaXPos, int areaYPos) {
		//cam(x,y) = area(cam.x * -1 + xConstantOffset, cam.y * -1 + yConstantOffset)
		this.xCamera = (-areaXPos + this.xScroll) / Tile.WIDTH;
		this.yCamera = (-areaYPos + this.yScroll) / Tile.HEIGHT;
	}
}
