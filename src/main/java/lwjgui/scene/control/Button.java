package lwjgui.scene.control;

import org.joml.Vector2d;
import lwjgui.LWJGUI;
import lwjgui.geometry.Insets;

public class Button extends ButtonBase {
	
	public Button(String name) {
		super(name);
		
		this.setMinSize(32, 16);
		this.setPadding(new Insets(3,5,3,5));
		this.setText(name);
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
	
	@Override
	protected void resize() {
		super.resize();
		//update();
	}

	@Override
	public void setText(String string) {
		super.setText(string);
		update();
	}
	
	private void update() {
		LWJGUI.runLater(() -> {
			//this.setPrefWidth(graphicLabel.getMaximumPotentialWidth()+this.getPadding().getWidth()+1);
			//this.setPrefWidth(this.getPrefW);
		});
	}
}
