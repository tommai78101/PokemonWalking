package abstracts;

import interfaces.CharacterActionable;
import interfaces.Interactable;
import interfaces.Obstructable;

public abstract class Character extends Entity implements Interactable, CharacterActionable, Obstructable {
	/**
	 * Entity class objects include NPC, Player, and monsters. Thus, it is fitting for the Entity objects to include a GenderType.
	 */
	public enum GenderType {
		// @formatter:off
		Nondetermined((byte) 0x7F), 
		Male((byte) 0x1), 
		Female((byte) 0xFF);
		// @formatter:on

		private byte typeId;

		private GenderType(byte value) {
			this.typeId = value;
		}

		public byte getByte() {
			return this.typeId;
		}

		public static GenderType determineGender(byte value) {
			if (value == Male.typeId)
				return Male;
			if (value == Female.typeId)
				return Female;
			return Nondetermined;
		}
	}
	
	// These numbers correspond to the index number of the columns of the character sprite sheet.
	public static final int UP = 2;
	public static final int DOWN = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 3;
	
	private boolean isPlayable;
	private int interactionDataColorID = 0;
	private GenderType gender = GenderType.Nondetermined;

	protected void setCharacterPlayable(final boolean value) {
		this.isPlayable = value;
	}

	public boolean isCharacterPlayable() {
		return this.isPlayable;
	}

	public int getInteractableID() {
		return this.interactionDataColorID;
	}

	public void setInteractableID(int dataColor) {
		this.interactionDataColorID = dataColor;
	}

	public void setGender(final GenderType value) {
		this.gender = value;
	}

	public GenderType getGender() {
		return this.gender;
	}
}