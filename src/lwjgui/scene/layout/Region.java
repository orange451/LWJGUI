package lwjgui.scene.layout;

import org.joml.Vector2d;

import lwjgui.Color;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Node;
import lwjgui.geometry.Node.LayoutBounds;
import lwjgui.theme.Theme;

public abstract class Region extends Node {
	private boolean fillToParentHeight;
	private boolean fillToParentWidth;
	private Insets padding = Insets.EMPTY;
	
	public void setFillToParentWidth( boolean fill ) {
		this.fillToParentWidth = fill;
	}

	public boolean isFillToParentWidth() {
		return this.fillToParentWidth;
	}

	public boolean isFillToParentHeight() {
		return this.fillToParentHeight;
	}
	
	public void setFillToParentHeight( boolean fill ) {
		this.fillToParentHeight = fill;
	}
	
	@Override
	public Vector2d getAvailableSize() {
		if ( parent == null )
			return super.getAvailableSize();
		
		Vector2d available = super.getAvailableSize();
		double availableWidth = available.x;
		double availableHeight = available.y;
	
		LayoutBounds bounds = parent.getInnerBounds();
		
		float padX = (float) (parent.getWidth()-bounds.getWidth());
		float parentWid = (float) (parent.getWidth()-padX);
		if ( availableWidth > parentWid ) {
			availableWidth = parentWid;
		}
		
		float padY = (float) (parent.getHeight()-bounds.getHeight());
		float parentHei = (float) (parent.getHeight()-padY);
		if ( availableHeight > parentHei ) {
			availableHeight = parentHei;
		}
		
		return new Vector2d(availableWidth, availableHeight);
	}
	
	@Override
	public void position(Node parent) {
		if ( fillToParentWidth ) {
			LayoutBounds bounds = parent.getInnerBounds();
			double potential = 0;
			if ( parent instanceof Pane ) {
				potential = ((Pane)parent).getMinimumPotentialWidth();
			}
			double wid = bounds.getWidth()-potential;
			//this.setMinWidth(wid);
			//this.setMaxWidth(wid);
			this.size.x = wid;
		}
		if ( fillToParentHeight ) {
			LayoutBounds bounds = parent.getInnerBounds();
			double potential = 0;
			if ( parent instanceof Pane ) {
				potential = ((Pane)parent).getMinimumPotentialHeight();
			}
			double hei = bounds.getHeight()-potential;
			//this.setMinHeight(hei);
			//this.setMaxHeight(hei);
			this.size.y = hei;
		}
		
		super.position(parent);
	}
	
    public final void setPadding(Insets value) { padding = value; }
    public final Insets getPadding() { return padding; }
	
	@Override
	public LayoutBounds getInnerBounds() {
		return new LayoutBounds((int)padding.getLeft(), (int)padding.getTop(), (int)(getWidth()-padding.getRight()), (int)(getHeight()-padding.getBottom()));
	}
	
	@Override
	protected void resize() {
		// Fix this pane to the width of its elements. Provided it does not exceed max width
		float maxWidthInside = (float) getMaxElementWidth();
		if ( maxWidthInside > getInnerBounds().getWidth() ) {
			float temp = (float) (maxWidthInside+padding.getLeft()+padding.getRight());
			if ( temp <= this.getMaxWidth() ) {
				size.x = temp;
			}
		}

		// Fix this pane to the height of its elements. Provided it does not exceed max height
		float maxHeightInside = (float) getMaxElementHeight();
		if ( maxHeightInside > getInnerBounds().getHeight() ) {
			float temp = (float) (maxHeightInside+padding.getTop()+padding.getBottom());
			if ( temp <= this.getMaxHeight() ) {
				size.y = temp;
			}
		}
		
		super.resize();
	}
	
	public double getMaxElementWidth() {
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
	
	public double getMaxElementHeight() {
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
	
	public double getMinimumPotentialWidth() {
		float totalWidth = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			float childWid = (float) child.getWidth();
			if ( child instanceof Region && ((Region)child).isFillToParentWidth() ) {
				childWid = 0;
			}
			totalWidth += childWid;
		}
		totalWidth += this.getPadding().getWidth();
		
		return totalWidth;
	}
	
	public double getMinimumPotentialHeight() {
		float totalHeight = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			float temp = (float) child.getHeight();
			if ( child instanceof Region && ((Region)child).isFillToParentHeight() ) {
				temp = 0;
			}
			totalHeight += temp;
		}
		totalHeight += this.getPadding().getHeight();
		
		return totalHeight;
	}
	
	public double getMaximumPotentialWidth() {
		float totalWidth = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			float childWid = (float) child.getWidth();
			if ( child instanceof Region && ((Region)child).isFillToParentWidth() ) {
				childWid = 0;
				return this.parent.getInnerBounds().getWidth();
			}
			totalWidth += childWid;
		}
		totalWidth += this.getPadding().getWidth();
		
		return totalWidth;
	}
	
	public double getMaximumPotentialHeight() {
		float totalHeight = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			float temp = (float) child.getHeight();
			if ( child instanceof Region && ((Region)child).isFillToParentHeight() ) {
				temp = 0;
				return this.parent.getInnerBounds().getHeight();
			}
			totalHeight += temp;
		}
		totalHeight += this.getPadding().getHeight();
		
		return totalHeight;
	}
}
