package lwjgui.scene.layout;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;
import lwjgui.geometry.Node;
import lwjgui.geometry.VPos;

public class VBox extends DirectionalBox {
	
	@Override
	public void position( Node parent ) {
		super.position(parent);
		
		double totalHeight = getMaximumPotentialHeight();
		
		float mult = 0;
		if ( getAlignment().getVpos() == VPos.CENTER)
			mult = 0.5f;
		if ( getAlignment().getVpos() == VPos.BOTTOM)
			mult = 1;
		
		double yStart = (getHeight()-totalHeight)*mult;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			
			child.setLocalPosition(this, child.getX(),yStart);
			yStart += child.getHeight();
			yStart += this.getSpacing();
		}
	}

	public void render(Context context) {
		clip(context);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRect(context.getNVG(), (float)getAbsoluteX(), (float)getAbsoluteY(), (float)getWidth(), (float)getHeight());
		NanoVG.nvgFillColor(context.getNVG(), getBackground().getNVG());
		NanoVG.nvgFill(context.getNVG());
		
		for (int i = 0; i < children.size(); i++) {
			// Clip
			NanoVG.nvgScissor(context.getNVG(), (float)getAbsoluteX(), (float)getAbsoluteY(), (float)getWidth(), (float)getHeight());
			
			// Draw child
			Node child = children.get(i);
			child.render(context);
		}
	}
	
	@Override
	public double getMinimumPotentialHeight() {
		return super.getMinimumPotentialHeight() + (Math.max(0, children.size()-1)*spacing);
	}
	
	@Override
	public double getMinimumPotentialWidth() {
		return getWidth()-this.getInnerBounds().getWidth();
	}
}
