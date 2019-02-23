package lwjgui.scene.control.text_input;

public class TextField extends TextInputControl {
	public TextField() {
		this("");
	}
	
	public TextField(String text) {
		this(new TextInputScrollPane(), text);
	}
	
	public TextField(TextInputScrollPane internalScrollPane, String text) {
		super(internalScrollPane);
		this.setText(text);
		this.setPrefWidth(120);
	}
	
	@Override
	public double getPrefHeight() {
		return this.fontSize+8;
	}
	
	@Override
	public void setText(String text) {
		super.setText(text.replace("\n", "").replace("\t", ""));
	}
}
