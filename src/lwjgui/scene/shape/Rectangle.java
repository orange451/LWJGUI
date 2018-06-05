package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;

public class Rectangle extends Shape {
	private float corderRadius = 0;
	
	public Rectangle() {
		this(0);
	}
	
	public Rectangle( float radius ) {
		this.setPrefSize(16, 16);
		this.setCornerRadius(radius);
	}
	
	public void setCornerRadius( float radius ) {
		this.corderRadius = radius;
	}
	
	public float getCornerRadius() {
		return this.corderRadius;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		clip(context);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRoundedRect(context.getNVG(), (float)getAbsoluteX(), (float)getAbsoluteY(), (float)getWidth(), (float)getHeight(), corderRadius);
		NanoVG.nvgFillColor(context.getNVG(), fill.getNVG());
		NanoVG.nvgFill(context.getNVG());
	}

}
