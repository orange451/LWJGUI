package lwjgui.scene.control;

import java.awt.Point;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.event.ButtonEvent;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.theme.Theme;

public abstract class ButtonBase extends Labeled {
	private ButtonEvent buttonEvent;
	protected double cornerRadius = 3.0f;
	
	public ButtonBase(String name) {
		super(name);
		
		this.setMinSize(20, 20);
		this.setPadding(new Insets(4,4,4,4));
		
		this.mouseReleasedEvent = new MouseEvent() {
			@Override
			public void onEvent(int button) {
				if ( button == 0 ) {
					if ( buttonEvent != null ) {
						buttonEvent.onEvent();
					}
				}
			}
		};
		
		this.setText(name);
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	public Vector2d getAvailableSize() {
		return new Vector2d(getMaxWidth(),getMaxHeight());
	}
	
	@Override
	protected void resize() {
		super.resize();
	}

	@Override
	public void render(Context context) {
		clip(context, 4);
		
		NanoVG.nvgTranslate(context.getNVG(), (int)getAbsoluteX(), (int)getAbsoluteY());		
			long vg = context.getNVG();
			Point p = getDrawSize();
			int w = p.x;
			int h = p.y;
			
			// Draw dropped down button outline
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRoundedRect(context.getNVG(), 0, 1, w, h, (float) cornerRadius);
			NanoVG.nvgFillColor(context.getNVG(), Theme.currentTheme().getButton().getNVG());
			NanoVG.nvgFill(context.getNVG());
			
			// Selection graphic
			if ( context.isSelected(this) && context.isFocused() ) {
				int feather = 4;//(int) (cornerRadius*2); // Smoothing
				float ind = 0; // Inset
				NVGPaint paint = NanoVG.nvgBoxGradient(vg, ind,ind, w-ind*2,h-ind*2,(float)cornerRadius, feather, Theme.currentTheme().getSelection().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.calloc());
				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgRect(vg, -feather,-feather, w+feather*2,h+feather*2);
				NanoVG.nvgFillPaint(vg, paint);
				NanoVG.nvgFill(vg);
				paint.free();
			}
			
			// Draw button outline
			Color outlineColor = (context.isSelected(this)&&context.isFocused())?Theme.currentTheme().getSelection():Theme.currentTheme().getButtonOutline();
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRoundedRect(context.getNVG(), 0, 0, w, h, (float) cornerRadius);
			NanoVG.nvgFillColor(context.getNVG(), outlineColor.getNVG());
			NanoVG.nvgFill(context.getNVG());
			
			// Draw main background
			float hDist = context.isHovered(this) ? 3 : 2.5f;
			Color buttonColor = context.isHovered(this) ? (GLFW.glfwGetMouseButton(context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS?Theme.currentTheme().getButtonOutline():Theme.currentTheme().getButtonHover()):Theme.currentTheme().getButton();
			NVGPaint bg = NanoVG.nvgLinearGradient(vg, 0, -hDist/2f, 0, h*hDist, buttonColor.getNVG(), Theme.currentTheme().getShadow().getNVG(), NVGPaint.calloc());
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRoundedRect(vg, 1,1, w-2,h-2, (float) cornerRadius-1.0f);
			NanoVG.nvgFillColor(vg, Color.WHITE.getNVG());
			NanoVG.nvgFill(vg);
			NanoVG.nvgFillPaint(vg, bg);
			NanoVG.nvgFill(vg);
			
			// Draw inset outline
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRoundedRect(vg, 1,1, w-2,h-2, (float) cornerRadius-1);
			NVGColor c1 = Theme.currentTheme().getButtonHover().getNVG();
			if ( context.isHovered(this) && GLFW.glfwGetMouseButton(context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS)
				c1 = Theme.currentTheme().getShadow().getNVG();
			NanoVG.nvgStrokePaint(vg, NanoVG.nvgLinearGradient(vg, 0, 0, 0, h, c1, Theme.currentTheme().getSelectionPassive().getNVG(), NVGPaint.calloc()));
			NanoVG.nvgStrokeWidth(vg, 1f);
			NanoVG.nvgStroke(vg);
			
			// internal selection graphic
			if ( context.isSelected(this) && context.isFocused() ) {
				Color sel = Theme.currentTheme().getSelection();
				Color col = new Color(sel.getRed(), sel.getGreen(), sel.getBlue(), 64);
				NanoVG.nvgBeginPath(vg);
				float inset = 1.33f;
				NanoVG.nvgRoundedRect(vg, inset, inset, w-inset*2,h-inset*2, (float) cornerRadius-inset);
				NanoVG.nvgStrokeColor(vg, col.getNVG());
				NanoVG.nvgStrokeWidth(vg, inset*1.25f);
				NanoVG.nvgStroke(vg);
			}
			
			bg.free();
			
		NanoVG.nvgTranslate(context.getNVG(), (int)-getAbsoluteX(), (int)-getAbsoluteY());
		
		if ( graphicLabel != null ) {
			this.setAlignment(graphicLabel.alignment);
			graphicLabel.render(context);
		}
	}

	protected Point getDrawSize() {
		return new Point((int)getWidth(), (int)getHeight());
	}

	public void setOnAction(ButtonEvent event) {
		this.buttonEvent = event;
	}

}
