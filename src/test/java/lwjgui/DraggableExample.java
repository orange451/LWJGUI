package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.layout.StackPane;

public class DraggableExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Draggable Example", WIDTH, HEIGHT, true, false);
		
		// Initialize lwjgui for this window
		Window newWindow = LWJGUI.initialize(window);
		Scene scene = newWindow.getScene();
		
		// Add some components
		addComponents(scene);
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}
		
		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create a simple pane
		StackPane pane = new StackPane();
		
		// Set the pane as the scenes root
		scene.setRoot(pane);
		
		// Create a new draggable pane
		FloatingPane floatPane = new DraggablePane();
		floatPane.setBackground(Color.GREEN);
		floatPane.setPrefHeight(64);
		pane.getChildren().add(floatPane);
		
		// Put pane in center of screen
		floatPane.setAbsolutePosition(WIDTH/2, HEIGHT/2);
		
		// Add text
		floatPane.getChildren().add(new Label("I'm draggable!"));
		
		// Test that it is sticky!
		floatPane.setAbsolutePosition(0, 0);
	}
	
	static class StickyPane extends FloatingPane {
		@Override
		public void setAbsolutePosition( double x, double y ) {
			double deltaX = x - this.getX();
			double deltaY = y - this.getY();
			
			super.setAbsolutePosition(x, y);
			
			for (int i = 0; i < children.size(); i++) {
				children.get(i).offset(deltaX, deltaY);
			}
		}
	}
	
	static class DraggablePane extends StickyPane {
		private boolean dragging;
		private boolean failedClick;
		private Vector2d dragOffset;
		
		public DraggablePane() {
			this.dragOffset = new Vector2d();
		}
		
		@Override
		public void position(Node parent) {
			super.position(parent);
			
			int mouse = GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
			double mouseX = this.cached_context.getMouseX();
			double mouseY = this.cached_context.getMouseY();
			
			if ( mouse == GLFW.GLFW_PRESS ) {
				if ( !dragging && !failedClick ) {
					if ( this.cached_context.isMouseInside(this) ) {
						double diffx = mouseX - this.getX();
						double diffy = mouseY - this.getY();
						
						dragOffset.set(diffx,diffy);
						dragging = true;
					} else {
						failedClick = true;
					}
				}
			} else {
				dragging = false;
				failedClick = false;
			}
			
			if ( dragging ) {
				this.setAbsolutePosition(mouseX-dragOffset.x, mouseY-dragOffset.y);
			}
		}
	}
}