package lwjgui.font;

import lwjgui.Color;

public class FontMetaData {
	private Color color;
	private Font font;
	private FontStyle style;
	
	public FontMetaData color(Color color) {
		this.color = color;
		return this;
	}
	
	public FontMetaData style(FontStyle style) {
		this.style = style;
		return this;
	}

	public Color getColor() {
		return this.color;
	}

	public Font getFont() {
		return this.font;
	}

	public FontStyle getStyle() {
		return this.style;
	}
}
