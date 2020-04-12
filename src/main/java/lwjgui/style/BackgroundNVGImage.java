package lwjgui.style;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryStack;

import lwjgui.LWJGUIUtil;
import lwjgui.gl.OffscreenBuffer;
import lwjgui.scene.Context;
import lwjgui.scene.Window;

public class BackgroundNVGImage extends Background {

	private int nvgImage;
	
	public BackgroundNVGImage() {
		this(-1);
	}
	
	public BackgroundNVGImage(int nvgImage) {
		this.setNVGImage(nvgImage);
	}
	
	public static BackgroundNVGImage fromOffscreenBuffer(Window window, OffscreenBuffer buffer) {
		if ( window.getContext().isModernOpenGL() ) {
			return new BackgroundNVGImage(NanoVGGL3.nvglCreateImageFromHandle(window.getContext().getNVG(), buffer.getTexId(), buffer.getWidth(), buffer.getHeight(), NanoVG.NVG_IMAGE_FLIPY));
		} else {
			return new BackgroundNVGImage(NanoVGGL2.nvglCreateImageFromHandle(window.getContext().getNVG(), buffer.getTexId(), buffer.getWidth(), buffer.getHeight(), NanoVG.NVG_IMAGE_FLIPY));
		}
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
