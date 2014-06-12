package editor;

import java.util.ArrayList;

public class Category {
	public int id;
	public String name;
	public ArrayList<Data> nodes;
	
	public Category() {
		this.nodes = new ArrayList<Data>();
	}
	
	public void add(Data data) {
		this.nodes.add(data);
		this.id = data.alpha;
		
	}
}