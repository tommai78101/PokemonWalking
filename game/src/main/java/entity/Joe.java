package entity;

import java.awt.Graphics;

import abstracts.Character;
import abstracts.Entity;
import abstracts.Item;
import screen.Scene;

public class Joe extends Character {
	public Joe() {
		this.setCharacterPlayable(false);
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Scene screen, Graphics graphics, int offsetX, int offsetY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void walk() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sprint() {
		// TODO Auto-generated method stub

	}

	@Override
	public void jump() {
		// TODO Auto-generated method stub

	}

	@Override
	public void ride() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swim() {
		// TODO Auto-generated method stub

	}

	@Override
	public void interact(Entity target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void interact(Entity target, Item item) {
		// TODO Auto-generated method stub

	}
}