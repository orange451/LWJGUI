package lwjgui.scene.layout;

import lwjgui.scene.Node;

public class HBox extends DirectionalBox {
	
	@Override
	public void position( Node parent ) {
		super.position(parent);
		
		double totalWidth = this.getAvailableSize().x;//getMinimumPotentialWidth();
		
		float xMult = 0;
		/*if ( getAlignment().getHpos() == HPos.CENTER)
			xMult = 0.5f;
		if ( getAlignment().getHpos() == HPos.RIGHT)
			xMult = 1;*/
		
		double xStart = (getWidth()*xMult)-(totalWidth*xMult);
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			
			child.setLocalPosition(this, xStart,child.getY());
			xStart += child.getWidth();
			xStart += this.getSpacing();
		}
	}

	@Override
	protected double getMaxElementWidth() {
		return getMinimumPotentialWidth();
	}
	
	@Override
	protected double getMinimumPotentialHeight() {
		//return this.getMaxElementHeight();
		return getHeight()-this.getInnerBounds().getHeight();
	}
	
	@Override
	protected double getMinimumPotentialWidth() {
		return super.getMinimumPotentialWidth() + (Math.max(0, children.size()-1)*spacing);
	}
}
