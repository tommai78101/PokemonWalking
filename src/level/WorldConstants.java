package level;

import java.util.ArrayList;
import java.util.List;

import resources.Art;

public class WorldConstants {
	private WorldConstants() {
		
	}
	
	public static final int TEST_WORLD_1 = 0x01;
	public static final int TEST_WORLD_2 = 0x02;
	
	public static Area convertToArea(int areaID) {
		switch (areaID) {
			case TEST_WORLD_1:
				return new Area(Art.testArea, TEST_WORLD_1);
			case TEST_WORLD_2:
				return new Area(Art.testArea2, TEST_WORLD_2);
			default:
				return null;
		}
	}
	
	public static List<Area> getAllAreas() {
		List<Area> result = new ArrayList<Area>();
		result.add(new Area(Art.testArea, TEST_WORLD_1));
		result.add(new Area(Art.testArea2, TEST_WORLD_2));
		return result;
	}
}
