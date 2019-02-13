package lwjgui.scene.layout.floating;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.event.EventHandler;
import lwjgui.event.ScrollEvent;

public class PannablePane extends DraggablePane {
	public PannablePane() {
		this.flag_clip = true;
		
		this.center();
		LWJGUI.runLater(()->{
			this.center();
		});
		
		// Draggable by scrolling (for laptops)
		this.mouseScrollEventInternal = new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if ( isDescendentHovered() ) {
					PannablePane.this.offset(-event.x, -event.y);
				}
			}
		};
		
		// Draggable using mouse wheel AS A BUTTON
		this.mouseButton = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
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
