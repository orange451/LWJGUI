package lwjgui.scene.control;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.event.ScrollEvent;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Window;
import lwjgui.scene.layout.Pane;
import lwjgui.theme.Theme;

public class ScrollPane extends Control {
	
	private Node content;
	protected ScrollCanvas internalPane;
	private Vector2i viewportSize;
	
	private ScrollBar vBar;
	private ScrollBar hBar;
	
	private double thickness = 7;
	private double barPadding = 2;
	private double scrollGestureSpeedMultiplier = 5;
	
	private ScrollBar holdingBar;
	private ScrollBar hoveredBar;
	
	private ObservableList<ScrollBar> scrollBars;
	
	public ScrollPane() {
		this.setPrefSize(100, 100);
		this.setAlignment(Pos.TOP_LEFT);
		this.flag_clip = true;
		this.viewportSize = new Vector2i();
		
		this.setBackground(Theme.currentTheme().getPane());
		
		this.vBar = new ScrollBar(Orientation.VERTICAL);
		this.hBar = new ScrollBar(Orientation.HORIZONTAL);
		this.scrollBars = new ObservableList<ScrollBar>();
		this.scrollBars.add(vBar);
		this.scrollBars.add(hBar);
		
		this.internalPane = new ScrollCanvas();
		
		this.mouseScrollEventInternal = new ScrollEvent() {
			@Override
			public void onEvent(double x, double y) {
				if ( isDecendentHovered() ) {
					vBar.pixel -= y*scrollGestureSpeedMultiplier;
					hBar.pixel -= x*scrollGestureSpeedMultiplier;
				}
			}	
		};
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);

		viewportSize.set((int)getWidth(),(int)getHeight());
		if ( vBar.active )
			viewportSize.x -= (thickness+barPadding*2);
		if ( hBar.active )
			viewportSize.y -= (thickness+barPadding*2);
		
		if ( content != null ) {
			
			// Update internal content
			this.internalPane.setParent(null);
			this.updateChildren();
			int px = (int) (viewportSize.x-getPadding().getWidth()-1);
			int py = (int) (viewportSize.y-getPadding().getHeight()-1);
			//System.out.println(px + " / " + py);
			sizeInternal(Integer.MAX_VALUE, Integer.MAX_VALUE);
			content.setPrefSize(px, py);
			children.add(internalPane);
			internalPane.updateChildren();
			children.remove(internalPane);
			internalPane.setAbsolutePosition(this.getAbsoluteX()+1, this.getAbsoluteY()+1);
			content.setAbsolutePosition(internalPane.getAbsoluteX()+this.padding.getLeft()-hBar.pixel, internalPane.getAbsoluteY()+this.padding.getTop()-vBar.pixel);
			sizeInternal(viewportSize.x-1, viewportSize.y-1);
			this.internalPane.setParent(this);
			
			// Update scrollbars
			vBar.update(viewportSize.y, content.getHeight()+this.padding.getHeight());
			hBar.update(viewportSize.x, content.getWidth()+this.padding.getWidth());
		} else {
			hBar.active = false;
			vBar.active = false;
		}
		
		updateBars();
	}
	
	private void sizeInternal(double x, double y) {
		this.internalPane.setMinSize(x, y);
		this.internalPane.setMaxSize(x, y);
		this.internalPane.setPrefSize(x, y);
	}

	@Override
	public ObservableList<Node> getChildren() {
		return new ObservableList<Node>(internalPane);
	}
	
	private boolean click = false;
	private boolean released = true;
	private Vector2d mouseGrabLocation = new Vector2d();
	private void updateBars() {

		// Get mouse coordinates
		Window window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
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
		Window window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
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
	
	public void setScrollBarThickness(double thickness) {
		this.thickness = thickness;
	}

	@Override
	public void render(Context context) {
		this.clip(context);
		
		if ( this.getBackground() != null ) {
			LWJGUIUtil.fillRect(context, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), this.getBackground());
		}

		this.internalPane.render(context);
		
		hoveredBar = getBarUnderMouse();
		
		this.clip(context);
		if ( vBar.active ) // Vertical scrollbar track
			LWJGUIUtil.fillRect(context, getAbsoluteX()+viewportSize.x-0.5, getAbsoluteY()+1, getWidth()-viewportSize.x-1, getHeight()-2, Theme.currentTheme().getControl());
		if ( hBar.active ) // Horizontal scrollbar track
			LWJGUIUtil.fillRect(context, getAbsoluteX()+1, getAbsoluteY()+viewportSize.y, getWidth()-2, getHeight()-viewportSize.y-1, Theme.currentTheme().getControl());
		if ( vBar.active ) // Vertical scrollbar track outline
			LWJGUIUtil.fillRect(context, getAbsoluteX()+viewportSize.x-1.5, getAbsoluteY()+1, 1, viewportSize.y-2, Theme.currentTheme().getSelectionPassive());
		if ( hBar.active ) // Horizontal scrollbar track outline
			LWJGUIUtil.fillRect(context, getAbsoluteX()+1, getAbsoluteY()+viewportSize.y-1, viewportSize.x-1, 1, Theme.currentTheme().getSelectionPassive());
		
		// Draw bars
		for (int i = 0; i < scrollBars.size(); i++) {
			ScrollBar b = scrollBars.get(i);
			if ( b.active ) {
				b.render(context);
			}
		}
		
		// Pane Outline
		if ( this.getBackground() != null ) {
			Color outlineColor = this.isDecendentSelected()?Theme.currentTheme().getSelection():Theme.currentTheme().getControlOutline();
			LWJGUIUtil.outlineRect( context, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), outlineColor);
		}
	}
	
	public void setContent( Node content ) {
		this.content = content;
		this.internalPane.getChildren().clear();
		this.internalPane.getChildren().add(content);
	}
	
	public Node getContent() {
		return this.content;
	}
	
	static class ScrollCanvas extends Pane {
		ScrollCanvas() {
			this.flag_clip = true;
			this.setBackground(null);
			this.setAlignment(Pos.TOP_LEFT);
		}
		
		public void setParent(Node node) {
			this.parent = node;
		}
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
			this.minScrollLen = minScrollLen;
			this.contentLen = contentLen;
			
			// Calculate if scrollbar is active
			boolean a = false;
			if ( !policy.equals(ScrollBarPolicy.NEVER) && content != null ) {
				if ( contentLen > minScrollLen || policy.equals(ScrollBarPolicy.ALWAYS) ) {
					a = true;
				}
			}
			active = a;
			
			// limit view of internal canvas
			if ( pixel < 0 )
				pixel = 0;
			if ( pixel + scrollSpaceToPixelSpace(length) > contentLen )
				pixel = contentLen - scrollSpaceToPixelSpace(length);
			
			// Calculate length of scrollbar
			length = pixelSpaceToScrollSpace(minScrollLen);
			if ( length > pixelSpaceToScrollSpace(contentLen) ) {
				length = pixelSpaceToScrollSpace(contentLen);
			}
		}
		
		public Vector4d getBounds() {
			if ( this.orientation.equals(Orientation.HORIZONTAL) ) {
				return new Vector4d( 
						(int)(getAbsoluteX()+pixelSpaceToScrollSpace(pixel)),
						(int)(getAbsoluteY()+viewportSize.y+barPadding),
						(int)Math.max(length,1),
						(int)thickness
						);
			} else {
				return new Vector4d( 
						(int)(getAbsoluteX()+viewportSize.x+barPadding-0.5),
						(int)(getAbsoluteY()+pixelSpaceToScrollSpace(pixel)),
						(int)thickness,
						(int)Math.max(length,1)
						);
			}
		}

		public double pixelSpaceToScrollSpace(double pixel) {
			return (pixel / contentLen) * minScrollLen;
		}
		
		public double scrollSpaceToPixelSpace(double scroll) {
			return (scroll/minScrollLen)*contentLen;
		}
		
		public void render(Context context) {
			Vector4d bd = getBounds();
			double barThickness = thickness;
			double x1 = bd.x;
			double y1 = bd.y+2;
			double x2 = bd.x;
			double y2 = bd.y+bd.w-barThickness-1;
			double len = y2-y1;
			if ( this.orientation.equals(Orientation.HORIZONTAL) ) {
				x1 = bd.x+2;
				y1 = bd.y;
				x2 = bd.x+bd.z-barThickness-1;
				y2 = bd.y;
				len = x2-x1;
			}
			Color color = Theme.currentTheme().getControlOutline();
			LWJGUIUtil.fillRoundRect(context, x1, y1, barThickness, barThickness, barThickness/2f, color);
			LWJGUIUtil.fillRoundRect(context, x2, y2, barThickness, barThickness, barThickness/2f, color);
			if ( len > 0 ) {
				if ( this.orientation.equals(Orientation.VERTICAL) ) {
					LWJGUIUtil.fillRect(context, x1, y1+barThickness/2f, barThickness, len, color);
				} else {
					LWJGUIUtil.fillRect(context, x1+barThickness/2f, y1, len, barThickness, color);
				}
			}

		}
	}
}
