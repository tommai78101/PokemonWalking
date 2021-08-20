package dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Debug;

public class EditorDataSet {
	private Map<Map.Entry<Integer, Integer>, EditorData> cache;
	private String checksum;

	public EditorDataSet(String checksum) {
		this.checksum = checksum;
		this.cache = new HashMap<>();
	}

	public boolean add(int x, int y, int editorID, int data) {
		EditorData d = new EditorData();
		d.setEditorData(editorID);
		d.setColorData(data);
		d.setX(x);
		d.setY(y);
		EditorData prev = this.cache.put(Map.entry(x, y), d);
		return prev != null && prev.getEditorData() != data;
	}

	public boolean remove(int x, int y) {
		return this.cache.remove(Map.entry(x, y)) != null;
	}

	public EditorData get(int x, int y) {
		Map.Entry<Integer, Integer> key = Map.entry(x, y);
		return this.cache.getOrDefault(key, null);
	}

	public int[] produce() {
		int size = this.cache.size();
		List<Integer> result = new ArrayList<>();
		result.add(size);
		this.cache.forEach(
			(key, data) -> {
				result.add(data.getX());
				result.add(data.getY());
				result.add(data.getEditorData());
				result.add(data.getColorData());
			}
		);
		return result.parallelStream().mapToInt(Integer::intValue).toArray();
	}

	public void read(int[] data) {
		if ((data.length - 1) % 4 != 0) {
			Debug.error("Invalid data structure for NPC data set.");
			return;
		}
		this.cache = new HashMap<>();
		int size = data[0];
		for (int i = 1; i < size; i += 4) {
			this.add(data[i], data[i + 1], data[i + 2], data[i + 3]);
		}
	}

	public boolean isEmpty() {
		return this.cache.isEmpty();
	}

	public boolean matchesChecksum(String checksum) {
		return this.checksum.equals(checksum);
	}

	public int getSize() {
		return this.cache.size();
	}
}
