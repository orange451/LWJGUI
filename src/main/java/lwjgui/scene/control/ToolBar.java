package lwjgui.scene.control;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.DirectionalBox;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.VBox;
import lwjgui.theme.Theme;

public class ToolBar extends Control {
	private ObservableList<Node> items = new ObservableList<Node>();
	private DirectionalBox internalBox;
	private Orientation orientation;
	
	public ToolBar() {
		this.setOrientation(Orientation.HORIZONTAL);
		this.setMinHeight(24);
		this.setMaxHeight(24);
		
		this.items.setAddCallback(new ElementCallback<Node>() {
			@Override
			public void onEvent(Node changed) {
				recalculate();
			}
		});
		
		this.items.setRemoveCallback(new ElementCallback<Node>() {
			@Override
			public void onEvent(Node changed) {
				recalculate();
			}
		});
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		
		if ( orientation.equals(Orientation.HORIZONTAL) ) {
			this.setPrefWidth(Integer.MAX_VALUE);
			this.setPrefHeight(0);
			this.setFillToParentWidth(true);
			this.setFillToParentHeight(false);
			
			this.children.clear();
			this.internalBox = new HBox();
			this.internalBox.setBackground(null);
			this.children.add(internalBox);
			
			this.setPadding(new Insets(0, 6, 0, 6));
		} else {
			this.setPrefWidth(0);
			this.setPrefHeight(Integer.MAX_VALUE);
			this.setFillToParentWidth(false);
			this.setFillToParentHeight(true);
			
			this.children.clear();
			this.internalBox = new VBox();
			this.internalBox.setBackground(null);
			this.children.add(internalBox);

			this.setPadding(new Insets(6, 0, 6, 0));
		}
		
		recalculate();
	}

	protected void recalculate() {
		internalBox.getChildren().clear();
		for (int i = 0; i < items.size(); i++) {
			internalBox.getChildren().add(items.get(i));
		}
	}
	
	public ObservableList<Node> getItems() {
		return this.items;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	public void position(Node parent) {
		this.setAlignment(Pos.TOP_LEFT);
		super.position(parent);
	}

	@Override
	public void render(Context context) {
		clip(context);
		long vg = context.getNVG();
		
		// Gradient
		NVGPaint bg = NanoVG.nvgLinearGradient(vg, 0, 0, 0, (float)getHeight(), Theme.currentTheme().getPane().getNVG(), Theme.currentTheme().getControlAlt().getNVG(), NVGPaint.calloc());
		if ( orientation.equals(Orientation.VERTICAL) ) {
			bg = NanoVG.nvgLinearGradient(vg, 0, 0, (float)getWidth(), 0, Theme.currentTheme().getPane().getNVG(), Theme.currentTheme().getControlAlt().getNVG(), NVGPaint.calloc());
		}
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, (int)getAbsoluteX(), (int)getAbsoluteY(), (int)getWidth(), (int)getHeight());
		NanoVG.nvgFillPaint(vg, bg);
		NanoVG.nvgFill(vg);
		
		// Divider line
		NanoVG.nvgBeginPath(vg);
		if ( orientation.equals(Orientation.VERTICAL) ) {
			NanoVG.nvgRect(vg, (int)(getAbsoluteX()+getWidth()-1), (int)getAbsoluteY(), 1, (int)getHeight());
		} else {
			NanoVG.nvgRect(vg, (int)getAbsoluteX(), (int)(getAbsoluteY()+getHeight()-1), (int)getWidth(), 1);
		}
		NanoVG.nvgFillColor(vg, Theme.currentTheme().getControlOutline().getNVG());
		NanoVG.nvgFill(vg);
		
		// Render internal box
		this.internalBox.render(context);
	}

}
