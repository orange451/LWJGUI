package lwjgui.scene.control;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class ProgressBar extends Control {
	protected double progress;
	
	public ProgressBar() {
		this.setPrefSize(100, 14);
	}
	
	public void setProgress(double progress) {
		this.progress = Math.min( 1, Math.max( progress, 0 ) );
	}
	
	public double getProgress() {
		return this.progress;
	}

	@Override
	public String getElementType() {
		return "progressbar";
	}
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		long vg = context.getNVG();
		float x = (float) (getX()+this.getInnerBounds().getX());
		float y = (float) (getY()+this.getInnerBounds().getY());
		float w = (float) this.getInnerBounds().getWidth();
		float h = (float) this.getInnerBounds().getHeight();
		float r = 4;

		// Background
		{
			Color c1 = Theme.current().getPaneAlt();
			Color c2 = Theme.current().getBackground();
			try (MemoryStack stack = stackPush()) {
				NVGPaint grad1 = NanoVG.nvgLinearGradient(vg, x, y, x, y+h*0.5f, c1.getNVG(), c2.getNVG(), NVGPaint.callocStack(stack));
				NVGPaint grad2 = NanoVG.nvgLinearGradient(vg, x, y+h*0.5f, x, y+h, c2.getNVG(), c1.getNVG(), NVGPaint.callocStack(stack));

				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgRoundedRectVarying(vg, x, y, w, h*0.5f, r, r, 0, 0);
				NanoVG.nvgFillPaint(vg, grad1);
				NanoVG.nvgFill(vg);
				NanoVG.nvgClosePath(vg);

				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgRoundedRectVarying(vg, x, y+h*0.5f, w, h*0.5f, 0, 0, r, r);
				NanoVG.nvgFillPaint(vg, grad2);
				NanoVG.nvgFill(vg);
				NanoVG.nvgClosePath(vg);
			}
		}
		
		// Outline
		{
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRoundedRect(vg, x, y, w, h, r);
			NanoVG.nvgStrokeColor(vg, Theme.current().getControlOutline().getNVG());
			NanoVG.nvgStrokeWidth(vg, 0.6f);
			NanoVG.nvgStroke(vg);
			NanoVG.nvgClosePath(vg);
		}
		
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
