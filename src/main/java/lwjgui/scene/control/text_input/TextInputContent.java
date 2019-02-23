package lwjgui.scene.control.text_input;

import java.util.ArrayList;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.IndexRange;
import lwjgui.scene.layout.Pane;

/**
 * Handles rendering the interal context (e.g. text) of TextInputControl.
 */
class TextInputContent extends Pane {
	
	private TextInputControl textInputControl;
	private Color caretFillCopy = null;
	
	public TextInputContent(TextInputControl textInputControl) {
		this.textInputControl = textInputControl;
		this.setMouseTransparent(true);
		this.setBackground(null);
		
		this.setAlignment(Pos.TOP_LEFT);
	}
	
	private long lastTime;
	@Override
	public void render(Context context) {
		super.render(context);
		
		if ( this.textInputControl.glyphData.size() == 0 ) {
			this.textInputControl.setText(this.textInputControl.getText());
		}
		
		this.clip(context);
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

				LWJGUIUtil.fillRect(context, getX() + xx, getY() + yy, width, height, this.textInputControl.selectionAltFill);
			}
		}
		
		// Draw text
		for (int i = 0; i < this.textInputControl.linesDraw.size(); i++) {
			int mx = (int)getX();
			int my = (int)getY() + (this.textInputControl.fontSize*i);
			
			// Quick bounds check
			if ( my < this.textInputControl.internalScrollPane.getY()-(this.textInputControl.fontSize*i))
				continue;
			if ( my > this.textInputControl.internalScrollPane.getY()+this.textInputControl.internalScrollPane.getHeight())
				continue;
			
			long vg = context.getNVG();
			String text = this.textInputControl.linesDraw.get(i);
			
			// Setup font
			this.textInputControl.bindFont();
			
			// Inefficient Draw. Thanks NanoVG refusing to implement \t
			if ( this.textInputControl.glyphData.size() > 0 ) {
				ArrayList<GlyphData> dat = this.textInputControl.glyphData.get(i);
				if ( dat.size() > 0 ) {
					float x = 0;
					
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
						
						// Get current x offset
						x = g.x();
						
						if ( draw ) {
							NanoVG.nvgBeginPath(vg);
							NanoVG.nvgFontBlur(vg,0);
							NanoVG.nvgFillColor(vg, this.textInputControl.fontFill.getNVG());
							NanoVG.nvgText(vg, mx+x, my, c);
						}
						
						//x += g.width();
					}
				}
			}
		}
		
		// Draw caret
		if ( this.textInputControl.editing ) {
			int line = this.textInputControl.getRowFromCaret(this.textInputControl.caretPosition);
			int index = this.textInputControl.getIndexFromCaret(this.textInputControl.caretPosition);
			int cx = (int) (getX()-1);
			int cy = (int) (getY() + (line * this.textInputControl.fontSize));
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
	}
}