/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class EditorInput implements MouseListener, MouseMotionListener {

	public int mouseX, mouseY;
	public int offsetX, offsetY;
	public int oldX, oldY;
	public int drawingX, drawingY;

	private boolean panning;
	private boolean drawing;

	private LevelEditor editor;

	public EditorInput(LevelEditor editor) {
		this.editor = editor;
		// TODO: Dispatch event to Canvas other than Control Panel. Events must be
		// separated from each other.
	}

	public boolean isDragging() {
		return panning;
	}

	public boolean isDrawing() {
		return drawing;
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		int button1 = MouseEvent.BUTTON1_DOWN_MASK;
		int button3 = MouseEvent.BUTTON3_DOWN_MASK;
		if ((event.getModifiersEx() & (button1 | button3)) == button1) {
			drawing = true;
		} else if ((event.getModifiersEx() & (button1 | button3)) == button3) {
			panning = true;
		}
		mouseX = event.getX();
		mouseY = event.getY();
		if (drawing) {
			drawingX = mouseX;
			drawingY = mouseY;
		} else if (panning) {
			offsetX = oldX - mouseX;
			offsetY = oldY - mouseY;
		}
		editor.validate();
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		drawing = false;
		mouseX = event.getX();
		mouseY = event.getY();
		editor.validate();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		if (event.getButton() == MouseEvent.BUTTON1) {
			drawing = true;
			panning = false;
		}
		if (drawing) {
			drawingX = mouseX;
			drawingY = mouseY;
		}
		editor.validate();
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		drawing = false;
		mouseX = event.getX();
		mouseY = event.getY();
		editor.validate();
	}

	@Override
	public void mouseExited(MouseEvent event) {
		drawing = false;
		mouseX = event.getX();
		mouseY = event.getY();
		editor.validate();

	}

	@Override
	public void mousePressed(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		if (event.getButton() == MouseEvent.BUTTON1) {
			drawing = true;
			panning = false;
			drawingX = mouseX;
			drawingY = mouseY;
		} else if (event.getButton() == MouseEvent.BUTTON3) {
			panning = true;
			drawing = false;
			oldX = mouseX + offsetX;
			oldY = mouseY + offsetY;
		} else {
			drawing = panning = false;
		}
		editor.validate();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (drawing) {
			drawingX = event.getX();
			drawingY = event.getY();
		} else if (panning) {
			mouseX = event.getX();
			mouseY = event.getY();
			offsetX = oldX - mouseX;
			offsetY = oldY - mouseY;
		}
		drawing = false;
		panning = false;
		editor.validate();

	}

	public void forceCancelDrawing() {
		drawing = false;
		panning = false;
	}
}
