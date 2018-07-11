package lwjgui.scene.control;

import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;

public class PasswordField extends TextField {
	public PasswordField() {
		this("");
		this.setFontStyle(FontStyle.BOLD);
	}
	
	public PasswordField(String text) {
		super();
		
		this.setFont(Font.COURIER);
		
		this.setTextParser( new TextParser() {
			@Override
			public String parseText(String input) {
				String t = "";
				for (int i = 0; i < input.length(); i++) {
					t += "\u2022";
				}
				
				return t;
			}
		});
		
		this.setText(text);
	}
}
