package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.control.ScrollPane.ScrollBarPolicy;

public class TextArea extends TextInputControl {
	public TextArea() {
		// Make it larger by default
		this.setPreferredColumnCount(20);
		this.setPreferredRowCount(8);
		
		// Allow for Tab and Enter inputs
		this.setOnKeyPressed( new TextAreaKeyInput() );
		
		// Allow for scroll bars
		this.internal.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.internal.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
	}
	
	protected class TextAreaKeyInput extends TextInputControlKeyInput {
		@Override
		public void onEvent(int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
			if ( !editing )
				return;
			
			// Parents key input
			super.onEvent(key, mods, isCtrlDown, isAltDown, isShiftDown);
			
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
