package lwjgui.style;

import lwjgui.paint.Color;

public class ColorStop {
	private float ratio;
	private Color color;
	
	public ColorStop(Color color, float ratio) {
		this.color = color;
		this.ratio = ratio;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public float getRatio() {
		return this.ratio;
	}
}
