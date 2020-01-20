package lwjgui.glfw;

import java.util.ArrayList;
import java.util.List;

public abstract class Callback<T> {

	protected List<T> callbacks = new ArrayList<>();

	public void addCallback(T callback) {
		if (callback != null)
			callbacks.add(callback);
	}

	public void removeCallback(T callback) {
		if (callback != null)
			callbacks.remove(callback);
	}

}
