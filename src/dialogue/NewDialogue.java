package dialogue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import main.Game;
import main.Keys;
import main.MainComponent;
import resources.Art;
import screen.BaseScreen;
import abstracts.Tile;

public class NewDialogue {
	private ArrayList<Map.Entry<String, Boolean>> lines;
	private ArrayList<String> completedLines;
	private int subStringIterator;
	private int lineLength;
	private int totalDialogueLength;
	private byte tickCount = 0x0;
	private boolean scrollFlag;
	private int scrollDistance;
	private int lineIterator;
	private boolean nextFlag;
	private byte nextTick;
	private Game game;
	private boolean showDialog;
	
	// Dialogue max string length per line.
	public static final int MAX_STRING_LENGTH = 18;
	public static final int HALF_STRING_LENGTH = 9;
	
	private NewDialogue(Game game) {
		lines = new ArrayList<Map.Entry<String, Boolean>>();
		completedLines = new ArrayList<String>(3);
		this.subStringIterator = 0;
		this.lineLength = 0;
		this.totalDialogueLength = 0;
		this.nextFlag = false;
		this.scrollFlag = false;
		this.scrollDistance = 0;
		this.nextTick = 0x0;
		this.lineIterator = 0;
		this.game = game;
		this.showDialog = false;
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
	
	private static void renderDialogBackground(BaseScreen output, int x, int y, int centerWidth, int centerHeight) {
		for (int j = 0; j < centerHeight - 1; j++) {
			for (int i = 0; i < centerWidth - 1; i++) {
				output.blit(Art.dialogue_background, ((x + 1) * Tile.WIDTH) + (i * Tile.WIDTH), ((y + 1) * Tile.HEIGHT) + j * Tile.HEIGHT);
			}
		}
	}
	
	public void render(BaseScreen output, Graphics graphics, int x, int y, int w, int h) {
		if (showDialog) {
			renderDialogBackground(output, x, y, w, h);
			renderDialogBorderBox(output, x, y, w, h);
			if (this.nextFlag && this.nextTick < 0x8)
				output.blit(Art.dialogue_next, MainComponent.GAME_WIDTH - 16, MainComponent.GAME_HEIGHT - 8);
			graphics.drawImage(MainComponent.createCompatibleBufferedImage(output.getBufferedImage()), 0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT, null);
			renderText(graphics);
		}
	}
	
	public void tick() {
		Keys keys = this.game.getPlayer().keys;
		if (this.subStringIterator < this.totalDialogueLength && (!this.nextFlag && !this.scrollFlag)) {
			tickCount++;
			if (tickCount > 0x3)
				tickCount = 0x0;
		}
		else if (this.nextFlag) {
			this.nextTick++;
			if (this.nextTick > 0xE)
				this.nextTick = 0x0;
		}
		
		try {
			if (!this.nextFlag) {
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
						if (!this.scrollFlag)
							this.nextFlag = true;
					}
				}
				if (keys.Z.keyStateDown && !(keys.Z.lastKeyState)) {
					keys.Z.lastKeyState = true;
					tickCount = 0x8;
				}
			}
			else {
				if (keys.Z.keyStateDown && !(keys.Z.lastKeyState)) {
					keys.Z.lastKeyState = true;
					this.nextFlag = false;
					this.scrollFlag = true;
				}
			}
			if (this.scrollFlag) {
				if (this.lineIterator >= this.lines.size()) {
					this.showDialog = false;
					return;
				}
				this.scrollDistance += 8;
			}
			
		}
		catch (Exception e) {
			if (this.lineIterator >= this.lines.size()) {
				this.showDialog = false;
			}
		}
	}
	
	public boolean textIsCreated() {
		return !this.lines.isEmpty();
	}
	
	public boolean dialogBoxIsShowing() {
		return this.showDialog;
	}
	
	private void renderText(Graphics g) {
		final int X = 8 * MainComponent.GAME_SCALE;
		final int Y1 = 360;
		final int Y2 = 408;
		final Rectangle rect = new Rectangle(X, Y1 - Tile.HEIGHT * 2, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT);
		
		g.setFont(Art.font);
		g.setColor(Color.black);
		
		String string = null;
		try {
			switch (this.completedLines.size()) {
				case 0:
					//None completed.
					string = this.lines.get(this.lineIterator).getKey();
					if (this.subStringIterator > string.length()) {
						g.drawString(string.substring(0, string.length() - 1), X, Y1);
						this.subStringIterator = this.lineLength;
					}
					else
						g.drawString(string.substring(0, this.subStringIterator), X, Y1);
					break;
				case 1:
					//One line completed.
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
					//Two lines completed.
					if (!this.scrollFlag) {
						g.drawString(this.completedLines.get(0), X, Y1);
						g.drawString(this.completedLines.get(1), X, Y2);
					}
					else {
						//Time to scroll.
						g.setClip(rect.x, rect.y, rect.width, rect.height);
						g.drawString(this.completedLines.get(0), X, Y1 - scrollDistance);
						g.drawString(this.completedLines.get(1), X, Y2 - scrollDistance);
						if (tickCount == 0x0) {
							if (scrollDistance >= Y2 - Y1) {
								this.scrollFlag = false;
								this.scrollDistance = 0;
								this.subStringIterator = 0;
								this.completedLines.remove(0);
								this.lines.get(this.lineIterator).setValue(true);
							}
						}
						g.setClip(0, 0, MainComponent.COMPONENT_WIDTH, MainComponent.COMPONENT_HEIGHT);
					}
					break;
			}
		}
		catch (Exception e) {
		}
		
	}
	
	public static NewDialogue createText(Game game, String dialogue, int length) {
		NewDialogue dialogues = new NewDialogue(game);
		dialogues.lines = toLines(dialogue, length);
		dialogues.lineLength = length;
		dialogues.totalDialogueLength = dialogue.length();
		dialogues.showDialog = true;
		return dialogues;
	}
	
	private static ArrayList<Map.Entry<String, Boolean>> toLines(String all, final int regex) {
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
	
}
