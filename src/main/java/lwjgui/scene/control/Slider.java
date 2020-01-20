package lwjgui.scene.control;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Orientation;
import lwjgui.glfw.input.MouseHandler;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

import static lwjgui.geometry.Orientation.HORIZONTAL;

public class Slider extends Control {
	private double min;
	private double max;
	private double value;
	private double blockIncrement;
	
	private Orientation orientation;
	
	private Thumb thumb;

	protected boolean disabled;
	
	private EventHandler<ActionEvent> onValueChangeEvent;
	
	public Slider() {
		this(0, 100, 50, 0);
	}
	
	public Slider(double min, double max, double value) {
		this(min, max, value, 0);
	}
	
	public Slider(double min, double max, double value, double blockIncrement) {
		this.min = min;
		this.max = max;
		this.value = value;
		this.blockIncrement = blockIncrement;
		this.setPrefSize(100, 14);
		
		this.thumb = new Thumb();
		this.children.add(thumb);
		this.flag_clip = false;
		
		this.orientation = Orientation.HORIZONTAL;
	}

	@Override
	public String getElementType() {
		return "slider";
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
	
	public double getBlockIncrement() {
		return blockIncrement;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public void setValue(double value) {
		double prevValue = this.value;
		this.value = value;
		
		if ( blockIncrement != 0.0 ) {
			this.value = Math.round(value / blockIncrement) * blockIncrement;
		}
		
		this.value = Math.min( max, Math.max( min, this.value ) );
		
		if ( this.value != prevValue && onValueChangeEvent != null ) {
			EventHelper.fireEvent(onValueChangeEvent, new ActionEvent());
		}
	}

	/**
	 * This is the interval at which the slider will "snap" to. Setting
	 * 'blockIncrement' to 0 will disable snapping.
	 * 
	 * @param blockIncrement - The snap interval (0 to disable snapping)
	 */
	public void setBlockIncrement(double blockIncrement) {
		if (blockIncrement == Double.MAX_VALUE || blockIncrement == Double.MIN_VALUE) 
			blockIncrement = 0d;
		
		double prevInterval = this.value;
		this.blockIncrement = blockIncrement;
		
		if ( this.blockIncrement != prevInterval && this.blockIncrement != 0.0 ) {
			double prevValue = value;
			value = ( Math.floor(value) / this.blockIncrement ) * this.blockIncrement;
			
			if (value != prevValue && onValueChangeEvent != null ) {
				EventHelper.fireEvent(onValueChangeEvent, new ActionEvent());
			}
		}
	}
	
	public void setOrientation(Orientation orientation) {
		Orientation old = this.orientation;
		this.orientation = orientation;
		
		if ( old != orientation ) {
			this.setPrefSize( this.getHeight(), this.getWidth() );
		}
	}
	
	private float trackLength;
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;

		drawTrack(context);
		
		thumb.setDisabled(this.isDisabled());
		thumb.render(context);
	}
	
	protected void drawTrack(Context context) {
		if ( context == null )
			return;
		
		long vg = context.getNVG();
		float x, y, w, h, r;
		
		if ( orientation == HORIZONTAL ) {
			w = (float) this.getInnerBounds().getWidth();
			h = (float) this.getInnerBounds().getHeight() / 3;
			x = (float) ( getX()+this.getInnerBounds().getX() );
			y = (float) ( getY() + this.getInnerBounds().getY() ) + ( this.getInnerBounds().getHeight() / 2 )-( h / 2 );
			r = h / 2;
			
			trackLength = (float) ( w - thumb.getWidth() );
		}
		else {
			w = (float) this.getInnerBounds().getWidth() / 3;
			h = (float) this.getInnerBounds().getHeight();
			x = (float) ( getX()+this.getInnerBounds().getX() + w );
			y = (float) ( getY() + this.getInnerBounds().getY() ) + ( this.getInnerBounds().getHeight() / 2 )-( h / 2 );
			r = w / 2;
			
			trackLength = (float) ( h - thumb.getHeight() );
		}
		
		// Background
		{
			Color c1 = Theme.current().getPaneAlt();
			Color c2 = Theme.current().getBackground();
			try (MemoryStack stack = stackPush()) {
				NVGPaint grad2 = NanoVG.nvgLinearGradient(vg, x, y+h*0.5f, x, y+h, c2.getNVG(), c1.getNVG(), NVGPaint.callocStack(stack));

				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgRoundedRect(vg, x, y, w, h, r);
				NanoVG.nvgFillPaint(vg, grad2);
				NanoVG.nvgFill(vg);
				NanoVG.nvgClosePath(vg);
			}
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
	}
	
	private double tween(double min, double max, double ratio) {
		return min+(max-min)*ratio;
	}
	
	class Thumb extends ButtonBase {
		private int size = 16;
		private double bOff;
		private boolean dragged = false;
		
		private double pos;
		private double len;
		
		public Thumb() {
			super("");
			
			this.forceSize(size, size);
			this.setPadding(Insets.EMPTY);
			this.setBorderRadii(size/2f);
			
			this.setOnMousePressed((event)->{
				if ( isDisabled() )
					return;
				
				if ( orientation == HORIZONTAL ) 
					bOff = event.getMouseX()-(this.getX()+this.getWidth()/2f);
				else
					bOff = event.getMouseY()-(this.getY()+this.getHeight()/2f);
			
				dragged = true;
			});
		}

		@Override
		public String getElementType() {
			return "sliderthumb";
		}
		
		private double mouseSpaceToTrackSpace(double mousePos) {
			double padding = (len-trackLength)/2;
			double t1 = mousePos - pos - padding;
			double t2 = t1/trackLength;
			return tween(min,max,t2);
		}
		
		private double trackSpaceToMouseSpace(double trackPos) {
			
			double v = ((trackPos-min)/(max-min))*trackLength;
			double padding = (len-trackLength)/2;
			double offset = pos+padding;
			return v+offset;
		}
		
		/**
		 * Make position public for this node because this node is not public to outside Slider class.
		 */
		@Override
		public void position(Node parent) {
			super.position(parent);
			this.forceSize(size, size);
		}
		
		@Override
		public void render(Context context) {
			if ( !isVisible() )
				return;
			
			if ( orientation == HORIZONTAL ) {
				pos = Slider.this.getX();
				len = Slider.this.getWidth();
			}
			else {
				pos = Slider.this.getY();
				len = Slider.this.getHeight();
			}
			
			if ( dragged ) {
				MouseHandler mh = window.getMouseHandler();
				double mousePos;
				
				if ( orientation == HORIZONTAL ) 
					mousePos = mh.getX();
				else
					mousePos = mh.getY();
				
				double t = mouseSpaceToTrackSpace(mousePos-bOff);
				setValue(t);
			}
			
			if ( GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT)!=GLFW.GLFW_PRESS )
				dragged = false;

			double v = trackSpaceToMouseSpace(value);
			
			if ( orientation == HORIZONTAL ) {
				this.setAbsolutePosition(v-getWidth()/2, getY());
				// Limit the position of the thumb
				if ( this.absolutePosition.x < pos )
					this.absolutePosition.x = pos;
				if ( this.absolutePosition.x > pos + len - this.getWidth() )
					this.absolutePosition.x = pos + len - this.getWidth();
			}
			else {
				this.setAbsolutePosition(getX(), v-getHeight()/2);
				// Limit the position of the thumb
				if ( this.absolutePosition.y < pos )
					this.absolutePosition.y = pos;
				if ( this.absolutePosition.y > pos + len - this.getWidth() )
					this.absolutePosition.y = pos + len - this.getWidth();
			}
			
			super.render(context);
		}
	}
}
