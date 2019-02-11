package lwjgui.scene.control;

import lwjgui.scene.Node;

public class Label extends Labeled {
	
	public Label() {
		//
	}
	
	public Label(String text) {
		super();
		setText(text);
	}
	
	public Label(String text, Node graphic) {
		this(text);
		this.setGraphic(graphic);
	}
}