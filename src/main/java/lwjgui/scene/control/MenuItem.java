package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.event.ButtonEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class MenuItem extends Node {
	protected EventHandler<ButtonEvent> buttonEvent;
	private Labeled internalLabel;
	private static final int prefHeight = 24;
	private static final int padding = 4;
	protected Color background;
	
	public MenuItem(String string) {
		this(string, null);
	}
	
	public MenuItem(String string, Node graphic) {
		this.internalLabel = new Label(string) {};
		this.internalLabel.setGraphic(graphic);
		this.internalLabel.setPadding(new Insets(0,padding,0,padding));
		this.internalLabel.setFontSize(16);
		
		background = Theme.currentTheme().getPane();
		
		this.setMouseReleasedEvent( event -> {
			if ( event.button == 0 ) {
				if ( buttonEvent != null ) 
					EventHelper.fireEvent(buttonEvent, new ButtonEvent());
				
				((ContextMenu)getParent().getParent()).close();
			}
		});
	}
	
	public void setGraphic(Node node) {
		this.internalLabel.setGraphic(node);
	}
	
	@Override
	protected void resize() {
		super.resize();
		
		this.setAlignment(Pos.CENTER_LEFT);
		this.resize(Integer.MAX_VALUE, prefHeight);
		this.internalLabel.position(this);
		//this.resize(internalLabel.graphicLabel.getMaximumPotentialWidth(), prefHeight);
		this.resize(internalLabel.getWidth(), prefHeight);
	}
	
	protected boolean isSelected() {
		return LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext().isHovered(this);
	}

	@Override
	public void render(Context context) {
		
		// Outline
		if ( isSelected() ) {
			if ( this.parent.getParent() instanceof ContextMenu ) {
				((ContextMenu) this.parent.getParent()).mouseEntered = true;
			}
		}
		
		Color bg = isSelected()?Theme.currentTheme().getSelection():this.background;
		if ( bg != null ) {
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRect(context.getNVG(), (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
			NanoVG.nvgFillColor(context.getNVG(), bg.getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
		
		// Render text on menu item
		this.internalLabel.setTextFill(isSelected()?Theme.currentTheme().getControlHover():Theme.currentTheme().getText());
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
	
	public void setOnAction(EventHandler<ButtonEvent> event) {
		this.buttonEvent = event;
	}
}
