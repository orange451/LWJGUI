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

	public String getElementType() {
		return "draggablepane";
	}
	
	protected void select(MouseEvent e) {
		anchor.set(e.mouseX - this.getX(), e.mouseY - this.getY());
		window.getContext().setSelected(this);
	}

	@Override
	public void position(Node parent) {
		super.position(parent);
		
		if (!isDragInputDown() && window.getContext().isSelected(this)) {
			window.getContext().setSelected(null);
		}
	}
	
	/**
	 * Checks if the controls for dragging this DraggablePane are being triggered. By default, it checks if the left mouse button is down.
	 * 
	 * @return true if dragging
	 */
	protected boolean isDragInputDown() {		
		return window.getMouseHandler().isButtonPressed(0);
	}

	protected void drag(MouseEvent e) {
		setAbsolutePosition(e.mouseX - anchor.x, e.mouseY - anchor.y);
	}
}