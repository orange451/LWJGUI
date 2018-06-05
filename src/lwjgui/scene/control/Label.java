package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import lwjgui.Color;
import lwjgui.Context;
import lwjgui.LWJGUI;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.theme.Theme;

public class Label extends Control {
	private String text = "";
	private float fontSize = 18;
	private FontStyle fontStyle = FontStyle.REGULAR;
	private Color textColor;
	private boolean updated = true;

	public Label(String text) {
		setText(text);
		textColor = Theme.currentTheme().getText();
		this.flag_clip = false;
	}

	public Label() {
		this("");
	}

	public void setText(String text) {
		this.text = text;
		updated = true;
	}

	public void setFontSize( float size ) {
		if ( size == this.fontSize )
			return;
		
		this.fontSize = size;
		updated = true;
	}

	public void setFontStyle( FontStyle style ) {
		if ( fontStyle != null && style.equals(this.fontStyle) )
			return;
		
		this.fontStyle = style;
		updated = true;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void position(Node parent) {
		Context context = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext();
		long vg = context.getNVG();

		if ( updated ) {
			updated = false;
			float[] bounds = new float[4];

			NanoVG.nvgFontSize(vg, fontSize);
			NanoVG.nvgFontFace(vg, Font.SANS.getFont(fontStyle));
			NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
			if ( text != null ) {
				NanoVG.nvgTextBounds(vg, 0, 0, text, bounds);
			}

			double wid = (bounds[2] - bounds[0]);
			double hei = (bounds[3] - bounds[1]);

			this.setMinWidth(wid);
			this.setMaxWidth(wid);
			this.setMinHeight(hei);
			this.setMaxHeight(hei);
		}

		super.position(parent);
	}
	
	public double getTextWidth() {
		Context context = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext();
		long vg = context.getNVG();
		
		float[] bounds = new float[4];
		
		if ( text != null ) {
			NanoVG.nvgFontSize(vg, fontSize);
			NanoVG.nvgFontFace(vg, Font.SANS.getFont(fontStyle));
			NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
			NanoVG.nvgTextBounds(vg, 0, 0, text, bounds);
		}

		double wid = (bounds[2] - bounds[0]);
		//double hei = (bounds[3] - bounds[1]);
		
		return wid;
	}

	@Override
	public void render(Context context) {
		clip(context);

		long vg = context.getNVG();
		int absX = (int)getAbsoluteX();
		int absY = (int)getAbsoluteY();
		
		// Background
		/*NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, absX, absY, (int)getWidth(), (int)getHeight());
		NanoVG.nvgFillColor(vg, Color.RED.getNVG());
		NanoVG.nvgFill(vg);*/

		// Setup font
		NanoVG.nvgFontSize(vg, fontSize);
		NanoVG.nvgFontFace(vg, Font.SANS.getFont(fontStyle));
		NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

		// Draw
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgFontBlur(vg,0);
		NanoVG.nvgFillColor(vg, textColor.getNVG());
		NanoVG.nvgText(vg, absX, absY, text);
	}

	public void setTextFill(Color color) {
		this.textColor = color;
	}

	public float getFontSize() {
		return this.fontSize;
	}

}
