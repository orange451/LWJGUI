package lwjgui.scene.layout.floating;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import lwjgui.event.MouseEvent;
import lwjgui.scene.Node;

public class DraggablePane extends StickyPane {
	private Vector2d anchor = new Vector2d();

	public DraggablePane() {
		setOnMousePressedInternal(e -> {
			if (isDragInputDown()) {
				select(e);
			}
		});
		
		setOnMouseDraggedInternal(e -> {
			drag(e);
		});
	}
	
	protected void select(MouseEvent e) {
		anchor.set(e.mouseX - this.getX(), e.mouseY - this.getY());
		cached_context.setSelected(this);
	}

	@Override
	public void position(Node parent) {
		super.position(parent);
		
		if (!isDragInputDown() && cached_context.isSelected(this)) {
			cached_context.setSelected(null);
		}
	}
	
	/**
	 * Checks if the controls for dragging this DraggablePane are being triggered. By default, it checks if the left mouse button is down.
	 * 
	 * @return true if dragging
	 */
	protected boolean isDragInputDown() {		
		return (GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
	}

	protected void drag(MouseEvent e) {
		setAbsolutePosition(e.mouseX - anchor.x, e.mouseY - anchor.y);
	}
}