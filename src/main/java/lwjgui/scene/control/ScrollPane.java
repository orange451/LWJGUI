package lwjgui.scene.control;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.glfw.input.MouseHandler;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Pane;
import lwjgui.style.BorderStyle;
import lwjgui.theme.Theme;

public class ScrollPane extends Pane {

	private Node content;
	protected ScrollPaneCanvas internalCanvas;
	private double thickness = 7;
	private double barPadding = 2;
	private double scrollGestureSpeedMultiplier = 20;
	
	protected ScrollBar vBar;
	protected ScrollBar hBar;
	private List<ScrollBar> scrollBars;
	
	private Vector2f viewport = new Vector2f();
	
	public ScrollPane() {
		this.flag_clip = true;
		this.internalCanvas = new ScrollPaneCanvas();
		this.setPrefSize(150, 150);
		
		this.vBar = new ScrollBar(Orientation.VERTICAL);
		this.hBar = new ScrollBar(Orientation.HORIZONTAL);
		
		this.scrollBars = new ArrayList<ScrollBar>();
		this.scrollBars.add(hBar);
		this.scrollBars.add(vBar);
		
		this.setOnMouseScrollInternal( (event) -> {
			if (isDescendentHovered()) {
				double newvPixel = vBar.pixel-event.y*scrollGestureSpeedMultiplier;
				double newhPixel = hBar.pixel-event.x*scrollGestureSpeedMultiplier;

				newvPixel = Math.max(0, Math.min(vBar.maxPixel, newvPixel));
				newhPixel = Math.max(0, Math.min(hBar.maxPixel, newhPixel));
				
				vBar.pixel = newvPixel;
				hBar.pixel = newhPixel;
				
				position(this.getParent());
			}
		});
		
		this.setBorderStyle(BorderStyle.SOLID);
		this.setBorderWidth(1);
		this.setBorderColor(Theme.current().getControlOutline());
	}
	
	@Override
	protected void position(Node node) {
		super.position(node);
		//this.internalCanvas.position(this);
	}
	
	private void updateScrollPane() {
		this.viewport.set((float)this.getInnerBounds().getWidth(), (float)this.getInnerBounds().getHeight());
		if ( hBar.active )
			this.viewport.y -= (thickness+barPadding*2);
		if ( vBar.active )
			this.viewport.x -= (thickness+barPadding*2);
		
		updateBars();
		
		// Update internal canvas
		this.internalCanvas.forceSize(Integer.MAX_VALUE,Integer.MAX_VALUE);
		this.internalCanvas.setParent(null);
		this.internalCanvas.updateChildren();
		this.internalCanvas.setParent(this);
		internalCanvas.setAbsolutePosition(getX()+this.getInnerBounds().getX(), getY()+this.getInnerBounds().getY());
		internalCanvas.forceSize(this.viewport.x, this.viewport.y);
		
		
		// Position content relative to scrollbars
		if ( content != null && this.getParent() != null ) {
			content.setLocalPosition(internalCanvas, -hBar.pixel, -vBar.pixel);
		}

		// Update scrollbars
		if ( content != null ) {
			hBar.update(viewport.x, content.getWidth()+(internalCanvas.getWidth()-internalCanvas.getInnerBounds().getWidth()));
			vBar.update(viewport.y, content.getHeight()+(internalCanvas.getHeight()-internalCanvas.getInnerBounds().getHeight()));
		}
	}
	
	private boolean click = false;
	private boolean released = true;
	private Vector2d mouseGrabLocation = new Vector2d();
	private ScrollBar holdingBar;
	private ScrollBar hoveredBar;
	private void updateBars() {
		
		// Get mouse coordinates
		MouseHandler mh = window.getMouseHandler();
		double mx = mh.getX();
		double my = mh.getY();
		
		// Get mouse pressed
		boolean mouse = window.getMouseHandler().isButtonPressed(0);
		
		// Check if we're clicking
		if (!click && mouse && released )
			click = true;
		else if ( click && mouse) {
			released = false;
			click = false;
		} else if (!mouse ) {
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
		if ( !mouse ) {
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
		MouseHandler mh = window.getMouseHandler();
		double mx = mh.getX();
		double my = mh.getY();
		
		for (int i = 0; i < scrollBars.size(); i++) {
			ScrollBar d = scrollBars.get(i);
			Vector4d bounds = d.getBounds();
			if ( mx > bounds.x && mx < bounds.x+bounds.z && my > bounds.y && my < bounds.y+bounds.w) {
				return d;
			}
		}
		
		return null;
	}
	
	public void setVvalue(double value) {
		vBar.pixel = value*vBar.contentLen;
	}
	
	public void setHvalue(double value) {
		hBar.pixel = value*hBar.contentLen;
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
	
	public double getScrollBarThickness() {
		return this.thickness;
	}
	
	public void setScrollBarThickness(double thickness) {
		this.thickness = thickness;
	}
	
	@Override
	public String getElementType() {
		return "scrollpane";
	}
	
	/**
	 * Returns the width of the viewable region of the scroll pane.
	 * @return
	 */
	public double getViewportWidth() {
		return viewport.x;
	}
	
	/**
	 * Returns the height of the viewable region of the scroll pane.
	 * @return
	 */
	public double getViewportHeight() {
		return viewport.y;
	}

	public void setContent( Node content ) {
		this.content = content;
		
		this.internalCanvas.getChildren().clear();
		this.internalCanvas.getChildren().add(content);
	}
	
	/**
	 * Returns unmodifiable list of children.
	 */
	@Override
	public ObservableList<Node> getChildren() {
		return new ObservableList<Node>(internalCanvas);
	}
	
	public Node getContent() {
		return this.content;
	}
	
	public void setInternalPadding(Insets insets) {
		this.internalCanvas.setPadding(insets);
	}
	
	public Insets getInternalPadding() {
		return this.internalCanvas.getPadding();
	}
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		updateScrollPane();
		
		super.render(context);
		
		// Setup scissor
		if ( context != null )
			NanoVG.nvgScissor(context.getNVG(), (float)internalCanvas.getX(), (float)internalCanvas.getY(), (float)internalCanvas.getWidth(), (float)internalCanvas.getHeight());
		hoveredBar = getBarUnderMouse();
		
		clip(context, 0);
		
		double svx = getX()+viewport.x;
		double svy = getY()+1;
		double svw = getWidth()-viewport.x-1;
		double svh = getHeight()-2; //viewport.y-2

		double shx = getX()+1;
		double shy = getY()+viewport.y;
		double shw = getWidth()-2;
		double shh = getHeight()-viewport.y-1;
		
		// Vertical scrollbar track
		if ( vBar.active ) {
			LWJGUIUtil.fillRoundRect(context, svx, svy, svw, svh, 0, this.getBorderRadii()[1]-1, this.getBorderRadii()[2]-1, 0, Theme.current().getControl());
		} 
		
		// Horizontal scrollbar track
		if ( hBar.active ) {
			LWJGUIUtil.fillRoundRect(context, shx, shy, shw, shh, 0, 0, this.getBorderRadii()[2]-1, this.getBorderRadii()[3]-1, Theme.current().getControl());
		}
		
		// Vertical scrollbar track outline
		if ( vBar.active ) {
			LWJGUIUtil.fillRect(context, svx, svy, 1, svh-shh, Theme.current().getSelectionPassive());
		}
		
		// Horizontal scrollbar track outline
		if ( hBar.active ) {
			LWJGUIUtil.fillRect(context, shx, shy, shw-svw, 1, Theme.current().getSelectionPassive());
		}
		
		if ( hBar.active )
			hBar.render(context);
		
		if ( vBar.active )
			vBar.render(context);
	}
	
	class ScrollPaneCanvas extends Pane {
		ScrollPaneCanvas() {
			this.setAlignment(Pos.TOP_LEFT);
			this.setPadding(Insets.EMPTY);
		}

		@Override
		public String getElementType() {
			return "scrollcanvas";
		}
		
		public void setParent(Node node) {
			this.parent = node;
		}
		
		@Override
		public void position(Node parent) {
			this.setBorderRadii(ScrollPane.this.getBorderRadii());
			super.position(parent);
		}
		
		@Override
		public void render(Context context) {
			this.clip(context, -16);
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
						(int)(getY()+viewport.y+barPadding),
						(int)Math.max(length,1),
						(int)thickness
						);
			} else {
				return new Vector4d( 
						(int)(getX()+viewport.x+barPadding),
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
			return (scroll/minScrollLen) * contentLen;
		}
		
		public void render(Context context) {
			Vector4d bd = getBounds();
			double barThickness = thickness;
			double x1 = bd.x+1;
			double y1 = bd.y+2;
			double x2 = bd.x+barThickness;
			double y2 = bd.y+bd.w-1;
			if ( this.orientation.equals(Orientation.HORIZONTAL) ) {
				x1 = bd.x+2;
				y1 = bd.y+1;
				x2 = bd.x+bd.z-1;
				y2 = bd.y+barThickness;
			}
			Color color = Theme.current().getControlOutline();
			LWJGUIUtil.fillRoundRect(context, x1, y1, x2-x1, y2-y1, barThickness/2f, color);
		}
	}
}
