package lwjgui.event;

import lwjgui.scene.control.Tab;

public abstract class TabDragEvent extends Event {
	
	public abstract void onEvent( Tab tab );

	@Override
	final public void onEvent() {
	}

}
