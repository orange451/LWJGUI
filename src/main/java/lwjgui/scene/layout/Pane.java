package lwjgui.scene.layout;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.paint.Color;
import lwjgui.scene.Node;
import lwjgui.style.Background;
import lwjgui.style.BackgroundSolid;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.style.StyleBackground;
import lwjgui.style.StyleBorder;
import lwjgui.style.StyleBoxShadow;
import lwjgui.scene.Context;
import lwjgui.scene.FillableRegion;

public abstract class Pane extends FillableRegion implements StyleBorder,StyleBackground,StyleBoxShadow {
	
	private Background background;
	private Color borderColor;
	private float[] borderRadii;
	private float borderWidth;
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

	@Override
	protected void position(Node parent) {

		if ( this.cached_context != null ) {
			// Add our sheet to the stack
			if ( this.getStylesheet() != null )
				this.cached_context.getCurrentStyling().add(this.getStylesheet());
			
			// Apply styling!
			for (int i = 0; i < cached_context.getCurrentStyling().size(); i++) {
				cached_context.getCurrentStyling().get(i).applyStyling(this);
			}
			
			// Apply our local style if it exists
			if ( this.getStyleLocal() != null ) {
				this.getStyleLocal().applyStyling(this, "NODESTYLE");
			}
		}
		
		super.position(parent);

		// Remove our sheet from the stack
		if ( this.cached_context != null ) {
			if ( this.getStylesheet() != null )
				cached_context.getCurrentStyling().remove(this.getStylesheet());
		}
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
		
		// Draw drop shadows
		for (int i = 0; i < getBoxShadowList().size(); i++) {
			BoxShadow shadow = getBoxShadowList().get(i);
			if ( shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, this.getBorderRadii(), (int) getX(), (int) getY(), (int)getWidth(), (int)getHeight());
		}
		
		// Draw border
		if ( this.getBorderStyle() != BorderStyle.NONE && this.getBorderWidth() > 0 && this.getBorderColor() != null ) {
			LWJGUIUtil.drawBorder(context, getX(), getY(), getWidth(), getHeight(), this.getBorderWidth(), this.getBackground(), this.getBorderColor(), this.getBorderRadii() );
		}
		
		// Draw background
		if ( getBackground() != null ) {
			getBackground().render(context, getX(), getY(), getWidth(), getHeight(), getBorderRadii());
		}
		
		// Draw inset shadows
		for (int i = 0; i < getBoxShadowList().size(); i++) {
			BoxShadow shadow = getBoxShadowList().get(i);
			if ( !shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, this.getBorderRadii(), (int) getX(), (int) getY(), (int)getWidth(), (int)getHeight());
		}
		
		// Draw children
		super.render(context);
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
	public void setBorderWidth(float width) {
		this.borderWidth = width;
	}

	@Override
	public float getBorderWidth() {
		return this.borderWidth;
	}

	@Override
	public ObservableList<BoxShadow> getBoxShadowList() {
		return this.boxShadows;
	}
}
