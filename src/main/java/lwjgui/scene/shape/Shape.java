package lwjgui.scene.shape;

import lwjgui.Color;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public abstract class Shape extends Node {
	protected Color fill = Theme.currentTheme().getText();
	
	public void setFill(Color color) { 
		this.fill = color;
	}

	public Color getFill() {
		return fill;
	}
}
