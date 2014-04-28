package dialogue;

public class MenuItem {
	private String name;
	private String enabledDescription;
	private String disabledDescription;
	private boolean enabled;
	
	public MenuItem(String name, String enabled, String disabled) {
		this.name = name;
		this.enabledDescription = enabled;
		this.disabledDescription = disabled;
	}
	
	public void toggleDescription(boolean value) {
		this.enabled = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		if (enabled)
			return enabledDescription;
		else
			return disabledDescription;
	}
}
