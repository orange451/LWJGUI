package lwjgui.scene.control;

import lwjgui.scene.Node;

public class Label extends Labeled {
	
	public Label() {
		this("");
	}
	
	public Label(String text) {
		this(text, null);
	}
	
	public Label(String text, Node graphic) {
		super();
		this.setText(text);
		this.setGraphic(graphic);
	}

	@Override
	public String getElementType() {
		return "label";
	}
}