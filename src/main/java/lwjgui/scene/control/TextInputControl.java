package lwjgui.scene.control;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.collections.StateStack;
import lwjgui.event.Event;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.event.KeyEvent;
import lwjgui.event.TypeEvent;
import lwjgui.font.Font;
import lwjgui.font.FontMetaData;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.glfw.input.MouseHandler;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Cursor;
import lwjgui.scene.Node;
import lwjgui.scene.WindowManager;
import lwjgui.scene.layout.Pane;
import lwjgui.style.Background;
import lwjgui.style.BackgroundSolid;
import lwjgui.style.BlockPaneRenderer;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.theme.Theme;

/**
 * This class acts as the core controller for text input classes.
 */
public abstract class TextInputControl extends Control implements BlockPaneRenderer {
	ArrayList<String> lines;
	ArrayList<ArrayList<GlyphData>> glyphData;
	ArrayList<String> linesDraw;
	private String source = "";
	int caretPosition;
	protected boolean editing = false;
	protected boolean editable = true;
	
	private Background background;
	private Color borderColor;
	private float[] borderRadii;
	private BorderStyle borderStyle;
	private ObservableList<Background> backgrounds = new ObservableList<>();
	private ObservableList<BoxShadow> boxShadows = new ObservableList<>();
	
	private boolean wordWrap;
	
	int selectionStartPosition;
	int selectionEndPosition;
	
	protected TextInputScrollPane internalScrollPane;
	protected TextInputContentRenderer internalRenderingPane;
	
	private StateStack<TextState> undoStack;
	private boolean forceSaveState;

	private EventHandler<Event> onSelectEvent;
	private EventHandler<Event> onDeselectEvent;
	private EventHandler<Event> onTextChange;
	
	private String prompt = null;
	
	private TextParser textParser;

	private static final int MAX_LINES = Integer.MAX_VALUE;
	
	/*
	 * Visual customization
	 */
	protected int fontSize = 16;
	protected Font font = Font.SANS;
	protected Color fontFill = Theme.current().getText();
	protected FontStyle style = FontStyle.REGULAR;
	
	private boolean selectionOutlineEnabled = true;
	
	Color caretFill = Theme.current().getText();
	boolean caretFading = false;
	
	private Color selectionFill = Theme.current().getSelection();
	private Color selectionPassiveFill = Theme.current().getSelectionPassive();
	Color selectionAltFill = Theme.current().getSelectionAlt();
	private Color promptFill = Theme.current().getText().alpha(0.4f);
	
	protected TextInputControlShortcuts shortcuts;
	
	public TextInputControl() {
		this.setBorderStyle(BorderStyle.SOLID);
		this.setBorderRadii(3);
		this.setBackground(new BackgroundSolid(Theme.current().getBackground()));
		
		/*
		 * Input setup
		 */
		this.internalRenderingPane = new TextInputContentRenderer(this);
		
		/*
		 * Scroll Pane setup
		 */
		
		internalScrollPane = new TextInputControl.TextInputScrollPane();
		internalScrollPane.setContent(internalRenderingPane);
		children.add(internalScrollPane);
		
		undoStack = new StateStack<TextState>();
		setText("");
		saveState();
		this.flag_clip = false;
		
		setOnTextInputInternal( new EventHandler<TypeEvent>() {
			@Override
			public void handle(TypeEvent event) {
				if (!editing) return;
				
				if ( forceSaveState ) {
					saveState();
					forceSaveState = false;
				}
				deleteSelection();
				insertText(caretPosition, event.getCharacterString());
				setCaretPosition(caretPosition+1);
				deselect();
				
				// If you press space then Save history
				if (event.character == ' ') {
					saveState();
				}
			}
		});
		
		shortcuts = new TextInputControlShortcuts();
		setOnKeyPressedAndRepeatInternal( event -> {
			if ( !this.isEditing() )
				return;
			shortcuts.process(this, event);
		});
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
		saveState();
		setText("");
		deselect();
	}
	
	public void deselect() {
		this.selectionStartPosition = caretPosition;
		this.selectionEndPosition = caretPosition;
	}
	
	public void setPrompt(String string) {
		this.prompt = string;
	}
	
	public void setText(String text) {
		if (text == null)
			return;
		
		boolean changed = true;
		if ( this.source != null && this.source.equals(text) )
			changed = false;
		
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
		String trail = "[!$*]T!R@A#I$L%I^N&G[!$*]"; // Naive fix to allow trailing blank lines to still be parsed
		text = text.replace("\r", "");
		this.source = text;

		String temp = text + trail; // Add tail
		String[] split = temp.split("\n");
		for (int i = 0; i < split.length; i++) {
			String tt = split[i];
			tt = tt.replace(trail, ""); // Remove tail
			if (i < split.length - 1) {
				tt += "\n";
			}
			addRow(tt);
		}
		setCaretPosition(oldCaret);
		
		// Fire on text change event
		if ( onTextChange != null && changed ) {
			EventHelper.fireEvent(onTextChange, new Event());
		}
	}
	
	private void addRow(String originalText) {
		String drawLine = originalText;
		
		ArrayList<GlyphData> glyphEntry = new ArrayList<GlyphData>();
		bindFont();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			NVGGlyphPosition.Buffer positions;
			if (drawLine.length() > 0) {
				positions = NVGGlyphPosition.mallocStack(drawLine.length(), stack);
			} else {
				positions = NVGGlyphPosition.mallocStack(1, stack);
			}

			// Create glyph data for each character in the line
			NanoVG.nvgTextGlyphPositions(window.getContext().getNVG(), 0, 0, drawLine, positions);
			int j = 0;
			while (j < drawLine.length()) {
				GlyphData currentGlyph = fixGlyph(positions.get(), drawLine.substring(j, j + 1));
				glyphEntry.add(currentGlyph);
				j++;
			}
			
			// Add blank glyph to end of line
			GlyphData last = glyphEntry.size()== 0 ? new GlyphData(0,0,"") : glyphEntry.get(glyphEntry.size()-1);
			glyphEntry.add(new GlyphData( last.x()+last.width(), 1, "" ));
			
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
		}
		
		// Word Wrap not yet properly implemented properly. Will be rewritten.
		/*int vWid = (int) (this.internalScrollPane.getViewport().getWidth() - 20);
		int maxWidth = (int) (wordWrap?vWid:Integer.MAX_VALUE);
		int index = 0;
		while ( index < originalText.length() ) {
			GlyphData entry = glyphEntry.get(index);
			if ( entry.x()+entry.width() >= maxWidth ) {
				addRow(originalText.substring(0, index-1));
				addRow(originalText.substring(index-1,originalText.length()));
				return;
			}
			
			index++;
		}*/
		
		glyphData.add(glyphEntry);
		
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
		saveState();
		insertText(getLength(), text);
		
		internalScrollPane.scrollToBottom();
	}
	
	public void insertText(int index, String text) {
		String before = getText(0, index);
		String after = getText(index, getLength());
		setText(before + text + after);
		deselect();
	}
	
	public void setSelection(IndexRange range) {
		this.selectionStartPosition = range.getStart();
		this.selectionEndPosition = range.getEnd();
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
		
		String before = getText(0, range.getStart());
		String after = getText(range.getEnd(), getLength());

		saveState();
		setText(before+after);
		deselect();
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

	public void setOnSelected( EventHandler<Event> event ) {
		this.onSelectEvent = event;
	}
	
	public void setOnDeselected( EventHandler<Event> event ) {
		this.onDeselectEvent = event;
	}
	
	public void setOnTextChange( EventHandler<Event> event ) {
		this.onTextChange = event;
	}
	
	public void undo() {
		if ( this.undoStack.isCurrent() ) {
			this.saveState();
			this.undoStack.Rewind(); // Extra undo, since we just secretly saved. SHHHH
		}
		TextState state = this.undoStack.Rewind();
		if ( state == null )
			return;
		setText(state.text);
		deselect();
		this.setCaretPosition(state.caretPosition);
		forceSaveState = true;
	}
	
	public void redo() {
		TextState state = this.undoStack.Forward();
		if ( state == null )
			return;
		//this.undoStack.Rewind(); // Go back one more, since setting text will overwrite
		setText(state.text);
		deselect();
		this.setCaretPosition(state.caretPosition);
	}
	
	public void copy() {
		String text = getSelectedText();
		
		WindowManager.runLater(() -> {
			GLFW.glfwSetClipboardString(window.getID(), text);
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
		WindowManager.runLater(()-> {
			String str = GLFW.glfwGetClipboardString(window.getID());
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
		
		return source.substring(
				Math.max(0, selection.getStart()),
				Math.min(selection.getEnd(),source.length())
		);
		
		/*
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
		
		return text;*/
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
	
	/**
	 * @return the total number of lines in this text area.
	 */
	public int getNumLines() {
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
		/*int len = 0;
		for (int i = 0; i < lines.size(); i++) {
			len += lines.get(i).length();
		}
		return len;*/
		return source.length();
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
	
	/**
	 * Returns the text of the line at the specified caret position.
	 * @param caretPosition
	 * @return
	 */
	public String getLine(int caretPosition) {
		return lines.get(this.getRowFromCaret(caretPosition));
	}
	
	public void setTextParser(TextParser parser) {
		this.textParser = parser;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isEditing() {
		return this.editing;
	}
	
	public double getViewportWidth() {
		return this.internalScrollPane.getViewportWidth();
	}
	
	public double getViewportHeight() {
		return this.internalScrollPane.getViewportHeight();
	}
	
	@Override
	protected void position(Node parent) {
		defaultStyle();
		super.position(parent);
		
		//this.internalScrollPane.position(this);
		//this.internalScrollPane.setAbsolutePosition(getX()+this.getInnerBounds().getX(), getY()+this.getInnerBounds().getY());
		//this.internalScrollPane.setPrefSize(this.getInnerBounds().getWidth(), this.getInnerBounds().getHeight());
		//this.internalScrollPane.updateChildren();
	}
	
	private void defaultStyle() {
		// SETUP OUTLINE
		this.setBorderStyle(BorderStyle.SOLID);
		this.setBorderWidth(1);
		Color outlineColor = (this.isDescendentSelected()&&!this.isDisabled())? selectionFill : Theme.current().getControlOutline();
		this.setBorderColor(outlineColor);
		
		// SETUP SELECTION GRAPHIC
		this.getBoxShadowList().clear();
		this.getBoxShadowList().add(new BoxShadow( 0, 2, 8, -3, Theme.current().getShadow(), true ));
		if (isDescendentSelected() && !this.isDisabled()) {
			Color sel = Theme.current().getSelection();
			if ( isDisabled() )
				sel = Theme.current().getSelectionPassive();

			this.getBoxShadowList().add(new BoxShadow(0, 0, 4, 0, sel.alpha(0.8f)));
			this.getBoxShadowList().add(new BoxShadow(0, 0, 1.5f, 2, sel.alpha(0.2f), true));
		}
	}
	
	@Override
	protected void resize() {
		this.setAlignment(Pos.TOP_LEFT);
		this.internalScrollPane.setPrefSize(this.getInnerBounds().getWidth(), this.getInnerBounds().getHeight());

		int width = getMaxTextWidth();
		this.internalRenderingPane.setMinSize(width, lines.size()*fontSize);
		
		super.resize();
		
		// Force select this if rendering pane or scroll pane is selected
		if ( this.internalRenderingPane.isSelected() || this.internalScrollPane.isSelected() || this.internalScrollPane.getViewport().isSelected() ) {
			window.getContext().setSelected(this);
		}
		
		//TODO: Move htis into an actual input callback
		if ( (this.isSelected() || this.isDescendentSelected()) && editable && !this.isDisabled() ) {
			if ( !editing && onSelectEvent != null ) {
				EventHelper.fireEvent(onSelectEvent, new Event());
			}
			editing = true;
		} else {
			if ( editing ) {
				if ( onDeselectEvent != null ) {
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
		float width = 0;
		for (int i = 0; i < linesDraw.size(); i++) {
			String str = linesDraw.get(i);
			float len = 0;
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
		
		return (int) width;
	}
	
	protected void bindFont() {
		long vg = window.getContext().getNVG();
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, font.getFont(style));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
	}
	
	protected void bindFont(FontMetaData data) {
		double fs = data.getSize()==null?fontSize:data.getSize().doubleValue();
		Font f = data.getFont()==null?font:data.getFont();
		FontStyle fst = data.getStyle()==null?style:data.getStyle();
		
		long vg =  window.getContext().getNVG();
		NanoVG.nvgFontSize(vg, (float)fs);
		NanoVG.nvgFontFace(vg, f.getFont(fst));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
	}
	
	protected GlyphData fixGlyph(NVGGlyphPosition glyph, String originalCharacter) {
		if ( originalCharacter.equals("\t") )
			return new GlyphData( glyph.x(), 32, originalCharacter, true );
		
		if ( originalCharacter.equals("\r") ) {
			new GlyphData( glyph.x(), 0, originalCharacter );
		}
		
		return new GlyphData( glyph.x(), glyph.maxx()-glyph.x(), originalCharacter );
	}
	
	protected int getCaretFromRowLine(int row, int index) {
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
		
		float temp = 0;
		for (int i = 0; i < offset; i++) {
			GlyphData g = glyphData.get(row).get(i);
			temp += g.width();
		}
		
		return (int) temp;
	}
	
	int getCaretFromPixelOffset( int row, int pixelX ) {
		if (linesDraw.size() == 0)
			return 0;
		String line = linesDraw.get(row);
		
		if ( line.length() == 0 )
			return getCaretFromRowLine(row,0);
		
		// Find first character clicked in row
		int index = 0;
		ArrayList<GlyphData> glyphLine = glyphData.get(row);
		for (int i = 0; i < glyphLine.size(); i++) {
			GlyphData dat = glyphLine.get(i);
			if ( dat.character().equals("\n"))
				break;
			if ( dat.x()+dat.width()/2 > pixelX )
				break;
			index++;
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
		MouseHandler mh = window.getMouseHandler();
		double mx = mh.getX()-(internalScrollPane.getContent().getX());
		double my = mh.getY()-(internalScrollPane.getContent().getY());
		
		// Find row clicked
		int row = (int) (my / (float)fontSize);
		if ( row > lines.size()-1 )
			row = lines.size()-1;
		if ( row < 0 )
			row = 0;
		return getCaretFromPixelOffset(row, (int) mx);
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
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */	
	public void setBackground(Background color) {
		this.background = color;
	}
	
	/**
	 * Get the current background color of this node.
	 * @return
	 */
	public Background getBackground() {
		return this.background;
	}

	/**
	 * Get list of backgrounds.
	 */
	@Deprecated
	public ObservableList<Background> getBackgrounds() {
		return this.backgrounds;
	}
	
	@Override
	public void setBorderStyle(BorderStyle style) {
		this.borderStyle = style;
	}

	@Override
	public BorderStyle getBorderStyle() {
		return this.borderStyle;
	}

	@Override
	public float[] getBorderRadii() {
		return borderRadii;
	}

	@Override
	public void setBorderRadii(float radius) {
		this.setBorderRadii(radius, radius, radius, radius);
	}

	@Override
	public void setBorderRadii(float[] radius) {
		this.setBorderRadii(radius[0], radius[1], radius[2], radius[3]);
	}

	@Override
	public void setBorderRadii(float cornerTopLeft, float cornerTopRight, float cornerBottomRight, float cornerBottomLeft) {
		this.borderRadii = new float[] {cornerTopLeft, cornerTopRight, cornerBottomRight, cornerBottomLeft};
	}

	@Override
	public void setBorderColor(Color color) {
		this.borderColor = color;
	}

	@Override
	public Color getBorderColor() {
		return this.borderColor;
	}

	@Override
	public ObservableList<BoxShadow> getBoxShadowList() {
		return this.boxShadows;
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
		if ( !isVisible() )
			return;

		this.clip(context,8);
		
		// Apply CSS
		this.stylePush();
		{
			// Render standard pane
			BlockPaneRenderer.render(context, this);
			
			// Draw sub nodes
			this.internalScrollPane.setBackground(null);
			this.internalScrollPane.setBorderStyle(BorderStyle.NONE);
			super.render(context);
		}
		this.stylePop();
		
		if ( context == null )
			return;
		
		long vg = context.getNVG();
		// Draw Prompt
		if ( getLength() == 0 && prompt != null && prompt.length() > 0 ) {
			int xx = (int) (this.internalRenderingPane.getX());
			int yy = (int) (this.internalRenderingPane.getY());
			
			// Setup font
			NanoVG.nvgFontSize(vg, fontSize);
			NanoVG.nvgFontFace(vg, Font.SANS.getFont(FontStyle.REGULAR));
			NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

			// Draw
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgFontBlur(vg,0);
			NanoVG.nvgFillColor(vg, promptFill.getNVG());
			NanoVG.nvgText(vg, xx, yy, prompt);
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
	
	class GlyphData { // This class could be avoided if NanoVG author wouldn't ignore me.
		float width;
		float x;
		private String c;
		boolean SPECIAL;
		
		public GlyphData( float x, float width, String car ) {
			this.c = car;
			this.x = x;
			this.width = width;
		}
		
		public GlyphData( float x, float width, String car, boolean special ) {
			this( x, width, car );
			this.SPECIAL = special;
		}
		
		public String character() {
			return c;
		}
		
		public float x() {
			return x;
		}
		
		public float width() {
			return width;
		}
	}
	
	abstract class TextParser {
		public abstract String parseText(String input);
	}
	
	float renderCaret = 0;
	long lastClickTime = 0;
	long lastLastClickTime = 0;
	int DOUBLE_CLICK_SPEED = 225; // Time between clicks, in milliseconds.
	int TRIPLE_CLICK_SPEED = 650; // Time between all clicks, in milliseconds.
	
	class TextInputScrollPane extends ScrollPane {

		public TextInputScrollPane() {
			setFillToParentHeight(true);
			setFillToParentWidth(true);
			setVbarPolicy(ScrollBarPolicy.NEVER);
			setHbarPolicy(ScrollBarPolicy.NEVER);
			
			// Set padding of viewport
			this.internalCanvas.setPadding(new Insets(3,4,4,3));

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
				long clickTime = System.currentTimeMillis();

				if ( event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && clickTime - lastClickTime > DOUBLE_CLICK_SPEED ) {
					window.getContext().setSelected(getViewport());
					
					//Sets caret position at mouse
					setCaretPosition(getCaretAtMouse());
					selectionEndPosition = caretPosition;
					
					if ( !window.getKeyboardHandler().isShiftPressed() ) {
						selectionStartPosition = caretPosition;
					}
				} else if ( event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && clickTime - lastClickTime <= DOUBLE_CLICK_SPEED ) {

					if (clickTime - lastLastClickTime <= TRIPLE_CLICK_SPEED && lastLastClickTime != 0) {
						// Triple clicked.
						String line = lines.get(getRowFromCaret(caretPosition));
						int rowStart = getCaretFromRowLine(getRowFromCaret(caretPosition), 0);

						int lineLength = line.length();

						if (line.charAt(lineLength-1) == '\n' || line.charAt(lineLength-1) == '\r') {
							lineLength--;
						}

						int rowEnd = rowStart + lineLength;

						selectionStartPosition = rowStart;
						selectionEndPosition = rowEnd;

						lastLastClickTime = 0;
					} else {
						lastLastClickTime = lastClickTime;
						// Double clicked.
						setCaretPosition(getCaretAtMouse());
						selectionEndPosition = caretPosition;

						String line = lines.get(getRowFromCaret(caretPosition));

						int caretIndex = getIndexFromCaret(caretPosition);

						Pattern pattern = Pattern.compile("\\w+|\\d+");
						Matcher matcher = pattern.matcher(line);

						while (matcher.find()) {
							if (matcher.start() <= caretIndex && matcher.end() >= caretIndex) {
								selectionStartPosition = caretPosition - (caretIndex - matcher.start());
								selectionEndPosition = caretPosition + (matcher.end() - caretIndex);
								break;
							}
						}
					}

					setCaretPosition(selectionEndPosition);
				} else if ( event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT ) {
					if ( TextInputControl.this.getContextMenu() != null ) {
						TextInputControl.this.getContextMenu().show(this.getScene(), event.getMouseX(), event.getMouseY());
					}
				}

				lastClickTime = clickTime;
			});
			
			// Drag mouse
			getViewport().setOnMouseDragged(event -> {
				caretPosition = getCaretAtMouse();
				selectionEndPosition = caretPosition;

				lastClickTime = 0;
				lastLastClickTime = 0;
			});
		}
		
		public void scrollToBottom() {
			setVvalue(1.0);
		}

		protected Pane getViewport() {
			return internalCanvas;
		}
	}
	
	/**
	 * Handles rendering the interal context (e.g. text) of TextInputControl.
	 */
	class TextInputContentRenderer extends Pane {
		
		private TextInputControl textInputControl;
		private Color caretFillCopy = null;
		
		public TextInputContentRenderer(TextInputControl textInputControl) {
			this.textInputControl = textInputControl;
			this.setMouseTransparent(true);
			
			this.setAlignment(Pos.TOP_LEFT);
			
			this.flag_clip = false;
		}

		@Override
		public String getElementType() {
			return "textcontentpane";
		}
		
		private long lastTime;
		@Override
		public void render(Context context) {
			if ( !isVisible() )
				return;
			
			double startX = this.getX();// + internalScrollPane.getViewport().getInnerBounds().getX();
			double startY = this.getY();// + internalScrollPane.getViewport().getInnerBounds().getY();
			
			if (textInputControl.glyphData.size() == 0) {
				textInputControl.setText(textInputControl.getText());
			}
			
			this.clip(context, -1);
			this.textInputControl.renderCaret += lastTime-System.currentTimeMillis();
			lastTime = System.currentTimeMillis();
			
			// Render selection
			IndexRange range = this.textInputControl.getSelection();
			range.normalize();
			int len = range.getLength();
			int startLine = this.textInputControl.getRowFromCaret(range.getStart());
			int endLine = this.textInputControl.getRowFromCaret(range.getEnd());
			int a = this.textInputControl.getIndexFromCaret(range.getStart());
			int b = this.textInputControl.getIndexFromCaret(range.getEnd());
			if ( len > 0 ) {
				for (int i = startLine; i <= endLine; i++) {
					String l = this.textInputControl.lines.get(i);
					
					int left = a;
					int right = b;
					
					if ( i != endLine )
						right = l.length()-1;
					if ( i != startLine )
						left = 0;

					int xx = (int)(this.textInputControl.glyphData.get(i).get(left).x());
					int yy = (int)(this.textInputControl.fontSize*i);
					int height = this.textInputControl.fontSize;
					int width = (int) (this.textInputControl.glyphData.get(i).get(right).x()-xx);

					LWJGUIUtil.fillRect(context, startX + xx, startY + yy, width, height, this.textInputControl.selectionAltFill);
				}
			}
			
			// Draw text
			synchronized(this.textInputControl.linesDraw) {
				for (int i = 0; i < this.textInputControl.linesDraw.size(); i++) {
					if ( i >= this.textInputControl.linesDraw.size() )
						continue;
					
					int mx = (int)(startX);
					int my = (int)(startY) + (this.textInputControl.fontSize*i);
					
					// Quick bounds check
					if ( my < this.textInputControl.internalScrollPane.getY()-(this.textInputControl.fontSize*i))
						continue;
					if ( my > this.textInputControl.internalScrollPane.getY()+this.textInputControl.internalScrollPane.getHeight())
						continue;
					
					String text = this.textInputControl.linesDraw.get(i);
					
					// Setup font
					this.textInputControl.bindFont();
					
					// Inefficient Draw. Thanks NanoVG refusing to implement \t Very cool
					if ( this.textInputControl.glyphData.size() > 0 ) {
						ArrayList<GlyphData> dat = this.textInputControl.glyphData.get(i);
						if ( dat.size() > 0 ) {
							
							for (int j = 0; j < text.length(); j++) {
								boolean draw = true;
								String c = text.substring(j, j+1);
								
								// Manual fix for drawing boxes of special characters of certain fonts
								// NanoVG author ALSO refuses to fix this. Cheers.
								if ( c.length() == 1 ) {
									if ( c.charAt(0) < 32 )
										draw = false;
								}
								GlyphData g = dat.get(j);
								
								if ( draw ) {
									final int currentPosition = textInputControl.getCaretFromRowLine(i, j);
									TextHighlighter highlight = textInputControl.getHighlighting(currentPosition);
									Color color = textInputControl.fontFill;
									Color background = null;
									if ( highlight == null ) {
										textInputControl.bindFont();
									} else {
										textInputControl.bindFont(highlight.getMetaData());
										if ( highlight.getMetaData().getColor() != null )
											color = highlight.getMetaData().getColor();
										
										background = highlight.getMetaData().getBackground();
									}
									if ( context == null )
										continue;
									long vg = context.getNVG();
									
									// Fill a background behind the letter if necessary.
									if ( background != null ) {
										float wid = g.width();
										if ( j < text.length() - 1 ) {
											GlyphData nextGlyph = dat.get(j+1);
											wid = nextGlyph.x()-g.x();
										}
										
										// NVG Background
										NanoVG.nvgBeginPath(context.getNVG());
										NanoVG.nvgRect(context.getNVG(), mx+g.x(), my, wid, (int)this.textInputControl.fontSize);
										NanoVG.nvgFillColor(context.getNVG(), background.getNVG());
										NanoVG.nvgFill(context.getNVG());
										NanoVG.nvgClosePath(context.getNVG());
									}
									
									// Draw character
									NanoVG.nvgBeginPath(vg);
									NanoVG.nvgFontBlur(vg,0);
									NanoVG.nvgFillColor(vg, color.getNVG());
									NanoVG.nvgText(vg, mx+g.x(), my, c);
								}
							}
						}
					}
				}
			}
			
			// Draw caret
			if ( this.textInputControl.editing ) {
				int line = this.textInputControl.getRowFromCaret(this.textInputControl.caretPosition);
				int index = this.textInputControl.getIndexFromCaret(this.textInputControl.caretPosition);
				int cx = (int) (startX-1);
				int cy = (int) (startY + (line * this.textInputControl.fontSize));
				if ( this.textInputControl.glyphData.size() > 0 ) {
					
					// Check if caret goes past the line
					boolean addWid = false;
					while ( index >= this.textInputControl.glyphData.get(line).size() ) {
						index--;
						addWid = true;
					}

					// Get current x offset
					float offsetX = this.textInputControl.glyphData.get(line).get(index).x();
					if ( addWid ) {
						offsetX += this.textInputControl.glyphData.get(line).get(index).width();
					}
					
					if (this.textInputControl.caretFading) {
						if (caretFillCopy == null) {
							caretFillCopy = this.textInputControl.caretFill.copy();
						} else if (!caretFillCopy.rgbMatches(this.textInputControl.caretFill)){
							caretFillCopy.set(this.textInputControl.caretFill);
						}

						float alpha = 1.0f-(float) (Math.sin(this.textInputControl.renderCaret * 0.004f)*0.5+0.5);
						LWJGUIUtil.fillRect(context, cx+offsetX, cy, 2, this.textInputControl.fontSize, caretFillCopy.alpha(alpha));
					} else if ( Math.sin(this.textInputControl.renderCaret*1/150f) < 0 ) {
						LWJGUIUtil.fillRect(context, cx+offsetX, cy, 2, this.textInputControl.fontSize, this.textInputControl.caretFill);
					}
				}
			}
			
			super.render(context);
		}
	}
	
	/**
	 * Handles the special key shortcuts of TextInputControl (e.g. CTRL-V to paste)
	 */
	public class TextInputControlShortcuts {

		public void process(TextInputControl tic, KeyEvent event) {
			
			// Return if consumed
			if (event.isConsumed())
				return;
			
			// Select All
			if (event.key == GLFW.GLFW_KEY_A && event.isCtrlDown && tic.isDescendentSelected()) {
				tic.selectAll();
				event.consume();
			}
			
			// Copy
			if (event.key == GLFW.GLFW_KEY_C && event.isCtrlDown && tic.isDescendentSelected() ) {
				tic.copy();
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
			}
			
			
			
			// These require us to be editing...
			if (tic.editing) {
			
				// Backspace
				if (event.key == GLFW.GLFW_KEY_BACKSPACE) {
					tic.deletePreviousCharacter();
					event.consume();
				}
				
				// Delete
				if (event.key == GLFW.GLFW_KEY_DELETE ) {
					tic.deleteNextCharacter();
					event.consume();
				}
				
				// Paste
				if (event.key == GLFW.GLFW_KEY_V && event.isCtrlDown ) {
					tic.paste();
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
				
				// Normal Redo
				if ( event.key == GLFW.GLFW_KEY_Y && event.isCtrlDown ) {
					tic.redo();
					event.consume();
				}
			}
		}
	}
}
