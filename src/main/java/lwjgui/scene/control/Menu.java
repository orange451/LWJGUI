package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.event.MouseEvent;
import lwjgui.scene.Context;

public class Menu extends MenuItem {
	
	private ObservableList<MenuItem> items = new ObservableList<MenuItem>();
	private ContextMenu context;
	
	public Menu(String string) {
		super(string, null);
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
		
		this.setMousePressedEvent( event -> {
			open();
		});
		
		this.mouseReleasedEvent = null;
	}
	
	public boolean isOpen() {
		return this.context.isOpen();
	}

	public ObservableList<MenuItem> getItems() {
		return this.items;
	}
	
	@Override
	protected boolean isSelected() {
		return super.isSelected() || this.isOpen();
	}
	
	@Override
	public void render(Context context) {
		super.render(context);
	}

	public void open() {
		((MenuBar)(getParent().getParent())).isOpen = true;
		((MenuBar)(getParent().getParent())).currentMenu = Menu.this;
		context.show(Menu.this.getScene(), getAbsoluteX(), getAbsoluteY()+getHeight());
	}

	public void close() {
		((MenuBar)(getParent().getParent())).isOpen = false;
		context.close();
	}
}
