package lwjgui.style;

import lwjgui.paint.Color;

public class Shadow {
	private final Color toColor = Color.TRANSPARENT;
	
	protected float xOffset;
	protected float yOffset;
	protected float blurRadius;
	protected Color fromColor;
	protected boolean inset;
	
	public Shadow(float xOffset, float yOffset, float blurRadius, Color fromColor, boolean inset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.blurRadius = blurRadius;
		this.fromColor = fromColor;
		this.inset = inset;
	}
	
	public Shadow clone() {
		return new Shadow(getXOffset(), getYOffset(), getBlurRadius(), getFromColor(), isInset());
	}
	
	public Shadow(float xOffset, float yOffset, float blurRadius, Color fromColor) {
		this(xOffset, yOffset, blurRadius, fromColor, false);
	}
	
	public Shadow(float xOffset, float yOffset, float blurRadius) {
		this(xOffset, yOffset, blurRadius, Color.BLACK.alpha(0.5f));
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
	
	public Color getFromColor() {
		return this.fromColor;
	}
	
	public Color getToColor() {
		return this.toColor;
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
