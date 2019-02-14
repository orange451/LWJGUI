package lwjgui.scene;

public abstract class FillableRegion extends Region {
	private boolean fillToParentHeight;
	private boolean fillToParentWidth;

	/**
	 * Flag that controls whether this parent will stretch to the width of its parent.
	 * @param fill
	 */
	public void setFillToParentWidth( boolean fill ) {
		this.fillToParentWidth = fill;
	}

	/**
	 * Flag that controls whether this parent will stretch to the height of its parent.
	 * @param fill
	 */
	public void setFillToParentHeight( boolean fill ) {
		this.fillToParentHeight = fill;
	}
	
	/**
	 * 
	 * @return Returns if this node will fit to its parents width.
	 */
	public boolean isFillToParentWidth() {
		return this.fillToParentWidth;
	}

	/**
	 * 
	 * @return Returns if this node will fit to its parents height.
	 */
	public boolean isFillToParentHeight() {
		return this.fillToParentHeight;
	}
	
	@Override
	protected void position(Node parent) {
		if ( fillToParentWidth ) {
			LayoutBounds bounds = parent.getInnerBounds();
			double potential = 0;
			if ( parent instanceof FillableRegion ) {
				potential = ((FillableRegion)parent).getMaximumPotentialWidth();
			}
			double wid = bounds.getWidth()-potential;
			this.size.x = wid;
		}
		if ( fillToParentHeight ) {
			LayoutBounds bounds = parent.getInnerBounds();
			double potential = 0;
			if ( parent instanceof FillableRegion ) {
				potential = ((FillableRegion)parent).getMaximumPotentialHeight();
			}
			double hei = bounds.getHeight()-potential;
			this.size.y = hei;
		}
		
		super.position(parent);
	}

	@Override
	protected boolean canPackElementWidth() {
		return !this.isFillToParentWidth();
	}
	
	@Override
	protected boolean canPackElementHeight() {
		return !this.isFillToParentHeight();
	}
}
