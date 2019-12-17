package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.event.KeyEvent;
import lwjgui.scene.control.ScrollPane.ScrollBarPolicy;

public class TextArea extends TextInputControl {
	
	public TextArea(String text) {
		super();
		
		setText(text);
		
		// Make it larger by default
		this.setPrefSize(240, 160);
		
		// Allow for scroll bars
		this.internalScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.internalScrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		// Overwrite the default shortcuts (add enter/tab)
		this.shortcuts = new TextAreaShortcuts();
	}
	
	public TextArea() {
		this("");
	}

	@Override
	public String getElementType() {
		return "textarea";
	}
	
	class TextAreaShortcuts extends TextInputControlShortcuts {
		@Override
		public void process(TextInputControl tic, KeyEvent event) {
			super.process(tic, event);
			
			if ( event.isConsumed() )
				return;
			
			if ( !tic.isEditing() )
				return;
			
			// Enter
			if (event.key == GLFW.GLFW_KEY_ENTER ) {
				tic.saveState();
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
}
