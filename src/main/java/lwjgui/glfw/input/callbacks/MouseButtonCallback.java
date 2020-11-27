package lwjgui.glfw.input.callbacks;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

public class MouseButtonCallback extends lwjgui.glfw.Callbacks.MouseButtonCallback {

	private Map<Integer, Boolean> button = new HashMap<>();
	private Map<Integer, Boolean> ignore = new HashMap<>();

	@Override
	public void invoke(long windowID, int button, int action, int mods) {
		if (this.button == null || this.ignore == null)
			return;
		
		this.button.put(button, (action != GLFW.GLFW_RELEASE));
		
		if (action == GLFW.GLFW_RELEASE && this.ignore.containsKey(button) && this.ignore.get(button))
			this.ignore.put(button, false);
	}

	public boolean isButtonPressed(int button) {
		if ( !this.button.containsKey(button) )
			return false;
		
		return this.button.get(button);
	}

	public boolean isButtonIgnored(int button) {
		if ( !this.ignore.containsKey(button) )
			return false;
		
		return this.ignore.get(button);
	}

	public void setButtonIgnored(int button) {
		this.ignore.put(button, true);
	}

}
