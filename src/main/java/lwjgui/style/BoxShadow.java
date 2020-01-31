package lwjgui.style;

import lwjgui.paint.Color;

public class BoxShadow extends Shadow {
	protected float spread;
	
	public BoxShadow(float xOffset, float yOffset, float blurRadius, float spread, Color fromColor, boolean inset) {
		super(xOffset, yOffset, blurRadius, fromColor, inset);
		this.spread = spread;
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

	public BoxShadow clone() {
		return new BoxShadow(getXOffset(), getYOffset(), getBlurRadius(), getSpread(), getFromColor(), isInset());
	}
	
	public float getSpread() {
		return this.spread;
	}

	public void setSpread(float spread) {
		this.spread = spread;
	}
}
