package lwjgui.event;

public class MouseEvent extends Event {
	public final int button;
	private final int clicks;

	public MouseEvent( int button ) {
		this(button, 0);
	}
	public MouseEvent( int button, int clicks ) {
		this.button = button;
		this.clicks = clicks;
	}
	
	public final int getClickCount() {
		return clicks;
	}
}
