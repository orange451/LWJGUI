	package lwjgui;


import java.util.concurrent.atomic.AtomicBoolean;

import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;
import lwjgui.transition.SizeTransition;

public class AnimationExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create Root pane
		StackPane pane = new StackPane();
		
		// Create animation pane
		StackPane test = new StackPane();
		test.setBackgroundLegacy(Color.ORANGE);
		pane.getChildren().add(test);
		
		// Animation logic
		AtomicBoolean inAnimation = new AtomicBoolean(false);
		test.setOnMouseClicked((event)->{
			if ( inAnimation.get() )
				return;
			
			if ( event.getClickCount() == 2 ) {
				if ( test.isFillToParentWidth() ) {
					SizeTransition transition = new SizeTransition(100, 0, 0) {
						@Override
						protected double getCurrentWidth() {
							return pane.getWidth();
						}
	
						@Override
						protected double getCurrentHeight() {
							return pane.getHeight();
						}
	
						@Override
						protected void setWidth(double width) {
							test.setPrefWidth(width);
						}
	
						@Override
						protected void setHeight(double height) {
							test.setPrefHeight(height);
						}
						
						@Override
						public void completedCallback() {
							inAnimation.set(false);
						}
					};
					
					inAnimation.set(true);
					test.setFillToParentHeight(false);
					test.setFillToParentWidth(false);
					transition.play();
				} else {
					SizeTransition transition = new SizeTransition(100, pane.getWidth(), pane.getHeight()) {
						@Override
						protected double getCurrentWidth() {
							return 0;
						}
	
						@Override
						protected double getCurrentHeight() {
							return 0;
						}
	
						@Override
						protected void setWidth(double width) {
							test.setPrefWidth(width);
						}
	
						@Override
						protected void setHeight(double height) {
							test.setPrefHeight(height);
						}
						
						@Override
						public void completedCallback() {
							test.setFillToParentHeight(true);
							test.setFillToParentWidth(true);
							inAnimation.set(false);
						}
					};

					inAnimation.set(true);
					transition.play();
				}
			}
		});
		
		// Put a label in the pane
		Label label = new Label("Double-click me!");
		label.setMouseTransparent(true);
		test.getChildren().add(label);
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}