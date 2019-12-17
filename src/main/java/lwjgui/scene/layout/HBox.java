package lwjgui.scene.layout;

import lwjgui.geometry.VPos;
import lwjgui.scene.Node;

public class HBox extends DirectionalBox {
	
	@Override
	public void updateChildren() {
		super.updateChildren();
		
		double totalWidth = this.getMaxPotentialWidth();
		
		float xMult = 0;
		/*if ( getAlignment().getHpos() == HPos.CENTER)
			xMult = 0.5f;
		if ( getAlignment().getHpos() == HPos.RIGHT)
			xMult = 1;*/
		
		double yMult = 0;
		if ( getAlignment().getVpos() == VPos.CENTER )
			yMult = 0.5;
		if ( getAlignment().getVpos() == VPos.BOTTOM )
			yMult = 1;
		
		int xStart = (int) ((getWidth()*xMult)-(totalWidth*xMult));
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			
			double yy = (this.getHeight()-child.getHeight())*yMult;
			
			child.setLocalPosition(this, xStart, yy);
			xStart += child.getWidth();
			xStart += this.getSpacing();
		}
	}
	
	@Override
	protected double getMaxElementWidth() {
		return this.getMinimumPotentialWidth();
	}
	
	@Override
	protected double getMinimumPotentialWidth() {
		//return this.getMaxElementHeight();
		//return getHeight()-this.getInnerBounds().getHeight();
		return super.getMinimumPotentialWidth() + (Math.max(0, children.size()-1)*spacing);
	}
	
	@Override
	protected double getMinimumPotentialHeight() {
		//return this.getMaxElementHeight();
		return 0;//getHeight()-this.getInnerBounds().getHeight();
	}

	@Override
	public String getElementType() {
		return "hbox";
	}
}
