package lwjgui.scene.control;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
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
	}
	
	public CodeArea() {
		this("");
	}
	
	@Override
	protected void position(Node parent) {
		
		// Normal positioning
		super.position(parent);
		
		// Only update if the amount of lines has changed
		lineCounter.update(this.getNumLines());
		
		// Position line counter
		this.internalScrollPane.setPadding(new Insets(internalScrollPane.getPadding().getTop(), internalScrollPane.getPadding().getRight(), internalScrollPane.getPadding().getBottom(), lineCounter.getWidth()+2));
		this.lineCounter.setLocalPosition(lineCounter.getParent(), 
				-internalScrollPane.getPadding().getLeft()-1, 
				(internalScrollPane.getContent().getY()-internalScrollPane.getPadding().getTop())-internalScrollPane.getY());
	}
	
	@Override
	public void render(Context context) {
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
			this.setBackground(null);
			this.setPrefWidth(0);
			this.flag_clip = false;
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
							NanoVG.nvgScissor(context.getNVG(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
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
			LWJGUIUtil.fillRect(context, getX()+1, CodeArea.this.getY()+1, getWidth(), CodeArea.this.getInnerBounds().getHeight()-2, Theme.current().getPane());
			LWJGUIUtil.fillRect(context, getX()+getWidth(), CodeArea.this.getY()+1, 1, CodeArea.this.getInnerBounds().getHeight()-2, Theme.current().getSelectionPassive());
			
			NanoVG.nvgScissor(context.getNVG(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
			
			super.render(context);
		}
	}
}
