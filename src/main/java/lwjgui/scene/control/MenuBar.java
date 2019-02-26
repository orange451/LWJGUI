package lwjgui.scene.control;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.layout.HBox;
import lwjgui.theme.Theme;

public class MenuBar extends FillableRegion {
	
	private ObservableList<Menu> items = new ObservableList<Menu>();
	private HBox internalBox;
	
	protected boolean isOpen;
	protected Menu currentMenu;
	
	public MenuBar() {
		this.setPrefWidth(Integer.MAX_VALUE);
		this.setFillToParentWidth(true);
		this.setMinHeight(24);
		this.setMaxHeight(24);
		
		this.setPadding(new Insets(0, 6, 0, 6));
		
		this.internalBox = new HBox();
		this.internalBox.setAlignment(Pos.TOP_LEFT);
		this.internalBox.setFillToParentHeight(true);
		this.internalBox.setBackground(null);
		this.children.add(internalBox);
		
		this.items.setAddCallback(new ElementCallback<Menu>() {
			@Override
			public void onEvent(Menu changed) {
				recalculate();
			}
		});
		
		this.items.setRemoveCallback(new ElementCallback<Menu>() {
			@Override
			public void onEvent(Menu changed) {
				recalculate();
			}
		});
	}

	protected void recalculate() {
		internalBox.getChildren().clear();
		for (int i = 0; i < items.size(); i++) {
			internalBox.getChildren().add(items.get(i));
			items.get(i).setPrefHeight(getHeight());
		}
		if ( getParent() != null ) {
			position(getParent());
		}
	}
	
	public ObservableList<Menu> getItems() {
		return this.items;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	public void position(Node parent) {
		this.setAlignment(Pos.TOP_LEFT);
		
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setPrefHeight(getHeight());
		}
		
		super.position(parent);
		
		if ( !isOpen ) {
			currentMenu = null;
		} else {
			if ( currentMenu != null ) {
				if (!currentMenu.isOpen() ) {
					currentMenu = null;
				} else {
					Node hover = this.cached_context.getHovered();//LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext().getHovered();
					if ( hover instanceof Menu ) {
						Menu menu = (Menu) hover;
						if ( items.contains(menu) ) {
							currentMenu.close();
							menu.open();
						}
					}
				}
			}
		}
	}

	@Override
	public void render(Context context) {
		clip(context);
		long vg = context.getNVG();
		
		// Gradient
		NVGPaint bg = NanoVG.nvgLinearGradient(vg, 0, 0, 0, (float)getHeight(), Theme.current().getPane().getNVG(), Theme.current().getPaneAlt().getNVG(), NVGPaint.calloc());
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
		NanoVG.nvgFillPaint(vg, bg);
		NanoVG.nvgFill(vg);
		
		// Divider line
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, (int)getX(), (int)(getY()+getHeight()-1), (int)getWidth(), 1);
		NanoVG.nvgFillColor(vg, Theme.current().getControlOutline().getNVG());
		NanoVG.nvgFill(vg);
		
		// Render internal box
		this.internalBox.render(context);
	}

}
