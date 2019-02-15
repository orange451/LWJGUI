package lwjgui;
 
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
 
import java.io.IOException;
 
import org.lwjgl.glfw.GLFW;
 
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.StackPane;
 
public class BorderPaneExample2 {
    public static final int WIDTH   = 320;
    public static final int HEIGHT  = 240;
 
    public static void main(String[] args) throws IOException {
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Create a standard opengl 3.2 window. You can do this yourself.
        long window = LWJGUIUtil.createOpenGLCoreWindow("Border Pane Example", WIDTH, HEIGHT, true, false);
       
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
        BorderPane pane = new BorderPane();
       
        // Set the pane as the scenes root
        scene.setRoot(pane);
       
        // Create footer panel
        StackPane footer = new StackPane();
        footer.setBackground(Color.GREEN);
        footer.setPrefHeight(18);
        footer.setFillToParentWidth(true);
        pane.setBottom(footer);
       
        // Create border pane (gives us left/right alignment easily)
        BorderPane t = new BorderPane();
        t.setBackground(null);
        footer.getChildren().add(t);
 
        // Add left/right labels
        t.setLeft(new Label("Bottom Left"));
        t.setRight(new Label("Bottom Right"));
    }
}