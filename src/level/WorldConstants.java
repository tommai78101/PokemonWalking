/**
 * THIS IS CREATED BY tom_mai78101. GIVE PROJECT CREATOR ITS CREDITS.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. THIS IS A CLONE. 
 * */

package level;

import java.util.ArrayList;
import java.util.List;

import resources.Art;

public class WorldConstants {
	private WorldConstants() {
		// Add/delete a map area, update everything shown below.
	}

	// Area IDs
	public static final int TEST_WORLD_1 = 0x01;
	public static final int TEST_WORLD_2 = 0x02;
	public static final int TEST_WORLD_3 = 0x03;
	public static final int TEST_WORLD_4 = 0x04;
	public static final int DEBUG = 0x05;

	// Biome Colors
	public static final int GRASS_GREEN = 0xFFA4E767;
	public static final int MOUNTAIN_BROWN = 0xFFD5B23B;

	// Building Roof Colors / Main Area Color Theme
	public static final int AREA_1_COLOR = 0xFFA495B0;

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
			if (area.getAreaID() == areaID)
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
		case TEST_WORLD_3:
			area = new Area(Art.testArea3, TEST_WORLD_3);
			break;
		case TEST_WORLD_4:
			area = new Area(Art.testArea4, TEST_WORLD_4);
			break;
		case DEBUG:
			area = new Area(Art.testArea_debug, DEBUG);
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
	public static List<Area> getAllAreas() {
		List<Area> result = new ArrayList<Area>();
		result.add(new Area(Art.testArea, TEST_WORLD_1));
		result.add(new Area(Art.testArea2, TEST_WORLD_2));
		result.add(new Area(Art.testArea3, TEST_WORLD_3));
		result.add(new Area(Art.testArea4, TEST_WORLD_4));
		result.add(new Area(Art.testArea_debug, DEBUG));
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
		default:
			return 0;
		}
	}
}
