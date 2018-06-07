package lwjgui.theme;

import lwjgui.Color;

public class ThemeDark extends Theme {

	public static Color backgroundColor		= Color.BLACK;
	public static Color paneColor			= new Color(45,45,45);
	public static Color selectColor			= Color.AQUA;
	public static Color selectColorPassive	= Color.DARK_GRAY;
	public static Color shadow				= new Color(12, 12, 12, 100);
	public static Color textColor			= Color.WHITE_SMOKE;
	public static Color controlColor			= new Color(40,40,40);
	public static Color controlColorAlt		= controlColor.brighter();
	public static Color controlHoverColor	= new Color(60,60,60);
	public static Color controlOutlineColor	= new Color(20,20,20);

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
	public Color getControl() {
		return controlColor;
	}

	@Override
	public Color getControlAlt() {
		return controlColorAlt;
	}

	@Override
	public Color getControlOutline() {
		return controlOutlineColor;
	}

	@Override
	public Color getControlHover() {
		return controlHoverColor;
	}
}
