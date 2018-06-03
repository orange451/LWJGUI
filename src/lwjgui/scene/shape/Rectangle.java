package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;

public class Rectangle extends Shape {
	private float corderRadius = 0;
	
	public Rectangle() {
		//
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
