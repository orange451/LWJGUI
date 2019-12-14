package lwjgui.style;

import lwjgui.paint.Color;

public interface StyleBorder {
	public float[] getBorderRadii();
	public void setBorderRadii(float radius);
	public void setBorderRadii(float cornerTopLeft, float cornerTopRight, float cornerBottomRight, float cornerBottomLeft);
	public void setBorderStyle(BorderStyle style);
	public void setBorderColor(Color color);
	public void setBorderWidth(float width);
	public BorderStyle getBorderStyle();
	public Color getBorderColor();
	public float getBorderWidth();
}
