package level;

import java.util.ArrayList;
import java.util.List;
import resources.Art;

public class WorldConstants {
	private WorldConstants() {
		//Add/delete a map area, update everything shown below.
	}
	
	//Area IDs
	public static final int TEST_WORLD_1 = 0x01;
	public static final int TEST_WORLD_2 = 0x02;
	public static final int TEST_WORLD_3 = 0x03;
	public static final int TEST_WORLD_4 = 0x04;
	public static final int DEBUG = 0x05;
	
	//Biome Colors
	public static final int GRASS_GREEN = 0xFFA4E767;
	public static final int MOUNTAIN_BROWN = 0xFFD5B23B;
	
	public static Area convertToArea(int areaID) {
		switch (areaID) {
			case TEST_WORLD_1:
				return new Area(Art.testArea, TEST_WORLD_1);
			case TEST_WORLD_2:
				return new Area(Art.testArea2, TEST_WORLD_2);
			case TEST_WORLD_3:
				return new Area(Art.testArea3, TEST_WORLD_3);
			case TEST_WORLD_4:
				return new Area(Art.testArea4, TEST_WORLD_4);
			case DEBUG:
				return new Area(Art.testArea_debug, DEBUG);
			default:
				return null;
		}
	}
	
	public static List<Area> getAllAreas() {
		List<Area> result = new ArrayList<Area>();
		result.add(new Area(Art.testArea, TEST_WORLD_1));
		result.add(new Area(Art.testArea2, TEST_WORLD_2));
		result.add(new Area(Art.testArea3, TEST_WORLD_3));
		result.add(new Area(Art.testArea4, TEST_WORLD_4));
		result.add(new Area(Art.testArea_debug, DEBUG));
		return result;
	}
}
