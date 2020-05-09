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

	public void add(Data data) {
		this.nodes.add(data);
		this.id = data.alpha;
	}

	public void add(Trigger trigger) {
		this.triggers.add(trigger);
	}
}