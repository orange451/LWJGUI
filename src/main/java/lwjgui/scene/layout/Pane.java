package lwjgui.scene.layout;

import lwjgui.collections.ObservableList;
import lwjgui.paint.Color;
import lwjgui.scene.Node;
import lwjgui.style.Background;
import lwjgui.style.BackgroundSolid;
import lwjgui.style.BlockPaneRenderer;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.scene.Context;
import lwjgui.scene.FillableRegion;

public abstract class Pane extends FillableRegion implements BlockPaneRenderer {
	
	private Background background;
	private Color borderColor;
	private float[] borderRadii;
	private BorderStyle borderStyle;
	private ObservableList<BoxShadow> boxShadows = new ObservableList<>();
	
	public Pane() {
		this.setBackground(null);
		this.setPrefSize(1, 1);
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.setBorderStyle(BorderStyle.NONE);
		this.setBorderRadii(0);
		
		this.flag_clip = false;
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
		if ( !isVisible() )
			return;
		
		// Apply CSS
		this.stylePush();
		{
			// Render standard pane
			BlockPaneRenderer.render(context, this);
			
			// Draw children
			super.render(context);
		}
		this.stylePop();
	}
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */
	public void setBackgroundLegacy(Color color) {
		setBackground( new BackgroundSolid(color) );
	}
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */	
	public void setBackground(Background color) {
		this.background = color;
	}
	
	/**
	 * Get the current background color of this node.
	 * @return
	 */
	public Background getBackground() {
		return this.background;
	}
	
	@Override
	public void setBorderStyle(BorderStyle style) {
		this.borderStyle = style;
	}

	@Override
	public BorderStyle getBorderStyle() {
		return this.borderStyle;
	}

	@Override
	public float[] getBorderRadii() {
		return borderRadii;
	}

	@Override
	public void setBorderRadii(float radius) {
		this.setBorderRadii(radius, radius, radius, radius);
	}

	@Override
	public void setBorderRadii(float[] radius) {
		this.setBorderRadii(radius[0], radius[1], radius[2], radius[3]);
	}

	@Override
	public void setBorderRadii(float cornerTopLeft, float cornerTopRight, float cornerBottomRight, float cornerBottomLeft) {
		this.borderRadii = new float[] {cornerTopLeft, cornerTopRight, cornerBottomRight, cornerBottomLeft};
	}

	@Override
	public void setBorderColor(Color color) {
		this.borderColor = color;
	}

	@Override
	public Color getBorderColor() {
		return this.borderColor;
	}

	@Override
	public ObservableList<BoxShadow> getBoxShadowList() {
		return this.boxShadows;
	}
}
