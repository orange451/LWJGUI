package lwjgui;


import java.io.IOException;

import lwjgui.LWJGUI;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.scene.layout.floating.PannablePane;
import lwjgui.scene.shape.Rectangle;

public class PannableExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Root pane
		StackPane root = new StackPane();
		root.setAlignment(Pos.TOP_CENTER);
		
		// Create a pannable pane in the root
		PannablePane pane = new PannablePane();
		root.getChildren().add(pane);
		
		// Create a draggable pane
		DraggablePane floatPane = new DraggablePane();
		floatPane.setBackgroundLegacy(Color.GREEN);
		floatPane.setPrefSize(64, 64);
		Label label = new Label("I'm draggable!");
		label.setMouseTransparent(true);
		floatPane.getChildren().add(label);
		pane.getChildren().add(floatPane);
		
		// Center the floating pane
		LWJGUI.runLater(()->{
			floatPane.offset(-floatPane.getWidth()/2, -floatPane.getHeight()/2);
		});

		// Create a second draggable pane
		DraggablePane d = new DraggablePane();
		d.setAbsolutePosition(32, 48);
		d.setBackgroundLegacy(Color.RED);
		d.setPrefSize(64, 64);
		Label label2 = new Label("Me too!");
		label2.setMouseTransparent(true);
		d.getChildren().add(label2);
		pane.getChildren().add(d);
		
		// Overlapping UI
		{
			HBox ui = new HBox();
			ui.setBackgroundLegacy(null);
			ui.setPadding(new Insets(8, 0, 0, 0));
			root.getChildren().add(ui);
			
			Button b = new Button("Center");
			ui.getChildren().add(b);
			
			b.setOnAction((event)->{
				pane.center();
			});
		}
		
		// This crosshair represents the center
		pane.getChildren().add(new Rectangle(2, 2));
		pane.getChildren().add(new Rectangle(1, 10));
		pane.getChildren().add(new Rectangle(10, 1));
		
		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}