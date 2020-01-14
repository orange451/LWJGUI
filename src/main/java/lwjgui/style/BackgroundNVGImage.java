package lwjgui.style;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.LWJGUIUtil;
import lwjgui.scene.Context;

public class BackgroundNVGImage extends Background {

	private int nvgImage;
	
	public BackgroundNVGImage() {
		this(-1);
	}
	
	public BackgroundNVGImage(int nvgImage) {
		this.setNVGImage(nvgImage);
	}

	public int getNVGImage() {
		return this.nvgImage;
	}

	public void setNVGImage(int image) {
		this.nvgImage = image;
	}

	@Override
	public void render(Context context, double x, double y, double width, double height, float[] cornerRadii) {
		if ( context == null )
			return;
		
		if ( this.getNVGImage() < 0 )
			return;
		
		try (MemoryStack stack = stackPush()) {
			long vg = context.getNVG();
			NVGPaint imagePaint = NanoVG.nvgImagePattern(vg,  (int) x, (int) y, (int) width, (int) height, 0, getNVGImage(), 1, NVGPaint.callocStack(stack));
			NanoVG.nvgFillPaint(vg, imagePaint);
			LWJGUIUtil.fillRoundRect(context, (int) x, (int) y, width, height, cornerRadii[0], cornerRadii[1], cornerRadii[2], cornerRadii[3], null);
		}
	}

	@Override
	public String toString() {
		return "BackgroundNVGImage("+getNVGImage()+")";
	}
}
