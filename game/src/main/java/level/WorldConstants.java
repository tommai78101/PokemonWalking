/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.Debug;
import dialogue.Dialogue;
import resources.Art;
import resources.Mod;
import screen.Bitmap;
import script.Script;
import utility.DialogueBuilder;

public class WorldConstants {
	private WorldConstants() {
		// Add/delete a map area, update everything shown below.
	}

	// Item IDs
	public static final int ITEM_RETURN = 0;
	public static final int ITEM_TEST = 1;
	public static final int ITEM_3_OPTIONS = 2;
	public static final int ITEM_BICYCLE = 3;

	// Entity Types
	public static final int ENTITY_TYPE_OBSTACLE = 0x03;
	public static final int ENTITY_TYPE_CHARACTER = 0x0E;
	public static final int ENTITY_TYPE_ITEM = 0x0A;

	// Biome Colors
	public static final int GRASS_GREEN = 0xFFA4E767;
	public static final int MOUNTAIN_BROWN = 0xFFD5B23B;

	// Building Roof Colors / Main Area Color Theme
	public static final int AREA_1_COLOR = 0xFFC495B0;
	public static final int AREA_2_COLOR = 0xFF345CBD;

	// World IDs
	public static final int OVERWORLD = 0x0A000001;

	// Dialogues
	public static List<Map.Entry<Dialogue, Integer>> signTexts = DialogueBuilder.loadDialogues("art/dialogue/dialogue.txt");

	// NOTE(Thompson): We're going to start building items instead of loading items from script files.
	// Items
	// public static HashMap<Integer, ItemText> itemms = Item.loadItemResources("item/items.txt");
	// public static List<Map.Entry<ItemText, Item>> items =
	// ItemBuilder.loadItems("art/item/items.txt");

	// Areas
	public static final int CHECKSUM_MAX_BYTES_LENGTH = 16;
	public static List<Area> areas = new ArrayList<>(0);
	public static List<Area> moddedAreas = new ArrayList<>(0);

	// Main Menu Item Names
	public static final String MENU_ITEM_NAME_EXIT = "EXIT";
	public static final String MENU_ITEM_NAME_INVENTORY = "PACK";
	public static final String MENU_ITEM_NAME_SAVE = "SAVE";

	// Main Menu Item Descriptions
	public static final String MENU_ITEM_DESC_EXIT = "Exit the menu.";
	public static final String MENU_ITEM_DESC_INVENTORY = "Open the pack.";
	public static final String MENU_ITEM_DESC_SAVE = "Save the game.";

	// Default scripts path. For internal use only.
	public static final String ScriptsDefaultPath = "/art/script";
	
	// Modded maps enabled?
	public static Boolean isModsEnabled = null;

	// Movement
	// TODO (11/24/2014): Make map more flexible by allowing scripting files of
	// different filenames to be loaded. Not sure where it was being loaded.
	public static List<Script> scripts = new ArrayList<>(0);
	public static List<Script> moddedScripts = new ArrayList<>(0);
	// public static ArrayList<Script> gameScripts =
	// Script.loadScript("script/aas.script"); // TODO (6/19/2015): Check to see why
	// there's a need to load "aas.script".

	// Custom scripts
	// This array list is made for a script + area file name. They both go together.
	// public static ArrayList<Map.Entry<Script, String>> customScripts = new
	// ArrayList<Map.Entry<Script, String>>();

	// All bitmaps
	public static List<Bitmap> bitmaps = new ArrayList<>();

	/**
	 * Returns the area matching the given area ID value.
	 * 
	 * @param areaID
	 *            The area ID value.
	 * @return The Area object with the matching area ID value. If no matching value exists, it returns
	 *         null.
	 */
	public static Area convertToArea(List<Area> areas, int areaID) {
		for (int i = 0; i < areas.size(); i++) {
			Area area = areas.get(i);
			if (area != null && area.getAreaID() == areaID)
				return area;
		}
		return null;
	}

	/**
	 * <p>
	 * Checks to see if there exists custom maps in the "mod" folder.
	 * </p>
	 * 
	 * @return Nothing
	 */
	public static void checkForMods() {
		if (WorldConstants.isModsEnabled == null) {
			if (Mod.moddedAreas.isEmpty()) {
				WorldConstants.isModsEnabled = Boolean.FALSE;
			}
			else {
				WorldConstants.isModsEnabled = Boolean.TRUE;
			}
		}
	}

	/**
	 * Returns all available areas defined.
	 * 
	 * @return A List<Area> object containing all available areas in specified order defined.
	 */
	public static List<Area> getAllNewAreas() {
		// List<Area> result = new ArrayList<Area>();
		if (WorldConstants.isModsEnabled == Boolean.FALSE || WorldConstants.isModsEnabled == null) {
			if (WorldConstants.areas.isEmpty()) {
				WorldConstants.areas.add(new Area(Art.testArea));
				WorldConstants.areas.add(new Area(Art.testArea2));
				WorldConstants.areas.add(new Area(Art.debugArea));
			}
			else {
				// WorldConstants.addNewArea(Art.testArea);
				// WorldConstants.addNewArea(Art.testArea2);
			}
			// result.add(new Area(Art.testArea3, TEST_WORLD_3));
			// result.add(new Area(Art.testArea4, TEST_WORLD_4));
			// result.add(new Area(Art.testArea_debug, Debug));
			return WorldConstants.areas;
		}
		else {
			for (int i = 0; i < Mod.moddedAreas.size(); i++) {
				Map.Entry<Bitmap, Integer> entry = Mod.moddedAreas.get(i);
				WorldConstants.moddedAreas.add(new Area(entry.getKey()));
			}
			return WorldConstants.moddedAreas;
		}
	}

	/**
	 * Returns all available scripts defined.
	 * 
	 * @return A List<Script> object containing all available scripts in specified order defined.
	 */
	public static List<Script> getAllNewScripts() {
		if (WorldConstants.isModsEnabled == null) {
			if (Mod.moddedAreas.isEmpty()) {
				WorldConstants.isModsEnabled = Boolean.FALSE;
			}
			else {
				WorldConstants.isModsEnabled = Boolean.TRUE;
			}
		}
		if (WorldConstants.isModsEnabled.booleanValue()) {
			WorldConstants.moddedScripts = Script.loadModdedScriptsNew();
			return WorldConstants.moddedScripts;
		}
		else {
			WorldConstants.scripts = Script.loadDefaultScripts();
			return WorldConstants.scripts;
		}
	}

	/**
	 * Goes with the static method, "getAllNewAreas()".
	 */
	@SuppressWarnings("unused")
	private static void addNewArea(Bitmap bitmap) {
		Debug.toDo("Utilize this method when the opportunity arises.");
		int areaID = Area.getAreaIDFromBitmap(bitmap);
		// Java 8 feature
		boolean exists = WorldConstants.areas.stream()
		    .filter(chosenArea -> chosenArea.getAreaID() == areaID)
		    .findFirst()
		    .isPresent();
		if (!exists) {
			WorldConstants.areas.add(new Area(bitmap));
		}
	}

	/**
	 * Returns the area color theme of the given area ID.
	 * 
	 * @param areaID
	 *            The area ID value.
	 * @return The primary area color of full opacity.
	 */
	public static int convertToAreaColor(int areaID) {
		switch (areaID % 2) {
			case 0x01:
				return WorldConstants.AREA_1_COLOR;
			case 0x00:
				return WorldConstants.AREA_2_COLOR;
			default:
				return 0;
		}
	}
}
