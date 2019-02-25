package lwjgui.transition;

import lwjgui.paint.Color;
import lwjgui.transition.Transition;

public class FillTransition extends Transition {

	private Color fromFill, toFill, storeFill;
	
	/**
	 * Transitions one color to another. This constructor is simplified to where the color that the result is stored in is also the one that the transition starts at.
	 * 
	 * @param durationInMillis - time in milliseconds for the transition to occur
	 * @param fromAndStoreFill - the starting fill and the fill to store the blend results in
	 * @param toFill - the destination fill
	 */
	public FillTransition(long durationInMillis, Color fromAndStoreFill, Color toFill) {
		this(durationInMillis, fromAndStoreFill, toFill, fromAndStoreFill);
	}
	
	/**
	 * Transitions one color to another and stores the result in a third color. If you set a Control's fill with the storeFill, it should be automatically updated
	 * as this Transition progresses (if you're using an immutable color, use copy() when setting it on the Control to ensure that it's recycled).
	 * 
	 * @param durationInMillis - time in milliseconds for the transition to occur
	 * @param fromFill - the starting fill
	 * @param toFill - the destination fill
	 * @param storeFill - the fill to store the blend results in
	 */
	public FillTransition(long durationInMillis, Color fromFill, Color toFill, Color storeFill) {
		super(durationInMillis);
		this.fromFill = fromFill;
		this.toFill = toFill;
		this.storeFill = storeFill;
	}

	@Override
	public void tick(double progress) {
		Color.blend(fromFill, toFill, storeFill, progress);
	}
}
