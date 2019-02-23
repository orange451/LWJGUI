package lwjgui.scene.control.text_input;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.collections.StateStack;
import lwjgui.event.Event;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.event.TypeEvent;
import lwjgui.font.Font;
import lwjgui.font.FontMetaData;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.Control;
import lwjgui.scene.control.IndexRange;
import lwjgui.theme.Theme;

/**
 * This class acts as the core controller for text input classes.
 */
public abstract class TextInputControl extends Control {
	ArrayList<String> lines;
	ArrayList<ArrayList<GlyphData>> glyphData;
	ArrayList<String> linesDraw;
	private String source;
	int caretPosition;
	protected boolean editing = false;
	protected boolean editable = true;
	
	private boolean wordWrap; // Buggy AF
	
	int selectionStartPosition;
	int selectionEndPosition;
	
	protected TextInputScrollPane internalScrollPane;
	protected TextInputContent fakeBox;
	
	private StateStack<TextState> undoStack;

	private EventHandler<Event> onSelectEvent;
	private EventHandler<Event> onDeselectEvent;
	
	private String prompt = null;
	
	private TextParser textParser;

	private static final int MAX_LINES = 10_000;
	
	/*
	 * Visual customization
	 */
	protected int fontSize = 16;
	protected Font font = Font.SANS;
	protected Color fontFill = Theme.current().getText();
	protected FontStyle style = FontStyle.REGULAR;
	
	private boolean decorated = true;
	private boolean selectionOutlineEnabled = true;
	
	Color caretFill = Color.BLACK;
	boolean caretFading = false;
	
	private Color selectionFill = Theme.current().getSelection();
	private Color selectionPassiveFill = Theme.current().getSelectionPassive();
	Color selectionAltFill = Theme.current().getSelectionAlt();
	private Color controlOutlineFill = Theme.current().getControlOutline();
	
	public TextInputControl() {
		this(new TextInputScrollPane(), new TextInputControlShortcuts());
	}
	
	public TextInputControl(TextInputScrollPane textAreaScrollPane) {
		this(textAreaScrollPane, new TextInputControlShortcuts());
	}
	
	public TextInputControl(TextInputControlShortcuts specialKeyInputs) {
		this(new TextInputScrollPane(), specialKeyInputs);
	}
	
	public TextInputControl(TextInputScrollPane textAreaScrollPane, TextInputControlShortcuts specialKeyInputs) {

		/*
		 * Visual setup
		 */
		
		setBackground(Theme.current().getBackground());
		
		/*
		 * Input setup
		 */
		this.fakeBox = new TextInputContent(this);
		
		undoStack = new StateStack<TextState>();
		setText("");
		saveState();
		this.flag_clip = true;
		
		setOnTextInputInternal( new EventHandler<TypeEvent>() {
			int charCount = Integer.MAX_VALUE/2;

			@Override
			public void handle(TypeEvent event) {
				if (!editing) return;
				
				charCount++;
				deleteSelection();
				insertText(caretPosition, event.getCharacterString());
				setCaretPosition(caretPosition+1);
				deselect();
				
				// If you press space or if it hasen't saved in 10 chars --> Save history
				if (event.character == ' ' || charCount > 6) {
					charCount = 0;
					saveState();
				}
			}
		});
		
		setOnKeyPressedAndRepeatInternal( event -> {
			specialKeyInputs.process(this, event);
		});
		
		
		/*
		 * Scroll Pane setup
		 */
		
		internalScrollPane = textAreaScrollPane;
		internalScrollPane.setContent(fakeBox);
		internalScrollPane.textInputControl = this;
		children.add(internalScrollPane);
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
			
			// Create glyph data for each character in the line
			NanoVG.nvgTextGlyphPositions(cached_context.getNVG(), 0, 0, drawLine, positions);
			int j = 0;
			while (j < drawLine.length()) {
				GlyphData currentGlyph = fixGlyph(positions.get(), drawLine.substring(j, j+1));
				glyphEntry.add(currentGlyph);
				j++;
			}
			positions.free();
			
			// Add blank glyph to end of line
			GlyphData last = glyphEntry.size()== 0 ? new GlyphData(0,0,"") : glyphEntry.get(glyphEntry.size()-1);
			glyphEntry.add(new GlyphData( last.x()+last.width, 0, "" ));
			
			// Hack to fix spacing of special characters
			for (int i = 0; i < glyphEntry.size()-1; i++) {
				GlyphData currentGlyph = glyphEntry.get(i);
				if ( currentGlyph.SPECIAL ) {
					GlyphData t = glyphEntry.get(i+1);
					float tOff = t.x()-currentGlyph.x();
					float newOff = currentGlyph.width()-tOff;
					
					for (int k = i+1; k < glyphEntry.size(); k++) {
						GlyphData fixGlyph = glyphEntry.get(k);
						fixGlyph.x += newOff;
					}
				}
			}
			
			// Word Wrap not yet properly implemented properly. Will be rewritten.
			int vWid = (int) (this.internalScrollPane.getViewport().getWidth() - 24);
			int maxWidth = (int) (wordWrap?vWid:Integer.MAX_VALUE);
			int index = 0;
			while ( index < originalText.length() ) {
				GlyphData entry = glyphEntry.get(index);
				
				if ( entry.x()+entry.width() >= maxWidth ) {
					addRow(originalText.substring(0, index));
					addRow(originalText.substring(index,originalText.length()));
					//index--;
					return;
				}
				
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
		
		while ( lines.size() > MAX_LINES ) {
			lines.remove(0);
			linesDraw.remove(0);
			glyphData.remove(0);
		}
	}
	
	public void appendText(String text) {
		insertText(getLength(), text);
		saveState();
		
		internalScrollPane.scrollBottom();
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
	
	public void setFontFill(Color fontFill) {
		this.fontFill = fontFill;
	}
	
	public void setFontSize( int size ) {
		this.fontSize = size;
	}
	
	public void setFontStyle(FontStyle style) {
		this.style = style;
	}

	public Color getSelectionFill() {
		return selectionFill;
	}

	public void setSelectionFill(Color selectionFill) {
		this.selectionFill = selectionFill;
	}

	public Color getSelectionPassiveFill() {
		return selectionPassiveFill;
	}

	public void setSelectionPassiveFill(Color selectionPassiveFill) {
		this.selectionPassiveFill = selectionPassiveFill;
	}

	public Color getSelectionAltFill() {
		return selectionAltFill;
	}

	public void setSelectionAltFill(Color selectionAltFill) {
		this.selectionAltFill = selectionAltFill;
	}

	public Color getControlOutlineFill() {
		return controlOutlineFill;
	}

	public void setControlOutlineFill(Color controlOutlineFill) {
		this.controlOutlineFill = controlOutlineFill;
	}

	public void setOnSelected( EventHandler<Event> event ) {
		this.onSelectEvent = event;
	}
	
	public void setOnDeselected( EventHandler<Event> event ) {
		this.onDeselectEvent = event;
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
		
		if ( str.length() > 0 && str.charAt(str.length()-1) == '\n' )
			caretPosition--;
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

	public Color getCaretFill() {
		return caretFill;
	}

	public void setCaretFill(Color caretFill) {
		this.caretFill = caretFill;
	}

	public boolean isCaretFading() {
		return caretFading;
	}

	/**
	 * If set to true, the caret will fade in and out instead of blink in and out.
	 * 
	 * @param caretFading
	 */
	public void setCaretFading(boolean caretFading) {
		this.caretFading = caretFading;
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
		this.setAlignment(Pos.TOP_LEFT);
		internalScrollPane.setPrefSize(getWidth(), getHeight());
		
		int width = getMaxTextWidth();
		this.fakeBox.setMinSize(width, lines.size()*fontSize);
		
		super.resize();
		
		/*int prefX = (int) (this.preferredColumnCount*(fontSize*(2/3f)));
		int prefY = (int) ((this.preferredRowCount*fontSize)+this.internal.getPadding().getHeight()+1);
		prefX = (int) Math.min(prefX, this.getMaxPotentialWidth());
		prefY = (int) Math.min(prefY, this.getMaxPotentialHeight());
		this.setPrefSize(prefX, prefY);*/
		
		//TODO: Move htis into an actual input callback
		if ( this.isDescendentSelected() && editable ) {
			if ( !editing && onSelectEvent != null ) {
				EventHelper.fireEvent(onSelectEvent, new Event());
			}
			editing = true;
		} else {
			if ( editing ) {
				if ( onSelectEvent != null ) {
					EventHelper.fireEvent(onDeselectEvent, new Event());
				}
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
	
	protected void bindFont() {
		if ( cached_context == null )
			return;
		
		long vg = cached_context.getNVG();
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, font.getFont(style));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
	}
	
	protected void bindFont(FontMetaData data) {
		if ( cached_context == null )
			return;
		
		int fs = fontSize;
		Font f = data.getFont()==null?font:data.getFont();
		FontStyle fst = data.getStyle()==null?style:data.getStyle();
		
		long vg = cached_context.getNVG();
		NanoVG.nvgFontSize(vg, fs);
		NanoVG.nvgFontFace(vg, f.getFont(fst));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
	}
	
	protected GlyphData fixGlyph(NVGGlyphPosition glyph, String originalCharacter) {
		if ( originalCharacter.equals("\t") )
			return new GlyphData( glyph.x(), 32, originalCharacter, true );
		
		return new GlyphData( glyph.x(), glyph.maxx()-glyph.x(), originalCharacter );
	}
	
	private int getCaretFromRowLine(int row, int index) {
		int c = 0;
		for (int i = 0; i < row; i++) {
			c += lines.get(i).length();
		}
		c += index;
		return c;
	}
	
	int getPixelOffsetFromCaret( int caret ) {
		int row = getRowFromCaret( caret );
		int offset = getIndexFromCaret( caret );
		
		int temp = 0;
		for (int i = 0; i < offset; i++) {
			GlyphData g = glyphData.get(row).get(i);
			temp += g.width();
		}
		
		return temp;
	}
	
	int getCaretFromPixelOffset( int row, int pixelX ) {
		String line = linesDraw.get(row);
		
		if ( line.length() == 0 )
			return getCaretFromRowLine(row,0);
		
		// Find first character clicked in row
		int index = 0;
		int tempx = 0;
		ArrayList<GlyphData> glyphLine = glyphData.get(row);
		GlyphData lastGlyph = null;
		for (int i = 0; i < glyphLine.size(); i++) {
			GlyphData dat = glyphLine.get(i);
			lastGlyph = dat;
			if ( dat.character().equals("\n"))
				break;
			if ( dat.x()+dat.width/2-3 > pixelX )
				break;
			index++;
		}
		
		// If mouse is halfway over, move to next character (provided the character exists, and it isn't a new line)
		if ( pixelX > tempx + lastGlyph.width()/2) {
			if ( getRowFromCaret(getCaretFromRowLine(row,index+1)) == row ) {
				//index++;
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
	
	int getCaretAtMouse() {
		double mx = cached_context.getMouseX()-internalScrollPane.getContent().getX();
		double my = cached_context.getMouseY()-internalScrollPane.getContent().getY();
		
		// Find row clicked
		int row = (int) (my / (float)fontSize);
		if ( row > lines.size()-1 )
			row = lines.size()-1;
		if ( row < 0 )
			row = 0;
		return getCaretFromPixelOffset(row, (int) mx);
	}
	
	public boolean isDecorated() {
		return decorated;
	}

	public void setDecorated(boolean backgroundEnabled) {
		this.decorated = backgroundEnabled;
	}

	public boolean isSelectionOutlineEnabled() {
		return selectionOutlineEnabled;
	}

	public void setSelectionOutlineEnabled(boolean selectionOutlineEnabled) {
		this.selectionOutlineEnabled = selectionOutlineEnabled;
	}

	public TextInputScrollPane getInternalScrollPane() {
		return internalScrollPane;
	}
	
	public void setHighlighting(int startIndex, int endIndex, FontMetaData metaData) {
		highlighting.add(new TextHighlighter( startIndex, endIndex, metaData) );
	}
	
	public void resetHighlighting() {
		highlighting.clear();
	}
	
	private ArrayList<TextHighlighter> highlighting = new ArrayList<TextHighlighter>();
	
	protected TextHighlighter getHighlighting( int position ) {
		for (int i = 0; i < highlighting.size(); i++) {
			TextHighlighter t = highlighting.get(i);
			if ( t.contains(position) )
				return t;
		}
		
		return null;
	}
	
	static class TextHighlighter {
		private int startIndex;
		private int endIndex;
		private FontMetaData metaData;
		
		public TextHighlighter(int startIndex, int endIndex, FontMetaData metaData) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.metaData = metaData;
		}

		public boolean contains(int position) {
			return position >= startIndex && position <= endIndex;
		}

		public FontMetaData getMetaData() {
			return this.metaData;
		}
	}

	@Override
	public void render(Context context) {
		long vg = context.getNVG();
		float x = (int)(getX()+this.getInnerBounds().getX());
		float y = (int)(getY()+this.getInnerBounds().getY());
		float w = (int)this.getInnerBounds().getWidth();
		float h = (int)this.getInnerBounds().getHeight();
		float r = 2;
		

		super.render(context);
		this.clip(context,4);
		
		// Selection graphic
		if (isDescendentSelected() && isDecorated()) {
			int feather = 4;
			Color color = context.isFocused() ? selectionFill : selectionPassiveFill;
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
		if (isDecorated()) {
			Color outlineColor = this.isDescendentSelected()? selectionFill : controlOutlineFill;
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRoundedRect(context.getNVG(), x, y, w, h, (float) 2);
			NanoVG.nvgFillColor(context.getNVG(), outlineColor.getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
		
		// Background
		if (isDecorated() && getBackground() != null ) {	
			int inset = 1;
			LWJGUIUtil.fillRect(context, getX()+inset, getY()+inset, w-inset*2, h-inset*2, this.getBackground());
		}
		
		// Dropshadow
		if (isDecorated()) {
			NVGPaint bg = NanoVG.nvgLinearGradient(vg, x, y-5, x, y+4, Theme.current().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.calloc());
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRect(vg, x, y, w, 4);
			NanoVG.nvgFillPaint(vg, bg);
			NanoVG.nvgFill(vg);
		}
		
		// Draw Prompt
		if ( getLength() == 0 && prompt != null && prompt.length() > 0 ) {
			int xx = (int) this.fakeBox.getX();
			int yy = (int) this.fakeBox.getY();
			
			// Setup font
			NanoVG.nvgFontSize(vg, fontSize);
			NanoVG.nvgFontFace(vg, Font.SANS.getFont(FontStyle.REGULAR));
			NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

			// Draw
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgFontBlur(vg,0);
			NanoVG.nvgFillColor(vg, Theme.current().getShadow().getNVG());
			NanoVG.nvgText(vg, xx, yy, prompt);
		}
		
		// Draw text
		this.internalScrollPane.setBackground(null);
		this.internalScrollPane.render(context);
		
		// internal selection graphic
		if (isDescendentSelected() && isSelectionOutlineEnabled()) {
			NanoVG.nvgTranslate(context.getNVG(), x, y);	
			Color sel = context.isFocused() ? selectionFill : selectionPassiveFill;
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
	
	float renderCaret = 0;
}
