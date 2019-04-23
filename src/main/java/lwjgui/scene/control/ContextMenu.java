package lwjgui.scene.control;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.layout.VBox;
import lwjgui.theme.Theme;

public class ContextMenu extends PopupWindow {
	private VBox internalBox;
	private ContextMenu childMenu;
	private Node returnNode;
	
	private ObservableList<MenuItem> items = new ObservableList<MenuItem>();
	
	public ContextMenu() {
		this.setAutoHide(true);
		this.internalBox = new VBox();
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
	}

	@Override
	public void show(Scene scene, double absoluteX, double absoluteY) {
		this.returnNode = LWJGUI.getCurrentContext().getSelected();
		super.show(scene, absoluteX, absoluteY);
	}
	
	private void recalculate() {
		// Fill the internal box with our items
		internalBox.getChildren().clear();
		for (int i = 0; i < items.size(); i++) {
			internalBox.getChildren().add(items.get(i));
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
		
		LWJGUI.runLater(()->{
			if ( this.returnNode != null ) {
				LWJGUI.getCurrentContext().setSelected(returnNode);
				this.returnNode = null;
			}
		});
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		// Position the menu
		this.position(getParent());
		
		// Position internal box inside menu
		this.setAlignment(Pos.TOP_LEFT);
		//internalBox.position(this);
		
		// Get my width, and resize internal buttons so that they match the width of the box
		double innerWidth = this.getInnerBounds().getWidth();
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setPrefWidth(innerWidth);
			items.get(i).setMinWidth(innerWidth);
		}
		
		// If the mouse is ontop of me, then set the mouse entered flag to true
		// This is used to autohide the menu when the mouse leaves (if flag set)
		if ( context.isHovered(this) ) {
			this.mouseEntered = true;
		}
		
		// Setup rendering info
		long vg = context.getNVG();
		int x = (int) getX();
		int y = (int) getY();
		int w = (int) getWidth();
		int h = (int) getHeight();
		
		// Draw Drop Shadow
		//this.clip(context,16);
		NVGPaint paint = NanoVG.nvgBoxGradient(vg, x+2,y+3, w-2,h, 4, 12, Theme.current().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.create());
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, x-16,y-16, w+32,h+32);
		NanoVG.nvgFillPaint(vg, paint);
		NanoVG.nvgFill(vg);
		
		// Draw Outline
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRect(context.getNVG(), (int)x-1, (int)y-1, (int)w+2, (int)h+2);
		NanoVG.nvgFillColor(context.getNVG(), Theme.current().getControlOutline().getNVG());
		NanoVG.nvgFill(context.getNVG());
		
		// Render insides
		//this.internalBox.render(context);
		super.render(context);
	}
}
