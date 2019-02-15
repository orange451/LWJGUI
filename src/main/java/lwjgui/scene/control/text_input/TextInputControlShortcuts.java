package lwjgui.scene.control.text_input;

import org.lwjgl.glfw.GLFW;

import lwjgui.event.KeyEvent;

class TextInputControlShortcuts {

	public void process(TextInputControl tic, KeyEvent event) {
		
		if (!tic.editing) return;
		
		// Return if consumed
		if (event.isConsumed()) return;
		
		// Backspace
		if (event.key == GLFW.GLFW_KEY_BACKSPACE) {
			tic.deletePreviousCharacter();
		}
		
		// Delete
		if (event.key == GLFW.GLFW_KEY_DELETE ) {
			tic.deleteNextCharacter();
			event.consume();
		}
		
		// Select All
		if (event.key == GLFW.GLFW_KEY_A && event.isCtrlDown) {
			tic.selectAll();
			event.consume();
		}
		
		// Paste
		if (event.key == GLFW.GLFW_KEY_V && event.isCtrlDown ) {
			tic.paste();
			event.consume();
		}
		
		// Copy
		if (event.key == GLFW.GLFW_KEY_C && event.isCtrlDown ) {
			tic.copy();
			event.consume();
		}
		
		// Cut
		if (event.key == GLFW.GLFW_KEY_X && event.isCtrlDown ) {
			tic.cut();
			event.consume();
		}
		
		// Undo/Redo
		if (event.key == GLFW.GLFW_KEY_Z && event.isCtrlDown ) {
			if (event.isShiftDown) {
				tic.redo();
			} else {
				tic.undo();
			}
			
			event.consume();
		}
		
		// Home
		if (event.key == GLFW.GLFW_KEY_HOME ) {
			
			if (event.isCtrlDown) {
				tic.caretPosition = 0;
			} else {
				tic.home();
			}

			if (!event.isShiftDown) {
				tic.deselect();
			} else {
				tic.selectionEndPosition = tic.caretPosition;
			}
			
			event.consume();
		}
		
		// End
		if (event.key == GLFW.GLFW_KEY_END ) {
				
			if (event.isCtrlDown) {
				tic.caretPosition = tic.getLength();
			} else {
				tic.end();
			}
			
			if (!event.isShiftDown) {
				tic.deselect();
			} else {
				tic.selectionEndPosition = tic.caretPosition;
			}
			
			event.consume();
		}
		
		// Left
		if (event.key == GLFW.GLFW_KEY_LEFT ) {
			if (!event.isShiftDown && tic.getSelection().getLength() > 0) {
				tic.deselect();
			} else {
				tic.setCaretPosition(tic.caretPosition-1);
				if (event.isShiftDown) {
					tic.selectionEndPosition = tic.caretPosition;
				} else {
					tic.selectionStartPosition = tic.caretPosition;
					tic.selectionEndPosition = tic.caretPosition;
				}
			}
			
			event.consume();
		}
		
		// Right
		if (event.key == GLFW.GLFW_KEY_RIGHT ) {
			if (!event.isShiftDown && tic.getSelection().getLength()>0 ) {
				tic.deselect();
			} else {
				tic.setCaretPosition(tic.caretPosition+1);
				if (event.isShiftDown) {
					tic.selectionEndPosition = tic.caretPosition;
				} else {
					tic.selectionStartPosition = tic.caretPosition;
					tic.selectionEndPosition = tic.caretPosition;
				}
			}
			
			event.consume();
		}
		
		// Up
		if (event.key == GLFW.GLFW_KEY_UP ) {
			if (!event.isShiftDown && tic.getSelection().getLength()>0 ) {
				tic.deselect();
			}
			
			int nextRow = tic.getRowFromCaret(tic.caretPosition)-1;
			if ( nextRow < 0 ) {
				tic.setCaretPosition(0);
			} else {
				int pixelX = (int) tic.getPixelOffsetFromCaret(tic.caretPosition);
				int index = tic.getCaretFromPixelOffset(nextRow, pixelX);
				tic.setCaretPosition(index);
			}
			if (event.isShiftDown) {
				tic.selectionEndPosition = tic.caretPosition;
			} else {
				tic.selectionStartPosition = tic.caretPosition;
				tic.selectionEndPosition = tic.caretPosition;
			}
			
			event.consume();
		}
		
		// Down
		if (event.key == GLFW.GLFW_KEY_DOWN ) {
			if (!event.isShiftDown && tic.getSelection().getLength() > 0) {
				tic.deselect();
			}
			
			int nextRow = tic.getRowFromCaret(tic.caretPosition)+1;
			if ( nextRow >= tic.lines.size() ) {
				tic.setCaretPosition(tic.getLength());
			} else {
				int pixelX = (int) tic.getPixelOffsetFromCaret(tic.caretPosition);
				int index = tic.getCaretFromPixelOffset(nextRow, pixelX);
				tic.setCaretPosition(index);
			}
			if (event.isShiftDown) {
				tic.selectionEndPosition = tic.caretPosition;
			} else {
				tic.selectionStartPosition = tic.caretPosition;
				tic.selectionEndPosition = tic.caretPosition;
			}
			
			event.consume();
		}
	}
}