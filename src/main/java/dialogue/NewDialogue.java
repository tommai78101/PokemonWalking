/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package dialogue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import main.Keys;
import main.MainComponent;
import resources.Art;
import screen.BaseScreen;
import abstracts.Tile;
import entity.Player;

//TODO (6/25/2015): Check to see why modded scripts still suffer from blinking dialogue boxes. Non-modded scripts are fixed.

public class NewDialogue {
	public static final int DIALOGUE_SPEECH = 0x40;
	public static final int DIALOGUE_QUESTION = 0x41;
	public static final int DIALOGUE_ALERT = 0x42;
	
	public static final int HALF_STRING_LENGTH = 9;
	
	// Dialogue max string length per line.
	public static final int MAX_STRING_LENGTH = 18;
	
	private ArrayList<String> completedLines;
	
	private Keys input;
	
	private int lineIterator;
	private int lineLength;
	private ArrayList<Map.Entry<String, Boolean>> lines;
	private boolean nextFlag;
	private boolean simpleQuestionFlag;
	private boolean simpleSpeechFlag;
	private boolean yesNoCursorPosition;
	private Boolean yesNoAnswerFlag;
	private byte nextTick;
	private int scrollDistance;
	private boolean scrollFlag;
	private boolean showDialog;
	
	private int subStringIterator;
	private byte tickCount = 0x0;
	
	private int totalDialogueLength;
	private int type;
	
	private NewDialogue(Keys keys) {
		lines = new ArrayList<Map.Entry<String, Boolean>>();
		completedLines = new ArrayList<String>();
		this.subStringIterator = 0;
		this.lineLength = 0;
		this.totalDialogueLength = 0;
		this.nextFlag = false;
		this.simpleQuestionFlag = false;
		this.simpleSpeechFlag = false;
		this.scrollFlag = false;
		this.scrollDistance = 0;
		this.nextTick = 0x0;
		this.lineIterator = 0;
		this.input = keys;
		this.showDialog = false;
		this.type = 0;
		this.yesNoCursorPosition = true;
		this.yesNoAnswerFlag = null; // Default
	}
	
	public NewDialogue(NewDialogue dialogue) {
		// Deep copy
		this.completedLines = new ArrayList<String>();
		for (String s: dialogue.completedLines)
			this.completedLines.add(s);
		
		this.input = dialogue.input;
		
		this.lineIterator = dialogue.lineIterator;
		this.lineLength = dialogue.lineLength;
		this.lines = new ArrayList<Map.Entry<String, Boolean>>();
		for (Map.Entry<String, Boolean> e: dialogue.lines)
			this.lines.add(new AbstractMap.SimpleEntry<String, Boolean>(e.getKey(), e.getValue()));
		
		
		this .nextFlag = dialogue.nextFlag;
		this .simpleQuestionFlag = dialogue.simpleQuestionFlag;
		this .simpleSpeechFlag = dialogue.simpleSpeechFlag;
		this .yesNoCursorPosition = dialogue.yesNoCursorPosition;
		this .yesNoAnswerFlag = dialogue.yesNoAnswerFlag;
		this.nextTick = dialogue.nextTick;
		this.scrollDistance = dialogue.scrollDistance;
		this.scrollFlag = dialogue.scrollFlag;
		this.showDialog = dialogue.showDialog;
		
		this.subStringIterator = dialogue.subStringIterator;
		this. tickCount = dialogue.tickCount;
		this.totalDialogueLength = dialogue.totalDialogueLength;
		this.type = dialogue.type;
	}

	public Boolean getAnswerToSimpleQuestion() {
		if (this.yesNoAnswerFlag == null)
			return null;
		return this.yesNoAnswerFlag.booleanValue();
	}
	
	public int getDialogueType() {
		return this.type;
	}
	
	public boolean isDialogueCompleted() {
		return (this.lineIterator >= this.lines.size());
	}
	
	public boolean isScrolling() {
		return this.scrollFlag;
	}
	
	public void resetDialogue() {
		this.subStringIterator = 0;
		this.nextFlag = false;
		this.simpleQuestionFlag = false;
		this.simpleSpeechFlag = false;
		this.scrollFlag = false;
		this.scrollDistance = 0;
		this.nextTick = 0x0;
		this.lineIterator = 0;
		this.showDialog = true;
		this.yesNoCursorPosition = true;
		this.yesNoAnswerFlag = null;
		this.completedLines.clear();
	}
	
	public boolean isDialogueTextSet() {
		return !this.lines.isEmpty();
	}
	
	public boolean isShowingDialog() {
		return this.showDialog;
	}
	
	public void render(BaseScreen output, Graphics graphics) {
		render(output, graphics, 0, 6, 9, 2);
	}
	
	public void render(BaseScreen output, Graphics graphics, int x, int y, int w, int h) {
		if (x < 0)
			x = 0;
		if (x > 9)
			x = 9;
		if (y < 0)
			y = 0;
		if (y > 8)
			y = 8;
		if (x + w > 9)
			w = 9 - x;
		if (y + h > 8)
			h = 8 - y;
		if (showDialog) {
			switch (this.type) {
				case DIALOGUE_SPEECH: {
					renderDialogBackground(output, x, y, w, h);
					renderDialogBorderBox(output, x, y, w, h);
					if (this.nextFlag && this.nextTick < 0x8)
						output.blit(Art.dialogue_next, MainComponent.GAME_WIDTH - 16, MainComponent.GAME_HEIGHT - 8);
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					renderText(g2d);
					g2d.dispose();
					break;
				}
				case DIALOGUE_QUESTION: {
					renderDialogBackground(output, x, y, w, h);
					renderDialogBorderBox(output, x, y, w, h);
					if (this.simpleQuestionFlag && !this.nextFlag) {
						renderDialogBackground(output, 7, 3, 2, 2);
						renderDialogBorderBox(output, 7, 3, 2, 2);
						// Offset by -3 for the Y axis.
						output.blit(Art.dialogue_pointer, MainComponent.GAME_WIDTH - Tile.WIDTH * 3 + 8, this.yesNoCursorPosition ? (Tile.HEIGHT * 4 - 3) : (Tile.HEIGHT * 5 - 3));
					}
					else if (!this.simpleQuestionFlag && (this.nextFlag && this.nextTick < 0x8))
						output.blit(Art.dialogue_next, MainComponent.GAME_WIDTH - 16, MainComponent.GAME_HEIGHT - 8);
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					renderText(g2d);
					renderYesNoAnswerText(g2d);
					g2d.dispose();
					break;
				}
				case DIALOGUE_ALERT: {
					renderDialogBackground(output, x, y, w, h);
					renderDialogBorderBox(output, x, y, w, h);
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					renderText(g2d);
					g2d.dispose();
					break;
				}
			}
		}
	}
	
	public void renderYesNoAnswerText(Graphics g) {
		if (this.simpleQuestionFlag) {
			g.setFont(Art.font.deriveFont(8f));
			g.setColor(Color.black);
			
			final int X = Tile.WIDTH * 8;
			final int YES_HEIGHT = Tile.HEIGHT * 4 + 4;
			final int NO_HEIGHT = Tile.HEIGHT * 5 + 4;
			try {
				g.drawString("YES", X, YES_HEIGHT);
				g.drawString("NO", X, NO_HEIGHT);
			}
			catch (Exception e) {
			}
		}
	}
	
	/**
	 * <p>Update method for NewDialogue class.</p>
	 * 
	 * <p><b>WARNING</b> : The code content of this tick() method is deliberately setup and designed in such a way that it 
	 * replicates the dialogues in Gen 1 and Gen 2 Pokémon games. May require a heavy amount of refactoring/rewriting.</p>
	 * */
	public void tick() {
		int count = 0;
		try {
			for (int i = 0; i < this.lineIterator; i++) {
				count += this.lines.get(i).getKey().length();
				if (i != this.lines.size() - 1)
					count += 1;
			}
		}
		catch (Exception e) {
			count = 0;
		}
		if (count < this.totalDialogueLength && (!this.nextFlag && !this.scrollFlag)) {
			tickCount++;
			if (tickCount > 0x1)
				tickCount = 0x0;
		}
		else if (this.nextFlag) {
			switch (this.type) {
				case DIALOGUE_QUESTION: {
					if (count >= this.totalDialogueLength) {
						this.simpleQuestionFlag = true;
						this.nextFlag = false;
						this.scrollFlag = false;
					}
					else {
						this.nextTick++;
						if (this.nextTick > 0xE)
							this.nextTick = 0x0;
					}
					break;
				}
				case DIALOGUE_SPEECH: {
					this.nextTick++;
					if (this.nextTick > 0xE)
						this.nextTick = 0x0;
					break;
				}
				case DIALOGUE_ALERT: {
					this.nextTick++;
					if (this.nextTick > 0xE)
						this.nextTick = 0x0;
					break;
				}
			}
		}
		else if (count >= this.totalDialogueLength) {
			if (this.lineIterator >= this.lines.size()) {
				switch (this.type) {
					case DIALOGUE_QUESTION:
						this.simpleQuestionFlag = true;
						this.scrollFlag = false;
						this.nextFlag = false;
						break;
					case DIALOGUE_SPEECH:
						this.simpleSpeechFlag = true;
						this.nextFlag = false;
						break;
					case DIALOGUE_ALERT:
						this.simpleSpeechFlag = true;
						this.nextFlag = false;
						break;
				}
			}
			else {
				Map.Entry<String, Boolean> entry = this.lines.get(this.lineIterator);
				this.completedLines.add(entry.getKey());
				this.lineIterator++;
				switch (this.type) {
					case DIALOGUE_SPEECH:
						this.nextFlag = true;
						break;
					case DIALOGUE_QUESTION:
						this.simpleQuestionFlag = true;
						this.nextFlag = true;
						break;
					case DIALOGUE_ALERT:
						this.nextFlag = true;
						break;
				}
			}
		}
		
		try {
			
			if (!this.nextFlag && !this.simpleQuestionFlag && !this.scrollFlag) {
				if (tickCount == 0x0) {
					if (!this.scrollFlag)
						this.subStringIterator++;
					if (this.subStringIterator >= this.lineLength) {
						this.subStringIterator %= this.lineLength;
						Map.Entry<String, Boolean> entry = this.lines.get(this.lineIterator);
						this.completedLines.add(entry.getKey());
						this.lineIterator++;
					}
					if (this.completedLines.size() == 2) {
						if (!this.scrollFlag) {
							switch (this.type) {
								case DIALOGUE_SPEECH:
									this.nextFlag = true;
									break;
								case DIALOGUE_QUESTION:
									// Must get to the end of the entire dialogue before asking for answers.
									if (this.lineIterator >= this.lines.size()) {
										this.simpleQuestionFlag = true;
										this.nextFlag = false;
									}
									else
										this.nextFlag = true;
									break;
								case DIALOGUE_ALERT:
									this.nextFlag = true;
									break;
							}
						}
					}
				}
				
				// Speeds up text speed.
				if (this.type != DIALOGUE_ALERT) {
					if ((input.Z.keyStateDown && !(input.Z.lastKeyState)) || (input.SLASH.keyStateDown && !input.SLASH.lastKeyState)) {
						if (this.subStringIterator >= this.lineLength - 2) {
							input.Z.lastKeyState = true;
							input.SLASH.lastKeyState = true;
						}
						else if (this.subStringIterator < this.lineLength) {
							this.subStringIterator++;
						}
					}
					else if ((input.X.keyStateDown && !(input.X.lastKeyState)) || (input.PERIOD.keyStateDown && !input.PERIOD.lastKeyState)) {
						if (this.subStringIterator < this.lineLength - 1) {
							this.subStringIterator = this.lineLength - 1;
							input.X.lastKeyState = true;
							input.PERIOD.lastKeyState = true;
						}
					}
				}
			}
			else if (this.simpleQuestionFlag && !this.nextFlag && !this.scrollFlag) {
				// Making sure this doesn't trigger the "Next" arrow.
				this.nextFlag = false;
				
				if ((this.input.up.keyStateDown && !this.input.up.lastKeyState) || (this.input.W.keyStateDown && !this.input.W.lastKeyState)) {
					this.input.up.lastKeyState = true;
					this.input.W.lastKeyState = true;
					// Made it consistent with Inventory's menu selection, where it doesn't wrap around.
					this.yesNoCursorPosition = !this.yesNoCursorPosition;
				}
				else if ((this.input.down.keyStateDown && !this.input.down.lastKeyState) || (this.input.S.keyStateDown && !this.input.S.lastKeyState)) {
					this.input.down.lastKeyState = true;
					this.input.S.lastKeyState = true;
					// Made it consistent with Inventory's menu selection, where it doesn't wrap around.
					this.yesNoCursorPosition = !this.yesNoCursorPosition;
				}
				if ((this.input.Z.keyStateDown && !this.input.Z.lastKeyState) || (this.input.SLASH.keyStateDown && !this.input.SLASH.lastKeyState)) {
					this.input.Z.lastKeyState = true;
					this.input.SLASH.lastKeyState = true;
					// The answer to simple questions have already been set by UP and DOWN.
					this.yesNoAnswerFlag = this.yesNoCursorPosition;
					this.simpleQuestionFlag = false;
					this.closeDialog();
				}
				else if ((this.input.X.keyStateDown && !this.input.X.lastKeyState) || (this.input.PERIOD.keyStateDown && !this.input.PERIOD.lastKeyState)) {
					this.input.X.lastKeyState = true;
					this.input.PERIOD.lastKeyState = true;
					// Always negative for cancel button.
					this.yesNoAnswerFlag = false;
					this.yesNoCursorPosition = false;
					this.simpleQuestionFlag = false;
					this.closeDialog();
				}
			}
			else if (this.simpleSpeechFlag) {
				//Handles only the simplest forms of dialogues.
				if ((input.Z.keyStateDown && !(input.Z.lastKeyState) || (input.SLASH.keyStateDown && !input.SLASH.lastKeyState))) {
					input.Z.lastKeyState = true;
					input.SLASH.lastKeyState = true;
					this.closeDialog();
				}
			}
			else {
				if ((input.Z.keyStateDown && !(input.Z.lastKeyState)) || (input.SLASH.keyStateDown && !(input.SLASH.lastKeyState))
						|| (input.X.keyStateDown && !(input.X.lastKeyState)) || (input.PERIOD.keyStateDown && !(input.PERIOD.lastKeyState))) {
					input.Z.lastKeyState = true;
					input.SLASH.lastKeyState = true;
					input.X.lastKeyState = true;
					input.PERIOD.lastKeyState = true;
					switch (this.type) {
						case DIALOGUE_SPEECH:
							this.nextFlag = false;
							this.scrollFlag = true;
							break;
						case DIALOGUE_QUESTION:
							// Must get to the end of the entire dialogue before asking questions.
							this.simpleQuestionFlag = false;
							this.nextFlag = false;
							this.scrollFlag = true;
							break;
						case DIALOGUE_ALERT:
							this.nextFlag = false;
							this.scrollFlag = true;
							break;
					}
				}
				else if (this.scrollFlag) {
					if (this.lineIterator >= this.lines.size()) {
						switch (this.type) {
							case DIALOGUE_QUESTION:
								this.simpleQuestionFlag = true;
								this.scrollFlag = false;
								this.nextFlag = false;
								break;
							case DIALOGUE_SPEECH:
								this.closeDialog();
								return;
							case DIALOGUE_ALERT:
								this.closeDialog();
								return;
						}
					}
					else
						this.scrollDistance += 8;
				}
			}
		}
		catch (Exception e) {
			if (this.lineIterator >= this.lines.size()) {
				if (this.scrollFlag) {
					this.closeDialog();
				}
				else {
					switch (this.type) {
						case DIALOGUE_SPEECH:
						case DIALOGUE_ALERT:
							this.nextFlag = true;
							break;
						case DIALOGUE_QUESTION:
							this.simpleQuestionFlag = true;
							this.nextFlag = false;
							break;
					}
				}
			}
		}
	}
	
	public boolean yesNoQuestionHasBeenAnswered() {
		return this.yesNoAnswerFlag != null;
	}
	
	public void closeDialog() {
		this.showDialog = false;
	}
	
	public void clearDialogueLines() {
		if (!this.lines.isEmpty())
			this.lines.clear();
	}
	
	private void renderText(Graphics g) {
		final int X = 8;
		final int Y1 = 120;
		final int Y2 = 136;
		final Rectangle rect = new Rectangle(X, Y1 - Tile.HEIGHT, MainComponent.GAME_WIDTH - 16, Tile.HEIGHT * 2);
		
		g.setFont(Art.font.deriveFont(8f));
		g.setColor(Color.black);
		
		String string = null;
		try {
			switch (this.completedLines.size()) {
				case 0:
					// None completed.
					string = this.lines.get(this.lineIterator).getKey();
					if (this.subStringIterator > string.length()) {
						g.drawString(string.substring(0, string.length()), X, Y1);
						this.subStringIterator = this.lineLength;
					}
					else
						g.drawString(string.substring(0, this.subStringIterator), X, Y1);
					break;
				case 1:
					// One line completed.
					g.drawString(this.completedLines.get(0), X, Y1);
					string = this.lines.get(this.lineIterator).getKey();
					if (this.subStringIterator > string.length()) {
						g.drawString(string.substring(0, string.length()), X, Y2);
						this.subStringIterator = this.lineLength;
					}
					else
						g.drawString(string.substring(0, this.subStringIterator), X, Y2);
					break;
				case 2:
					// Two lines completed.
					if (!this.scrollFlag) {
						g.drawString(this.completedLines.get(0), X, Y1);
						g.drawString(this.completedLines.get(1), X, Y2);
					}
					else {
						// Time to scroll.
						// DEBUG: Needs testing to see if there's any problem with
						// it.
						Graphics g_clipped = g.create();
						g_clipped.setClip(rect.x, rect.y, rect.width, rect.height);
						g_clipped.drawString(this.completedLines.get(0), X, Y1 - scrollDistance);
						g_clipped.drawString(this.completedLines.get(1), X, Y2 - scrollDistance);
						if (tickCount == 0x0) {
							if (scrollDistance >= Y2 - Y1) {
								this.scrollFlag = false;
								this.scrollDistance = 0;
								this.subStringIterator = 0;
								this.completedLines.remove(0);
								this.lines.get(this.lineIterator).setValue(true);
							}
						}
						g_clipped.dispose();
					}
					break;
			}
		}
		catch (Exception e) {
		}
		
	}
	
	public static NewDialogue createEmptyDialogue() {
		return new NewDialogue(MainComponent.getMainInput());
	}
	
	public static NewDialogue createText(String dialogue, int length, int type, boolean lock) {
		NewDialogue dialogues = new NewDialogue(MainComponent.getMainInput());
		dialogues.lines = toLines(dialogue, length);
		dialogues.lineLength = length;
		dialogues.totalDialogueLength = dialogue.length();
		dialogues.type = type;
		dialogues.showDialog = true;
		if (lock) {
			if (!Player.isMovementsLocked())
				Player.lockMovements();
		}
		return dialogues;
	}
	
	public static void renderDialogBox(BaseScreen output, int x, int y, int centerWidth, int centerHeight) {
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
	}
	
	public static ArrayList<Map.Entry<NewDialogue, Integer>> loadDialogues(String filename) {
		ArrayList<Map.Entry<NewDialogue, Integer>> result = new ArrayList<Map.Entry<NewDialogue, Integer>>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(NewDialogue.class.getClassLoader().getResourceAsStream(filename)));
			String line;
			String[] tokens;
			int dialogueID = 0;
			NewDialogue temp = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					// Dialogue ID
					tokens = line.split("#");
					dialogueID = Integer.valueOf(tokens[1]);
				}
				else if (line.startsWith("@")) {
					// Dialogue message
					tokens = line.split("@");
					temp = NewDialogue.createText(tokens[1], NewDialogue.MAX_STRING_LENGTH, NewDialogue.DIALOGUE_SPEECH, false);
				}
				else if (line.startsWith("-")) {
					// Dialogue delimiter
					Map.Entry<NewDialogue, Integer> entry = new AbstractMap.SimpleEntry<NewDialogue, Integer>(temp, dialogueID);
					result.add(entry);
				}
			}
		}
		catch (Exception e) {
			return null;
		}
		return result;
	}
	
	public static ArrayList<Map.Entry<String, Boolean>> toLines(String all, final int regex) {
		ArrayList<Map.Entry<String, Boolean>> lines = new ArrayList<>();
		String[] words = all.split("\\s");
		String line = "";
		int length = 0;
		for (String w : words) {
			if (length + w.length() + 1 > regex) {
				if (w.length() >= regex) {
					line += w;
					lines.add(new AbstractMap.SimpleEntry<String, Boolean>(line, false));
					line = "";
					continue;
				}
				lines.add(new AbstractMap.SimpleEntry<String, Boolean>(line, false));
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
			lines.add(new AbstractMap.SimpleEntry<String, Boolean>(line, false));
		return lines;
	}
	
	private static void renderDialogBackground(BaseScreen output, int x, int y, int centerWidth, int centerHeight) {
		for (int j = 0; j < centerHeight - 1; j++) {
			for (int i = 0; i < centerWidth - 1; i++) {
				output.blit(Art.dialogue_background, ((x + 1) * Tile.WIDTH) + (i * Tile.WIDTH), ((y + 1) * Tile.HEIGHT) + j * Tile.HEIGHT);
			}
		}
	}
	
	private static void renderDialogBorderBox(BaseScreen output, int x, int y, int centerWidth, int centerHeight) {
		output.blit(Art.dialogue_top_left, x * Tile.WIDTH, y * Tile.HEIGHT);
		for (int i = 0; i < centerWidth - 1; i++) {
			output.blit(Art.dialogue_top, ((x + 1) * Tile.WIDTH) + (i * Tile.WIDTH), y * Tile.HEIGHT);
		}
		output.blit(Art.dialogue_top_right, (x + centerWidth) * Tile.WIDTH, y * Tile.HEIGHT);
		
		for (int j = 0; j < centerHeight - 1; j++) {
			output.blit(Art.dialogue_left, x * Tile.WIDTH, ((y + 1) * Tile.HEIGHT) + j * Tile.HEIGHT);
			output.blit(Art.dialogue_right, (x + centerWidth) * Tile.WIDTH, ((y + 1) * Tile.HEIGHT) + j * Tile.HEIGHT);
		}
		
		output.blit(Art.dialogue_bottom_left, x * Tile.WIDTH, ((y + centerHeight) * Tile.HEIGHT));
		for (int i = 0; i < centerWidth - 1; i++) {
			output.blit(Art.dialogue_bottom, ((x + 1) * Tile.WIDTH) + (i * Tile.WIDTH), ((y + centerHeight) * Tile.HEIGHT));
		}
		output.blit(Art.dialogue_bottom_right, (x + centerWidth) * Tile.WIDTH, ((y + centerHeight) * Tile.HEIGHT));
	}
	
}
