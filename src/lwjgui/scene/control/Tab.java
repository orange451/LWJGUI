package lwjgui.scene.control;

import lwjgui.Color;
import lwjgui.geometry.Insets;
import lwjgui.scene.Node;
import lwjgui.scene.layout.StackPane;

public class Tab {
	protected TabButton button;
	private Node content;
	protected Node lastSelected;
	
	public Tab(String name) {
		button = new TabButton(name);
		content = new StackPane();
	}
	
	public Tab() {
		this("");
	}
	
	public void setContent(Node node) {
		this.content = node;
	}
	
	public void setText(String text) {
		button.label.setText(text);
	}

	class TabButton extends StackPane {
		protected Label label;
		
		protected TabButton(String name) {
			label = new Label(name);
			label.setMouseTransparent(true);
			this.getChildren().add(label);
			
			this.setBackground(Color.GRAY);
			this.setPrefSize(4, 4);
			this.setPadding(new Insets(2,2,2,2));
		}
	}

	public Node getContent() {
		return content;
	}
}
