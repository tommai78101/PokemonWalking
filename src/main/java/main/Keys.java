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

import java.util.ArrayList;
import java.util.List;

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

	List<Key> all = new ArrayList<Key>();

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
		for (Key k : all)
			k.reset();
	}

	public void resetInputs() {
		for (Key k : all) {
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
}
