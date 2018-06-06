package test;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.SplitPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class SplitPaneExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("LWJGUI Window", WIDTH, HEIGHT);
		
		// Initialize lwjgui for this window
		Scene scene = LWJGUI.initialize(window);
		
		// Add some components
		addComponents(scene);
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Clear screen
			glClearColor(0,0,0,0);
			glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
			
			// poll events to callbacks
			glfwPollEvents();
			
			// Render GUI
			LWJGUI.render();

			// Draw
			glfwSwapBuffers(window);
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create background pane
		StackPane pane = new StackPane();
		scene.setRoot(pane);
		
		// Create vertical layout
		VBox box = new VBox();
		box.setFillToParentHeight(true);
		box.setFillToParentWidth(true);
		pane.getChildren().add(box);
		
		// Title label
		Label b = new Label("Split Pane Test");
		b.setFontSize(32);
		box.getChildren().add(b);
		
		// Create split pane
		SplitPane split = new SplitPane();
		split.setFillToParentHeight(true);
		split.setFillToParentWidth(true);
		split.setOrientation(Orientation.VERTICAL);
		box.getChildren().add(split);
		
		// Add some content
		for (int i = 0; i < 3; i++) {
			StackPane p = new StackPane();
			p.setAlignment(Pos.CENTER);
			split.getItems().add(p);
			
			p.getChildren().add(new Button("Hello World"));
		}
	}
}