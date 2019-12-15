package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.FlowPane;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;

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
		private float rad = 0;
		private BoxShadow outline;
		private BoxShadow shadow;

		public BootStrapButton(Color color, String text, boolean darkText ) {
			color.immutable(true);
			
			this.setBackgroundLegacy(color);
			this.setPadding(new Insets(5, 16));
			this.setBorderColor(color.darker());
			this.setBorderRadii(3);
			this.setBorderWidth(1);
			this.setBorderStyle(BorderStyle.SOLID);
			
			this.shadow = new BoxShadow(4, 4, 16, -1, Color.BLACK.alpha(0));
			this.getBoxShadowList().add(shadow);
			
			this.outline = new BoxShadow(0, 0, 0, rad, color.alpha(0.5f));
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
			});
			
			this.setOnDeselectedEventInternal((event)->{
				selected = false;
			});
			
			this.setOnMouseEnteredInternal((event)->{
				this.setBackgroundLegacy(color.brighter());
				hovered = true;
			});
			
			this.setOnMouseExitedInternal((event)->{
				this.setBackgroundLegacy(color);
				hovered = false;
			});
			
			this.setOnMousePressedInternal((event)->{
				this.setBackgroundLegacy(color.darker());
			});
			
			this.setOnMouseReleasedInternal((event)->{
				if ( hovered )
					this.setBackgroundLegacy(color.brighter());
			});
		}
		
		public BootStrapButton(Color color, String text) {
			this(color, text, false);
		}
		
		private float tween(double value1, double value2, double ratio) {
			ratio = Math.min(Math.max(ratio, 0), 1);
			double v = value1 + (value2-value1)*ratio;
			return (float)v;
		}
		
		protected void position(Node parent) {
			super.position(parent);	
			if ( selected ) {
				this.outline.setSpread(tween(this.outline.getSpread(), 4, 1/20f));
			} else {
				this.outline.setSpread(tween(this.outline.getSpread(), 0, 1/60f));
			}
			
			if ( hovered ) {
				this.shadow.setFromColor(Color.BLACK.alpha(tween(this.shadow.getFromColor().getAlphaF(), 0.33, 1/50d)));
			} else {
				this.shadow.setFromColor(Color.BLACK.alpha(tween(this.shadow.getFromColor().getAlphaF(), 0, 1/400d)));
			}
		}
	}
}