package dataset;

public class EditorData {
	private int editorData;
	private int xPosition;
	private int yPosition;
	private int colorData;

	public int getColorData() {
		return this.colorData;
	}

	public int getEditorData() {
		return this.editorData;
	}

	public int getX() {
		return this.xPosition;
	}

	public int getY() {
		return this.yPosition;
	}

	public void setColorData(int colorData) {
		this.colorData = colorData;
	}

	public void setEditorData(int editorData) {
		this.editorData = editorData;
	}

	public void setX(int xPosition) {
		this.xPosition = xPosition;
	}

	public void setY(int yPosition) {
		this.yPosition = yPosition;
	}
}
