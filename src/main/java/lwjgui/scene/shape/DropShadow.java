package lwjgui.scene.shape;

import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class DropShadow extends Shape {
	
	private float shadowRadius;
	private Vector2f shadowOffset = new Vector2f(0, 0);


	public DropShadow(int width, int height) {
		this(width, height, 4, 4, 5);
	}
	
	public DropShadow(int width, int height, float shadowOffsetX, float shadowOffsetY, float shadowRadius) {
		shadowOffset.set(shadowOffsetX, shadowOffsetY);
		this.shadowRadius = shadowRadius;
		
		setPrefSize(width, height);
	}
	
	public float getShadowRadius() {
		return shadowRadius;
	}

	public void setShadowRadius(float shadowRadius) {
		this.shadowRadius = shadowRadius;
	}

	public Vector2f getShadowOffset() {
		return shadowOffset;
	}
	
	public void setShadowOffset(float x, float y) {
		shadowOffset.set(x, y);
	}

	@Override
	public void render(Context context) {
		long vg = context.getNVG();
		float x = (float) getX();
		float y = (float) getY();
		float w = (float) getWidth();
		float h = (float) getHeight();

		w += shadowOffset.x;
		h += shadowOffset.y;
		
		clip(context, 16);
		
		NVGPaint paint = NanoVG.nvgBoxGradient(vg, x + shadowRadius, y + shadowRadius, w - (shadowRadius * 2), h - (shadowRadius * 2), 4, 12, Theme.current().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.create());
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, x, y, w, h);
		NanoVG.nvgFillPaint(vg, paint);
		NanoVG.nvgFill(vg);
		NanoVG.nvgClosePath(context.getNVG());
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
}
