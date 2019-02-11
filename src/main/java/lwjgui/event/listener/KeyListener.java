package lwjgui.event.listener;

/**
 * This listener is invoked by keyboard inputs.
 */

@EventListenerType(type="KeyListener")
public abstract class KeyListener extends EventListener {
	public abstract void invoke(long window, int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown);
}