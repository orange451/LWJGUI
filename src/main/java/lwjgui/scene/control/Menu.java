package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.scene.Context;
import lwjgui.scene.Node;

public class Menu extends MenuItem {
	
	private ObservableList<MenuItem> items = new ObservableList<MenuItem>();
	private ContextMenu context;
	
	public Menu(String string) {
		super(string);
		background = null;
		
		context = new ContextMenu();
		context.setAutoHide(true);
		
		this.items.setAddCallback(new ElementCallback<MenuItem>() {
			@Override
			public void onEvent(MenuItem changed) {
				if ( changed == null )
					return;
				context.getItems().add(changed);
			}
		});
		
		this.items.setRemoveCallback(new ElementCallback<MenuItem>() {
			@Override
			public void onEvent(MenuItem changed) {
				context.getItems().remove(changed);
			}
		});
		
		this.setOnMousePressed( event -> {
			open();
		});
		
		this.mouseReleasedEvent = null;
	}
	
	public void setAutoHide(boolean autoHide) {
		this.context.setAutoHide(autoHide);
	}
	
	@Override
	protected void setContent(String string, Font font, Node graphic) {
		super.setContent(string, font, graphic);
		
		this.internalLabel.setPadding(new Insets(0,6,0,6));
	}
	
	public boolean isOpen() {
		return this.context.isOpen();
	}

	public ObservableList<MenuItem> getItems() {
		return this.items;
	}
	
	@Override
	public boolean isSelected() {
		return this.isHover() || this.isOpen();
	}
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		super.render(context);
	}

	public void open() {
		((MenuBar)(getParent().getParent())).isOpen = true;
		((MenuBar)(getParent().getParent())).currentMenu = Menu.this;
		context.setStylesheet(this.getStylesheet());
		context.show(Menu.this.getScene(), getX(), getY()+getHeight());
	}

	public void close() {
		((MenuBar)(getParent().getParent())).isOpen = false;
		context.close();
	}
}
