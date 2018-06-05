package lwjgui.scene.control;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.event.ButtonEvent;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class MenuItem extends Node {
	private ButtonEvent buttonEvent;
	private Labeled internalLabel;
	private static final int prefHeight = 24;
	private static final int padding = 4;
	
	public MenuItem(String string) {
		this(string, null);
	}
	
	public MenuItem(String string, Node graphic) {
		this.internalLabel = new Labeled(string) {};
		this.internalLabel.setGraphic(graphic);
		this.internalLabel.setPadding(new Insets(0,padding,0,padding));
		
		this.mouseReleasedEvent = new MouseEvent() {
			@Override
			public void onEvent(int button) {
				if ( button == 0 ) {
					if ( buttonEvent != null ) 
						buttonEvent.onEvent();
					((ContextMenu)getParent().getParent()).close();
				}
			}
		};
	}
	
	@Override
	protected void resize() {
		super.resize();
		
		this.setAlignment(Pos.CENTER_LEFT);
		this.resize(internalLabel.graphicLabel.getMaximumPotentialWidth(), prefHeight);
		internalLabel.position(this);
	}

	@Override
	public void render(Context context) {
		
		// Outline
		if ( context.isHovered(this) ) {
			if ( this.parent.getParent() instanceof ContextMenu ) {
				((ContextMenu) this.parent.getParent()).mouseEntered = true;
			}
			
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRect(context.getNVG(), (int)getAbsoluteX(), (int)getAbsoluteY(), (int)getWidth(), (int)getHeight());
			NanoVG.nvgFillColor(context.getNVG(), Color.AQUA.getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
		
		// Render text on menu item
		this.internalLabel.graphicLabel.label.setTextFill(context.isHovered(this)?Theme.currentTheme().getButtonHover():Theme.currentTheme().getText());
		this.internalLabel.render(context);
	}
	
	private void resize( double x, double y ) {
		this.setMinSize(x, y);
		this.setMaxSize(x, y);
		this.setPrefSize(x, y);
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
	
	public void setOnAction(ButtonEvent event) {
		this.buttonEvent = event;
	}
}
