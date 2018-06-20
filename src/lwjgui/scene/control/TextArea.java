package lwjgui.scene.control;

import java.nio.Buffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.event.KeyEvent;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.scene.Context;
import lwjgui.scene.Cursor;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.Pane;
import lwjgui.theme.Theme;

public class TextArea extends Control {
	private ArrayList<String> lines;
	private int caretPosition;
	private boolean editing = false;
	
	private int selectionStartPosition;
	private int selectionEndPosition;
	
	private TextAreaScrollPane internal;
	private TextAreaContent fakeBox;
	
	private int fontSize = 16;
	private Font font = Font.SANS;
	private FontStyle style = FontStyle.REGULAR;

	public TextArea() {
		this.setText("");
		
		this.fakeBox = new TextAreaContent();
		this.fakeBox.setPrefSize(300, 300);

		this.setBackground(Theme.currentTheme().getControlHover());
		this.internal = new TextAreaScrollPane();
		this.children.add(internal);
		this.internal.setContent(fakeBox);
		
		this.flag_clip = true;
		
		this.setOnTextInput(new KeyEvent() {
			@Override
			public void onEvent(int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				if ( !editing )
					return;
				
				deleteSelection();
				insertText(caretPosition, ""+(char)key);
				setCaretPosition(caretPosition+1);
				deselect();
			}
		});
		
		this.setOnKeyPressed( new KeyEvent() {
			@Override
			public void onEvent(int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				if ( !editing )
					return;
				
				// Tab
				if ( key == GLFW.GLFW_KEY_TAB ) {
					deleteSelection();
					insertText(caretPosition,"\t");
					caretPosition++;
				}
				
				// Backspace
				if ( key == GLFW.GLFW_KEY_BACKSPACE ) {
					if (!deleteSelection()) {
						deleteText(caretPosition-1, caretPosition);
						caretPosition--;
					}
				}
				
				// Delete
				if ( key == GLFW.GLFW_KEY_DELETE ) {
					if (!deleteSelection()) {
						deleteText(caretPosition, caretPosition+1);
					}
				}
				
				// Enter
				if ( key == GLFW.GLFW_KEY_ENTER ) {
					deleteSelection();
					insertText(caretPosition, "\n");
					caretPosition++;
				}
				
				// Select All
				if ( key == GLFW.GLFW_KEY_A && isCtrlDown) {
					selectAll();
				}
				
				// Paste
				if ( key == GLFW.GLFW_KEY_V && isCtrlDown ) {
					paste();
				}
				
				// Copy
				if ( key == GLFW.GLFW_KEY_C && isCtrlDown ) {
					copy();
				}
				
				// Cut
				if ( key == GLFW.GLFW_KEY_X && isCtrlDown ) {
					cut();
				}
				
				// Home
				if ( key == GLFW.GLFW_KEY_HOME ) {
					if ( isShiftDown )
						selectionStartPosition = caretPosition;
					
					if ( isCtrlDown ) {
						caretPosition = 0;
					} else {
						home();
					}

					if ( isShiftDown )
						selectionEndPosition = caretPosition;
				}
				
				// End
				if ( key == GLFW.GLFW_KEY_END ) {
					if ( isShiftDown )
						selectionStartPosition = caretPosition;
					
					if ( isCtrlDown ) {
						caretPosition = getLength();
					} else {
						end();
					}
					
					if ( isShiftDown )
						selectionEndPosition = caretPosition;
				}
				
				// Left
				if ( key == GLFW.GLFW_KEY_LEFT ) {
					if ( !isShiftDown && getSelection().getLength()>0 ) {
						deselect();
					} else {
						setCaretPosition(caretPosition-1);
						if ( isShiftDown ) {
							selectionEndPosition = caretPosition;
						} else {
							selectionStartPosition = caretPosition;
							selectionEndPosition = caretPosition;
						}
					}
				}
				
				// Right
				if ( key == GLFW.GLFW_KEY_RIGHT ) {
					if ( !isShiftDown && getSelection().getLength()>0 ) {
						deselect();
					} else {
						setCaretPosition(caretPosition+1);
						if ( isShiftDown ) {
							selectionEndPosition = caretPosition;
						} else {
							selectionStartPosition = caretPosition;
							selectionEndPosition = caretPosition;
						}
					}
				}
				
				// Up
				if ( key == GLFW.GLFW_KEY_UP ) {
					if ( !isShiftDown && getSelection().getLength()>0 ) {
						deselect();
					}
					
					int nextRow = getRowFromCaret(caretPosition)-1;
					if ( nextRow < 0 ) {
						setCaretPosition(0);
					} else {
						int pixelX = (int) getGlyphPosition(caretPosition).minx();
						int index = getCaretIndexFromPixelOffset(nextRow, pixelX);
						setCaretPosition(index);
					}
					if ( isShiftDown ) {
						selectionEndPosition = caretPosition;
					} else {
						selectionStartPosition = caretPosition;
						selectionEndPosition = caretPosition;
					}
				}
				
				// Down
				if ( key == GLFW.GLFW_KEY_DOWN ) {
					if ( !isShiftDown && getSelection().getLength()>0 ) {
						deselect();
					}
					
					int nextRow = getRowFromCaret(caretPosition)+1;
					if ( nextRow >= lines.size() ) {
						setCaretPosition(getLength());
					} else {
						int pixelX = (int) getGlyphPosition(caretPosition).minx();
						int index = getCaretIndexFromPixelOffset(nextRow, pixelX);
						setCaretPosition(index);
					}
					if ( isShiftDown ) {
						selectionEndPosition = caretPosition;
					} else {
						selectionStartPosition = caretPosition;
						selectionEndPosition = caretPosition;
					}
				}
			}
		});
	}

	public void clear() {
		setText("");
	}
	
	public void deselect() {
		this.selectionStartPosition = caretPosition;
		this.selectionEndPosition = caretPosition;
	}
	
	public void setText(String text) {
		int oldCaret = caretPosition;
		
		if ( lines == null ) {
			this.lines = new ArrayList<String>();
		} else {
			this.lines.clear();
		}
		this.caretPosition = 0;
		this.deselect();
		
		String trail = "[!$*]T!R@A#I$L%I^N&G[!$*]"; // Naive fix to allow trailing blank lines to still be parsed
		text = text.replace("\r", "");
		text = text + trail; // Add tail
		String[] split = text.split("\n");
		for (int i = 0; i < split.length; i++) {
			String tt = split[i];
			tt = tt.replace(trail, ""); // Remove tail
			if ( i < split.length -1 ) {
				tt += "\n";
			}
			lines.add(tt);
		}
		setCaretPosition(oldCaret);
	}
	
	public void appendText(String text) {
		insertText(getLength(), text);
	}
	
	public void insertText(int index, String text) {
		String before = getText(0, index);
		String after = getText(index, getLength());
		setText(before + text + after);
	}
	
	private boolean deleteSelection() {
		IndexRange selection = getSelection();
		if ( selection.getLength() > 0 ) {
			deleteText(selection);
			caretPosition = selection.getStart();
			selectionStartPosition = caretPosition;
			selectionEndPosition = caretPosition;
			return true;
		}
		return false;
	}
	
	public void deleteText(IndexRange range) {
		range.normalize();
		
		if ( range.getStart() < 0 )
			return;
		if ( range.getEnd() > getLength() )
			return;
		
		String text = this.getText();
		String prefix = text.substring(0, range.getStart());
		String suffix = text.substring(range.getEnd(),text.length());
		this.setText(prefix+suffix);
	}
	
	public void deleteText(int start, int end) {
		deleteText(new IndexRange(start, end));
	}
	
	public void copy() {
		String text = getSelectedText();
		
		LWJGUI.runLater(() -> {
			GLFW.glfwSetClipboardString(cached_context.getWindowHandle(), text);
		});
	}
	
	public void cut() {
		copy();
		deleteSelection();
	}
	
	public void paste() {
		deleteSelection();
		LWJGUI.runLater(()-> {
			String str = GLFW.glfwGetClipboardString(cached_context.getWindowHandle());
			insertText(caretPosition, str);
			caretPosition += str.length();
		});
	}
	
	public void home() {
		caretPosition = this.getCaretFromRowLine(getRowFromCaret(caretPosition), 0);
	}
	
	public void end() {
		int line = getRowFromCaret(caretPosition);
		String str = lines.get(line);
		caretPosition = this.getCaretFromRowLine(line, str.length());
	}
	
	public void selectAll() {
		selectionStartPosition = 0;
		selectionEndPosition = getLength();
		caretPosition = selectionEndPosition;
	}
	
	public void backward() {
		caretPosition--;
		if ( caretPosition < 0 ) {
			caretPosition = 0;
		}
	}
	
	public void forward() {
		caretPosition++;
		int offset = getIndexFromCaret(caretPosition);
		if ( offset == -1 ) {
			caretPosition--;
		}
	}
	
	public int getCaretPosition() {
		return this.caretPosition;
	}
	
	public void setCaretPosition(int pos) {
		this.renderCaret = (float) (Math.PI*(3/2f));
		
		this.caretPosition = pos;
	}
	
	public String getSelectedText() {
		return getText(getSelection());
	}
	
	private String getText(IndexRange selection) {
		selection.normalize();
		
		if ( selection.getLength() == 0 )
			return "";
		
		int startLine = getRowFromCaret(selection.getStart());
		int endLine = getRowFromCaret(selection.getEnd());
		int t = startLine;
		String text = "";
		int a = getIndexFromCaret(selection.getStart());
		int b = getIndexFromCaret(selection.getEnd());
		while ( t <= endLine ) {
			String curLine = lines.get(t);
			if ( t == startLine && t != endLine ) {
				text += curLine.substring(a);
			} else if ( t != startLine && t == endLine ) {
				text += curLine.substring(0, b);
			} else if ( t == startLine && t == endLine ) {
				text += curLine.substring(a, b);
			} else {
				text += curLine;
			}
			t++;
		}
		
		return text;
	}
	
	public String getText() {
		String text = "";
		for (int i = 0; i < lines.size(); i++) {
			text += lines.get(i);
			if ( i < lines.size()-1 ) {
				//text += "\n";
			}
		}
		
		return text;
	}
	
	public String getText(int start, int end) {
		return getText(new IndexRange(start,end));
	}

	public IndexRange getSelection() {
		return new IndexRange(selectionStartPosition, selectionEndPosition);
	}

	/**
	 * Returns the amount of characters in this text area.
	 * @return
	 */
	public int getLength() {
		int len = 0;
		for (int i = 0; i < lines.size(); i++) {
			len += lines.get(i).length();
		}
		return len;
	}
	
	/**
	 * Returns the caret offset at the specific line the caret pos is on.
	 * @param pos
	 * @return
	 */
	private int getIndexFromCaret(int pos) {
		int line = getRowFromCaret(pos);
		int a = 0;
		for (int i = 0; i < line; i++) {
			a += lines.get(i).length();
		}
		return pos-a;
	}

	private int getRowFromCaret(int caret) {
		int line = -1;
		int a = 0;
		while ( a <= caret && line < lines.size()-1 ) {
			line++;
			String t = lines.get(line);
			a += t.length();
		}
		return line;
	}
	
	@Override
	protected void resize() {
		super.resize();
		
		int width = getMaxTextWidth();
		this.fakeBox.setPrefSize(width, lines.size()*fontSize);
		
		if ( !this.isDecendentSelected() && editing ) {
			editing = false;
			this.deselect();
		}
		
		if ( caretPosition < 0 )
			caretPosition = 0;
	}
	
	private int getMaxTextWidth() {
		int width = 0;
		for (int i = 0; i < lines.size(); i++) {
			String str = lines.get(i);
			float[] bounds = new float[4];
			
			NanoVG.nvgFontSize(cached_context.getNVG(), fontSize);
			NanoVG.nvgFontFace(cached_context.getNVG(), font.getFont(style));
			NanoVG.nvgTextAlign(cached_context.getNVG(),NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
			NanoVG.nvgTextBounds(cached_context.getNVG(), 0, 0, str, bounds);
			
			int len = (int) (bounds[2]-bounds[0]);
			if ( len > width ) {
				width = len;
			}
		}
		
		return width;
	}
	
	private NVGGlyphPosition getGlyphPosition(int line, int index) {
		if ( cached_context == null )
			return null;
		if ( line < 0 )
			return null;
		if ( line >= lines.size() )
			return null;
		
		String original = lines.get(line);
		String str = original + " ";
		
		if ( index > original.length() )
			index = original.length();
		
		long vg = cached_context.getNVG();
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, font.getFont(style));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
		org.lwjgl.nanovg.NVGGlyphPosition.Buffer positions = NVGGlyphPosition.malloc(str.length());
		NanoVG.nvgTextGlyphPositions(vg, 0, 0, str, positions);
		NVGGlyphPosition lastPosition = positions.get(index);
		positions.free();
		return lastPosition;
	}
	
	private NVGGlyphPosition getGlyphPosition(int caret) {
		return getGlyphPosition(getRowFromCaret(caret),getIndexFromCaret(caret));
	}
	
	private int getCaretFromRowLine(int row, int index) {
		int c = 0;
		for (int i = 0; i < row; i++) {
			c += lines.get(i).length();
		}
		c += index;
		return c;
	}
	
	private int getCaretIndexFromPixelOffset( int row, int offset ) {
		String line = lines.get(row);
		
		if ( line.length() == 0 )
			return getCaretFromRowLine(row,0);
		
		// Find character clicked in row
		long vg = cached_context.getNVG();
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, font.getFont(style));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
		org.lwjgl.nanovg.NVGGlyphPosition.Buffer positions = NVGGlyphPosition.malloc(line.length());
		NanoVG.nvgTextGlyphPositions(vg, 0, 0, line, positions);
		int index = -1;
		float tx = 0;
		NVGGlyphPosition cur = null;
		while ( positions.hasRemaining() && ((cur=positions.get())!=null) && tx-2 <= offset ) {
			tx = cur.maxx();
			index++;
		}
		if ( index >= line.length() )
			index = line.length()-1;
		if ( index < 0 )
			index = 0;
		positions.free();
		
		if ( tx < offset && row == lines.size()-1) {
			index++;
		}
		return getCaretFromRowLine(row,index);
	}
	
	private int getCaretAtMouse() {
		double mx = cached_context.getMouseX()-internal.getContent().getAbsoluteX();
		double my = cached_context.getMouseY()-internal.getContent().getAbsoluteY();
		
		// Find row clicked
		int row = (int) (my / (float)fontSize);
		if ( row > lines.size()-1 )
			row = lines.size()-1;
		if ( row < 0 )
			row = 0;
		return getCaretIndexFromPixelOffset(row, (int) mx);
	}

	@Override
	public void render(Context context) {
		long vg = context.getNVG();
		float x = (int)getAbsoluteX();
		float y = (int)getAbsoluteY();
		float w = (int)getWidth();
		float h = (int)getHeight();
		float r = 2;
		
		this.clip(context,4);
		
		// Selection graphic
		if ( this.isDecendentSelected() ) {
			int feather = 4;
			Color color = context.isFocused()?Theme.currentTheme().getSelection():Theme.currentTheme().getSelectionPassive();
			NanoVG.nvgTranslate(context.getNVG(), x, y);	
				NVGPaint paint = NanoVG.nvgBoxGradient(vg, 0,0, w+1,h+1, r, feather, color.getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.calloc());
				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgRect(vg, -feather,-feather, w+feather*2,h+feather*2);
				NanoVG.nvgFillPaint(vg, paint);
				NanoVG.nvgFill(vg);
				paint.free();
			NanoVG.nvgTranslate(context.getNVG(), -x, -y);	
		}
		
		// Background
		if ( this.getBackground() != null ) {
			LWJGUIUtil.fillRect(context, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), this.getBackground());
		}
		
		// Draw text
		this.internal.setBackground(null);
		this.internal.render(context);
		
		// internal selection graphic
		if ( this.isDecendentSelected() ) {
			NanoVG.nvgTranslate(context.getNVG(), x, y);	
			Color sel = context.isFocused()?Theme.currentTheme().getSelection():Theme.currentTheme().getSelectionPassive();
			Color col = new Color(sel.getRed(), sel.getGreen(), sel.getBlue(), 64);
			NanoVG.nvgBeginPath(vg);
				float inset = 1.33f;
				NanoVG.nvgRoundedRect(vg, inset, inset, w-inset*2+1,h-inset*2+1, (float) r-inset);
				NanoVG.nvgStrokeColor(vg, col.getNVG());
				NanoVG.nvgStrokeWidth(vg, inset*1.25f);
				NanoVG.nvgStroke(vg);
			NanoVG.nvgTranslate(context.getNVG(), -x, -y);
		}
	}
	
	class TextAreaScrollPane extends ScrollPane {
		
		public TextAreaScrollPane() {
			this.setFillToParentHeight(true);
			this.setFillToParentWidth(true);
			this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
			this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
			this.setPadding(new Insets(4,4,4,4));
			
			this.getViewport().setMouseEnteredEvent(new MouseEvent() {
				@Override
				public void onEvent(int button) {
					System.out.println("ENTERED");
					getScene().setCursor(Cursor.IBEAM);
				}
			});

			
			this.getViewport().setMouseLeftEvent(new MouseEvent() {
				@Override
				public void onEvent(int button) {
					System.out.println("LEFT");
					getScene().setCursor(Cursor.NORMAL);
				}
			});
			
			this.getViewport().setMousePressedEvent(new MouseEvent() {
				@Override
				public void onEvent(int button) {
					if ( cached_context == null )
						return;
					
					LWJGUI.runLater(()-> {
						cached_context.setSelected(getViewport());
						editing = true;
					});
					
					setCaretPosition(getCaretAtMouse());
					selectionEndPosition = caretPosition;
					
					int state = GLFW.glfwGetKey(cached_context.getWindowHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);
					if ( state != GLFW.GLFW_PRESS ) {
						selectionStartPosition = caretPosition;
					}
				}
			});
			
			this.getViewport().setMouseDraggedEvent(new MouseEvent() {
				@Override
				public void onEvent(int button) {
					caretPosition = getCaretAtMouse();
					selectionEndPosition = caretPosition;
				}
			});
		}
		
		protected Pane getViewport() {
			return this.internalPane;
		}
	}

	private float renderCaret = 0;
	class TextAreaContent extends Pane {
		
		public TextAreaContent() {
			this.setMouseTransparent(true);
			this.setBackground(null);
		}
		
		@Override
		public void render(Context context) {
			this.clip(context);
			renderCaret += 1/100f;
			
			// Render selection
			IndexRange range = getSelection();
			range.normalize();
			int len = range.getLength();
			int startLine = getRowFromCaret(range.getStart());
			int endLine = getRowFromCaret(range.getEnd());
			int a = getIndexFromCaret(range.getStart());
			int b = getIndexFromCaret(range.getEnd());
			if ( len > 0 ) {
				for (int i = startLine; i <= endLine; i++) {
					String l = lines.get(i);
					NVGGlyphPosition g1;
					NVGGlyphPosition g2;
					
					if ( i == startLine && i == endLine ) {
						g1 = getGlyphPosition(i, a);
						g2 = getGlyphPosition(i, b);
					} else if ( i == startLine && i != endLine ) {
						g1 = getGlyphPosition(i, a);
						g2 = getGlyphPosition(i, l.length()-1);
					} else if ( i != startLine && i == endLine ) {
						g1 = getGlyphPosition(i, 0);
						g2 = getGlyphPosition(i, b);
					} else {
						g1 = getGlyphPosition(i, 0);
						g2 = getGlyphPosition(i, l.length()-1);
					}

					int xx = (int)(getAbsoluteX()+g1.minx());
					int yy = (int)getAbsoluteY() + (fontSize*i);
					int width = (int) (g2.minx()-g1.minx())+1;
					int height = fontSize;
					LWJGUIUtil.fillRect(context, xx, yy, width, height, Theme.currentTheme().getSelectionAlt());
				}
			}
			
			// Draw text
			for (int i = 0; i < lines.size(); i++) {
				int mx = (int)getAbsoluteX();
				int my = (int)getAbsoluteY() + (fontSize*i);
				long vg = context.getNVG();
				
				String text = lines.get(i);
				
				// Setup font
				NanoVG.nvgFontSize(vg, fontSize);
				NanoVG.nvgFontFace(vg, font.getFont(style));
				NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

				// Draw
				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgFontBlur(vg,0);
				NanoVG.nvgFillColor(vg, Theme.currentTheme().getText().getNVG());
				NanoVG.nvgText(vg, mx, my, text);
			}
			
			// Draw caret
			if ( editing ) {
				int line = getRowFromCaret(caretPosition);
				NVGGlyphPosition glyph = getGlyphPosition(caretPosition);
				
				if ( glyph != null ) {
					int cx = (int) (getAbsoluteX()+glyph.minx());
					int cy = (int) (getAbsoluteY() + (line * fontSize));
					
					if ( Math.sin(renderCaret) < 0 ) {
						LWJGUIUtil.fillRect(context, cx, cy, 2, fontSize, Color.BLACK);
					}
				}
			}
		}
	}
}
