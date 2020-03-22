package menu;

import abstracts.SubMenu;
import abstracts.SubMenu.Type;

public class MenuEvent {
	protected Type menuType;
	protected SubMenu subMenu;

	public MenuEvent(SubMenu menu, Type type) {
		this.subMenu = menu;
		this.menuType = type;
	}

	public Type getType() {
		return this.menuType;
	}

	public SubMenu getMenu() {
		return this.subMenu;
	}
}
