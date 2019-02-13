package lwjgui.transition;

import lwjgui.Color;
import lwjgui.scene.shape.Shape;
import lwjgui.transition.Transition;

public class ShapeFillTransition extends Transition {

	private Shape shape;
	private Color fromFill, toFill, storeFill;
	
	public ShapeFillTransition(long durationInMillis, Shape shape, Color fromFill, Color toFill) {
		super(durationInMillis);
		this.shape = shape;
		this.fromFill = fromFill;
		this.toFill = toFill;
		
		storeFill = new Color(fromFill);
	}

	@Override
	public void tick(double progress) {
		shape.setFill(Color.blend(fromFill, toFill, storeFill, progress));
	}
}
