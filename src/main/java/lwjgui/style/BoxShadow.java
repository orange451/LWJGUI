package lwjgui.style;

import lwjgui.paint.Color;

public class BoxShadow {
	private final Color toColor = Color.TRANSPARENT;
	
	private float xOffset;
	private float yOffset;
	private float blurRadius;
	private float spread;
	private Color fromColor;
	private boolean inset;
	
	public BoxShadow(float xOffset, float yOffset, float blurRadius, float spread, Color fromColor, boolean inset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.blurRadius = blurRadius;
		this.spread = spread;
		this.fromColor = fromColor;
		this.inset = inset;
	}
	
	public BoxShadow(float xOffset, float yOffset, float blurRadius, float spread, Color fromColor) {
		this(xOffset, yOffset, blurRadius, spread, fromColor, false);
	}
	
	public BoxShadow(float xOffset, float yOffset, float blurRadius, Color fromColor, boolean inset) {
		this(xOffset, yOffset, blurRadius, 0, fromColor, false);
	}
	
	public BoxShadow(float xOffset, float yOffset, float blurRadius, Color fromColor) {
		this(xOffset, yOffset, blurRadius, 0, fromColor);
	}
	
	public BoxShadow(float xOffset, float yOffset, float blurRadius, float spread) {
		this(xOffset, yOffset, blurRadius, spread, Color.BLACK.alpha(0.5f));
	}
	
	public BoxShadow(float xOffset, float yOffset, float blurRadius) {
		this(xOffset, yOffset, blurRadius, 0);
	}
	
	public boolean isInset() {
		return this.inset;
	}
	
	public float getXOffset() {
		return this.xOffset;
	}
	
	public float getYOffset() {
		return this.yOffset;
	}
	
	public float getBlurRadius() {
		return this.blurRadius;
	}
	
	public float getSpread() {
		return this.spread;
	}
	
	public Color getFromColor() {
		return this.fromColor;
	}
	
	public Color getToColor() {
		return this.toColor;
	}

	public void setSpread(float spread) {
		this.spread = spread;
	}
	
	public void setBlurRadius(float radius) {
		this.blurRadius = radius;
	}
	
	public void setXOffset(float xOffset) {
		this.xOffset = xOffset;
	}
	
	public void setYOffset(float yOffset) {
		this.yOffset = yOffset;
	}
	
	public void setFromColor(Color color) {
		this.fromColor = color;
	}
	
	public void setInset(boolean inset) {
		this.inset = inset;
	}
}
