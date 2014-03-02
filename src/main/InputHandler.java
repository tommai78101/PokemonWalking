package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.Keys.Key;

public class InputHandler implements KeyListener {
	
	Map<Key, Integer> mappings = new HashMap<Key, Integer>();
	private boolean hasTapped = false;
	
	public InputHandler(Keys keys) {
		initialize(keys.up, KeyEvent.VK_UP);
		initialize(keys.down, KeyEvent.VK_DOWN);
		initialize(keys.left, KeyEvent.VK_LEFT);
		initialize(keys.right, KeyEvent.VK_RIGHT);
	}
	
	public void initialize(Key key, int defaultKeyCode) {
		mappings.put(key, defaultKeyCode);
	}
	
	public void toggle(KeyEvent event, boolean value) {
		Key key = null;
		Set<Key> keySet = mappings.keySet();
		for (Key k : keySet) {
			if (mappings.get(k) == event.getKeyCode()) {
				key = k;
			}
		}
		if (key != null) {
			key.isTapped = this.hasTapped;
			key.nextState = value;
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		toggle(e, true);
		this.hasTapped = false;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		toggle(e, false);
		this.hasTapped = true;
	}
	
}
