package lwjgui.callbacks;

import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowIconifyCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

public final class Callbacks {

	private Callbacks() {
	}

	public static class WindowCloseCallback extends Callback<GLFWWindowCloseCallbackI>
			implements GLFWWindowCloseCallbackI {
		@Override
		public void invoke(long window) {
			for (GLFWWindowCloseCallbackI callback : callbacks)
				callback.invoke(window);
		}
	}

	public static class WindowSizeCallback extends Callback<GLFWWindowSizeCallbackI>
			implements GLFWWindowSizeCallbackI {
		@Override
		public void invoke(long window, int width, int height) {
			for (GLFWWindowSizeCallbackI callback : callbacks)
				callback.invoke(window, width, height);
		}
	}

	public static class WindowFocusCallback extends Callback<GLFWWindowFocusCallbackI>
			implements GLFWWindowFocusCallbackI {
		@Override
		public void invoke(long window, boolean focused) {
			for (GLFWWindowFocusCallbackI callback : callbacks)
				callback.invoke(window, focused);
		}
	}

	public static class KeyCallback extends Callback<GLFWKeyCallbackI> implements GLFWKeyCallbackI {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			for (GLFWKeyCallbackI callback : callbacks)
				callback.invoke(window, key, scancode, action, mods);
		}
	}

	public static class CharCallback extends Callback<GLFWCharCallbackI> implements GLFWCharCallbackI {
		@Override
		public void invoke(long window, int codepoint) {
			for (GLFWCharCallbackI callback : callbacks)
				callback.invoke(window, codepoint);
		}
	}

	public static class MouseButtonCallback extends Callback<GLFWMouseButtonCallbackI>
			implements GLFWMouseButtonCallbackI {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			for (GLFWMouseButtonCallbackI callback : callbacks)
				callback.invoke(window, button, action, mods);
		}
	}

	public static class CursorPosCallback extends Callback<GLFWCursorPosCallbackI> implements GLFWCursorPosCallbackI {
		@Override
		public void invoke(long window, double xpos, double ypos) {
			for (GLFWCursorPosCallbackI callback : callbacks)
				callback.invoke(window, xpos, ypos);
		}
	}

	public static class ScrollCallback extends Callback<GLFWScrollCallbackI> implements GLFWScrollCallbackI {
		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			for (GLFWScrollCallbackI callback : callbacks)
				callback.invoke(window, xoffset, yoffset);
		}
	}

	public static class WindowIconifyCallback extends Callback<GLFWWindowIconifyCallbackI>
			implements GLFWWindowIconifyCallbackI {
		@Override
		public void invoke(long window, boolean iconified) {
			for (GLFWWindowIconifyCallbackI callback : callbacks)
				callback.invoke(window, iconified);
		}
	}
}
