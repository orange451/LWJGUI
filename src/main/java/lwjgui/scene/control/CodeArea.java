package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.event.KeyEvent;
import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.control.TextInputControl.TextInputControlShortcuts;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.VBox;
import lwjgui.theme.Theme;

public class CodeArea extends TextArea {
	private LineCounterNode lineCounter;
	
	public CodeArea(String text) {
		super(text);
		
		this.setFont(Font.COURIER);
		this.setFontSize(16);
		
		// Replace content window with our custom one
		this.internalRenderingPane = new CodeAreaContent(this);
		this.internalScrollPane.setContent(internalRenderingPane);

		// Add line counter to scrollpane
		this.lineCounter = new LineCounterNode();
		this.internalRenderingPane.getChildren().add(lineCounter);
		
		this.shortcuts = new TextAreaShortcutsCode();
	}
	
	class TextAreaShortcutsCode extends TextAreaShortcuts {
		@Override
		public void process(TextInputControl tic, KeyEvent event) {
			if ( event.isConsumed() )
				return;
			
			if ( !tic.isEditing() )
				return;
			
			// Tab
			if (event.key == GLFW.GLFW_KEY_TAB ) {
				IndexRange selection = tic.getSelection().normalize();
				if ( selection.getLength() == 0 )
					selection = new IndexRange(CodeArea.this.getCaretPosition(), CodeArea.this.getCaretPosition());
				
				// Special tab
				if ( selection.getLength() != 0 || event.isShiftDown ) {
					if ( event.isShiftDown ) {
						// Tab down
						int lineStart = CodeArea.this.getRowFromCaret(selection.getStart());
						int lineEnd = CodeArea.this.getRowFromCaret(selection.getEnd());
						for (int i = lineStart; i <= lineEnd; i++) {
							final int line = i;
							int startPos = CodeArea.this.getCaretFromRowLine(line, 0);
							
							// remove tab
							if ( CodeArea.this.getText(startPos, startPos+1).equals("\t") ) {
								CodeArea.this.deleteText(startPos, startPos+1);
							
								// Update selection
								if ( startPos < selection.getStart() )
									selection.setStart(selection.getStart()-1);
								selection.setEnd(selection.getEnd()-1);
							}
						}
						CodeArea.this.setSelection(selection);
					} else {
						// Tab up
						int lineStart = CodeArea.this.getRowFromCaret(selection.getStart());
						int lineEnd = CodeArea.this.getRowFromCaret(selection.getEnd());
						for (int i = lineStart; i <= lineEnd; i++) {
							final int line = i;
							int startPos = CodeArea.this.getCaretFromRowLine(line, 0);
							
							// Add tab
							CodeArea.this.insertText(startPos, "\t");
							
							// Update selection
							if ( startPos < selection.getStart() )
								selection.setStart(selection.getStart()+1);
							selection.setEnd(selection.getEnd()+1);
						}
						CodeArea.this.setSelection(selection);
					}
					event.consume();
				}
			}
			
			super.process(tic, event);
		}
	}
	
	public CodeArea() {
		this("");
	}
	
	@Override
	protected void position(Node parent) {
		// Only update if the amount of lines has changed
		lineCounter.update(this.getNumLines());
		
		// Normal positioning
		super.position(parent);
		
		// Update padding
		//this.internalScrollPane.setInternalPadding(new Insets(internalScrollPane.getInternalPadding().getTop(), internalScrollPane.getInternalPadding().getRight(), internalScrollPane.getInternalPadding().getBottom(), lineCounter.getWidth()+2));
		this.internalScrollPane.setInternalPadding(new Insets(internalScrollPane.getInternalPadding().getTop(), internalScrollPane.getInternalPadding().getRight(), internalScrollPane.getInternalPadding().getBottom(), lineCounter.getWidth()+2));
	}
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		lineCounter.render(context);
		super.render(context);
	}
	
	class CodeAreaContent extends TextInputContentRenderer {
		public CodeAreaContent(TextInputControl textInputControl) {
			super(textInputControl);
		}

		@Override
		public void render(Context context) {
			// Draw current selected line
			if ( editing ) {
				Color c1 = Theme.current().getSelection();
				Color c2 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 32);
				double xx = getX();
				double yy = getY()+getRowFromCaret(getCaretPosition())*fontSize;
				LWJGUIUtil.fillRect(context, xx+1, yy, CodeArea.this.getWidth()-2-(xx-CodeArea.this.getX()), fontSize, c2);
			}
			
			// Render normal text
			super.render(context);
		}
	}
	
	class LineCounterNode extends VBox {
		int lastLines = -1;
		
		public LineCounterNode() {
			this.setFillToParentWidth(false);
			this.setFillToParentHeight(true);
			this.setMouseTransparent(true);
			this.setPadding(new Insets(0,4,0,0));
			this.setAlignment(Pos.TOP_LEFT);
			this.setBackgroundLegacy(null);
			this.setPrefWidth(0);
			this.flag_clip = false;
		}
		
		@Override
		public void position(Node parent) {
			super.position(parent);
			
			this.setAbsolutePosition(CodeArea.this.getX(), internalScrollPane.getContent().getY()+2);
			this.updateChildren();
		}
		
		public void update(int lines) {
			if ( lines != lastLines ) {
				lastLines = lines;
				
				// Add line labels in
				getChildren().clear();
				for (int i = 0; i < lines; i++) {
					Label l = new Label(" "+(i+1)) {
						@Override
						public void render(Context context) {
							Pane viewport = CodeArea.this.getInternalScrollPane().getViewport();
							NanoVG.nvgScissor(context.getNVG(), (int)viewport.getX(), (int)viewport.getY(), (int)viewport.getWidth(), (int)viewport.getHeight());
							super.render(context);
						}
					};
					l.setFontSize(fontSize);
					l.setFont(Font.COURIER);
					getChildren().add(l);
				}
			}
		}
		
		@Override
		public void render( Context context ) {
			// Draw line counter background
			LWJGUIUtil.fillRect(context, CodeArea.this.getX(), CodeArea.this.getY(), getWidth(), CodeArea.this.getInnerBounds().getHeight()-2, Theme.current().getPane());
			LWJGUIUtil.fillRect(context, CodeArea.this.getX()+getWidth(), CodeArea.this.getY(), 1, CodeArea.this.getInnerBounds().getHeight()-2, Theme.current().getSelectionPassive());
			
			this.position(this.getParent());

			super.render(context);
		}
	}
}
