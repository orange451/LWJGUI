package lwjgui;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

import lwjgui.scene.Node;
import lwjgui.scene.Scene;

public class LWJGUIWindow {
	private Context context;
	private Scene scene;
	
	private LWJGUIWindow(final Context context, Scene scene) {
		this.context = context;
		this.scene = scene;
		
        GLFW.glfwSetMouseButtonCallback(context.getWindowHandle(), new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int downup, int modifier) {
				if ( downup == 1 ) {
					Node hovered = context.getHovered();
					if ( hovered != null ) {
						hovered.onMousePressed(button);
					}
				} else {
					Node hovered = context.getHovered();
					if ( hovered != null ) {
						hovered.onMouseReleased(button);
					}
					context.setSelected(hovered);
				}
			}
        });
        
		GLFW.glfwSetWindowFocusCallback(context.getWindowHandle(), new GLFWWindowFocusCallbackI() {
			@Override
			public void invoke(long window, boolean focus) {
				context.focused = focus;
			}
		});
	}
	
	protected static LWJGUIWindow newWindow(Context context, Scene scene) {
		return new LWJGUIWindow(context, scene);
	}
	
	public Context getContext() {
		return context;
	}
	
	protected void render() {
		context.updateContext();
		int width = context.getWidth();
		int height = context.getHeight();
		int ratio = context.getPixelRatio();
		
		GL11.glViewport(0, 0, (int)(width*ratio), (int)(height*ratio));
		NanoVG.nvgBeginFrame(context.getNVG(), (int)width, (int)height, ratio);
		
		scene.setMinSize(width, height);
		scene.setPrefSize(width, height);
		scene.setMaxSize(width, height);
		scene.render(context);
		
        NanoVG.nvgRestore(context.getNVG());
		NanoVG.nvgEndFrame(context.getNVG());
	}

	public Scene getScene() {
		return this.scene;
	}
}
