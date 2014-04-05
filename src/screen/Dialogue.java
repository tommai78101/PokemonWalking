package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import main.Keys;
import main.MainComponent;
import resources.Art;
import abstracts.Tile;

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
	
	//Dialogue max string length per line.
	public static final int MAX_STRING_LENGTH = 18;
	
	//Dialogue defaults;
	public static final int DIALOGUE_SPEECH = 0xF1;
	
	private int stringPointer;
	private int tickSpeed;
	private String dialogueText;
	private boolean showDialog;
	private boolean next;
	private boolean nextTick;
	
	private String[] tokens;
	private int tokenPointer;
	private int firstLinePointer;
	private int secondLinePointer;
	private boolean firstLineFull;
	private boolean secondLineFull;
	
	private Keys input;
	
	//public ArrayList<String> dialogueList;
	
	public Dialogue(Keys input) {
		this.input = input;
		
		this.stringPointer = 0;
		this.tickSpeed = 0;
		this.dialogueText = "POKéMON WORLD IS HAPPENING TOMORROW.";
		this.showDialog = false;
		this.next = false;
		this.nextTick = false;
		//this.dialogueList = new ArrayList<String>();
		
		this.tokens = this.dialogueText.split(" ");
		this.tokenPointer = 0;
		this.firstLinePointer = 0;
		this.secondLinePointer = 0;
	}
	
	public void tick() {
		if (this.showDialog) {
			this.tickSpeed--;
			if (this.tickSpeed < 0) {
				if (this.stringPointer < this.dialogueText.length() && !this.next)
					this.stringPointer++;
				this.tickSpeed = 0x4;
				this.nextTick = !this.nextTick;
			}
		}
	}
	
	public void render(BaseScreen output, int x, int y, int centerWidth, int centerHeight) {
		if (this.showDialog) {
			output.blit(Art.dialogue_top_left, x * Tile.WIDTH, y * Tile.HEIGHT);
			for (int i = 0; i < centerWidth - 1; i++) {
				output.blit(Art.dialogue_top, ((x + 1) * Tile.WIDTH) + (i * Tile.WIDTH), y * Tile.HEIGHT);
			}
			output.blit(Art.dialogue_top_right, (x + centerWidth) * Tile.WIDTH, y * Tile.HEIGHT);
			
			for (int j = 0; j < centerHeight - 1; j++) {
				output.blit(Art.dialogue_left, x * Tile.WIDTH, ((y + 1) * Tile.HEIGHT) + j * Tile.HEIGHT);
				for (int i = 0; i < centerWidth - 1; i++) {
					output.blit(Art.dialogue_background, ((x + 1) * Tile.WIDTH) + (i * Tile.WIDTH), ((y + 1) * Tile.HEIGHT) + j * Tile.HEIGHT);
				}
				output.blit(Art.dialogue_right, (x + centerWidth) * Tile.WIDTH, ((y + 1) * Tile.HEIGHT) + j * Tile.HEIGHT);
			}
			
			output.blit(Art.dialogue_bottom_left, x * Tile.WIDTH, ((y + centerHeight) * Tile.HEIGHT));
			for (int i = 0; i < centerWidth - 1; i++) {
				output.blit(Art.dialogue_bottom, ((x + 1) * Tile.WIDTH) + (i * Tile.WIDTH), ((y + centerHeight) * Tile.HEIGHT));
			}
			output.blit(Art.dialogue_bottom_right, (x + centerWidth) * Tile.WIDTH, ((y + centerHeight) * Tile.HEIGHT));
			
			if (this.next && this.nextTick) {
				output.blit(Art.dialogue_next, MainComponent.GAME_WIDTH - 16, MainComponent.GAME_HEIGHT - 8);
			}
		}
	}
	
	public void displayDialog() {
		this.showDialog = true;
	}
	
	public void hideDialog() {
		this.showDialog = false;
		this.stringPointer = 0;
		this.tickSpeed = 0;
		this.tokenPointer = 0;
	}
	
	public void renderTextGraphics(Graphics g) {
		g.setColor(Color.black);
		//The game uses 8f FONT when shown on the screen. It is scaled by GAME_SCALE.
		//Text are drawn with positive X = RIGHT, positive Y = UP. Not the other way around.
		g.setFont(Art.font.deriveFont(Font.PLAIN, 24f));
		
		if (this.dialogueText != null && !this.dialogueText.isEmpty()) {
			if (this.stringPointer <= this.dialogueText.length()) {
				String text = this.tokens[this.tokenPointer];
				
				if (this.firstLinePointer + text.length() < MAX_STRING_LENGTH) {
					if (this.stringPointer > text.length()) {
						this.firstLinePointer += this.stringPointer;
						this.secondLinePointer = this.firstLinePointer;
						this.stringPointer = 0;
						if (this.tokenPointer < this.tokens.length - 1)
							this.tokenPointer++;
					}
				}
				else {
					this.firstLineFull = true;
				}
				if (this.firstLineFull) {
					if (this.secondLinePointer + text.length() < MAX_STRING_LENGTH * 2) {
						if (this.stringPointer > text.length()) {
							this.secondLinePointer += this.stringPointer;
							this.stringPointer = 0;
							if (this.tokenPointer < this.tokens.length - 1)
								this.tokenPointer++;
						}
					}
					else {
						this.secondLineFull = true;
					}
				}
				if (!this.firstLineFull) {
					g.drawString(this.dialogueText.substring(0, this.firstLinePointer + this.stringPointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
				}
				else {
					g.drawString(this.dialogueText.substring(0, this.firstLinePointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
					if (!this.secondLineFull) {
						if (this.secondLinePointer + text.length() < MAX_STRING_LENGTH * 2) {
							g.drawString(this.dialogueText.substring(this.firstLinePointer, this.secondLinePointer + this.stringPointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
						}
					}
					else {
						g.drawString(this.dialogueText.substring(this.firstLinePointer, this.secondLinePointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
						this.next = true;
					}
				}
				
			}
		}
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
	
	public static final int getDialogueTextSecondLineStartingY() {
		return (Dialogue.getDialogueY() * MainComponent.GAME_SCALE) + Dialogue.SECOND_LINE_SPACING_HEIGHT * MainComponent.GAME_SCALE + Dialogue.FONT_SIZE;
	}
}
