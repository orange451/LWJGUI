package lwjgui.scene.control;

import lwjgui.scene.Context;
import lwjgui.scene.Region;
import lwjgui.scene.Scene;

public abstract class PopupWindow extends Region {
	protected double absoluteX;
	protected double absoluteY;
	
	protected boolean mouseEntered;
	protected boolean autoHide;
	private boolean open;
	
	public abstract void render(Context context);

	public void setAbsolutePosition(double absoluteX, double absoluteY) {
		this.absolutePosition.set(absoluteX, absoluteY);
		this.absoluteX = absoluteX;
		this.absoluteY = absoluteY;
	}
	
	public void show(Scene scene, double absoluteX, double absoluteY) {
		this.position(scene);
		this.setAbsolutePosition(absoluteX, absoluteY);
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
		return x >= getAbsoluteX() && x <= getAbsoluteX()+getWidth() && y >= getAbsoluteY() && y <= getAbsoluteY() + getHeight();
	}

	public void weakClose() {
		if ( mouseEntered && autoHide ) {
			close();
		}
	}
}

