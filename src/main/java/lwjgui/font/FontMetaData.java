package lwjgui.font;

import lwjgui.paint.Color;

public class FontMetaData {
	private Color color;
	private Font font;
	private FontStyle style;
	private Double size;
	
	public FontMetaData color(Color color) {
		this.color = color;
		return this;
	}
	
	public FontMetaData size(double size) {
		this.size = new Double(size);
		return this;
	}
	
	public FontMetaData style(FontStyle style) {
		this.style = style;
		return this;
	}
	
	public Double getSize() {
		return this.size;
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
