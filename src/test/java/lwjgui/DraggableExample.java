package lwjgui;

import java.io.IOException;

import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.style.BoxShadow;
import lwjgui.scene.layout.StackPane;

public class DraggableExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		//Create a simple pane
		StackPane root = new StackPane();
		
		/*
		 * Create a DraggablePane
		 */
		
		DraggablePane dragPane1 = new DraggablePane();
		dragPane1.setBackgroundLegacy(Color.GREEN);
		dragPane1.setPrefHeight(64);
		dragPane1.getBoxShadowList().add(new BoxShadow(3, 3, 6, Color.LIGHT_GRAY));
		
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
		dragPane2.setBackgroundLegacy(Color.VIOLET);
		dragPane2.setPrefHeight(64);
		dragPane2.getBoxShadowList().add(new BoxShadow(4, 4, 12, Color.GRAY));
		
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
		
		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}