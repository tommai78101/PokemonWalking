package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import main.Keys;
import main.MainComponent;
import main.NewInputHandler;
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
	
	//Styles
	public static final int DIALOGUE_STYLE_SPEECH = 0xF1;
	
	//TODO: Optimize this, to make way for other types of dialogues to use.
	private String[] tokens;
	private int tokenPointer;
	private Map<Integer, Boolean> dialogs;
	private boolean next;
	private boolean nextTick;
	private byte arrowTickSpeed;
	private boolean showDialog;
	
	private Keys input;
	
	public Dialogue(Keys input) {
		this.input = input;
		this.arrowTickSpeed = 0;
		this.showDialog = false;
		this.next = false;
		this.nextTick = false;
		this.tokenPointer = 0;
		this.dialogs = new HashMap<Integer, Boolean>();
	}
	
	//	public void tick_old() {
	//		if (this.showDialog) {
	//			if (!NewInputHandler.inputsAreLocked())
	//				NewInputHandler.lockInputs();
	//			this.tickSpeed--;
	//			if (this.tickSpeed < 0) {
	//				if (this.totalStringPointer < this.dialogueText.length() && !this.next) {
	//					this.stringPointer++;
	//					this.totalStringPointer++;
	//				}
	//				this.tickSpeed = 0x2;
	//			}
	//			this.arrowTickSpeed--;
	//			if (this.arrowTickSpeed < 0) {
	//				this.nextTick = !this.nextTick;
	//				this.arrowTickSpeed = 0x6;
	//			}
	//			if (this.next) {
	//				if (input.Z.isPressedDown || input.Z.isTappedDown
	//						|| input.X.isTappedDown || input.X.isPressedDown
	//						|| input.SLASH.isTappedDown || input.SLASH.isPressedDown
	//						|| input.PERIOD.isTappedDown || input.PERIOD.isPressedDown) {
	//					if (this.totalStringPointer < this.dialogueText.length()) {
	//						this.firstLineFull = false;
	//						this.secondLineFull = false;
	//						this.firstLinePointer = 0;
	//						this.beginningPointer += this.secondLinePointer;
	//						this.secondLinePointer = 0;
	//						this.stringPointer = 0;
	//						this.next = false;
	//					}
	//					else {
	//						this.hideDialog();
	//					}
	//				}
	//			}
	//		}
	//	}
	
	public void renderDialog(BaseScreen output, int x, int y, int centerWidth, int centerHeight) {
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
			
			//The boolean "next" is for dialogues that are complete, and the "nextTick" is for displaying the arrow.
			if (this.next && this.nextTick) {
				output.blit(Art.dialogue_next, MainComponent.GAME_WIDTH - 16, MainComponent.GAME_HEIGHT - 8);
			}
		}
	}
	
	//	/**
	//	 * Displays a dialog box showing the text message that was passed in as a parameter. This method
	//	 * can lock player's inputs while the dialog is displayed onto the screen.
	//	 * 
	//	 * @param text
	//	 *            Pass a String string to make it display the message to the player.
	//	 * @param dialogID
	//	 *            This is currently unimplemented.
	//	 * @return Nothing.
	//	 * */
	//	public void displayDialog(String text, int dialogID) {
	//		if (this.dialogs.containsKey(dialogID)) {
	//			//TODO: DO SOMETHING WHEN dialogs HAVE BEEN REACHED ALREADY.
	//			if (this.dialogs.get(dialogID).booleanValue()) { return; }
	//		}
	//		if (text != null && !text.isEmpty() && !this.showDialog) {
	//			this.showDialog = true;
	//			this.dialogueText = text;
	//			this.tokens = this.dialogueText.split("\\s");
	//			this.tokenPointer = 0;
	//			this.beginningPointer = 0;
	//			this.firstLinePointer = 0;
	//			this.secondLinePointer = 0;
	//			this.totalStringPointer = 0;
	//			this.dialogs.put(dialogID, false);
	//		}
	//	}
	
	/**
	 * Hides the dialog by making it disappear from the screen.
	 * 
	 * This method is used to hide the dialog box and allow back the player's inputs.
	 * 
	 * @return Nothing.
	 * */
	public void hideDialog() {
		this.showDialog = false;
		//		this.dialogueText = null;
		this.tokenPointer = 0;
		this.setDialogCheckpoint();
		NewInputHandler.unlockInputs();
	}
	
	//	/**
	//	 * Tells the Graphics object to draw text onto the screen.
	//	 * 
	//	 * <p>
	//	 * There are many complicated situations where texts can overflow around the dialog boxes, causing it to show up glitchy. Using lots of
	//	 * conditional checkings, I was able to confine the glitchiness down to a minimum.
	//	 * 
	//	 * <p>
	//	 * In order to use this, this method must be placed somewhere where there is an easy way to pass the Graphics object to this. The Graphics object
	//	 * is obtained by using getDrawGraphics() from the BufferStrategy, which is created from the Canvas AWT component.
	//	 * 
	//	 * @param graphics
	//	 *            The Graphics object used for drawing texts using custom
	//	 *            fonts.
	//	 * @return Nothing.
	//	 * 
	//	 * */
	//	public void renderTextGraphics(Graphics g) {
	//		if (this.dialogueText != null && !this.dialogueText.isEmpty()) {
	//			//The game uses 8f FONT when shown on the screen. It is scaled by GAME_SCALE.
	//			//Text are drawn with positive X = RIGHT, positive Y = UP. Not the other way around.
	//			g.setColor(Color.black);
	//			g.setFont(Art.font.deriveFont(Font.PLAIN, 24f));
	//			if (this.totalStringPointer <= this.dialogueText.length()) {
	//				//				//Handles one word sentences only.
	//				//				if (this.stringPointer > this.dialogueText.length()) {
	//				//					this.stringPointer = this.dialogueText.length();
	//				//					this.next = true;
	//				//					g.drawString(this.dialogueText.substring(this.beginningPointer, this.beginningPointer + this.stringPointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
	//				//					return;
	//				//				}
	//				
	//				if (this.totalStringPointer == this.dialogueText.length()) {
	//					if ((this.totalStringPointer - this.beginningPointer) <= MAX_STRING_LENGTH) {
	//						g.drawString(this.dialogueText.substring(this.beginningPointer, this.totalStringPointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
	//					}
	//					else {
	//						g.drawString(this.dialogueText.substring(this.beginningPointer, this.beginningPointer + this.firstLinePointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
	//						g.drawString(this.dialogueText.substring(this.beginningPointer + this.firstLinePointer, this.totalStringPointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
	//					}
	//					this.next = true;
	//					return;
	//				}
	//				
	//				//Handles more than one word.
	//				String text = this.tokens[this.tokenPointer];
	//				if (this.firstLinePointer + text.length() <= MAX_STRING_LENGTH) {
	//					if (this.stringPointer > text.length()) {
	//						this.firstLinePointer += this.stringPointer;
	//						if (this.tokenPointer < this.tokens.length - 1) {
	//							this.tokenPointer++;
	//							this.stringPointer = 0;
	//						}
	//						//Short sentences.
	//						if (this.totalStringPointer >= this.dialogueText.length() - 1) {
	//							g.drawString(this.dialogueText.substring(this.beginningPointer, this.beginningPointer + this.firstLinePointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
	//							return;
	//						}
	//					}
	//				}
	//				else {
	//					if (!this.firstLineFull) {
	//						this.firstLineFull = true;
	//						this.secondLinePointer = this.firstLinePointer + this.stringPointer;
	//					}
	//				}
	//				if (this.firstLineFull) {
	//					//First line is full
	//					int textCompare = 0;
	//					if (beginningPointer > (MAX_STRING_LENGTH + 1) * 2)
	//						textCompare = (((beginningPointer % (MAX_STRING_LENGTH + 1)) + secondLinePointer - 1) % MAX_STRING_LENGTH) + text.length();
	//					if ((this.totalStringPointer > (MAX_STRING_LENGTH * 2) + 1) ? (textCompare <= MAX_STRING_LENGTH) : (text.length() <= MAX_STRING_LENGTH)) {
	//						if (this.stringPointer > text.length()) {
	//							this.secondLinePointer += this.stringPointer;
	//							if (this.secondLinePointer >= MAX_STRING_LENGTH * 2) {
	//								//Subtract 2 space characters from the (first + second lines) character total.
	//								this.secondLineFull = true;
	//							}
	//							if (this.tokenPointer < this.tokens.length - 1) {
	//								this.tokenPointer++;
	//								this.stringPointer = 0;
	//							}
	//						}
	//					}
	//					else {
	//						this.secondLineFull = true;
	//						if (this.stringPointer > 0)
	//							this.secondLinePointer += this.stringPointer;
	//					}
	//					g.drawString(this.dialogueText.substring(this.beginningPointer, this.beginningPointer + this.firstLinePointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
	//					if (!this.secondLineFull) {
	//						g.drawString(this.dialogueText.substring(this.beginningPointer + this.firstLinePointer, this.beginningPointer + this.secondLinePointer + this.stringPointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
	//					}
	//					else {
	//						g.drawString(this.dialogueText.substring(this.beginningPointer + this.firstLinePointer, this.beginningPointer + this.secondLinePointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
	//						this.next = true;
	//					}
	//				}
	//				else {
	//					//First line isn't full
	//					g.drawString(this.dialogueText.substring(this.beginningPointer, this.beginningPointer + this.firstLinePointer + this.stringPointer), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
	//				}
	//			}
	//		}
	//	}
	
	private int firstLineIterator;
	private int secondLineIterator;
	private int dialogKeyID;
	
	public void renderText(Graphics g) {
		g.setColor(Color.black);
		g.setFont(Art.font.deriveFont(Font.PLAIN, 24f));
		try {
			g.drawString(this.tokens[this.tokenPointer].substring(0, this.firstLineIterator), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
			g.drawString(this.tokens[this.tokenPointer + 1].substring(0, secondLineIterator), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
		}
		catch (Exception e) {
			//Ignore. Silently catch the any sorts of exception, and just let the game flow on.
			//this.setDialogCheckpoint();
		}
	}
	
	public void tick() {
		if (this.next) {
			if (input.Z.isPressedDown || input.Z.isTappedDown
					|| input.X.isTappedDown || input.X.isPressedDown
					|| input.SLASH.isTappedDown || input.SLASH.isPressedDown
					|| input.PERIOD.isTappedDown || input.PERIOD.isPressedDown) {
				this.next = false;
				boolean result1 = false, result2 = false;
				try {
					this.tokens[this.tokenPointer].length();
				}
				catch (ArrayIndexOutOfBoundsException e) {
					result1 = true;
				}
				try {
					this.tokens[this.tokenPointer + 1].length();
				}
				catch (ArrayIndexOutOfBoundsException e) {
					result2 = true;
				}
				if (result1 || result2)
					this.hideDialog();
				else {
					this.tokenPointer += 2;
					this.firstLineIterator = this.secondLineIterator = 0;
				}
			}
			this.arrowTickSpeed--;
			if (this.arrowTickSpeed < 0) {
				this.nextTick = !this.nextTick;
				this.arrowTickSpeed = 0x6;
			}
		}
		if (this.showDialog) {
			if (!NewInputHandler.inputsAreLocked())
				NewInputHandler.lockInputs();
			boolean result1 = false, result2 = false;
			try {
				if (this.firstLineIterator < this.tokens[this.tokenPointer].length())
					this.firstLineIterator++;
				else {
					this.tokens[this.tokenPointer + 1].length();
				}
			}
			catch (ArrayIndexOutOfBoundsException e) {
				result1 = true;
			}
			try {
				if (this.secondLineIterator >= this.tokens[this.tokenPointer + 1].length()) {
					this.next = true;
				}
				else {
					if (this.secondLineIterator < this.tokens[this.tokenPointer + 1].length() && this.firstLineIterator >= this.tokens[this.tokenPointer].length())
						this.secondLineIterator++;
				}
			}
			catch (ArrayIndexOutOfBoundsException e) {
				result2 = true;
			}
			if (result1 && result2 && this.tokenPointer + 1 >= this.tokens.length)
				this.next = true;
		}
		else {
			this.firstLineIterator = this.secondLineIterator = 0;
			if (NewInputHandler.inputsAreLocked())
				NewInputHandler.unlockInputs();
		}
	}
	
	public void createText(String str, int key) {
		this.tokens = toLines(str);
		if (!this.dialogs.isEmpty()) {
			if (!this.getDialogueCheckpoint(this.dialogKeyID))
				this.showDialog = true;
		}
		else {
			this.setDialogKeyID(key);
		}
	}
	
	private String[] toLines(String all) {
		ArrayList<String> lines = new ArrayList<>();
		String[] words = all.split("\\s");
		String line = "";
		int length = 0;
		for (String w : words) {
			if (length + w.length() + 1 > MAX_STRING_LENGTH) {
				if (w.length() >= MAX_STRING_LENGTH) {
					line += w;
					lines.add(line);
					line = "";
					continue;
				}
				lines.add(line);
				line = "";
				length = 0;
			}
			if (length > 0) {
				line += " ";
				length += 1;
			}
			line += w;
			length += w.length();
		}
		if (line.length() > 0)
			lines.add(line);
		return lines.toArray(new String[lines.size()]);
	}
	
	public void setDialogKeyID(int value) {
		this.dialogKeyID = value;
		this.dialogs.put(value, false);
	}
	
	public void setDialogCheckpoint() {
		this.dialogs.put(this.dialogKeyID, true);
	}
	
	public boolean isDialogCheckpointSet(int key) {
		if (this.dialogs.isEmpty())
			return false;
		return this.dialogs.get(key);
	}
	
	//-------------------------  STATIC FINAL METHODS ONLY -------------------------------
	
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
	
	public boolean isDisplayingDialogue() {
		return this.showDialog;
	}
	
	public boolean getDialogueCheckpoint(int key) {
		return this.dialogs.get(key);
	}
}
