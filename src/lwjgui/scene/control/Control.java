package lwjgui.scene.control;

import lwjgui.scene.Region;

public abstract class Control extends Region {
	
	public Control() {
		this.flag_clip = true;
	}
	
	@Override
	public boolean isResizeable() {
		return true;
	}
}
