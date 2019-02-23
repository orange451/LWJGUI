package lwjgui.scene.control;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.VPos;
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

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	public void setGraphic( Node graphic ) {
		if ( this.graphic != null )
			this.children.remove(this.graphic);
		
		this.graphic = graphic;
		this.children.add(graphic);
	}
	
	@Override
	public void position(Node parent) {
		if (this.getParent() != null && cached_context != null) {
			useString = text;
			
			// Get max width of parent element
			double maxWid = this.getParent().getInnerBounds().getWidth();
			double gWid = graphic == null ? 0 : graphic.getWidth();
			
			int remove = 0;
			
			// Get some text bounds
			float[] bounds = font.getTextBounds( cached_context, text, fontStyle, fontSize);
			float[] elipBnd = font.getTextBounds( cached_context, ELIPSES, fontStyle, fontSize);
			//double curWid = bounds[2]-bounds[0]+gWid + this.getPadding().getWidth();
			double curWid = getTextWidth() + this.getPadding().getWidth();
			double prefWid = curWid;
			this.setPrefWidth(prefWid);
			
			// If we're too large for the parent element...
			if ( this.getPrefWidth() >= this.getAvailableSize().x ) {
				this.setPrefWidth(prefWid);
				float eWid = elipBnd[2]-elipBnd[0];
				curWid += eWid;
				
				// While we're too large, remove text off the end and replace with elipses
				while ( (curWid >= maxWid) && (remove < text.length()) ) {
					remove++;
					useString = useString.substring(0, text.length()-remove)+ELIPSES;
					bounds = font.getTextBounds( cached_context, useString, fontStyle, fontSize);
					curWid = bounds[2]-bounds[0]+gWid;
				}
			}
			
			// Compute preferred height
			double hei = (bounds[3] - bounds[1]) + this.padding.getHeight();
			this.setMinHeight(hei);
			this.setPrefHeight(hei);
		}
		
		super.position(parent);
	}
	
	@Override
	public void render(Context context) {
		//clip(context);
		
		super.render(context);

		long vg = context.getNVG();
		int absX = (int)(getX()-0.5 + this.padding.getLeft());
		int absY = (int)(getY()+0.5 + this.padding.getTop());
		
		double gWid = graphic == null ? -1 : graphic.getWidth();
		if ( gWid >= 0 ) {
			graphic.setAbsolutePosition(absX, absY);
			graphic.render(context);
			
			absX += gWid;
			
			double yMult = 0;
			if ( this.getAlignment().getVpos() == VPos.CENTER )
				yMult = 0.5f;
			if ( this.getAlignment().getVpos() == VPos.BOTTOM )
				yMult = 1.0f;
			absY += (this.getHeight()-fontSize)*yMult;
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
		float[] bounds = font.getTextBounds(this.cached_context, text, fontStyle, fontSize);
		float gWid = (float) (graphic == null ? 0 : graphic.getWidth());
		return bounds[2] - bounds[0] + gWid;
	}

	public String getText() {
		return this.text;
	}

	public Color getTextFill() {
		return this.textColor;
	}
}
