package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class Slider extends Control {
	private double min;
	private double max;
	private double value;
	
	private Thumb thumb;

	protected boolean disabled;
	
	private EventHandler<ActionEvent> onValueChangeEvent;
	
	public Slider() {
		this(0, 100, 50);
	}
	
	public Slider(double min, double max, double value) {
		this.min = min;
		this.max = max;
		this.value = value;
		this.setPrefSize(100, 14);
		
		this.thumb = new Thumb();
		this.children.add(thumb);
		this.flag_clip = false;
	}
	
	public void setOnValueChangedEvent(EventHandler<ActionEvent> event) {
		this.onValueChangeEvent = event;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public boolean isDisabled() {
		return this.disabled;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public void setValue(double value) {
		double old = this.value;
		this.value = value;
		
		if ( value != old && onValueChangeEvent != null ) {
			EventHelper.fireEvent(onValueChangeEvent, new ActionEvent());
		}
	}
	
	private float trackLength;
	
	@Override
	public void render(Context context) {
		long vg = context.getNVG();
		float w = (float) this.getInnerBounds().getWidth();//-thumb.getWidth());
		float h = (float) this.getInnerBounds().getHeight()/3;
		float x = (float) (getX()+this.getInnerBounds().getX());// + (thumb.getWidth()/2));
		float y = (float) (getY()+this.getInnerBounds().getY()) + (this.getInnerBounds().getHeight()/2)-(h/2);
		float r = h/2;
		
		trackLength = (float) (w-thumb.getWidth());

		// Background
		{
			Color c1 = Theme.current().getPaneAlt();
			Color c2 = Theme.current().getBackground();
			NVGPaint grad2 = NanoVG.nvgLinearGradient(vg, x, y+h*0.5f, x, y+h, c2.getNVG(), c1.getNVG(), NVGPaint.calloc());
			
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRoundedRect(vg, x, y, w, h, r);
			NanoVG.nvgFillPaint(vg, grad2);
			NanoVG.nvgFill(vg);
			NanoVG.nvgClosePath(vg);
		}
		
		// Outline
		{
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRoundedRect(vg, x, y, w, h, r);
			NanoVG.nvgStrokeColor(vg, Theme.current().getControlOutline().getNVG());
			NanoVG.nvgStrokeWidth(vg, 0.7f);
			NanoVG.nvgStroke(vg);
			NanoVG.nvgClosePath(vg);
		}
		
		thumb.setDisabled(this.isDisabled());
		thumb.render(context);
	}
	
	private double tween(double min, double max, double ratio) {
		return min+(max-min)*ratio;
	}
	
	class Thumb extends ButtonBase {
		private int size = 16;
		private double bOff;
		private boolean dragged = false;
		
		public Thumb() {
			super("");
			
			this.setMinSize(size, size);
			this.setMaxSize(size, size);
			this.setPrefSize(size, size);
			this.setPadding(Insets.EMPTY);
			this.setCornerRadius(size/2);
			
			this.setOnMousePressed((event)->{
				if ( isDisabled() )
					return;
				bOff = event.getMouseX()-(this.getX()+this.getWidth()/2);
				dragged = true;
			});
		}
		
		private double mouseSpaceToTrackSpace(double mousePos) {
			double padding = (Slider.this.getWidth()-trackLength)/2;
			double t1 = mousePos - Slider.this.getX() - padding;
			double t2 = t1/trackLength;
			return tween(min,max,t2);
		}
		
		private double trackSpaceToMouseSpace(double trackPos) {
			double v = ((trackPos-min)/(max-min))*trackLength;
			double padding = (Slider.this.getWidth()-trackLength)/2;
			double offset = Slider.this.getX()+padding;
			return v+offset;
		}
		
		/**
		 * Make position public for this node because this node is not public to outside Slider class.
		 */
		@Override
		public void position(Node parent) {
			super.position(parent);
		}
		
		@Override
		public void render(Context context) {
			if ( dragged ) {
				double t = Math.min( max, Math.max( min, mouseSpaceToTrackSpace(context.getMouseX()-bOff) ) );
				setValue(t);
			}
			
			if ( GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT)!=GLFW.GLFW_PRESS )
				dragged = false;

			double v = trackSpaceToMouseSpace(value);
			this.setAbsolutePosition(v-getWidth()/2, getY());
			
			// Limit the position of ths thumb
			if ( this.absolutePosition.x < Slider.this.getX() )
				this.absolutePosition.x = Slider.this.getX();
			if ( this.absolutePosition.x > Slider.this.getX()+Slider.this.getWidth()-this.getWidth() )
				this.absolutePosition.x = Slider.this.getX()+Slider.this.getWidth()-this.getWidth();
			
			super.render(context);
		}
	}
}
