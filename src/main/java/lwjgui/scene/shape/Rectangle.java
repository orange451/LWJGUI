package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class Rectangle extends Shape {
	protected float cornerRadius = 0;
	
	protected Color strokeFill = null;
	
	public Rectangle(double width, double height, Color color) {
		this(width, height, 0, color);
	}
	
	public Rectangle(double width, double height, int cornerRadius, Color color) {
		super(color);
		this.setPrefSize(width, height);
		this.cornerRadius = cornerRadius;
	}
	
	public Rectangle(double width, double height) {
		this(width, height, 0, Theme.current().getText());
	}

	@Override
	public String getElementType() {
		return "rectangle";
	}
	
	public void setCornerRadius(float cornerRadius) {
		this.cornerRadius = cornerRadius;
	}
	
	public float getCornerRadius() {
		return this.cornerRadius;
	}

	public Color getStrokeFill() {
		return strokeFill;
	}

	public void setStrokeFill(Color strokeFill) {
		this.strokeFill = strokeFill;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		clip(context);
		
		long vg = context.getNVG();
		float x = (float) getX();
		float y = (float) getY();
		float w = (float) getWidth();
		float h = (float) getHeight();
		
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRoundedRect(vg, x, y, w, h, cornerRadius);
		NanoVG.nvgFillColor(vg, fill.getNVG());
		NanoVG.nvgFill(vg);
		NanoVG.nvgClosePath(vg);
		
		if (strokeFill != null) {
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRoundedRect(vg, x+0.5f, y+0.5f, w-1, h-1, cornerRadius-1);
			NanoVG.nvgStrokeColor(vg, strokeFill.getNVG());
			NanoVG.nvgStroke(vg);
			NanoVG.nvgClosePath(vg);
		}
		
	}

}
