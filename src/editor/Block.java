package editor;

import abstracts.Tile;

public class Block extends Tile {
	private int xPosition;
	private int yPosition;
	
	public Block() {
		
	}
	
	public void render(int[] pixels) {
		this.xPosition = Tile.WIDTH;
		this.yPosition = Tile.HEIGHT;
		//Each tile
		for (int y = 0; y < Tile.HEIGHT; y++) {
			for (int x = 0; x < Tile.WIDTH; x++) {
				int result = ((y + yPosition) * LevelEditor.WIDTH + (x + xPosition));
				pixels[result] = 0xFFFFFFFF;
			}
		}
	}
}
