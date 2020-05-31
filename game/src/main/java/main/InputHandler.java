/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.Keys.Key;

public class InputHandler implements KeyListener {
	public Map<Key, Integer> mappings = new HashMap<>();
	public static final ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * Initializes the control inputs.
	 * 
	 * This input handler use threads to monitor inputs that require tapping or pressing the keys.
	 * 
	 * @param Keys
	 *            An object that holds key input properties. All properties are used throughout the life cycle of this application.
	 */
	public InputHandler(Keys keys) {
		this.mappings.put(keys.up, KeyEvent.VK_UP);
		this.mappings.put(keys.down, KeyEvent.VK_DOWN);
		this.mappings.put(keys.left, KeyEvent.VK_LEFT);
		this.mappings.put(keys.right, KeyEvent.VK_RIGHT);
		this.mappings.put(keys.Z, KeyEvent.VK_Z);
		this.mappings.put(keys.X, KeyEvent.VK_X);

		this.mappings.put(keys.W, KeyEvent.VK_W);
		this.mappings.put(keys.S, KeyEvent.VK_S);
		this.mappings.put(keys.A, KeyEvent.VK_A);
		this.mappings.put(keys.D, KeyEvent.VK_D);
		this.mappings.put(keys.SLASH, KeyEvent.VK_SLASH);
		this.mappings.put(keys.PERIOD, KeyEvent.VK_PERIOD);

		this.mappings.put(keys.START, KeyEvent.VK_ENTER);

		// Debugging purposes
		this.mappings.put(keys.F1, KeyEvent.VK_F1);

	}

	@Override
	public void keyPressed(KeyEvent event) {
		int code = event.getKeyCode();
		for (Key key : this.mappings.keySet()) {
			if (this.mappings.get(key) == code && !key.keyStateDown) {
				key.lastKeyState = key.keyStateDown;
				key.isTappedDown = true;
				key.isPressedDown = false;
				key.keyStateDown = true;
				InputHandler.threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(100);
						}
						catch (InterruptedException e) {}
						if (key.keyStateDown) {
							key.isPressedDown = true;
							key.isTappedDown = false;
						}
					}
				});
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		for (Key k : this.mappings.keySet()) {
			if (this.mappings.get(k) == event.getKeyCode()) {
				k.lastKeyState = k.keyStateDown;
				k.isPressedDown = false;
				k.isTappedDown = false;
				k.keyStateDown = false;
				break;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Ignore. Used for sending Unicode character mapped as a system input.
	}
}