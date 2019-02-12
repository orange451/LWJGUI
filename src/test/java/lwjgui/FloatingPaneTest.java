package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.FloatingPane;
import lwjgui.scene.layout.StackPane;

public class FloatingPaneTest {
   public static final int WIDTH   = 320;
   public static final int HEIGHT  = 240;

   public static void main(String[] args) throws IOException {
       if ( !glfwInit() )
           throw new IllegalStateException("Unable to initialize GLFW");

       // Create a standard opengl 3.2 window. You can do this yourself.
       long window = LWJGUIUtil.createOpenGLCoreWindow("Hello World", WIDTH, HEIGHT, true, false);
      
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
      
       // Create a new floating pane
       FloatingPane floatPane = new FloatingPane();
       pane.getChildren().add(floatPane);
      
       // Put pane in center of screen
       floatPane.setAlignment(Pos.CENTER);
       floatPane.setAbsolutePosition(WIDTH/2, HEIGHT/2);
      
       // Put a label in the floating pane
       floatPane.getChildren().add(new Label("Hello World!"));
   }
}