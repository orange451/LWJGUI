package lwjgui.theme;

import lwjgui.paint.Color;
import lwjgui.theme.Theme;

public class ThemeCoral extends Theme {

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
		return Color.blend(getSelection().brighter(0.8), Color.WHITE, new Color(Color.BLACK), 0.75);
	}
}
