package lwjgui.scene;

import org.joml.Vector2d;

import lwjgui.Color;
import lwjgui.geometry.Insets;

public abstract class Region extends Parent {
	protected Insets padding = Insets.EMPTY;
	
	private Color backgroundColor;
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */
	public void setBackground(Color color) {
		this.backgroundColor = color;
	}
	
	/**
	 * Get the current background color of this node.
	 * @return
	 */
	public Color getBackground() {
		return this.backgroundColor;
	}
	
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
	
	@Override
	public LayoutBounds getInnerBounds() {
		LayoutBounds t = super.getInnerBounds();
		
		t.minX += (int)padding.getLeft();
		t.minY += (int)padding.getTop();
		t.maxX -= padding.getRight();
		t.maxY -= padding.getBottom();
		
		return t;
	}
	
	protected boolean canPackElementWidth() {
		return true;
	}
	
	protected boolean canPackElementHeight() {
		return true;
	}
	
	@Override
	protected void resize() {
		Vector2d availableSize = this.getAvailableSize();
		
		// Fix this pane to the width of its elements. Provided it does not exceed max width
		if ( canPackElementWidth() ) {
			float maxWidthInside = (float) (getMaxElementWidth()+padding.getRight());
			maxWidthInside = (float) Math.max(maxWidthInside, getPrefWidth());
			size.x = Math.min(maxWidthInside, availableSize.x);
		}
		
		// Fix this pane to the height of its elements. Provided it does not exceed max height
		if ( canPackElementHeight() ) {
			float maxHeightInside = (float) (getMaxElementHeight()+padding.getBottom());
			maxHeightInside = (float) Math.max(maxHeightInside, getPrefHeight());
			size.y = Math.min(maxHeightInside, availableSize.y);
		}
		
		super.resize();
	}
	
	protected double getMinimumPotentialWidth() {
		float totalWidth = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			float childWid = (float) child.getWidth();
			if ( child instanceof Region && !((Region)child).canPackElementWidth() ) {
				childWid = 0;
			}
			totalWidth += childWid;
		}
		totalWidth += this.getPadding().getWidth();
		
		return totalWidth;
	}
	
	protected double getMinimumPotentialHeight() {
		float totalHeight = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			float temp = (float) child.getHeight();
			if ( child instanceof Region && !((Region)child).canPackElementHeight() ) {
				temp = 0;
			}
			totalHeight += temp;
		}
		totalHeight += this.getPadding().getHeight();
		
		return totalHeight;
	}
}
