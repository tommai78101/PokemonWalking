package editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class EditorInput implements MouseListener, MouseMotionListener {
	
	public int mouseX, mouseY;
	public int dx, dy;
	public int oldX, oldY;
	
	private boolean panning;
	private boolean drawing;
	
	private LevelEditor editor;
	
	public EditorInput(LevelEditor editor) {
		this.editor = editor;
		//TODO: Dispatch event to Canvas other than Control Panel. Events must be separated from each other.
	}
	
	public boolean isDragging() {
		return panning;
	}
	
	public boolean isDrawing() {
		return drawing;
	}
	
	@Override
	public void mouseDragged(MouseEvent event) {
		dx = oldX - event.getX();
		dy = oldY - event.getY();
		if (event.getButton() == MouseEvent.BUTTON3) {
			panning = true;
		}
		else if (event.getButton() == MouseEvent.BUTTON1) {
			drawing = true;
		}
		editor.validate();
	}
	
	@Override
	public void mouseMoved(MouseEvent event) {
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
		}
		else if (event.getButton() == MouseEvent.BUTTON3) {
			panning = true;
		}
		editor.validate();
	}
	
	@Override
	public void mouseEntered(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		editor.validate();
		drawing = false;
	}
	
	@Override
	public void mouseExited(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		editor.validate();
		drawing = false;
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		oldX = event.getX() + dx;
		oldY = event.getY() + dy;
		if (event.getButton() == MouseEvent.BUTTON1) {
			drawing = true;
		}
		else if (event.getButton() == MouseEvent.BUTTON3) {
			panning = true;
		}
		editor.validate();
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		dx = oldX - event.getX();
		dy = oldY - event.getY();
		
		drawing = false;
		panning = false;
		editor.validate();
		
	}
	
	public void forceCancelDrawing() {
		drawing = false;
	}
}
