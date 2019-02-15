package lwjgui.scene.control.text_input;

public class TextField extends TextInputControl {
	public TextField() {
		this("");
	}
	
	public TextField(String text) {
		super();
		this.setText(text);
	}
	
	@Override
	public void setText(String text) {
		super.setText(text.replace("\n", ""));
	}
}
