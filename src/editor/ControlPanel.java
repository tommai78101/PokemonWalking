package editor;

public class ControlPanel {
	public static final int WIDTH = 50;
	public static final int HEIGHT = LevelEditor.HEIGHT;
	
	public ControlPanel() {
		
	}
	
	public void tick() {
		
	}
	
	public void render(int[] pixels) {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				int row = y * LevelEditor.WIDTH;
				pixels[row + x] = 0xFF77BBDD;
			}
		}
	}
}
