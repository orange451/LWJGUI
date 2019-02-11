package lwjgui.scene.control;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.theme.Theme;

public abstract class Labeled extends Control {
	private Node graphic;
	private String text = "";
	private String useString = "";
	private float fontSize = 18;
	private Font font = Font.SANS;
	private FontStyle fontStyle = FontStyle.REGULAR;
	private Color textColor;
	
	private static final String ELIPSES = "...";

	public Labeled() {
		textColor = Theme.currentTheme().getText();
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
			float[] bounds = getTextBounds( cached_context, text, font, fontStyle, fontSize);
			float[] elipBnd = getTextBounds( cached_context, ELIPSES, font, fontStyle, fontSize);
			double curWid = bounds[2]-bounds[0]+gWid + this.getPadding().getWidth();
			double prefWid = curWid;
			this.setPrefWidth(prefWid);
			
			// If we're too large for the parent element...
			if ( this.getPrefWidth() >= this.getAvailableSize().x ) {
				this.setPrefWidth(maxWid);
				float eWid = elipBnd[2]-elipBnd[0];
				curWid += eWid;
				
				// While we're too large, remove text off the end and replace with elipses
				while ( (curWid >= maxWid) && (remove < text.length()) ) {
					remove++;
					useString = useString.substring(0, text.length()-remove)+ELIPSES;
					bounds = getTextBounds( cached_context, useString, font, fontStyle, fontSize);
					curWid = bounds[2]-bounds[0]+gWid;
				}
			}
			
			// Set final bounds
			double hei = (bounds[3] - bounds[1]) + this.padding.getHeight();
			this.setMinHeight(hei);
			this.setMaxHeight(hei);
		}
		
		super.position(parent);

	}
	
	private static float[] getTextBounds(Context context, String string, Font font, FontStyle style, float size) {
		float[] bounds = new float[4];
		if ( context == null ) {
			return bounds;
		}
		String fnt = font.getFont(style);
		if ( fnt == null )
			return bounds;
		
		NanoVG.nvgFontSize(context.getNVG(), size);
		NanoVG.nvgFontFace(context.getNVG(), fnt);
		NanoVG.nvgTextAlign(context.getNVG(),NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
		if ( string != null ) {
			NanoVG.nvgTextBounds(context.getNVG(), 0, 0, string, bounds);
		}
		return bounds;
	}

	@Override
	public void render(Context context) {
		//clip(context);

		long vg = context.getNVG();
		int absX = (int)(getAbsoluteX()-0.5 + this.padding.getLeft());
		int absY = (int)(getAbsoluteY()+0.5 + this.padding.getTop());
		
		double gWid = graphic == null ? -1 : graphic.getWidth();
		if ( gWid >= 0 ) {
			graphic.setAbsolutePosition(absX, absY);
			graphic.render(context);
			absX += gWid;
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
		float[] bounds = getTextBounds(this.cached_context,text,font,fontStyle,fontSize);
		float gWid = (float) (graphic == null ? 0 : graphic.getWidth());
		return bounds[2]-bounds[0] + gWid;
	}

	public String getText() {
		return this.text;
	}

	public Color getTextFill() {
		return this.textColor;
	}
}
