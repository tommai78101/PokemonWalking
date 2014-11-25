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

import item.ItemText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import resources.Art;
import resources.Mod;
import screen.BaseBitmap;
import script.Script;
import abstracts.Item;
import dialogue.NewDialogue;

public class WorldConstants {
	private WorldConstants() {
		// Add/delete a map area, update everything shown below.
	}
	
	// Area IDs
	public static final int TEST_WORLD_1 = 0x01;
	public static final int TEST_WORLD_2 = 0x02;
	public static final int TEST_WORLD_3 = 0x03;
	
	// Item IDs
	public static final int ITEM_RETURN = 0;
	public static final int ITEM_TEST = 1;
	public static final int ITEM_3_OPTIONS = 2;
	public static final int ITEM_BICYCLE = 3;
	
	// Biome Colors
	public static final int GRASS_GREEN = 0xFFA4E767;
	public static final int MOUNTAIN_BROWN = 0xFFD5B23B;
	
	// Building Roof Colors / Main Area Color Theme
	public static final int AREA_1_COLOR = 0xFFC495B0;
	public static final int AREA_2_COLOR = 0xFF345CBD;
	
	// World IDs
	public static final int OVERWORLD = 0x0A000001;
	
	// Dialogues
	public static ArrayList<Map.Entry<NewDialogue, Integer>> signTexts = NewDialogue.loadDialogues("dialogue/dialogue.txt");
	
	// Items
	//public static HashMap<Integer, ItemText> itemms = Item.loadItemResources("item/items.txt");
	public static ArrayList<Map.Entry<ItemText, Item>> items = Item.loadItems("item/items.txt");
	
	// Modded maps enabled?
	public static Boolean isModsEnabled = null;
	
	// Movement
	//TODO (11/24/2014): Make map more flexible by allowing scripting files of different filenames to be loaded. Not sure where it was being loaded.
	public static ArrayList<Script> scripts = Script.loadScript("script/scripts.txt");
	
	// All bitmaps
	public static ArrayList<BaseBitmap> bitmaps = new ArrayList<BaseBitmap>();
	
	/**
	 * Returns the area matching the given area ID value.
	 * 
	 * @param areaID
	 *            The area ID value.
	 * @return The Area object with the matching area ID value. If no matching value exists, it returns null.
	 * */
	public static Area convertToArea(List<Area> areas, int areaID) {
		for (int i = 0; i < areas.size(); i++) {
			Area area = areas.get(i);
			if (area != null && area.getAreaID() == areaID)
				return area;
		}
		Area area = null;
		switch (areaID) {
			case TEST_WORLD_1:
				area = new Area(Art.testArea, TEST_WORLD_1);
				break;
			case TEST_WORLD_2:
				area = new Area(Art.testArea2, TEST_WORLD_2);
				break;
			default:
				area = null;
				break;
		}
		areas.add(area);
		return area;
	}
	
	/**
	 * Returns all available areas defined.
	 * 
	 * @return A List<Area> object containing all available areas in specified order defined.
	 * */
	public static List<Area> getAllNewAreas() {
		List<Area> result = new ArrayList<Area>();
		if (WorldConstants.isModsEnabled == null) {
			if (Mod.moddedAreas.isEmpty()) {
				WorldConstants.isModsEnabled = Boolean.FALSE;
			}
			else {
				WorldConstants.isModsEnabled = Boolean.TRUE;
			}
		}
		if (WorldConstants.isModsEnabled == Boolean.FALSE) {
			result.add(new Area(Art.testArea, TEST_WORLD_1));
			result.add(new Area(Art.testArea2, TEST_WORLD_2));
//			result.add(new Area(Art.testArea3, TEST_WORLD_3));
//			result.add(new Area(Art.testArea4, TEST_WORLD_4));
//			result.add(new Area(Art.testArea_debug, DEBUG));
		}
		else { //Including NULL value.
			for (int i = 0; i < Mod.moddedAreas.size(); i++) {
				Map.Entry<BaseBitmap, Integer> entry = Mod.moddedAreas.get(i);
				result.add(new Area(entry.getKey(), entry.getValue()));
			}
		}
		return result;
	}
	
	/**
	 * Returns the area color theme of the given area ID.
	 * 
	 * @param areaID
	 *            The area ID value.
	 * @return The primary area color of full opacity.
	 * */
	public static int convertToAreaColor(int areaID) {
		switch (areaID) {
			case TEST_WORLD_1:
				return AREA_1_COLOR;
			case TEST_WORLD_2:
				return AREA_2_COLOR;
			default:
				return 0;
		}
	}
}
