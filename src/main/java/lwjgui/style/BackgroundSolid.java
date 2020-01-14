package lwjgui.style;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.paint.Color;
import lwjgui.scene.Context;

public class BackgroundSolid extends Background {

	private Color color;
	
	public BackgroundSolid(Color color) {
		this.color = color;
	}

	@Override
	public void render(Context context, double x, double y, double width, double height, float[] cornerRadii) {
		if ( context == null )
			return;
		
		if ( color == null )
			return;
		
		if ( color.getAlpha() <= 0 )
			return;
		
		boolean hasCorner = cornerRadii[0] != 0 || cornerRadii[1] != 0 || cornerRadii[2] != 0 || cornerRadii[3] != 0;
		
		if ( hasCorner ) {
			LWJGUIUtil.fillRoundRect(context, (int) x, (int) y, width, height, cornerRadii[0], cornerRadii[1], cornerRadii[2], cornerRadii[3], color);
		} else {
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRect(context.getNVG(), (int) x, (int) y, (float) width, (float) height);
			NanoVG.nvgFillColor(context.getNVG(), color.getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String toString() {
		return "BackgroundSolid("+color+")";
	}
}
