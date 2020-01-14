package lwjgui.scene.image;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.scene.Context;
import lwjgui.scene.FillableRegion;

public class ImageView extends FillableRegion {
	private Image image;
	private boolean stretchToFit = true;

	public ImageView() {
		this.setPrefSize(100, 100);
	}

	public ImageView(Image image) {
		this();
		setImage(image);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void setMaintainAspectRatio(boolean maintain) {
		this.stretchToFit = !maintain;
	}

	public String getElementType() {
		return "imageview";
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;

		if (image == null)
			return;

		if (!image.isLoaded())
			return;

		int img = image.getImage();

		int x = (int) this.getX();
		int y = (int) this.getY();
		int w = (int) this.getWidth();
		int h = (int) this.getHeight();
		int xx = x;
		int yy = y;
		int ww = w;
		int hh = h;
		if (!stretchToFit) {
			double wid = getWidth() + (image.getWidth() - (image.getWidth() / image.getHeight() * image.getWidth()));
			double hei = getHeight() + (image.getHeight() - (image.getWidth() / image.getHeight() * image.getHeight()));
			double ratio = wid / hei;
			xx = x;
			yy = y - (int) ((w - h) / 2);
			ww = w;
			hh = (int) (h * ratio);
			if (ratio < 1) {
				ratio = 1.0 / ratio;
				xx = x - (int) ((h - w) / 2);
				yy = y;
				ww = (int) (w * ratio);
				hh = h;
			}
		}
		try (MemoryStack stack = stackPush()) {
			if ( context == null )
				return;
			long vg = context.getNVG();
			NVGPaint imagePaint = NanoVG.nvgImagePattern(vg, xx, yy, ww, hh, 0, img, 1, NVGPaint.callocStack(stack));
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRect(vg, x, y, w, h);
			NanoVG.nvgFillPaint(vg, imagePaint);
			NanoVG.nvgFill(vg);
		}
	}

	public void setImage(Image image) {
		if (this.image != null && image != this.image)
			this.image.dispose();
		this.image = image;
	}

}
