package lwjgui.style;
public class Percentage {
	public static final Percentage ONE_HUNDRED = new Percentage(100);
	public static final Percentage SEVENTY_FIVE = new Percentage(75);
	public static final Percentage FIFTY = new Percentage(50);
	public static final Percentage TWENTY_FIVE = new Percentage(25);
	public static final Percentage ZERO = new Percentage(0);
	
	private final double percent;

	public Percentage(double percent) {
		this.percent = percent;
	}

	public double getPercent() {
		return percent;
	}

	public double getValue() {
		return percent / 100d;
	}
	
	public String toString() {
		return percent + "%";
	}

	public double getValueClamped() {
		return Math.min(1, Math.max(0, getValue()));
	}

	public static Percentage fromRatio(double ratio) {
		return new Percentage(ratio*100);
	}
}