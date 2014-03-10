package abstracts;

import interfaces.Interactable;
import interfaces.Moveable;

import java.util.List;

import level.WayPoint;
import screen.BaseBitmap;
import screen.BaseScreen;
import entity.Data;
import entity.Plot;

public abstract class World {

	
	
	
	
	protected BaseBitmap bitmap;
	protected int xOffset;
	protected int yOffset;
	protected int xPosition;
	protected int yPosition;
	protected List<WayPoint> warpPoints;
	protected List<Interactable> interactables;
	protected List<Moveable> moveables;
	
	protected Data data;
	public Plot storyPlot;
	protected int id;
	
	public void tick() {
		
	}
	
	public void render(BaseScreen output, int x, int y) {
		
	}
	
	public void transitionTo(World world) {
		
	}
	
	public void add(Interactable object) {
		
	}
	
	public void remove(Interactable object) {
		
	}
	
	public void setOffset(int x, int y) {
		this.xOffset = x;
		this.yOffset = y;
	}
	
	public void setPosition(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
	}
	
}
