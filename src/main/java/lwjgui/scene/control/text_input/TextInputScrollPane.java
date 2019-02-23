package lwjgui.scene.control.text_input;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.geometry.Insets;
import lwjgui.scene.Cursor;
import lwjgui.scene.control.ScrollPane;
import lwjgui.scene.layout.Pane;

/**
 * Acts as the internal ScrollPane for TextInputControl, preventing text content from overflowing.
 */
public class TextInputScrollPane extends ScrollPane {
	
	TextInputControl textInputControl = null;
	
	public TextInputScrollPane() {
		setFillToParentHeight(true);
		setFillToParentWidth(true);
		setVbarPolicy(ScrollBarPolicy.NEVER);
		setHbarPolicy(ScrollBarPolicy.NEVER);
		setPadding(new Insets(4,4,4,4));

		// Enter
		getViewport().setOnMouseEntered(event -> {
			getScene().setCursor(Cursor.IBEAM);
		});

		// Leave
		getViewport().setOnMouseExited(event -> {
			getScene().setCursor(Cursor.NORMAL);
		});
		
		// Clicked
		getViewport().setOnMousePressed(event -> {
			if (cached_context == null) {
				return;
			}
			
			LWJGUI.runLater(()-> {
				cached_context.setSelected(getViewport());
			});
			
			if (textInputControl != null) {
				textInputControl.setCaretPosition(textInputControl.getCaretAtMouse());
				textInputControl.selectionEndPosition = textInputControl.caretPosition;
				
				int state = GLFW.glfwGetKey(cached_context.getWindowHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);
				if ( state != GLFW.GLFW_PRESS ) {
					textInputControl.selectionStartPosition = textInputControl.caretPosition;
				}
			}
		});
		
		// Drag mouse
		getViewport().setOnMouseDragged(event -> {
			if (textInputControl != null) {
				textInputControl.caretPosition = textInputControl.getCaretAtMouse();
				textInputControl.selectionEndPosition = textInputControl.caretPosition;
			}
		});
	}
	
	public void scrollBottom() {
		this.setVvalue(1.0);
	}

	protected Pane getViewport() {
		return internalScrollCanvas;
	}
}