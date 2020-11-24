package lwjgui.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.event.Event;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.event.KeyEvent;
import lwjgui.event.MouseEvent;
import lwjgui.event.ScrollEvent;
import lwjgui.event.TypeEvent;
import lwjgui.geometry.HPos;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.geometry.Resizable;
import lwjgui.geometry.VPos;
import lwjgui.style.StyleTransition;
import lwjgui.style.Stylesheet;
import lwjgui.style.StylesheetCompileError;
import lwjgui.util.Bounds;

public abstract class Node implements Resizable {
	
	protected Node parent;
	protected ObservableList<Node> children = new ObservableList<>();
	
	/*
	 * Positioning settings
	 */
	protected Vector2d absolutePosition = new Vector2d();
	private Vector2d localPosition = new Vector2d(); // ONLY USER INTERNALLY DONT TOUCH
	protected Vector2d size = new Vector2d();
	protected Vector2d prefsize = new Vector2d();
	protected LayoutBounds layoutBounds = new LayoutBounds(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE);
	protected Insets padding = new Insets(0,0,0,0);
	protected Pos alignment = Pos.CENTER;

	protected Bounds nodeBounds = new Bounds(0, 0, 0, 0);
	
	private String id;
	
	private Stylesheet stylesheet;
	private String localStyle;
	private Stylesheet localStylesheet;
	private Map<String, StyleTransition> styleTransitions = new HashMap<>();

	private boolean initialized;
	private boolean visible = true;

	/*
	 * Event Handlers
	 */
	protected EventHandler<Event> selectedEvent;
	protected EventHandler<Event> selectedEventInternal;

	protected EventHandler<Event> deselectedEvent;
	protected EventHandler<Event> deselectedEventInternal;
	
	protected EventHandler<MouseEvent> mousePressedEvent;
	protected EventHandler<MouseEvent> mousePressedEventInternal;
	
	protected EventHandler<MouseEvent> mouseReleasedEvent;
	protected EventHandler<MouseEvent> mouseReleasedEventInternal;
	
	protected EventHandler<MouseEvent> mouseClickedEvent;
	protected EventHandler<MouseEvent> mouseClickedEventInternal;
	
	protected EventHandler<Event> mouseEnteredEvent;
	protected EventHandler<Event> mouseEnteredEventInternal;
	
	protected EventHandler<Event> mouseExitedEvent;
	protected EventHandler<Event> mouseExitedEventInternal;
	
	protected EventHandler<MouseEvent> mouseDraggedEvent;
	protected EventHandler<MouseEvent> mouseDraggedEventInternal;
	
	protected EventHandler<MouseEvent> mouseDraggedEndEvent;
	protected EventHandler<MouseEvent> mouseDraggedEndEventInternal;
	
	protected EventHandler<ScrollEvent> mouseScrollEvent;
	protected EventHandler<ScrollEvent> mouseScrollEventInternal;
	
	protected EventHandler<TypeEvent> textInputEvent;
	protected EventHandler<TypeEvent> textInputEventInternal;
	
	protected EventHandler<KeyEvent> keyPressedEvent;
	protected EventHandler<KeyEvent> keyPressedEventInternal;
	
	protected EventHandler<KeyEvent> keyRepeatEvent;
	protected EventHandler<KeyEvent> keyRepeatEventInternal;
	
	protected EventHandler<KeyEvent> keyReleasedEvent;
	protected EventHandler<KeyEvent> keyReleasedEventInternal;
	
	/*
	 * Other settings
	 */
	private boolean mouseTransparent = false;
	protected boolean flag_clip = false;
	protected boolean mousePressed = false;
	protected boolean mouseDragged = false;
	
	protected Window window;
	
	public Node() {
		window = LWJGUI.getThreadWindow();
		children.setAddCallback((element) -> {
			if (!element.initialized)
				element.init();
		});
		children.setRemoveCallback((element) -> {
			if (element.initialized)
				element.dispose();
		});
	}

	protected void init() {
		initialized = true;
		for (int i = 0; i < children.size(); i++) {
			Node c = children.get(i);
			if (!c.initialized)
				c.init();
		}
	}

	public void dispose() {
		initialized = false;
		for (int i = 0; i < children.size(); i++) {
			Node c = children.get(i);
			if (c.initialized)
				c.dispose();
		}
	}

	public void setWindow(Window window) {
		this.window = window;
		for (int i = 0; i < children.size(); i++) {
			children.get(i).setWindow(window);
		}
	}
	
	public Window getWindow() {
		return this.window;
	}

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
	
	public void setAbsolutePosition(double x, double y) {
		this.absolutePosition.set(x,y);
		computeLocalPosition();
	}
	
	public void offset(double x, double y) {
		setAbsolutePosition( this.getX()+x, this.getY()+y);
		//updateChildren();
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			child.offset(x, y);
		}
	}
	
	/**
	 * Repositions the non public facing children
	 */
	public void updateChildren() {
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child != null ) {
				child.position(this);
			}
		}
	}
	
	/**
	 * Repositions only the public facing children
	 */
	public void updateChildrenPublic() {
		for (int i = 0; i < getChildren().size(); i++) {
			Node child = getChildren().get(i);
			if ( child != null ) {
				child.position(this);
			}
		}
	}
	
	protected void updateChildrenLocalRecursive() {
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child != null ) {
				child.updateFromLocalPosition();
				child.updateChildrenLocalRecursive();
			}
		}	
	}
	
	/**
	 * Computes the local position by comparing the absolute position to the parents absolute position.
	 */
	private void computeLocalPosition() {
		if ( parent == null )
			return;

		LayoutBounds bounds = parent.getInnerBounds();

		float topLeftX = (float) (parent.getX() + bounds.minX);
		float topLeftY = (float) (parent.getY() + bounds.minY);
		localPosition.set(absolutePosition.x-topLeftX, absolutePosition.y-topLeftY);
	}
	
	/**
	 * Sets absolute position based on local positions.
	 */
	private void updateFromLocalPosition() {
		if ( parent == null )
			return;

		LayoutBounds bounds = parent.getInnerBounds();

		float topLeftX = (float) (parent.getX() + bounds.minX);
		float topLeftY = (float) (parent.getY() + bounds.minY);
		absolutePosition.set(localPosition.x+topLeftX, localPosition.y+topLeftY);
	}
	
	/**
	 * Computes initial absolute position based on alignment rules.
	 */
	private void computeAbsolutePosition() {
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

			float topLeftX = (float) (parent.getX() + bounds.minX);
			float topLeftY = (float) (parent.getY() + bounds.minY);

			double offsetX = (bounds.getWidth()-this.getWidth())*xMult;
			double offsetY = (bounds.getHeight()-this.getHeight())*yMult;

			absolutePosition.x = topLeftX + offsetX;
			absolutePosition.y = topLeftY + offsetY;
			computeLocalPosition();
		}
	}
	
	/**
	 * Computes the absolute position of this Node
	 * @param parent
	 */
	protected void position(Node parent) {
		stylePush();
		{
			// Check parent
			Node oldParent = this.parent;
			this.parent = parent;
	
			// If parents change...
			if ( oldParent != this.parent ) {
				// Recompute absolute position on parent change
				if ( oldParent != null ) {
					unregisterFromParent(this, oldParent);
					computeAbsolutePosition();
				}
				
				registerToParent(this, this.getParent());
			}
			
			// Register our element ID
			registerElementIDToParent(this, this.parent);
			
			// Perform actual sizing/positioning
			updateChildren();
			resize();
			computeAbsolutePosition();
		}
		stylePop();
	}
	
	private HashMap<String, Node> idToNode = new HashMap<>();
	private ArrayList<Node> descendents = new ArrayList<Node>();
	private ArrayList<String> classList = new ArrayList<String>();
	
	private void registerToParent(Node node, Node parent) {
		if ( parent == null )
			return;
		
		parent.descendents.add(node);
		registerToParent(node, parent.getParent());
	}
	
	private void registerElementIDToParent(Node node, Node parent) {
		if ( parent == null )
			return;
		
		if ( node.getElementId() != null && node.getElementId().length() > 0 )
			parent.idToNode.put(node.getElementId(), node);
		
		registerElementIDToParent(node, parent.getParent());
	}
	
	private void unregisterFromParent(Node node, Node parent) {
		if ( parent == null )
			return;
		
		parent.idToNode.remove(node.getElementId());
		parent.descendents.remove(node);
		unregisterFromParent(node, parent.getParent());
	}
	
	/**
	 * Apply our style to the current stack
	 */
	protected void stylePush() {
		if ( window == null )
			return;
		
		Context context = window.getContext();
		if ( context != null ) {
			
			// Add our sheet to the stack
			if ( this.getStylesheet() != null )
				context.getCurrentStyling().add(this.getStylesheet());
			
			// Apply styling!
			for (int i = 0; i < context.getCurrentStyling().size(); i++)
				context.getCurrentStyling().get(i).applyStyling(this);
			
			// Apply our local style if it exists
			if ( this.getStyleLocal() != null ) {
				this.getStyleLocal().applyStyling(this, "NODESTYLE");
			}
		}
	}
	
	/**
	 * Remove out style from the current stack
	 */
	protected void stylePop() {
		if ( window == null )
			return;
		
		Context context = window.getContext();
		// Remove our sheet from the stack
		if ( context != null ) {
			if ( this.getStylesheet() != null )
				context.getCurrentStyling().remove(this.getStylesheet());
		}
	}
	
	/**
	 * Set the stylesheet used to style this node and all descendent nodes to this node.
	 * @param css
	 */
	public void setStylesheet(String css) {
		try {
			Stylesheet style = new Stylesheet(css);
			style.compile();
			this.stylesheet = style;
		} catch(StylesheetCompileError e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the stylesheet used to style this node and all descendent nodes to this node.
	 * @param css
	 */
	public void setStylesheet(Stylesheet css) {
		if ( css != null ) {
			try {
				if ( !css.isCompiled() )
					css.compile();
				this.stylesheet = css;
			} catch(StylesheetCompileError e) {
				e.printStackTrace();
			}
		}
		this.stylesheet = css;
	}
	
	/**
	 * Returns the stylesheet object used to style this node and all descendent nodes to this node.
	 * @return
	 */
	public Stylesheet getStylesheet() {
		return this.stylesheet;
	}
	
	/**
	 * Set the local style used to directly style this node when drawing.
	 * @param localStyle
	 */
	public void setStyle(String localStyle) {
		this.localStyle = localStyle;
		try {
			Stylesheet local = new Stylesheet("NODESTYLE { " + this.localStyle + " }");
			local.compile();
			this.localStylesheet = local;
		} catch(StylesheetCompileError e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the local style used to directly style this node when drawing.
	 * @return
	 */
	public String getStyle() {
		return this.localStyle;
	}
	
	/**
	 * Returns the local stylesheet used to directly style this node when drawing.
	 * @return
	 */
	protected Stylesheet getStyleLocal() {
		return this.localStylesheet;
	}
	
	/**
	 * Returns a specific style transition attached to a property in this node. NULL if no transition is specified.
	 * @param name
	 * @return
	 */
	public StyleTransition getStyleTransition(String property) {
		return this.styleTransitions.get(property);
	}

	/**
	 * Sets the specific style transition attached to a property in this node.
	 * @param property
	 * @param styleTransition
	 */
	public void setStyleTransition(String property, StyleTransition styleTransition) {
		this.styleTransitions.put(property, styleTransition);
	}
	
	/**
	 * Returns the first descendant element that has the matching id.
	 * @param id
	 * @return
	 */
	public Node getElementById(String id) {
		return idToNode.get(id);
	}
	
	/**
	 * Returns a list of all descendant elements with the matching tag (Element Type)
	 * @param tag
	 * @return
	 */
	public ArrayList<Node> getElementsByTag(String tag) {
		ArrayList<Node> ret = new ArrayList<Node>();
		
		for (int i = 0; i < descendents.size(); i++) {
			if ( i >= descendents.size() )
				continue;
			
			Node t = descendents.get(i);
			if ( t == null )
				continue;
			
			if ( t.getElementType().equals(tag) )
				ret.add(t);
		}
		
		return ret;
	}
	
	/**
	 * returns a list of all descendant elements with the matching class name in its class list.
	 * @param className
	 * @return
	 */
	public ArrayList<Node> getElementsByClassName(String className) {
		ArrayList<Node> ret = new ArrayList<Node>();
		
		for (int i = 0; i < descendents.size(); i++) {
			if ( i >= descendents.size() )
				continue;
			
			Node t = descendents.get(i);
			if ( t == null )
				continue;
			
			if ( t.getClassList().contains(className) )
				ret.add(t);
		}
		
		return ret;
	}
	
	/**
	 * Returns this elements class list. Used primarily for styling.
	 * @return
	 */
	public ArrayList<String> getClassList() {
		return this.classList;
	}
	
	/**
	 * Set the unique ID for this Node. Can be querried via context#getNodeById()
	 * @param id
	 */
	public void setElementId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the unique ID for this node.
	 * @param id
	 */
	public String getElementId() {
		return this.id;
	}

	/**
	 * Calculates the bounding of this node based on its own position and the positions of its children. Meant to be used for some rendering (E.G. filling in the background) 
	 * and input handling (ensuring that every child in the node is clickable).
	 */
	public void calculateNodeBounds() {
		double sx = getX();
		double sy = getY();
		double ex = sx + getWidth();
		double ey = sy + getHeight();
		
		//System.out.println("calculateNodeBounds() "+ this + " -> " + sx + " " + sy + " " + getWidth() + " " + getHeight());
		
		for (int i = 0; i < children.size(); i++) {
			Node n = children.get(i);
			
			if (n == null) continue;
			
			double nSX = n.getX();
			double nSY = n.getY();
			double nEX = nSX + n.getWidth();
			double nEY = nSY + n.getHeight();
			
			if (nSX < sx) {
				sx = nSX;
			}
			
			if (nSY < sy) {
				sy = nSY;
			}
			
			if (nEX > ex) {
				ex = nEX;
			}
			
			if (nEY > ey) {
				ey = nEY;
			}
		}
		
		nodeBounds.set(sx, sy, ex, ey);
	}
	
	public abstract void render(Context context);

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
		
		// Size up to pref size
		if ( size.x < this.getPrefWidth() )
			size.x = this.getPrefWidth();
		if ( size.y < this.getPrefHeight() )
			size.y = this.getPrefHeight();

		// Get available size
		Vector2d available = this.getAvailableSize();
		double availableWidth = available.x;
		double availableHeight = available.y;

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
	
	protected double getMaxPotentialWidth() {
		double max = Double.MAX_VALUE;
		Node p = this;
		
		while ( p != null ) {
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
	
	/**
	 * Get the width of the widest element inside this node.
	 * @return
	 */
	protected double getMaxElementWidth() {
		double runningX = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child == null )
				continue;

			double tempX = child.getWidth();
			
			if ( child instanceof FillableRegion && ((FillableRegion)child).isFillToParentWidth()) {
				tempX = child.getMaxElementWidth();
			}
			
			if ( tempX > runningX ) {
				runningX = tempX;
			}
		}
		
		return runningX;
	}
	
	/**
	 * Get the height of the highest element inside this node.
	 * @return
	 */
	protected double getMaxElementHeight() {
		double runningY = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child == null )
				continue;
			
			double tempSize = child.getHeight();
			
			if ( child instanceof FillableRegion && ((FillableRegion)child).isFillToParentHeight()) {
				tempSize = Math.max(child.getPrefHeight(), child.getMaxElementHeight());
			}
			
			if ( tempSize > runningY ) {
				runningY = tempSize;
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
	
	/**
	 * Sets the nanovg clip brush
	 * @param context
	 * @param padding
	 */
	protected void clip( Context context, int padding ) {
		if ( context == null )
			return;
		
		if ( getScene() == null )
			return;
		
		LayoutBounds clipBoundsTemp = new LayoutBounds(0, 0, (int)getScene().getWidth(), (int)getScene().getHeight());
		LayoutBounds tempBounds = LAYOUT_CACHE;
		
		Node current = this;
		while ( current != null ) {
			if ( current.flag_clip ) {
				
				// Update temp bounds
				LayoutBounds inner = current.getInnerBounds();
				tempBounds.minX = (int)(current.getX()+inner.getX());
				tempBounds.minY = (int)(current.getY()+inner.getY());
				tempBounds.maxX = (int)(current.getX()+inner.getWidth()+1);
				tempBounds.maxY = (int)(current.getY()+inner.getHeight()+1);
				
				if ( current instanceof Region ) {
					Insets pad = ((Region)current).getPadding();
					tempBounds.minX += pad.getLeft();
					tempBounds.maxX -= pad.getRight();
					tempBounds.minY += pad.getTop();
					tempBounds.maxY -= pad.getBottom();
				}
				
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
			
			current = current.parent;
		}
		
		//TEMP_CACHE.setClipBounds(clipBoundsTemp.minX-padding, clipBoundsTemp.minY-padding, clipBoundsTemp.getWidth()+padding*2, clipBoundsTemp.getHeight()+padding*2);
		context.setScissor(clipBoundsTemp.minX-padding, clipBoundsTemp.minY-padding, clipBoundsTemp.getWidth()+padding*2, clipBoundsTemp.getHeight()+padding*2);
	}
	
	public boolean isDescendentSelected() {
		Context context = window.getContext();
		if ( context == null ) {
			return false;
		}
		Node selected = context.getSelected();
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
	
	public Bounds getNodeBounds() {
		calculateNodeBounds();
		return nodeBounds;
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
	 * Forces the min, max, preferred, and actual width & height to the given values.
	 */
	public void forceSize(double width, double height) {
		this.forceWidth(width);
		this.forceHeight(height);
	}
	
	/**
	 * Forces the min, max, preferred, and actual width to the given value.
	 */
	public void forceWidth(double width) {
		setMinWidth(width);
		setPrefWidth(width);
		setMaxWidth(width);
		size.x = width;
	}
	
	/**
	 * Forces the min, max, preferred, and actual width to the given value.
	 */
	public void forceHeight(double height) {
		setMinHeight(height);
		setPrefHeight(height);
		setMaxHeight(height);
		size.y = height;
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
		if ( this.prefsize == null )
			return;
		
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
		if ( this.prefsize == null )
			return;
		
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

		public double getX() {
			return minX;
		}

		public double getY() {
			return minY;
		}
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public abstract String getElementType();
	
	protected void onMousePressed(MouseEvent event) {
		mousePressed = true;
		
		Context context = window.getContext();
		if ( context != null ) {
			context.setLastPressed(this);
		}
		
		if (mousePressedEventInternal != null) {
			EventHelper.fireEvent(mousePressedEventInternal, event);
		}
		
		if (mousePressedEvent != null) {
			EventHelper.fireEvent(mousePressedEvent, event);
		}
	}
	
	private long _lastClick = 0;
	private int _flag_clicks = 0;
	
	protected boolean onMouseReleased(MouseEvent event) {
		if (!mousePressed) return false;
		mousePressed = false;
		
		// Clicked
		if (mouseClickedEventInternal != null || mouseClickedEvent != null) {
			long time = System.currentTimeMillis()-_lastClick;
			
			if ( time > 300 ) {
				_flag_clicks = 0;
			}
			
			_flag_clicks++;
			_lastClick = System.currentTimeMillis();
			
			event.clicks = _flag_clicks;
			
			if (mouseClickedEventInternal != null) {
				EventHelper.fireEvent(mouseClickedEventInternal, event);
			}
			
			if (mouseClickedEvent != null) {
				EventHelper.fireEvent(mouseClickedEvent, event);
			}
		}
		
		// Released
		boolean consumed = false;
		
		if (mouseReleasedEventInternal != null && EventHelper.fireEvent(mouseReleasedEventInternal, event)) {
			consumed = true;
		}
		
		if (mouseReleasedEvent != null && EventHelper.fireEvent(mouseReleasedEvent, event)) {
			consumed = true;
		}
		
		return consumed;
	}
	
	protected void onMouseEntered() {
		if (mouseEnteredEventInternal != null) {
			EventHelper.fireEvent(mouseEnteredEventInternal, new Event());
		}
		
		if (mouseEnteredEvent != null) {
			EventHelper.fireEvent(mouseEnteredEvent, new Event());
		}
	}
	
	protected void onMouseExited() {
		if (mouseExitedEventInternal != null) {
			EventHelper.fireEvent(mouseExitedEventInternal, new Event());
		}
		
		if (mouseExitedEvent != null) {
			EventHelper.fireEvent(mouseExitedEvent, new Event());
		}
	}
	
	/*
	 * 
	 * Public event handler setters/getters
	 * 
	 */
	
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
	
	public EventHandler<Event> getSelectedEvent() {
		return selectedEvent;
	}
	
	public void setOnSelectedEvent(EventHandler<Event> event) {
		this.selectedEvent = event;
	}
	
	public EventHandler<Event> getDeselectedEvent() {
		return deselectedEvent;
	}
	
	public void setOnDeselectedEvent(EventHandler<Event> event) {
		this.deselectedEvent = event;
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
	
	public EventHandler<MouseEvent> getMouseDraggedEndEvent() {
		return this.mouseDraggedEndEvent;
	}
	
	public void setOnMouseDraggedEnd( EventHandler<MouseEvent> event ) {
		this.mouseDraggedEndEvent = event;
	}
	
	public EventHandler<TypeEvent> getOnTextInput() {
		return this.textInputEvent;
	}
	
	public void setOnTextInput( EventHandler<TypeEvent> event ) {
		this.textInputEvent = event;
	}
	
	public EventHandler<KeyEvent> getOnKeyPressed() {
		return this.keyPressedEvent;
	}
	
	public void setOnKeyPressed( EventHandler<KeyEvent> event ) {
		this.keyPressedEvent = event;
	}
	
	protected EventHandler<KeyEvent> getKeyRepeatEvent() {
		return keyRepeatEvent;
	}
	
	protected void setOnKeyRepeat(EventHandler<KeyEvent> keyRepeatEvent) {
		this.keyRepeatEvent = keyRepeatEvent;
	}
	
	protected void setOnKeyPressedAndRepeat(EventHandler<KeyEvent> keyPressAndRepeatEvent) {
		keyPressedEvent = keyRepeatEvent = keyPressAndRepeatEvent;
	}
	
	public EventHandler<KeyEvent> getOnKeyReleased() {
		return this.keyReleasedEvent;
	}
	
	public void setOnKeyReleased( EventHandler<KeyEvent> event ) {
		this.keyReleasedEvent = event;
	}

	/*
	 * 
	 * Internal Event Handlers setters/getters
	 * 
	 */
	
	protected EventHandler<Event> getSelectedEventInternal() {
		return selectedEventInternal;
	}
	
	protected void setOnSelectedEventInternal(EventHandler<Event> event) {
		this.selectedEventInternal = event;
	}
	
	protected EventHandler<Event> getDeselectedEventInternal() {
		return deselectedEventInternal;
	}
	
	protected void setOnDeselectedEventInternal(EventHandler<Event> event) {
		this.deselectedEventInternal = event;
	}
	
	protected EventHandler<MouseEvent> getMousePressedEventInternal() {
		return mousePressedEventInternal;
	}
	
	protected void setOnMousePressedInternal(EventHandler<MouseEvent> mousePressedEventInternal) {
		this.mousePressedEventInternal = mousePressedEventInternal;
	}

	protected EventHandler<MouseEvent> getMouseReleasedEventInternal() {
		return mouseReleasedEventInternal;
	}

	protected void setOnMouseReleasedInternal(EventHandler<MouseEvent> mouseReleasedEventInternal) {
		this.mouseReleasedEventInternal = mouseReleasedEventInternal;
	}

	protected EventHandler<MouseEvent> getMouseClickedEventInternal() {
		return mouseClickedEventInternal;
	}

	protected void setOnMouseClickedInternal(EventHandler<MouseEvent> mouseClickedEventInternal) {
		this.mouseClickedEventInternal = mouseClickedEventInternal;
	}

	protected EventHandler<Event> getMouseEnteredEventInternal() {
		return mouseEnteredEventInternal;
	}

	protected void setOnMouseEnteredInternal(EventHandler<Event> mouseEnteredEventInternal) {
		this.mouseEnteredEventInternal = mouseEnteredEventInternal;
	}

	protected EventHandler<Event> getMouseExitedEventInternal() {
		return mouseExitedEventInternal;
	}

	protected void setOnMouseExitedInternal(EventHandler<Event> mouseExitedEventInternal) {
		this.mouseExitedEventInternal = mouseExitedEventInternal;
	}

	protected EventHandler<MouseEvent> getMouseDraggedEventInternal() {
		return mouseDraggedEventInternal;
	}

	protected void setOnMouseDraggedInternal(EventHandler<MouseEvent> mouseDraggedEventInternal) {
		this.mouseDraggedEventInternal = mouseDraggedEventInternal;
	}

	protected EventHandler<MouseEvent> getMouseDraggedEndEventInternal() {
		return mouseDraggedEndEventInternal;
	}

	protected void setOnMouseDraggedEndInternal(EventHandler<MouseEvent> mouseDraggedEndEventInternal) {
		this.mouseDraggedEndEventInternal = mouseDraggedEndEventInternal;
	}

	protected EventHandler<ScrollEvent> getMouseScrollEventInternal() {
		return mouseScrollEventInternal;
	}

	protected void setOnMouseScrollInternal(EventHandler<ScrollEvent> mouseScrollEventInternal) {
		this.mouseScrollEventInternal = mouseScrollEventInternal;
	}
	
	protected EventHandler<TypeEvent> getTextInputEventInternal() {
		return textInputEventInternal;
	}
	
	protected void setOnTextInputInternal(EventHandler<TypeEvent> textInputEventInternal) {
		this.textInputEventInternal = textInputEventInternal;
	}
	
	protected EventHandler<KeyEvent> getKeyPressedEventInternal() {
		return keyPressedEventInternal;
	}
	
	protected void setOnKeyPressedInternal(EventHandler<KeyEvent> keyPressedEventInternal) {
		this.keyPressedEventInternal = keyPressedEventInternal;
	}
	
	protected EventHandler<KeyEvent> getKeyRepeatEventInternal() {
		return keyRepeatEventInternal;
	}
	
	protected void setOnKeyRepeatInternal(EventHandler<KeyEvent> keyRepeatEventInternal) {
		this.keyRepeatEventInternal = keyRepeatEventInternal;
	}

	protected void setOnKeyPressedAndRepeatInternal(EventHandler<KeyEvent> keyPressAndRepeatEventInternal) {
		keyPressedEventInternal = keyRepeatEventInternal = keyPressAndRepeatEventInternal;
	}
	
	protected EventHandler<KeyEvent> getKeyReleasedEventInternal() {
		return keyReleasedEventInternal;
	}

	protected void setOnKeyReleasedInternal(EventHandler<KeyEvent> keyReleasedEventInternal) {
		this.keyReleasedEventInternal = keyReleasedEventInternal;
	}

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

	/**
	 * Returns if this node is currently hovered.
	 * @return
	 */
	public boolean isHover() {
		return this.window.getContext().isHovered(this);
	}

	/**
	 * Returns if this node is currently selected.
	 * @return
	 */
	public boolean isSelected() {
		return this.window.getContext().isSelected(this);
	}
	
	/**
	 * Returns true if this node is currently selected or any descendents of this node are currently selected.
	 * @return
	 */
	public boolean isSelectedOrDescendentSelected() {
		return this.isSelected() || this.isDescendentSelected();
	}

	/**
	 * Returns if this node is currently being clicked! (Hovered + mouse left down)
	 * @return
	 */
	public boolean isClicked() {
		return this.isHover() && this.window.getMouseHandler().isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}
}
