package lwjgui.scene.control;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

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
		this.flag_clip = false;
		
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

	@Override
	public String getElementType() {
		return "toolbar";
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
			this.internalBox.setBackgroundLegacy(null);
			this.internalBox.setSpacing(4);
			this.children.add(internalBox);
			
			this.setPadding(new Insets(3, 6, 4, 6));
		} else {
			this.setPrefWidth(0);
			this.setPrefHeight(Integer.MAX_VALUE);
			this.setFillToParentWidth(false);
			this.setFillToParentHeight(true);
			
			this.children.clear();
			this.internalBox = new VBox();
			this.internalBox.setBackgroundLegacy(null);
			this.internalBox.setSpacing(3);
			this.children.add(internalBox);

			this.setPadding(new Insets(6, 3, 6, 3));
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
		if ( !isVisible() )
			return;
		
		clip(context);
		long vg = context.getNVG();
		
		// Gradient
		NanoVG.nvgTranslate(vg, (int)getX(), (int)getY());
		try (MemoryStack stack = stackPush()) {
			NVGPaint bg = NanoVG.nvgLinearGradient(vg, 0, 0, 0, (float)getHeight()*0.7f, Theme.current().getPane().getNVG(), Theme.current().getPaneAlt().getNVG(), NVGPaint.callocStack(stack));
			if ( orientation.equals(Orientation.VERTICAL) ) {
				bg = NanoVG.nvgLinearGradient(vg, 0, 0, (float)getWidth(), 0, Theme.current().getPane().getNVG(), Theme.current().getPaneAlt().getNVG(), NVGPaint.callocStack(stack));
			}
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRect(vg, 0, 0, (int)getWidth(), (int)getHeight());
			NanoVG.nvgFillPaint(vg, bg);
			NanoVG.nvgFill(vg);
		}
		NanoVG.nvgTranslate(vg, (int)-getX(), (int)-getY());
		
		// Divider line
		NanoVG.nvgBeginPath(vg);
		if ( orientation.equals(Orientation.VERTICAL) ) {
			NanoVG.nvgRect(vg, (int)(getX()+getWidth()-1), (int)getY(), 1, (int)getHeight());
		} else {
			NanoVG.nvgRect(vg, (int)getX(), (int)(getY()+getHeight()-1), (int)getWidth(), 1);
		}
		NanoVG.nvgFillColor(vg, Theme.current().getControlOutline().getNVG());
		NanoVG.nvgFill(vg);
		
		// Render internal box
		this.internalBox.render(context);
	}

}
