package lwjgui.scene;

import lwjgui.geometry.Insets;
import lwjgui.scene.layout.floating.FloatingPane;

public abstract class Region extends Parent  {
	protected Insets padding = Insets.EMPTY;
	protected Insets border = Insets.EMPTY;
	
	/*@Override
	public Vector2d getAvailableSize() {
		if ( parent == null )
			return super.getAvailableSize();
		
		Vector2d available = super.getAvailableSize();
		double availableWidth = available.x;
		double availableHeight = available.y;
	
		/*LayoutBounds parentCurrentSize = parent.getInnerBounds();
		double maxParentInternalWidth = parent.getMaxWidth() - (parent.getWidth()-parent.getInnerBounds().getWidth());
		double maxParentInternalHeight = parent.getMaxHeight() - (parent.getHeight()-parent.getInnerBounds().getHeight());
		*/
		
		//availableWidth = Math.min(getMaxPotentialWidth(), availableWidth);
		//availableHeight = Math.min(getMaxPotentialHeight(), availableHeight);
		
		/*float padX = (float) (parent.getWidth()-bounds.getWidth());
		float parentWid = (float) (parent.getWidth()-padX);
		if ( availableWidth > parentWid ) {
			availableWidth = parentWid;
		}
		
		float padY = (float) (parent.getHeight()-bounds.getHeight());
		float parentHei = (float) (parent.getMaxHeight()-padY);
		if ( availableHeight > parentHei ) {
			availableHeight = parentHei;
		}*/
		
		//return new Vector2d(availableWidth, availableHeight);
	//}
	
	/**
	 * Set the padding insets of this node. All child nodes will be offset based on the insets.
	 * @param value
	 */
    public final void setPadding(Insets value) { padding = value; }
    
    /**
     * 
     * @return Return the padding insets of this node.
     */
    public final Insets getPadding() { return padding; }
    
    /**
     * Set the border insets of this node. All child nodes will be offset based on the insets.
     * @param value
     */
    public final void setBorder(Insets value) { border = value; }
    
    /**
     * 
     * @return Return the padding insets of this node.
     */
    public final Insets getBorder() { return border; }

    /**
     * Convenience method to set the 4 borders based on 1 value.
     * @param width
     */
	public void setBorderWidth(float width) {
		this.setBorder(new Insets(width));
	}
	
	/**
	 * Returns the average of the 4 border widths.
	 * @return
	 */
	public float getBorderWidth() {
		return (float) (this.getBorder().getLeft() + this.getBorder().getRight() + this.getBorder().getTop() + this.getBorder().getBottom())/4f;
	}
	
    /**
     * Convenience method to set the 4 borders based on 2 values.
     * @param width
     */
	public void setBorderWidth(float width, float height) {
		this.setBorder(new Insets(height, width, height, width));
	}
	
	@Override
	public LayoutBounds getInnerBounds() {
		LayoutBounds t = super.getInnerBounds();
		
		t.minX += (int)(padding.getLeft()+border.getLeft());
		t.minY += (int)(padding.getTop()+border.getTop());
		t.maxX -= (padding.getRight()+border.getRight());
		t.maxY -= (padding.getBottom()+border.getBottom());
		
		return t;
	}
	

	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		for (int i = 0; i < getChildren().size(); i++) {
			// Clip to my bounds
			clip(context);

			// Draw child
			Node child = getChildren().get(i);
			if ( child == null )
				continue;
			
			if ( !child.isVisible() )
				continue;
			
			child.render(context);
		}
	}
	
	protected boolean packToDescendentElementWidth() {
		return true;
	}
	
	protected boolean packToDescendentElementHeight() {
		return true;
	}
	
	@Override
	protected void resize() {
		// Fit this pane to the width of its elements.
		float maxWidthInside = (float) (getMaxElementWidth()+getPadding().getWidth()+getBorder().getWidth());
		maxWidthInside = (float) Math.max(maxWidthInside, getPrefWidth());
		size.x = maxWidthInside;
		
		// Fit this pane to the height of its elements.
		float maxHeightInside = (float) (getMaxElementHeight()+getPadding().getHeight()+getBorder().getHeight());
		maxHeightInside = (float) Math.max(maxHeightInside, getPrefHeight());
		size.y = maxHeightInside;
	
		// Apply normal resizing
		super.resize();
	}
	
	/**
	 * Returns the widths of all direct children added together.
	 * Treats fillable regions that stretch as size 0.
	 * @return
	 */
	protected double getMinimumPotentialWidth() {
		float totalWidth = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double childWid = child.getWidth();
			if ( child instanceof Region && !((Region)child).packToDescendentElementWidth() ) {
				childWid = 0;
			}
			if ( child instanceof FloatingPane ) {
				double csw = child.getX()-getX()+child.getWidth();
				childWid = Math.max(0, csw-getWidth());
			}
			totalWidth += childWid;
		}
		
		return totalWidth;
	}
	
	/**
	 * Returns the heights of all direct children added together.
	 * Treats fillable regions that stretch as size 0.
	 * @return
	 */
	protected double getMinimumPotentialHeight() {
		float totalHeight = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double temp = child.getHeight();
			if ( child instanceof Region && !((Region)child).packToDescendentElementHeight() ) {
				temp = 0;
			}
			if ( child instanceof FloatingPane ) {
				double csw = child.getY()-getY()+child.getHeight();
				temp = Math.max(0, csw-getHeight());
			}
			totalHeight += temp;
		}
		
		return totalHeight;
	}
}
