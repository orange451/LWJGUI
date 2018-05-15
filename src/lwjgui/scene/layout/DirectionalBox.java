package lwjgui.scene.layout;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;
import lwjgui.geometry.Node;

public abstract class DirectionalBox extends Pane {
	protected float spacing;
	
	public DirectionalBox() {
		this.setFillToParentHeight(true);
		this.setFillToParentWidth(true);
	}
	
	public void setSpacing(double d) {
		this.spacing = (float) d;
	}
	
	public double getSpacing() {
		return this.spacing;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
}
