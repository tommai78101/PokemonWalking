/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package level;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import abstracts.Item;
import abstracts.World;
import dialogue.NewDialogue;
import entity.Player;
import interfaces.InterfaceTile;
import item.ItemText;
import main.Game;
import obstacle.Obstacle;
import screen.BaseScreen;
import submenu.Inventory;

public class OverWorld extends World {
	// Overworld properties.
	private boolean invertBitmapColors;
	private int currentAreaSectorID;
	private NewDialogue[] newDialogues;
	private int newDialoguesIterator;

	/**
	 * Initializes the overworld in the game.
	 * 
	 * All game entities are to be loaded through this method.
	 * 
	 * @param Player Takes a Player object. The overworld then loads all related
	 *               properties in respect to the Player object.
	 */
	public OverWorld(Player player, Game game) {
		// There should be a maximum number of areas available for the OverWorld.
		// All areas defined must be placed in WorldConstants.

		// Player and Game.
		super(player, game);

		this.worldID = WorldConstants.OVERWORLD;

		if (!this.areas.isEmpty())
			this.areas.clear();
		WorldConstants.getAllNewScripts();
		this.areas = WorldConstants.getAllNewAreas();

		// Overworld properties
		this.invertBitmapColors = false;

		// Going to set this area as test default only. This will need to change in the
		// future.
		this.currentArea = this.areas.get(0);
		this.setCurrentArea(this.areas.get(0));
		this.currentArea.setPlayer(player);
		this.currentArea.setDebugDefaultPosition();
		this.setCurrentAreaSector(0);
		// TODO: Add a method that executes according to the sector ID. Basically,
		// it needs to tell the player that they entered a new sector.
		// Needs a marker in the area that points to where the area connects together.

		// Dialogue
		this.newDialogues = null;
		this.newDialoguesIterator = 0;
	}

	// Will add this in the future. Currently, the only entity is Player.
	// public void addEntity(Entity e) {
	// //e.initialize(this);
	// //this.tiles.add(e);
	// }

	// Worlds no longer need to calculate total width and height.
	// public int getTotalWidth() {
	// int result = 0;
	// for (Area a : areas) {
	// if (a != null)
	// result += a.getWidth();
	// }
	// return result;
	// }
	//
	// public int getTotalHeight() {
	// int result = 0;
	// for (Area a : areas) {
	// if (a != null)
	// result += a.getHeight();
	// }
	// return result;
	// }

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
					System.out
							.println("Area: " + this.currentArea.getAreaID() + " Sector: " + currentArea.getSectorID());
				}
			}
		}

		this.handlePlayerInteractions();
		this.handleDialogues();
	}

	/**
	 * <p>
	 * Handles player interactions with objects in the world. When the player is
	 * interacting, it passes the interacted object's interaction ID number to the
	 * OverWorld, where it handles the interaction ID accordingly.
	 * </p>
	 * 
	 */
	private void handlePlayerInteractions() {
		// TODO: Fix the awkward interaction caused by so many states not working
		// properly.
		int interactionID = player.getInteractionID();
		if (this.player.isInteracting() && interactionID != 0 && !this.currentArea.isBeingTriggered()) {
			int alpha = (interactionID >> 24) & 0xFF;
			int red = (interactionID >> 16) & 0xFF;
			switch (alpha) {
			case 0x03: {// Obstacles
				switch (red) {
				case 0x05: {// Signs
					int dialogueID = (interactionID & 0xFFFF);
					SIGN_LOOP: for (Map.Entry<NewDialogue, Integer> entry : WorldConstants.signTexts) {
						if (entry.getValue() == dialogueID) {
							this.newDialogues = new NewDialogue[] { entry.getKey() };
							break SIGN_LOOP;
						}
					}
					break;
				}
				default: // Other obstacles
					ArrayList<Obstacle> list = this.currentArea.getObstaclesList();
					OBSTACLE_LOOP: for (int i = 0; i < list.size(); i++) {
						Obstacle obstacle = list.get(i);
						if (obstacle.getID() != red)
							continue;
						this.newDialogues = obstacle.getDialogues();
						break OBSTACLE_LOOP;
					}
					break;
				}
				break;
			}
			case 0x0A: {// Item
				ItemText text = null;
				for (Entry<ItemText, Item> entry : WorldConstants.items) {
					if (entry.getKey().id == red) {
						text = entry.getKey();
						break;
					}
				}
				if (this.newDialogues == null) {
					this.newDialogues = new NewDialogue[] { NewDialogue.createText(text.itemName + " has been found.",
							NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_ALERT, true) };
					new Thread(new Runnable() {
						@Override
						public void run() {
							PixelData data = currentArea.getCurrentPixelData();
							switch (player.getFacing()) {
							case Player.UP:
								currentArea.setPixelData(data, player.getXInArea(), player.getYInArea() - 1);
								break;
							case Player.DOWN:
								currentArea.setPixelData(data, player.getXInArea(), player.getYInArea() + 1);
								break;
							case Player.LEFT:
								currentArea.setPixelData(data, player.getXInArea() - 1, player.getYInArea());
								break;
							case Player.RIGHT:
								currentArea.setPixelData(data, player.getXInArea() + 1, player.getYInArea());
								break;
							}
						}
					}).start();
					Inventory inventory = this.game.getStartMenu().getInventory();
					inventory.addItem(text);
				}
				break;
			}
			}
		} else {
			if (this.currentArea != null && !this.currentArea.isBeingTriggered()) {
				if (Player.isMovementsLocked())
					Player.unlockMovements();
			}
		}
	}

	private void handleDialogues() {
		if (this.newDialogues != null) {
			// The order is IMPORTANT!!
			if (this.newDialogues.length == 1) {
				if (this.newDialogues[this.newDialoguesIterator].isDialogueCompleted()
						&& this.newDialogues[this.newDialoguesIterator].isScrolling()) {
					Player.unlockMovements();
					this.newDialogues[this.newDialoguesIterator].resetDialogue();
					this.newDialogues = null;
					this.newDialoguesIterator = 0;
					this.player.stopInteraction();
				} else if (this.newDialogues[this.newDialoguesIterator].isDialogueCompleted()
						&& !this.newDialogues[this.newDialoguesIterator].isScrolling()) {
					this.newDialogues[this.newDialoguesIterator].tick();
				} else if (this.newDialogues[this.newDialoguesIterator].isDialogueTextSet()
						&& !(this.newDialogues[this.newDialoguesIterator].isDialogueCompleted()
								&& this.newDialogues[this.newDialoguesIterator].isShowingDialog())) {
					Player.lockMovements();
					this.newDialogues[this.newDialoguesIterator].tick();
				}
			} else {
				switch (this.newDialogues[this.newDialoguesIterator].getDialogueType()) {
				case NewDialogue.DIALOGUE_SPEECH:
					if (this.newDialogues[this.newDialoguesIterator].isDialogueCompleted()
							&& this.newDialogues[this.newDialoguesIterator].isScrolling()) {
						Player.unlockMovements();
						this.newDialogues[this.newDialoguesIterator].resetDialogue();
						if (this.newDialoguesIterator < this.newDialogues.length - 1) {
							this.newDialoguesIterator++;
							this.handleDialogues();
						} else {
							this.newDialogues = null;
							this.newDialoguesIterator = 0;
							this.player.stopInteraction();
						}
					} else if (this.newDialogues[this.newDialoguesIterator].isDialogueCompleted()
							&& !this.newDialogues[this.newDialoguesIterator].isScrolling()) {
						if (!this.newDialogues[this.newDialoguesIterator].isShowingDialog()) {
							Player.unlockMovements();
							this.newDialogues[this.newDialoguesIterator].resetDialogue();
							if (this.newDialoguesIterator < this.newDialogues.length - 1) {
								this.newDialoguesIterator++;
								this.handleDialogues();
							} else {
								this.newDialogues = null;
								this.newDialoguesIterator = 0;
								this.player.stopInteraction();
							}
						} else
							this.newDialogues[this.newDialoguesIterator].tick();
					} else if (this.newDialogues[this.newDialoguesIterator].isDialogueTextSet()
							&& !(this.newDialogues[this.newDialoguesIterator].isDialogueCompleted()
									&& this.newDialogues[this.newDialoguesIterator].isShowingDialog())) {
						Player.lockMovements();
						this.newDialogues[this.newDialoguesIterator].tick();
					}
					break;
				case NewDialogue.DIALOGUE_QUESTION:
					if (!this.newDialogues[this.newDialoguesIterator].yesNoQuestionHasBeenAnswered()) {
						this.newDialogues[this.newDialoguesIterator].tick();
						if (!Player.isMovementsLocked())
							Player.lockMovements();
					}
					if (this.newDialogues[this.newDialoguesIterator].getAnswerToSimpleQuestion() == Boolean.TRUE) {
						this.newDialogues[this.newDialoguesIterator].resetDialogue();
						if (this.newDialoguesIterator < this.newDialogues.length - 1) {
							this.newDialoguesIterator++;
							this.handleDialogues();
						} else {
							this.newDialogues[this.newDialoguesIterator].resetDialogue();
							this.newDialogues = null;
							this.newDialoguesIterator = 0;
							this.player.stopInteraction();
							Player.unlockMovements();
						}
					} else if (this.newDialogues[this.newDialoguesIterator]
							.getAnswerToSimpleQuestion() == Boolean.FALSE) {
						this.newDialogues[this.newDialoguesIterator].resetDialogue();
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

	private void handleWarpPointEvent() {
		boolean currentAreaFound = false;
		int currentAreaID = 0;
		if (WorldConstants.isModsEnabled.booleanValue() && this.currentArea.getAreaID() < 1000)
			currentAreaID = this.currentArea.getAreaID() + 1000;
		else
			currentAreaID = this.currentArea.getAreaID();
		// boolean doorIsLocked = false;
		for (int i = 0; i < this.areas.size(); i++) {
			if (this.areas.get(i) == null) {
				// Door needs to be blocking if there is no area to walk to.
				// doorIsLocked = true;
				continue;
			}
			if (this.areas.get(i).getAreaID() == currentAreaID) {
				this.areas.set(i, this.currentArea);
				currentAreaFound = true;
				break;
			}
		}
		if (currentAreaFound) {
			PixelData data = this.currentArea.getCurrentPixelData();
			int targetAreaID = 0;
			if (WorldConstants.isModsEnabled.booleanValue() && this.currentArea.getAreaID() < 1000)
				targetAreaID = data.getTargetAreaID() + 1000;
			else
				targetAreaID = data.getTargetAreaID();
			this.currentArea = WorldConstants.convertToArea(areas, targetAreaID);
			if (currentArea == null)
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
		// else if (doorIsLocked) {
		// PixelData data = this.currentArea.getCurrentPixelData();
		// //Pixel data color, alpha value is 0x09 = House door
		// if (((data.getColor() >> 24) & 0xFF) == 0x09){
		//
		// }
		// // int targetAreaID = 0;
		// // if (WorldConstants.isModsEnabled.booleanValue() &&
		// this.currentArea.getAreaID() < 1000)
		// // targetAreaID = data.getTargetAreaID() + 1000;
		// // else
		// // targetAreaID = data.getTargetAreaID();
		// }
	}

	@Override
	public void render(BaseScreen screen, int xPlayerPos, int yPlayerPos) {
		// OverWorld offsets are not set.
		// Setting area offsets with player positions

		screen.setOffset(screen.getWidth() / 2 - InterfaceTile.WIDTH, (screen.getHeight() - InterfaceTile.HEIGHT) / 2);
		if (this.currentArea != null)
			this.currentArea.render(screen, xPlayerPos, yPlayerPos);
		screen.setOffset(0, 0);

		if (this.invertBitmapColors) {
			if (screen.getRenderingEffectTick() == (byte) 0x7) {
				screen.setRenderingEffectTick((byte) 0x0);
			}
			this.invertBitmapColors = screen.invert();
		}

		if (screen.getRenderingEffectTick() < (byte) 0x4 || screen.getRenderingEffectTick() >= (byte) 0x7)
			player.render(screen, 0, 0);

		if (this.newDialogues != null && this.newDialogues.length > 0) {
			NewDialogue.renderDialogBox(screen, 0, 6, 9, 2);
			this.newDialogues[this.newDialoguesIterator].render(screen, screen.getBufferedImage().createGraphics());
		}
	}

	// ---------------------------------------------------------------------------------------
	// Private methods

	private void setCurrentAreaSector(int i) {
		this.currentAreaSectorID = i;
	}
}
