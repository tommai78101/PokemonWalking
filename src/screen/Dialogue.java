package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import main.MainComponent;
import resources.Art;

public class Dialogue {
	//According to the width and height of the dialog shown in the original games.
	public static final int WIDTH = 160;
	public static final int HEIGHT = 48;
	//When counting from top of the dialog to top of the text.
	public static final int FIRST_LINE_SPACING_HEIGHT = 16;
	public static final int SECOND_LINE_SPACING_HEIGHT = 32;
	//When counting from left of the dialog to the right of the text's first letter.
	//This is also how far the arrow is from the right of the dialog.
	public static final int TEXT_SPACING_WIDTH = 8;
	
	//Background dialog color is 0xFFF8F8F8, or all RGB values are 248/255.
	//Font size
	public static final int FONT_SIZE = 24;
	
	private BaseBitmap bitmap_base;
	private BaseBitmap bitmap_next;
	
	public Dialogue() {
		this.bitmap_base = Art.dialogue_base;
		this.bitmap_next = Art.dialogue_next;
	}
	
	public void tick() {
		
	}
	
	public void render(BaseScreen output) {
		//TODO: Make dialog box flexible to use. There is no way that there is just one kind of
		//dialog box used throughout the game.
		output.blit(bitmap_base, Dialogue.getDialogueX(), Dialogue.getDialogueY());
	}
	
	public void renderText(Graphics g) {
		drawText(g);
	}
	
	private void drawText(Graphics g) {
		g.setColor(Color.black);
		//The game uses 8f FONT when shown on the screen. It is scaled by GAME_SCALE.
		//Text are drawn with positive X = RIGHT, positive Y = UP. Not the other way around.
		g.setFont(Art.font.deriveFont(Font.PLAIN, 24f));
		g.drawString("POKéMON", Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
	}
	
	public static final int getDialogueX() {
		return 0;
	}
	
	public static final int getDialogueY() {
		return (MainComponent.GAME_HEIGHT - Dialogue.HEIGHT);
	}
	
	public static final int getDialogueTextStartingX() {
		return Dialogue.TEXT_SPACING_WIDTH * MainComponent.GAME_SCALE;
	}
	
	public static final int getDialogueTextStartingY() {
		return (Dialogue.getDialogueY() * MainComponent.GAME_SCALE) + Dialogue.FIRST_LINE_SPACING_HEIGHT * MainComponent.GAME_SCALE + Dialogue.FONT_SIZE;
	}
}
