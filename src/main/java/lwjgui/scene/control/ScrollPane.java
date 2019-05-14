package lwjgui.scene.control;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.Node;
import lwjgui.scene.Window;
import lwjgui.scene.layout.Pane;
import lwjgui.theme.Theme;

public class ScrollPane extends FillableRegion {
	
	private Node content;
	protected ScrollCanvas internalScrollCanvas;
	private Vector2i viewportSize;
	
	protected ScrollBar vBar;
	protected ScrollBar hBar;
	
	private double thickness = 7;
	private double barPadding = 2;
	private double scrollGestureSpeedMultiplier = 8;
	
	private ScrollBar holdingBar;
	private ScrollBar hoveredBar;
	
	private ObservableList<ScrollBar> scrollBars;
	
	/*
	 * Customization
	 */
	
	private Color selectionFill = Theme.current().getSelection();
	private Color selectionPassiveFill = Theme.current().getBackgroundAlt();
	private Color controlFill = Theme.current().getControl();
	
	private Color controlOutlineFill = Theme.current().getControlOutline();
	protected boolean decorated = true;
	
	public ScrollPane() {
		this.setPrefSize(100, 100);
		this.setAlignment(Pos.TOP_LEFT);
		this.flag_clip = true;
		this.viewportSize = new Vector2i();
		
		//this.setBackground(Theme.current().getPane());
		
		this.vBar = new ScrollBar(Orientation.VERTICAL);
		this.hBar = new ScrollBar(Orientation.HORIZONTAL);
		this.scrollBars = new ObservableList<ScrollBar>();
		this.scrollBars.add(vBar);
		this.scrollBars.add(hBar);
		
		this.internalScrollCanvas = new ScrollCanvas();
		
		this.setOnMouseScrollInternal( (event) -> {
			if (isDescendentHovered()) {
				double newvPixel = vBar.pixel-event.y*scrollGestureSpeedMultiplier;
				double newhPixel = hBar.pixel-event.x*scrollGestureSpeedMultiplier;

				newvPixel = Math.max(0, Math.min(vBar.maxPixel, newvPixel));
				newhPixel = Math.max(0, Math.min(hBar.maxPixel, newhPixel));
				
				vBar.pixel = newvPixel;
				hBar.pixel = newhPixel;
				
				update();
			}
		});
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	private void update() {
		viewportSize.set((int)getWidth()-1,(int)getHeight()-1);
		if ( vBar.active )
			viewportSize.x -= (thickness+barPadding*2);
		if ( hBar.active )
			viewportSize.y -= (thickness+barPadding*2);
		
		if ( content != null ) {
			
			// Update internal content
			this.internalScrollCanvas.setParent(null);
			this.updateChildren();
			int px = (int) (viewportSize.x-getPadding().getWidth()-1);
			int py = (int) (viewportSize.y-getPadding().getHeight()-1);
			
			sizeInternal(Integer.MAX_VALUE, Integer.MAX_VALUE);
			content.setPrefSize(px, py);
			
			children.add(internalScrollCanvas);
			internalScrollCanvas.updateChildren();
			children.remove(internalScrollCanvas);
			
			internalScrollCanvas.setAbsolutePosition(this.getX()+1, this.getY()+1);
			content.setAbsolutePosition(internalScrollCanvas.getX()+this.padding.getLeft()-hBar.pixel, internalScrollCanvas.getY()+this.padding.getTop()-vBar.pixel);
			
			sizeInternal(viewportSize.x-1, viewportSize.y-1);
			this.internalScrollCanvas.setParent(this);
			
			// Update scrollbars
			vBar.update(viewportSize.y, content.getHeight()+this.padding.getHeight());
			hBar.update(viewportSize.x, content.getWidth()+this.padding.getWidth());
			
			//
			//internalScrollCanvas.calculateNodeBounds();
		} else {
			hBar.active = false;
			vBar.active = false;
		}
		
		updateBars();
	}
	
	/**
	 * Returns the width of the viewable region of the scroll pane.
	 * @return
	 */
	public double getViewportWidth() {
		return viewportSize.x;
	}
	
	/**
	 * Returns the height of the viewable region of the scroll pane.
	 * @return
	 */
	public double getViewportHeight() {
		return viewportSize.y;
	}
	
	@Override
	protected void position(Node parent) {
		update();
		
		super.position(parent);
	}

	public void setVvalue(double value) {
		vBar.pixel = value*vBar.contentLen;
	}
	
	public void setHvalue(double value) {
		hBar.pixel = value*hBar.contentLen;
	}
	
	private void sizeInternal(double x, double y) {
		this.internalScrollCanvas.setMinSize(x, y);
		this.internalScrollCanvas.setMaxSize(x, y);
		this.internalScrollCanvas.setPrefSize(x, y);
	}

	public Color getSelectionFill() {
		return selectionFill;
	}

	public void setSelectionFill(Color selectionFill) {
		this.selectionFill = selectionFill;
	}

	public Color getSelectionPassiveFill() {
		return selectionPassiveFill;
	}

	public void setSelectionPassiveFill(Color selectionPassiveFill) {
		this.selectionPassiveFill = selectionPassiveFill;
	}

	public Color getControlFill() {
		return controlFill;
	}

	public void setControlFill(Color controlFill) {
		this.controlFill = controlFill;
	}

	public Color getControlOutlineFill() {
		return controlOutlineFill;
	}

	public void setControlOutlineFill(Color controlOutlineFill) {
		this.controlOutlineFill = controlOutlineFill;
	}

	/**
	 * Returns unmodifiable list of children.
	 */
	@Override
	public ObservableList<Node> getChildren() {
		return new ObservableList<Node>(internalScrollCanvas);
	}
	
	private boolean click = false;
	private boolean released = true;
	private Vector2d mouseGrabLocation = new Vector2d();
	private void updateBars() {
		
		// Get mouse coordinates
		Context context = cached_context;
		if ( context == null )
			return;
		double mx = context.getMouseX();
		double my = context.getMouseY();
		
		// Get mouse pressed
		int mouse = GLFW.glfwGetMouseButton(GLFW.glfwGetCurrentContext(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
		
		// Check if we're clicking
		if (!click && mouse == GLFW.GLFW_PRESS && released )
			click = true;
		else if ( click && mouse == GLFW.GLFW_PRESS) {
			released = false;
			click = false;
		} else if (mouse != GLFW.GLFW_PRESS) {
			released = true;
		}
		
		if (click && hoveredBar != null) {
			holdingBar = hoveredBar;
			mouseGrabLocation.set(mx, my);
		}
		
		if (holdingBar == null) {
			return;
		}
		
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
		super.render(context);
		
		hoveredBar = getBarUnderMouse();

		clip(context, 0);

		double svx = getX()+viewportSize.x;
		double svy = getY()+1;
		double svw = getWidth()-viewportSize.x-1;
		double svh = getHeight()-2; //viewportSize.y-2

		double shx = getX()+1;
		double shy = getY()+viewportSize.y;
		double shw = getWidth()-2;
		double shh = getHeight()-viewportSize.y-1;
		
		// Vertical scrollbar track
		if ( vBar.active ) {
			LWJGUIUtil.fillRect(context, svx, svy, svw, svh, controlFill);
		} 
		
		// Horizontal scrollbar track
		if ( hBar.active ) {
			LWJGUIUtil.fillRect(context, shx, shy, shw, shh, controlFill);
		}
		
		// Vertical scrollbar track outline
		if ( vBar.active ) {
			LWJGUIUtil.fillRect(context, svx, svy, 1, svh-shh, selectionPassiveFill);
		}
		
		// Horizontal scrollbar track outline
		if ( hBar.active ) {
			LWJGUIUtil.fillRect(context, shx, shy, shw-svw, 1, selectionPassiveFill);
		}
		
		// Draw bars
		for (int i = 0; i < scrollBars.size(); i++) {
			ScrollBar b = scrollBars.get(i);
			if ( b.active ) {
				b.render(context);
			}
		}
		
		// Pane Outline
		if ( decorated ) {
			Color outlineColor = this.isDescendentSelected() ? selectionFill : controlOutlineFill;
			LWJGUIUtil.outlineRect( context, getX(), getY(), getWidth()-1, getHeight()-1, outlineColor);
		}
	}
	
	public void setContent( Node content ) {
		this.content = content;
		this.internalScrollCanvas.getChildren().clear();
		this.internalScrollCanvas.getChildren().add(content);
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
		
		@Override
		public void render(Context context) {
			this.clip(context,-1);
			super.render(context);
		}
	}
	
	public enum ScrollBarPolicy {
		ALWAYS, AS_NEEDED, NEVER;
	}
	
	private class ScrollBar {
		public boolean active;
		public double pixel;
		public double length;
		public double maxPixel;
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
			maxPixel = contentLen-scrollSpaceToPixelSpace(length);
			if ( pixel < 0 )
				pixel = 0;
			if ( pixel > maxPixel)
				pixel = maxPixel;
			
			// Calculate length of scrollbar
			length = pixelSpaceToScrollSpace(minScrollLen);
			if ( length > pixelSpaceToScrollSpace(contentLen) ) {
				length = pixelSpaceToScrollSpace(contentLen);
			}
		}
		
		public Vector4d getBounds() {
			if ( this.orientation.equals(Orientation.HORIZONTAL) ) {
				return new Vector4d( 
						(int)(getX()+pixelSpaceToScrollSpace(pixel)),
						(int)(getY()+viewportSize.y+barPadding),
						(int)Math.max(length,1),
						(int)thickness
						);
			} else {
				return new Vector4d( 
						(int)(getX()+viewportSize.x+barPadding),
						(int)(getY()+pixelSpaceToScrollSpace(pixel)),
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
			Color color = controlOutlineFill;
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
