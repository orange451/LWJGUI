package lwjgui.geometry;

import org.joml.Vector2d;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;
import lwjgui.event.MouseEvent;

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
	
	private boolean mouseTransparent = false;
	
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
	
	protected void position(Node parent) {
		this.parent = parent;
		
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			child.position(this);
		}

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
	
	public Vector2d getAvailableSize() {
		float availableWidth = (float) getMaxWidth();
		float availableHeight = (float) getMaxHeight();
		return new Vector2d(availableWidth, availableHeight);
	}
	
	protected void resize() {
		
		// Get available size
		Vector2d available = this.getAvailableSize();
		double availableWidth = available.x;
		double availableHeight = available.y;
		
		//System.out.println(this.getClass().getName() + " / " + getWidth() + " / " + availableWidth + " / " + getInnerBounds().getWidth());
		//System.out.println("  " + prefsize.x + " / " + availableWidth + " / " + getMinWidth());
		
		// Resize if smaller than pref size
		synchronized(size) {
			if ( size.x < prefsize.x )
				size.x = prefsize.x;
			if ( size.y < prefsize.y )
				size.y = prefsize.y;
			
			// Cap size to available size
			if ( size.x > availableWidth )
				size.x = availableWidth;
			if ( size.y > availableHeight )
				size.y = availableHeight;
			
			// Cap size to min size
			if ( size.x < this.getMinWidth() && this.getMinWidth() > 0 )
				size.x = this.getMinWidth();
			if ( size.y < this.getMinHeight() && this.getMinHeight() > 0 )
				size.y = this.getMinHeight();
		}
		//System.out.println("    " + getWidth());
	}
	
	protected void clip(Context context) {
		clip(context, 0);
	}
	
	protected void clip( Context context, int padding ) {
		LayoutBounds clipBoundsTemp = new LayoutBounds((int)getAbsoluteX()-padding, (int)getAbsoluteY()-padding, (int)getAbsoluteX()+(int)getWidth()+padding, (int)getAbsoluteY()+(int)getHeight()+padding);
		Node par = parent;
		while (par != null) {
			LayoutBounds tempBounds = new LayoutBounds((int)par.getAbsoluteX(), (int)par.getAbsoluteY(), (int)par.getAbsoluteX()+(int)par.getWidth(), (int)par.getAbsoluteY()+(int)par.getHeight());
			if ( tempBounds.minX > clipBoundsTemp.minX )
				clipBoundsTemp.minX = tempBounds.minX;
			if ( tempBounds.minY > clipBoundsTemp.minY )
				clipBoundsTemp.minY = tempBounds.minY;
			if ( tempBounds.maxX < clipBoundsTemp.maxX )
				clipBoundsTemp.maxX = tempBounds.maxX;
			if ( tempBounds.maxY < clipBoundsTemp.maxY )
				clipBoundsTemp.maxY = tempBounds.maxY;
			par = par.parent;
		}
		
		NanoVG.nvgScissor(context.getNVG(), clipBoundsTemp.minX, clipBoundsTemp.minY, clipBoundsTemp.getWidth(), clipBoundsTemp.getHeight());
	}
	
	public LayoutBounds getInnerBounds() {
		return new LayoutBounds(0, 0, (int)getWidth(), (int)getHeight());
	}
	
	public double getWidth() {
		return size.x;
	}
	
	public double getHeight() {
		return size.y;
	}
	
	public void setAlignment(Pos pos) {
		this.alignment = pos;
	}
	
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
	
	protected ObservableList<Node> getChildren() {
		return this.children;
	}
	
	public double getX() {
		return localPosition.x;
	}
	
	public double getY() {
		return localPosition.y;
	}

	public double getAbsoluteX() {
		return absolutePosition.x;
	}
	
	public double getAbsoluteY() {
		return absolutePosition.y;
	}
	
	public double getMinWidth() {
		return layoutBounds.minX;
	}
	
	public double getMinHeight() {
		return layoutBounds.minY;
	}
	
	public double getMaxWidth() {
		return layoutBounds.maxX;
	}
	
	public double getMaxHeight() {
		return layoutBounds.maxY;
	}
	
	public void setPrefSize( double width, double height ) {
		setPrefWidth( width );
		setPrefHeight( height );
	}
	
	public void setMinSize( double width, double height ) {
		setMinWidth(width);
		setMinHeight(height);
	}
	
	public void setPrefWidth( double width ) {
		this.prefsize.x = width;
		if (this.getMinWidth() > 0) {
			width = Math.max(layoutBounds.minX, Math.min(layoutBounds.maxX, width));
		}
		this.size.x = width;
	}
	
	public void setPrefHeight( double height ) {
		this.prefsize.y = height;
		if ( this.getMinHeight() > 0 ) {
			height = Math.max(layoutBounds.minY, Math.min(layoutBounds.maxY, height));
		}
		this.size.y = height;
	}
	
	public double getPrefWidth() {
		return prefsize.x;
	}
	
	public double getPrefHeight() {
		return prefsize.y;
	}
	
	public void setMinWidth( double width ) {
		layoutBounds.minX = (int)width;
		if ( size.x < (int)width )
			size.x = (int)width;
	}
	
	public void setMinHeight( double height ) {
		layoutBounds.minY = (int)height;
		if ( size.y < (int)height )
			size.y = (int)height;
	}
	
	public void setMaxWidth( double width ) {
		layoutBounds.maxX = (int)width;
		if ( size.x > (int)width )
			size.x = (int)width;
	}
	
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

		public float getWidth() {
			return maxX - minX;
		}
		
		public float getHeight() {
			return maxY - minY;
		}
	}
	
	public void onMousePressed( int button ) {
		if ( mousePressedEvent == null )
			return;
		this.mousePressedEvent.onEvent(button);
	}
	
	public void onMouseReleased( int button ) {
		if ( mouseReleasedEvent == null )
			return;
		this.mouseReleasedEvent.onEvent(button);
	}

	public abstract void render(Context context);

	public boolean isMouseTransparent() {
		return this.mouseTransparent;
	}
	
	public void setMouseTransparent(boolean t ) {
		this.mouseTransparent = t;
	}
}
