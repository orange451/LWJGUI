package lwjgui.scene;

import static lwjgui.event.listener.EventListener.EventListenerType.CURSOR_POS_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.KEY_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.MOUSE_BUTTON_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.MOUSE_WHEEL_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.WINDOW_CLOSE_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.WINDOW_SIZE_LISTENER;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.stb.STBImage;

import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.event.EventHelper;
import lwjgui.event.KeyEvent;
import lwjgui.event.MouseEvent;
import lwjgui.event.ScrollEvent;
import lwjgui.event.TypeEvent;
import lwjgui.event.listener.CursorPositionListener;
import lwjgui.event.listener.EventListener;
import lwjgui.event.listener.EventListener.EventListenerType;
import lwjgui.event.listener.KeyListener;
import lwjgui.event.listener.MouseButtonListener;
import lwjgui.event.listener.MouseWheelListener;
import lwjgui.event.listener.WindowCloseListener;
import lwjgui.event.listener.WindowFocusListener;
import lwjgui.event.listener.WindowSizeListener;
import lwjgui.gl.Renderer;
import lwjgui.paint.Color;
import lwjgui.scene.control.PopupWindow;
import lwjgui.theme.Theme;

public class Window {
	private Context context;
	private Scene scene;
	private boolean canUserClose = true;
	protected boolean windowResizing;
	private Renderer renderCallback;
	private boolean autoDraw = true;
	private boolean autoClear = true;

	private int lastWidth;
	private int lastHeight;
	
	private HashMap<EventListenerType, ArrayList<EventListener>> eventListeners = new HashMap<>();
	
	public Window(final Context context, Scene scene) {
		this.context = context;
		this.scene = scene;
		
		GLFW.glfwSetCursorPosCallback(context.getWindowHandle(), new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double x, double y) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(CURSOR_POS_LISTENER);
				
				for (int i = 0; i < listeners.size(); i++) {
					((CursorPositionListener) listeners.get(i)).invoke(window, x, y);
				}
				
				/*
				 * Call scene node listeners
				 */
				Node selected = context.getSelected();
				if ( selected == null ) return;
				
				if (selected.mousePressed) {
					selected.mouseDragged = true;
					if (selected.mouseDraggedEvent != null) {
						EventHelper.fireEvent(selected.mouseDraggedEvent, new MouseEvent(x, y, GLFW.GLFW_MOUSE_BUTTON_LEFT));
					} 
					
					if (selected.mouseDraggedEventInternal != null) {
						EventHelper.fireEvent(selected.mouseDraggedEventInternal, new MouseEvent(x, y, GLFW.GLFW_MOUSE_BUTTON_LEFT));
					}
				}
			}
		});
		
		GLFW.glfwSetCharCallback(context.getWindowHandle(), new GLFWCharCallback() {

			@Override
			public void invoke(long window, int codepoint) {
			notifyTextInput(Window.this.scene, new TypeEvent(codepoint));
			
			ObservableList<PopupWindow> popups = Window.this.scene.getPopups();
			for (int i = 0; i < popups.size(); i++) {
				Node root = popups.get(i);
				notifyTextInput(root, new TypeEvent(codepoint));
			}
			}
			
			private void notifyTextInput(Node root, TypeEvent event) {
				boolean consumed = false;
				
				if ( root == null )
					return;

				if (root.textInputEventInternal != null && EventHelper.fireEvent(root.textInputEventInternal, event)) {
					consumed = true;
				}
				
				if (root.textInputEvent != null && EventHelper.fireEvent(root.textInputEvent, event)) {
					consumed = true;
				}
				
				if (consumed) return;
				
				ObservableList<Node> children = root.getChildren();
				for (int i = 0; i < children.size(); i++) {
					notifyTextInput(children.get(i), event);
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
				ArrayList<EventListener> listeners = getEventListenersForType(KEY_LISTENER);
				
				for (int i = 0; i < listeners.size(); i++) {
					((KeyListener) listeners.get(i)).invoke(handle, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				}

				/*
				 * Call scene node listeners
				 */

				notifyKeyInput(Window.this.scene, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				
				ObservableList<PopupWindow> popups = Window.this.scene.getPopups();
				for (int i = 0; i < popups.size(); i++) {
					Node root = popups.get(i);
					notifyKeyInput(root, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				}
			}
			
			private void notifyKeyInput(Node root, int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				if ( root == null )
					return;
				
				ObservableList<Node> children = root.getChildren();
				for (int i = 0; i < children.size(); i++) {
					notifyKeyInput(children.get(i), key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				}
				
				KeyEvent event = new KeyEvent(key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				
				/*
				 * Key pressed 
				 */
				
				if (event.action == GLFW.GLFW_PRESS) {
					if (root.keyPressedEventInternal != null && EventHelper.fireEvent(root.keyPressedEventInternal, event)) {
						return;
					}
						
					if (root.keyPressedEvent != null && EventHelper.fireEvent(root.keyPressedEvent, event)) {
						return;
					}
				}
				
				/*
				 * Key repeat (e.g. holding backspace to "spam" it)
				 */
				
				if (event.action == GLFW.GLFW_REPEAT) {
					if (root.keyRepeatEventInternal != null && EventHelper.fireEvent(root.keyRepeatEventInternal, event)) {
						return;
					}
						
					if (root.keyRepeatEvent != null && EventHelper.fireEvent(root.keyRepeatEvent, event)) {
						return;
					}
				}
				
				/*
				 * Key released 
				 */
				
				if (event.action == GLFW.GLFW_RELEASE) {
					if (root.keyReleasedEventInternal != null && EventHelper.fireEvent(root.keyReleasedEventInternal, event)) {
						return;
					}
					
					if (root.keyReleasedEvent != null && EventHelper.fireEvent(root.keyReleasedEvent, event)) {
						return;
					}
				}
			}
		});
		
        GLFW.glfwSetMouseButtonCallback(context.getWindowHandle(), new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int downup, int modifier) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(MOUSE_BUTTON_LISTENER);
				
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
						hovered.onMousePressed(context.getMouseX(), context.getMouseY(), button);
					}
				} else { // Release
					
					Node hovered = context.getHovered();
					if ( hovered != null && hovered.mousePressed ) {
						boolean consumed = hovered.onMouseReleased(context.getMouseX(), context.getMouseY(), button);

						// If not consumed, set selected
						if ( button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !consumed) {
							context.setSelected(hovered);
						}
						
						double x = context.getMouseX();
						double y = context.getMouseY();
						if ( hovered.mouseDragged )
							EventHelper.fireEvent(hovered.getMouseDraggedEndEvent(), new MouseEvent(x, y, GLFW.GLFW_MOUSE_BUTTON_LEFT));
						if ( hovered.mouseDragged )
							EventHelper.fireEvent(hovered.getMouseDraggedEndEventInternal(), new MouseEvent(x, y, GLFW.GLFW_MOUSE_BUTTON_LEFT));
						hovered.mouseDragged = false;
					}
					
					// If we released on a different node than the one we clicked on...
					if ( context.getLastPressed() != null && context.getLastPressed() != hovered ) {
						context.getLastPressed().mousePressed = false;
						
						double x = context.getMouseX();
						double y = context.getMouseY();
						
						if ( context.getLastPressed().mouseDragged ) {
							EventHelper.fireEvent(context.getLastPressed().getMouseDraggedEndEvent(), new MouseEvent(x, y, GLFW.GLFW_MOUSE_BUTTON_LEFT));
						}
						
						if ( context.getLastPressed().mouseDragged ) {
							EventHelper.fireEvent(context.getLastPressed().getMouseDraggedEndEventInternal(), new MouseEvent(x, y, GLFW.GLFW_MOUSE_BUTTON_LEFT));
						}
						
						context.getLastPressed().mouseDragged = false;
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
				ArrayList<EventListener> listeners = getEventListenersForType(MOUSE_WHEEL_LISTENER);
				
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
				ArrayList<EventListener> listeners = getEventListenersForType(WINDOW_CLOSE_LISTENER);
				
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
				ArrayList<EventListener> listeners = getEventListenersForType(WINDOW_SIZE_LISTENER);
				
				for (int i = 0; i < listeners.size(); i++) {
					((WindowSizeListener) listeners.get(i)).invoke(window, width, height);
				}
				
				/*
				 * Update context
				 */
				
				windowResizing = true;
				lastWidth = width;
				lastHeight = height;
				
				// Double buffer the rendering
				for (int i = 1; i < 2; i++) 
					render();
				
				windowResizing = false;
			}
		});
		
		GLFW.glfwSetScrollCallback(context.getWindowHandle(), new GLFWScrollCallbackI() {
			@Override
			public void invoke(long window, double dx, double dy) {
				/*
				 * Call window event listeners
				 */
				ArrayList<EventListener> listeners = getEventListenersForType(MOUSE_WHEEL_LISTENER);
				
				for (int i = 0; i < listeners.size(); i++) {
					((MouseWheelListener) listeners.get(i)).invoke(window, dx, dy);
				}
				
				/*
				 * Call scene node listeners
				 */
				
				notifyScroll(Window.this.scene, dx, dy);
			}

			private void notifyScroll(Node t, double x, double y) {
				if ( t == null )
					return;
				
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
	}
	
	/**
	 * Return the current context object for this window. The context object stores more complex information
	 * about the window and how its drawn.
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * Signal to the OS that the window should be focused.
	 */
	public void focus() {
		GLFW.glfwFocusWindow(context.getWindowHandle());
	}
	
	/**
	 * Orange451's hack for forcing the window to focus in scenarios where GLFW's built-in function doesn't work.
	 */
	public void focusHack() {
		// Hack to get window to focus on windows. Often using Windows 10 a GLFW window will not have focus when it's first on the screen...
		GLFW.glfwHideWindow(context.getWindowHandle());
		GLFW.glfwShowWindow(context.getWindowHandle());
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
		LWJGUI.setCurrentContext(getContext());
		
		// Clear screen
		if ( isWindowAutoClear() ) {
			Color c = Theme.current().getBackground();
			glClearColor(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f,1);
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
		}
		
		// Update context
		context.updateContext();
		int width = context.getWidth();
		int height = context.getHeight();
		int ratio = context.getPixelRatio();
		
		// Use resize height if resizing
		if ( this.windowResizing ) {
			context.setContextSize( lastWidth, lastHeight );
			width = lastWidth;
			height = lastHeight;
		}
		
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
		
        NanoVG.nvgRestore(context.getNVG());
		NanoVG.nvgEndFrame(context.getNVG());
		
		if ( autoDraw ) {
			GLFW.glfwSwapBuffers(context.getWindowHandle());
		}
	}
	
	/**
	 * Attempts to show this Window by setting visibility to true
	 */
	public void show() {
		glfwShowWindow(context.getWindowHandle());
		focus();
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
		
		try {
			final int s = 1024*1024;
			// Set scene size
			scene.setMinSize(0, 0);
			scene.setMaxSize(s, s);
			scene.size.set(s, s);
			
			// Buffer it a few times
			for (int i = 0; i < 8; i++) {
				LWJGUI.setCurrentContext(getContext());
				scene.size.set(s, s);
				scene.position(null);
			}
			
			double maxWid = scene.getMaxElementWidth();
			double maxHei = scene.getMaxElementHeight();
			
			// Uh oh, we're too big! AHHHHH
			if ( maxWid >= s || maxHei >= s ) {
				maxWid = Math.max(100, scene.getPrefWidth());
				maxHei = Math.max(100, scene.getPrefHeight());
			}
			
			// Set window to match scene's size
			double sw = Math.max(maxWid, scene.getPrefWidth());
			double sh = Math.max(maxHei, scene.getPrefHeight());
			
			// Size window
			GLFW.glfwSetWindowSize(context.getWindowHandle(), (int)Math.ceil(sw), (int)Math.ceil(sh));
			context.updateContext();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current scene object used by this window.
	 * @return
	 */
	public Scene getScene() {
		return this.scene;
	}
	
	/**
	 * Sets the title of the window.
	 * @param title
	 */
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(context.getWindowHandle(), title);
	}

	/**
	 * Sets an internal flag which controls whether or not a window can be closed by the user.
	 * @param close
	 */
	public void setCanUserClose(boolean close) {
		this.canUserClose = close;
	}

	/**
	 * Sets the rendering callback for this window. By default there is no rendering callback.<br>
	 * A rendering callback runs directly before the window renders its UI.
	 * @param callback
	 */
	public void setRenderingCallback(Renderer callback) {
		this.renderCallback = callback;
	}

	/**
	 * Add the given EventListener to the Window.
	 * 
	 * @param listener
	 */
	public void addEventListener(EventListener listener) {
		EventListenerType key = listener.getEventListenerType();
		
		if (eventListeners.containsKey(key)) {
			eventListeners.get(key).add(listener);
		} else {
			eventListeners.put(key, new ArrayList<EventListener>());
			addEventListener(listener);
		}
	}
	
	/**
	 * Remove the given EventListener.
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeEventListener(EventListener listener) {
		EventListenerType key = listener.getEventListenerType();
		
		if (eventListeners.containsKey(key)) {
			return eventListeners.get(key).remove(listener);
		} else {
			return false;
		}
	}
	
	/**
	 * Remove all EventListeners of the given type.
	 * @param type
	 */
	public void removeAll(EventListenerType type) {
		eventListeners.put(type, new ArrayList<EventListener>());
	}
	
	/**
	 * Get all EventListeners of the given type.
	 * 
	 * @param type
	 * @return
	 */
	public ArrayList<EventListener> getEventListenersForType(EventListenerType type){
		if (eventListeners.containsKey(type)) {
			return eventListeners.get(type);
		} else {
			eventListeners.put(type, new ArrayList<EventListener>());
			return getEventListenersForType(type);
		}
	}
	
	/**
	 * Adds the following files as icons of varying sizes for the window.
	 * 
	 * @param iconFiles - the array of files to check/load
	 */
	public void setIcon(File[] iconFiles) {
		setIcon(null, iconFiles);
	}
	
	/**
	 * Adds the following files as icons of varying sizes for the window.
	 * 
	 * @param filetype - only uses the Files that end with this file extension. Set to null to just use any file.
	 * @param iconFiles - the array of files to check/load
	 */
	public void setIcon(String filetype, File[] files) {
		
		/*
		 * Check the listed files for images that can be used as icons
		 */
		
		Stack<File> validFiles = new Stack<File>();
		
		for(int i = 0; i < files.length; i++) {
			File f = files[i];
			
			if (filetype != null && f.getName().endsWith(filetype)) {
				validFiles.push(f);
			}
		}
		
		/*
		 * Compile an array of valid icons
		 */
		
		File[] iconFiles = new File[validFiles.size()];
		
		for (int i = 0; i < iconFiles.length; i++) {
			iconFiles[i] = validFiles.pop();
		}
		
		/*
		 * Set the icons
		 */
		
		int numIcons = iconFiles.length;
		
		GLFWImage.Buffer icons = GLFWImage.malloc(numIcons);
		
		int[] w = new int[1];
		int[] h = new int[1];
		int[] c = new int[1];
		
		ByteBuffer[] datas = new ByteBuffer[numIcons];
		
		for(int i = 0; i < numIcons; i++) {
			ByteBuffer data = datas[i] = STBImage.stbi_load(iconFiles[i].getAbsolutePath(), w, h, c, 4);
			icons.get(i).set(w[0], h[0], data);
		}
		
		glfwSetWindowIcon(context.getWindowHandle(), icons);
		
		icons.free();
		
		for(int i = 0; i < numIcons; i++) {
			STBImage.stbi_image_free(datas[i]);
		}
	}

	public void setResizible(boolean resizable) {
		GLFW.glfwSetWindowAttrib(getContext().getWindowHandle(), GLFW.GLFW_RESIZABLE, resizable?GLFW.GLFW_TRUE:GLFW.GLFW_FALSE);
	}
}
