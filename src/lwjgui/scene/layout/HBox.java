package lwjgui.scene.layout;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;
import lwjgui.geometry.HPos;
import lwjgui.geometry.Node;

public class HBox extends DirectionalBox {
	
	@Override
	public void position( Node parent ) {
		super.position(parent);
		
		double totalWidth = getMaximumPotentialWidth();
		
		float xMult = 0;
		if ( getAlignment().getHpos() == HPos.CENTER)
			xMult = 0.5f;
		if ( getAlignment().getHpos() == HPos.RIGHT)
			xMult = 1;
		
		double xStart = (getWidth()*xMult)-(totalWidth*xMult);
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			
			child.setLocalPosition(this, xStart,child.getY());
			xStart += child.getWidth();
			xStart += this.getSpacing();
		}
	}

	public void render(Context context) {
		clip(context);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRect(context.getNVG(), 0, 0, (float)getWidth(), (float)getHeight());
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
		return getHeight()-this.getInnerBounds().getHeight();
	}
	
	@Override
	public double getMinimumPotentialWidth() {
		return super.getMinimumPotentialWidth() + (Math.max(0, children.size()-1)*spacing);
	}
}
