package lwjgui.scene.control.text_input;

import org.lwjgl.glfw.GLFW;

import lwjgui.event.KeyEvent;
import lwjgui.scene.control.ScrollPane.ScrollBarPolicy;

public class TextArea extends TextInputControl {
	public TextArea() {
		super(new TextAreaShortcuts());
		
		// Make it larger by default
		this.setPreferredColumnCount(20);
		this.setPreferredRowCount(8);
		
		// Allow for scroll bars
		this.internal.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.internal.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
	}
}

class TextAreaShortcuts extends TextInputControlShortcuts {
	@Override
	public void process(TextInputControl tic, KeyEvent event) {
		super.process(tic, event);

		// Enter
		if (event.key == GLFW.GLFW_KEY_ENTER ) {
			tic.deleteSelection();
			tic.insertText(tic.getCaretPosition(), "\n");
			tic.setCaretPosition(tic.getCaretPosition()+1);
			tic.deselect();
		}
		
		// Tab
		if (event.key == GLFW.GLFW_KEY_TAB ) {
			tic.tab();
		}
	}
}
