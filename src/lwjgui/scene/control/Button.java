package lwjgui.scene.control;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.event.ButtonEvent;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.HBox;
import lwjgui.theme.Theme;

public class Button extends ButtonBase {
	
	public Button(String name) {
		super(name);
		
		this.setMinSize(32, 24);
		this.setPadding(new Insets(4,8,4,8));
		this.setText(name);
	}
	
	public void setCornerRadius( double radius ) {
		this.cornerRadius = radius;
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	public Vector2d getAvailableSize() {
		return new Vector2d(getMaxWidth(),getMaxHeight());
	}
	
	@Override
	protected void resize() {
		super.resize();
	}

	@Override
	public void setText(String string) {
		super.setText(string);
		update();
	}
	
	private void update() {
		LWJGUI.runLater(() -> {
			this.setPrefWidth(inside.getMaximumPotentialWidth()+this.getPadding().getWidth());
		});
	}
}
