package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.FlowPane;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.transition.FillTransition;
import lwjgui.transition.Transition;

public class BoxShadowAnimation extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		// Setup flow pane to hold buttons
		FlowPane flow = new FlowPane();
		flow.setAlignment(Pos.CENTER);
		flow.setFillToParentWidth(true);
		flow.setFillToParentHeight(true);
		flow.setOrientation(Orientation.HORIZONTAL);
		flow.setHgap(8);
		flow.setVgap(8);
		pane.getChildren().add(flow);
		
		// Create buttons
		flow.getItems().add(new BootStrapButton(new Color("#007bff"), "Click Me"));
		flow.getItems().add(new BootStrapButton(new Color("#6c757d"), "Secondary"));
		flow.getItems().add(new BootStrapButton(new Color("#28a745"), "Success"));
		flow.getItems().add(new BootStrapButton(new Color("#dc3545"), "Danger"));
		flow.getItems().add(new BootStrapButton(new Color("#ffc107"), "Warning", true));
		flow.getItems().add(new BootStrapButton(new Color("#17a2b8"), "Info"));
		flow.getItems().add(new BootStrapButton(new Color("#f8f9fa"), "Light", true));
		flow.getItems().add(new BootStrapButton(new Color("#343a40"), "Dark"));

		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
	
	static class BootStrapButton extends Pane {
		private Label label;
		private boolean selected;
		private boolean hovered;
		private BoxShadow outline;
		private BoxShadow shadow;

		public BootStrapButton(Color color, String text, boolean darkText ) {
			color.immutable(true);
			Color currentColor = new Color(color);
			
			this.setBackgroundLegacy(currentColor);
			this.setPadding(new Insets(5, 16));
			this.setBorderColor(color.darker());
			this.setBorderRadii(3);
			this.setBorderWidth(1);
			this.setBorderStyle(BorderStyle.SOLID);

			Color shadowColor = new Color(Color.BLACK).alpha(0);
			this.shadow = new BoxShadow(0, 8, 24, -4, shadowColor);
			this.getBoxShadowList().add(shadow);

			this.outline = new BoxShadow(0, 0, 0, 1, color.alpha(0.5f));
			this.getBoxShadowList().add(outline);
			
			// Label
			this.label = new Label(text);
			if ( !darkText )
				this.label.setTextFill(Color.WHITE);
			else
				this.label.setTextFill(Color.DARK_GRAY);
			this.label.setMouseTransparent(true);
			this.getChildren().add(label);
			
			this.setOnSelectedEventInternal((event)->{
				selected = true;
				
				Transition t = new Transition(75) {
					@Override
					public void tick(double progress) {
						outline.setSpread(4 * (float)progress);
					}
				};
				t.play();
			});
			
			this.setOnDeselectedEventInternal((event)->{
				selected = false;
				
				Transition t = new Transition(75) {
					@Override
					public void tick(double progress) {
						outline.setSpread(4 - (4 * (float)progress));
					}
				};
				t.play();
			});
			
			this.setOnMouseEnteredInternal((event)->{
				hovered = true;

				Transition t = new FillTransition(100, new Color(currentColor), color.brighter(), currentColor);
				t.play();
				
				Transition t2 = new FillTransition(200, new Color(shadowColor), Color.BLACK.alpha(0.2f), shadowColor);
				t2.play();
			});
			
			this.setOnMouseExitedInternal((event)->{
				hovered = false;

				Transition t = new FillTransition(100, new Color(currentColor), color, currentColor);
				t.play();
				
				Transition t2 = new FillTransition(200, new Color(shadowColor), Color.BLACK.alpha(0.0f), shadowColor);
				t2.play();
			});
			
			this.setOnMousePressedInternal((event)->{

				Transition t = new FillTransition(100, new Color(currentColor), color.darker(), currentColor);
				t.play();
				
				Transition t2 = new FillTransition(200, new Color(shadowColor), Color.BLACK.alpha(0.5f), shadowColor);
				t2.play();
			});
			
			this.setOnMouseReleasedInternal((event)->{
				if ( hovered ) {
					Transition t = new FillTransition(200, new Color(currentColor), color.brighter(), currentColor);
					t.play();
					
					Transition t2 = new FillTransition(200, new Color(shadowColor), Color.BLACK.alpha(0.2f), shadowColor);
					t2.play();
				}
			});
		}
		
		public BootStrapButton(Color color, String text) {
			this(color, text, false);
		}

		public String getElementType() {
			return "button";
		}
	}
}