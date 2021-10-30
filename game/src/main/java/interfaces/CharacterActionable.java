package interfaces;

/**
 * This interface holds information about whether the object can do in-game actions, such as
 * walking, running, bicycling, swimming/surfing, and more.
 */
public interface CharacterActionable {
	void jump();

	void ride();

	void sprint();

	void swim();

	void walk();
}