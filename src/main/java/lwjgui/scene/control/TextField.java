package lwjgui.scene.control;

public class TextField extends TextInputControl {
	public TextField() {
		this("");
	}
	
	public TextField(String text) {
		super();
		
		this.setText(text);
		this.setPrefWidth(120);
		this.setCornerRadius(3);
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
