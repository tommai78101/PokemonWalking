package interfaces;

/**
 * This interface holds information about whether the object can do in-game actions, such as
 * walking, running, bicycling, swimming/surfing, and more.
 */
public interface CharacterActionable {
	void walk();

	void sprint();

	void jump();

	void ride();

	void swim();
}