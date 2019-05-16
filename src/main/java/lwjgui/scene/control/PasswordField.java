package lwjgui.scene.control;

import lwjgui.font.Font;
import lwjgui.font.FontStyle;

public class PasswordField extends TextField {
	public PasswordField() {
		this("");
	}
	
	public PasswordField(String text) {
		super();
		
		this.setFont(Font.CONSOLAS);
		this.setFontStyle(FontStyle.BOLD);
		
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
