package lwjgui.scene.control;

import org.joml.Vector2d;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.LWJGUIWindow;
import lwjgui.collections.ObservableList;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class ScrollPane extends Control {
	
	private Node content;
	private Vector2d viewportSize;
	
	private ScrollBar vBar;
	private ScrollBar hBar;
	
	private double thickness = 8;
	
	private ScrollBar holdingBar;
	private ScrollBar hoveredBar;
	
	private ObservableList<ScrollBar> scrollBars;
	
	public ScrollPane() {
		this.setPrefSize(100, 100);
		this.setAlignment(Pos.TOP_LEFT);
		this.flag_clip = true;
		this.viewportSize = new Vector2d();
		
		this.vBar = new ScrollBar(Orientation.VERTICAL);
		this.hBar = new ScrollBar(Orientation.HORIZONTAL);
		this.scrollBars = new ObservableList<ScrollBar>();
		this.scrollBars.add(vBar);
		this.scrollBars.add(hBar);
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);

		viewportSize.set(getWidth(),getHeight());
		if ( vBar.active )
			viewportSize.x -= thickness;
		if ( hBar.active )
			viewportSize.y -= thickness;
		
		if ( content != null ) {
			double oldx = this.size.x;
			double oldy = this.size.y;
			Node oldp = this.parent;
			
			// Update internal content
			this.parent = null;
			this.size.set(Integer.MAX_VALUE, Integer.MAX_VALUE);
			children.add(content);
			updateChildren();
			children.remove(content);
			content.setAbsolutePosition(this.getAbsoluteX()-hBar.pixel, this.getAbsoluteY()-vBar.pixel);
			this.size.set(oldx,oldy);
			this.parent = oldp;
			
			// Update scrollbars
			vBar.update(viewportSize.y, content.getHeight());
			hBar.update(viewportSize.x, content.getWidth());
		} else {
			hBar.active = false;
			vBar.active = false;
		}
		
		updateBars();
	}
	
	@Override
	public ObservableList<Node> getChildren() {
		return new ObservableList<Node>(content);
	}
	
	private boolean click = false;
	private boolean released = true;
	private Vector2d mouseGrabLocation = new Vector2d();
	private void updateBars() {

		// Get mouse coordinates
		LWJGUIWindow window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
		Context context = window.getContext();
		double mx = context.getMouseX();
		double my = context.getMouseY();
		
		// Get mouse pressed
		int mouse = GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
		
		// Check if we're clicking
		if ( !click && mouse == GLFW.GLFW_PRESS && released )
			click = true;
		else if ( click && mouse == GLFW.GLFW_PRESS) {
			released = false;
			click = false;
		} else if ( mouse != GLFW.GLFW_PRESS )
			released = true;
		
		if ( click && hoveredBar != null ) {
			holdingBar = hoveredBar;
			mouseGrabLocation.set(mx, my);
		}
		
		if ( holdingBar == null )
			return;
		
		// If mouse not pressed, not holding divider
		if ( mouse != GLFW.GLFW_PRESS ) {
			holdingBar = null;
			return;
		}

		double offsetx = mx - mouseGrabLocation.x;
		double offsety = my - mouseGrabLocation.y;
		
		// If we're holding onto a divider
		double pChange = holdingBar.scrollSpaceToPixelSpace(offsety);
		if ( holdingBar.orientation.equals(Orientation.HORIZONTAL) ) {
			pChange = holdingBar.scrollSpaceToPixelSpace(offsetx);
		}
		double t1 = holdingBar.pixel;
		holdingBar.pixel += pChange;
		holdingBar.update(holdingBar.minScrollLen, holdingBar.contentLen);
		double t2 = holdingBar.pixel;
		
		if ( t1 != t2 ) {
			mouseGrabLocation.add(offsetx, offsety); 
		}
	}
	
	private ScrollBar getBarUnderMouse() {
		LWJGUIWindow window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
		Context context = window.getContext();
		double mx = context.getMouseX();
		double my = context.getMouseY();
		
		for (int i = 0; i < scrollBars.size(); i++) {
			ScrollBar d = scrollBars.get(i);
			Vector4d bounds = d.getBounds();
			if ( mx > bounds.x && mx < bounds.x+bounds.z && my > bounds.y && my < bounds.y+bounds.w) {
				return d;
			}
		}
		
		return null;
	}

	public void setVbarPolicy( ScrollBarPolicy policy ) {
		this.vBar.policy = policy;
	}
	
	public ScrollBarPolicy getVbarPolicy() {
		return this.vBar.policy;
	}
	
	public void setHbarPolicy( ScrollBarPolicy policy ) {
		this.hBar.policy = policy;
	}
	
	public ScrollBarPolicy getHbarPolicy() {
		return this.hBar.policy;
	}

	@Override
	public void render(Context context) {
		this.clip(context);
		
		if ( this.getBackground() != null ) {
			LWJGUIUtil.fillRect(context, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), this.getBackground());
		}
		
		if ( content != null ) {
			content.render(context);
		}
		
		hoveredBar = getBarUnderMouse();
		
		this.clip(context);
		if ( vBar.active ) {
			// Draw background
			LWJGUIUtil.fillRect(context, getAbsoluteX()+viewportSize.x, getAbsoluteY(), thickness, getHeight(), Theme.currentTheme().getSelectionPassive());
		}
		
		if ( hBar.active ) {
			// Draw background
			LWJGUIUtil.fillRect(context, getAbsoluteX(), getAbsoluteY()+viewportSize.y, getWidth(), thickness, Theme.currentTheme().getSelectionPassive());
		}
		
		// Draw bars
		for (int i = 0; i < scrollBars.size(); i++) {
			ScrollBar b = scrollBars.get(i);
			if ( b.active ) {
				Vector4d bd = b.getBounds();
				LWJGUIUtil.fillRect(context, bd.x, bd.y, bd.z, bd.w, Theme.currentTheme().getControlOutline());
			}
		}
		
		// Pane Outline
		long vg = context.getNVG();
		Color outlineColor = context.isSelected(this)?Theme.currentTheme().getSelection():Theme.currentTheme().getControlOutline();
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, (int)this.getAbsoluteX(), (int)this.getAbsoluteY(), (int)getWidth(), (int)getHeight());
		NanoVG.nvgStrokeColor(vg, outlineColor.getNVG());
		NanoVG.nvgStrokeWidth(vg, 1f);
		NanoVG.nvgStroke(vg);
	}
	
	public void setContent( Node content ) {
		this.content = content;
	}
	
	public enum ScrollBarPolicy {
		ALWAYS, AS_NEEDED, NEVER;
	}
	
	private class ScrollBar {
		public boolean active;
		public double pixel;
		public double length;
		public ScrollBarPolicy policy = ScrollBarPolicy.AS_NEEDED;
		
		private double minScrollLen;
		private double contentLen;
		
		private Orientation orientation;
		
		public ScrollBar(Orientation orientation) {
			this.orientation = orientation;
		}

		public void update(double minScrollLen, double contentLen) {
			boolean a = false;
			if ( !policy.equals(ScrollBarPolicy.NEVER) && content != null ) {
				if ( contentLen > minScrollLen || policy.equals(ScrollBarPolicy.ALWAYS) ) {
					a = true;
				}
			}
			active = a;
			
			if ( pixel < 0 ) {
				pixel = 0;
			}
			if ( pixel + scrollSpaceToPixelSpace(length) > contentLen ) {
				pixel = contentLen - scrollSpaceToPixelSpace(length);
			}
			
			length = pixelSpaceToScrollSpace(minScrollLen);
			if ( length > pixelSpaceToScrollSpace(contentLen) ) {
				length = pixelSpaceToScrollSpace(contentLen);
			}
			
			this.minScrollLen = minScrollLen;
			this.contentLen = contentLen;
		}
		
		public Vector4d getBounds() {
			if ( this.orientation.equals(Orientation.HORIZONTAL) ) {
				return new Vector4d( 
						getAbsoluteX()+pixelSpaceToScrollSpace(pixel),
						getAbsoluteY()+viewportSize.y,
						length,
						thickness
						);
			} else {
				return new Vector4d( 
						getAbsoluteX()+viewportSize.x,
						getAbsoluteY()+pixelSpaceToScrollSpace(pixel),
						thickness,
						length
						);
			}
		}

		public double pixelSpaceToScrollSpace(double pixel) {
			return (pixel / contentLen) * minScrollLen;
		}
		
		public double scrollSpaceToPixelSpace(double scroll) {
			return (scroll/minScrollLen)*contentLen;
		}
	}
}
