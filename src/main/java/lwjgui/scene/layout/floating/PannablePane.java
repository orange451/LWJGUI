package lwjgui.scene.layout.floating;

import lwjgui.Color;

public class PannablePane extends DraggablePane {
	public PannablePane() {
		this.setBackground(Color.GRAY);
		this.flag_clip = true;
		
		this.center();
	}
	
	public void center() {
		final int t = 512;
		this.setAbsolutePosition(-Integer.MAX_VALUE/t, -Integer.MAX_VALUE/t);
		this.setMinSize(Integer.MAX_VALUE/(t/2), Integer.MAX_VALUE/(t/2));
	}
}
