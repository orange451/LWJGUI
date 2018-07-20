package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.control.ScrollPane.ScrollBarPolicy;
import lwjgui.scene.control.TextInputControl.TextInputControlKeyInput;

public class TextArea extends TextInputControl {
	public TextArea() {
		// Make it larger by default
		this.setPreferredColumnCount(20);
		this.setPreferredRowCount(8);
		
		// Allow for Tab and Enter inputs
		this.setOnKeyPressed( event -> {
			TextInputControlKeyInput t = new TextAreaKeyInput(event.key, event.mods, event.isCtrlDown, event.isAltDown, event.isShiftDown);
			if ( t.isConsumed() ) {
				event.consume();
				return;
			}
		});
		
		// Allow for scroll bars
		this.internal.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.internal.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
	}
	
	protected class TextAreaKeyInput extends TextInputControlKeyInput {
		public TextAreaKeyInput(int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
			super(key, mods, isCtrlDown, isAltDown, isShiftDown);
			
			if ( !editing )
				return;
			
			// Return if consumed
			if ( this.isConsumed() )
				return;
			
			// Enter
			if ( key == GLFW.GLFW_KEY_ENTER ) {
				deleteSelection();
				insertText(getCaretPosition(), "\n");
				setCaretPosition(getCaretPosition()+1);
				deselect();
				this.consume();
			}
			
			// Tab
			if ( key == GLFW.GLFW_KEY_TAB ) {
				tab();
				this.consume();
			}
		}
	}
}
