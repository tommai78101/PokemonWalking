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

import java.util.ArrayList;
import java.util.List;

import entity.Player;
import level.Area;
import main.Game;

public abstract class World extends Entity {
	// World doesn't need to keep track of its coordinates.
	// World should keep track of player coordinates.
	// World should keep track of current area.
	// (Basically, World should keep track of current area, and this current area
	// should keep track of player position.)
	// World should prepare new areas if player steps into warp point.

	// Current area must contain warp points.
	// Each warp point defines two areas.
	// All warp points can point to a list of areas.
	// Question: Should this list of areas be global?
	// Question: Should subclasses of World contain list of areas specific to those
	// Worlds?

	// Player coordinates is relative to area position.
	// Player needs a way to interact with World, to use warp points and do actions.
	// World should check Player flags.
	// Player should set flags for World to check.

	protected Player player;
	protected Area currentArea;
	protected Game game;

	public List<Area> areas = new ArrayList<Area>();

	protected int worldID;

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

	public World(Player player, Game game) {
		this.player = player;
		this.game = game;
		this.currentArea = null;
	}

	// Will work on this in the future, once multi-worlds are a possibility.
	// public void transitionTo(World world) {
	//
	// }
	//

	// Will work on this in the future, once interactive objects are
	// available/necessary.
	// public void add(Interactable object) {
	//
	// }
	//
	// public void remove(Interactable object) {
	//
	// }

	// public void setOffset(int x, int y) {
	//     this.xOffset = x;
	//     this.yOffset = y;
	// }
	//
	// public void setPosition(int x, int y) {
	//     this.xPosition = x;
	//     this.yPosition = y;
	// }

	public Area getCurrentArea() {
		return this.currentArea;
	}

	public void setCurrentArea(Area area) {
		this.currentArea = area;
	}

	public List<Area> getAllAreas() {
		// for (int i=0; i<this.areas.size(); i++){
		//     if (this.areas.get(i).getAreaID() == this.currentArea.getAreaID()){
		//         this.areas.set(i, this.currentArea);
		//         break;
		//     }
		// }
		return this.areas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + worldID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		World other = (World) obj;
		if (worldID != other.worldID) {
			return false;
		}
		return true;
	}

	public void refresh() {
		for (int i = 0; i < this.areas.size(); i++) {
			if (this.areas.get(i).getAreaID() == this.currentArea.getAreaID()) {
				this.currentArea = this.areas.get(i);
				break;
			}
		}
	}

}
