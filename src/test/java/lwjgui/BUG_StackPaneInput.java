package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

/**
 * This class showcases some input issues that I assume are related to stacking multiple panes on top of each other. To see the bug, turn the program on and try hovering over the 
 * label. Correct behavior is showcases when the proper printouts appear in the console.
 * 
 * You can see a version of this setup that appears to work fine in HelloWorld.java. When you add a second StackPane into the mix and also start adjusting the alignments and padding,
 * suddenly the inputs stop working as intended.
 * 
 * I've included two methods here: one where everything works, and one where everything breaks when you apply minor edits.
 *
 * Delete this class once the problem is fixed.
 *
 */
public class BUG_StackPaneInput {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		long window = LWJGUIUtil.createOpenGLCoreWindow("Hello World", WIDTH, HEIGHT, true, false);
		
		Window newWindow = LWJGUI.initialize(window);
		Scene scene = newWindow.getScene();
		
		addComponents_Triggers_Bug(scene);
		
		//Comment out the above and uncomment below to switch the example
		//addComponents_Works_As_Intended(scene);
		
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}
		
		glfwTerminate();
	}

	private static void addComponents_Triggers_Bug(Scene scene) {
		/*
		 * StackPane 1
		 */
		StackPane pane1 = new StackPane();
		pane1.setAlignment(Pos.BOTTOM_LEFT);
		scene.setRoot(pane1);
		
		/*
		 * Label 1
		 */
		Label label = new Label("Hello World!");
		label.setBackground(Color.ORANGE);
		
		label.setOnMouseEntered(e -> {
			System.out.println("Mouse entered Label 1.");
		});
		
		label.setOnMouseExited(e -> {
			System.out.println("Mouse exited Label 1.");
		});
		//label.setBackground(Color.BLACK);
		
		/*
		 * Label 2
		 */
		Label label2 = new Label("Label 2");
		label2.setBackground(Color.ORANGE);
		
		label2.setOnMouseEntered(e -> {
			System.out.println("Mouse entered Label 2.");
		});
		
		label2.setOnMouseExited(e -> {
			System.out.println("Mouse exited Label 2.");
		});
		
		label2.setPadding(new Insets(0, 0, 0, 100));
		//label2.setBackground(Color.BLACK);
		
		/*
		 * StackPane 2
		 */
		
		StackPane pane2 = new StackPane();
		pane2.setAlignment(Pos.BOTTOM_LEFT);
		pane2.setPadding(new Insets(0, 0, 20, 20));
		pane2.getChildren().addAll(label, label2);
		
		pane1.getChildren().add(pane2);
	}
	
	@SuppressWarnings("unused")
	private static void addComponents_Works_As_Intended(Scene scene) {
		StackPane pane = new StackPane();
		pane.setAlignment(Pos.BOTTOM_LEFT);
		pane.setPadding(new Insets(0, 0, 20, 20));
		
		scene.setRoot(pane);
		
		Label label = new Label("Hello World!");
		
		label.setOnMouseEntered(e -> {
			System.out.println("Mouse entered Hello World label.");
		});
		
		label.setOnMouseExited(e -> {
			System.out.println("Mouse exited Hello World label.");
		});
		
		StackPane pane2 = new StackPane();
		pane2.setAlignment(Pos.CENTER);
		pane2.getChildren().add(label);
		
		pane.getChildren().add(pane2);
	}
}