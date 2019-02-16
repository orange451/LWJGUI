package lwjgui.scene.shape;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class DropShadow extends Shape {
	
	private float shadowRadius;
	
	public DropShadow(int width, int height, float shadowRadius) {
		this.shadowRadius = shadowRadius;
		
		setPrefSize(width, height);
	}
	
	@Override
	public void render(Context context) {
		long vg = context.getNVG();
		float x = (float) getX();
		float y = (float) getY();
		float w = (float) getWidth();
		float h = (float) getHeight();
		
		clip(context, 16);
		
		NVGPaint paint = NanoVG.nvgBoxGradient(vg, x + shadowRadius, y + shadowRadius, w - (shadowRadius * 2), h - (shadowRadius * 2), 4, 12, Theme.currentTheme().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.create());
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
