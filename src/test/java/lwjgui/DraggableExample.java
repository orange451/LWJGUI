package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.scene.layout.StackPane;

public class DraggableExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		//Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Draggable Example", WIDTH, HEIGHT, true, false);
		
		//Initialize lwjgui for this window
		Window newWindow = LWJGUI.initialize(window);
		Scene scene = newWindow.getScene();
		
		//Add some components
		addComponents(scene);
		
		//Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}
		
		//Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		//Create a simple pane
		StackPane root = new StackPane();
		
		//Set the pane as the scenes root
		scene.setRoot(root);
		
		/*
		 * Create a DraggablePane
		 */
		
		DraggablePane dragPane1 = new DraggablePane();
		dragPane1.setBackground(Color.GREEN);
		dragPane1.setPrefHeight(64);
		
		//Put pane in center of screen
		dragPane1.setAbsolutePosition(WIDTH/2, HEIGHT/2);
		
		//Add text
		Label label = new Label("I'm draggable!");
		label.setMouseTransparent(true);
		dragPane1.getChildren().add(label);
		
		//Test that it is sticky!
		dragPane1.setAbsolutePosition(0, 0);
		
		//Add it to root
		root.getChildren().add(dragPane1);
		
		/*
		 * Create another DraggablePane
		 */
		
		DraggablePane dragPane2 = new DraggablePane();
		dragPane2.setBackground(Color.VIOLET);
		dragPane2.setPrefHeight(64);
		
		//Put pane in center of screen
		dragPane2.setAbsolutePosition(WIDTH/2, HEIGHT/2);
		
		//Add text
		Label label2 = new Label("I'm draggable too!");
		label2.setMouseTransparent(true);
		dragPane2.getChildren().add(label2);
		
		//Test that it is sticky!
		dragPane2.setAbsolutePosition(50, 50);
		
		//Add it to root
		root.getChildren().add(dragPane2);
	}
}