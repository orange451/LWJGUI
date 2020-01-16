package lwjgui.font;

import lwjgui.paint.Color;

public class FontMetaData {
	private Color color;
	private Font font;
	private FontStyle style;
	private Double size;
	private Color background;
	
	/**
	 * Set the font color for this font metadata.
	 * @param color
	 * @return
	 */
	public FontMetaData color(Color color) {
		this.color = color;
		return this;
	}
	
	/**
	 * Set the background color for this font metadata.
	 * @param background
	 * @return
	 */
	public FontMetaData background(Color background) {
		this.background = background;
		return this;
	}
	
	/**
	 * Set the size for this font metadata.
	 * @param size
	 * @return
	 */
	public FontMetaData size(double size) {
		this.size = new Double(size);
		return this;
	}
	
	/**
	 * Set the style for this font metadata.
	 * @param style
	 * @return
	 */
	public FontMetaData style(FontStyle style) {
		this.style = style;
		return this;
	}
	
	/**
	 * Return the size of the font metadata.
	 * @return
	 */
	public Double getSize() {
		return this.size;
	}

	/**
	 * Return the font color for the font metadata.
	 * @return
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Return the font face for the font metadata.
	 * @return
	 */
	public Font getFont() {
		return this.font;
	}

	/**
	 * Return the font style for the font metadata.
	 * @return
	 */
	public FontStyle getStyle() {
		return this.style;
	}
	
	/**
	 * Return the font background for the font metadata.
	 * @return
	 */
	public Color getBackground() {
		return this.background;
	}
}
