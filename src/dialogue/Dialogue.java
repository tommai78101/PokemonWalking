package dialogue;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import main.Game;
import main.Keys;
import main.MainComponent;
import resources.Art;
import screen.BaseScreen;
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
	public static final int HALF_STRING_LENGTH = 9;
	
	//Styles
	public static final int DIALOGUE_STYLE_SPEECH = 0xF1;
	
	//TODO: Optimize this, to make way for other types of dialogues to use.
	private String[] tokens;
	private int tokenPointer;
	//private Map<Integer, Boolean> dialogs;
	private boolean next;
	private boolean nextTick;
	private byte arrowTickSpeed;
	private byte repeatDialogueTick;
	private boolean showDialog;
	private int firstLineIterator;
	private int secondLineIterator;
	private ArrayList<DialogueText> dialogues = Dialogue.loadDialogues("dialogue/dialogue.txt");
	private boolean doneDisplayingDialogue;
	
	private Keys input;
	private Game game;
	
	//---------------------------------------
	private boolean isMenuActivated;
	private ArrayList<Map.Entry<String, String>> menuItems = new ArrayList<Map.Entry<String, String>>();
	private int menuPointerPosition = 0;
	
	public Dialogue(Keys input, Game game) {
		this.input = input;
		this.game = game;
		this.arrowTickSpeed = 0;
		this.showDialog = false;
		this.next = false;
		this.nextTick = false;
		this.tokenPointer = 0;
		this.isMenuActivated = false;
		
		//tempMenuItems.add("BICYCLE");
		menuItems.add(new AbstractMap.SimpleEntry<String, String>("BICYCLE", "Use your bicycle."));
		menuItems.add(new AbstractMap.SimpleEntry<String, String>("TEMP", "Do nothing."));
	}
	
	/**
	 * Renders the dialogue box.
	 * 
	 * @param output
	 *            A BaseScreen object that the rendered tiles be blit to.
	 * @param x
	 *            The dialogue box's X coordinate.
	 * @param y
	 *            The dialogue box's Y coordinate.
	 * @param centerWidth
	 *            The dialogue box's width.
	 * @param centerHeight
	 *            The dialogue box's height.
	 * @return Nothing.
	 * */
	public void renderDialog(BaseScreen output, int x, int y, int centerWidth, int centerHeight) {
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
	
	/**
	 * Hides the dialog by making it disappear from the screen.
	 * 
	 * This method is used to hide the dialog box and allow back the player's inputs.
	 * 
	 * @return Nothing.
	 * */
	public void hideDialog() {
		this.showDialog = false;
		this.next = false;
		this.tokenPointer = 0;
		//this.setDialogCheckpoint();
		//this.currentDialogue.checkpoint = true; 
		//TODO: Checkpoints are for specific game goals that players had reached.
		this.doneDisplayingDialogue = true;
		this.repeatDialogueTick = 0xF;
	}
	
	/**
	 * Renders the text onto the screen.
	 * 
	 * <p>
	 * This method must be called after the Graphics object has been obtained from BufferStrategy.
	 * 
	 * <p>
	 * There is a slight exception handling abuse. That is done to make sure all dialogue lines have been drawn.
	 * 
	 * @param g
	 *            The Graphics object obtained from the BufferStrategy.
	 * @return Nothing.
	 * */
	public void renderText(Graphics g) {
		g.setColor(Color.black);
		g.setFont(Art.font);
		try {
			g.drawString(this.tokens[this.tokenPointer].substring(0, this.firstLineIterator), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextStartingY());
			g.drawString(this.tokens[this.tokenPointer + 1].substring(0, secondLineIterator), Dialogue.getDialogueTextStartingX(), Dialogue.getDialogueTextSecondLineStartingY());
		}
		catch (Exception e) {
			//Ignore. Silently catch the any sorts of exception, and just let the game flow on.
		}
		
		if (this.isMenuActivated) {
			for (int i = 0; i < menuItems.size(); i++) {
				//TODO: We need to have an arrow pointing at the menu items.
				//We can't do anything about it. Scaling problems.
				g.drawString(menuItems.get(i).getKey(), MainComponent.GAME_SCALE * (Tile.WIDTH * 6), (((Tile.HEIGHT * 2 - 8) + i * 16) * MainComponent.GAME_SCALE));
				
			}
		}
	}
	
	/**
	 * Updates the dialogues on a per-tick basis.
	 * 
	 * <p>
	 * Note that there is a slight exception handling abuse. It is used to thwart away hidden bugs that can contribute to erratic text behavior when
	 * displaying dialogues. More information can be found by reading the comments in this method code.
	 * 
	 * @return Nothing.
	 * */
	public void tick() {
		
		//		if (!this.input.START.lastKeyState && this.input.START.keyStateDown) {
		//			isMenuActivated = !isMenuActivated;
		//			this.input.START.lastKeyState = true;
		//		}
		//		if ((this.input.X.keyStateDown || this.input.PERIOD.keyStateDown) && this.isMenuActivated)
		//			this.isMenuActivated = false;
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
					//If (N+1)th line doesn't exist, then the dialogue has already
					//been completed.
					result1 = true;
				}
				try {
					this.tokens[this.tokenPointer + 1].length();
				}
				catch (ArrayIndexOutOfBoundsException e) {
					//If (N+1)th line doesn't exist, then this doesn't exist.
					//However, if (N+1)th do exist, there's a chance that this line may
					//not exist. We check just to make sure.
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
			speechDialogueHandling();
		}
		//		else if (this.isMenuActivated) {
		//			prepareDialogueText();
		//			menuDialogueText();
		//			menuDialogueHandling();
		//		}
		else if (this.game.getPlayer().warningsTriggered) {
			warningText();
			this.game.getPlayer().warningsTriggered = false;
		}
		else {
			this.firstLineIterator = this.secondLineIterator = 0;
			this.menuPointerPosition = 0;
		}
		if (this.repeatDialogueTick > 0)
			this.repeatDialogueTick--;
	}
	
	//	private void prepareDialogueText() {
	//		Player player = this.game.getPlayer();
	//		Map.Entry<String, String> entry = menuItems.get(menuPointerPosition);
	//		if (entry.getKey().equals("BICYCLE")) {
	//			if (player.isRidingBicycle()) {
	//				entry.setValue("Get off bicycle.");
	//			}
	//			else {
	//				entry.setValue("Use your bicycle.");
	//			}
	//		}
	//	}
	
	//	private void menuDialogueText() {
	//		String menuLine = menuItems.get(menuPointerPosition).getValue();
	//		this.tokens = this.toLines(menuLine, HALF_STRING_LENGTH);
	//		this.tokenPointer = 0;
	//		try {
	//			this.firstLineIterator = this.tokens[this.tokenPointer].length();
	//		}
	//		catch (Exception e) {
	//			this.doneDisplayingDialogue = true;
	//			return;
	//		}
	//		if (this.tokens.length > 2) {
	//			this.secondLineIterator = HALF_STRING_LENGTH;
	//			this.next = true;
	//		}
	//		else {
	//			try {
	//				this.secondLineIterator = this.tokens[this.tokenPointer + 1].length();
	//			}
	//			catch (Exception e) {
	//				this.secondLineIterator = 0;
	//			}
	//		}
	//		this.doneDisplayingDialogue = false;
	//	}
	
	//	private void menuDialogueHandling() {
	//		//Player input mechanism
	//		if (!Player.isMovementsLocked())
	//			Player.lockMovements();
	//		if (!this.input.down.lastKeyState && this.input.down.keyStateDown) {
	//			this.menuPointerPosition++;
	//			if (this.menuPointerPosition > this.menuItems.size() - 1)
	//				this.menuPointerPosition = 0;
	//			this.input.down.lastKeyState = true;
	//		}
	//		else if (!this.input.up.lastKeyState && this.input.up.keyStateDown) {
	//			this.menuPointerPosition--;
	//			if (this.menuPointerPosition < 0)
	//				this.menuPointerPosition = this.menuItems.size() - 1;
	//			this.input.up.lastKeyState = true;
	//		}
	//		
	//		//Menu input mechanism
	//		if ((this.input.Z.keyStateDown || this.input.SLASH.keyStateDown) && (!this.input.Z.lastKeyState || !this.input.SLASH.lastKeyState)) {
	//			Map.Entry<String, String> entry = menuItems.get(menuPointerPosition);
	//			game.sendAction(entry);
	//			this.input.Z.lastKeyState = true;
	//			this.input.SLASH.lastKeyState = true;
	//			this.doneDisplayingDialogue = true;
	//			this.isMenuActivated = false;
	//		}
	//	}
	
	private void warningText() {
		this.tokens = this.toLines("There's a time and place for everything, but not now.", MAX_STRING_LENGTH);
		this.showDialog = true;
		this.doneDisplayingDialogue = false;
		this.firstLineIterator = this.secondLineIterator = 0;
	}
	
	private void speechDialogueHandling() {
		boolean result1 = false, result2 = false;
		try {
			if (this.firstLineIterator < this.tokens[this.tokenPointer].length())
				this.firstLineIterator++;
			else {
				//This is done to check to see if there exist a (N+1)th line in the entire dialogue.
				//If it didn't exist, set result1 to true, so that the game knows the first
				//line is finished.
				//Abusing the exception handling.
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
			//Since there can only be cases where (N+2)th line exists but not (N+2)th line,
			//and cases where the (N+2)th line has finished.
			//Therefore, it returns true in both cases.
			result2 = true;
		}
		if (result1 && result2 && this.tokenPointer + 1 >= this.tokens.length)
			this.next = true;
	}
	
	/**
	 * Creates a set of lines for use with the dialogues from a given dialogue message.
	 * 
	 * <p>
	 * Messages must be full dialogues.
	 * 
	 * @param str
	 *            Dialogue message to be used for dialogues in the game.
	 * @param key
	 *            Dialogue ID, used to differentiate dialogues from others.
	 * @return Nothing.
	 * */
	public void createText(int interactionID) {
		for (DialogueText dt : this.dialogues) {
			if (dt.dialogueID == interactionID) {
				this.tokens = toLines(dt.dialogueMessage, MAX_STRING_LENGTH);
				this.showDialog = true;
				this.doneDisplayingDialogue = false;
				break;
			}
		}
	}
	
	/**
	 * Sets the dialogue checkpoint to true.
	 * 
	 * <p>
	 * This must be used when the current dialogue has completed, or when a checkpoint in the game has been set.
	 * 
	 * <p>
	 * This must be used after when {@link #setDialogKeyID(int)} has been called.
	 * 
	 * @return Nothing.
	 * @see #setDialogKeyID(int)
	 * 
	 * */
	//	public void setDialogCheckpoint() {
	//		//this.dialogs.put(this.dialogKeyID, true);
	//	}
	
	/**
	 * Checks to see if the dialogue checkpoint has been set.
	 * 
	 * @param key
	 *            The dialogue ID used to identify the dialogue needed to check.
	 * @return True, if the dialogue of the dialogue ID given has been set. False, if there are no
	 *         checkpoints set, or if the dialogue of the dialogue ID given has not been set.
	 * */
	//	public boolean isDialogCheckpointSet(int key) {
	//		if (this.dialogs.isEmpty())
	//			return false;
	//		return this.dialogs.get(key);
	//	}
	
	public void render(BaseScreen screen, int offsetX, int offsetY, Graphics g) {
		if (this.isDisplayingDialogue()) {
			screen.disableRenderHalf();
			this.renderDialog(screen, 0, 6, 9, 2);
		}
		else if (this.isMenuActivated) {
			this.renderDialog(screen, 0, 6, 4, 2);
			this.renderDialog(screen, 5, 0, 4, menuItems.size()); //TODO: This needs to use a array list size as height.
			//Scaling problems again.
			screen.blit(Art.dialogue_pointer, Tile.WIDTH * 5 + 8, Tile.HEIGHT + this.menuPointerPosition * Tile.HEIGHT);
		}
		if (this.isDisplayingDialogue() || this.isMenuActivated) {
			g.drawImage(MainComponent.createCompatibleBufferedImage(screen.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
			this.renderText(g);
		}
	}
	
	/**
	 * Checks to see if the dialogue is allowed to be shown.
	 * 
	 * @return True, if the dialogue is allowed to be shown. False, if the dialogue is not allowed to be shown.
	 * */
	public boolean isDisplayingDialogue() {
		return this.showDialog;
	}
	
	public boolean isDoneDisplayingDialogue() {
		return this.doneDisplayingDialogue;
	}
	
	public boolean isMenuActivated() {
		return this.isMenuActivated;
	}
	
	public void reset() {
		this.showDialog = false;
		if (this.repeatDialogueTick <= 0)
			this.doneDisplayingDialogue = false;
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
	
	public static ArrayList<DialogueText> loadDialogues(String filename) {
		ArrayList<DialogueText> result = new ArrayList<DialogueText>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Dialogue.class.getClassLoader().getResourceAsStream(filename)));
			String line = null;
			DialogueText text = new DialogueText();
			String[] tokens;
			while ((line = reader.readLine()) != null) {
				text.checkpoint = false;
				if (line.startsWith("#")) {
					//Dialogue ID
					tokens = line.split("#");
					text.dialogueID = Integer.valueOf(tokens[1]);
				}
				else if (line.startsWith("@")) {
					tokens = line.split("@");
					text.dialogueMessage = tokens[1];
				}
				else if (line.isEmpty() || line.trim().equals("")) {
					result.add(text);
					text = new DialogueText();
				}
			}
			return result;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] toLines(String all, final int regex) {
		ArrayList<String> lines = new ArrayList<>();
		String[] words = all.split("\\s");
		String line = "";
		int length = 0;
		for (String w : words) {
			if (length + w.length() + 1 > regex) {
				if (w.length() >= regex) {
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
}
