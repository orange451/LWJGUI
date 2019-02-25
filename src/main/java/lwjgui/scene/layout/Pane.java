package lwjgui.scene.layout;

import lwjgui.collections.ObservableList;
import lwjgui.scene.Node;
import lwjgui.scene.FillableRegion;
import lwjgui.theme.Theme;

public class Pane extends FillableRegion {
	//private boolean scrollableX;
	//private boolean scrollableY;

	public Pane() {
		this.setBackground(Theme.current().getPane());
		this.setPrefSize(1, 1);
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	@Override
	protected void position(Node parent) {
		super.position(parent);

		//float maxWidthInside = (float) getMaxElementWidth();
		//float maxHeightInside = (float) getMaxElementHeight();
		//scrollableX = maxWidthInside > this.getAbsoluteX() + this.getWidth();
		//scrollableY = maxHeightInside > this.getAbsoluteY() + this.getHeight();
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
}
