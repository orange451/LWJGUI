package lwjgui.event;

public class ScrollEvent extends Event {
	public final double x;
	public final double y;
	
	public ScrollEvent( double x, double y ) {
		this.x = x;
		this.y = y;
	}

}
