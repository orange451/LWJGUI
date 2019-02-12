package lwjgui.scene.layout;

import lwjgui.geometry.Pos;

public abstract class DirectionalBox extends Pane {
	protected float spacing;
	
	public DirectionalBox() {
		//this.setFillToParentHeight(true);
		//this.setFillToParentWidth(true);
		this.setPrefSize(0, 0);
		this.setAlignment(Pos.TOP_LEFT);
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
