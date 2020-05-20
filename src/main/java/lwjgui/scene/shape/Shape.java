package lwjgui.scene.shape;

import lwjgui.paint.Color;
import lwjgui.scene.Node;
import lwjgui.style.StyleFillColor;
import lwjgui.theme.Theme;

public abstract class Shape extends Node implements StyleFillColor {
	protected Color fill = null;

	public Shape() {
		fill = Theme.current().getText();
	}
	
	public Shape(Color fill) {
		this.fill = fill;
	}
	
	public void setFill(Color color) { 
		this.fill = color;
	}

	public Color getFill() {
		return fill;
	}
}
