package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.event.MouseEvent;
import lwjgui.scene.Region;

public abstract class Control extends Region {
	private ContextMenu context;
	
	public Control() {
		this.flag_clip = true;
		
		this.mousePressedEvent = new MouseEvent() {
			@Override
			public void onEvent(int button) {
				if ( button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ) {
					if ( context != null ) {
						context.show(getScene(), getAbsoluteX(), getAbsoluteY()+getHeight());
					}
				}
			}
		};
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
