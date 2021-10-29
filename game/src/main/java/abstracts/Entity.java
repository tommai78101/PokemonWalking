/**
 * Open-source Game Boy inspired game.
 *
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo.
 */

package abstracts;

import common.Debug;
import common.Tileable;
import entity.Player;
import interfaces.UpdateRenderable;
import level.Area;
import level.PixelData;
import level.WorldConstants;
import screen.Bitmap;
import screen.Scene;
import script.Script;
import script.TriggerData;

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

	@Deprecated
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
	protected TriggerData triggerData;
	protected Event event;
	protected Area area;

	public Entity() {}

	public static boolean isCharacter(PixelData data) {
		return data.getAlpha() == WorldConstants.ENTITY_TYPE_CHARACTER;
	}

	public static boolean isItem(PixelData data) {
		return data.getAlpha() == WorldConstants.ENTITY_TYPE_ITEM;
	}

	public static boolean isObstacle(PixelData data) {
		return data.getAlpha() == WorldConstants.ENTITY_TYPE_OBSTACLE;
	}

	public void dialogueRender(Scene screen) {
		if (this.interactingState && this.triggerData != null)
			this.triggerData.render(screen, screen.getBufferedImage().createGraphics());
	}

	public void dialogueTick() {
		if (this.interactingState) {
			if (this.triggerData != null && this.triggerData.hasActiveScript(this.area)) {
				this.triggerData.prepareActiveScript();
				this.triggerData.tick(this.area);
			}
			else {
				this.interactingState = false;
			}
		}
	}

	public Area getArea() {
		return this.area;
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

	public Event getEvent() {
		return this.event;
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
	 * Gets a value that determines the direction the player had last been facing towards at.
	 *
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public int getLastFacing() {
		return this.lastFacing;
	}

	public int getOldX() {
		return this.oldXAreaPosition;
	}

	public int getOldY() {
		return this.oldYAreaPosition;
	}

	public PixelData getPixelData() {
		return this.pixelData;
	}

	public int getPredictedX() {
		return this.predictedXAreaPosition;
	}

	public int getPredictedY() {
		return this.predictedYAreaPosition;
	}

	public TriggerData getTriggerData() {
		return this.triggerData;
	}

	public int getX() {
		return this.xAreaPosition;
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

	public int getY() {
		return this.yAreaPosition;
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

	/**
	 * Checks whether the entity object has recently changed its facing direction.
	 *
	 * @return An integer of one of the followings: Entity.UP, Entity.DOWN, Entity.LEFT, Entity.RIGHT.
	 */
	public boolean hasChangedFacing() {
		// True, if current facing has been changed.
		return this.lastFacing != this.facing;
	}

	public boolean isInteracting() {
		return this.interactingState;
	}

	public void loadTriggerData() {
		if (this.area == null) {
			Debug.warn("Area is not set.");
			return;
		}
		if (this.area.getTriggerDatasMap() == null || this.area.getTriggerDatasMap().isEmpty()) {
			Debug.warn("Area trigger data map is not set.");
			return;
		}
		var triggerDataMap = this.area.getTriggerDatasMap();
		FINISH_LOADING:
		for (var entry : triggerDataMap.entrySet()) {
			TriggerData data = entry.getValue();
			var scripts = data.getScripts();
			for (Script s : scripts) {
				if ((this instanceof Obstacle o && s.getTriggerID() == this.pixelData.getTileSpecificData()) || (this instanceof Character c && s.getNpcTriggerID() == this.pixelData.getRed())) {
					this.setTriggerData(data);
					break FINISH_LOADING;
				}
			}
		}
	}

	public void setArea(Area a) {
		this.area = a;
	}

	/**
	 * Sets a value that determines where the direction the entity is currently facing towards.
	 */
	public void setFacing(int value) {
		this.lastFacing = this.facing;
		this.facing = value;
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

	public void setPixelData(PixelData data) {
		this.pixelData = data;
	}

	public void setTriggerData(TriggerData data) {
		this.triggerData = data;
	}

	protected void setPixelDataPosition(int x, int y) {
		this.pixelData.setPosition(x, y);
	}

	protected void setPosition(int x, int y) {
		this.xAreaPosition = x;
		this.yAreaPosition = y;
		this.xPixelPosition = x * Tileable.WIDTH;
		this.yPixelPosition = y * Tileable.HEIGHT;
	}
}
