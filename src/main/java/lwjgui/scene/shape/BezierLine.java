package lwjgui.scene.shape;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.scene.Context;

public class BezierLine extends Shape {
	
	private double ex, ey, c1x, c1y, c2x, c2y;
	private float strokeThickness = 1f;
	
	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public String getElementType() {
		return "bezier";
	}
	
	/**
	 * Equivalent to setAbsolutePosition(). This method is purely syntax sugar to make the start/end points more clear from a writing perspective.
	 * 
	 * @param sx
	 * @param sy
	 */
	public void setStartPosition(double sx, double sy) {
		setAbsolutePosition(sx, sy);
	}
	
	/**
	 * Sets the ending point of the bezier line.
	 * 
	 * @param x1 - end x
	 * @param x2 - end y
	 */
	public void setEndPosition(double ex, double ey) {
		this.ex = ex;
		this.ey = ey;
	}
	
	/**
	 * Sets the first control point of the line.
	 * 
	 * @param c1x
	 * @param c1y
	 */
	public void setControl1Position(double c1x, double c1y) {
		this.c1x = c1x;
		this.c1y = c1y;
	}

	/**
	 * Sets the first control point of the line.
	 * 
	 * @param c1x
	 * @param c1y
	 */
	public void setControl2Position(double c2x, double c2y) {
		this.c2x = c2x;
		this.c2y = c2y;
	}
	
	/**
	 * Sets the thickness of the line. By default it's 1.0f.
	 * 
	 * @param strokeThickness
	 */
	public void setStrokeThickness(float strokeThickness) {
		this.strokeThickness = strokeThickness;
	}
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		long vg = context.getNVG();
		
		float sx = (float) getX();
		float sy = (float) getY();
		
		float c1x = (float) (sx + this.c1x);
		float c1y = (float) (sy + this.c1y);
		
		float c2x = (float) (sx + this.c2x);
		float c2y = (float) (sy + this.c2y);
		
		float ex = (float) (sx + this.ex);
		float ey = (float) (sy + this.ey);
		
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgMoveTo(vg, sx, sy);
		NanoVG.nvgBezierTo(vg, c1x, c1y, c2x, c2y, ex, ey);
		NanoVG.nvgStrokeColor(vg, fill.getNVG());
		NanoVG.nvgStroke(vg);
		NanoVG.nvgStrokeWidth(vg, strokeThickness);
		NanoVG.nvgClosePath(vg);
	}

}
