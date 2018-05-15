package lwjgui.scene.layout;

import lwjgui.Color;
import lwjgui.geometry.Node;
import lwjgui.geometry.ObservableList;
import lwjgui.theme.Theme;

public abstract class Pane extends Region {
	private Color backgroundColor;
	private boolean scrollableX;
	private boolean scrollableY;
	
	public Pane() {
		this.backgroundColor = Theme.currentTheme().getPane();
	}
	
	public void setBackground(Color color) {
		this.backgroundColor = color;
	}
	
	public Color getBackground() {
		return this.backgroundColor;
	}
	
	@Override
	public void position(Node parent) {
		super.position(parent);

		float maxWidthInside = (float) getMaxElementWidth();
		float maxHeightInside = (float) getMaxElementHeight();
		scrollableX = maxWidthInside > this.getAbsoluteX() + this.getWidth();
		scrollableY = maxHeightInside > this.getAbsoluteY() + this.getHeight();
	}
	
    /**
    *
    * @return modifiable list of children.
    */
   @Override
   public ObservableList<Node> getChildren() {
       return super.getChildren();
   }
}
