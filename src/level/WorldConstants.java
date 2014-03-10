package level;

import resources.Art;

public class WorldConstants {
	private WorldConstants() {
		
	}
	
	public static final int TEST_WORLD_1 = 0x01;
	public static final int TEST_WORLD_2 = 0x02;
	
	public static Area convertToArea(int areaID) {
		switch (areaID) {
			case TEST_WORLD_1:
				return new Area(Art.testArea);
			case TEST_WORLD_2:
				return new Area(Art.testArea2);
			default:
				return null;
		}
	}
}
