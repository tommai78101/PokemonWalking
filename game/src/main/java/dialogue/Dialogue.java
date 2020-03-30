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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import interfaces.Tileable;
import main.Game;
import main.MainComponent;
import resources.Art;
import screen.Scene;

//TODO (6/25/2015): Check to see why modded scripts still suffer from blinking dialogue boxes. Non-modded scripts are fixed.

public class Dialogue {
	//The type value is tied to how the data bits are parsed from the game data.
	//TODO(Thompson): Need to uncover the valid range, and whether we can actually determine arbitrary values instead?
	public static enum DialogueType {
		SPEECH(0x40),
		QUESTION(0x41),
		ALERT(0x42);

		public final int typeValue;

		DialogueType(int value) {
			this.typeValue = value;
		}

		@Override
		public String toString() {
			return Integer.toString(this.typeValue);
		}
	}

	public static final int HALF_STRING_LENGTH = 9;

	// Dialogue max string length per line.
	public static final int MAX_STRING_LENGTH = 18;

	//Tick delays
	public static final byte MAX_TICK_DELAY = 0xE;
	public static final byte CHARACTER_TICK_DELAY = 0x1;
	public static final byte ZERO_TICK = 0x0;

	private List<String> completedLines;

	private int lineIterator;
	private int lineLength;
	private List<Map.Entry<String, Boolean>> lines;

	private boolean simpleQuestionFlag;
	private boolean simpleSpeechFlag;
	private boolean ignoreInputsFlag;

	/**
	 * If true, displays the "down arrow" inside the dialogue box.
	 */
	private boolean nextFlag;

	/**
	 * If true, the dialogue is animating a scrolling animation and moving the text upwards.
	 */
	private boolean scrollFlag;

	/**
	 * Three states: YES, NO, NULL. NULL means the YesNo alert dialogue box does not appear.
	 */
	private Boolean yesNoAnswerFlag;
	private boolean yesNoCursorPosition;

	private byte nextTick;
	private int scrollDistance;
	private boolean showDialog;

	private int subStringIterator;
	private byte tickCount = 0x0;

	private int totalDialogueLength;
	private DialogueType type;

	public Dialogue() {
		this.lines = new ArrayList<>();
		this.completedLines = new ArrayList<>();
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
		this.showDialog = false;
		this.type = null;
		this.yesNoCursorPosition = true;
		this.yesNoAnswerFlag = null; // Default
		this.ignoreInputsFlag = false; // Default
	}

	public Dialogue(Dialogue dialogue) {
		// Deep copy
		this.completedLines = new ArrayList<>();
		for (String s : dialogue.completedLines)
			this.completedLines.add(s);

		this.lineIterator = dialogue.lineIterator;
		this.lineLength = dialogue.lineLength;
		this.lines = new ArrayList<>();
		for (Map.Entry<String, Boolean> e : dialogue.lines)
			this.lines.add(e);

		this.nextFlag = dialogue.nextFlag;
		this.simpleQuestionFlag = dialogue.simpleQuestionFlag;
		this.simpleSpeechFlag = dialogue.simpleSpeechFlag;
		this.yesNoCursorPosition = dialogue.yesNoCursorPosition;
		this.yesNoAnswerFlag = dialogue.yesNoAnswerFlag;
		this.nextTick = dialogue.nextTick;
		this.scrollDistance = dialogue.scrollDistance;
		this.scrollFlag = dialogue.scrollFlag;
		this.showDialog = dialogue.showDialog;

		this.subStringIterator = dialogue.subStringIterator;
		this.tickCount = dialogue.tickCount;
		this.totalDialogueLength = dialogue.totalDialogueLength;
		this.type = dialogue.type;
		this.ignoreInputsFlag = false;
	}

	public Boolean getAnswerToSimpleQuestion() {
		if (this.yesNoAnswerFlag == null)
			return null;
		return this.yesNoAnswerFlag.booleanValue();
	}

	public DialogueType getDialogueType() {
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

	/**
	 * Checks if the dialogues were set / not cleared away.
	 * 
	 * @return
	 */
	public boolean isDialogueTextSet() {
		return !this.lines.isEmpty();
	}

	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}

	public boolean isShowingDialog() {
		return this.showDialog;
	}

	public void render(Scene output, Graphics graphics) {
		this.render(output, graphics, 0, 6, 9, 2);
	}

	public void render(Scene output, Graphics graphics, int x, int y, int w, int h) {
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
		if (this.showDialog) {
			switch (this.type) {
				case SPEECH: {
					this.renderDialogBackground(output, x, y, w, h);
					this.renderDialogBorderBox(output, x, y, w, h);
					if (this.nextFlag && this.nextTick < 0x8)
						output.blit(Art.dialogue_next, MainComponent.GAME_WIDTH - 16, MainComponent.GAME_HEIGHT - 8);
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					this.renderText(g2d);
					g2d.dispose();
					break;
				}
				case QUESTION: {
					this.renderDialogBackground(output, x, y, w, h);
					this.renderDialogBorderBox(output, x, y, w, h);
					if (this.simpleQuestionFlag && !this.nextFlag) {
						this.renderDialogBackground(output, 7, 3, 2, 2);
						this.renderDialogBorderBox(output, 7, 3, 2, 2);
						// Offset by -3 for the Y axis.
						output.blit(
							Art.dialogue_pointer, MainComponent.GAME_WIDTH - Tileable.WIDTH * 3 + 8,
							this.yesNoCursorPosition ? (Tileable.HEIGHT * 4 - 3) : (Tileable.HEIGHT * 5 - 3)
						);
					}
					else if (!this.simpleQuestionFlag && (this.nextFlag && this.nextTick < 0x8))
						output.blit(Art.dialogue_next, MainComponent.GAME_WIDTH - 16, MainComponent.GAME_HEIGHT - 8);
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					this.renderText(g2d);
					this.renderYesNoAnswerText(g2d);
					g2d.dispose();
					break;
				}
				case ALERT: {
					this.renderDialogBackground(output, x, y, w, h);
					this.renderDialogBorderBox(output, x, y, w, h);
					Graphics2D g2d = output.getBufferedImage().createGraphics();
					this.renderText(g2d);
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

			final int X = Tileable.WIDTH * 8;
			final int YES_HEIGHT = Tileable.HEIGHT * 4 + 4;
			final int NO_HEIGHT = Tileable.HEIGHT * 5 + 4;
			try {
				g.drawString("YES", X, YES_HEIGHT);
				g.drawString("NO", X, NO_HEIGHT);
			}
			catch (Exception e) {}
		}
	}

	/**
	 * <p>
	 * Update method for NewDialogue class.
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING</b> : The code content of this tick() method is deliberately setup and designed in such a way that it replicates the dialogues in Gen 1 and Gen 2 Pokémon games. May require a heavy amount of refactoring/rewriting.
	 * </p>
	 */
	public void tick() {
		this.handleDialogueUpdate();

		try {
			if (this.simpleQuestionFlag) {
				if (!this.nextFlag && !this.scrollFlag) {
					// Making sure this doesn't trigger the "Next" arrow.
					this.nextFlag = false;

					if (Game.keys.isUpPressed()) {
						Game.keys.upReceived();
						// Made it consistent with Inventory's menu selection, where it doesn't wrap
						// around.
						this.yesNoCursorPosition = !this.yesNoCursorPosition;
					}
					else if (Game.keys.isDownPressed()) {
						Game.keys.downReceived();
						// Made it consistent with Inventory's menu selection, where it doesn't wrap
						// around.
						this.yesNoCursorPosition = !this.yesNoCursorPosition;
					}
					if (Game.keys.isPrimaryPressed()) {
						Game.keys.primaryReceived();
						// The answer to simple questions have already been set by UP and DOWN.
						this.yesNoAnswerFlag = this.yesNoCursorPosition;
						this.simpleQuestionFlag = false;
						this.closeDialog();
					}
					else if (Game.keys.isSecondaryPressed()) {
						Game.keys.secondaryReceived();
						// Always negative for cancel button.
						this.yesNoAnswerFlag = false;
						this.yesNoCursorPosition = false;
						this.simpleQuestionFlag = false;
						this.closeDialog();
					}
				}
			}
			else if (this.simpleSpeechFlag) {
				if (!this.nextFlag && !this.scrollFlag) {
					if (this.tickCount == 0x0) {
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
									case SPEECH:
										this.nextFlag = true;
										break;
									case QUESTION:
										// Must get to the end of the entire dialogue before asking for answers.
										if (this.lineIterator >= this.lines.size()) {
											this.simpleQuestionFlag = true;
											this.nextFlag = false;
										}
										else
											this.nextFlag = true;
										break;
									case ALERT:
										this.nextFlag = true;
										break;
								}
							}
						}
					}

					// Speeds up text speed.
					if (this.type != DialogueType.ALERT) {
						if (Game.keys.isPrimaryPressed()) {
							Game.keys.primaryReceived();
							if (!this.ignoreInputsFlag && this.subStringIterator < this.lineLength) {
								this.subStringIterator++;
							}
						}
						else if (Game.keys.isSecondaryPressed()) {
							Game.keys.secondaryReceived();
							if (!this.ignoreInputsFlag && this.subStringIterator < this.lineLength - 1) {
								this.subStringIterator = this.lineLength - 1;
							}
						}
					}
				}

				// Handles only the simplest forms of dialogues.
				else if (Game.keys.isPrimaryPressed()) {
					Game.keys.primaryReceived();
					this.closeDialog();
				}
			}

			//Does not belong in either Speech dialogue or Question dialogue.
			else {
				if (!this.nextFlag && !this.scrollFlag) {
					if (this.tickCount == 0x0) {
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
									case SPEECH:
										this.nextFlag = true;
										break;
									case QUESTION:
										// Must get to the end of the entire dialogue before asking for answers.
										if (this.lineIterator >= this.lines.size()) {
											this.simpleQuestionFlag = true;
											this.nextFlag = false;
										}
										else
											this.nextFlag = true;
										break;
									case ALERT:
										this.nextFlag = true;
										break;
								}
							}
						}
					}

					// Speeds up text speed.
					if (this.type != DialogueType.ALERT) {
						if (Game.keys.isPrimaryPressed()) {
							Game.keys.primaryReceived();
							if (!this.ignoreInputsFlag && this.subStringIterator < this.lineLength) {
								this.subStringIterator++;
							}
						}
						else if (Game.keys.isSecondaryPressed()) {
							Game.keys.secondaryReceived();
							if (!this.ignoreInputsFlag && this.subStringIterator < this.lineLength - 1) {
								this.subStringIterator = this.lineLength - 1;
							}
						}
					}
				}

				if (Game.keys.isPrimaryPressed() || Game.keys.isSecondaryPressed()) {
					if (Game.keys.isPrimaryPressed())
						Game.keys.primaryReceived();
					if (Game.keys.isSecondaryPressed())
						Game.keys.secondaryReceived();
					switch (this.type) {
						case SPEECH:
							this.nextFlag = false;
							this.scrollFlag = true;
							break;
						case QUESTION:
							// Must get to the end of the entire dialogue before asking questions.
							this.simpleQuestionFlag = false;
							this.nextFlag = false;
							this.scrollFlag = true;
							break;
						case ALERT:
							this.nextFlag = false;
							this.scrollFlag = true;
							break;
					}
				}
				else if (this.scrollFlag) {
					if (this.lineIterator >= this.lines.size()) {
						switch (this.type) {
							case QUESTION:
								this.simpleQuestionFlag = true;
								this.scrollFlag = false;
								this.nextFlag = false;
								break;
							case SPEECH:
								this.closeDialog();
								return;
							case ALERT:
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
						case SPEECH:
						case ALERT:
							this.nextFlag = true;
							break;
						case QUESTION:
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

	/**
	 * Clears dialogues and unsets the dialogue flag.
	 */
	public void clearDialogueLines() {
		if (!this.lines.isEmpty())
			this.lines.clear();
	}

	private void renderText(Graphics g) {
		final int X = 8;
		final int Y1 = 120;
		final int Y2 = 136;
		final Rectangle rect = new Rectangle(X, Y1 - Tileable.HEIGHT, MainComponent.GAME_WIDTH - 16, Tileable.HEIGHT * 2);

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
						g_clipped.drawString(this.completedLines.get(0), X, Y1 - this.scrollDistance);
						g_clipped.drawString(this.completedLines.get(1), X, Y2 - this.scrollDistance);
						if (this.tickCount == 0x0) {
							if (this.scrollDistance >= Y2 - Y1) {
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
		catch (Exception e) {}
	}

	/**
	 * This is to render the information dialogue box that will appear in the lower left corner when the Main Menu is displayed.
	 * 
	 * @param output
	 * @param x
	 * @param y
	 * @param centerWidth
	 * @param centerHeight
	 */
	public void renderInformationBox(Scene output, int x, int y, int centerWidth, int centerHeight) {
		output.blit(Art.dialogue_top_left, x * Tileable.WIDTH, y * Tileable.HEIGHT);
		for (int i = 0; i < centerWidth - 1; i++) {
			output.blit(Art.dialogue_top, ((x + 1) * Tileable.WIDTH) + (i * Tileable.WIDTH), y * Tileable.HEIGHT);
		}
		output.blit(Art.dialogue_top_right, (x + centerWidth) * Tileable.WIDTH, y * Tileable.HEIGHT);

		for (int j = 0; j < centerHeight - 1; j++) {
			output.blit(Art.dialogue_left, x * Tileable.WIDTH, ((y + 1) * Tileable.HEIGHT) + j * Tileable.HEIGHT);
			for (int i = 0; i < centerWidth - 1; i++) {
				output.blit(
					Art.dialogue_background, ((x + 1) * Tileable.WIDTH) + (i * Tileable.WIDTH),
					((y + 1) * Tileable.HEIGHT) + j * Tileable.HEIGHT
				);
			}
			output.blit(Art.dialogue_right, (x + centerWidth) * Tileable.WIDTH, ((y + 1) * Tileable.HEIGHT) + j * Tileable.HEIGHT);
		}

		output.blit(Art.dialogue_bottom_left, x * Tileable.WIDTH, ((y + centerHeight) * Tileable.HEIGHT));
		for (int i = 0; i < centerWidth - 1; i++) {
			output.blit(
				Art.dialogue_bottom, ((x + 1) * Tileable.WIDTH) + (i * Tileable.WIDTH),
				((y + centerHeight) * Tileable.HEIGHT)
			);
		}
		output.blit(Art.dialogue_bottom_right, (x + centerWidth) * Tileable.WIDTH, ((y + centerHeight) * Tileable.HEIGHT));
	}

	// ======================================================
	// Private methods
	// ======================================================

	private void handleDialogueUpdate() {
		int count = 0;
		try {
			for (int i = 0; i < this.lineIterator; i++) {
				count += this.lines.get(i).getKey().length();
				if (i != this.lines.size() - 1)
					count += 1;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			count = 0;
		}

		if (this.nextFlag) {
			switch (this.type) {
				case QUESTION: {
					if (count >= this.totalDialogueLength) {
						this.simpleQuestionFlag = true;
						this.nextFlag = false;
						this.scrollFlag = false;
					}
					else {
						this.nextTick++;
						if (this.nextTick > Dialogue.MAX_TICK_DELAY)
							this.nextTick = Dialogue.ZERO_TICK;
					}
					break;
				}
				case SPEECH: {
					this.nextTick++;
					if (this.nextTick > Dialogue.MAX_TICK_DELAY)
						this.nextTick = Dialogue.ZERO_TICK;
					break;
				}
				case ALERT: {
					this.nextTick++;
					if (this.nextTick > Dialogue.MAX_TICK_DELAY)
						this.nextTick = Dialogue.ZERO_TICK;
					break;
				}
			}
		}
		else if (count < this.totalDialogueLength) {
			if (!this.scrollFlag) {
				this.tickCount++;
				if (this.tickCount > Dialogue.CHARACTER_TICK_DELAY)
					this.tickCount = Dialogue.ZERO_TICK;
			}
//			else {
//				//Just speed up the dialogue.
//				this.tickCount = Dialogue.CHARACTER_TICK_DELAY;
//			}
		}
		else {
			if (this.lineIterator >= this.lines.size()) {
				switch (this.type) {
					case QUESTION:
						this.simpleQuestionFlag = true;
						this.scrollFlag = false;
						this.nextFlag = false;
						break;
					case SPEECH:
						this.simpleSpeechFlag = true;
						this.nextFlag = false;
						break;
					case ALERT:
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
					case SPEECH:
						this.nextFlag = true;
						break;
					case QUESTION:
						this.simpleQuestionFlag = true;
						this.nextFlag = true;
						break;
					case ALERT:
						this.nextFlag = true;
						break;
				}
			}
		}
	}

	private void renderDialogBackground(Scene output, int x, int y, int centerWidth, int centerHeight) {
		for (int j = 0; j < centerHeight - 1; j++) {
			for (int i = 0; i < centerWidth - 1; i++) {
				output.blit(
					Art.dialogue_background, ((x + 1) * Tileable.WIDTH) + (i * Tileable.WIDTH),
					((y + 1) * Tileable.HEIGHT) + j * Tileable.HEIGHT
				);
			}
		}
	}

	private void renderDialogBorderBox(Scene output, int x, int y, int centerWidth, int centerHeight) {
		output.blit(Art.dialogue_top_left, x * Tileable.WIDTH, y * Tileable.HEIGHT);
		for (int i = 0; i < centerWidth - 1; i++) {
			output.blit(Art.dialogue_top, ((x + 1) * Tileable.WIDTH) + (i * Tileable.WIDTH), y * Tileable.HEIGHT);
		}
		output.blit(Art.dialogue_top_right, (x + centerWidth) * Tileable.WIDTH, y * Tileable.HEIGHT);

		for (int j = 0; j < centerHeight - 1; j++) {
			output.blit(Art.dialogue_left, x * Tileable.WIDTH, ((y + 1) * Tileable.HEIGHT) + j * Tileable.HEIGHT);
			output.blit(Art.dialogue_right, (x + centerWidth) * Tileable.WIDTH, ((y + 1) * Tileable.HEIGHT) + j * Tileable.HEIGHT);
		}

		output.blit(Art.dialogue_bottom_left, x * Tileable.WIDTH, ((y + centerHeight) * Tileable.HEIGHT));
		for (int i = 0; i < centerWidth - 1; i++) {
			output.blit(
				Art.dialogue_bottom, ((x + 1) * Tileable.WIDTH) + (i * Tileable.WIDTH),
				((y + centerHeight) * Tileable.HEIGHT)
			);
		}
		output.blit(Art.dialogue_bottom_right, (x + centerWidth) * Tileable.WIDTH, ((y + centerHeight) * Tileable.HEIGHT));
	}

	public List<String> getCompletedLines() {
		return this.completedLines;
	}

	public void setCompletedLines(ArrayList<String> completedLines) {
		this.completedLines = completedLines;
	}

	public int getLineIterator() {
		return this.lineIterator;
	}

	public void setLineIterator(int lineIterator) {
		this.lineIterator = lineIterator;
	}

	public int getLineLength() {
		return this.lineLength;
	}

	public void setLineLength(int lineLength) {
		this.lineLength = lineLength;
	}

	public List<Map.Entry<String, Boolean>> getLines() {
		return this.lines;
	}

	public void setLines(List<Map.Entry<String, Boolean>> lines) {
		this.lines = lines;
	}

	public boolean isSimpleQuestionFlag() {
		return this.simpleQuestionFlag;
	}

	public void setSimpleQuestionFlag(boolean simpleQuestionFlag) {
		this.simpleQuestionFlag = simpleQuestionFlag;
	}

	public boolean isSimpleSpeechFlag() {
		return this.simpleSpeechFlag;
	}

	public void setSimpleSpeechFlag(boolean simpleSpeechFlag) {
		this.simpleSpeechFlag = simpleSpeechFlag;
	}

	public boolean isNextFlag() {
		return this.nextFlag;
	}

	public void setNextFlag(boolean nextFlag) {
		this.nextFlag = nextFlag;
	}

	public boolean isScrollFlag() {
		return this.scrollFlag;
	}

	public void setScrollFlag(boolean scrollFlag) {
		this.scrollFlag = scrollFlag;
	}

	public Boolean getYesNoAnswerFlag() {
		return this.yesNoAnswerFlag;
	}

	public void setYesNoAnswerFlag(Boolean yesNoAnswerFlag) {
		this.yesNoAnswerFlag = yesNoAnswerFlag;
	}

	public boolean isYesNoCursorPosition() {
		return this.yesNoCursorPosition;
	}

	public void setYesNoCursorPosition(boolean yesNoCursorPosition) {
		this.yesNoCursorPosition = yesNoCursorPosition;
	}

	public byte getNextTick() {
		return this.nextTick;
	}

	public void setNextTick(byte nextTick) {
		this.nextTick = nextTick;
	}

	public int getScrollDistance() {
		return this.scrollDistance;
	}

	public void setScrollDistance(int scrollDistance) {
		this.scrollDistance = scrollDistance;
	}

	public int getSubStringIterator() {
		return this.subStringIterator;
	}

	public void setSubStringIterator(int subStringIterator) {
		this.subStringIterator = subStringIterator;
	}

	public byte getTickCount() {
		return this.tickCount;
	}

	public void setTickCount(byte tickCount) {
		this.tickCount = tickCount;
	}

	public int getTotalDialogueLength() {
		return this.totalDialogueLength;
	}

	public void setTotalDialogueLength(int totalDialogueLength) {
		this.totalDialogueLength = totalDialogueLength;
	}

	public DialogueType getType() {
		return this.type;
	}

	public void setType(DialogueType type) {
		this.type = type;
	}

	public void setIgnoreInputs(boolean flag) {
		this.ignoreInputsFlag = flag;
	}

	public boolean getIgnoreInputs() {
		return this.ignoreInputsFlag;
	}
}
