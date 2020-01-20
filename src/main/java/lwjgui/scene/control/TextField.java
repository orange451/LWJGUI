package lwjgui.scene.control;

import lwjgui.geometry.Insets;

public class TextField extends TextInputControl {
	public TextField() {
		this("");
	}
	
	public TextField(String text) {
		super();
		
		this.setText(text);
		this.setPrefWidth(120);
		this.setBorderRadii(3);
		
		this.internalScrollPane.setInternalPadding(new Insets(2,0,0,2));
	}
	
	@Override
	public double getPrefHeight() {
		return this.fontSize+8;
	}
	
	@Override
	public void setText(String text) {
		if(text != null)
		super.setText(text.replace("\n", "").replace("\t", ""));
	}

	@Override
	public String getElementType() {
		return "textfield";
	}
}
