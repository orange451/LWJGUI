package lwjgui.scene.control;

import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.theme.Theme;

/**
 * Creates a node that exists above a scene. This is useful in situations where you need GUI elements such as right click context menus, and so on.
 */
public abstract class PopupWindow extends FloatingPane {
	protected boolean mouseEntered;
	protected boolean autoHide;
	private boolean open;
	
	public PopupWindow() {
		this.setBackgroundLegacy(Theme.current().getPane());
	}
	
	public void show(Scene scene, double absoluteX, double absoluteY) {
		// Make sure the popup window is properly sized (i.e. buffer)
		for (int i = 0; i < 2; i++) {
			this.position(scene);
		}
		this.setAbsolutePosition((int)absoluteX, (int)absoluteY);
		this.position(scene);
		
		scene.showPopup(this);
		mouseEntered = false;
		this.open = true;
		
		for (int i = 0; i < 8; i++) {
			this.position(this.getScene());
		}
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		// Reposition if outside of screen
		if ( this.getY() + this.getHeight() > this.getScene().getHeight() )
			this.setAbsolutePosition(getX(), getScene().getHeight()-this.getHeight());
		if ( this.getY() < 0 )
			this.setAbsolutePosition(getX(), 0);
		if ( this.getX() + this.getWidth() > this.getScene().getWidth() )
			this.setAbsolutePosition(getScene().getWidth()-this.getWidth(), getY());
		if ( this.getX() < 0 )
			this.setAbsolutePosition(0, getY());
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
		if ( !isVisible() )
			return;
		
		this.position(this.getScene());
		
		super.render(context);
	}
}

