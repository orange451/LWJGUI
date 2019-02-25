package lwjgui.scene.control;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class ProgressBar extends Control {
	protected double progress;
	
	public ProgressBar() {
		this.setPrefSize(100, 16);
	}
	
	public void setProgress(double progress) {
		this.progress = Math.min( 1, Math.max( progress, 0 ) );
	}
	
	public double getProgress() {
		return this.progress;
	}
	
	@Override
	public void render(Context context) {
		long vg = context.getNVG();
		float x = (float) (getX()+this.getInnerBounds().getX());
		float y = (float) (getY()+this.getInnerBounds().getY());
		float w = (float) this.getInnerBounds().getWidth();
		float h = (float) this.getInnerBounds().getHeight();
		float r = 9;

		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRoundedRect(vg, x, y, w, h, r);
		NanoVG.nvgFillColor(vg, Theme.current().getControl().getNVG());
		NanoVG.nvgFill(vg);
		NanoVG.nvgClosePath(vg);
		
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRoundedRect(vg, x, y, w, h, r);
		NanoVG.nvgStrokeColor(vg, Theme.current().getControlOutline().getNVG());
		NanoVG.nvgStroke(vg);
		NanoVG.nvgClosePath(vg);
		
		float pw = (float) (w*progress);
		float pr = r*0.7f;
		pw = Math.max(pw, pr*2);
		
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRoundedRect(vg, x+2, y+2, pw-4, h-5, r*0.8f);
		NanoVG.nvgFillColor(vg, Theme.current().getSelectionAlt().getNVG());
		NanoVG.nvgFill(vg);
		NanoVG.nvgClosePath(vg);
		
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRoundedRect(vg, x+2, y+3, pw-4, h-5, r*0.8f);
		NanoVG.nvgFillColor(vg, Theme.current().getSelection().getNVG());
		NanoVG.nvgFill(vg);
		NanoVG.nvgClosePath(vg);
	}
}
