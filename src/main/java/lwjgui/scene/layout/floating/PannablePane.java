package lwjgui.scene.layout.floating;

import lwjgui.LWJGUI;

public class PannablePane extends DraggablePane {
	public PannablePane() {
		this.flag_clip = true;
		
		this.center();
		LWJGUI.runLater(()->{
			this.center();
		});
	}
	
	public boolean isDraggingControlsTriggered() {
		//Doesn't allow the PannablePane to be dragged if the mouse is over one of its DraggablePane children.
		if (!this.isBeingDragged()) {
			if ( !this.cached_context.getHovered().equals(this) )
				return false;
		}
		
		return super.isDraggingControlsTriggered();
	}
	
	/**
	 * Puts the origin of the PannablePane (0,0) in the center of its container.
	 */
	public void center() {
		final int t = 512; // Divisor so that we don't hit any rounding issues with the double
		this.setAbsolutePosition(-Integer.MAX_VALUE/t, -Integer.MAX_VALUE/t);
		this.setMinSize(Integer.MAX_VALUE/(t/2), Integer.MAX_VALUE/(t/2));

		// Push to center of container
		if ( this.getScene() != null ) {
			double offsetX = this.getScene().getWidth()/2;
			double offsetY = this.getScene().getHeight()/2;
			if ( this.getParent() != null ) {
				offsetX = this.getParent().getX()+this.getParent().getWidth()/2;
				offsetY = this.getParent().getY()+this.getParent().getHeight()/2;
			}
			
			this.offset( offsetX, offsetY );
		}
	}
}
