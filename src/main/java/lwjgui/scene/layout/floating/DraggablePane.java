package lwjgui.scene.layout.floating;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import lwjgui.scene.Node;

public class DraggablePane extends StickyPane {
	private boolean dragging;
	private boolean failedClick;
	private Vector2d dragOffset;
	
	public DraggablePane() {
		this.dragOffset = new Vector2d();
	}
	
	/**
	 * Checks if the controls for dragging this DraggablePane are being triggered. By default, it checks if the left mouse button is down.
	 * 
	 * @return true if dragging
	 */
	public boolean isDraggingControlsTriggered() {		
		return (GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
	}
	
	public boolean isBeingDragged() {
		return dragging;
	}
	
	protected void drag() {
		double mouseX = this.cached_context.getMouseX();
		double mouseY = this.cached_context.getMouseY();
		
		if (isDraggingControlsTriggered()) {
			if ( !isBeingDragged() && !failedClick) {
				if (this.cached_context.isMouseInside(this) && this.cached_context.getHovered().isDescendentOf(this)) {
					double diffx = mouseX - this.getX();
					double diffy = mouseY - this.getY();
					
					dragOffset.set(diffx,diffy);
					dragging = true;
				} else {
					failedClick = true;
				}
			}
		} else {
			dragging = false;
			failedClick = false;
		}
		
		if (dragging) {
			this.setAbsolutePosition(mouseX-dragOffset.x, mouseY-dragOffset.y);
		}
	}
	
	@Override
	public void position(Node parent) {
		super.position(parent);
		drag();
	}
}