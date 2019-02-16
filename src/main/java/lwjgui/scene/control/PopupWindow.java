package lwjgui.scene.control;

import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.layout.floating.FloatingPane;

public abstract class PopupWindow extends FloatingPane {
	protected boolean mouseEntered;
	protected boolean autoHide;
	private boolean open;
	
	public void show(Scene scene, double absoluteX, double absoluteY) {
		this.position(scene);
		this.setAbsolutePosition((int)absoluteX, (int)absoluteY);
		scene.showPopup(this);
		mouseEntered = false;
		this.open = true;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void close() {
		this.getScene().closePopup(this);
		this.open = false;
	}
	
	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}

	public boolean contains(double x, double y) {
		return x >= getX() && x <= getX()+getWidth() && y >= getY() && y <= getY() + getHeight();
	}

	public void weakClose() {
		if ( mouseEntered && autoHide ) {
			close();
		}
	}
	
	@Override
	public void render(Context context) {
		this.position(this.getScene());
		
		super.render(context);
	}
}

