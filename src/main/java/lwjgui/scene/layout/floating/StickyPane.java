package lwjgui.scene.layout.floating;

public class StickyPane extends FloatingPane {
	@Override
	public void setAbsolutePosition( double x, double y ) {
		double deltaX = x - this.getX();
		double deltaY = y - this.getY();
		
		super.setAbsolutePosition(x, y);
		
		for (int i = 0; i < children.size(); i++) {
			children.get(i).offset(deltaX, deltaY);
		}
	}
	
	@Override
	public void offset( double x, double y ) {
		setAbsolutePosition( getX()+x, getY()+y );
	}

	public String getElementType() {
		return "stickypane";
	}
}