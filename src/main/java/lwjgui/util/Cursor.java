package lwjgui.util;

import static org.lwjgl.glfw.GLFW.*;

/**
 * This class can be used to create mouse cursors for use in various types of user-interfaces.
 * 
 */
public class Cursor {
	public enum CursorType {
		/**
		 * The default arrow-type mouse icon.
		 */
		ARROW(GLFW_ARROW_CURSOR),
		
		/**
		 * The I cursor used frequently for typing interfaces.
		 */
		I_BEAM(GLFW_IBEAM_CURSOR),
		
		/**
		 * A cross-shaped crosshair cursor.
		 */
		CROSSHAIR(GLFW_CROSSHAIR_CURSOR),
		
		/**
		 * A pointing finger icon.
		 */
		HAND(GLFW_HAND_CURSOR),
		
		/**
		 * A cursor used for resizing an element horizontally.
		 */
		HORIZONTAL_RESIZE(GLFW_HRESIZE_CURSOR),
		
		/**
		 * A cursor used for resizing an element vertically.
		 */
		VERTICAL_RESIZE(GLFW_VRESIZE_CURSOR);
		
		private int shape;
		
		private CursorType(int shape) {
			this.shape = shape;
		}
	}
	
	private long cursor = -1;
	
	/**
	 * Creates a new Cursor object without loading any cursor.
	 */
	public Cursor() {
		
	}
	
	/**
	 * Creates a new Cursor object and loads the given CursorType.
	 */
	public Cursor(CursorType type) {
		loadCursor(type);
	}
	
	/**
	 * Loads the given standard CursorType. If loaded, you must make sure to also call destroyCursor() at the end of the program to properly dispose of it.
	 * 
	 * If this function is called and the currently loaded cursor hasn't been disposed of yet, destroyCursor() will automatically be called before loading the new cursor.
	 */
	public void loadCursor(CursorType type) {
		if (cursor != -1) {
			destroyCursor();
		}
		
		cursor = glfwCreateStandardCursor(type.shape);
	}
	
	/**
	 * Sets the currently loaded cursor.
	 * @param windowHandle
	 */
	public void setCursor(long windowHandle) {
		glfwSetCursor(windowHandle, cursor);
	}
	
	/**
	 * Destroys and removes the cursor from GLFW.
	 */
	public void destroyCursor() {
		glfwDestroyCursor(cursor);
		cursor = -1;
	}
}
	
	
