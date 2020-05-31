package editor;

import java.util.ArrayList;
import java.util.List;

public class Category {
	public int id;
	public String name;
	public List<Data> nodes;
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

	public int setIdByData(Data data) {
		this.id = data.alpha;
		return this.id;
	}

	public void add(Data data) {
		this.nodes.add(data);
	}

	public void add(Trigger trigger) {
		this.triggers.add(trigger);
	}

	public boolean matchesData(Data data) {
		return this.id == data.alpha;
	}
}