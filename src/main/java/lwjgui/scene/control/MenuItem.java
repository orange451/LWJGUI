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
import lwjgui.scene.layout.Font;
import lwjgui.theme.Theme;

public class MenuItem extends Node {
	protected EventHandler<ButtonEvent> buttonEvent;
	private Label internalLabel = null;
	private static final int padding = 4;
	protected Color background;
	
	public MenuItem(String string) {
		this(string, null, null);
	}
	
	public MenuItem(String string, Node graphic) {
		this(string, null, graphic);
	}
	
	public MenuItem(String string, Font font) {
		this(string, font, null);
	}
	
	public MenuItem(String string, Font font, Node graphic) {
		if (string != null) {
			setContent(string, font, graphic);
		}
		
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
	
	public void setContent(String string, Font font, Node graphic) {
		if (internalLabel == null) {
			internalLabel = new Label(string);
		}
		
		internalLabel.setGraphic(graphic);
		internalLabel.setPadding(new Insets(0, padding, 0, padding));
		internalLabel.setFontSize(16);

		if (font != null) {
			internalLabel.setFont(font);
		}

		this.internalLabel.setMouseTransparent(true);
		this.children.add(internalLabel);
	}
	
	@Override
	protected void resize() {
		this.setAlignment(Pos.CENTER_LEFT);
		super.resize();
		this.updateChildren();
		
		if ( internalLabel != null ) {
			this.setMinSize(internalLabel.getWidth(), getPrefHeight());
		}
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
		if ( this.internalLabel != null ) {
			if ( this.internalLabel instanceof Labeled ) {
				((Labeled) this.internalLabel).setTextFill(isSelected()?Theme.current().getTextAlt():Theme.current().getText());
			}
			this.internalLabel.render(context);
		}
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	public void setOnAction(EventHandler<ButtonEvent> event) {
		this.buttonEvent = event;
	}
}
