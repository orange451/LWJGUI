package lwjgui.scene;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.event.Event;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
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
	protected Vector2d absolutePosition = new Vector2d();
	protected Vector2d size = new Vector2d();
	protected Vector2d prefsize = new Vector2d();
	protected LayoutBounds layoutBounds = new LayoutBounds(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
	protected Insets padding = new Insets(0,0,0,0);
	protected Pos alignment = Pos.CENTER;
	protected Node parent;

	protected EventHandler<MouseEvent> mousePressedEvent;
	protected EventHandler<MouseEvent> mouseReleasedEvent;
	protected EventHandler<MouseEvent> mouseClickedEvent;
	protected EventHandler<Event> mouseEnteredEvent;
	protected EventHandler<Event> mouseExitedEvent;
	protected EventHandler<MouseEvent> mouseDraggedEvent;
	protected EventHandler<ScrollEvent> mouseScrollEvent;
	protected EventHandler<ScrollEvent> mouseScrollEventInternal;
	protected EventHandler<KeyEvent> textInputEvent;
	protected EventHandler<KeyEvent> keyPressedEvent;
	protected EventHandler<KeyEvent> keyReleasedEvent;
	
	private boolean mouseTransparent = false;
	protected boolean flag_clip = false;
	protected boolean mousePressed = false;
	
	protected Context cached_context;
	
	public void setLocalPosition(Node parent, double x, double y) {
		//double changex = x-this.getX();
		//double changey = y-this.getY();
		this.parent = parent;
		//this.localPosition.x = x;
		//this.localPosition.y = y;
		
		LayoutBounds bounds = parent.getInnerBounds();
		
		float topLeftX = (float) (parent.absolutePosition.x + bounds.minX);
		float topLeftY = (float) (parent.absolutePosition.y + bounds.minY);
		
		double changex = (topLeftX + x)-absolutePosition.x;
		double changey = (topLeftY + y)-absolutePosition.y;
		
		setAbsolutePosition( this.getX()+changex, this.getY()+changey);
	}
	
	public void setAbsolutePosition( double x, double y ) {
		this.absolutePosition.set(x,y);
	}
	
	public void offset( double x, double y ) {
		//this.localPosition.add(x, y);
		setAbsolutePosition( this.getX()+x, this.getY()+y);
		
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			child.offset(x, y);
		}
	}
	
	public void updateChildren() {
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child != null ) {
				child.position(this);
			}
		}
	}
	
	protected void position(Node parent) {
		this.parent = parent;
		
		cached_context = LWJGUI.getCurrentContext();
		
		updateChildren();
		resize();
		
		Pos useAlignment = usingAlignment();
		
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
			
			double offsetX = (bounds.getWidth()-size.x)*xMult;
			double offsetY = (bounds.getHeight()-size.y)*yMult;
			
			float topLeftX = (float) (parent.absolutePosition.x + bounds.minX);
			float topLeftY = (float) (parent.absolutePosition.y + bounds.minY);
			
			absolutePosition.x = topLeftX + offsetX;
			absolutePosition.y = topLeftY + offsetY;
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
			//System.out.println(this.getClass() + "   /   preferred width: " + this.getPrefWidth());
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
			
			//if ( size.x > prefsize.x && size.x < availableWidth && size.x < this.getMaxWidth() )
				//size.x = prefsize.x;
			
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
	}
	
	protected double getMaxPotentialWidth() {
		double max = Double.MAX_VALUE;
		Node p = this;
		
		while ( p != null ) {
			/*double pWid = p.getWidth();
			double padding = pWid-p.getInnerBounds().getWidth();
			double use = p.getMaxWidth()-padding;
			if ( use > Double.MAX_VALUE*0.9 )
				use = pWid-padding;
			
			max = Math.min(max, use);
			p = p.parent;*/
			
			double use = p.getMaxWidth();
			if ( use > Double.MAX_VALUE*0.9 )
				use = p.getWidth();
			
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
			if ( child == null )
				continue;
			
			double tempX = (child.getX()-this.getX()) + child.getWidth();
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
			if ( child == null )
				continue;
			
			double tempY = (child.getY()-this.getY()) + child.getHeight();
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
	
	private LayoutBounds LAYOUT_CACHE = new LayoutBounds(0,0,0,0);
	
	protected void clip( Context context, int padding ) {
		LayoutBounds clipBoundsTemp = new LayoutBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		LayoutBounds tempBounds = LAYOUT_CACHE;
		
		Node par = this.parent;
		while ( par != null ) {
			if ( par.flag_clip ) {
				
				// Update temp bounds
				tempBounds.minX = (int)par.getX();
				tempBounds.minY = (int)par.getY();
				tempBounds.maxX = (int)Math.ceil(par.getX()+par.getWidth()+0.5);
				tempBounds.maxY = (int)Math.ceil(par.getY()+par.getHeight()+0.5);
				
				// Clamp
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
	
	public boolean isDescendentSelected() {
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
	}
	
	public boolean isDescendentHovered() {
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
	private LayoutBounds innerBounds;
	public LayoutBounds getInnerBounds() {
		if ( innerBounds == null )
			innerBounds = new LayoutBounds(0,0,0,0);
		
		innerBounds.minX = 0;
		innerBounds.minY = 0;
		innerBounds.maxX = (int)getWidth();
		innerBounds.maxY = (int)getHeight();
		
		return innerBounds;
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
		return this.alignment;
	}
	
	/**
	 * Return the layout alignment used to position this node.
	 * @return
	 */
	public Pos usingAlignment() {
		Pos useAlignment = null;
		Node p = this.getParent();
		int t = 0;
		while ( p != null && useAlignment == null && t < 32 ) {
			useAlignment = p.alignment;
			p = p.parent;
			t++;
		}
		
		if ( useAlignment == null )
			return Pos.CENTER;
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
	 * Return the absolute x position of this node.
	 * @return
	 */
	public double getX() {
		return absolutePosition.x;
	}
	
	/**
	 * Return the absolute y position of this node.
	 * @return
	 */
	public double getY() {
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
		if ( width < 0 )
			width = 0;
		
		layoutBounds.minX = (int)width;
		if ( size.x < (int)width )
			size.x = (int)width;
	}
	
	/**
	 * Set the minimum height of this node.
	 * @param height
	 */
	public void setMinHeight( double height ) {
		if ( height < 0 )
			height = 0;
		
		layoutBounds.minY = (int)height;
		if ( size.y < (int)height )
			size.y = (int)height;
	}
	
	/**
	 * Set the maximum width of this node.
	 * @param width
	 */
	public void setMaxWidth( double width ) {
		if ( width < 0 )
			width = 0;
		
		layoutBounds.maxX = (int)width;
		if ( size.x > (int)width )
			size.x = (int)width;
	}
	
	/**
	 * Set the maxmimum height of this node.
	 * @param height
	 */
	public void setMaxHeight( double height ) {
		if ( height < 0 )
			height = 0;
		
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
	
	protected boolean onMousePressed( int button ) {
		mousePressed = true;
		
		if ( mousePressedEvent != null ) {
			return EventHelper.fireEvent(this.mousePressedEvent, new MouseEvent(button));
		}
		return false;
	}
	
	@Deprecated
	private long _lastClick = 0;
	@Deprecated
	private int _flag_clicks = 0;
	
	protected boolean onMouseReleased( int button ) {
		if ( !mousePressed )
			return false;
		mousePressed = false;
		
		// Clicked
		if ( this.mouseClickedEvent != null ) {
			long time = System.currentTimeMillis()-_lastClick;
			if ( time > 300 ) {
				_flag_clicks = 0;
			}
			_flag_clicks++;
			_lastClick = System.currentTimeMillis();
			EventHelper.fireEvent(this.mouseClickedEvent, new MouseEvent(button, _flag_clicks));
		}
		
		// Released
		if ( mouseReleasedEvent != null ) {
			return EventHelper.fireEvent(this.mouseReleasedEvent, new MouseEvent(button));
		}
		return false;
	}
	
	protected void onMouseEntered() {
		if ( this.mouseEnteredEvent != null ) {
			EventHelper.fireEvent(this.mouseEnteredEvent, new Event());
		}
	}
	
	protected void onMouseExited() {
		if ( this.mouseExitedEvent != null ) {
			EventHelper.fireEvent(this.mouseExitedEvent, new Event());
		}
	}
	
	public EventHandler<Event> getMouseEnteredEvent() {
		return mouseEnteredEvent;
	}
	
	public void setOnMouseEntered( EventHandler<Event> event ) {
		this.mouseEnteredEvent = event;
	}
	
	public EventHandler<Event> getMouseExitedEvent() {
		return this.mouseExitedEvent;
	}
	
	public void setOnMouseExited( EventHandler<Event> event ) {
		this.mouseExitedEvent = event;
	}
	
	public EventHandler<MouseEvent> getMousePressedEvent() {
		return this.mousePressedEvent;
	}
	
	public void setOnMousePressed( EventHandler<MouseEvent> event ) {
		this.mousePressedEvent = event;
	}
	
	public EventHandler<MouseEvent> getMouseReleasedEvent() {
		return this.mouseReleasedEvent;
	}
	
	public void setOnMouseReleased( EventHandler<MouseEvent> event ) {
		this.mouseReleasedEvent = event;
	}
	
	public EventHandler<MouseEvent> getOnMouseClicked() {
		return this.mouseClickedEvent;
	}
	
	public void setOnMouseClicked( EventHandler<MouseEvent> event) {
		this.mouseClickedEvent = event;
	}
	
	public EventHandler<ScrollEvent> getMouseScrollGesture() {
		return this.mouseScrollEvent;
	}
	
	public void setOnMouseScrolled( EventHandler<ScrollEvent> event ) {
		this.mouseScrollEvent = event;
	}
	
	public EventHandler<MouseEvent> getMouseDraggedEvent() {
		return this.mouseDraggedEvent;
	}
	
	public void setOnMouseDragged( EventHandler<MouseEvent> event ) {
		this.mouseDraggedEvent = event;
	}
	
	public EventHandler<KeyEvent> getOnTextInput() {
		return this.textInputEvent;
	}
	
	public void setOnTextInput( EventHandler<KeyEvent> event ) {
		this.textInputEvent = event;
	}
	
	public EventHandler<KeyEvent> getOnKeyPressed() {
		return this.keyPressedEvent;
	}
	
	public void setOnKeyPressed( EventHandler<KeyEvent> event ) {
		this.keyPressedEvent = event;
	}
	
	public EventHandler<KeyEvent> getOnKeyReleased() {
		return this.keyReleasedEvent;
	}
	
	public void setOnKeyReleased( EventHandler<KeyEvent> event ) {
		this.keyReleasedEvent = event;
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
