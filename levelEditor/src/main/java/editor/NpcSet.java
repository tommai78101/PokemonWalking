package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import common.Debug;

class NpcData {
	private int editorData;
	private int xPosition;
	private int yPosition;

	public int getEditorData() {
		return editorData;
	}

	public void setEditorData(int editorData) {
		this.editorData = editorData;
	}

	public int getX() {
		return xPosition;
	}

	public void setX(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getY() {
		return yPosition;
	}

	public void setY(int yPosition) {
		this.yPosition = yPosition;
	}
}

public class NpcSet {
	private List<NpcData> npcEditorData;

	public NpcSet() {
		this.npcEditorData = new ArrayList<>();
	}

	public void add(int x, int y, int editorData) {
		NpcData data = new NpcData();
		data.setEditorData(editorData);
		data.setX(x);
		data.setY(y);
		this.npcEditorData.add(data);
	}

	public int[] produce() {
		int size = this.npcEditorData.size();
		List<Integer> result = new ArrayList<>();
		result.add(size);
		for (int i = 0; i < size; i++) {
			NpcData d = this.npcEditorData.get(i);
			result.add(d.getX());
			result.add(d.getY());
			result.add(d.getEditorData());
		}
		return result.parallelStream().mapToInt(Integer::intValue).toArray();
	}

	public void read(int[] data) {
		if ((data.length - 1) % 3 != 0) {
			Debug.error("Invalid data structure for NPC data set.");
			return;
		}
		this.npcEditorData = new ArrayList<>();
		int size = data[0];
		for (int i = 1; i < size; i += 3) {
			this.add(data[i], data[i + 1], data[i + 2]);
		}
	}

	// ---------------------------------------------------------------------
	// Getters and setters

	public List<NpcData> getNpcEditorData() {
		return npcEditorData;
	}

	public void setNpcEditorData(List<NpcData> npcEditorData) {
		this.npcEditorData = npcEditorData;
	}

	public int getSize() {
		return this.npcEditorData.size();
	}
}
