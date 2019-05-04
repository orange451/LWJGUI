package lwjgui.scene.control;

import org.joml.Vector2d;

import lwjgui.geometry.Insets;
import lwjgui.scene.Node;

public class Button extends ButtonBase {
	
	public Button(String name) {
		this( name, null );
	}
	
	public Button( String name, Node graphic ) {
		super(name);
		
		this.setPadding(new Insets(4,6,4,6));
		this.setText(name);
		this.setGraphic(graphic);
	}
	
	@Override
	public void setCornerRadius( double radius ) {
		super.setCornerRadius(radius);
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	public Vector2d getAvailableSize() {
		return new Vector2d(getMaxWidth(),getMaxHeight());
	}
}
