package lwjgui.transition;

public abstract class SizeTransition extends Transition {
	private double targetWidth, targetHeight;
	
	public SizeTransition(long durationInMillis, double targetWidth, double targetHeight) {
		super(durationInMillis);
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	@Override
	public void tick(double progress) {
		setWidth(changeSize(progress, getCurrentWidth(), targetWidth));
		setHeight(changeSize(progress, getCurrentHeight(), targetHeight));
	}
	
	private double changeSize(double progress, double current, double target) {
		if (current > target) {
			double d = (current- target);
			return (target + (d * (1f - progress)));
		} else {
			double d = (target - current);
			return (current + (d * progress));
		}
	}
	
	protected abstract double getCurrentWidth();
	protected abstract double getCurrentHeight();
	
	protected abstract void setWidth(double width);
	protected abstract void setHeight(double height);
}
