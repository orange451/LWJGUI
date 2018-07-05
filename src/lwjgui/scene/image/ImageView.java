package lwjgui.scene.image;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Context;
import lwjgui.scene.Region;

public class ImageView extends Region {
	private Image image;
	private boolean stretchToFit = true;
	
	public ImageView() {
		this.setBackground(Color.BLACK);
		this.setPrefSize(100, 100);
	}
	
	public void setMaintainAspectRatio(boolean maintain) {
		this.stretchToFit = !maintain;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		LWJGUIUtil.fillRect(context, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), getBackground());
		
		if ( image == null )
			return;
		
		if ( !image.isLoaded() )
			return;
		
		int img = image.getImage();
		
		long vg = context.getNVG();
		int x = (int) this.getAbsoluteX();
		int y = (int) this.getAbsoluteY();
		int w = (int) this.getWidth();
		int h = (int) this.getHeight();
		int xx = x;
		int yy = y;
		int ww = w;
		int hh = h;
		if ( !stretchToFit ) {
			double wid = getWidth()+(image.getWidth()-(image.getWidth()/image.getHeight()*image.getWidth()));
			double hei = getHeight()+(image.getHeight()-(image.getWidth()/image.getHeight()*image.getHeight()));
			double ratio = wid/hei;
			xx = x;
			yy = y - (int)((w-h)/2);
			ww = w;
			hh = (int) (h*ratio);
			if ( ratio < 1 ) {
				ratio = 1.0/ratio;
				xx = x - (int)((h-w)/2);
				yy = y;
				ww = (int) (w*ratio);
				hh = h;
			}
		}
		NVGPaint imagePaint = NanoVG.nvgImagePattern(vg, xx, yy, ww, hh, 0, img, 1, NVGPaint.calloc());
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, x, y, w, h);
		NanoVG.nvgFillPaint(vg, imagePaint);
		NanoVG.nvgFill(vg);
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

}
