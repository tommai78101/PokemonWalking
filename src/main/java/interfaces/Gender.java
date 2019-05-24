package interfaces;

public interface Gender {
	enum GenderType {
		Nondetermined((byte) 0x7F),
		Male((byte) 0x1),
		Female((byte) 0xFF);
	
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

	public void setGender(GenderType value);
	public GenderType getGender();
}