package lwjgui.scene.layout.floating;

import org.joml.Vector2d;
import org.joml.Vector2f;

import lwjgui.collections.ObservableList;
import lwjgui.scene.Node;
import lwjgui.scene.Parent;
import lwjgui.scene.Region;

public class FloatingPane_BrokenVer_ extends Region {
	private double absx;
	private double absy;
	
	public FloatingPane_BrokenVer_() {
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
	public Vector2d getAvailableSize() {
		return new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	protected void position(Node parent) {
		super.position(parent);
		this.absolutePosition.set(absx,absy);
		
		// If elements are inside us, but top the left/top of us, we need to resize!
		double minx = getMinimumX(this, getX());
		double miny = getMinimumY(this, getY());
		double dx = getX()-minx;
		double dy = getY()-miny;
		//absx -= dx;
		//absy -= dy;
		//this.absolutePosition.set(absx,absy);
		this.absolutePosition.sub(dx,dy);
		//this.size.add(dx,dy);
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

	@Override
	public void setAbsolutePosition(double x, double y) {
		super.setAbsolutePosition(x, y);
		this.absx = x;
		this.absy = y;
	}
}
