package lwjgui.scene;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

public enum Cursor {
	NORMAL(GLFW.GLFW_ARROW_CURSOR),
	VRESIZE(GLFW.GLFW_VRESIZE_CURSOR),
	HRESIZE(GLFW.GLFW_HRESIZE_CURSOR),
	IBEAM(GLFW.GLFW_IBEAM_CURSOR);
	
	Cursor( int shape ) {
		this.shape = shape;
	}
	
	private int shape;
	private HashMap<Long,Long> shape_gl_cache = new HashMap<Long,Long>();
	
	protected long getCursor(long context) {
		if ( shape_gl_cache.containsKey(context) ) {
			return shape_gl_cache.get(context);
		} else {
			long value = GLFW.glfwCreateStandardCursor(shape);
			shape_gl_cache.put(context, value);
			return value;
		}
	}
}
