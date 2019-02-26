package lwjgui.theme;

import lwjgui.paint.Color;

public class ThemeDark extends Theme {

	public static Color backgroundColor		= Color.BLACK;
	public static Color paneColor			= new Color(35,35,35);
	public static Color paneColorAlt		= new Color(35,35,35);
	public static Color selectColor			= new Color(110, 135, 160);
	public static Color selectColorPassive	= Color.DARK_GRAY;
	public static Color selectColorAlt		= selectColor.brighter();
	public static Color shadow				= new Color(16, 16, 16, 100);
	public static Color textColor			= Color.SILVER;
	public static Color textColorAlt		= Color.DARK_GRAY;
	public static Color controlColor			= new Color(60,60,60);
	public static Color controlColorAlt		= new Color(50,50,50);
	public static Color controlHoverColor	= new Color(80,85,90);
	public static Color controlOutlineColor	= new Color(20,20,20);

	@Override
	public Color getBackground() {
		return backgroundColor;
	}

	@Override
	public Color getBackgroundAlt() {
		return Color.DARK_GRAY;
	}
	
	@Override
	public Color getPane() {
		return paneColor;
	}
	
	@Override
	public Color getPaneAlt() {
		return paneColorAlt;
	}

	@Override
	public Color getSelection() {
		return selectColor;
	}

	@Override
	public Color getSelectionAlt() {
		return selectColorAlt;
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
	public Color getTextAlt() {
		return textColorAlt;
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
