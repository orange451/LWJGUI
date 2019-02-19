package lwjgui.scene.control;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.event.ButtonEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class MenuItem extends Node {
	protected EventHandler<ButtonEvent> buttonEvent;
	private Node internalNode;
	private static final int padding = 4;
	protected Color background;
	
	public MenuItem(String string) {
		this(string, null);
	}
	
	public MenuItem(String string, Node graphic) {
		this.internalNode = new Label(string);
		((Label)this.internalNode).setGraphic(graphic);
		((Label)this.internalNode).setPadding(new Insets(0,padding,0,padding));
		((Label)this.internalNode).setFontSize(16);
		this.internalNode.setMouseTransparent(true);
		this.children.add(internalNode);
		
		this.setPrefHeight(24);
		
		background = Theme.current().getPane();
		
		this.setOnMouseReleased( event -> {
			if ( event.button == 0 ) {
				if ( buttonEvent != null ) 
					EventHelper.fireEvent(buttonEvent, new ButtonEvent());
				
				((ContextMenu)getParent().getParent()).close();
			}
		});
	}
	
	@Override
	protected void resize() {
		this.setAlignment(Pos.CENTER_LEFT);
		super.resize();
		this.updateChildren();
		this.setMinSize(internalNode.getWidth(), getPrefHeight());
	}
	
	protected boolean isSelected() {
		return this.cached_context.isHovered(this) || this.isDescendentHovered();
	}

	@Override
	public void render(Context context) {
		// Outline
		if ( isSelected() ) {
			if ( this.parent.getParent() instanceof ContextMenu ) {
				((ContextMenu) this.parent.getParent()).mouseEntered = true;
			}
		}
		
		Color bg = isSelected()?Theme.current().getSelection():this.background;
		if ( bg != null ) {
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRect(context.getNVG(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
			NanoVG.nvgFillColor(context.getNVG(), bg.getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
		
		// Render text on menu item
		if ( this.internalNode instanceof Labeled ) {
			((Labeled) this.internalNode).setTextFill(isSelected()?Theme.current().getControlHover():Theme.current().getText());
		}
		this.internalNode.render(context);
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	public void setOnAction(EventHandler<ButtonEvent> event) {
		this.buttonEvent = event;
	}
}
