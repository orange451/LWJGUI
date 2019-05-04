package lwjgui.scene.control;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.collections.ObservableList;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.HPos;
import lwjgui.geometry.VPos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public abstract class Labeled extends Control {
	private Node graphic;
	private String text = "";
	private String useString = "";
	private float fontSize = 18;
	private Font font = Font.SANS;
	private FontStyle fontStyle = FontStyle.REGULAR;
	private Color textColor;
	
	private ContentDisplay contentDisplay = ContentDisplay.LEFT;
	private double contentGap = 4;
	
	private static final String ELIPSES = "\u2026";

	public Labeled() {
		textColor = Theme.current().getText();
		this.flag_clip = false;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setFontSize( float size ) {
		if ( size == this.fontSize )
			return;
		
		this.fontSize = size;
	}

	public void setFontStyle( FontStyle style ) {
		if ( fontStyle != null && style.equals(this.fontStyle) )
			return;
		
		this.fontStyle = style;
	}
	
	/**
	 * Cannot add children to the label publicly.
	 */
	public ObservableList<Node> getChildren() {
		if ( graphic == null )
			return new ObservableList<Node>();
		
		return new ObservableList<Node>(graphic);
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	public void setGraphic( Node graphic ) {
		if ( this.graphic != null )
			this.children.remove(this.graphic);
		
		this.graphic = graphic;
		if ( graphic != null ) {
			this.children.add(graphic);
		}
	}
	
	public void setGraphicTextGap( double gap ) {
		this.contentGap = gap;
	}
	
	public void setContentDisplay( ContentDisplay display ) {
		if ( display == null )
			display = ContentDisplay.LEFT;
		
		this.contentDisplay = display;
	}
	
	@Override
	protected void resize() {
		super.resize();
		
		// Change label size if it's too large for its container (cut off text)
		if (this.getParent() != null && cached_context != null) {
			useString = text;
			
			// Get max width of parent element
			double graphicWid = graphic == null ? 0 : (graphic.getWidth()+contentGap);
			
			// Get some text bounds
			float[] elipBnd = font.getTextBounds( cached_context, ELIPSES, fontStyle, fontSize, garbage);
			double curWid = getTextWidth() + this.getPadding().getWidth();
			
			// Set initial size
			size.x = Math.min( Math.max(curWid, size.x), getMaxWidth() );
			
			// If we're too large for the parent element...
			if ( this.size.x > this.getAvailableSize().x ) {
				
				// Add ellipse width to string width
				float eWid = elipBnd[2]-elipBnd[0];
				curWid += eWid;
				
				// While we're too large, remove text off the end and replace with elipses
				int remove = 0;
				while ( (curWid > this.getAvailableSize().x) && (remove < text.length()) ) {
					remove++;
					useString = useString.substring(0, text.length()-remove)+ELIPSES;
					float[] bounds = font.getTextBounds( cached_context, useString, fontStyle, fontSize, garbage);
					curWid = (bounds[2]-bounds[0])+graphicWid;
				}
				this.size.x = curWid;
			}
			
			// Compute preferred height
			float[] bounds = font.getTextBounds(cached_context, useString, fontStyle, fontSize, garbage);
			double hei = (bounds[3] - bounds[1]) + this.padding.getHeight();
			if ( contentDisplay.equals(ContentDisplay.BOTTOM) || contentDisplay.equals(ContentDisplay.TOP))
				hei += getGraphicHeightInternalUse();
			this.size.y = hei;
		}
	}
	
	@Override
	public void render(Context context) {
		// get Absolute position
		long vg = context.getNVG();
		int absX = (int)(getX() + this.padding.getLeft());
		int absY = (int)(getY() + this.getPadding().getTop());// + (this.getInnerBounds().getHeight()/2f-textHeight/2f));

		// Get width of graphic
		double gWid = getGraphicWidthInternalUse();
		double gHei = getGraphicHeightInternalUse();
		
		// Offset the label if there's a difference between its size and its text width
		double textWidth = getTextWidth(useString);
		double widthDifference = getWidth()-(textWidth+getPadding().getWidth());
		double xMult = 0;
		if ( this.getAlignment().getHpos() == HPos.CENTER )
			xMult = 0.5f;
		if ( this.getAlignment().getHpos() == HPos.RIGHT )
			xMult = 1.0f;
		absX += Math.abs(widthDifference)*xMult;
		
		// Offset the label if there's a difference between its size and its height
		double textHeight = getTextHeight(useString);
		double heightDifference = getHeight()-(textHeight+getPadding().getHeight());
		double ty = 0;
		if ( this.getAlignment().getVpos() == VPos.CENTER )
			ty = 0.5f;
		if ( this.getAlignment().getVpos() == VPos.BOTTOM )
			ty = 1.0f;
		absY += Math.abs(heightDifference)*ty;
		
		// Offset graphic horizontally (if its horizontal alignment)
		if ( gWid > 0 ) {
			if ( contentDisplay.equals(ContentDisplay.LEFT ) ) {
				graphic.setAbsolutePosition(absX, absY);
				absX += gWid;
			} else {
				graphic.setAbsolutePosition(absX+textWidth-graphic.getWidth(), absY);
			}
			graphic.render(context);
			
			double yMult = 0;
			if ( this.getAlignment().getVpos() == VPos.CENTER )
				yMult = 0.5f;
			if ( this.getAlignment().getVpos() == VPos.BOTTOM )
				yMult = 1.0f;
			absY += (graphic.getHeight()-fontSize)*yMult;
		}
		
		// Offset graphic vertically (if it's vertical alignment)
		if ( gHei > 0 ) {
			if ( contentDisplay.equals(ContentDisplay.TOP ) ) {
				graphic.setAbsolutePosition(absX, absY);
				absY += gHei;
			} else {
				graphic.setAbsolutePosition(absX, absY+fontSize);
			}
			graphic.render(context);
		}

		// Setup font
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, font.getFont(fontStyle));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

		// Draw
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgFontBlur(vg,0);
		NanoVG.nvgFillColor(vg, textColor.getNVG());
		NanoVG.nvgText(vg, absX, absY, useString);
	}

	public void setTextFill(Color color) {
		this.textColor = color;
	}

	public float getFontSize() {
		return this.fontSize;
	}

	public double getTextWidth() {
		return getTextWidth(text);
	}
	
	private float getGraphicWidthInternalUse() {
		float gWid = 0;
		if ( graphic == null )
			return gWid;
		
		if ( contentDisplay.equals(ContentDisplay.LEFT) || contentDisplay.equals(ContentDisplay.RIGHT) )
			gWid += graphic.getWidth() + contentGap;
		
		return gWid;	
	}
	
	private float getGraphicHeightInternalUse() {
		float gWid = 0;
		if ( graphic == null )
			return gWid;
		
		if ( contentDisplay.equals(ContentDisplay.BOTTOM) || contentDisplay.equals(ContentDisplay.TOP) )
			gWid += graphic.getHeight() + contentGap;
		
		return gWid;	
	}

	private float[] garbage = new float[4];
	private double getTextWidth(String string) {
		float[] bounds = font.getTextBounds(this.cached_context, string, fontStyle, fontSize, garbage);
		float gWid = getGraphicWidthInternalUse();
		return bounds[2] - bounds[0] + gWid;
	}

	private double getTextHeight(String string) {
		float[] bounds = font.getTextBounds(this.cached_context, string, fontStyle, fontSize, garbage);
		float gHei = getGraphicHeightInternalUse();
		return bounds[3] - bounds[1] + gHei;
	}

	public String getText() {
		return this.text;
	}

	public Color getTextFill() {
		return this.textColor;
	}
}
