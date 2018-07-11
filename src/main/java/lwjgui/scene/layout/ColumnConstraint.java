package lwjgui.scene.layout;

public class ColumnConstraint {
	private double minWidth;
	private double maxWidth;
	private double prefWidth;
	
	public ColumnConstraint() {
		this(0);
	}
	
	public ColumnConstraint( double prefWidth ) {
		this(0, prefWidth, Double.MAX_VALUE);
	}
	
	public ColumnConstraint( double minWidth, double prefWidth, double maxWidth ) {
		this.minWidth = minWidth;
		this.prefWidth = prefWidth;
		this.maxWidth = maxWidth;
	}
	
	public double getPrefWidth() {
		return this.prefWidth;
	}
	
	public double getMinWidth() {
		return this.minWidth;
	}
	
	public double getMaxWidth() {
		return this.maxWidth;
	}
}