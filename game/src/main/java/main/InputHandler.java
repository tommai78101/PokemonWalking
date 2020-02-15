/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.Keys.Key;

public class InputHandler implements KeyListener {
	public Map<Key, Integer> mappings = new HashMap<Key, Integer>();
	public static final ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * Initializes the control inputs.
	 * 
	 * This input handler use threads to monitor inputs that require tapping or
	 * pressing the keys.
	 * 
	 * @param Keys An object that holds key input properties. All properties are
	 *             used throughout the life cycle of this application.
	 */
	public InputHandler(Keys keys) {
		mappings.put(keys.up, KeyEvent.VK_UP);
		mappings.put(keys.down, KeyEvent.VK_DOWN);
		mappings.put(keys.left, KeyEvent.VK_LEFT);
		mappings.put(keys.right, KeyEvent.VK_RIGHT);
		mappings.put(keys.Z, KeyEvent.VK_Z);
		mappings.put(keys.X, KeyEvent.VK_X);

		mappings.put(keys.W, KeyEvent.VK_W);
		mappings.put(keys.S, KeyEvent.VK_S);
		mappings.put(keys.A, KeyEvent.VK_A);
		mappings.put(keys.D, KeyEvent.VK_D);
		mappings.put(keys.SLASH, KeyEvent.VK_SLASH);
		mappings.put(keys.PERIOD, KeyEvent.VK_PERIOD);

		mappings.put(keys.START, KeyEvent.VK_ENTER);

		// Debugging purposes
		mappings.put(keys.F1, KeyEvent.VK_F1);

	}

	@Override
	public void keyPressed(KeyEvent event) {
		int code = event.getKeyCode();
		for (Key v : mappings.keySet()) {
			if (mappings.get(v) == code) {
				if (!v.keyStateDown) {
					final Key key = v;
					key.lastKeyState = key.keyStateDown;
					key.isTappedDown = true;
					key.isPressedDown = false;
					key.keyStateDown = true;
					InputHandler.threadPool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
							}
							if (key.keyStateDown) {
								key.isPressedDown = true;
								key.isTappedDown = false;
							}
						}
					});
					break;
				} else
					break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		for (Key k : mappings.keySet()) {
			if (mappings.get(k) == event.getKeyCode()) {
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