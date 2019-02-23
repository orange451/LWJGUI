package lwjgui.scene.control.text_input;

import lwjgui.Color;
import lwjgui.LWJGUIUtil;
import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.VBox;
import lwjgui.theme.Theme;

public class CodeArea extends TextArea {
	private LineCounterNode lineCounter;
	
	public CodeArea(TextInputScrollPane internalScrollPane, String text) {
		super(internalScrollPane, text);
		
		this.setFont(Font.CONSOLAS);
		this.setFontSize(14);
		
		// Replace content window with our custom one
		this.fakeBox = new CodeAreaContent(this);
		this.internalScrollPane.setContent(fakeBox);

		// Add line counter to scrollpane
		this.lineCounter = new LineCounterNode();
		this.fakeBox.getChildren().add(lineCounter);
	}
	
	public CodeArea(String text) {
		this(new TextInputScrollPane(), text);
	}
	
	public CodeArea() {
		this("");
	}
	
	@Override
	protected void position(Node parent) {
		
		// Only update if the amount of lines has changed
		lineCounter.update(this.lines());
		
		// Normal positioning
		super.position(parent);
		
		// Make sure line counter is on the left side of the area.
		this.internalScrollPane.setPadding(new Insets(internalScrollPane.getPadding().getTop(), internalScrollPane.getPadding().getRight(), internalScrollPane.getPadding().getBottom(), lineCounter.getWidth()+2));
		double a = this.internalScrollPane.getX()+this.internalScrollPane.getPadding().getLeft();
		double b = this.internalScrollPane.getContent().getX();
		double c = b-a;
		this.lineCounter.offset(-internalScrollPane.getPadding().getLeft()-c, 0);
		this.lineCounter.updateChildren();
	}
	
	@Override
	public void render(Context context) {
		super.render(context);
		
		this.clip(context);
		lineCounter.render(context);
	}
	
	class CodeAreaContent extends TextInputContent {
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
			this.setBackground(null);
			this.setPrefWidth(0);
		}
		
		public void update(int lines) {
			if ( lines != lastLines ) {
				lastLines = lines;
				
				// Add line labels in
				getChildren().clear();
				for (int i = 0; i < lines; i++) {
					Label l = new Label(" "+(i+1));
					l.setFontSize(fontSize);
					l.setFont(Font.COURIER);
					getChildren().add(l);
				}
			}
		}
		
		@Override
		public void render( Context context ) {
			// Draw line counter background
			LWJGUIUtil.fillRect(context, getX()+1, CodeArea.this.getY()+1, getWidth(), CodeArea.this.getInnerBounds().getHeight()-2, Theme.current().getPane());
			LWJGUIUtil.fillRect(context, getX()+getWidth(), CodeArea.this.getY()+1, 1, CodeArea.this.getInnerBounds().getHeight()-2, Theme.current().getSelectionPassive());
			
			super.render(context);
		}
	}
}
