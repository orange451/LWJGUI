package lwjgui.theme;

import lwjgui.Color;

public abstract class Theme {
	public abstract Color getBackground();
	public abstract Color getPane();
	public abstract Color getSelection();
	public abstract Color getSelectionPassive();
	public abstract Color getShadow();
	public abstract Color getText();
	public abstract Color getButton();
	public abstract Color getButtonHover();
	public abstract Color getButtonOutline();
	
	private static Theme currentTheme = new ThemeWhite();
	
	public static Theme currentTheme() {
		return currentTheme;
	}
}
