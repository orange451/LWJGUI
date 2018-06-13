package lwjgui.scene.layout;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.collections.ObservableList;
import lwjgui.scene.Node;
import lwjgui.scene.Region;
import lwjgui.theme.Theme;

public class Pane extends Region {
	private Color backgroundColor;
	//private boolean scrollableX;
	//private boolean scrollableY;
	
	public Pane() {
		this.setBackground(Theme.currentTheme().getPane());
		this.setPrefSize(100, 100);
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}
	
	public void setBackground(Color color) {
		this.backgroundColor = color;
	}
	
	public Color getBackground() {
		return this.backgroundColor;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);

		//float maxWidthInside = (float) getMaxElementWidth();
		//float maxHeightInside = (float) getMaxElementHeight();
		//scrollableX = maxWidthInside > this.getAbsoluteX() + this.getWidth();
		//scrollableY = maxHeightInside > this.getAbsoluteY() + this.getHeight();
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
			// Clip to my bounds
			clip(context);
			
			// Draw child
			Node child = children.get(i);
			child.render(context);
		}
	}
	
    /**
    *
    * @return modifiable list of children.
    */
   @Override
   public ObservableList<Node> getChildren() {
       return this.children;
   }

	@Override
	public boolean isResizeable() {
		return false;
	}
}
