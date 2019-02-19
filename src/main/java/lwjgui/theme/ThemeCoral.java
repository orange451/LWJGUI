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
		return Color.CORAL.brighter();
	}

	@Override
	public Color getSelectionAlt() {
		return getSelection().brighter(0.9);
	}

	@Override
	public Color getSelectionPassive() {
		return Color.CORAL.brighter(0.7);
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
		return Color.CORAL.brighter(0.8);
	}

	@Override
	public Color getControlAlt() {
		return Color.CORAL.brighter(0.6);
	}

	@Override
	public Color getControlOutline() {
		return getSelection().darker();
	}

	@Override
	public Color getControlHover() {
		return getSelection().brighter(0.8);
	}
}
