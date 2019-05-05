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

	/**
	 * Set the text used for this label.
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Set the font used to draw the text in this label.
	 * @param font
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Set the size of the font for this label.
	 * @param size
	 */
	public void setFontSize( float size ) {
		if ( size == this.fontSize )
			return;
		
		this.fontSize = size;
	}

	/**
	 * Set the font style for this label.
	 * @param style
	 */
	public void setFontStyle( FontStyle style ) {
		if ( fontStyle != null && style.equals(this.fontStyle) )
			return;
		
		this.fontStyle = style;
	}
	
	/**
	 * Unmodifyable list of children.
	 */
	@Override
	protected ObservableList<Node> getChildren() {
		return new ObservableList<Node>(children);
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	/**
	 * Sets the graphic node for this label. Setting to null removes the graphic.
	 * @param graphic
	 */
	public void setGraphic( Node graphic ) {
		if ( this.graphic != null )
			this.children.remove(this.graphic);
		
		this.graphic = graphic;
		if ( graphic != null ) {
			this.children.add(graphic);
		}
	}
	
	/**
	 * Sets the gap in pixels between a label and its graphic.
	 * @param gap
	 */
	public void setGraphicTextGap( double gap ) {
		this.contentGap = gap;
	}
	
	/**
	 * Returns the gap in pixels between the label and the graphic.
	 * @return
	 */
	public double getGraphicTextGap() {
		return this.contentGap;
	}
	
	/**
	 * Sets the content display for this label.<br>
	 * A content display is used to position a labels graphic in relation to it.<br>
	 * A label with a content display of {@link ContentDisplay#RIGHT} will put the graphic node to the right of the text.
	 * @param display
	 */
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
			double curWid = getTextWidth(text) + this.getPadding().getWidth();
			
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
			this.size.y = Math.max(Math.max(this.getPrefHeight(), this.getHeight()), getTextHeight(useString)+getPadding().getHeight());
		}
	}
	
	@Override
	public void render(Context context) {
		//LWJGUIUtil.fillRect(context, getX(), getY(), getWidth(), getHeight(), Color.AQUA);
		
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
			double yMult = 0;
			if ( this.getAlignment().getVpos() == VPos.CENTER )
				yMult = 0.5f;
			if ( this.getAlignment().getVpos() == VPos.BOTTOM )
				yMult = 1.0f;
			double graphicOffsetY = (graphic.getHeight()-fontSize)*yMult;
			
			if ( contentDisplay.equals(ContentDisplay.LEFT ) ) {
				graphic.setAbsolutePosition(absX, absY+graphicOffsetY);
				absX += gWid;
			} else {
				graphic.setAbsolutePosition(absX+textWidth-graphic.getWidth(), absY-graphicOffsetY);
			}
		}
		
		// Offset graphic vertically (if it's vertical alignment)
		if ( gHei > 0 ) {
			if ( contentDisplay.equals(ContentDisplay.TOP ) ) {
				graphic.setAbsolutePosition(absX, absY);
				absY += gHei;
			} else {
				graphic.setAbsolutePosition(absX, absY+fontSize);
			}
		}
		
		if ( graphic != null ) {
			graphic.updateChildren();
			graphic.render(context);
		}

		// Setup font
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, font.getFont(fontStyle));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

		// Draw
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgFontBlur(vg,0);
		NanoVG.nvgFillColor(vg, isDisabled()?Theme.current().getShadow().getNVG():textColor.getNVG());
		NanoVG.nvgText(vg, absX, absY, useString);
	}

	/**
	 * Sets the color for the text of this label.
	 * @param color
	 */
	public void setTextFill(Color color) {
		this.textColor = color;
	}

	/**
	 * Returns the current font-size used to draw this label.
	 * @return
	 */
	public float getFontSize() {
		return this.fontSize;
	}

	/**
	 * Returns the width of the text in this label.
	 * @return
	 */
	public double getTextWidth() {
		return getTextWidth(text)-getGraphicWidthInternalUse();
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
	
	/**
	 * Returns the text of the label.
	 * @return
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Returns the color of the text of this label.
	 * @return
	 */
	public Color getTextFill() {
		return this.textColor;
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
}
