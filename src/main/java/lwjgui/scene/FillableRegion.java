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
	protected void resize() {
		
		super.resize();
		
		if ( fillToParentWidth ) {
			if ( parent != null ) {
				double ocupiedSize = 0;
				if ( parent instanceof Region ) {
					ocupiedSize += ((Region)parent).getMinimumPotentialWidth();
					ocupiedSize += ((Region)parent).getBorder().getWidth();
				}
				double wid = parent.getInnerBounds().getWidth()-ocupiedSize;
				
				if ( wid > this.getMinWidth() )
					this.size.x = wid;
			}
		}
		if ( fillToParentHeight ) {
			if ( parent != null ) {
				double ocupiedSize = 0;
				if ( parent instanceof Region ) {
					ocupiedSize += ((Region)parent).getMinimumPotentialHeight();
					ocupiedSize += ((Region)parent).getBorder().getHeight();
				}
				double hei = parent.getInnerBounds().getHeight()-ocupiedSize;
				
				if ( hei > this.getMinHeight() )
					this.size.y = hei;
			}
		}
	}

	@Override
	protected boolean packToDescendentElementWidth() {
		return !this.isFillToParentWidth();
	}
	
	@Override
	protected boolean packToDescendentElementHeight() {
		return !this.isFillToParentHeight();
	}
}
