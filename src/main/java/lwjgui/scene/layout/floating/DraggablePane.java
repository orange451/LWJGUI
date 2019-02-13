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
	
	@Override
	public void position(Node parent) {
		super.position(parent);
		
		int mouse = GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
		double mouseX = this.cached_context.getMouseX();
		double mouseY = this.cached_context.getMouseY();
		
		if ( mouse == GLFW.GLFW_PRESS ) {
			if ( !dragging && !failedClick ) {
				if ( this.cached_context.isMouseInside(this) ) {
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
		
		if ( dragging ) {
			this.setAbsolutePosition(mouseX-dragOffset.x, mouseY-dragOffset.y);
		}
	}
}