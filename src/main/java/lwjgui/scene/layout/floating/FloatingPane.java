package lwjgui.scene.layout.floating;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.collections.ObservableList;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Region;

public class FloatingPane extends Region {
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

	@Override
	protected void position(Node parent) {
		super.position(parent);

		this.absolutePosition.set(absx,absy);
	}

	@Override
	public void setAbsolutePosition(double x, double y) {
		super.setAbsolutePosition(x, y);
		this.absx = x;
		this.absy = y;
	}
}
