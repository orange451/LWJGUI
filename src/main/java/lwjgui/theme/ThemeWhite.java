package lwjgui.theme;

import lwjgui.paint.Color;

public class ThemeWhite extends Theme {
	public static Color backgroundColor		= Color.WHITE;
	public static Color paneColor			= Color.WHITE_SMOKE;
	public static Color paneAltColor		= new Color(240, 240, 240);
	public static Color selectColor			= Color.AQUA;
	public static Color selectColorAlt		= selectColor.brighter(0.9);
	public static Color selectColorPassive	= Color.LIGHT_GRAY;
	public static Color shadow				= new Color(32, 32, 32, 100);
	public static Color textColor			= Color.DARK_GRAY;
	public static Color textColorAlt		= Color.WHITE_SMOKE;
	public static Color controlColor		= new Color(240, 240, 240);
	public static Color controlColorAlt		= new Color(230, 230, 230);
	public static Color controlHoverColor	= Color.WHITE;
	public static Color controlOutlineColor	= Color.DIM_GRAY;

	@Override
	public Color getBackground() {
		return backgroundColor;
	}

	@Override
	public Color getBackgroundAlt() {
		return Color.LIGHT_GRAY;
	}
	
	@Override
	public Color getPane() {
		return Color.WHITE_SMOKE;
	}
	
	@Override
	public Color getPaneAlt() {
		return new Color(230, 230, 230);
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
		return new Color(240, 240, 240);
	}

	@Override
	public Color getControlAlt() {
		return new Color(230, 230, 230);
	}

	@Override
	public Color getControlOutline() {
		return Color.DIM_GRAY;
	}

	@Override
	public Color getControlHover() {
		return Color.WHITE;
	}
}
