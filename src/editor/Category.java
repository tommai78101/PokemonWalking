package editor;

import java.util.ArrayList;

public class Category {
	public int id;
	public String name;
	public ArrayList<Data> nodes;
	public ArrayList<Trigger> triggers;
	
	public Category() {
		this.nodes = new ArrayList<Data>();
		this.triggers = new ArrayList<Trigger>();
	}
	
	public void add(Data data) {
		this.nodes.add(data);
		this.id = data.alpha;
	}
	
	public void add(Trigger trigger) {
		this.triggers.add(trigger);
	}
}