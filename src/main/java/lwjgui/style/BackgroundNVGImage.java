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
	private boolean fitParentSize;

	/**
	 * Creats a blank BackgroundNVGImage
	 */
	public BackgroundNVGImage() {
		this(-1);
	}
	
	/**
	 * Creates a new BackgroundNVGImage from a NanoVG image handle
	 * @param nvgImage
	 */
	public BackgroundNVGImage(int nvgImage) {
		this.setNVGImage(nvgImage);
		this.setFitParentSize(true);
	}
	/**
	 * Creates a new BackgroundNVGImage from a Offscreen Buffer. Window object is used to detect OpenGL Version.
	 * @param window
	 * @param buffer
	 * @return
	 */
	public static BackgroundNVGImage fromOffscreenBuffer(Window window, OffscreenBuffer buffer) {
		if ( window.getContext().isModernOpenGL() ) {
			return new BackgroundNVGImage(NanoVGGL3.nvglCreateImageFromHandle(window.getContext().getNVG(), buffer.getTexId(), buffer.getWidth(), buffer.getHeight(), NanoVG.NVG_IMAGE_FLIPY));
		} else {
			return new BackgroundNVGImage(NanoVGGL2.nvglCreateImageFromHandle(window.getContext().getNVG(), buffer.getTexId(), buffer.getWidth(), buffer.getHeight(), NanoVG.NVG_IMAGE_FLIPY));
		}
	}

	/**
	 * Return the NanoVG Image handle.
	 * @return
	 */
	public int getNVGImage() {
		return this.nvgImage;
	}

	/**
	 * Sets the NanoVG Image handle.
	 * @param image
	 */
	public void setNVGImage(int image) {
		this.nvgImage = image;
	}

	/**
	 * Gets whether the image will be deformed to match the size of the parent
	 * @return	true if the image resizes to parent's dimensions, false otherwise
	 */
	public boolean fitsParentSize() {
		return fitParentSize;
	}

	/**
	 * Gets whether the image will be deformed to match the size of the parent
	 * @param fitParentSize
	 */
	public void setFitParentSize(boolean fitParentSize) {
		this.fitParentSize = fitParentSize;
	}

	/**
	 * Renders the Background.
	 */
	@Override
	public void render(Context context, double x, double y, double width, double height, float[] cornerRadii) {
		if ( context == null )
			return;

		if ( this.getNVGImage() < 0 )
			return;

		try (MemoryStack stack = stackPush()) {
			long vg = context.getNVG();
			if(!fitParentSize) {
				int[] w = new int[1];
				int[] h = new int[1];
				NanoVG.nvgImageSize(vg, this.getNVGImage(), w, h);
				width = w[0];
				height = h[0];
			}
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
