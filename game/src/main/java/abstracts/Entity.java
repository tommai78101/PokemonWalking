/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package abstracts;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import common.Tileable;
import dialogue.Dialogue;
import entity.Player;
import interfaces.UpdateRenderable;
import level.PixelData;
import level.WorldConstants;
import main.Game;
import screen.Bitmap;
import screen.Scene;

/**
 * Parent abstract class of all abstract classes.
 *
 * Holds all basic data needed for everything Entity.
 *
 * @author tlee
 *
 */
public abstract class Entity implements Tileable, UpdateRenderable {
	public int id;
	public int interactableID;
	public boolean isRemoved;
	protected boolean interactingState;

	protected byte typeId = 0;
	protected int xPosition;
	protected int yPosition;

	protected int xOffset;
	protected int yOffset;
	protected Bitmap bitmap = null;

	protected int lastFacing = 0;
	protected int facing = 0;

	protected String name;
	protected PixelData pixelData;
	protected Event event;

	// Dialogues
	protected int defaultDialogueIterator = 0;
	protected int overrideDialogueIterator = 0;
	protected Dialogue currentDefaultDialogue = null;
	protected Dialogue currentOverrideDialogue = null;
	protected List<Dialogue> defaultDialogues = new ArrayList<>();
	protected List<Dialogue> overrideDialogues = new ArrayList<>();

	// public abstract void initialize(BaseWorld world);

	public int getX() {
		return this.xPosition;
	}

	public int getY() {
		return this.yPosition;
	}

	protected void setPosition(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
	}

	public byte[] getByteName() {
		if (this.name == null)
			this.name = "Joe";
		byte[] result = new byte[16];
		byte[] nameData = this.name.getBytes();
		for (int i = 0; i < result.length; i++) {
			if (i < this.name.length())
				result[i] = nameData[i];
			else
				result[i] = 0;
		}
		return result;
	}

	/**
	 * Gets a value that determines where the direction the entity is currently facing towards.
	 *
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public int getFacing() {
		return this.facing;
	}

	/**
	 * Sets a value that determines where the direction the entity is currently facing towards.
	 */
	public void setFacing(int value) {
		this.lastFacing = this.facing;
		this.facing = value;
	}

	/**
	 * Gets a value that determines the direction the player had last been facing towards at.
	 *
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public int getLastFacing() {
		return this.lastFacing;
	}

	/**
	 * Checks whether the entity object has recently changed its facing direction.
	 *
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public boolean hasChangedFacing() {
		// True, if current facing has been changed.
		return this.lastFacing != this.facing;
	}

	public PixelData getPixelData() {
		return this.pixelData;
	}

	public void setPixelData(PixelData data) {
		this.pixelData = data;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setInteractingState(boolean value) {
		this.interactingState = value;
		if (value) {
			if (!Player.isMovementsLocked())
				Player.lockMovements();
		}
		else {
			if (Player.isMovementsLocked())
				Player.unlockMovements();
		}
	}

	public boolean isInteracting() {
		return this.interactingState;
	}

	// Dialogues

	public Dialogue getCurrentDialogue() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return null;
			}
			this.currentDefaultDialogue = this.defaultDialogues.get(this.defaultDialogueIterator);
			return this.currentDefaultDialogue;
		}
		this.currentOverrideDialogue = this.overrideDialogues.get(this.overrideDialogueIterator);
		return this.currentOverrideDialogue;
	}

	public Dialogue nextDialogue() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return null;
			}
			this.defaultDialogueIterator++;
			if (this.defaultDialogueIterator >= this.defaultDialogues.size()) {
				this.endDialogue();
				return null;
			}
			this.currentDefaultDialogue = this.defaultDialogues.get(this.defaultDialogueIterator);
			return this.currentDefaultDialogue;
		}
		this.overrideDialogueIterator++;
		if (this.overrideDialogueIterator >= this.overrideDialogues.size()) {
			this.endDialogue();
			return null;
		}
		this.currentOverrideDialogue = this.overrideDialogues.get(this.overrideDialogueIterator);
		return this.currentOverrideDialogue;
	}

	public void endDialogue() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return;
			}
			this.defaultDialogueIterator = 0;
			this.defaultDialogues.forEach(
				dialogue -> {
					dialogue.resetDialogue();
				}
			);
			return;
		}
		this.overrideDialogueIterator = 0;
		this.overrideDialogues.forEach(
			dialogue -> {
				dialogue.resetDialogue();
			}
		);
	}

	/**
	 * Render the dialogue, while temporarily ignoring previous offsets that were set in the Scene.
	 *
	 * @param screen
	 * @param graphics
	 */
	public void renderDialogue(Scene screen, Graphics graphics) {
		if (this.interactingState) {
			Dialogue currentDialogue = this.getCurrentDialogue();
			if (currentDialogue != null)
				currentDialogue.render(screen, graphics);
		}
	}

	public List<Dialogue> getDialogues() {
		if (this.overrideDialogues.isEmpty()) {
			if (this.defaultDialogues.isEmpty()) {
				return null;
			}
			return this.defaultDialogues;
		}
		return this.overrideDialogues;
	}

	public void dialogueTick() {
		if (this.interactingState) {
			this.setInteractingState(true);
			Dialogue currentDialogue = this.getCurrentDialogue();
			if (currentDialogue == null || !currentDialogue.isReady()) {
				this.setInteractingState(false);
				return;
			}
			if (!currentDialogue.isDialogueCompleted()) {
				currentDialogue.tick();
			}
			else if (Game.keys.isPrimaryPressed()) {
				Game.keys.primaryReceived();
				this.setInteractingState(false);
				this.endDialogue();
			}
		}
	}

	// ==============================================================
	// Static helper methods
	// ==============================================================

	public static boolean isObstacle(PixelData data) {
		return data.getAlpha() == WorldConstants.ENTITY_TYPE_OBSTACLE;
	}

	public static boolean isCharacter(PixelData data) {
		return data.getAlpha() == WorldConstants.ENTITY_TYPE_CHARACTER;
	}

	public static boolean isItem(PixelData data) {
		return data.getAlpha() == WorldConstants.ENTITY_TYPE_ITEM;
	}
}
