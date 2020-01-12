package lwjgui.scene.shape;

import org.joml.Vector2f;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.paint.Color;
import lwjgui.scene.Context;

/**
 * Creates a customizable circle.
 *
 */
public class SectorCircle extends Shape {
	protected float radius;
	protected float completion;
	protected float rotation;
	protected int circlePoints;
	protected Vector2f renderOffset = new Vector2f();
	
	/**
	 * Creates a circle with customizable attributes.
	 * 
	 * @param radius - the radius of the circle
	 * @param completion - the "completion" of the circle, a value between 0 and 1.0, where something like 0.5 would be a half circle.
	 * @param rotation - the rotation of the circle in radians
	 * @param circlePoints
	 */
	public SectorCircle(Color fill, float radius, float completion, float rotation, int circlePoints) {
		super(fill);
		this.radius = radius;
		this.completion = completion;
		this.rotation = rotation;
		this.circlePoints = circlePoints;
	}

	@Override
	public String getElementType() {
		return "sectorcircle";
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
		if ( !isVisible() )
			return;
		
		clip(context);
		
		float x = (float) getX();
		float y = (float) getY();
		
		float centerX = x + radius + renderOffset.x;
		float centerY = y + radius + renderOffset.y;
		
        float pi2 = (float) (Math.PI * 2) * completion;
		
		NanoVG.nvgBeginPath(context.getNVG());
		
		//The anti-aliasing behaves weird for some reason if I start at zero, giving the starting line a slight slant.
		int startOffset = 1;
		
		for (int i = startOffset; i < circlePoints; i++) {
        	float px = (float) (centerX + (radius * Math.cos(rotation + (i * pi2 / circlePoints))));
        	float py = (float) (centerY + (radius * Math.sin(rotation + (i * pi2 / circlePoints))));
			
			if (i == startOffset) {
				NanoVG.nvgMoveTo(context.getNVG(), px, py);
			} else {
				NanoVG.nvgLineTo(context.getNVG(), px, py);
			}
		}

		NanoVG.nvgFillColor(context.getNVG(), fill.getNVG());
		NanoVG.nvgFill(context.getNVG());
		
		NanoVG.nvgClosePath(context.getNVG());
	}

}
