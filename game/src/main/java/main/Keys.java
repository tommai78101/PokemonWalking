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

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void debugReceived() {
		Game.keys.F1.lastKeyState = true;
	}

	public void debugReleased() {
		Game.keys.F1.lastKeyState = Game.keys.F1.keyStateDown;
		Game.keys.F1.keyStateDown = false;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void downReceived() {
		Game.keys.down.lastKeyState = true;
		Game.keys.S.lastKeyState = true;
	}

	public void downReleased() {
		Game.keys.down.lastKeyState = Game.keys.down.keyStateDown;
		Game.keys.down.keyStateDown = false;
		Game.keys.S.lastKeyState = Game.keys.S.keyStateDown;
		Game.keys.S.keyStateDown = false;
	}

	public boolean isDebugPressed() {
		return (Game.keys.F1.keyStateDown && !Game.keys.F1.lastKeyState);
	}

	public boolean isDownPressed() {
		return (Game.keys.down.keyStateDown && !Game.keys.down.lastKeyState) || (Game.keys.S.keyStateDown && !Game.keys.S.lastKeyState);
	}

	public boolean isDownTapped() {
		return (Game.keys.down.isTappedDown && !Game.keys.down.lastKeyState) || (Game.keys.S.isTappedDown && !Game.keys.S.lastKeyState);
	}

	public boolean isDownTyped() {
		return (Game.keys.down.isPressedDown && !Game.keys.down.lastKeyState) || (Game.keys.S.isPressedDown && !Game.keys.S.lastKeyState);
	}

	/**
	 * Checks if any one of the Up, Down, Left, and Right button is pressed.
	 * 
	 * @return True, if any one of buttons is pressed. False, if otherwise.
	 */
	public boolean isDpadPressed() {
		return (Game.keys.isUpPressed() || Game.keys.isDownPressed() || Game.keys.isLeftPressed() || Game.keys.isRightPressed());
	}

	public boolean isLeftPressed() {
		return (Game.keys.left.keyStateDown && !Game.keys.left.lastKeyState) || (Game.keys.A.keyStateDown && !Game.keys.A.lastKeyState);
	}

	public boolean isLeftTapped() {
		return (Game.keys.left.isTappedDown && !Game.keys.left.lastKeyState) || (Game.keys.A.isTappedDown && !Game.keys.A.lastKeyState);
	}

	public boolean isLeftTyped() {
		return (Game.keys.left.isPressedDown && !Game.keys.left.lastKeyState) || (Game.keys.A.isPressedDown && !Game.keys.A.lastKeyState);
	}

	public boolean isPrimaryPressed() {
		return (Game.keys.Z.keyStateDown && !Game.keys.Z.lastKeyState) || (Game.keys.SLASH.keyStateDown && !Game.keys.SLASH.lastKeyState);
	}

	public boolean isRightPressed() {
		return (Game.keys.right.keyStateDown && !Game.keys.right.lastKeyState) || (Game.keys.D.keyStateDown && !Game.keys.D.lastKeyState);
	}

	public boolean isRightTapped() {
		return (Game.keys.right.isTappedDown && !Game.keys.right.lastKeyState) || (Game.keys.D.isTappedDown && !Game.keys.D.lastKeyState);
	}

	public boolean isRightTyped() {
		return (Game.keys.right.isPressedDown && !Game.keys.right.lastKeyState) || (Game.keys.D.isPressedDown && !Game.keys.D.lastKeyState);
	}

	public boolean isSecondaryPressed() {
		return (Game.keys.X.keyStateDown && !Game.keys.X.lastKeyState) || (Game.keys.PERIOD.keyStateDown && !Game.keys.PERIOD.lastKeyState);
	}

	public boolean isStartPressed() {
		return (Game.keys.START.keyStateDown && !Game.keys.START.lastKeyState);
	}

	public boolean isUpPressed() {
		return (Game.keys.up.keyStateDown && !Game.keys.up.lastKeyState) || (Game.keys.W.keyStateDown && !Game.keys.W.lastKeyState);
	}

	public boolean isUpTapped() {
		return (Game.keys.up.isTappedDown && !Game.keys.up.lastKeyState) || (Game.keys.W.isTappedDown && !Game.keys.W.lastKeyState);
	}

	public boolean isUpTyped() {
		return (Game.keys.up.isPressedDown && !Game.keys.up.lastKeyState) || (Game.keys.W.isPressedDown && !Game.keys.W.lastKeyState);
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void leftReceived() {
		Game.keys.left.lastKeyState = true;
		Game.keys.A.lastKeyState = true;
	}

	public void leftReleased() {
		Game.keys.left.lastKeyState = Game.keys.left.keyStateDown;
		Game.keys.left.keyStateDown = false;
		Game.keys.A.lastKeyState = Game.keys.A.keyStateDown;
		Game.keys.A.keyStateDown = false;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void primaryReceived() {
		Game.keys.Z.lastKeyState = true;
		Game.keys.SLASH.lastKeyState = true;
	}

	public void primaryReleased() {
		Game.keys.Z.lastKeyState = Game.keys.Z.keyStateDown;
		Game.keys.Z.keyStateDown = false;
		Game.keys.SLASH.lastKeyState = Game.keys.SLASH.keyStateDown;
		Game.keys.SLASH.keyStateDown = false;
	}

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

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void rightReceived() {
		Game.keys.right.lastKeyState = true;
		Game.keys.D.lastKeyState = true;
	}

	public void rightReleased() {
		Game.keys.right.lastKeyState = Game.keys.right.keyStateDown;
		Game.keys.right.keyStateDown = false;
		Game.keys.D.lastKeyState = Game.keys.D.keyStateDown;
		Game.keys.D.keyStateDown = false;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void secondaryReceived() {
		Game.keys.X.lastKeyState = true;
		Game.keys.PERIOD.lastKeyState = true;
	}

	public void secondaryReleased() {
		Game.keys.X.lastKeyState = Game.keys.X.keyStateDown;
		Game.keys.X.keyStateDown = false;
		Game.keys.PERIOD.lastKeyState = Game.keys.PERIOD.keyStateDown;
		Game.keys.PERIOD.keyStateDown = false;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void startReceived() {
		Game.keys.START.lastKeyState = true;
	}

	public void startReleased() {
		Game.keys.START.lastKeyState = Game.keys.START.keyStateDown;
		Game.keys.START.keyStateDown = false;
	}

	/**
	 * Game's way of saying to the InputHandler that "it has received this event, no need to continue
	 * telling me this button has been pressed down."
	 */
	public void upReceived() {
		Game.keys.up.lastKeyState = true;
		Game.keys.W.lastKeyState = true;
	}

	public void upReleased() {
		Game.keys.up.lastKeyState = Game.keys.up.keyStateDown;
		Game.keys.up.keyStateDown = false;
		Game.keys.W.lastKeyState = Game.keys.W.keyStateDown;
		Game.keys.W.keyStateDown = false;
	}
}
