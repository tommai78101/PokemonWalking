package entity;

import java.awt.Graphics;

import abstracts.Character;
import abstracts.Entity;
import dialogue.Dialogue.DialogueType;
import level.Area;
import resources.Art;
import screen.Scene;
import utility.DialogueBuilder;

public class Joe extends Character {
	public Joe() {
		// TODO(4/04/2020): Figure out a way to load dialogue scripts for Signs based on the pixel data's
		// green and blue values.
		this.defaultDialogues.add(
			DialogueBuilder.createText(
				"Hello world.", DialogueType.SPEECH
			)
		);
	}

	@Override
	public void tick() {
		this.controlTick();
		this.dialogueTick();
	}

	@Override
	public void render(Scene screen, Graphics graphics, int playerX, int playerY) {
		this.characterRender(screen, Art.joe, graphics, playerX, playerY);
		this.renderDialogue(screen, graphics);
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
	public void interact(Area area, Entity target) {
		// TODO Auto-generated method stub
	}
}