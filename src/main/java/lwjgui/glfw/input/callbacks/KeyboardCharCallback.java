package lwjgui.glfw.input.callbacks;

import lwjgui.glfw.Callbacks.CharCallback;
import lwjgui.util.gdx.Array;

public class KeyboardCharCallback extends CharCallback {
	private Array<String> queue;
	private String lastChar = "";
	private long lastPress;
	private boolean enabled = false;

	public void setEnabled(boolean flag) {
		this.enabled = flag;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean hasData() {
		return (this.enabled && this.queue.size > 0);
	}

	public Array<String> getData() {
		if (!this.enabled)
			return null;
		this.queue.shrink();
		Array<String> data = new Array<String>(this.queue);
		this.queue.clear();
		this.queue.ensureCapacity(5);
		return data;
	}

	@Override
	public void invoke(long window, int codepoint) {
		if (this.enabled) {
			String charr;

			charr = new String(Character.toChars(codepoint));

			if (this.lastChar.equals(charr) && ((System.currentTimeMillis() - this.lastPress) < 50))
				return; // 0.05 seconds

			this.lastChar = charr;
			this.lastPress = System.currentTimeMillis();
			this.queue.add(charr);
		}
	}

}
