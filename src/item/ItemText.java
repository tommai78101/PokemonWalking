/**
 * THIS IS CREATED BY tom_mai78101. PLEASE GIVE CREDIT FOR WORKING ON A CLONE.
 * 
 * ALL WORKS COPYRIGHTED TO The Pokémon Company and Nintendo. I REPEAT, THIS IS A CLONE.
 * 
 * YOU MAY NOT SELL COMMERCIALLY, OR YOU WILL BE PROSECUTED BY The Pokémon Company AND Nintendo.
 * 
 * THE CREATOR IS NOT LIABLE FOR ANY DAMAGES DONE. FOLLOW LOCAL LAWS, BE RESPECTFUL, AND HAVE A GOOD DAY!
 * */

package item;

import abstracts.Item.Category;

public class ItemText {
	public enum Type {
		ALL("ALL"), DUMMY("DUMMY");
		
		private String value;
		
		Type(String value) {
			this.value = value;
		}
		
		public static Type getType(String value) {
			for (Type t : Type.values()) {
				if (t.value.equals(value.toUpperCase()))
					return t;
			}
			return ALL;
		}
	};
	
	public Type type;
	public String itemName;
	public String description;
	public Category category;
	public boolean skipCheckCategory;
	public int id;
	public boolean setCommandFlag;
	public boolean useCommandFlag;
	public boolean tossCommandFlag;
	public boolean done;
	
	public ItemText() {
		type = null;
		itemName = null;
		description = null;
		category = null;
		id = -1;
		skipCheckCategory = false;
		setCommandFlag = useCommandFlag = tossCommandFlag = false;
		done = false;
	}
	
	public boolean isComplete() {
		return done;
	}
}
