package lwjgui.scene.layout.floating;

import lwjgui.collections.ObservableList;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.Node;
import lwjgui.scene.Parent;
import lwjgui.scene.Region;

public class FloatingPane extends Region {
	private double absx;
	private double absy;
	private boolean moveToFitChildren;
	
	public FloatingPane() {
		this.setPrefSize(0, 0);
	}

	/**
	 *
	 * @return modifiable list of children.
	 */
	@Override
	public ObservableList<Node> getChildren() {
		return this.children;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	/**
	 * Sets whether this node will try to move to fit its children.
	 * If true, the node will move only in the negative direction to try to fit to its children.
	 * This means its width/height will also increase.
	 * @param moveToFit
	 */
	public void setMoveToFitChildren(boolean moveToFit) {
		this.moveToFitChildren = moveToFit;
	}
	
	/**
	 * Returns whether this node will try to move to fit its children. See {@link FloatingPane#setMoveToFitChildren(boolean)}
	 * @return
	 */
	public boolean getMoveToFitChildren() {
		return moveToFitChildren;
	}
	
	/*
	@Override
	public Vector2d getAvailableSize() {
		//return new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);
		//return new Vector2d(getWidth(), getHeight());
		
		return new Vector2d
			(
				Math.max(
					getPrefWidth(),
					this.getPotentialWidth()
				),
				Math.max(
					getPrefHeight(),
					this.getPotentialHeight()
				)
			);
	}*/

	@Override
	protected void position(Node parent) {
		super.position(parent);
		this.absolutePosition.set(absx,absy);
		
		// If elements are inside us, but top the left/top of us, we need to resize!
		if ( moveToFitChildren ) {
			double minx = getMinimumX(this, getX());
			double miny = getMinimumY(this, getY());
			double dx = Math.floor(getX()-minx);
			double dy = Math.floor(getY()-miny);
			this.absolutePosition.sub(dx,dy);
		}
	}

	private double getMinimumX(Node root, double current) {
		
		double t = root.getX();
		if ( root instanceof Parent ) {
			for ( int i = 0; i < ((Parent)root).getChildren().size(); i++) {
				Node child = ((Parent)root).getChildren().get(i);
				double x = getMinimumX(child, t);
				if ( x < t ) {
					t = x;
				}
			}
		}
		
		if ( t < current )
			return t;
		
		return current;
	}

	private double getMinimumY(Node root, double current) {
		
		double t = root.getY();
		if ( root instanceof Parent ) {
			for ( int i = 0; i < ((Parent)root).getChildren().size(); i++) {
				Node child = ((Parent)root).getChildren().get(i);
				double x = getMinimumY(child, t);
				if ( x < t ) {
					t = x;
				}
			}
		}
		
		if ( t < current )
			return t;
		
		return current;
	}
	
	protected double getMaxElementWidth() {
		double runningX = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			if ( child == null )
				continue;
			
			double tempX = Math.abs(child.getX()-getX())+child.getWidth();
			if ( child instanceof FillableRegion && ((FillableRegion)child).isFillToParentWidth())
				tempX = 0;
			
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
			
			double tempY = Math.abs(child.getY()-getY())+child.getHeight();
			if ( child instanceof FillableRegion && ((FillableRegion)child).isFillToParentHeight())
				tempY = 0;
			
			if ( tempY > runningY ) {
				runningY = tempY;
			}
		}
		
		return runningY;
	}
	
	protected double getPotentialWidth() {
		float totalWidth = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double childWid = (child.getX()-getX())+child.getWidth();

			if ( child instanceof FillableRegion && ((FillableRegion)child).isFillToParentWidth() ) {
				childWid = 0;
			}
			
			totalWidth += childWid;
		}
		
		return totalWidth;
	}
	
	protected double getPotentialHeight() {
		float totalHeight = 0;
		for (int i = 0; i < children.size(); i++) {
			Node child = children.get(i);
			double temp = (child.getY()-getY())+child.getHeight();

			if ( child instanceof FillableRegion && ((FillableRegion)child).isFillToParentHeight() ) {
				temp = 0;
			}
			
			totalHeight += temp;
		}
		
		return totalHeight;
	}

	@Override
	public void setAbsolutePosition(double x, double y) {
		super.setAbsolutePosition(x, y);
		this.absx = x;
		this.absy = y;
	}
}
