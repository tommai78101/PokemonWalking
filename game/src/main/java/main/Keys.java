/**
 * Open-source Game Boy inspired game. 
 * 
 * Created by tom_mai78101. Hobby game programming only.
 *
 * All rights copyrighted to The Pokémon Company and Nintendo. 
 */

package main;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * WASD / Arrow Keys = Directional Input
 * <p>
 * Z / Forward Slash = Primary Action
 * <p>
 * X / Period = Secondary Action
 * <p>
 * Enter = Start Button
 */
public class Keys {
	public class Key {
		public final String name;

		// Input handling variables.
		public boolean keyStateDown;

		// Key related variables.
		public boolean isTappedDown;
		public boolean isPressedDown;
		public boolean lastKeyState;

		public Key(String name) {
			this.name = name;
			Keys.this.all.add(this);
		}

		public void reset() {
			this.keyStateDown = false;
			this.isTappedDown = false;
			this.isPressedDown = false;
			this.lastKeyState = false;
		}
	}

	List<Key> all = new ArrayList<>();

	public Key up = new Key("up");
	public Key down = new Key("down");
	public Key left = new Key("left");
	public Key right = new Key("right");
	public Key Z = new Key("Z");
	public Key X = new Key("X");

	public Key W = new Key("W");
	public Key S = new Key("S");
	public Key A = new Key("A");
	public Key D = new Key("D");
	public Key SLASH = new Key("/");
	public Key PERIOD = new Key(".");

	public Key START = new Key("ENTER");

	// Debugging purposes
	public Key F1 = new Key("F1");

	public void resetAll() {
		for (Key k : this.all)
			k.reset();
	}

	public void resetInputs() {
		for (Key k : this.all) {
			switch (k.name) {
				case "Z":
				case "X":
				case "/":
				case ".":
					break;
				default:
					k.reset();
					break;
			}
		}
	}

	public boolean isUpPressed() {
		return (Game.keys.up.keyStateDown && !Game.keys.up.lastKeyState) || (Game.keys.W.keyStateDown && !Game.keys.W.lastKeyState);
	}

	public boolean isDownPressed() {
		return (Game.keys.down.keyStateDown && !Game.keys.down.lastKeyState) || (Game.keys.S.keyStateDown && !Game.keys.S.lastKeyState);
	}

	public boolean isLeftPressed() {
		return (Game.keys.left.keyStateDown && !Game.keys.left.lastKeyState) || (Game.keys.A.keyStateDown && !Game.keys.A.lastKeyState);
	}

	public boolean isRightPressed() {
		return (Game.keys.right.keyStateDown && !Game.keys.right.lastKeyState) || (Game.keys.D.keyStateDown && !Game.keys.D.lastKeyState);
	}

	public boolean isPrimaryPressed() {
		return (Game.keys.Z.keyStateDown && !Game.keys.Z.lastKeyState) || (Game.keys.SLASH.keyStateDown && !Game.keys.SLASH.lastKeyState);
	}

	public boolean isSecondaryPressed() {
		return (Game.keys.X.keyStateDown && !Game.keys.X.lastKeyState) || (Game.keys.PERIOD.keyStateDown && !Game.keys.PERIOD.lastKeyState);
	}

	public boolean isStartPressed() {
		return (Game.keys.START.keyStateDown && !Game.keys.START.lastKeyState);
	}

	public boolean isDebugPressed() {
		return (Game.keys.F1.keyStateDown && !Game.keys.F1.lastKeyState);
	}

	/**
	 * Checks if any one of the Up, Down, Left, and Right button is pressed.
	 * 
	 * @return True, if any one of buttons is pressed. False, if otherwise.
	 */
	public boolean isDpadPressed() {
		return (Game.keys.isUpPressed() || Game.keys.isDownPressed() || Game.keys.isLeftPressed() || Game.keys.isRightPressed());
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void upReceived() {
		Game.keys.up.lastKeyState = true;
		Game.keys.W.lastKeyState = true;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void downReceived() {
		Game.keys.down.lastKeyState = true;
		Game.keys.S.lastKeyState = true;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void leftReceived() {
		Game.keys.left.lastKeyState = true;
		Game.keys.A.lastKeyState = true;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void rightReceived() {
		Game.keys.right.lastKeyState = true;
		Game.keys.D.lastKeyState = true;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void primaryReceived() {
		Game.keys.Z.lastKeyState = true;
		Game.keys.SLASH.lastKeyState = true;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void secondaryReceived() {
		Game.keys.X.lastKeyState = true;
		Game.keys.PERIOD.lastKeyState = true;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void startReceived() {
		Game.keys.START.lastKeyState = true;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue telling me this button has been pressed down."
	 */
	public void debugReceived() {
		Game.keys.F1.lastKeyState = true;
	}

	public void upReleased() {
		Game.keys.up.lastKeyState = Game.keys.up.keyStateDown;
		Game.keys.up.keyStateDown = false;
		Game.keys.W.lastKeyState = Game.keys.W.keyStateDown;
		Game.keys.W.keyStateDown = false;
	}

	public void downReleased() {
		Game.keys.down.lastKeyState = Game.keys.down.keyStateDown;
		Game.keys.down.keyStateDown = false;
		Game.keys.S.lastKeyState = Game.keys.S.keyStateDown;
		Game.keys.S.keyStateDown = false;
	}

	public void leftReleased() {
		Game.keys.left.lastKeyState = Game.keys.left.keyStateDown;
		Game.keys.left.keyStateDown = false;
		Game.keys.A.lastKeyState = Game.keys.A.keyStateDown;
		Game.keys.A.keyStateDown = false;
	}

	public void rightReleased() {
		Game.keys.right.lastKeyState = Game.keys.right.keyStateDown;
		Game.keys.right.keyStateDown = false;
		Game.keys.D.lastKeyState = Game.keys.D.keyStateDown;
		Game.keys.D.keyStateDown = false;
	}

	public void primaryReleased() {
		Game.keys.Z.lastKeyState = Game.keys.Z.keyStateDown;
		Game.keys.Z.keyStateDown = false;
		Game.keys.SLASH.lastKeyState = Game.keys.SLASH.keyStateDown;
		Game.keys.SLASH.keyStateDown = false;
	}

	public void secondaryReleased() {
		Game.keys.X.lastKeyState = Game.keys.X.keyStateDown;
		Game.keys.X.keyStateDown = false;
		Game.keys.PERIOD.lastKeyState = Game.keys.PERIOD.keyStateDown;
		Game.keys.PERIOD.keyStateDown = false;
	}

	public void startReleased() {
		Game.keys.START.lastKeyState = Game.keys.START.keyStateDown;
		Game.keys.START.keyStateDown = false;
	}

	public void debugReleased() {
		Game.keys.F1.lastKeyState = Game.keys.F1.keyStateDown;
		Game.keys.F1.keyStateDown = false;
	}
}
