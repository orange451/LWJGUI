package lwjgui.scene;

import org.lwjgl.glfw.GLFW;
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
		
        GLFW.glfwSetMouseButtonCallback(context.getWindowHandle(), new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int downup, int modifier) {
				if ( downup == 1 ) {
					if ( !context.hoveringOverPopup && context.getPopups().size() > 0 ) {
						context.closePopups();
						return;
					}
					
					Node hovered = context.getHovered();
					if ( hovered != null ) {
						hovered.onMousePressed(button);
					}
				} else {
					
					Node hovered = context.getHovered();
					if ( hovered != null ) {
						hovered.onMouseReleased(button);
					}
					
					if ( button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
						context.setSelected(hovered);
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
				if ( t.mouseScrollEventInternal != null )
					t.mouseScrollEventInternal.onEvent(x,y);
				if ( t.mouseScrollEvent != null )
					t.mouseScrollEvent.onEvent(x,y);
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
