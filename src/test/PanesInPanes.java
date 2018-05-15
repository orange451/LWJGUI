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
import org.lwjgl.opengl.GL11;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.Scene;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.TestNode;

public class PanesInPanes {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window.
		long window = LWJGUIUtil.createOpenGLCoreWindow("LWJGUI Window", WIDTH, HEIGHT);
		
		// Initialize lwjgui for this window
		Scene scene = LWJGUI.initialize(window);
		
		// Add some components
		addComponents(scene);
		
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Clear screen
			glClearColor(0,0,0,0);
			glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
			
			// Render GUI elements
			LWJGUI.render();

			// poll events to callbacks
			glfwPollEvents();
			glfwSwapBuffers(window);
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		StackPane background = new StackPane();
		background.setPadding(new Insets(4,4,4,4));
		scene.getChildren().add(background);
		
		StackPane pane = new StackPane();
		pane.setFillToParentWidth(false);
		pane.setPrefWidth(150);
		pane.setMinWidth(100);
		pane.setPadding(new Insets(4,4,4,4));
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setBackground(Color.LIGHT_GRAY);
		background.getChildren().add(pane);
		
		StackPane pane2 = new StackPane();
		pane2.setFillToParentHeight(false);
		pane2.setFillToParentWidth(false);
		pane2.setAlignment(Pos.BOTTOM_RIGHT);
		pane2.setPrefSize(200, 100);
		pane2.setPadding(new Insets(4,4,4,4));
		pane2.setBackground(Color.DARK_GRAY);
		pane.getChildren().add(pane2);
		
		Label label = new Label("Hello World!");
		label.setTextFill(Color.WHITE_SMOKE);
		pane2.getChildren().add(label);
	}
}