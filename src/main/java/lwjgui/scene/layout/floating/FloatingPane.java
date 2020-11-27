package lwjgui.scene.layout.floating;

import lwjgui.collections.ObservableList;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Pane;

public class FloatingPane extends Pane {
	private double absx;
	private double absy;

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

	public String getElementType() {
		return "floatingpane";
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
	}

	/*
	@Override
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
	
	@Override
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
	}*/

	@Override
	protected double getMaxPotentialWidth() {
		return this.getWindow().getWidth();
	}
	
	@Override
	protected double getMaxPotentialHeight() {
		return this.getWindow().getHeight();
	}

	@Override
	public void setAbsolutePosition(double x, double y) {
		super.setAbsolutePosition(x, y);
		this.absx = x;
		this.absy = y;
	}
	
	public double getX() {
		return this.absx;
	}
	
	public double getY() {
		return this.absy;
	}
}
