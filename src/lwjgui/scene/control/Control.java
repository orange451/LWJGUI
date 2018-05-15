package lwjgui.scene.control;

import lwjgui.scene.layout.Region;

public abstract class Control extends Region {
	@Override
	public boolean isResizeable() {
		return true;
	}
}
