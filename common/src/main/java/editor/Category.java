package editor;

import java.util.ArrayList;
import java.util.List;

public class Category {
	public int id;
	public String name;
	public List<SpriteData> nodes;
	public List<Trigger> triggers;

	public Category() {
		this.nodes = new ArrayList<>();
		this.triggers = new ArrayList<>();
	}

	public Category(String name, int id) {
		this.nodes = new ArrayList<>();
		this.triggers = new ArrayList<>();
		this.name = name;
		this.id = id;
	}

	public void add(SpriteData data) {
		this.nodes.add(data);
	}

	public void add(Trigger trigger) {
		this.triggers.add(trigger);
	}

	public boolean matchesData(SpriteData data) {
		return this.id == data.alpha;
	}

	public int setIdByData(SpriteData data) {
		this.id = data.alpha;
		return this.id;
	}
}