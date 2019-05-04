package lwjgui;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL11;

import lwjgui.gl.Renderer;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.ContentDisplay;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.OpenGLPane;
import lwjgui.scene.layout.StackPane;

public class OpenGLPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		
		ModernOpenGL = false;
		/* Flag to make the internal window to use deprecated openGL */
		/* We're using deprecated openGL in this example to keep it short. */
		/* This is needed for Mac users. Not needed for windows/Linux users. */
		/* Mac doesn't let you mix old/new openGL code together */

		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple pane
		StackPane root = new StackPane();

		// Create an OpenGL pane (canvas)
		OpenGLPane ogl = new OpenGLPane();
		ogl.setPrefSize(24, 24);

		// Add a rendering callback to the opengl pane
		ogl.setRendererCallback(new RenderingCallbackTest());

		// Create label, set icon to opengl pane
		Label testLabel = new Label( "This label's icon is rendered with OpenGL!", ogl );
		testLabel.setContentDisplay(ContentDisplay.RIGHT);
		root.getChildren().add(testLabel);

		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();
	}

	static class RenderingCallbackTest implements Renderer {

		@Override
		public void render(Context context) {
			
			// Disable culling (just in case)
			GL11.glDisable(GL11.GL_CULL_FACE);

			// Render geometry
			glBegin(GL_TRIANGLES);
				glColor3f(0.0f, 0.0f, 1.0f);  /* blue */
				glVertex2f(-1f, -1.0f);
				
				glColor3f(0.0f, 1.0f, 0.0f);  /* green */
				glVertex2f(1.0f, -1.0f);
				
				glColor3f(1.0f, 0.0f, 0.0f);  /* red */
				glVertex2f(0.0f, 1.0f);
			glEnd();
		}
	}
}