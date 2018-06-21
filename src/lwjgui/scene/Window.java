package lwjgui.scene;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.collections.ObservableList;
import lwjgui.gl.Renderer;

public class Window {
	private Context context;
	private Scene scene;
	private boolean canUserClose = true;
	protected boolean windowResizing;
	private Renderer renderCallback;
	
	public Window(final Context context, Scene scene) {
		this.context = context;
		this.scene = scene;
		
		GLFW.glfwSetCursorPosCallback(context.getWindowHandle(), new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double x, double y) {
				Node selected = context.getSelected();
				if ( selected == null )
					return;
				
				if ( selected.mousePressed && selected.mouseDraggedEvent != null ) {
					selected.mouseDraggedEvent.setConsumed(false);
					selected.mouseDraggedEvent.onEvent(-1);
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
					root.textInputEvent.setConsumed(false);
					root.textInputEvent.onEvent(key, mods, isCtrlDown, isAltDown, isShiftDown);
					if ( root.textInputEvent.isConsumed() )
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
				notifyKeyInput( scene, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown );
			}

			private void notifyKeyInput(Node root, int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				ObservableList<Node> children = root.getChildren();
				for (int i = 0; i < children.size(); i++) {
					notifyKeyInput(children.get(i), key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
				}
				
				if ( action == GLFW.GLFW_PRESS && root.keyPressedEvent != null ) {
					root.keyPressedEvent.setConsumed(false);
					root.keyPressedEvent.onEvent(key, mods, isCtrlDown, isAltDown, isShiftDown);
					if ( root.keyPressedEvent.isConsumed() )
						return;
				}
			}
		});
		
        GLFW.glfwSetMouseButtonCallback(context.getWindowHandle(), new GLFWMouseButtonCallback() {
    			Node lastPressed = null;
    			
			@Override
			public void invoke(long window, int button, int downup, int modifier) {
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
						hovered.onMouseReleased(button);
						if ( button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
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
				context.focused = focus;
			}
		});
		
		GLFW.glfwSetWindowCloseCallback(context.getWindowHandle(), new GLFWWindowCloseCallbackI() {
			@Override
			public void invoke(long arg0) {
				GLFW.glfwSetWindowShouldClose(context.getWindowHandle(), canUserClose);
			}
		});
		
		GLFW.glfwSetWindowSizeCallback(context.getWindowHandle(), new GLFWWindowSizeCallbackI() {
			@Override
			public void invoke(long handle, int wid, int hei) {
				windowResizing = true;
				render();
				windowResizing = false;
				GLFW.glfwSwapBuffers(context.getWindowHandle());
			}
		});
		
		GLFW.glfwSetScrollCallback(context.getWindowHandle(), new GLFWScrollCallbackI() {
			@Override
			public void invoke(long handle, double x, double y) {
				notifyScroll(scene, x, y);
			}

			private void notifyScroll(Node t, double x, double y) {
				ObservableList<Node> children = t.getChildren();
				for (int i = 0; i < children.size(); i++) {
					notifyScroll(children.get(i), x, y);
				}
				if ( t.mouseScrollEventInternal != null ) {
					t.mouseScrollEventInternal.setConsumed(false);
					t.mouseScrollEventInternal.onEvent(x,y);
				}
				if ( t.mouseScrollEvent != null ) {
					t.mouseScrollEvent.setConsumed(false);
					t.mouseScrollEvent.onEvent(x,y);
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
        NanoVG.nvgRestore(context.getNVG());
		NanoVG.nvgEndFrame(context.getNVG());
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
}
