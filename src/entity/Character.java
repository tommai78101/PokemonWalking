package entity;

import interfaces.Collidable;
import interfaces.Interactable;
import interfaces.Moveable;

import java.util.List;

import screen.BaseScreen;
import abstracts.Entity;
import abstracts.Tile;

public class Character extends Entity implements Collidable, Interactable, Moveable {
	
	protected Data data;
	protected List<Dialogue> dialogs;
	protected Plot storyPlot;
	protected int moveableId = -1;
	protected int interactableId = -1;
	protected int collidableId = -1;
	
	@Override
	public void handleCollision(Tile tile, int xAcceleration, int yAcceleration) {
	}
	
	@Override
	public void interact(Interactable object) {
	}
	
	//	@Override
	//	public void initialize(BaseWorld world) {
	//		//TODO: Do something about the incorrect ID.
	//		if (this.moveableId < 0 || this.interactableId < 0 || this.collidableId < 0)
	//			return;
	//	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public void render(BaseScreen screen, int x, int y) {
	}
	
	@Override
	public void move() {
	}
	
	@Override
	public int getMoveableId() {
		return this.moveableId;
	}
	
	@Override
	public int getInteractableId() {
		return this.interactableId;
	}
	
	@Override
	public int getCollidableId() {
		return this.collidableId;
	}
	
}
