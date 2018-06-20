package lwjgui.scene;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.event.KeyEvent;
import lwjgui.event.MouseEvent;
import lwjgui.event.ScrollEvent;
import lwjgui.geometry.HPos;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.geometry.Resizable;
import lwjgui.geometry.VPos;

public abstract class Node implements Resizable {
	protected ObservableList<Node> children = new ObservableList<Node>();
	protected Vector2d localPosition = new Vector2d();
	protected Vector2d absolutePosition = new Vector2d();
	protected Vector2d size = new Vector2d();
	protected Vector2d prefsize = new Vector2d();
	protected LayoutBounds layoutBounds = new LayoutBounds(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
	protected Insets padding = new Insets(0,0,0,0);
	protected Pos alignment;
	protected Node parent;

	protected MouseEvent mousePressedEvent;
	protected MouseEvent mouseReleasedEvent;
	protected MouseEvent mouseEnteredEvent;
	protected MouseEvent mouseLeftEvent;
	protected MouseEvent mouseDraggedEvent;
	protected ScrollEvent mouseScrollEvent;
	protected ScrollEvent mouseScrollEventInternal;
	protected KeyEvent textInputEvent;
	protected KeyEvent keyPressedEvent;
	
	private boolean mouseTransparent = false;
	protected boolean flag_clip = false;
	protected boolean mousePressed = false;
	
	@Deprecated
	protected Context cached_context;
	
	public void setLocalPosition(Node parent, double x, double y) {
		double changex = x-this.getX();
		double changey = y-this.getY();
		this.parent = parent;
		this.localPosition.x = x;
		this.localPosition.y = y;
		this.absolutePosition.x += changex;
		this.absolutePosition.y += changey;
		
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			child.setLocalPosition(this, child.getX(), child.getY());
		}
	}
	
	public void setAbsolutePosition( double x, double y ) {
		this.absolutePosition.set(x,y);
	}
	
	public void offset( double x, double y ) {
		//this.localPosition.add(x, y);
		this.absolutePosition.add(x,y);
		
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			child.setLocalPosition(this, child.getX(), child.getY());
		}
	}
	
	public void updateChildren() {
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			child.position(this);
		}
	}
	
	protected void position(Node parent) {
		this.parent = parent;
		
		cached_context = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext();
		
		updateChildren();
		resize();
		
		Pos useAlignment = getAlignment();
		if ( parent != null ) {
			useAlignment = parent.getAlignment();
		}
		
		double xMult = 0;
		if ( useAlignment.getHpos() == HPos.CENTER)
			xMult = 0.5f;
		if ( useAlignment.getHpos() == HPos.RIGHT)
			xMult = 1;
		
		double yMult = 0;
		if ( useAlignment.getVpos() == VPos.CENTER)
			yMult = 0.5f;
		if ( useAlignment.getVpos() == VPos.BOTTOM)
			yMult = 1f;
		
		if ( parent != null ) {
			LayoutBounds bounds = parent.getInnerBounds();
			
			localPosition.x = (bounds.getWidth()-size.x)*xMult;
			localPosition.y = (bounds.getHeight()-size.y)*yMult;
			
			float topLeftX = (float) (parent.absolutePosition.x + bounds.minX);
			float topLeftY = (float) (parent.absolutePosition.y + bounds.minY);
			
			absolutePosition.x = topLeftX + localPosition.x;
			absolutePosition.y = topLeftY + localPosition.y;
		}
	}
	
	/**
	 * Returns the scene that contains this node.
	 * @return
	 */
	public Scene getScene() {
		Node p = this;
		while ( p != null ) {
			if ( p instanceof Scene ) {
				return (Scene)p;
			}
			
			p = p.parent;
		}
		
		return null;
	}
	
	/**
	 * Return a vector containing the available size derived from the parent nodes.
	 * @return Vector2d
	 */
	public Vector2d getAvailableSize() {
		//float availableWidth = (float) getMaxWidth();
		//float availableHeight = (float) getMaxHeight();
		return new Vector2d(getMaxPotentialWidth(), getMaxPotentialHeight());
	}
	
	protected void resize() {
		
		// Resize if smaller than pref size
		synchronized(size) {
			// Size up to pref size
			if ( size.x < prefsize.x )
				size.x = prefsize.x;
			if ( size.y < prefsize.y )
				size.y = prefsize.y;
			
			// Get available size
			Vector2d available = this.getAvailableSize();
			double availableWidth = available.x;
			double availableHeight = available.y;
			
			/*if ( parent != null )
				System.out.println(parent.getClass().getSimpleName() + " / " + availableHeight + " / " + parent.getInnerBounds().getPadHeight()
						+ " / " 
						+ parent.getInnerBounds().maxY
						+ " / " 
						+ parent.getInnerBounds().minY
						+ " / " 
						+ parent.getHeight()
						+ " ////////// " 
						+ (parent.getInnerBounds().minY + (parent.getHeight()-parent.getInnerBounds().maxY)));*/
			
			// Cap size to available size
			if ( size.x > availableWidth )
				size.x = availableWidth;
			if ( size.y > availableHeight )
				size.y = availableHeight;
			
			// Cap size to min size
			if ( size.x < this.getMinWidth() )
				size.x = this.getMinWidth();
			if ( size.y < this.getMinHeight() )
				size.y = this.getMinHeight();
			
			// Cap size to max size
			if ( size.x > this.getMaxWidth() )
				size.x = this.getMaxWidth();
			if ( size.y > this.getMaxHeight() )
				size.y = this.getMaxHeight();
		}
		//System.out.println("    " + getWidth());
	}
	
	protected double getMaxPotentialWidth() {
		double max = Double.MAX_VALUE;
		Node p = this;
		
		while ( p != null ) {
			double padding = p.getWidth()-p.getInnerBounds().getWidth();
			double use = p.getMaxWidth()-padding;
			if ( use > Double.MAX_VALUE*0.9 )
				use = p.getWidth()-padding;
			
			max = Math.min(max, use);
			p = p.parent;
		}
		
		max = Math.max(max, getMinWidth());
		
		return max;
	}
	
	protected double getMaxPotentialHeight() {
		double max = Double.MAX_VALUE;
		Node p = this;
		
		while ( p != null ) {
			double use = p.getMaxHeight();
			if ( use > Double.MAX_VALUE*0.9 )
				use = p.getHeight();
			
			max = Math.min(max, use);
			p = p.parent;
		}
		
		max = Math.max(max, getMinHeight());
		
		return max;
	}
	
	protected double getMaxElementWidth() {
		double runningX = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double tempX = child.getX() + child.getWidth();
			if ( tempX > runningX ) {
				runningX = tempX;
			}
		}
		
		return runningX;
	}
	
	protected double getMaxElementHeight() {
		double runningY = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double tempY = child.getY() + child.getHeight();
			if ( tempY > runningY ) {
				runningY = tempY;
			}
		}
		
		return runningY;
	}
	
	/**
	 * Return the parent node.
	 * @return
	 */
	public Node getParent() {
		return this.parent;
	}
	
	protected void clip(Context context) {
		clip(context, 0);
	}
	
	protected void clip( Context context, int padding ) {
		LayoutBounds clipBoundsTemp = new LayoutBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		Node par = this.parent;
		while ( par != null ) {
			if ( par.flag_clip ) {
				LayoutBounds tempBounds = new LayoutBounds((int)par.getAbsoluteX(), (int)par.getAbsoluteY(), (int)Math.ceil(par.getAbsoluteX()+par.getWidth()+0.5), (int)Math.ceil(par.getAbsoluteY()+par.getHeight()+0.5));
				if ( tempBounds.minX > clipBoundsTemp.minX )
					clipBoundsTemp.minX = tempBounds.minX;
				if ( tempBounds.minY > clipBoundsTemp.minY )
					clipBoundsTemp.minY = tempBounds.minY;
				if ( tempBounds.maxX < clipBoundsTemp.maxX )
					clipBoundsTemp.maxX = tempBounds.maxX;
				if ( tempBounds.maxY < clipBoundsTemp.maxY )
					clipBoundsTemp.maxY = tempBounds.maxY;
			}
			
			par = par.parent;
		}
		
		NanoVG.nvgScissor(context.getNVG(), clipBoundsTemp.minX, clipBoundsTemp.minY, clipBoundsTemp.getWidth(), clipBoundsTemp.getHeight());
	}
	
	protected boolean isDecendentSelected() {
		if ( cached_context == null ) {
			return false;
		}
		Node selected = cached_context.getSelected();
		if ( selected == null )
			return false;
		
		Node p = selected;
		if ( !p.isDescendentOf(this) )
			return false;
		
		while(p != null) {
			if ( p.getClass().isAssignableFrom(this.getClass()) ) {
				return true;
			}
			p = p.getParent();
		}
		
		return false;
		/*if ( cached_context != null && cached_context.isSelected(this) ) {
			return true;
		}
		if ( cached_context != null )
			System.out.println("Checking " + this.toString() + "   /  " + cached_context.isSelected(this));

		ObservableList<Node> c = getChildren();
		for (int i = 0; i < c.size(); i++) {
			Node child = c.get(i);
			if ( child.isDecendentSelected() ) {
				return true;
			}
		}
		
		return false;*/
	}
	
	protected boolean isDecendentHovered() {
		Window window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
		Context context = window.getContext();
		Node hovered = context.getHovered();
		if ( hovered == null ) {
			return false;
		}
		
		Node p = hovered;
		if ( !p.isDescendentOf(this) )
			return false;
		
		while(p != null) {
			if ( p.getClass().isAssignableFrom(this.getClass()) ) {
				return true;
			}
			p = p.getParent();
		}
		
		return false;
	}
	
	/**
	 * Returns whether this node is a descendent of the supplied parent node.
	 * @param parent
	 * @return
	 */
	public boolean isDescendentOf(Node parent) {
		Node t = this;
		while ( t != null ) {
			if ( t.equals(parent) ) {
				return true;
			}
			t = t.getParent();
		}
		
		return false;
	}
	
	/**
	 * Return a bounds fit to the size of the node.
	 * @return
	 */
	public LayoutBounds getInnerBounds() {
		return new LayoutBounds(0, 0, (int)getWidth(), (int)getHeight());
	}
	
	/**
	 * Return the current width of the node.
	 * @return
	 */
	public double getWidth() {
		return size.x;
	}
	
	/**
	 * Return the current height of the node.
	 * @return
	 */
	public double getHeight() {
		return size.y;
	}
	
	/**
	 * Set the layout alignment.
	 * @param pos
	 */
	public void setAlignment(Pos pos) {
		this.alignment = pos;
	}
	
	/**
	 * Return the current layout alignment.
	 * @return
	 */
	public Pos getAlignment() {
		Pos useAlignment = Pos.CENTER;
		if ( parent != null ) {
			if ( parent.getAlignment() != null ) {
				useAlignment = parent.getAlignment();
			}
		}
		if ( alignment != null ) {
			useAlignment = alignment;
		}
		
		return useAlignment;
	}
	
	/**
	 * Return the modifiable list of children.
	 * @return
	 */
	protected ObservableList<Node> getChildren() {
		return this.children;
	}
	
	/**
	 * Return the local offset x of this node.
	 * @return
	 */
	public double getX() {
		return localPosition.x;
	}
	
	/**
	 * Return the local offset y of this node.
	 * @return
	 */
	public double getY() {
		return localPosition.y;
	}

	/**
	 * Return the absolute x position of this node.
	 * @return
	 */
	public double getAbsoluteX() {
		return absolutePosition.x;
	}
	
	/**
	 * Return the absolute y position of this node.
	 * @return
	 */
	public double getAbsoluteY() {
		return absolutePosition.y;
	}
	
	/**
	 * Return the minimum width of this node.
	 * @return
	 */
	public double getMinWidth() {
		return layoutBounds.minX;
	}
	
	/**
	 * Return the minimum height of this node.
	 * @return
	 */
	public double getMinHeight() {
		return layoutBounds.minY;
	}
	
	/**
	 * Return the maximum width of this node.
	 * @return
	 */
	public double getMaxWidth() {
		return layoutBounds.maxX;
	}
	
	/**
	 * Return the maximum height of this node.
	 * @return
	 */
	public double getMaxHeight() {
		return layoutBounds.maxY;
	}
	
	/**
	 * Set the preferred size of this node.
	 * <br>
	 * This size is not guaranteed.
	 * @param width
	 * @param height
	 */
	public void setPrefSize( double width, double height ) {
		setPrefWidth( width );
		setPrefHeight( height );
	}
	
	/**
	 * Set the absolute minimum size of this node.
	 * @param width
	 * @param height
	 */
	public void setMinSize( double width, double height ) {
		setMinWidth(width);
		setMinHeight(height);
	}
	
	/**
	 * Set the absolute maximum size of this node.
	 * @param width
	 * @param height
	 */
	public void setMaxSize( double width, double height ) {
		setMaxWidth(width);
		setMaxHeight(height);
	}
	
	/**
	 * Set the preferred width of this node.
	 * <br>
	 * This size is not guaranteed.
	 * @param width
	 */
	public void setPrefWidth( double width ) {
		this.prefsize.x = width;
		if (this.getMinWidth() > 0) {
			width = Math.max(layoutBounds.minX, Math.min(layoutBounds.maxX, width));
		}
		this.size.x = width;
	}
	
	/**
	 * Set the preferred height of this node.
	 * <br>
	 * This size is not guaranteed.
	 * @param width
	 */
	public void setPrefHeight( double height ) {
		this.prefsize.y = height;
		if ( this.getMinHeight() > 0 ) {
			height = Math.max(layoutBounds.minY, Math.min(layoutBounds.maxY, height));
		}
		this.size.y = height;
	}
	
	/**
	 * Returns the preferred width of this node.
	 * @return
	 */
	public double getPrefWidth() {
		return prefsize.x;
	}
	
	/**
	 * Returns the preferred height of this node.
	 * @return
	 */
	public double getPrefHeight() {
		return prefsize.y;
	}
	
	/**
	 * Set the minimum width of this node.
	 * @param width
	 */
	public void setMinWidth( double width ) {
		layoutBounds.minX = (int)width;
		if ( size.x < (int)width )
			size.x = (int)width;
	}
	
	/**
	 * Set the minimum height of this node.
	 * @param height
	 */
	public void setMinHeight( double height ) {
		layoutBounds.minY = (int)height;
		if ( size.y < (int)height )
			size.y = (int)height;
	}
	
	/**
	 * Set the maximum width of this node.
	 * @param width
	 */
	public void setMaxWidth( double width ) {
		layoutBounds.maxX = (int)width;
		if ( size.x > (int)width )
			size.x = (int)width;
	}
	
	/**
	 * Set the maxmimum height of this node.
	 * @param height
	 */
	public void setMaxHeight( double height ) {
		layoutBounds.maxY = (int)height;
		if ( size.y > (int)height )
			size.y = (int)height;
	}
	
	public class LayoutBounds {
		protected int minX;
		protected int minY;
		protected int maxX;
		protected int maxY;
		
		public LayoutBounds(int minX, int minY, int maxX, int maxY) {
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}

		/**
		 * Returns the height of the padding of this node.
		 * @return
		 */
		public double getPadHeight() {
			return minY + (Node.this.getHeight()-maxY);
		}

		/**
		 * Returns the width of the padding of this node.
		 * @return
		 */
		public double getPadWidth() {
			return minX + (Node.this.getWidth()-maxX);
		}

		/**
		 * Returns the internal width of this node.
		 * @return
		 */
		public float getWidth() {
			return maxX - minX;
		}
		
		/**
		 * Returns the internal height of this node.
		 * @return
		 */
		public float getHeight() {
			return maxY - minY;
		}
	}
	
	protected void onMousePressed( int button ) {
		mousePressed = true;
		
		if ( mousePressedEvent != null ) {
			this.mousePressedEvent.onEvent(button);
		}
	}
	
	protected void onMouseReleased( int button ) {
		if ( !mousePressed )
			return;
		mousePressed = false;
		
		if ( mouseReleasedEvent != null ) {
			this.mouseReleasedEvent.onEvent(button);
		}
	}
	
	protected void onMouseEntered() {
		if ( this.mouseEnteredEvent != null ) {
			this.mouseEnteredEvent.onEvent(-1);
		}
	}
	
	protected void onMouseLeft() {
		if ( this.mouseLeftEvent != null ) {
			this.mouseLeftEvent.onEvent(-1);
		}
	}
	
	public void setMouseEnteredEvent( MouseEvent event ) {
		this.mouseEnteredEvent = event;
	}
	
	public void setMouseLeftEvent( MouseEvent event ) {
		this.mouseLeftEvent = event;
	}
	
	public void setMousePressedEvent( MouseEvent event ) {
		this.mousePressedEvent = event;
	}
	
	public void setMouseReleasedEvent( MouseEvent event ) {
		this.mouseReleasedEvent = event;
	}
	
	public void setMouseScrollGestureEvent( ScrollEvent event ) {
		this.mouseScrollEvent = event;
	}
	
	public void setMouseDraggedEvent( MouseEvent event ) {
		this.mouseDraggedEvent = event;
	}
	
	public void setOnTextInput( KeyEvent event ) {
		this.textInputEvent = event;
	}
	
	public void setOnKeyPressed( KeyEvent event ) {
		this.keyPressedEvent = event;
	}

	public abstract void render(Context context);

	/**
	 * Returns whether or not this node (and all of hits children) will ignore the mouse.
	 * @return
	 */
	public boolean isMouseTransparent() {
		return this.mouseTransparent;
	}
	
	/**
	 * Flag that controls whether this node (and all of its children) ignore the mouse.
	 * @param t
	 */
	public void setMouseTransparent(boolean t ) {
		this.mouseTransparent = t;
	}
}
