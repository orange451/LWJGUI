package lwjgui.theme;

import lwjgui.paint.Color;

public class ThemeWhite extends Theme {
	public static Color selectColor			= Color.AQUA;
	public static Color selectColorAlt		= selectColor.brighter(0.9);
	public static Color selectColorPassive	= Color.LIGHT_GRAY;
	public static Color shadow				= new Color(32, 32, 32, 100);
	public static Color textColor			= Color.DARK_GRAY;
	public static Color textColorAlt		= Color.WHITE_SMOKE;

	@Override
	public Color getBackground() {
		return Color.WHITE;
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
		return new Color(238, 238, 238);
	}

	@Override
	public Color getControlAlt() {
		return new Color(227, 227, 227);
	}

	@Override
	public Color getControlOutline() {
		return new Color(180, 180, 180);
	}

	@Override
	public Color getControlHover() {
		return new Color(225, 225, 225);
	}
}
