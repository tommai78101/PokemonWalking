/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package editor;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class EditorInput implements MouseListener, MouseMotionListener {

	public int mouseX, mouseY;
	public int offsetX, offsetY;
	public int oldX, oldY;
	public int drawingX, drawingY;

	private boolean clicking;
	private boolean panning;
	private boolean drawing;

	private LevelEditor editor;

	public EditorInput(LevelEditor editor) {
		this.editor = editor;
		// TODO: Dispatch event to Canvas other than Control Panel. Events must be
		// separated from each other.
	}

	public boolean isDragging() {
		return this.panning;
	}

	public boolean isDrawing() {
		return this.drawing;
	}

	public boolean isClicking() {
		return this.clicking;
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		int button1 = InputEvent.BUTTON1_DOWN_MASK;
		int button3 = InputEvent.BUTTON3_DOWN_MASK;
		if ((event.getModifiersEx() & (button1 | button3)) == button1) {
			this.drawing = true;
		}
		else if ((event.getModifiersEx() & (button1 | button3)) == button3) {
			this.panning = true;
		}
		this.mouseX = event.getX();
		this.mouseY = event.getY();
		if (this.drawing) {
			this.drawingX = this.mouseX;
			this.drawingY = this.mouseY;
		}
		else if (this.panning) {
			this.offsetX = this.oldX - this.mouseX;
			this.offsetY = this.oldY - this.mouseY;
		}
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		this.drawing = false;
		this.mouseX = event.getX();
		this.mouseY = event.getY();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		this.mouseX = event.getX();
		this.mouseY = event.getY();
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.drawing = false;
			this.panning = false;
			this.clicking = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// Barely less than 2 ticks (33.34ms).
						Thread.sleep(32);
					}
					catch (InterruptedException e) {}
					EditorInput.this.clicking = false;
				}
			}).start();
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		this.drawing = false;
		this.mouseX = event.getX();
		this.mouseY = event.getY();
	}

	@Override
	public void mouseExited(MouseEvent event) {
		this.drawing = false;
		this.mouseX = event.getX();
		this.mouseY = event.getY();
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.mouseX = event.getX();
		this.mouseY = event.getY();
		if (event.getButton() == MouseEvent.BUTTON1) {
			this.drawing = true;
			this.panning = false;
			this.drawingX = this.mouseX;
			this.drawingY = this.mouseY;
		}
		else if (event.getButton() == MouseEvent.BUTTON3) {
			this.panning = true;
			this.drawing = false;
			this.oldX = this.mouseX + this.offsetX;
			this.oldY = this.mouseY + this.offsetY;
		}
		else {
			this.drawing = this.panning = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (this.drawing) {
			this.drawingX = event.getX();
			this.drawingY = event.getY();
		}
		else if (this.panning) {
			this.mouseX = event.getX();
			this.mouseY = event.getY();
			this.offsetX = this.oldX - this.mouseX;
			this.offsetY = this.oldY - this.mouseY;
		}
		this.drawing = false;
		this.panning = false;
	}

	public void forceCancelDrawing() {
		this.drawing = false;
		this.panning = false;
		this.clicking = false;
	}
}
