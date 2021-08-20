package dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import common.Debug;

public class EditorDataSet {
	private List<EditorData> editorDataSet;
	private String checksum;

	public EditorDataSet(String checksum) {
		this.checksum = checksum;
		this.editorDataSet = new ArrayList<>();
	}

	public boolean add(int x, int y, int data) {
		EditorData d = new EditorData();
		d.setEditorData(data);
		d.setX(x);
		d.setY(y);
		return this.editorDataSet.add(d);
	}

	public boolean remove(int x, int y) {
		int oldSize = this.editorDataSet.size();
		this.editorDataSet = this.editorDataSet.stream()
			.filter(npc -> x != npc.getX() && y != npc.getY())
			.collect(Collectors.toList());
		int newSize = this.editorDataSet.size();
		return oldSize != newSize;
	}

	public int[] produce() {
		int size = this.editorDataSet.size();
		List<Integer> result = new ArrayList<>();
		result.add(size);
		for (int i = 0; i < size; i++) {
			EditorData d = this.editorDataSet.get(i);
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
		this.editorDataSet = new ArrayList<>();
		int size = data[0];
		for (int i = 1; i < size; i += 3) {
			this.add(data[i], data[i + 1], data[i + 2]);
		}
	}

	public boolean isEmpty() {
		return this.editorDataSet.isEmpty();
	}

	public boolean matchesChecksum(String checksum) {
		return this.checksum.equals(checksum);
	}

	// ---------------------------------------------------------------------
	// Getters and setters

	public List<EditorData> getNpcEditorData() {
		return this.editorDataSet;
	}

	public void setNpcEditorData(List<EditorData> npcEditorData) {
		this.editorDataSet = npcEditorData;
	}

	public int getSize() {
		return this.editorDataSet.size();
	}
}
