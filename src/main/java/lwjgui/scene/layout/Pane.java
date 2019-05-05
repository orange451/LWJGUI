package lwjgui.scene.layout;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.collections.ObservableList;
import lwjgui.paint.Color;
import lwjgui.scene.Node;
import lwjgui.scene.Context;
import lwjgui.scene.FillableRegion;
import lwjgui.theme.Theme;

public class Pane extends FillableRegion {
	//private boolean scrollableX;
	//private boolean scrollableY;
	
	private Color backgroundColor;
	private Color paddingColor;

	public Pane() {
		this.setBackground(Theme.current().getPane());
		this.setPrefSize(1, 1);
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	@Override
	protected void position(Node parent) {
		super.position(parent);
	}
	
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
	
	/**
	 * Sets the color of this nodes padding. If the color is null, nothing will be drawn.
	 * @param color
	 */
	public void setPaddingColor(Color color) {
		this.paddingColor = color;
	}
	
	/**
	 * Returns the color used to draw the padding. If the color is null, nothing will be drawn.
	 * @return
	 */
	public Color getPaddingColor() {
		return this.paddingColor;
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
	
	@Override
	public void render(Context context) {
		if ( getBackground() != null ) {
			double boundsX = getX();
			double boundsY = getY();
			double boundsW = getWidth();
			double boundsH = getHeight();
			
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRect(context.getNVG(), (int) boundsX, (int) boundsY, (float) boundsW, (float) boundsH);
			NanoVG.nvgFillColor(context.getNVG(), getBackground().getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
		
		if ( this.getPaddingColor() != null ) {
			if ( this.getPadding().getTop() > 0 ) {
				int xx1 = (int) (this.getX()+this.getInnerBounds().getX());
				int yy1 = (int) (this.getY());
				NanoVG.nvgBeginPath(context.getNVG());
				NanoVG.nvgRect(context.getNVG(), xx1, yy1, (float)this.getInnerBounds().getWidth(), (float)this.getPadding().getTop());
				NanoVG.nvgFillColor(context.getNVG(), getPaddingColor().getNVG());
				NanoVG.nvgFill(context.getNVG());
			}

			if ( this.getPadding().getBottom() > 0 ) {
				int xx1 = (int) (this.getX()+this.getInnerBounds().getX());
				int yy1 = (int) (this.getY()+getHeight()-this.getPadding().getBottom());
				NanoVG.nvgBeginPath(context.getNVG());
				NanoVG.nvgRect(context.getNVG(), xx1, yy1, this.getInnerBounds().getWidth(), (float)this.getPadding().getBottom());
				NanoVG.nvgFillColor(context.getNVG(), getPaddingColor().getNVG());
				NanoVG.nvgFill(context.getNVG());
			}
			
			if ( this.getPadding().getRight() > 0 ) {
				int xx1 = (int) (this.getX()+getWidth()-this.getPadding().getRight());
				int yy1 = (int) (this.getY());
				NanoVG.nvgBeginPath(context.getNVG());
				NanoVG.nvgRect(context.getNVG(), xx1, yy1, (float)this.getPadding().getRight(), (float)this.getHeight());
				NanoVG.nvgFillColor(context.getNVG(), getPaddingColor().getNVG());
				NanoVG.nvgFill(context.getNVG());
			}
			
			if ( this.getPadding().getLeft() > 0 ) {
				int xx1 = (int) (this.getX());
				int yy1 = (int) (this.getY());
				NanoVG.nvgBeginPath(context.getNVG());
				NanoVG.nvgRect(context.getNVG(), xx1, yy1, (float)this.getPadding().getLeft(), (float)this.getHeight());
				NanoVG.nvgFillColor(context.getNVG(), getPaddingColor().getNVG());
				NanoVG.nvgFill(context.getNVG());
			}
		}
		
		super.render(context);
	}
}
