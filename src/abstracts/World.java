/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package abstracts;

import level.Area;
import screen.BaseScreen;
import entity.Player;

public abstract class World {
	// World doesn't need to keep track of its coordinates.
	// World should keep track of player coordinates.
	// World should keep track of current area.
	// (Basically, World should keep track of current area, and this current area should keep track of player position.)
	// World should prepare new areas if player steps into warp point.
	
	// Current area must contain warp points.
	// Each warp point defines two areas.
	// All warp points can point to a list of areas.
	// Question: Should this list of areas be global?
	// Question: Should subclasses of World contain list of areas specific to those Worlds?
	
	// Player coordinates is relative to area position.
	// Player needs a way to interact with World, to use warp points and do actions.
	// World should check Player flags.
	// Player should set flags for World to check.
	
	protected Player player;
	protected Area currentArea;
	
	// Contains new objects, must add them.
	// protected List<WayPoint> warpPoints;
	
	// protected BaseBitmap bitmap;
	// protected int xOffset;
	// protected int yOffset;
	// protected int xPosition;
	// protected int yPosition;
	
	// Will do in the future. It's too early to work on this.
	// protected List<Interactable> interactables;
	// protected List<Moveable> moveables;
	// public Plot storyPlot;
	// protected Data data;
	// protected int id;
	
	public void tick() {
	}
	
	public void render(BaseScreen output, int x, int y) {
		
	}
	
	// Will work on this in the future, once multi-worlds are a possibility.
	// public void transitionTo(World world) {
	//
	// }
	//
	
	// Will work on this in the future, once interactive objects are available/necessary.
	// public void add(Interactable object) {
	//
	// }
	//
	// public void remove(Interactable object) {
	//
	// }
	
	// public void setOffset(int x, int y) {
	// this.xOffset = x;
	// this.yOffset = y;
	// }
	//
	// public void setPosition(int x, int y) {
	// this.xPosition = x;
	// this.yPosition = y;
	// }
	
}
