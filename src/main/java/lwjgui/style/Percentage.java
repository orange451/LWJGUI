package lwjgui.style;
public class Percentage {
	private double percent;

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
}