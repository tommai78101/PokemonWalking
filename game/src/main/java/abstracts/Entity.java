/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package abstracts;

import java.util.Deque;
import java.util.Iterator;

import common.Tileable;
import entity.Player;
import interfaces.UpdateRenderable;
import level.Area;
import level.PixelData;
import level.WorldConstants;
import screen.Bitmap;
import screen.Scene;
import script.Script;

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
	protected int xAreaPosition;
	protected int yAreaPosition;
	protected int oldXAreaPosition;
	protected int oldYAreaPosition;
	protected int xPixelPosition;
	protected int yPixelPosition;
	protected int predictedXAreaPosition;
	protected int predictedYAreaPosition;

	protected int xOffset;
	protected int yOffset;
	protected Bitmap bitmap = null;

	protected int lastFacing = 0;
	protected int facing = 0;

	protected String name;
	protected PixelData pixelData;
	protected Event event;
	protected Iterator<Script> currentScriptIterator;
	protected Deque<Script> scripts;
	protected Script currentScript;
	protected Area area;

	public int getX() {
		return this.xAreaPosition;
	}

	public int getY() {
		return this.yAreaPosition;
	}

	public int getOldX() {
		return this.oldXAreaPosition;
	}

	public int getOldY() {
		return this.oldYAreaPosition;
	}

	public int getPredictedX() {
		return this.predictedXAreaPosition;
	}

	public int getPredictedY() {
		return this.predictedYAreaPosition;
	}

	protected void setPosition(int x, int y) {
		this.xAreaPosition = x;
		this.yAreaPosition = y;
		this.xPixelPosition = x * Tileable.WIDTH;
		this.yPixelPosition = y * Tileable.HEIGHT;
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

	public Deque<Script> getAllScripts() {
		return this.scripts;
	}

	public void addScript(Script s) {
		this.scripts.add(s);
	}

	public Script getCurrentScript() {
		if (this.currentScriptIterator != null && this.currentScriptIterator.hasNext()) {
			return this.currentScriptIterator.next();
		}
		this.currentScriptIterator = this.scripts.iterator();
		return this.getCurrentScript();
	}

	public void dialogueTick() {
		if (this.interactingState) {
			this.getCurrentScript().tick(this.area);
		}
	}

	public void dialogueRender(Scene screen) {
		if (this.interactingState) {
			this.getCurrentScript().render(screen, screen.getBufferedImage().createGraphics());
		}
	}

	public int getXInArea() {
		// Returns area position X.
		int result = (this.xAreaPosition / Tileable.WIDTH);
		switch (this.getFacing()) {
			case Character.LEFT:
				break;
			case Character.RIGHT:
				result += 1;
				break;
		}
		return result;
	}

	public int getYInArea() {
		// Returns area position Y.
		int result = (this.yAreaPosition / Tileable.HEIGHT);
		switch (this.getFacing()) {
			case Character.UP:
				break;
			case Character.DOWN:
				result += 1;
				break;
		}
		return result;
	}

	public Area getArea() {
		return this.area;
	}

	public void setArea(Area a) {
		this.area = a;
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
