package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;

public class Circle extends Shape {
	private float radius = 8;
	
	public Circle() {
		//
	}
	
	public Circle( float radius ) {
		this.setRadius(radius);
	}
	
	public float getRadius() {
		return this.radius;
	}
	
	public void setRadius( float radius ) {
		this.radius = radius;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	protected void resize() {
		this.setPrefSize(radius*2, radius*2);
	}

	@Override
	public void render(Context context) {
		clip(context);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgCircle(context.getNVG(), (float)getAbsoluteX()+(float)this.getWidth()/2f, (float)getAbsoluteY()+(float)this.getHeight()/2f, radius);
		//NanoVG.nvgRoundedRect(context.getNVG(), (float)getAbsoluteX(), (float)getAbsoluteY(), (float)getWidth(), (float)getHeight(), 4);
		NanoVG.nvgFillColor(context.getNVG(), fill.getNVG());
		NanoVG.nvgFill(context.getNVG());
	}

}
