package lwjgui.scene.shape;

import lwjgui.Color;
import lwjgui.scene.Region;
import lwjgui.theme.Theme;

public abstract class Shape extends Region {
	protected Color fill = Theme.currentTheme().getText();
	
	public void setFill(Color color) { 
		this.fill = color;
	}

	public Color getFill() {
		return fill;
	}
}
