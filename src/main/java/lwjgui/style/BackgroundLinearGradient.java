package lwjgui.style;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.paint.Color;
import lwjgui.scene.Context;

public class BackgroundLinearGradient extends Background {
	private ColorStop[] stops;
	private float angle;
	
	public BackgroundLinearGradient(float angle, ColorStop... stops) {
		this.stops = stops;
		this.angle = angle;
	}
	
	public BackgroundLinearGradient(float angle, Color...colors) {
		this.angle = angle;
		
		this.stops = new ColorStop[colors.length];
		for (int i = 0; i < colors.length; i++) {
			this.stops[i] = new ColorStop( colors[i], (float)i / (float)(colors.length-1) );
		}
	}

	@Override
	public void render(Context context, double x, double y, double width, double height, float[] cornerRadii) {
		if ( context == null )
			return;
		
		if ( stops == null || stops.length == 0 )
			return;
		
		if ( cornerRadii == null )
			cornerRadii = new float[] {0,0,0,0};
		
		for (int i = 0; i < stops.length-1; i++) {
			// Compute position
			float centerx = (float)x + (float)width*0.5f;
			float centery = (float)y + (float)height*0.5f;
			float xx = centerx - (float)((Math.cos(Math.toRadians(angle)) * width) * 0.5 + 0.5);
			float yy = centery - (float)((Math.sin(Math.toRadians(angle)) * height) * 0.5 + 0.5);
			float dirX = (float) Math.cos(Math.toRadians(angle));
			float dirY = (float) Math.sin(Math.toRadians(angle));
			float step = stops[i].getRatio();
			float nextstep = stops[i+1].getRatio();
			float startX = (float)xx + (dirX * step * (float)width);
			float startY = (float)yy + (dirY * step * (float)height);
			float endX = (float)xx + (dirX * nextstep * (float)width);
			float endY = (float)yy + (dirY * nextstep * (float)height);
			
			// Compute colors
			Color c = stops[i].getColor();
			if ( i > 0 )
				c = Color.TRANSPARENT;
			Color e = stops[i+1].getColor();
			
			// Create gradient paint
			NVGPaint grad = NanoVG.nvgLinearGradient(context.getNVG(), startX, startY, endX, endY, c.getNVG(), e.getNVG(), NVGPaint.create());

			// Draw gradient
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRoundedRectVarying(context.getNVG(), (int)x, (int)y, (int)width, (int)height, (float)cornerRadii[0], (float)cornerRadii[1], (float)cornerRadii[2], (float)cornerRadii[3]);
			NanoVG.nvgFillPaint(context.getNVG(), grad);
			NanoVG.nvgFill(context.getNVG());
			NanoVG.nvgClosePath(context.getNVG());
		}
	}
}
