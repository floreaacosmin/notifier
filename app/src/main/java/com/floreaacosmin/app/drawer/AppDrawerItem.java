package com.floreaacosmin.app.drawer;

class AppDrawerItem {

    private final String name;
    private final int icon;
    private final int secondIcon;

	public AppDrawerItem(String name, int icon, int secondIcon) {
		this.name = name;
		this.icon = icon;
		this.secondIcon = secondIcon;
	}

	public String getName() {
		return this.name;
	}
    
    public int getIcon() {
		return this.icon;
    }   
    
    public int getSecondIcon() {
		return secondIcon;
	}
}