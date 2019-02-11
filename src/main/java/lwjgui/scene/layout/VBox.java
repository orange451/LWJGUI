package lwjgui.scene.layout;

import lwjgui.scene.Node;

public class VBox extends DirectionalBox {
	
	@Override
	public void updateChildren() {
		super.updateChildren();
		
		double totalHeight = this.getMaxPotentialHeight();//getMinimumPotentialHeight();
		//double probableHeight = this.getAvailableSize().y;
		
		float mult = 0;
		/*if ( getAlignment().getVpos() == VPos.CENTER)
			mult = 0.5f;
		if ( getAlignment().getVpos() == VPos.BOTTOM)
			mult = 1;*/
		
		double yStart = (getHeight()-totalHeight)*mult;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			
			child.setLocalPosition(this, child.getX(),yStart);
			yStart += child.getHeight();
			yStart += this.getSpacing();
		}
	}
	
	@Override
	protected double getMaxElementHeight() {
		return getMinimumPotentialHeight();
	}
	
	@Override
	protected double getMinimumPotentialHeight() {
		return super.getMinimumPotentialHeight() + (Math.max(0, children.size()-1)*spacing);
	}
	
	@Override
	protected double getMinimumPotentialWidth() {
		//return this.getMaxElementWidth();
		return getWidth()-this.getInnerBounds().getWidth();
	}
}
