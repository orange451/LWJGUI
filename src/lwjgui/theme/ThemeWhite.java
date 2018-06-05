package lwjgui.theme;

import lwjgui.Color;

public class ThemeWhite extends Theme {

	public static Color backgroundColor		= Color.WHITE;
	public static Color paneColor			= Color.WHITE_SMOKE;
	public static Color selectColor			= Color.AQUA;
	public static Color selectColorPassive	= Color.LIGHT_GRAY;
	public static Color shadow				= new Color(32, 32, 32, 100);
	public static Color textColor			= Color.DARK_GRAY;
	public static Color buttonColor			= Color.WHITE_SMOKE;
	public static Color buttonHoverColor	= Color.WHITE;
	public static Color buttonOutlineColor	= Color.SILVER;

	@Override
	public Color getBackground() {
		return backgroundColor;
	}
	
	@Override
	public Color getPane() {
		return paneColor;
	}

	@Override
	public Color getSelection() {
		return selectColor;
	}

	@Override
	public Color getSelectionPassive() {
		return selectColorPassive;
	}

	@Override
	public Color getShadow() {
		return shadow;
	}

	@Override
	public Color getText() {
		return textColor;
	}

	@Override
	public Color getButton() {
		return buttonColor;
	}

	@Override
	public Color getButtonOutline() {
		return buttonOutlineColor;
	}

	@Override
	public Color getButtonHover() {
		return buttonHoverColor;
	}
}
