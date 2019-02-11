package lwjgui.scene;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.collections.ObservableList;
import lwjgui.event.EventHelper;
import lwjgui.event.KeyEvent;
import lwjgui.event.MouseEvent;
import lwjgui.event.ScrollEvent;
import lwjgui.event.listener.CursorPositionListener;
import lwjgui.event.listener.EventListener;
import lwjgui.event.listener.KeyListener;
import lwjgui.event.listener.MouseButtonListener;
import lwjgui.event.listener.MouseWheelListener;
import lwjgui.event.listener.WindowCloseListener;
import lwjgui.event.listener.WindowFocusListener;
import lwjgui.event.listener.WindowSizeListener;
import lwjgui.gl.Renderer;

public class Window {
	private Context context;
	private Scene scene;
	private boolean canUserClose = true;
	protected boolean windowResizing;
	private Renderer renderCallback;
	private boolean autoDraw = true;
	private boolean autoClear = true;
	
	private HashMap<String, ArrayList<EventListener>> eventListeners = new HashMap<>();
	
	public Window(final Context context, Scene scene) {
		this.context = context;
		this.scene = scene;
		
		GLFW.glfwSetCursorPosCallback(context.getWindowHandle(), new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double x, double y) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(CursorPositionListener.class);
				
				for (int i = 0; i < listeners.size(); i++) {
					((CursorPositionListener) listeners.get(i)).invoke(window, x, y);
				}
				
				/*
				 * Call scene node listeners
				 */
				Node selected = context.getSelected();
				if ( selected == null )
					return;
				
				if ( selected.mousePressed && selected.mouseDraggedEvent != null ) {
					EventHelper.fireEvent(selected.mouseDraggedEvent, new MouseEvent(GLFW.GLFW_MOUSE_BUTTON_LEFT));
				}
			}
		});
		
		GLFW.glfwSetCharModsCallback(context.getWindowHandle(), new GLFWCharModsCallbackI() {
			@Override
			public void invoke(long handle, int key, int mods) {
				boolean isCtrlDown = (mods & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL || (mods & GLFW.GLFW_MOD_SUPER) == GLFW.GLFW_MOD_SUPER;
				boolean isAltDown = (mods & GLFW.GLFW_MOD_ALT) == GLFW.GLFW_MOD_ALT;
				boolean isShiftDown = (mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT;
				
				notifyTextInput( scene, key, mods, isCtrlDown, isAltDown, isShiftDown);
			}

			private void notifyTextInput(Node root, int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				if ( root.textInputEvent != null ) {
					boolean consumed = EventHelper.fireEvent(root.textInputEvent, new KeyEvent(key, mods, isCtrlDown, isAltDown, isShiftDown));
					if ( consumed )
						return;
				}
				
				ObservableList<Node> children = root.getChildren();
				for (int i = 0; i < children.size(); i++) {
					notifyTextInput(children.get(i), key, mods, isCtrlDown, isAltDown, isShiftDown);
				}
			}
		});
		
		GLFW.glfwSetKeyCallback(context.getWindowHandle(), new GLFWKeyCallback() {

			@Override
			public void invoke(long handle, int key, int scancode, int action, int mods) {
				boolean isCtrlDown = (mods & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL || (mods & GLFW.GLFW_MOD_SUPER) == GLFW.GLFW_MOD_SUPER;
				boolean isAltDown = (mods & GLFW.GLFW_MOD_ALT) == GLFW.GLFW_MOD_ALT;
				boolean isShiftDown = (mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT;
				
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(KeyListener.class);
				
				for (int i = 0; i < listeners.size(); i++) {
					((KeyListener) listeners.get(i)).invoke(handle, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				}
				
				/*
				 * Call scene node listeners
				 */
				
				notifyKeyInput( scene, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown );
			}

			private void notifyKeyInput(Node root, int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				ObservableList<Node> children = root.getChildren();
				for (int i = 0; i < children.size(); i++) {
					notifyKeyInput(children.get(i), key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				}
				
				if ( action == GLFW.GLFW_PRESS && root.keyPressedEvent != null ) {
					boolean consumed = EventHelper.fireEvent(root.keyPressedEvent, new KeyEvent(key, mods, isCtrlDown, isAltDown, isShiftDown));
					if ( consumed )
						return;
				}
				
				if ( action == GLFW.GLFW_RELEASE && root.keyReleasedEvent != null ) {
					boolean consumed = EventHelper.fireEvent(root.keyReleasedEvent, new KeyEvent(key, mods, isCtrlDown, isAltDown, isShiftDown));
					if ( consumed )
						return;
				}
			}
		});
		
        GLFW.glfwSetMouseButtonCallback(context.getWindowHandle(), new GLFWMouseButtonCallback() {
    			Node lastPressed = null;
    			
			@Override
			public void invoke(long window, int button, int downup, int modifier) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(MouseButtonListener.class);
				
				for (int i = 0; i < listeners.size(); i++) {
					((MouseButtonListener) listeners.get(i)).invoke(window, button, downup, modifier);
				}
				
				/*
				 * Call scene node/etc listeners
				 */
				
				if ( downup == 1 ) { // Press
					if ( !context.hoveringOverPopup && context.getPopups().size() > 0 ) {
						context.closePopups();
						return;
					}
					
					Node hovered = context.getHovered();
					if ( hovered != null ) {
						hovered.onMousePressed(button);
					}
					lastPressed = hovered;
				} else { // Release
					
					Node hovered = context.getHovered();
					if ( hovered != null && hovered.mousePressed ) {
						boolean consumed = hovered.onMouseReleased(button);
						
						// If not consumed, set selected
						if ( button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !consumed) {
							context.setSelected(hovered);
						}
					}
					
					if ( lastPressed != null ) {
						lastPressed.mousePressed = false;
					}
				}
			}
        });
        
		GLFW.glfwSetWindowFocusCallback(context.getWindowHandle(), new GLFWWindowFocusCallbackI() {
			@Override
			public void invoke(long window, boolean focus) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(WindowFocusListener.class);
				
				for (int i = 0; i < listeners.size(); i++) {
					((WindowFocusListener) listeners.get(i)).invoke(window, focus);
				}
				
				/*
				 * Update context
				 */
				
				context.focused = focus;
				
			}
		});
		
		GLFW.glfwSetWindowCloseCallback(context.getWindowHandle(), new GLFWWindowCloseCallbackI() {
			@Override
			public void invoke(long window) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(WindowCloseListener.class);
				
				for (int i = 0; i < listeners.size(); i++) {
					((WindowCloseListener) listeners.get(i)).invoke(window);
				}
				
				/*
				 * Close the program
				 */
				
				GLFW.glfwSetWindowShouldClose(context.getWindowHandle(), canUserClose);
			}
		});
		
		GLFW.glfwSetWindowSizeCallback(context.getWindowHandle(), new GLFWWindowSizeCallbackI() {
			@Override
			public void invoke(long window, int width, int height) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(WindowSizeListener.class);
				
				for (int i = 0; i < listeners.size(); i++) {
					((WindowSizeListener) listeners.get(i)).invoke(window, width, height);
				}
				
				/*
				 * Update context
				 */
				
				windowResizing = true;
				render();
				windowResizing = false;
			}
		});
		
		GLFW.glfwSetWindowRefreshCallback(context.getWindowHandle(), new GLFWWindowRefreshCallbackI() {
			@Override
			public void invoke(long arg0) {
				render();
			}
		});
		
		GLFW.glfwSetScrollCallback(context.getWindowHandle(), new GLFWScrollCallbackI() {
			@Override
			public void invoke(long window, double dx, double dy) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(MouseWheelListener.class);
				
				for (int i = 0; i < listeners.size(); i++) {
					((MouseWheelListener) listeners.get(i)).invoke(window, dx, dy);
				}
				
				/*
				 * Call scene node listeners
				 */
				
				notifyScroll(scene, dx, dy);
			}

			private void notifyScroll(Node t, double x, double y) {
				ObservableList<Node> children = t.getChildren();
				for (int i = 0; i < children.size(); i++) {
					notifyScroll(children.get(i), x, y);
				}
				if ( t.mouseScrollEventInternal != null ) {
					EventHelper.fireEvent(t.mouseScrollEventInternal, new ScrollEvent(x,y));
				}
				if ( t.mouseScrollEvent != null ) {
					EventHelper.fireEvent(t.mouseScrollEvent, new ScrollEvent(x,y));
				}
			}
		});
		
		// Hack to get window to focus on windows. Often using Windows 10 a GLFW window will not have focus when it's first on the screen...
		GLFW.glfwHideWindow(context.getWindowHandle());
		GLFW.glfwShowWindow(context.getWindowHandle());
	}
	
	public Context getContext() {
		return context;
	}
	
	/**
	 * Default: true. When set to true the window will automatically call OpenGL's SwapBuffers method after drawing.
	 * @param autoDraw
	 */
	public void setWindowAutoDraw(boolean autoDraw) {
		this.autoDraw = autoDraw;
	}
	
	/**
	 * Default: true. When set to true, the window will automatically clear OpenGL's depth and color at the start of drawing.
	 * @param autoClear
	 */
	public void setWindowAutoClear(boolean autoClear) {
		this.autoClear = autoClear;
	}
	
	public boolean isWindowAutoClear() {
		return this.autoClear;
	}
	
	public void render() {
		if ( GLFW.glfwGetCurrentContext() != context.getWindowHandle() ) {
			System.err.println("Error rendering window. Incorrect GLFW context");
			return;
		}
		
		// Update context
		context.updateContext();
		int width = context.getWidth();
		int height = context.getHeight();
		int ratio = context.getPixelRatio();
		
		// Set correct sizes
		scene.setMinSize(width, height);
		scene.setPrefSize(width, height);
		scene.setMaxSize(width, height);

		// Begin rendering prepass
		context.refresh();
		if ( this.renderCallback != null ) {
			this.renderCallback.render(context);
		}
		
		// Do NVG frame
		context.refresh();
		NanoVG.nvgBeginFrame(context.getNVG(), (int)width, (int)height, ratio);
		scene.render(context);
		scene.render(context);
		scene.render(context);
		
        NanoVG.nvgRestore(context.getNVG());
		NanoVG.nvgEndFrame(context.getNVG());
		
		if ( autoDraw ) {
			GLFW.glfwSwapBuffers(context.getWindowHandle());
		}
	}

	public Scene getScene() {
		return this.scene;
	}

	public void setCanUserClose(boolean close) {
		this.canUserClose = close;
	}

	public void setRenderingCallback(Renderer callback) {
		this.renderCallback = callback;
	}

	public void addEventListener(EventListener listener) {
		String key = listener.getClass().getDeclaredAnnotations()[0].toString();
		
		if (eventListeners.containsKey(key)) {
			eventListeners.get(key).add(listener);
		} else {
			eventListeners.put(key, new ArrayList<EventListener>());
			addEventListener(listener);
		}
	}
	
	public boolean removeEventListener(EventListener listener) {
		String key = listener.getClass().getSuperclass().getName();
		
		if (eventListeners.containsKey(key)) {
			return eventListeners.get(key).remove(listener);
		} else {
			return false;
		}
	}
	
	public ArrayList<EventListener> getEventListenersForType(Class<?> type){
		String key = type.getClass().getSuperclass().getName();
		
		System.err.println("get " + key);
		
		if (eventListeners.containsKey(key)) {
			return eventListeners.get(key);
		} else {
			eventListeners.put(key, new ArrayList<EventListener>());
			return getEventListenersForType(type);
		}
	}
}
