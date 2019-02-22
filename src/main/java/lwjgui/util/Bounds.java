package lwjgui.util;

public class Bounds {
	protected double startX;
	protected double startY;
	protected double endX;
	protected double endY;
	
	public Bounds(double startX, double startY, double endX, double endY) {
		set(startX, startY, endX, endY);
	}

	public double getWidth() {
		return endX - startX;
	}

	public double getHeight() {
		return endY - startY;
	}

	public double getX() {
		return startX;
	}

	public double getY() {
		return startY;
	}
	
	public void set(double startX, double startY, double endX, double endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public  boolean isInside(double px, double py){
		double apx = px;
		double apy = py;
		
		double ax1 = getX();
		double ay1 = getY();
		double aw1 = getWidth();
		double ah1 = getHeight();
		
		if (apx >= ax1 && apx <= ax1 + aw1){
			if (apy >= ay1 && apy <= ay1 + ah1){
				return true;
			}
		}

		return false;
	}
}
