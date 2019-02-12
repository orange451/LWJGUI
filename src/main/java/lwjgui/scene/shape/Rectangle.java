package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.scene.Context;

public class Rectangle extends Shape {
	private float corderRadius = 0;
	
	public Rectangle() {
		this(0);
	}
	
	public Rectangle( float radius ) {
		this.setPrefSize(16, 16);
		this.setCornerRadius(radius);
	}
	
	public Rectangle( int width, int height, Color color ) {
		this.setPrefSize(width, height);
		this.setFill(color);
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
		NanoVG.nvgRoundedRect(context.getNVG(), (float)getX(), (float)getY(), (float)getWidth(), (float)getHeight(), corderRadius);
		NanoVG.nvgFillColor(context.getNVG(), fill.getNVG());
		NanoVG.nvgFill(context.getNVG());
	}

}
