package lwjgui.scene.control;

import java.awt.Point;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.event.ButtonEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public abstract class ButtonBase extends Labeled {
	protected EventHandler<ButtonEvent> buttonEvent;

	protected double cornerNW = 3.0;
	protected double cornerNE = 3.0;
	protected double cornerSW = 3.0;
	protected double cornerSE = 3.0;
	
	protected double textOffset;
	
	protected boolean disabled;
	
	public ButtonBase(String name) {
		super();
		this.setText(name);
		
		this.setMinSize(32, 24);
		this.setPadding(new Insets(4,6,4,6));
		
		this.setOnMouseReleased( new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if ( disabled )
					return;
				
				if ( event.button == 0 ) {
					if ( buttonEvent != null ) {
						EventHelper.fireEvent(buttonEvent, new ButtonEvent());
					}
				}
			}
		});
		this.setText(name);
	}
	
	protected void setCornerRadius(double radius) {
		this.cornerNE = radius;
		this.cornerNW = radius;
		this.cornerSE = radius;
		this.cornerSW = radius;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public boolean isDisabled() {
		return this.disabled;
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
	
	protected boolean isPressed() {
		if ( cached_context == null )
			return false;
		
		if ( isDisabled() )
			return false;
		
		return cached_context.isHovered(this) && GLFW.glfwGetMouseButton(cached_context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
	}

	@Override
	public void render(Context context) {
		clip(context, 8);
		
		//NanoVG.nvgTranslate(context.getNVG(), (int)getAbsoluteX(), (int)getAbsoluteY());		
			long vg = context.getNVG();
			Point p = getDrawSize();
			int x = (int) this.getX();
			int y = (int) this.getY();
			int w = p.x;
			int h = p.y;
			
			// Selection graphic
			if ( context.isSelected(this) && context.isFocused() ) {
				int feather = 4;
				float c = (float) Math.max(cornerNW,Math.max(cornerNE,Math.max(cornerSE,cornerSW)));
				NVGColor sel = Theme.currentTheme().getSelection().getNVG();
				if ( isDisabled() )
					sel = Theme.currentTheme().getSelectionPassive().getNVG();
				
				NVGPaint paint = NanoVG.nvgBoxGradient(vg, x,y, w,h,c, feather, sel, Color.TRANSPARENT.getNVG(), NVGPaint.calloc());
				NanoVG.nvgBeginPath(vg);
				buttonMask( vg, x-feather, y-feather, w+feather*2, h+feather*2, 0 );
				NanoVG.nvgFillPaint(vg, paint);
				NanoVG.nvgFill(vg);
				paint.free();
			}
			
			// Draw button outline
			Color outlineColor = (context.isSelected(this)&&context.isFocused()&&!isDisabled())?Theme.currentTheme().getSelection():Theme.currentTheme().getControlOutline();
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x,y,w,h,+1);
			NanoVG.nvgFillColor(vg, outlineColor.getNVG());
			NanoVG.nvgFill(vg);	
			
			// Draw main background
			Color buttonColor = isPressed()?Theme.currentTheme().getControlOutline():((context.isHovered(this)&&!isDisabled())?Theme.currentTheme().getControlHover():Theme.currentTheme().getControl());
			NVGPaint bg = NanoVG.nvgLinearGradient(vg, x, y, x, y+h*3, buttonColor.getNVG(), Theme.currentTheme().getControlOutline().getNVG(), NVGPaint.calloc());
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x+1,y+1,w-2,h-2, 0);
			NanoVG.nvgFillPaint(vg, bg);
			NanoVG.nvgFill(vg);
			
			// Draw inset outline
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x+1,y+1,w-2,h-2, 0);
			NVGColor c1 = Theme.currentTheme().getControlHover().getNVG();
			NVGColor c2 = Theme.currentTheme().getControlAlt().getNVG();
			if ( isPressed() ) {
				c2 = buttonColor.darker().getNVG();
				c1 = c2;
			}
			NanoVG.nvgStrokePaint(vg, NanoVG.nvgLinearGradient(vg, x, y, x, y+h, c1, c2, NVGPaint.calloc()));
			NanoVG.nvgStrokeWidth(vg, 1f);
			NanoVG.nvgStroke(vg);
			
			// internal selection graphic
			if ( context.isSelected(this) && context.isFocused() ) {
				Color sel = Theme.currentTheme().getSelection();
				if ( isDisabled() )
					sel = Theme.currentTheme().getSelectionPassive();
				Color col = new Color(sel.getRed(), sel.getGreen(), sel.getBlue(), 64);
				NanoVG.nvgBeginPath(vg);
				float inset = 1.25f;
				buttonMask(vg, x+inset,y+inset,w-inset*2,h-inset*2, 0);
				NanoVG.nvgStrokeColor(vg, col.getNVG());
				NanoVG.nvgStrokeWidth(vg, inset*1.25f);
				NanoVG.nvgStroke(vg);
			}
			
			bg.free();
			
		//NanoVG.nvgTranslate(context.getNVG(), (int)-getAbsoluteX(), (int)-getAbsoluteY());
		
		if ( isDisabled() ) {
			this.setTextFill(Theme.currentTheme().getShadow());
		}

		this.offset(textOffset, 0);
		super.render(context);
		this.offset(-textOffset, 0);
	}

	private void buttonMask(long vg, float x, float y, float w, float h, float rOffset) {
		NanoVG.nvgRoundedRectVarying(vg, x, y, w, h, (float)cornerNW+rOffset, (float)cornerNE+rOffset, (float)cornerSE+rOffset, (float)cornerSW+rOffset);
	}
	
	protected Point getDrawSize() {
		return new Point((int)getWidth(), (int)getHeight());
	}

	public void setOnAction(EventHandler<ButtonEvent> event) {
		this.buttonEvent = event;
	}

}
