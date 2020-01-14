package lwjgui;

import java.io.IOException;

import lwjgui.geometry.Insets;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.PasswordField;
import lwjgui.scene.control.TextArea;
import lwjgui.scene.control.TextField;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class TextAreaExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		StackPane root = new StackPane();
		root.setBorder(new Insets(6));
		
		VBox vBox = new VBox();
		vBox.setSpacing(8);
		root.getChildren().add(vBox);
		
		HBox hbox = new HBox();
		hbox.setSpacing(8);
		vBox.getChildren().add(hbox);
		
		// Create an Input Field
		TextField f = new TextField();
		f.setPrompt("Text Field");
		hbox.getChildren().add(f);
		
		// Password field
		PasswordField p = new PasswordField();
		p.setPrompt("Password Field");
		hbox.getChildren().add(p);
		
		// Create a Text Area
		TextArea t = new TextArea();
		t.setFillToParentWidth(true);
		vBox.getChildren().add(t);
		
		// Clear text button
		Button b = new Button("Clear Text");
		b.setFillToParentWidth(true);
		b.setPrefHeight(32);
		b.setOnAction((event)->{
			t.clear();
		});
		vBox.getChildren().add(b);
		
		// Set the scene
		window.setScene(new Scene(root));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}