package lwjgui.event;

public class MouseEvent extends Event {
	public final int button;
	
	public MouseEvent( int button ) {
		this.button = button;
	}
}
