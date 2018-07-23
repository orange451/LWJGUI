package lwjgui.scene.control;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.collections.StateStack;
import lwjgui.event.EventHandler;
import lwjgui.event.KeyEvent;
import lwjgui.geometry.Insets;
import lwjgui.scene.Context;
import lwjgui.scene.Cursor;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.Pane;
import lwjgui.theme.Theme;

public abstract class TextInputControl extends Control {
	private ArrayList<String> lines;
	private ArrayList<ArrayList<GlyphData>> glyphData;
	private ArrayList<String> linesDraw;
	private String source;
	private int caretPosition;
	protected boolean editing = false;
	protected boolean editable = true;
	
	private boolean wordWrap; // Not used yet. Requires major changes
	
	private int selectionStartPosition;
	private int selectionEndPosition;
	
	protected TextAreaScrollPane internal;
	protected TextAreaContent fakeBox;
	
	protected int fontSize = 16;
	protected Font font = Font.SANS;
	protected FontStyle style = FontStyle.REGULAR;
	
	private StateStack<TextState> undoStack;
	
	private int preferredRowCount = 1;
	private int preferredColumnCount = 10;
	
	private String prompt = null;
	
	private TextParser textParser;

	public TextInputControl() {
		this.undoStack = new StateStack<TextState>();
		this.setText("");
		this.saveState();
		
		this.fakeBox = new TextAreaContent();

		this.setBackground(Theme.currentTheme().getControlHover());
		this.internal = new TextAreaScrollPane();
		this.children.add(internal);
		this.internal.setContent(fakeBox);
		
		this.flag_clip = true;
		
		this.setOnTextInput( new EventHandler<KeyEvent>() {
			int charCount = Integer.MAX_VALUE/2;

			@Override
			public void handle(KeyEvent event) {
				if ( !editing )
					return;
				
				charCount++;
				deleteSelection();
				insertText(caretPosition, ""+(char)event.getKey());
				setCaretPosition(caretPosition+1);
				deselect();
				
				// If you press space or if it hasen't saved in 10 chars --> Save history
				if ( (char)event.getKey() == ' ' || charCount > 6 ) {
					charCount = 0;
					saveState();
				}
			}
		});
		
		this.setOnKeyPressed( event -> {
			TextInputControlKeyInput t = new TextInputControlKeyInput(event.key, event.mods, event.isCtrlDown, event.isAltDown, event.isShiftDown);
			if ( t.isConsumed() ) {
				event.consume();
				return;
			}
		});
	}
	
	public void setPreferredRowCount( int rows ) {
		this.preferredRowCount = rows;
	}
	
	public void setPreferredColumnCount( int columns ) {
		this.preferredColumnCount = columns;
	}
	
	protected void saveState() {
		undoStack.Push(new TextState(getText(),caretPosition));
	}
	
	public void setWordWrap(boolean wrap) {
		wordWrap = wrap;
	}
	
	public boolean isWordWrap() {
		return this.wordWrap;
	}

	public void clear() {
		setText("");
		saveState();
	}
	
	public void deselect() {
		this.selectionStartPosition = caretPosition;
		this.selectionEndPosition = caretPosition;
	}
	
	public void setPrompt(String string) {
		this.prompt = string;
	}
	
	public void setText(String text) {
		int oldCaret = caretPosition;
		
		if ( lines == null ) {
			this.lines = new ArrayList<String>();
			this.linesDraw = new ArrayList<String>();
			this.glyphData = new ArrayList<ArrayList<GlyphData>>();
		} else {
			this.lines.clear();
			this.linesDraw.clear();
			this.glyphData.clear();
		}
		this.caretPosition = 0;
		this.deselect();
		
		String trail = "[!$*]T!R@A#I$L%I^N&G[!$*]"; // Naive fix to allow trailing blank lines to still be parsed
		text = text.replace("\r", "");
		this.source = text;
		
		text = text + trail; // Add tail
		String[] split = text.split("\n");
		for (int i = 0; i < split.length; i++) {
			String tt = split[i];
			tt = tt.replace(trail, ""); // Remove tail
			if ( i < split.length -1 ) {
				tt += "\n";
			}
			addRow(tt);
			/*String drawLine = tt;
			if ( this.textParser != null )
				drawLine = textParser.parseText(drawLine);

			lines.add(tt);
			linesDraw.add(drawLine);
			
			ArrayList<GlyphData> glyphEntry = new ArrayList<GlyphData>();
			
			int curWid = 0;
			
			if ( cached_context != null ) {
				bindFont();
				org.lwjgl.nanovg.NVGGlyphPosition.Buffer positions = NVGGlyphPosition.malloc(drawLine.length());
				NanoVG.nvgTextGlyphPositions(cached_context.getNVG(), 0, 0, drawLine, positions);
				int j = 0;
				while (positions.hasRemaining()) {
					glyphEntry.add(fixGlyph(positions.get(), drawLine.substring(j, j+1)));
					j++;
				}
				positions.free();
				glyphData.add(glyphEntry);
			}*/
		}
		setCaretPosition(oldCaret);
	}
	
	private void addRow(String originalText) {
		String drawLine = originalText;
		
		if ( cached_context != null ) {
			ArrayList<GlyphData> glyphEntry = new ArrayList<GlyphData>();
			bindFont();
			
			org.lwjgl.nanovg.NVGGlyphPosition.Buffer positions;
			if (drawLine.length() > 0) {
				positions = NVGGlyphPosition.malloc(drawLine.length());
			} else {
				positions = NVGGlyphPosition.malloc(1);
			}
			
			NanoVG.nvgTextGlyphPositions(cached_context.getNVG(), 0, 0, drawLine, positions);
			int j = 0;
			while (j < drawLine.length()) {
				glyphEntry.add(fixGlyph(positions.get(), drawLine.substring(j, j+1)));
				j++;
			}
			positions.free();
			
			// Word Wrap not yet properly implemented properly. Will be rewritten.
			int vWid = (int) (this.internal.getViewport().getWidth() - 16);
			int maxWidth = (int) (wordWrap?vWid:Integer.MAX_VALUE);
			int index = 0;
			int curWid = 0;
			while ( index < glyphEntry.size() ) {
				GlyphData entry = glyphEntry.get(index);
				
				if ( curWid >= maxWidth ) {
					addRow(originalText.substring(0, index));
					addRow(originalText.substring(index,originalText.length()));
					//index--;
					return;
				}
				
				curWid += entry.width();
				index++;
			}
			
			glyphData.add(glyphEntry);
		}
		
		// Get decorated line
		if ( this.textParser != null )
			drawLine = textParser.parseText(drawLine);
		
		// Add line normally
		lines.add(originalText);
		linesDraw.add(drawLine);
	}
	
	public void appendText(String text) {
		insertText(getLength(), text);
		saveState();
	}
	
	public void insertText(int index, String text) {
		String before = getText(0, index);
		String after = getText(index, getLength());
		setText(before + text + after);
	}
	
	protected boolean deleteSelection() {
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
		saveState();
	}
	
	public void deleteText(int start, int end) {
		deleteText(new IndexRange(start, end));
	}
	
	public void deletePreviousCharacter() {
		if (!deleteSelection()) {
			this.saveState();
			int old = caretPosition;
			deleteText(caretPosition-1, caretPosition);
			this.setCaretPosition(old-1);
		}
	}
	
	public void deleteNextCharacter() {
		if (!deleteSelection()) {
			this.saveState();
			deleteText(caretPosition, caretPosition+1);
		}
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public void setFontSize( int size ) {
		this.fontSize = size;
	}
	
	public void setFontStyle(FontStyle style) {
		this.style = style;
	}
	
	public void undo() {
		if ( this.undoStack.isCurrent() ) {
			this.saveState();
			this.undoStack.Rewind(); // Go back one more, since setting text will overwrite
		}
		TextState state = this.undoStack.Rewind();
		if ( state == null )
			return;
		setText(state.text);
		this.setCaretPosition(state.caretPosition);
	}
	
	public void redo() {
		TextState state = this.undoStack.Forward();
		if ( state == null )
			return;
		//this.undoStack.Rewind(); // Go back one more, since setting text will overwrite
		setText(state.text);
		this.setCaretPosition(state.caretPosition);
	}
	
	public void copy() {
		String text = getSelectedText();
		
		LWJGUI.runLater(() -> {
			GLFW.glfwSetClipboardString(cached_context.getWindowHandle(), text);
		});
	}
	
	public void cut() {
		saveState();
		copy();
		deleteSelection();
	}
	
	public void paste() {
		saveState();
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
	
	public void tab() {
		deleteSelection();
		insertText(caretPosition,"\t");
		setCaretPosition(caretPosition+1);
		saveState();
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
		if ( this.caretPosition < 0 )
			this.caretPosition = 0;
		if ( this.caretPosition > getLength() )
			this.caretPosition = getLength();
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
		/*String text = "";
		for (int i = 0; i < lines.size(); i++) {
			text += lines.get(i);
			if ( i < lines.size()-1 ) {
				//text += "\n";
			}
		}
		
		return text;*/
		return source;
	}
	
	public String getText(int start, int end) {
		return getText(new IndexRange(start,end));
	}
	
	public int lines() {
		return this.lines.size();
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
	 * Returns the caret offset at the specific line the caret position is on.
	 * @param pos
	 * @return
	 */
	protected int getIndexFromCaret(int pos) {
		int line = getRowFromCaret(pos);
		int a = 0;
		for (int i = 0; i < line; i++) {
			a += lines.get(i).length();
		}
		return pos-a;
	}

	/**
	 * Returns the row that this caret position is on.
	 * @param caret
	 * @return
	 */
	protected int getRowFromCaret(int caret) {
		int line = -1;
		int a = 0;
		while ( a <= caret && line < lines.size()-1 ) {
			line++;
			String t = lines.get(line);
			a += t.length();
		}
		return line;
	}
	
	public void setTextParser(TextParser parser) {
		this.textParser = parser;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	@Override
	protected void resize() {
		super.resize();
		
		int prefX = (int) (this.preferredColumnCount*(fontSize*(2/3f)));
		int prefY = (int) ((this.preferredRowCount*fontSize)+this.internal.getPadding().getHeight()+1);
		prefX = (int) Math.min(prefX, this.getMaxPotentialWidth());
		prefY = (int) Math.min(prefY, this.getMaxPotentialHeight());
		this.setPrefSize(prefX, prefY);
		internal.setPrefSize(getWidth(), getHeight());
		
		int width = getMaxTextWidth();
		this.fakeBox.setMinSize(width, lines.size()*fontSize);
		
		if ( this.isDecendentSelected() && editable ) {
			editing = true;
		} else {
			if ( editing ) {
				this.deselect();
			}
			this.editing = false;
		}
		
		if ( caretPosition < 0 )
			caretPosition = 0;
	}
	
	private int getMaxTextWidth() {
		int width = 0;
		for (int i = 0; i < linesDraw.size(); i++) {
			String str = linesDraw.get(i);
			int len = 0;
			if ( glyphData.size() > 0 ) {
				for (int j = 0; j < str.length(); j++) {
					GlyphData d = glyphData.get(i).get(j);
					len += d.width();
				}
			}
			/*float[] bounds = new float[4];
			
			
			NanoVG.nvgFontSize(cached_context.getNVG(), fontSize);
			NanoVG.nvgFontFace(cached_context.getNVG(), font.getFont(style));
			NanoVG.nvgTextAlign(cached_context.getNVG(),NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
			NanoVG.nvgTextBounds(cached_context.getNVG(), 0, 0, str, bounds);
			
			int len = (int) (bounds[2]-bounds[0]);*/
			if ( len > width ) {
				width = len;
			}
		}
		
		return width;
	}
	
	private GlyphData getGlyphFromRowLine(int line, int index) {
		if ( cached_context == null )
			return null;
		if ( line < 0 )
			return null;
		if ( line >= lines.size() )
			return null;
		
		String original = linesDraw.get(line);
		String str = original + " ";
		
		if ( index > original.length() )
			index = original.length();
		
		bindFont();
		org.lwjgl.nanovg.NVGGlyphPosition.Buffer positions = NVGGlyphPosition.malloc(str.length());
		NanoVG.nvgTextGlyphPositions(cached_context.getNVG(), 0, 0, str, positions);
		NVGGlyphPosition lastPosition = positions.get(index);
		positions.free();
		return fixGlyph(lastPosition, str.substring(index,index+1));
	}
	
	private GlyphData getGlyphFromRowLine(int caret) {
		return getGlyphFromRowLine(getRowFromCaret(caret),getIndexFromCaret(caret));
	}
	
	private void bindFont() {
		if ( cached_context == null )
			return;
		
		long vg = cached_context.getNVG();
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, font.getFont(style));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
	}
	
	protected GlyphData fixGlyph(NVGGlyphPosition glyph, String string) {
		if ( string.equals("\t") )
			return new GlyphData( 0, 32, string );
		
		return new GlyphData( glyph.minx(), glyph.maxx(), string );
	}
	
	private int getCaretFromRowLine(int row, int index) {
		int c = 0;
		for (int i = 0; i < row; i++) {
			c += lines.get(i).length();
		}
		c += index;
		return c;
	}
	
	private int getPixelOffsetFromCaret( int caret ) {
		int row = getRowFromCaret( caret );
		int offset = getIndexFromCaret( caret );
		
		int temp = 0;
		for (int i = 0; i < offset; i++) {
			GlyphData g = glyphData.get(row).get(i);
			temp += g.width();
		}
		
		return temp;
	}
	
	private int getCaretFromPixelOffset( int row, int pixelX ) {
		String line = linesDraw.get(row);
		
		if ( line.length() == 0 )
			return getCaretFromRowLine(row,0);
		
		// Find first character clicked in row
		int index = 0;
		int tempx = 0;
		ArrayList<GlyphData> glyphLine = new ArrayList<GlyphData>(glyphData.get(row));
		GlyphData lastGlyph = null;
		for (int i = 0; i < glyphLine.size(); i++) {
			GlyphData dat = glyphLine.get(i);
			lastGlyph = dat;
			if ( dat.character().equals("\n"))
				break;
			if ( tempx+dat.width() > pixelX )
				break;
			tempx += dat.width();
			index++;
		}
		
		// If mouse is halfway over, move to next character (provided the character exists, and it isn't a new line)
		if ( pixelX > tempx + lastGlyph.width()/2) {
			if ( getRowFromCaret(getCaretFromRowLine(row,index+1)) == row ) {
				index++;
			}
		}
		
		// Limit
		if ( index > line.length() )
			index = line.length();
		if ( index < 0 )
			index = 0;
		
		// Return caret position
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
		return getCaretFromPixelOffset(row, (int) mx);
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
				NVGPaint paint = NanoVG.nvgBoxGradient(vg, 0,0, w,h, r, feather, color.getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.calloc());
				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgRect(vg, -feather,-feather, w+feather*2,h+feather*2);
				NanoVG.nvgFillPaint(vg, paint);
				NanoVG.nvgFill(vg);
				paint.free();
			NanoVG.nvgTranslate(context.getNVG(), -x, -y);	
		}
		
		// Outline
		Color outlineColor = this.isDecendentSelected()?Theme.currentTheme().getSelection():Theme.currentTheme().getControlOutline();
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRoundedRect(context.getNVG(), x, y, w, h, (float) 2);
		NanoVG.nvgFillColor(context.getNVG(), outlineColor.getNVG());
		NanoVG.nvgFill(context.getNVG());
		
		// Background
		if ( this.getBackground() != null ) {	
			int inset = 1;
			LWJGUIUtil.fillRect(context, getAbsoluteX()+inset, getAbsoluteY()+inset, getWidth()-inset*2, getHeight()-inset*2, this.getBackground());
		}
		
		// Dropshadow
		NVGPaint bg = NanoVG.nvgLinearGradient(vg, x, y-5, x, y+4, Theme.currentTheme().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.calloc());
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, x, y, w, 4);
		NanoVG.nvgFillPaint(vg, bg);
		NanoVG.nvgFill(vg);
		
		// Draw Prompt
		if ( getLength() == 0 && prompt != null && prompt.length() > 0 ) {
			int xx = (int) this.fakeBox.getAbsoluteX();
			int yy = (int) this.fakeBox.getAbsoluteY();
			
			// Setup font
			NanoVG.nvgFontSize(vg, fontSize);
			NanoVG.nvgFontFace(vg, Font.SANS.getFont(FontStyle.REGULAR));
			NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

			// Draw
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgFontBlur(vg,0);
			NanoVG.nvgFillColor(vg, Theme.currentTheme().getShadow().getNVG());
			NanoVG.nvgText(vg, xx, yy, prompt);
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
				NanoVG.nvgRoundedRect(vg, inset, inset, w-inset*2,h-inset*2, (float) r-inset);
				NanoVG.nvgStrokeColor(vg, col.getNVG());
				NanoVG.nvgStrokeWidth(vg, inset*1.25f);
				NanoVG.nvgStroke(vg);
			NanoVG.nvgTranslate(context.getNVG(), -x, -y);
		}
	}
	
	static class GlyphData { // This class could be avoided if NanoVG author wouldn't ignore me.
		private float minx;
		private float maxx;
		private float width;
		private String c;
		
		public GlyphData( float minx, float maxx, String string ) {
			this.minx = minx;
			this.maxx = maxx;
			this.c = string;
			this.width = Math.abs(maxx-minx);
		}
		
		public String character() {
			return c;
		}
		
		public float width() {
			return width;
		}
		
		public float minx() {
			return minx;
		}
		
		public float maxx() {
			return maxx;
		}
	}
	
	class TextState {
		protected String text;
		protected int caretPosition;
		
		public TextState(String text, int pos) {
			this.text = text;
			this.caretPosition = pos;
		}
		
		@Override
		public String toString() {
			return caretPosition+":"+text;
		}
	}
	
	abstract class TextParser {
		public abstract String parseText(String input);
	}
	
	protected class TextInputControlKeyInput extends KeyEvent {
		public TextInputControlKeyInput(int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
			super(key, mods, isCtrlDown, isAltDown, isShiftDown);
			
			if ( !editing )
				return;
			
			// Return if consued
			if ( this.isConsumed() )
				return;
			
			// Backspace
			if ( key == GLFW.GLFW_KEY_BACKSPACE ) {
				deletePreviousCharacter();
				this.consume();
			}
			
			// Delete
			if ( key == GLFW.GLFW_KEY_DELETE ) {
				deleteNextCharacter();
				this.consume();
			}
			
			// Select All
			if ( key == GLFW.GLFW_KEY_A && isCtrlDown) {
				selectAll();
				this.consume();
			}
			
			// Paste
			if ( key == GLFW.GLFW_KEY_V && isCtrlDown ) {
				paste();
				this.consume();
			}
			
			// Copy
			if ( key == GLFW.GLFW_KEY_C && isCtrlDown ) {
				copy();
				this.consume();
			}
			
			// Cut
			if ( key == GLFW.GLFW_KEY_X && isCtrlDown ) {
				cut();
				this.consume();
			}
			
			// Undo/Redo
			if ( key == GLFW.GLFW_KEY_Z && isCtrlDown ) {
				if ( isShiftDown ) {
					redo();
				} else {
					undo();
				}
				this.consume();
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
				
				this.consume();
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

				this.consume();
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
				this.consume();
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
				this.consume();
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
					int pixelX = (int) getPixelOffsetFromCaret(caretPosition);
					int index = getCaretFromPixelOffset(nextRow, pixelX);
					setCaretPosition(index);
				}
				if ( isShiftDown ) {
					selectionEndPosition = caretPosition;
				} else {
					selectionStartPosition = caretPosition;
					selectionEndPosition = caretPosition;
				}
				this.consume();
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
					int pixelX = (int) getPixelOffsetFromCaret(caretPosition);
					int index = getCaretFromPixelOffset(nextRow, pixelX);
					setCaretPosition(index);
				}
				if ( isShiftDown ) {
					selectionEndPosition = caretPosition;
				} else {
					selectionStartPosition = caretPosition;
					selectionEndPosition = caretPosition;
				}
				this.consume();
			}
		}
	};
	
	class TextAreaScrollPane extends ScrollPane {
		
		public TextAreaScrollPane() {
			this.setFillToParentHeight(true);
			this.setFillToParentWidth(true);
			this.setVbarPolicy(ScrollBarPolicy.NEVER);
			this.setHbarPolicy(ScrollBarPolicy.NEVER);
			this.setPadding(new Insets(4,4,4,4));

			// Enter
			this.getViewport().setMouseEnteredEvent(event -> {
				getScene().setCursor(Cursor.IBEAM);
			});

			// Leave
			this.getViewport().setMouseExitedEvent(event -> {
				getScene().setCursor(Cursor.NORMAL);
			});
			
			// Clicked
			this.getViewport().setMousePressedEvent(event -> {
				if ( cached_context == null )
					return;
				
				LWJGUI.runLater(()-> {
					cached_context.setSelected(getViewport());
				});
				
				setCaretPosition(getCaretAtMouse());
				selectionEndPosition = caretPosition;
				
				int state = GLFW.glfwGetKey(cached_context.getWindowHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);
				if ( state != GLFW.GLFW_PRESS ) {
					selectionStartPosition = caretPosition;
				}
			});
			
			// Drag mouse
			this.getViewport().setMouseDraggedEvent(event -> {
				caretPosition = getCaretAtMouse();
				selectionEndPosition = caretPosition;
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
		
		private long lastTime;
		@Override
		public void render(Context context) {
			super.render(context);
			
			if ( glyphData.size() == 0 ) {
				setText(getText());
			}
			
			this.clip(context);
			renderCaret += lastTime-System.currentTimeMillis();
			lastTime = System.currentTimeMillis();
			
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
					
					int left = a;
					int right = b;
					
					if ( i != endLine )
						right = l.length()-1;
					if ( i != startLine )
						left = 0;

					int xx = (int)(getAbsoluteX());
					int yy = (int)getAbsoluteY() + (fontSize*i);
					int height = fontSize;
					int width = 0;
					for (int j = 0; j < left; j++) {
						xx += glyphData.get(i).get(j).width();
					}
					for (int j = left; j < right; j++) {
						GlyphData g = glyphData.get(i).get(j);
						width += g.width();
					}
					LWJGUIUtil.fillRect(context, xx, yy, width, height, Theme.currentTheme().getSelectionAlt());
				}
			}
			
			// Draw text
			for (int i = 0; i < linesDraw.size(); i++) {
				int mx = (int)getAbsoluteX();
				int my = (int)getAbsoluteY() + (fontSize*i);
				
				// Quick bounds check
				if ( my < internal.getAbsoluteY()-(fontSize*i))
					continue;
				if ( my > internal.getAbsoluteY()+internal.getHeight())
					continue;
				
				long vg = context.getNVG();
				String text = linesDraw.get(i);
				
				// Setup font
				bindFont();
				
				// Inefficient Draw. Thanks NanoVG refusing to implement \t
				if ( glyphData.size() > 0 ) {
					ArrayList<GlyphData> dat = glyphData.get(i);
					if ( dat.size() > 0 ) {
						int x = 0;
						for (int j = 0; j < text.length(); j++) {
							boolean draw = true;
							String c = text.substring(j, j+1);
							char[] cs = c.toCharArray();
							
							// Manual fix for drawing boxes of special characters of certain fonts
							// NanoVG author ALSO refuses to fix this. Thanks again.
							if ( cs.length == 1 ) {
								if ( cs[0] < 32 )
									draw = false;
							}
							GlyphData g = dat.get(j);
							
							if ( draw ) {
								NanoVG.nvgBeginPath(vg);
								NanoVG.nvgFontBlur(vg,0);
								NanoVG.nvgFillColor(vg, Theme.currentTheme().getText().getNVG());
								NanoVG.nvgText(vg, mx+x, my, c);
							}
							
							x += g.width();
						}
					}
				}
			}
			
			// Draw caret
			if ( editing ) {
				int line = getRowFromCaret(caretPosition);
				int index = getIndexFromCaret(caretPosition);
				int cx = (int) (getAbsoluteX()-1);
				int cy = (int) (getAbsoluteY() + (line * fontSize));
				if ( glyphData.size() > 0 ) {
					int offsetX = 0;
					for (int j = 0; j < index; j++) {
						if ( glyphData.size() <= line )
							continue;
						if ( glyphData.get(line).size() <= j)
							continue;
						
						offsetX += glyphData.get(line).get(j).width();
					}
					cx += offsetX;
					
					if ( Math.sin(renderCaret*1/150f) < 0 ) {
						LWJGUIUtil.fillRect(context, cx, cy, 2, fontSize, Color.BLACK);
					}
				}
			}
		}
	}
}
