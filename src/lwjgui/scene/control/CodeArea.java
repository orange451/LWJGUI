package lwjgui.scene.control;

import lwjgui.Color;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.VBox;
import lwjgui.theme.Theme;

public class CodeArea extends TextArea {
	private VBox lineCounter;
	
	public CodeArea() {
		
		this.setPreferredColumnCount(20);
		this.setPreferredRowCount(10);
		this.setFont(Font.CONSOLAS);
		this.setFontSize(13);
		
		this.lineCounter = new LineCounter();
		
		this.fakeBox = new CodeAreaContent();
		this.internal.setContent(fakeBox);
		
	}
	
	int lastLines = -1;
	@Override
	protected void position(Node parent) {
		int lines = this.lines();
		if ( lines != lastLines ) {
			lastLines = lines;
			
			this.lineCounter.getChildren().clear();
			this.lineCounter.setMaxWidth(0);
			this.lineCounter.position(fakeBox);
			this.lineCounter.setMaxWidth(Integer.MAX_VALUE);
			
			for (int i = 0; i < lines; i++) {
				Label l = new Label(" "+(i+1));
				l.setFontSize(fontSize);
				l.setFont(Font.COURIER);
				lineCounter.getChildren().add(l);
			}
			
			if ( !this.internal.internalPane.getChildren().contains(lineCounter) )
				this.internal.internalPane.getChildren().add(lineCounter);
		}
		
		super.position(parent);
		
		double wid = lineCounter.getWidth()+2;
		this.lineCounter.setPrefHeight(fakeBox.getHeight());
		this.internal.setPadding(new Insets(internal.getPadding().getTop(), internal.getPadding().getRight(), internal.getPadding().getBottom(), wid));
		this.lineCounter.setAbsolutePosition(getAbsoluteX(), fakeBox.getAbsoluteY());
	}
	
	@Override
	public void render(Context context) {
		super.render(context);
		
		this.clip(context);
		lineCounter.render(context);
	}
	
	class CodeAreaContent extends TextAreaContent {
		@Override
		public void render(Context context) {
			// Draw current selected line
			if ( editing ) {
				Color c1 = Theme.currentTheme().getSelection();
				Color c2 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 32);
				double xx = getAbsoluteX();
				double yy = getAbsoluteY()+getRowFromCaret(getCaretPosition())*fontSize;
				LWJGUIUtil.fillRect(context, xx+1, yy, CodeArea.this.getWidth()-2-(xx-CodeArea.this.getAbsoluteX()), fontSize, c2);
			}
			
			// Render normal text
			super.render(context);
			
			// Draw line coutner background
			LWJGUIUtil.fillRect(context, lineCounter.getAbsoluteX()+1, CodeArea.this.getAbsoluteY()+1, lineCounter.getWidth(), CodeArea.this.getHeight()-2, Color.LIGHT_GRAY);
		}
	}
	
	class LineCounter extends VBox {
		public LineCounter() {
			this.setFillToParentWidth(false);
			this.setFillToParentHeight(true);
			this.setMouseTransparent(true);
			this.setPadding(new Insets(0,0,0,0));
			this.setAlignment(Pos.TOP_LEFT);
			this.setBackground(null);
			this.setPrefWidth(0);
		}
	}
}
