package lwjgui.style;

public interface StyleCornerRadius {
	public float[] getCornerRadii();
	public void setCornerRadii(float radius);
	public void setCornerRadii(float cornerTopLeft, float cornerTopRight, float cornerBottomRight, float cornerBottomLeft);
}
