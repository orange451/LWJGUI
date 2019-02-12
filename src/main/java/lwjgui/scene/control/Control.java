package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.FillableRegion;

public abstract class Control extends FillableRegion {
	private ContextMenu context;
	
	public Control() {
		this.flag_clip = true;
		
		this.setMousePressedEvent(e -> {
			if ( e.button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ) {
				if ( context != null ) {
					context.show(getScene(), getAbsoluteX(), getAbsoluteY()+getHeight());
				}
			}
		});
	}
	
	@Override
	public boolean isResizeable() {
		return true;
	}
	
	public void setContextMenu(ContextMenu menu) {
		this.context = menu;
	}
	
	public ContextMenu getContextMenu() {
		return this.context;
	}
}
