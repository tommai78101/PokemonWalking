/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import common.Debug;
import common.Tileable;
import entity.Player;
import error.GameException;
import interfaces.UpdateRenderable;
import main.Game;
import screen.Scene;

public class OverWorld implements Tileable, UpdateRenderable {
	// Overworld properties.
	protected Player player;
	protected Area currentArea;
	protected Game game;
	public List<Area> areas = new ArrayList<>();
	protected int worldID;

	private boolean invertBitmapColors;
	private int currentAreaSectorID;
	// private int newDialoguesIterator;

	/**
	 * Initializes the overworld in the game.
	 * 
	 * All game entities are to be loaded through this method.
	 * 
	 * @param Player
	 *            Takes a Player object. The overworld then loads all related properties in respect to
	 *            the Player object.
	 */
	public OverWorld(Player player, Game game) {
		// There should be a maximum number of areas available for the OverWorld.
		// All areas defined must be placed in WorldConstants.

		// Player and Game.
		// super(player, game);
		this.player = player;
		this.game = game;
		this.currentArea = null;

		this.worldID = WorldConstants.OVERWORLD;

		if (!this.areas.isEmpty())
			this.areas.clear();
		WorldConstants.getAllNewScripts();
		this.areas = WorldConstants.getAllNewAreas();

		// Overworld properties
		this.invertBitmapColors = false;

		// Going to set this area as test default only. This will need to change in the
		// future.
		this.setCurrentAreaSector(0);
		this.currentArea = this.areas.get(0);
		this.currentArea.setPlayer(player);
		this.currentArea.setDebugDefaultPosition();
		// TODO: Add a method that executes according to the sector ID. Basically,
		// it needs to tell the player that they entered a new sector.
		// Needs a marker in the area that points to where the area connects together.
	}

	// Will add this in the future. Currently, the only entity is Player.
	/*
	public void addEntity(Entity e) {
		e.initialize(this);
		this.tiles.add(e);
	}
	*/

	// Worlds no longer need to calculate total width and height.
	/*
	public int getTotalWidth() {
		int result = 0;
		for (Area a : areas) {
			if (a != null)
				result += a.getWidth();
		}
		return result;
	}
	
	public int getTotalHeight() {
		int result = 0;
		for (Area a : areas) {
			if (a != null)
				result += a.getHeight();
		}
		return result;
	}
	*/

	public Area getCurrentArea() {
		return this.currentArea;
	}

	public void setCurrentArea(Area area) {
		this.currentArea = area;
	}

	public List<Area> getAllAreas() {
		return this.areas;
	}

	public void reloadAllAreas() {
		for (Area area : this.areas) {
			area.loadModifiedPixelDataList();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.worldID;
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
		if (this.getClass() != obj.getClass() || !(obj instanceof OverWorld)) {
			return false;
		}
		OverWorld other = (OverWorld) obj;
		if (this.worldID != other.worldID) {
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

	// Not sure if these width and height values are useful...
	public int getCurrentAreaWidth() {
		return this.currentArea.getWidth();
	}

	public int getCurrentAreaHeight() {
		return this.currentArea.getHeight();
	}

	@Override
	public void tick() {
		if (!this.invertBitmapColors)
			this.player.tick();
		if (this.currentArea == null)
			this.currentArea = this.areas.get(0);
		this.currentArea.tick();

		if (this.currentArea.playerIsInWarpZone()
			|| (this.currentArea.isDisplayingExitArrow() && this.player.isColliding())) {
			this.handleWarpPointEvent();
		}

		if (!this.player.isLockedWalking()) {
			if (this.currentArea != null) {
				if (this.currentArea.getSectorID() != this.currentAreaSectorID) {
					this.currentAreaSectorID = this.currentArea.getSectorID();
					// This is where you get the latest sector id at.
					Debug.log("Area: " + this.currentArea.getAreaID() + " Sector: " + this.currentArea.getSectorID());
				}
			}
		}

		if (this.currentArea != null && !this.currentArea.isBeingTriggered() && !this.player.isInteracting() && Player.isMovementsLocked()) {
			Player.unlockMovements();
		}

		// this.handlePlayerInteractions();
		// this.handleDialogues();
	}

	/**
	 * <p>
	 * Handles player interactions with objects in the world. When the player is interacting, it passes
	 * the interacted object's interaction ID number to the OverWorld, where it handles the interaction
	 * ID accordingly.
	 * </p>
	 * 
	 * @throws GameException
	 * 
	 */
	/*
	private void handlePlayerInteractions() {
		// TODO: Fix the awkward interaction caused by so many states not working
		// properly.
		int interactionID = this.player.getInteractableID();
		if (this.player.isInteracting()) {
			if (interactionID != 0 && !this.currentArea.isBeingTriggered()) {
				int alpha = (interactionID >> 24) & 0xFF;
				int red = (interactionID >> 16) & 0xFF;
				switch (alpha) {
					case 0x03: {// Obstacles
						switch (red) {
							case 0x05: {// Signs
								int dialogueID = (interactionID & 0xFFFF);
								SIGN_LOOP:
								for (Map.Entry<Dialogue, Integer> entry : WorldConstants.signTexts) {
									if (entry.getValue() == dialogueID) {
										this.newDialogues.add(entry.getKey());
										break SIGN_LOOP;
									}
								}
								break;
							}
							default: // Other obstacles
								List<Obstacle> list = this.currentArea.getObstaclesList();
								OBSTACLE_LOOP:
								for (int i = 0; i < list.size(); i++) {
									Obstacle obstacle = list.get(i);
									if (obstacle.getID() != red)
										continue;
									try {
										this.newDialogues = obstacle.getDialogues();
									}
									catch (GameException e) {
										throw new RuntimeException(e);
									}
									break OBSTACLE_LOOP;
								}
								break;
						}
						break;
					}
					case 0x0A: {// Item
						// ItemText text = null;
						for (Entry<ItemText, Item> entry : WorldConstants.items) {
							if (entry.getKey().id == red) {
								// text = entry.getKey();
								break;
							}
						}
						if (this.newDialogues == null) {
							this.newDialogues.add(
								DialogueBuilder.createText(
									text.itemName + " has been found.",
									Dialogue.MAX_STRING_LENGTH, Dialogue.DIALOGUE_ALERT, true
								)
							);
							new Thread(new Runnable() {
								@Override
								public void run() {
									PixelData data = OverWorld.this.currentArea.getCurrentPixelData();
									switch (OverWorld.this.player.getFacing()) {
										case Character.UP:
											OverWorld.this.currentArea.setPixelData(
												data, OverWorld.this.player.getXInArea(),
												OverWorld.this.player.getYInArea() - 1
											);
											break;
										case Character.DOWN:
											OverWorld.this.currentArea.setPixelData(
												data, OverWorld.this.player.getXInArea(),
												OverWorld.this.player.getYInArea() + 1
											);
											break;
										case Character.LEFT:
											OverWorld.this.currentArea.setPixelData(
												data, OverWorld.this.player.getXInArea() - 1,
												OverWorld.this.player.getYInArea()
											);
											break;
										case Character.RIGHT:
											OverWorld.this.currentArea.setPixelData(
												data, OverWorld.this.player.getXInArea() + 1,
												OverWorld.this.player.getYInArea()
											);
											break;
									}
								}
							}).start();
							Inventory inventory = this.game.getInventory();
							inventory.addItem(text);
						}
						break;
					}
				}
			}
			else {
				this.player.stopInteraction();
			}
		}
		else {
			if (this.currentArea != null && !this.currentArea.isBeingTriggered()) {
				if (Player.isMovementsLocked())
					Player.unlockMovements();
			}
		}
	}
	
	private void handleDialogues() {
		if (this.newDialogues != null) {
			// The order is IMPORTANT!!
			if (this.newDialogues.size() == 1) {
				if (this.newDialogues.get(this.newDialoguesIterator).isDialogueCompleted()
					&& this.newDialogues.get(this.newDialoguesIterator).isScrolling()) {
					Player.unlockMovements();
					this.newDialogues.get(this.newDialoguesIterator).resetDialogue();
					this.newDialogues = null;
					this.newDialoguesIterator = 0;
					this.player.stopInteraction();
				}
				else if (this.newDialogues.get(this.newDialoguesIterator).isDialogueCompleted()
					&& !this.newDialogues.get(this.newDialoguesIterator).isScrolling()) {
					this.newDialogues.get(this.newDialoguesIterator).tick();
				}
				else if (this.newDialogues.get(this.newDialoguesIterator).isDialogueTextSet()
					&& !(this.newDialogues.get(this.newDialoguesIterator).isDialogueCompleted()
						&& this.newDialogues.get(this.newDialoguesIterator).isShowingDialog())) {
					Player.lockMovements();
					this.newDialogues.get(this.newDialoguesIterator).tick();
				}
			}
			else {
				switch (this.newDialogues.get(this.newDialoguesIterator).getDialogueType()) {
					case Dialogue.DIALOGUE_SPEECH:
						if (this.newDialogues.get(this.newDialoguesIterator).isDialogueCompleted()
							&& this.newDialogues.get(this.newDialoguesIterator).isScrolling()) {
							Player.unlockMovements();
							this.newDialogues.get(this.newDialoguesIterator).resetDialogue();
							if (this.newDialoguesIterator < this.newDialogues.size() - 1) {
								this.newDialoguesIterator++;
								this.handleDialogues();
							}
							else {
								this.newDialogues = null;
								this.newDialoguesIterator = 0;
								this.player.stopInteraction();
							}
						}
						else if (this.newDialogues.get(this.newDialoguesIterator).isDialogueCompleted()
							&& !this.newDialogues.get(this.newDialoguesIterator).isScrolling()) {
							if (!this.newDialogues.get(this.newDialoguesIterator).isShowingDialog()) {
								Player.unlockMovements();
								this.newDialogues.get(this.newDialoguesIterator).resetDialogue();
								if (this.newDialoguesIterator < this.newDialogues.size() - 1) {
									this.newDialoguesIterator++;
									this.handleDialogues();
								}
								else {
									this.newDialogues = null;
									this.newDialoguesIterator = 0;
									this.player.stopInteraction();
								}
							}
							else
								this.newDialogues.get(this.newDialoguesIterator).tick();
						}
						else if (this.newDialogues.get(this.newDialoguesIterator).isDialogueTextSet()
							&& !(this.newDialogues.get(this.newDialoguesIterator).isDialogueCompleted()
								&& this.newDialogues.get(this.newDialoguesIterator).isShowingDialog())) {
							Player.lockMovements();
							this.newDialogues.get(this.newDialoguesIterator).tick();
						}
						break;
					case Dialogue.DIALOGUE_QUESTION:
						if (!this.newDialogues.get(this.newDialoguesIterator).yesNoQuestionHasBeenAnswered()) {
							this.newDialogues.get(this.newDialoguesIterator).tick();
							if (!Player.isMovementsLocked())
								Player.lockMovements();
						}
						if (this.newDialogues.get(this.newDialoguesIterator).getAnswerToSimpleQuestion() == Boolean.TRUE) {
							this.newDialogues.get(this.newDialoguesIterator).resetDialogue();
							if (this.newDialoguesIterator < this.newDialogues.size() - 1) {
								this.newDialoguesIterator++;
								this.handleDialogues();
							}
							else {
								this.newDialogues.get(this.newDialoguesIterator).resetDialogue();
								this.newDialogues = null;
								this.newDialoguesIterator = 0;
								this.player.stopInteraction();
								Player.unlockMovements();
							}
						}
						else if (this.newDialogues.get(this.newDialoguesIterator)
							.getAnswerToSimpleQuestion() == Boolean.FALSE) {
							this.newDialogues.get(this.newDialoguesIterator).resetDialogue();
							this.newDialogues = null;
							this.newDialoguesIterator = 0;
							this.player.stopInteraction();
							Player.unlockMovements();
						}
						break;
				}
			}
		}
	}
	*/

	private void handleWarpPointEvent() {
		boolean currentAreaFound = false;
		int currentAreaID = this.currentArea.getAreaID();
		// boolean doorIsLocked = false;
		for (int i = 0; i < this.areas.size(); i++) {
			if (this.areas.get(i) == null) {
				// Door needs to be blocking if there is no area to walk to.
				// doorIsLocked = true;
				continue;
			}
			if (this.areas.get(i).getAreaID() == currentAreaID) {
				currentAreaFound = true;
				break;
			}
		}
		if (currentAreaFound) {
			PixelData data = this.currentArea.getCurrentPixelData();
			int targetAreaID = data.getTargetAreaID();
			this.currentArea = WorldConstants.convertToArea(this.areas, targetAreaID);
			if (this.currentArea == null)
				return;
			this.currentArea.setPlayer(this.player);
			this.currentArea.setDefaultPosition(data);
			this.invertBitmapColors = true;

			switch ((data.getColor() >> 24) & 0xFF) {
				case 0x04: // Warp point
					this.player.forceLockWalking();
					break;
				case 0x09: // Door
					this.player.tick();
					break;
				case 0x0B: // Carpet (Indoors)
				case 0x0C: // Carpet (Outdoors)
					this.player.forceLockWalking();
					this.player.tick();
					break;
			}

			this.currentArea.playerWentPastWarpZone();
		}
		/*
		else if (doorIsLocked) {
			PixelData data = this.currentArea.getCurrentPixelData();
			// Pixel data color, alpha value is 0x09 = House door
			if (((data.getColor() >> 24) & 0xFF) == 0x09) {
			}
			int targetAreaID = 0;
			if (WorldConstants.isModsEnabled.booleanValue() &&
				this.currentArea.getAreaID() < 1000)
				targetAreaID = data.getTargetAreaID() + 1000;
			else
				targetAreaID = data.getTargetAreaID();
		}
		*/
	}

	@Override
	public void render(Scene screen, Graphics graphics, int xPlayerPos, int yPlayerPos) {
		// OverWorld offsets are not set.
		// Setting area offsets with player positions

		screen.setOffset(screen.getWidth() / 2 - Tileable.WIDTH, (screen.getHeight() - Tileable.HEIGHT) / 2);
		if (this.currentArea != null)
			this.currentArea.render(screen, graphics, xPlayerPos, yPlayerPos);
		screen.setOffset(0, 0);

		if (this.invertBitmapColors) {
			if (screen.getRenderingEffectTick() == (byte) 0x7) {
				screen.setRenderingEffectTick((byte) 0x0);
			}
			this.invertBitmapColors = screen.invert();
		}

		if (screen.getRenderingEffectTick() < (byte) 0x4 || screen.getRenderingEffectTick() >= (byte) 0x7)
			this.player.render(screen, graphics, 0, 0);

		/*
		if (this.newDialogues != null && this.newDialogues.size() > 0) {
			Dialogue dialogue = this.newDialogues.get(this.newDialoguesIterator);
			dialogue.renderInformationBox(screen, 0, 6, 9, 2);
			dialogue.render(screen, screen.getBufferedImage().createGraphics());
		}
		*/
	}

	// ---------------------------------------------------------------------------------------
	// Private methods

	private void setCurrentAreaSector(int i) {
		this.currentAreaSectorID = i;
	}
}
