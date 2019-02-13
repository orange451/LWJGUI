package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.scene.Context;

public class Rectangle extends Shape {
	protected float cornerRadius = 0;
	
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
	
	public Rectangle( int width, int height ) {
		this.setPrefSize(width, height);
	}
	
	public void setCornerRadius( float cornerRadius ) {
		this.cornerRadius = cornerRadius;
	}
	
	public float getCornerRadius() {
		return this.cornerRadius;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		clip(context);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRoundedRect(context.getNVG(), (float)getX(), (float)getY(), (float)getWidth(), (float)getHeight(), cornerRadius);
		NanoVG.nvgFillColor(context.getNVG(), fill.getNVG());
		NanoVG.nvgFill(context.getNVG());
	}

}
