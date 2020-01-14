package lwjgui.style;

import lwjgui.geometry.Insets;
import lwjgui.paint.Color;

public interface StyleBorder {
	public float[] getBorderRadii();
	public void setBorderRadii(float radius);
	public void setBorderRadii(float cornerTopLeft, float cornerTopRight, float cornerBottomRight, float cornerBottomLeft);
	public void setBorderRadii(float[] radius);
	public void setBorderStyle(BorderStyle style);
	public void setBorderColor(Color color);
	public void setBorderWidth(float width);
	public void setBorder(Insets insets);
	public float getBorderWidth();
	public BorderStyle getBorderStyle();
	public Color getBorderColor();
	public Insets getBorder();
}
