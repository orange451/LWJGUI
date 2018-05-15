package lwjgui.scene.layout;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;
import lwjgui.geometry.Node;

public class StackPane extends Pane {
	
	public StackPane() {
		this.setFillToParentWidth(true);
		this.setFillToParentHeight(true);
	}

	public void render(Context context) {
		clip(context);
		
		if ( getBackground() != null ) {
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRect(context.getNVG(), (int)getAbsoluteX(), (int)getAbsoluteY(), (float)getWidth(), (float)getHeight());
			NanoVG.nvgFillColor(context.getNVG(), getBackground().getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
		/*
		long vg = context.getNVG();
		float x = 32;
		float y = 32;
		float w = 64;
		float h = 64;
		float r = 4;
		float feather = 8;
		NVGPaint paint = NanoVG.nvgBoxGradient(vg, x+feather/4,y+feather/4, w,h,r*2, feather, Theme.currentTheme().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.create());
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, x-feather,y-feather, w+feather*2,h+feather*2);
		NanoVG.nvgRoundedRect(vg, x,y, w,h, r);
		NanoVG.nvgFillPaint(vg, paint);
		NanoVG.nvgFill(vg);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRoundedRect(context.getNVG(), x, y, w, h, r);
		NanoVG.nvgFillColor(context.getNVG(), Color.lightYellow.getNVG());
		NanoVG.nvgFill(context.getNVG());
		*/
		
		for (int i = 0; i < children.size(); i++) {
			// Draw child
			Node child = children.get(i);
			child.render(context);
		}
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
}
