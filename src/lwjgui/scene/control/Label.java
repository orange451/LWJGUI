package lwjgui.scene.control;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.LWJGUI;
import lwjgui.geometry.Node;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.theme.Theme;

public class Label extends Control {
	private String text = "";
	private float fontSize = 16;
	private FontStyle fontStyle = FontStyle.REGULAR;
	private Color textColor;
	private boolean updated = true;
	
	public Label(String text) {
		setText(text);
		textColor = Theme.currentTheme().getText();
	}
	
	public Label() {
		this("");
	}
	
	public void setText(String text) {
		this.text = text;
		updated = true;
	}
	
	public void setFontSize( float size ) {
		this.fontSize = size;
		updated = true;
	}
	
	public void setFontStyle( FontStyle style ) {
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

	@Override
	public void render(Context context) {
		clip(context);
		
		long vg = context.getNVG();
		int absX = (int)getAbsoluteX();
		int absY = (int)getAbsoluteY();

	    ByteBuffer textBuffer = null;
	    try {
	        textBuffer = MemoryUtil.memUTF8(text, false);
	        long start = MemoryUtil.memAddress(textBuffer);
	        long end = start + textBuffer.remaining();
	
			// Draw
			NanoVG.nvgFontSize(vg, fontSize);
			NanoVG.nvgFontFace(vg, Font.SANS.getFont(fontStyle));
			NanoVG.nvgTextAlign(vg,NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
	
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgFontBlur(vg,0);
			NanoVG.nvgFillColor(vg, textColor.getNVG());
			NanoVG.nnvgText(vg, absX, absY, start, end);
	    } finally {
	        if (textBuffer != null) {
	            MemoryUtil.memFree(textBuffer);
	        }
	    }
	}

	public void setTextFill(Color color) {
		this.textColor = color;
	}

	public float getFontSize() {
		return this.fontSize;
	}

}
