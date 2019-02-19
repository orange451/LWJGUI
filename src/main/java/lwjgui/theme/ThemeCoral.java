package lwjgui.theme;

import lwjgui.Color;
import lwjgui.theme.Theme;

public class ThemeCoral extends Theme {

	@Override
	public Color getBackground() {
		return Color.WHITE_SMOKE;
	}
	
	@Override
	public Color getPane() {
		return Color.WHITE;
	}

	@Override
	public Color getSelection() {
		return Color.LIGHT_GRAY;
	}

	@Override
	public Color getSelectionAlt() {
		return getSelection().brighter(0.9);
	}

	@Override
	public Color getSelectionPassive() {
		return Color.CORAL;
	}

	@Override
	public Color getShadow() {
		return new Color(32, 32, 32, 100);
	}

	@Override
	public Color getText() {
		return Color.DARK_GRAY;
	}

	@Override
	public Color getTextAlt() {
		return Color.WHITE_SMOKE;
	}
	
	@Override
	public Color getControl() {
		return Color.CORAL;
	}

	@Override
	public Color getControlAlt() {
		return Color.WHITE_SMOKE;
	}

	@Override
	public Color getControlOutline() {
		return Color.CORAL;
	}

	@Override
	public Color getControlHover() {
		return Color.DARK_GRAY;
	}
}
