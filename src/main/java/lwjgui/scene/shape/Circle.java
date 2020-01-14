package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class Circle extends Shape {
	private float radius;
	
	public Circle( float radius ) {
		this(radius, Theme.current().getText());
	}

	public Circle(float radius, Color fill) {
		super(fill);
		this.setRadius(radius);
	}

	@Override
	public String getElementType() {
		return "circle";
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
		if ( context == null )
			return;
		
		if ( !isVisible() )
			return;
		
		clip(context);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgCircle(context.getNVG(), (float)getX()+(float)this.getWidth()/2f, (float)getY()+(float)this.getHeight()/2f, radius);
		NanoVG.nvgFillColor(context.getNVG(), fill.getNVG());
		NanoVG.nvgFill(context.getNVG());
		NanoVG.nvgClosePath(context.getNVG());
	}

}
