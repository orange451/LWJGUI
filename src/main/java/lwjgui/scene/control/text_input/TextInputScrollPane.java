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
		setBackground(null);
		
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
			
			//Sets caret position at mouse
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

	//TODO: Finish this
	public void scrollToCaret() {
		int caret = textInputControl.caretPosition;
		//int numVisibleLines = textInputControl.getNumVisibleLines();
		int numLines = textInputControl.getNumLines();
		
		//System.err.println(caret + " " + numVisibleLines + " " + numLines);
		
		double vValue = ((double) caret / (double) numLines);
		//setVvalue(vValue);
	}
	
	public void scrollToBottom() {
		setVvalue(1.0);
	}

	protected Pane getViewport() {
		return internalScrollCanvas;
	}
}