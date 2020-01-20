package lwjgui.scene.control;

import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.layout.VBox;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.theme.Theme;

public class ContextMenu extends PopupWindow {
	private VBox internalBox;
	private ContextMenu childMenu;
	private Node returnNode;
	
	private ObservableList<MenuItem> items = new ObservableList<MenuItem>();
	
	public ContextMenu() {
		this.setAutoHide(true);
		this.internalBox = new VBox();
		this.internalBox.setBackgroundLegacy(null);
		this.children.add(this.internalBox);
		
		this.items.setAddCallback(new ElementCallback<MenuItem>() {
			@Override
			public void onEvent(MenuItem changed) {
				recalculate();
			}
		});
		
		this.items.setRemoveCallback(new ElementCallback<MenuItem>() {
			@Override
			public void onEvent(MenuItem changed) {
				recalculate();
			}
		});
		
		// Style
		float r = 2;
		this.getBoxShadowList().add(new BoxShadow(4,5,18,-2));
		this.setBorderColor(Theme.current().getControlOutline());
		this.setBackgroundLegacy(Theme.current().getPane());
		this.setBorderWidth(1);
		this.setBorderStyle(BorderStyle.SOLID);
		this.setBorderRadii(r);
		this.setPadding(new Insets(r,0,r,0));
		
		// Default list-view class
		this.getClassList().add("list-view");
	}

	@Override
	public void show(Scene scene, double absoluteX, double absoluteY) {
		this.returnNode = window.getContext().getSelected();
		super.show(scene, absoluteX, absoluteY);
	}
	
	private void recalculate() {
		// Remove items from internal that are no longer in context menu
		for (int i = 0; i< internalBox.getChildren().size(); i++) {
			if ( !this.items.contains( (MenuItem) internalBox.getChildren().get(i) ) ) {
				internalBox.getChildren().remove(i--);
			}
		}
		
		// Add items from context menu that are not yet in internal
		for (int i = 0; i < items.size(); i++) {
			if ( !internalBox.getChildren().contains(items.get(i)) ) {
				internalBox.getChildren().add(items.get(i));
			}
		}
	}
	
	public ObservableList<MenuItem> getItems() {
		return this.items;
	}

	public ContextMenu getChild() {
		return this.childMenu;
	}
	
	@Override
	public void close() {
		if ( childMenu != null ) {
			childMenu.close();
		}
		
		super.close();
		
		if (this.returnNode != null) {
			this.window.getContext().setSelected(returnNode);
			this.returnNode = null;
		}
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		// Position the menu
		this.setAlignment(Pos.TOP_LEFT);
		this.position(getParent());
		
		// Get my width, and resize internal buttons so that they match the width of the box
		double innerWidth = this.getInnerBounds().getWidth();
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setPrefWidth(innerWidth);
			items.get(i).setMinWidth(innerWidth);
		}
		
		// If the mouse is ontop of me, then set the mouse entered flag to true
		// This is used to autohide the menu when the mouse leaves (if flag set)
		if ( context != null && context.isHovered(this) ) {
			this.mouseEntered = true;
		}
		
		// Render insides
		//this.internalBox.render(context);
		super.render(context);
	}
}
