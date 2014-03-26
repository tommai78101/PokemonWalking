package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import main.Keys.Key;

public class NewInputHandler implements KeyListener {
	
	/*
	 * Requirements:
	 * 
	 *  1. Needs to detect "tap" and "press".
	 *  
	 * 
	 */
	public Map<Key, Integer> mappings = new HashMap<Key, Integer>();
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	
	//private Keys keys;
	
	/**
	 * Initializes the control inputs.
	 * 
	 * This input handler use threads to monitor inputs that require tapping or pressing the keys.
	 * 
	 * @param Keys
	 *            An object that holds key input properties. All properties are used throughout
	 *            the life cycle of this application.
	 * */
	public NewInputHandler(Keys keys) {
		//this.keys = keys;
		mappings.put(keys.up, KeyEvent.VK_UP);
		mappings.put(keys.down, KeyEvent.VK_DOWN);
		mappings.put(keys.left, KeyEvent.VK_LEFT);
		mappings.put(keys.right, KeyEvent.VK_RIGHT);
		
		mappings.put(keys.W, KeyEvent.VK_W);
		mappings.put(keys.S, KeyEvent.VK_S);
		mappings.put(keys.A, KeyEvent.VK_A);
		mappings.put(keys.D, KeyEvent.VK_D);
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		for (Key v : mappings.keySet()) {
			if (mappings.get(v) == event.getKeyCode()) {
				if (!v.keyStateDown) {
					final Key key = v;
					key.isTappedDown = true;
					key.isPressedDown = false;
					key.keyStateDown = true;
					this.threadPool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(100);
							}
							catch (InterruptedException e) {
							}
							if (key.keyStateDown) {
								key.isPressedDown = true;
								key.isTappedDown = false;
							}
						}
					});
					break;
				}
				else
					break;
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		for (Key k : mappings.keySet()) {
			if (mappings.get(k) == event.getKeyCode()) {
				k.isPressedDown = false;
				k.isTappedDown = false;
				k.keyStateDown = false;
				break;
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {
		//Ignore. Used for sending Unicode character mapped as a system input.
	}
	
}
